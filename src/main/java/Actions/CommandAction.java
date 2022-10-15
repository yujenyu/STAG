package Actions;

import Entities.Location;
import Entities.Player;
import Exception.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class CommandAction implements GameCommand {

    private Item item;
    private String narration;
    private final String trigger;
    private Player player;
    private Map<String, Location> locations;

    public CommandAction(String trigger){
        this.trigger = trigger;
    }

    public Action findAction(List<Action> availableActions) throws IOException {
        for(Action action : availableActions) {
            if (action.getTriggers().contains(this.trigger)) {
                return action;
            }
        }

        throw new IOException("Trigger : '" + this.trigger + "' not recognized, ");
    }

    public String runAction(Action action,  Player player,
                          Map<String, Location> locations) throws ActionRefusedException {
        this.player = player;
        this.locations = locations;
        this.item = action.getItem();
        this.narration = action.getNarration();
        return execute();
    }

    public String execute() throws ActionRefusedException {
        if(checkRequiredSubject())
        {
            consumeObject();
            produceObject();
            return narration;
        }
        throw new ActionRefusedException(player.getName());
    }

    private boolean checkRequiredSubject(){

        for(String subject : item.getSubjects()) {
            if (!checkEachSubject(subject, player.getCurrLocation().getEntities()) && !player.isInventory(subject)) {
                return false;
            }
        }
        return true;
    }

        // check through hashset of subjects for matching from user input
    private boolean checkEachSubject(String input, Set<String> checkSubjects) {
        return checkSubjects.contains(input);
    }

    private void consumeObject() {
        for (String toBeConsumed : item.getConsumed()) {
            if (toBeConsumed.equalsIgnoreCase("health")) {
                if (!player.isDead()) {
                    player.injury(1);
                }
            } else if (player.getCurrLocation().isArtefact(toBeConsumed)) {
                player.getCurrLocation().removeArtefact(toBeConsumed);
            } else if (player.getCurrLocation().isForFurniture(toBeConsumed)) {
                player.getCurrLocation().removeFurniture(toBeConsumed);
            } else if (player.isInventory(toBeConsumed)) {
                player.removeInv(toBeConsumed);
            }
        }
    }

    private void produceObject(){

        Location  unplaced = locations.get("unplaced");

        // cycles through each entity to check it exists in unplaced
        for(String toBeProduced : item.getProduced()) {
            if (unplaced.isEntity(toBeProduced)) {
                unplaced.transitItem(player.getCurrLocation(), toBeProduced);
            }
            if (locations.containsKey(toBeProduced)) {
                player.getCurrLocation().addPath(toBeProduced);
            }
            if (toBeProduced.equals("health")) {
                player.cure(1);
            }
        }
    }
}

