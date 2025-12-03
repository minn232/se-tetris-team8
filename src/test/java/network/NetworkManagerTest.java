package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NetworkManager 테스트")
class NetworkManagerTest {

    private NetworkManager manager;

    @BeforeEach
    void setUp() {
        manager = NetworkManager.getInstance();
    }

    @AfterEach
    void tearDown() {
        if (manager != null) {
            manager.close();
        }
    }

    @Test
    @DisplayName("getInstance - 싱글톤 인스턴스 반환")
    void testGetInstance() {
        NetworkManager instance1 = NetworkManager.getInstance();
        NetworkManager instance2 = NetworkManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("isConnected - 초기값 false")
    void testIsConnectedInitial() {
        assertFalse(manager.isConnected());
    }

    @Test
    @DisplayName("startServer - 서버 시작 및 클라이언트 연결")
    void testStartServer() throws Exception {
        int port = 12345;
        
        manager.startServer(port);
        Thread.sleep(200);
        
        // 클라이언트 연결
        Socket client = new Socket("localhost", port);
        Thread.sleep(300);
        
        assertTrue(manager.isConnected());
        
        client.close();
    }

    @Test
    @DisplayName("startClient - 클라이언트 연결")
    void testStartClient() throws Exception {
        int port = 12346;
        
        // 서버 시작
        ServerSocket server = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = server.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                Thread.sleep(2000);
                clientSocket.close();
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        manager.startClient("localhost", port);
        
        // 연결 대기 (polling)
        int maxAttempts = 20;
        for (int i = 0; i < maxAttempts; i++) {
            if (manager.isConnected()) {
                break;
            }
            Thread.sleep(100);
        }
        
        assertTrue(manager.isConnected());
        
        server.close();
        serverThread.interrupt();
    }

    @Test
    @DisplayName("send - 메시지 전송")
    void testSend() throws Exception {
        int port = 12347;
        
        AtomicReference<String> receivedMessage = new AtomicReference<>();
        
        // 서버 시작
        ServerSocket server = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String msg;
                while ((msg = in.readLine()) != null) {
                    // PING 무시하고 일반 메시지만 저장
                    if (!msg.startsWith("PING:")) {
                        receivedMessage.set(msg);
                        break;
                    }
                }
                
                Thread.sleep(500);
                clientSocket.close();
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        manager.startClient("localhost", port);
        
        // 연결 대기 (polling)
        int maxAttempts = 20;
        for (int i = 0; i < maxAttempts; i++) {
            if (manager.isConnected()) {
                break;
            }
            Thread.sleep(100);
        }
        
        assertTrue(manager.isConnected(), "클라이언트가 연결되지 않았습니다");
        
        manager.send("TEST_MESSAGE");
        Thread.sleep(300);
        
        assertEquals("TEST_MESSAGE", receivedMessage.get());
        
        server.close();
        serverThread.interrupt();
    }

    @Test
    @DisplayName("setMessageListener - 메시지 리스너 설정")
    void testSetMessageListener() throws Exception {
        int port = 12348;
        
        AtomicReference<String> receivedMessage = new AtomicReference<>();
        manager.setMessageListener(receivedMessage::set);
        
        // 서버 시작
        ServerSocket server = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = server.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                Thread.sleep(300);
                out.println("HELLO");
                
                Thread.sleep(500);
                clientSocket.close();
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        manager.startClient("localhost", port);
        Thread.sleep(1000);
        
        assertEquals("HELLO", receivedMessage.get());
        
        server.close();
        serverThread.interrupt();
    }

    @Test
    @DisplayName("PING/PONG - RTT 측정")
    void testPingPong() throws Exception {
        int port = 12349;
        
        // 서버 시작
        ServerSocket server = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = server.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith("PING:")) {
                        String ts = msg.substring(5);
                        out.println("PONG:" + ts);
                    }
                }
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        manager.startClient("localhost", port);
        Thread.sleep(2000);
        
        long rtt = manager.getRTT();
        assertTrue(rtt >= 0);
        
        long lastPingTime = manager.getLastPingTime();
        assertTrue(lastPingTime > 0);
        
        server.close();
        serverThread.interrupt();
    }

    @Test
    @DisplayName("close - 리소스 정리")
    void testClose() throws Exception {
        int port = 12350;
        
        ServerSocket server = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = server.accept();
                Thread.sleep(2000);
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        manager.startClient("localhost", port);
        Thread.sleep(300);
        
        assertTrue(manager.isConnected());
        
        manager.close();
        Thread.sleep(100);
        
        assertFalse(manager.isConnected());
        
        server.close();
        serverThread.interrupt();
    }

    @Test
    @DisplayName("send - 연결 없이 전송")
    void testSendNotConnected() {
        assertDoesNotThrow(() -> manager.send("TEST"));
    }

    @Test
    @DisplayName("startClient - 연결 실패")
    void testStartClientFailure() throws Exception {
        manager.startClient("localhost", 29999);
        Thread.sleep(300);
        
        assertFalse(manager.isConnected());
    }
    
    @Test
    @DisplayName("로컬 네트워크 - RTT 200ms 이하 (성능 요구사항)")
    void testLocalNetworkLatency() throws Exception {
        int port = 12351;
        
        // 서버 시작
        ServerSocket server = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = server.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith("PING:")) {
                        String ts = msg.substring(5);
                        // 즉시 응답 (최소 지연)
                        out.println("PONG:" + ts);
                    }
                }
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        manager.startClient("localhost", port);
        Thread.sleep(2000); // PING/PONG 주기 대기
        
        long rtt = manager.getRTT();
        
        // 로컬 네트워크에서 RTT는 200ms 이하여야 함
        assertTrue(rtt <= 200, 
            String.format("로컬 네트워크 RTT가 200ms를 초과했습니다: %dms", rtt));
        
        server.close();
        serverThread.interrupt();
    }
    
    @Test
    @DisplayName("네트워크 지연 감지 - RTT 200ms 초과 시 랙 상태")
    void testLagDetection() throws Exception {
        int port = 12352;
        
        // 서버 시작 (의도적으로 지연 추가)
        ServerSocket server = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = server.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith("PING:")) {
                        String ts = msg.substring(5);
                        // 의도적으로 250ms 지연
                        Thread.sleep(250);
                        out.println("PONG:" + ts);
                    }
                }
            } catch (Exception ignored) {}
        });
        serverThread.start();
        
        manager.startClient("localhost", port);
        Thread.sleep(2000); // PING/PONG 주기 대기
        
        long rtt = manager.getRTT();
        
        // 지연이 추가된 경우 RTT가 200ms를 초과해야 함
        assertTrue(rtt > 200, 
            String.format("지연이 감지되어야 합니다. 현재 RTT: %dms", rtt));
        
        server.close();
        serverThread.interrupt();
    }
}
