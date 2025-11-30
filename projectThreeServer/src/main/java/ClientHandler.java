import java.io.*;
import java.net.Socket;

// Handles a single client connection. Communicates via PokerInfo.

public class ClientHandler extends Thread {
    private final Socket socket;
    private final ServerNetworkManager manager;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running = true;
    private GameSession session;

    public ClientHandler(Socket socket, ServerNetworkManager mgr) {
        this.socket = socket;
        this.manager = mgr;
        setName("ClientHandler-" + socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            manager.log("Streams established for " + socket.getRemoteSocketAddress());

            while (running) {
                Object o;
                try {
                    o = in.readObject();
                } catch (EOFException eof) {
                    break;
                }
                if (!(o instanceof PokerInfo)) {
                    manager.log("Received unexpected object from client");
                    continue;
                }
                PokerInfo req = (PokerInfo) o;
                handleRequest(req);
            }
        } catch (Exception e) {
            manager.log("Handler error: " + e.getMessage());
        } finally {
            close();
            manager.removeClient(socket, this);
        }
    }

    // Handles incoming PokerInfo requests from the client
    private void handleRequest(PokerInfo req) {
        try {

            if (req.getAnteBet() > 0 && !req.isFold() && req.getPlayBet() == 0) {
                session = new GameSession();
                session.newHand(req.getAnteBet(), req.getPairPlusBet());

                manager.log("BETS - " + socket.getRemoteSocketAddress()
                            + " | Ante: $" + session.getAnteBet()
                            + " | PairPlus: $" + session.getPairPlusBet()
                        );

                PokerInfo resp = new PokerInfo();
                resp.setSessionId(session.getSessionId());
                resp.setPlayerCards(session.getPlayerHand());
                resp.setDealerCards(session.getDealerHand());
                resp.setAnteBet(session.getAnteBet());
                resp.setPairPlusBet(session.getPairPlusBet());
                resp.setRevealDealer(false);
                resp.setMessage("Hand dealt. Choose PLAY or FOLD.");
                send(resp);
                manager.log("Dealt hand to " + socket.getRemoteSocketAddress());
                return;
            }

            // Fold
            if (req.isFold()) {
                if (session == null) {
                    sendError("No active session to fold.");
                    return;
                }
                PokerInfo resp = session.handleFold();
                send(resp);
                manager.log("Player folded: " + socket.getRemoteSocketAddress());
                return;
            }

            // Play
            if (req.getPlayBet() > 0) {
                if (session == null) {
                    sendError("No active session to play.");
                    return;
                }
                PokerInfo resp = session.evaluatePlay(req.getPlayBet());
                send(resp);
                manager.log("Evaluated play for " + socket.getRemoteSocketAddress() + " result " + resp.getResultAmount());
                return;
            }

            // Unknown request
            sendError("Unknown request");
        } catch (Exception e) {
            manager.log("Error handling request: " + e.getMessage());
            sendError("Server error: " + e.getMessage());
        }
    }

    public void send(PokerInfo info) {
        try {
            synchronized (out) {
                out.writeObject(info);
                out.flush();
            }
        } catch (IOException e) {
            manager.log("Send failed: " + e.getMessage());
        }
    }

    private void sendError(String msg) {
        PokerInfo err = new PokerInfo();
        err.setMessage(msg);
        send(err);
    }

    public void close() {
        running = false;
        try { if (in != null) in.close(); } catch (Exception e) {}
        try { if (out != null) out.close(); } catch (Exception e) {}
        try { if (socket != null) socket.close(); } catch (Exception e) {}
    }
}