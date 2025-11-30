import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ClientNetworkManager {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenerThread;
    private Consumer<PokerInfo> callback;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean listening = new AtomicBoolean(false);


    // Opens a socket connection to the server and initializes streams

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            running.set(true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            close();
            return false;
        }
    }

    // Starts a listener thread that processes incoming PokerInfo objects
    public void startListener(Consumer<PokerInfo> cb) {
        stopListener();
        
        this.callback = cb;
        listening.set(true);
        listenerThread = new Thread(() -> {
            try {
                while (listening.get() && running.get()) {
                    Object o = null;
                    try {
                        synchronized (in) {
                            o = in.readObject();
                        }
                    } catch (SocketException se) {
                        // Socket closed
                        break;
                    }
                    
                    if (o instanceof PokerInfo) {
                        PokerInfo pi = (PokerInfo) o;
                        if (callback != null && listening.get()) {
                            callback.accept(pi);
                        }
                    }
                }
            } catch (EOFException eof) {
                if (listening.get()) {
                    System.err.println("Connection closed by server");
                }
            } catch (Exception e) {
                if (listening.get() && running.get()) {
                    System.err.println("Listener error: " + e.getMessage());
                }
            }
        }, "ClientListener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    // Stops the listener thread
    public void stopListener() {
        listening.set(false);
        if (listenerThread != null && listenerThread.isAlive()) {
            try {
                listenerThread.join(500);
            } catch (InterruptedException e) {}
        }
    }

    // Sends a PokerInfo object to the server
    public void send(PokerInfo info) {
        try {
            if (socket == null || socket.isClosed() || !running.get()) {
                System.err.println("ERROR: Socket is closed, cannot send");
                return;
            }
            synchronized (out) {
                out.writeObject(info);
                out.flush();
            }
        } catch (Exception e) {
            System.err.println("Send error: " + e.getMessage());
            running.set(false);
        }
    }

    public void disconnect() {
        running.set(false);
        stopListener();
        close();
    }

    private void close() {
        try { if (in != null) in.close(); } catch (Exception e) {}
        try { if (out != null) out.close(); } catch (Exception e) {}
        try { if (socket != null) socket.close(); } catch (Exception e) {}
    }
}