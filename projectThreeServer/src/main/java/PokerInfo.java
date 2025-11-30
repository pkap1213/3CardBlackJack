import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID sessionId;
    private List<Card> playerCards;
    private List<Card> dealerCards;
    private int anteBet;
    private int pairPlusBet;
    private int playBet;
    private boolean fold;
    private int resultAmount;
    private String message;
    private boolean revealDealer;

    public PokerInfo() { 
        this.sessionId = UUID.randomUUID(); 
    }

    // Getters and Setters
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID id) { this.sessionId = id; }
    
    public List<Card> getPlayerCards() { return playerCards; }
    public void setPlayerCards(List<Card> c) { this.playerCards = c; }
    
    public List<Card> getDealerCards() { return dealerCards; }
    public void setDealerCards(List<Card> c) { this.dealerCards = c; }
    
    public int getAnteBet() { return anteBet; }
    public void setAnteBet(int b) { this.anteBet = b; }
    
    public int getPairPlusBet() { return pairPlusBet; }
    public void setPairPlusBet(int b) { this.pairPlusBet = b; }
    
    public int getPlayBet() { return playBet; }
    public void setPlayBet(int b) { this.playBet = b; }
    
    public boolean isFold() { return fold; }
    public void setFold(boolean f) { this.fold = f; }
    
    public int getResultAmount() { return resultAmount; }
    public void setResultAmount(int a) { this.resultAmount = a; }
    
    public String getMessage() { return message; }
    public void setMessage(String m) { this.message = m; }
    
    public boolean isRevealDealer() { return revealDealer; }
    public void setRevealDealer(boolean r) { this.revealDealer = r; }
}