package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean connected = false;

    public boolean isConnected() {
        return connected;
    }

    public void start(int port) {
        new Thread(() -> {
            try {
                System.out.println("[SERVER] 대기중... 포트: " + port);
                serverSocket = new ServerSocket(port);

                clientSocket = serverSocket.accept();
                System.out.println("[SERVER] 클라이언트 연결됨!");

                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                connected = true;

                send("SERVER_CONNECTED");

                while (true) {
                    Object msg = in.readObject();
                    System.out.println("[SERVER RECEIVED] " + msg);
                }

            } catch (Exception e) {
                System.out.println("[SERVER] ERROR");
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
            System.out.println("[SERVER] 메시지 전송 실패");
        }
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
    }
}
