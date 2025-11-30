import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }

    public enum Rank {
        TWO("02", 2), THREE("03", 3), FOUR("04", 4), FIVE("05", 5),
        SIX("06", 6), SEVEN("07", 7), EIGHT("08", 8), NINE("09", 9),
        TEN("10", 10), JACK("jack", 11), QUEEN("queen", 12), KING("king", 13),
        ACE("ace", 14);

        private final String symbol;
        private final int value;
        Rank(String symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }

        public String getSymbol() { return symbol; }
        public int getValue() { return value; }
    }

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() { return suit; }
    public Rank getRank() { return rank; }

    public String getImagePath() {
        // Format: /images/cards/clubs_02.png, hearts_ace.png.
        String suitName = suit.toString().substring(0, 1).toLowerCase() + suit.toString().substring(1).toLowerCase();
        String rankSymbol = rank.getSymbol();
        return "/images/cards/" + suitName + "_" + rankSymbol + ".png";
    }

    @Override
    public String toString() {
        return rank.getSymbol() + " of " + suit.toString().toLowerCase();
    }
}