package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Client 테스트")
class ClientTest {

    private Client client;
    private ServerSocket mockServer;
    private Thread serverThread;

    @BeforeEach
    void setUp() {
        client = new Client();
    }

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (mockServer != null && !mockServer.isClosed()) {
            try {
                mockServer.close();
            } catch (IOException ignored) {}
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    @Test
    @DisplayName("isConnected - 초기값 false")
    void testIsConnectedInitial() {
        assertFalse(client.isConnected());
    }

    @Test
    @DisplayName("connect - 서버 연결 성공")
    void testConnectSuccess() throws Exception {
        int port = 9999;
        
        // 모의 서버 시작
        mockServer = new ServerSocket(port);
        
        // 서버 준비 완료 플래그
        final boolean[] serverReady = {false};
        
        serverThread = new Thread(() -> {
            try {
                serverReady[0] = true;
                Socket clientSocket = mockServer.accept();
                // 클라이언트와 동일한 순서: out 먼저, in 나중에
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                
                // CLIENT_CONNECTED 메시지 수신 대기
                Object msg = in.readObject();
                assertEquals("CLIENT_CONNECTED", msg);
                
                // 테스트 메시지 전송
                out.writeObject("TEST_MESSAGE");
                out.flush();
                
                Thread.sleep(100);
                clientSocket.close();
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        // 서버가 준비될 때까지 대기
        int waitCount = 0;
        while (!serverReady[0] && waitCount < 50) {
            Thread.sleep(10);
            waitCount++;
        }
        
        // 클라이언트 연결
        client.connect("localhost", port);
        
        // 연결 대기 (polling) - 최대 5초
        int maxAttempts = 50;
        for (int i = 0; i < maxAttempts; i++) {
            if (client.isConnected()) {
                break;
            }
            Thread.sleep(100);
        }
        
        assertTrue(client.isConnected());
    }

    @Test
    @DisplayName("connect - 연결 실패 시 connected=false")
    void testConnectFailure() throws Exception {
        // 존재하지 않는 포트로 연결 시도
        client.connect("localhost", 19999);
        
        Thread.sleep(200);
        
        assertFalse(client.isConnected());
    }

    @Test
    @DisplayName("send - 연결되지 않은 상태에서 전송")
    void testSendNotConnected() {
        assertDoesNotThrow(() -> client.send("TEST"));
    }

    @Test
    @DisplayName("send - 메시지 전송")
    void testSend() throws Exception {
        int port = 10000;
        
        // 모의 서버 시작
        mockServer = new ServerSocket(port);
        serverThread = new Thread(() -> {
            try {
                Socket clientSocket = mockServer.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                
                // 메시지 수신
                Object msg1 = in.readObject();
                assertEquals("CLIENT_CONNECTED", msg1);
                
                Object msg2 = in.readObject();
                assertEquals("CUSTOM_MESSAGE", msg2);
                
                clientSocket.close();
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        // 클라이언트 연결 및 메시지 전송
        client.connect("localhost", port);
        Thread.sleep(500);
        
        client.send("CUSTOM_MESSAGE");
        Thread.sleep(100);
    }

    @Test
    @DisplayName("close - 리소스 정리")
    void testClose() throws Exception {
        int port = 10001;
        
        // 모의 서버 시작
        mockServer = new ServerSocket(port);
        serverThread = new Thread(() -> {
            try {
                Socket clientSocket = mockServer.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                in.readObject();
                Thread.sleep(1000);
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        client.connect("localhost", port);
        Thread.sleep(500);
        
        assertTrue(client.isConnected());
        
        client.close();
        Thread.sleep(100);
        
        // close 후 소켓이 닫혔는지 확인
        Field socketField = Client.class.getDeclaredField("socket");
        socketField.setAccessible(true);
        Socket socket = (Socket) socketField.get(client);
        
        if (socket != null) {
            assertTrue(socket.isClosed());
        }
    }

    @Test
    @DisplayName("close - 연결되지 않은 상태에서 close")
    void testCloseNotConnected() {
        assertDoesNotThrow(() -> client.close());
    }
}
