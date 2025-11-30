import java.util.ArrayList;
import java.util.List;

public class AppState {
    private static boolean newLookEnabled = false;
    private static List<String> preservedInfo = new ArrayList<>();
    private static int totalBalance = 0;

    public static boolean isNewLookEnabled() {
        return newLookEnabled;
    }

    public static void setNewLookEnabled(boolean v) {
        newLookEnabled = v;
    }

    public static List<String> getPreservedInfo() {
        return new ArrayList<>(preservedInfo);
    }

    public static void setPreservedInfo(List<String> items) {
        List<String> src;
        if (items == null) {
            src = List.of();
        } else {
            src = items;
        }
        preservedInfo = new ArrayList<>(src);
    }

    public static void clearPreservedInfo() {
        preservedInfo.clear();
    }

     public static int getTotalBalance() {
        return totalBalance;
    }

    public static void setTotalBalance(int v) {
        totalBalance = v;
    }

}