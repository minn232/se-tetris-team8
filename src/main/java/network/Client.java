package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean connected = false;

    public boolean isConnected() {
        return connected;
    }

    public void connect(String ip, int port) {
        new Thread(() -> {
            try {
                System.out.println("[CLIENT] 서버 접속 시도: " + ip + ":" + port);
                socket = new Socket(ip, port);
                System.out.println("[CLIENT] 서버 연결 성공!");

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                connected = true;

                send("CLIENT_CONNECTED");

                while (true) {
                    Object msg = in.readObject();
                    System.out.println("[CLIENT RECEIVED] " + msg);
                }

            } catch (Exception e) {
                System.out.println("[CLIENT] ERROR");
                e.printStackTrace();
                connected = false;
                close();
            }
        }).start();
    }

    public void send(String msg) {
        if (!connected || out == null) return;
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("[CLIENT] 메시지 전송 실패");
        }
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
