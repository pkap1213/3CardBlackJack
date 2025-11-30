import java.time.LocalDateTime;
import java.util.UUID;

public class GameRecord {
    private final LocalDateTime timestamp;
    private final UUID sessionId;
    private final int ante;
    private final int pairPlus;
    private final int play;
    private final int result;
    private final String summary;

    public GameRecord(LocalDateTime timestamp, UUID sessionId, int ante, int pairPlus, int play, int result, String summary) {
        this.timestamp = timestamp;
        this.sessionId = sessionId;
        this.ante = ante;
        this.pairPlus = pairPlus;
        this.play = play;
        this.result = result;
        this.summary = summary;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public UUID getSessionId() { return sessionId; }
    public int getAnte() { return ante; }
    public int getPairPlus() { return pairPlus; }
    public int getPlay() { return play; }
    public int getResult() { return result; }
    public String getSummary() { return summary; }
}