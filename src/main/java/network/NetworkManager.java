package network;

public class NetworkManager {

    private static NetworkManager instance;

    private Server server;
    private Client client;

    public enum Mode {
        NONE,
        SERVER,
        CLIENT
    }

    private Mode mode = Mode.NONE;

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public Mode getMode() {
        return mode;
    }

    public void startServer(int port) {
        server = new Server();
        mode = Mode.SERVER;
        server.start(port);
    }

    public void startClient(String ip, int port) {
        client = new Client();
        mode = Mode.CLIENT;
        client.connect(ip, port);
    }

    public boolean isConnected() {
        return switch (mode) {
            case SERVER -> server != null && server.isConnected();
            case CLIENT -> client != null && client.isConnected();
            default -> false;
        };
    }

    public void send(String msg) {
        if (mode == Mode.SERVER && server != null) {
            server.send(msg);
        } else if (mode == Mode.CLIENT && client != null) {
            client.send(msg);
        }
    }
}