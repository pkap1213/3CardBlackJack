
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


 // Accepts client connections and manages ClientHandler threads

public class ServerNetworkManager {
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private final Map<Socket, ClientHandler> clients = Collections.synchronizedMap(new HashMap<>());
    private final ServerController controller;
    private boolean running = false;

    public ServerNetworkManager(ServerController controller) {
        this.controller = controller;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        acceptThread = new Thread(this::acceptLoop, "Server-AcceptThread");
        acceptThread.setDaemon(true);
        acceptThread.start();
        controller.addEvent("Listening for clients...");
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) { /* ignore */ }
        // close all clients
        synchronized (clients) {
            for (ClientHandler h : clients.values()) {
                h.close();
            }
            clients.clear();
        }
        controller.addEvent("Stopped listening and closed clients");
        updateCount();
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket sock = serverSocket.accept();
                controller.addEvent("New connection from " + sock.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(sock, this);
                clients.put(sock, handler);
                handler.start();
                updateCount();
            } catch (IOException e) {
                if (running) controller.addEvent("Accept error: " + e.getMessage());
            }
        }
    }

    void removeClient(Socket s, ClientHandler h) {
        clients.remove(s);
        controller.addEvent("Client disconnected: " + s.getRemoteSocketAddress());
        updateCount();
    }


    void log(String msg) { controller.addEvent(msg); }

    private void updateCount() {
        if (controller != null) {
            controller.updateClientCount(clients.size());
        }
    }
}