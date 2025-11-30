import java.util.ArrayList;
import java.util.List;

public class GameHistory {
    private final List<GameRecord> records = new ArrayList<>();
    public void addRecord(GameRecord r) { records.add(r); }
    public List<GameRecord> getRecords() { return records; }
}