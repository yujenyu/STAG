package Actions;
import java.util.Set;

// Action object for each JSONObject
public class Action {
    private Item item;
    private Set<String> triggers;
    private String narration;

    public Action(Item item, Set<String> triggers,
                  String narration) {
        this.item = item;
        this.triggers = triggers;
        this.narration = narration;
    }

    public Set<String> getTriggers(){ return triggers; }

    public Item getItem() {
        return this.item;
    }

    public String getNarration(){ return narration; }

    public String toString() {
        return "[subjects] " + item.getSubjects().toString() + "\n" +
               "[consumed] " + item.getConsumed().toString() + "\n" +
               "[produced] " + item.getProduced().toString() + "\n" +
               "[narration] " + narration;
    }
}
