import java.util.*;

public class Deck {
    private List<Card> cards;
    
    public Deck() {
        reset();
    }
    
    public void reset() {
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public List<Card> deal(int numCards) {
        List<Card> dealt = new ArrayList<>();
        for (int i = 0; i < numCards && !cards.isEmpty(); i++) {
            dealt.add(cards.remove(0));
        }
        return dealt;
    }
    
    public int cardsRemaining() {
        return cards.size();
    }
}