import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Getter
public class Command {

    private final String rawIn;
    private String player;
    private List<String> tokens;

    public Command(String rawIn) {
        this.rawIn = rawIn;
        tokens = new ArrayList<>();
        this.parse();
    }

    public void parse() {
        String[] playerAndTokens = rawIn.replace(": ", " ").split(" ", 0);
        player = playerAndTokens[0];
        tokens = Arrays.asList(playerAndTokens).subList(1, playerAndTokens.length);
    }

    public String getTrigger() {
        return this.getTokens().get(0);
    }

    @Override
    public String toString() {
       return "rawIn: " + rawIn + "\t" + tokens;
    }
}
