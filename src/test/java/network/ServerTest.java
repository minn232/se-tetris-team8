package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Server 테스트")
class ServerTest {

    private Server server;

    @BeforeEach
    void setUp() {
        server = new Server();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.close();
        }
    }

    @Test
    @DisplayName("isConnected - 초기값 false")
    void testIsConnectedInitial() {
        assertFalse(server.isConnected());
    }

    @Test
    @DisplayName("start - 서버 시작 및 클라이언트 연결")
    void testStart() throws Exception {
        int port = 15000;
        
        server.start(port);
        Thread.sleep(200);
        
        // 클라이언트 연결
        Socket client = new Socket("localhost", port);
        ObjectOutputStream clientOut = new ObjectOutputStream(client.getOutputStream());
        clientOut.flush();
        ObjectInputStream clientIn = new ObjectInputStream(client.getInputStream());
        
        Thread.sleep(300);
        
        assertTrue(server.isConnected());
        
        // SERVER_CONNECTED 메시지 수신
        Object msg = clientIn.readObject();
        assertEquals("SERVER_CONNECTED", msg);
        
        client.close();
    }

    @Test
    @DisplayName("send - 메시지 전송")
    void testSend() throws Exception {
        int port = 15001;
        
        server.start(port);
        Thread.sleep(200);
        
        // 클라이언트 연결
        Socket client = new Socket("localhost", port);
        ObjectOutputStream clientOut = new ObjectOutputStream(client.getOutputStream());
        clientOut.flush();
        ObjectInputStream clientIn = new ObjectInputStream(client.getInputStream());
        
        Thread.sleep(300);
        
        // SERVER_CONNECTED 수신
        clientIn.readObject();
        
        // 메시지 전송
        server.send("CUSTOM_MESSAGE");
        
        // 메시지 수신
        Object msg = clientIn.readObject();
        assertEquals("CUSTOM_MESSAGE", msg);
        
        client.close();
    }

    @Test
    @DisplayName("send - 연결되지 않은 상태에서 전송")
    void testSendNotConnected() {
        assertDoesNotThrow(() -> server.send("TEST"));
    }

    @Test
    @DisplayName("close - 리소스 정리")
    void testClose() throws Exception {
        int port = 15002;
        
        server.start(port);
        Thread.sleep(200);
        
        Socket client = new Socket("localhost", port);
        ObjectOutputStream clientOut = new ObjectOutputStream(client.getOutputStream());
        clientOut.flush();
        
        Thread.sleep(300);
        
        assertTrue(server.isConnected());
        
        server.close();
        Thread.sleep(100);
        
        assertFalse(server.isConnected());
        
        // ServerSocket이 닫혔는지 확인
        Field serverSocketField = Server.class.getDeclaredField("serverSocket");
        serverSocketField.setAccessible(true);
        java.net.ServerSocket serverSocket = (java.net.ServerSocket) serverSocketField.get(server);
        
        if (serverSocket != null) {
            assertTrue(serverSocket.isClosed());
        }
        
        client.close();
    }

    @Test
    @DisplayName("close - 연결되지 않은 상태에서 close")
    void testCloseNotConnected() {
        assertDoesNotThrow(() -> server.close());
    }
}
