import java.util.List;
import java.util.UUID;

 // Represents a single player's game session
 // All game logic results are computed here and returned in PokerInfo.
 
public class GameSession {
    private final UUID sessionId = UUID.randomUUID();
    private Deck deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private int anteBet;
    private int pairPlusBet;
    private int playBet = 0;
    private GameHistory history = new GameHistory();



    public UUID getSessionId() { return sessionId; }
    public List<Card> getPlayerHand() { return playerHand; }
    public List<Card> getDealerHand() { return dealerHand; }
    public int getAnteBet() { return anteBet; }
    public int getPairPlusBet() { return pairPlusBet; }

    public void newHand(int ante, int pairPlus) {
        this.anteBet = ante;
        this.pairPlusBet = pairPlus;
        deck = new Deck();
        deck.shuffle();
        playerHand = deck.deal(3);
        dealerHand = deck.deal(3);
    }

    public PokerInfo handleFold() {
        int net = -(anteBet + pairPlusBet);
        PokerInfo res = new PokerInfo();
        res.setSessionId(sessionId);
        res.setResultAmount(net);
        res.setMessage("Player folded. Lost ante and pair plus.");
        res.setRevealDealer(true);
        res.setPlayerCards(playerHand);
        res.setDealerCards(dealerHand);
        history.addRecord(new GameRecord(java.time.LocalDateTime.now(), sessionId, anteBet, pairPlusBet, 0, net, res.getMessage()));
        return res;
    }

    public PokerInfo evaluatePlay(int play) {
        this.playBet = play;
        PokerInfo res = new PokerInfo();
        res.setSessionId(sessionId);
        res.setPlayerCards(playerHand);
        res.setDealerCards(dealerHand);
        res.setRevealDealer(true);

        // PairPlus  evaluated if pairPlusBet > 0 and player did not fold
        int ppWin = ThreeCardLogic.evalPPWinnings(playerHand, pairPlusBet);

        // Determine dealer qualification (queen high or better)
        int dealerHigh = dealerHand.stream().mapToInt(c -> c.getRank().getValue()).max().orElse(0);
        boolean dealerQualifies = dealerHigh >= 12;

        if (!dealerQualifies) {
            // play returned, ante pushed
            int net = ppWin;
            String ppSummary;
            if (ppWin == 0) {
                ppSummary = "lost";
            } else {
                ppSummary = "won $" + ppWin;
            }
            res.setResultAmount(net);
            res.setMessage("Dealer does not have Queen high. Ante pushed, play returned. PairPlus: " + ppSummary);
            history.addRecord(new GameRecord(java.time.LocalDateTime.now(), sessionId, anteBet, pairPlusBet, playBet, net, res.getMessage()));
            return res;
        }

        // Dealer qualifies, compare hands
        int compare = ThreeCardLogic.compareHands(dealerHand, playerHand);
        if (compare > 0) {
            // player wins
            int net = anteBet + playBet + ppWin;
            String ppSummary;
            if (ppWin == 0) {
                ppSummary = "lost";
            } else {
                ppSummary = "won $" + ppWin;
            }
            res.setResultAmount(net);
            res.setMessage("Player wins. PairPlus: " + ppSummary);
            history.addRecord(new GameRecord(java.time.LocalDateTime.now(), sessionId, anteBet, pairPlusBet, playBet, net, res.getMessage()));
            return res;
        } else if (compare < 0) {
            // dealer wins
            int net = -(anteBet + playBet) + ppWin;
            String ppSummary;
            if (ppWin == 0) {
                ppSummary = "lost";
            } else {
                ppSummary = "won $" + ppWin;
            }
            res.setResultAmount(net);
            res.setMessage("Player loses to dealer. PairPlus: " + ppSummary);
            history.addRecord(new GameRecord(java.time.LocalDateTime.now(), sessionId, anteBet, pairPlusBet, playBet, net, res.getMessage()));
            return res;
        } else {
            // tie / push
            int net = ppWin;
            String ppSummary;
            if (ppWin == 0) {
                ppSummary = "lost";
            } else {
                ppSummary = "won $" + ppWin;
            }
            res.setResultAmount(net);
            res.setMessage("Push (tie). PairPlus: " + ppSummary);
            history.addRecord(new GameRecord(java.time.LocalDateTime.now(), sessionId, anteBet, pairPlusBet, playBet, net, res.getMessage()));
            return res;
        }
    }
}