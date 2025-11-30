import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;


class MyTest {


	private Card c(Card.Suit s, Card.Rank r) {
        return new Card(s, r);
    }

 @Test
    void testEvalHand_straightFlush() {
        List<Card> hand = Arrays.asList(
            c(Card.Suit.HEARTS, Card.Rank.ACE),
            c(Card.Suit.HEARTS, Card.Rank.KING),
            c(Card.Suit.HEARTS, Card.Rank.QUEEN)
        );
        assertEquals(500, ThreeCardLogic.evalHand(hand), "Straight flush should be highest rank");
    }

    @Test
    void testEvalHand_threeOfAKind() {
        List<Card> hand = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.TWO),
            c(Card.Suit.HEARTS, Card.Rank.TWO),
            c(Card.Suit.CLUBS, Card.Rank.TWO)
        );
        assertEquals(400, ThreeCardLogic.evalHand(hand), "Three of a kind 400");
    }

    @Test
    void testEvalHand_straight_aceLow() {
        List<Card> hand = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.TWO),
            c(Card.Suit.HEARTS, Card.Rank.THREE),
            c(Card.Suit.CLUBS, Card.Rank.ACE)
        );
        assertEquals(300, ThreeCardLogic.evalHand(hand), "Ace-low straight should be recognized 300");
    }

    @Test
    void testEvalHand_flush() {
        List<Card> hand = Arrays.asList(
            c(Card.Suit.DIAMONDS, Card.Rank.TWO),
            c(Card.Suit.DIAMONDS, Card.Rank.SIX),
            c(Card.Suit.DIAMONDS, Card.Rank.NINE)
        );
        assertEquals(200, ThreeCardLogic.evalHand(hand), "Flush 200");
    }

    @Test
    void testEvalHand_pair() {
        List<Card> hand = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.NINE),
            c(Card.Suit.HEARTS, Card.Rank.NINE),
            c(Card.Suit.CLUBS, Card.Rank.FIVE)
        );
        assertEquals(100, ThreeCardLogic.evalHand(hand), "Pair 100");
    }

    @Test
    void testEvalHand_highCardValue() {
        List<Card> hand = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.FOUR),
            c(Card.Suit.HEARTS, Card.Rank.SEVEN),
            c(Card.Suit.CLUBS, Card.Rank.TEN)
        );
        assertEquals(10, ThreeCardLogic.evalHand(hand), "High card should return highest card value 10");
    }


	@Test
    void playerWins_Test() {
        List<Card> dealer = Arrays.asList(
            c(Card.Suit.HEARTS, Card.Rank.QUEEN),
            c(Card.Suit.DIAMONDS, Card.Rank.TEN),
            c(Card.Suit.CLUBS, Card.Rank.NINE)
        );

        List<Card> player = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.TWO),
            c(Card.Suit.HEARTS, Card.Rank.TWO),
            c(Card.Suit.CLUBS, Card.Rank.TWO)
        );

        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Player (trips) should beat dealer (queen-high)");
    }

    @Test
    void dealerWins_Test() {
        List<Card> dealer = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.ACE),
            c(Card.Suit.HEARTS, Card.Rank.ACE),
            c(Card.Suit.CLUBS, Card.Rank.ACE)
        );

        List<Card> player = Arrays.asList(
            c(Card.Suit.DIAMONDS, Card.Rank.TWO),
            c(Card.Suit.DIAMONDS, Card.Rank.SIX),
            c(Card.Suit.DIAMONDS, Card.Rank.NINE)
        );

        assertEquals(-1, ThreeCardLogic.compareHands(dealer, player), "Dealer (trips A) should beat player (flush)");
    }

    @Test
    void tie_whenHandsAreEquivalent() {
        List<Card> dealer = Arrays.asList(
            c(Card.Suit.HEARTS, Card.Rank.TEN),
            c(Card.Suit.SPADES, Card.Rank.EIGHT),
            c(Card.Suit.CLUBS, Card.Rank.SEVEN)
        );

        List<Card> player = Arrays.asList(
            c(Card.Suit.DIAMONDS, Card.Rank.TEN),
            c(Card.Suit.HEARTS, Card.Rank.EIGHT),
            c(Card.Suit.SPADES, Card.Rank.SEVEN)
        );

        assertEquals(0, ThreeCardLogic.compareHands(dealer, player), "Equivalent hands should tie");
    }


    @Test
    void testEvalPPWinnings_multipliers() {
        int bet = 5;
        List<Card> sf = Arrays.asList(
            c(Card.Suit.HEARTS, Card.Rank.ACE),
            c(Card.Suit.HEARTS, Card.Rank.KING),
            c(Card.Suit.HEARTS, Card.Rank.QUEEN)
        );
        assertEquals(bet * 40, ThreeCardLogic.evalPPWinnings(sf, bet), "Straight flush 40x");

        List<Card> trips = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.TWO),
            c(Card.Suit.HEARTS, Card.Rank.TWO),
            c(Card.Suit.CLUBS, Card.Rank.TWO)
        );
        assertEquals(bet * 30, ThreeCardLogic.evalPPWinnings(trips, bet), "Trips 30x");

        List<Card> straight = Arrays.asList(
            c(Card.Suit.SPADES, Card.Rank.FOUR),
            c(Card.Suit.HEARTS, Card.Rank.FIVE),
            c(Card.Suit.CLUBS, Card.Rank.SIX)
        );
        assertEquals(bet * 6, ThreeCardLogic.evalPPWinnings(straight, bet), "Straight 6x");

        List<Card> flush = Arrays.asList(
            c(Card.Suit.DIAMONDS, Card.Rank.TWO),
            c(Card.Suit.DIAMONDS, Card.Rank.SIX),
            c(Card.Suit.DIAMONDS, Card.Rank.NINE)
        );
        assertEquals(bet * 3, ThreeCardLogic.evalPPWinnings(flush, bet), "Flush 3x");

        List<Card> pair = Arrays.asList(
            c(Card.Suit.HEARTS, Card.Rank.JACK),
            c(Card.Suit.SPADES, Card.Rank.JACK),
            c(Card.Suit.CLUBS, Card.Rank.THREE)
        );
        assertEquals(bet * 1, ThreeCardLogic.evalPPWinnings(pair, bet), "Pair 1x");

        List<Card> none = Arrays.asList(
            c(Card.Suit.HEARTS, Card.Rank.TWO),
            c(Card.Suit.SPADES, Card.Rank.FIVE),
            c(Card.Suit.CLUBS, Card.Rank.NINE)
        );
        assertEquals(0, ThreeCardLogic.evalPPWinnings(none, bet), "No PP win  0");
    }

}
