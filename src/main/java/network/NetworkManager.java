package network;

import java.io.BufferedReader;
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

    private Socket socket;
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
                ServerSocket server = new ServerSocket(port);
                socket = server.accept();

                out = new PrintWriter(socket.getOutputStream(), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connected = true;

                startPing();   // ★ 연결 직후 PING 시작
                listenLoop();  // ★ 메시지 수신 시작

                server.close();
            } catch (Exception e) {
                e.printStackTrace();
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

                startPing();   // ★ 연결 직후 PING 시작
                listenLoop();  // ★ 메시지 수신 시작

            } catch (Exception e) {
                e.printStackTrace();
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
                while ((msg = in.readLine()) != null) {

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
            } catch (Exception ignored) {}
        }).start();
    }


    // ======================================================
    // SEND
    // ======================================================
    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }


    // ======================================================
    // CLOSE
    // ======================================================
    public void close() {
        try {
            connected = false;

            if (out != null) { out.close(); out = null; }
            if (in != null) { in.close(); in = null; }
            if (socket != null && !socket.isClosed()) { socket.close(); socket = null; }

            messageListener = null;

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
                while (socket != null && socket.isConnected()) {

                    long now = System.currentTimeMillis();
                    lastPingSentTime = now;

                    send("PING:" + now);

                    Thread.sleep(1000); // 1초마다 PING
                }
            } catch (Exception ignored) {}
        }).start();
    }

    // 현재 RTT 가져오기
    public long getRTT() {
        return lastRTT;
    }

    // 마지막 ping 수신 시간 → 끊김 감지용
    public long getLastPingTime() {
        return lastPingReceivedTime;
    }
}
