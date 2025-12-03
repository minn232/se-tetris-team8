package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkManager {

    private static NetworkManager instance = new NetworkManager();
    public static NetworkManager getInstance() {
        return instance;
    }

    private volatile boolean running = true;

    private Socket socket;
    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;

    private boolean connected = false;

    private Consumer<String> messageListener;

    public void setMessageListener(Consumer<String> listener) {
        this.messageListener = listener;
    }

    public boolean isConnected() {
        return connected;
    }

    // ======================================================
    // SERVER MODE
    // ======================================================
    public void startServer(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();

                out = new PrintWriter(socket.getOutputStream(), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connected = true;
                running = true;

                startPing();
                listenLoop();

            } catch (IOException e) {
                System.err.println("서버 시작 실패: " + e.getMessage());
                connected = false;
            }
        }).start();
    }

    // ======================================================
    // CLIENT MODE
    // ======================================================
    public void startClient(String ip, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);

                out = new PrintWriter(socket.getOutputStream(), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connected = true;
                running = true;

                startPing();
                listenLoop();

            } catch (IOException e) {
                System.err.println("서버 접속 실패 (" + ip + ":" + port + "): " + e.getMessage());
                connected = false;
            }
        }).start();
    }

    // ======================================================
    // 메시지 수신 루프
    // ======================================================
    private void listenLoop() {
        new Thread(() -> {
            try {
                String msg;

                while (running && (msg = in.readLine()) != null) {

                    // ===== PING =====
                    if (msg.startsWith("PING:")) {
                        String ts = msg.substring(5);
                        send("PONG:" + ts);
                        continue;
                    }

                    // ===== PONG =====
                    if (msg.startsWith("PONG:")) {
                        long sent = Long.parseLong(msg.substring(5));
                        long now = System.currentTimeMillis();
                        lastRTT = now - sent;
                        lastPingReceivedTime = now;
                        continue;
                    }

                    // ===== 일반 메시지 =====
                    if (messageListener != null) {
                        messageListener.accept(msg);
                    }
                }

            } catch (IOException | NumberFormatException ignored) {}

        }).start();
    }

    // ======================================================
    // SEND
    // ======================================================
    public void send(String msg) {
        if (out != null) out.println(msg);
    }

    // ======================================================
    // CLOSE (완전 종료)
    // ======================================================
    public void close() {
        try {
            running = false;
            connected = false;
            messageListener = null;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            if (in != null) in.close();
            if (out != null) out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    //  PING / PONG 시스템
    // ======================================================
    private long lastPingSentTime = 0;
    private long lastPingReceivedTime = 0;
    private long lastRTT = 0;

    // ===== 자동 PING 스레드 =====
    public void startPing() {
        new Thread(() -> {
            try {
                while (running && socket != null && socket.isConnected()) {

                    long now = System.currentTimeMillis();
                    lastPingSentTime = now;

                    send("PING:" + now);

                    Thread.sleep(1000); // 1초마다 PING
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public long getRTT() {
        return lastRTT;
    }

    public long getLastPingTime() {
        return lastPingReceivedTime;
    }
}
