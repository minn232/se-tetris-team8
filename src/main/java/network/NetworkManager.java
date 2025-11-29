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

    private Consumer<String> messageListener;  // 🔥 추가된 부분

    public void setMessageListener(Consumer<String> listener) {
        this.messageListener = listener;
    }

    public boolean isConnected() {
        return connected;
    }

    // ======================================
    // SERVER MODE
    // ======================================
    public void startServer(int port) {
        new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(port);
                socket = server.accept();

                out = new PrintWriter(socket.getOutputStream(), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connected = true;

                listenLoop();   // 🔥 메시지 수신 시작

                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ======================================
    // CLIENT MODE
    // ======================================
    public void startClient(String ip, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);

                out = new PrintWriter(socket.getOutputStream(), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connected = true;

                listenLoop();  // 🔥 메시지 수신 시작

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ======================================
    // 메시지 수신 루프
    // ======================================
    private void listenLoop() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {

                    // 🔥 UI로 메시지 전달
                    if (messageListener != null) {
                        messageListener.accept(msg);
                    }
                }
            } catch (Exception ignored) {}
        }).start();
    }

    // ======================================
    // SEND
    // ======================================
    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    // ======================================
    // CLOSE
    // ======================================
    public void close() {
        try {
            connected = false;
            
            if (out != null) {
                out.close();
                out = null;
            }
            
            if (in != null) {
                in.close();
                in = null;
            }
            
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            
            messageListener = null;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
