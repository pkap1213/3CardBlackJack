import java.util.ArrayList;
import java.util.List;

public class ThreeCardLogic {
    
  // Evaluates a hand and returns a rank

    public static int evalHand(List<Card> hand) {
        if (hand == null || hand.size() != 3) return -1;
        
        if (isStraightFlush(hand)) return 500;
        if (isThreeOfAKind(hand)) return 400;
        if (isStraight(hand)) return 300;
        if (isFlush(hand)) return 200;
        if (isPair(hand)) return 100;
        
        // High card: use highest card value
        return getHighCard(hand).getRank().getValue();
    }
    
    // Compares dealer hand vs player hand.
    // Returns: 1 if player wins, 0 if tie, -1 if dealer wins

    public static int compareHands(List<Card> dealerHand, List<Card> playerHand) {
        int dealerRank = evalHand(dealerHand);
        int playerRank = evalHand(playerHand);
        
        // Dealer must have Queen high (12) or better to qualify
        if (dealerRank < 12 && !isQualified(dealerHand)) {
            return 0; // Dealer doesn't qualify, ante is pushed
        }
        
        if (playerRank > dealerRank) return 1;  // Player wins
        if (playerRank < dealerRank) return -1; // Dealer wins
        return 0; // Tie
    }
    
    
    // Calculates Pair Plus winnings

    public static int evalPPWinnings(List<Card> hand, int bet) {
        if (hand == null || hand.size() != 3) return 0;
        if (bet <= 0) return 0;
        
        if (isStraightFlush(hand)) return bet * 40;
        if (isThreeOfAKind(hand)) return bet * 30;
        if (isStraight(hand)) return bet * 6;
        if (isFlush(hand)) return bet * 3;
        if (isPair(hand)) return bet * 1;
        
        return 0; // No qualifying hand
    }
    

    
    private static boolean isStraightFlush(List<Card> hand) {
        return isStraight(hand) && isFlush(hand);
    }
    
    private static boolean isThreeOfAKind(List<Card> hand) {
        return hand.get(0).getRank() == hand.get(1).getRank() &&
               hand.get(1).getRank() == hand.get(2).getRank();
    }
    
    private static boolean isStraight(List<Card> hand) {
        List<Card> sorted = new ArrayList<>(hand);
        sorted.sort((a, b) -> Integer.compare(a.getRank().getValue(), b.getRank().getValue()));
        
        Card c1 = sorted.get(0);
        Card c2 = sorted.get(1);
        Card c3 = sorted.get(2);
        
        // Checks for consecutive values
        if (c2.getRank().getValue() == c1.getRank().getValue() + 1 &&
            c3.getRank().getValue() == c2.getRank().getValue() + 1) {
            return true;
        }
        
        if (c1.getRank() == Card.Rank.TWO && 
            c2.getRank() == Card.Rank.THREE && 
            c3.getRank() == Card.Rank.ACE) {
            return true;
        }
        
        return false;
    }
    
    // Checks for various different hands


    private static boolean isFlush(List<Card> hand) {
        return hand.get(0).getSuit() == hand.get(1).getSuit() &&
               hand.get(1).getSuit() == hand.get(2).getSuit();
    }
    
    private static boolean isPair(List<Card> hand) {
        return hand.get(0).getRank() == hand.get(1).getRank() ||
               hand.get(1).getRank() == hand.get(2).getRank() ||
               hand.get(0).getRank() == hand.get(2).getRank();
    }
    
    private static Card getHighCard(List<Card> hand) {
        Card high = hand.get(0);
        for (Card c : hand) {
            if (c.getRank().getValue() > high.getRank().getValue()) {
                high = c;
            }
        }
        return high;
    }
    
    private static boolean isQualified(List<Card> hand) {
        Card high = getHighCard(hand);
        return high.getRank().getValue() >= 12; 
    }
}