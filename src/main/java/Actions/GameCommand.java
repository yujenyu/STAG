package Actions;

import java.io.IOException;
import Exception.*;

public interface GameCommand {
    String execute() throws IOException, EntityNotFoundException, InventoryNotFoundException, ActionRefusedException;
}
