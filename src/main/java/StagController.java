import Actions.*;
import Entities.*;
import Exception.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.String;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class StagController {
    private final HashMap<String, Player> connectedPlayers;
    private final GameState gameState;
    private Player currPlayer;
    private Location currLocation;
    private List<String> userInput;
    private BufferedWriter out;

    public StagController(GameState gameState){
        this.gameState = gameState;
        this.connectedPlayers = new HashMap<>();
    }

    private boolean isNewPlayer(String playerName) {
        return ! connectedPlayers.containsKey(playerName) ;
    }

    public Player createOrGetConnectedPlayer(String playerName) {
        if(isNewPlayer(playerName)) {
            return initPlayer(playerName);
        }
        return connectedPlayers.get(playerName);
    }

    private Player initPlayer(String playerName) {
        Player newPlayer = new Player(playerName);

        newPlayer.setPlayerLocation(gameState.getStartLocation());
        newPlayer.getCurrLocation().addCharacter(playerName, String.format("[Player-%s]",playerName));
        connectedPlayers.put(playerName, newPlayer);

        return newPlayer;
    }

    public void playGame(Command command) throws IOException, EntityNotFoundException, InventoryNotFoundException, ActionRefusedException {
        currPlayer = createOrGetConnectedPlayer(command.getPlayer());
        setUserInput(command);
        performAction(command);
        currLocation = currPlayer.getCurrLocation();
        endTurn();
        checkPlayerAlive();
    }

    private void setUserInput(Command command){
        this.userInput = command.getTokens();
    }

    private void performAction(Command command) throws IOException, EntityNotFoundException, InventoryNotFoundException, ActionRefusedException {
        if(isGameCommand(command)) {
            out.write(preformGameCmd(command));
        }
        else if (isAction(command)){
            CommandAction action = new CommandAction(command.getTrigger());
            log.debug("userInput: {}\tactionWord: {}", userInput, command.getTrigger());
            Action currAction = action.findAction(gameState.getActions());
            out.write(action.runAction(currAction,
                    currPlayer, gameState.getAllWorldLocations()));
        }
        else {
            out.write("Please ensure you input a valid command.\n");

        }
    }

    private String preformGameCmd(Command command) throws IOException {
        switch (command.getTrigger()) {
            case "inventory":
            case "inv":
                return runInventoryCmd();
            case "get":
                return runGetCmd(command);
            case "drop":
                return runDropCmd(command);
            case "goto":
                return runGotoCmd(command);
            case "look":
                return runLookCmd();
            case "health":
                return runHealthCmd();
            default:
                return "";
        }
    }

    private String runGotoCmd(Command command) throws IOException {
        String requestedLocationName = command.getTokens().get(1);
        if(currPlayer.getCurrLocation().getPaths().contains(requestedLocationName)) {
            currPlayer.getCurrLocation().removeCharacter(currPlayer.getName());
            currPlayer.setPlayerLocation(gameState.getAllWorldLocations().get(requestedLocationName));
            currPlayer.getCurrLocation().addCharacter(currPlayer.getName(), currPlayer.getDescription());
            return runLookCmd();
        }
        return "No path to specified location.";
    }

    private String runDropCmd(Command command) {
        String artefactTobeDropped = command.getTokens().get(1);
        Map<String, Entity> inventoryOfCurrentPlayer = currPlayer.getInv();

        if( inventoryOfCurrentPlayer.containsKey(artefactTobeDropped)) {
            currPlayer.getCurrLocation().addArtefact(inventoryOfCurrentPlayer.get(artefactTobeDropped));
            inventoryOfCurrentPlayer.remove(artefactTobeDropped);

            return currPlayer.getName() + " dropped off a " + artefactTobeDropped;
        }
        return "Specified artefact is not present in your inventory.";

    }

    private String runHealthCmd() {
        return String.format("You have %d units of health remaining.\n", currPlayer.getBlood());
    }

    private String runInventoryCmd() {
        Map<String, Entity> inventoryOfCurrentPlayer = currPlayer.getInv();

        if (! inventoryOfCurrentPlayer.isEmpty()) {
            return "Your inventory contains: " + inventoryOfCurrentPlayer.keySet() + "\n";
        }
        return "Your inventory is empty.\n";
    }

    private String runGetCmd(Command command) {
        if(command.getTokens().size() < 2) {
            return "What artifact do you want to get?";
        }

        String requestedArtefactName = command.getTokens().get(1);
        Entity requestedArtefact;
        Location currLocation = currPlayer.getCurrLocation();

        if(currLocation.getArtefacts().containsKey(requestedArtefactName)) {
            requestedArtefact = currLocation.getArtefacts().get(requestedArtefactName);
            currPlayer.addInv(requestedArtefact);
            currLocation.removeArtefact(requestedArtefactName);

            return currPlayer.getName() + " picked up a " + requestedArtefactName;
        }
        return requestedArtefactName + " does not exist in " + currLocation.describe();
    }

    private String runLookCmd() {
        Location currLocation = currPlayer.getCurrLocation();

        String msg = "You are in " + currLocation.describe() + ".\n" + "You can see:\n";
        log.debug("in lookCmd's execute");
        msg = msg.concat(describeLocationEntity(currLocation.getArtefacts()));
        msg = msg.concat(describeLocationEntity(currLocation.getAllCharacters()));
        msg = msg.concat(describeLocationEntity(currLocation.getAllFurniture()));
        msg = msg.concat(describePaths(currLocation));

        return msg;
    }

    private String describePaths(Location location) {
        List<String> paths = location.getPaths();
        return "You can access from here:\n" + String.join("\n", paths);
    }

    private String describeLocationEntity(Map<String, ? extends Entity> locationEntity) {
        if (! locationEntity.isEmpty()) {
            String msg = locationEntity.values().stream()
                    .map(Entity::getDescription)
                    .filter(descriptions -> !descriptions.contains(String.format("[Player-%s]",currPlayer.getName())))
                    .collect(Collectors.joining("\n"));
            return msg.isEmpty() ? "" : msg + "\n";
        }
        return "";
    }

    private void checkPlayerAlive() throws IOException {
        if(currPlayer.isDead()) {
            Map<String, Entity> copyOnWriteMap = new ConcurrentHashMap<>(currPlayer.getInv());
            for(Map.Entry<String, Entity> artefactEntry : copyOnWriteMap.entrySet()) {
                currPlayer.removeInv(artefactEntry.getKey());
                currLocation.addArtefact(artefactEntry.getValue());
            }
            currPlayer.getCurrLocation().removeCharacter(currPlayer.getName());
            out.write("\nYou have died and lose all of your items, return to the start.\n");

            // reborn
            currPlayer = initPlayer(currPlayer.getName());
        }
    }

    private boolean isAction(Command command) {
        for(Action action : gameState.getActions()) {
            if(action.getTriggers().contains(command.getTrigger())) {
                return true;
            }
        }
        return false;
    }

    private boolean isGameCommand(Command command){
        log.debug("isGameCommand: {}", Config.gameCommands.contains(command.getTrigger()));
        return Config.gameCommands.contains(command.getTrigger());
    }

    public void endTurn(){ currPlayer.setPlayerLocation(currLocation); }

    public void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException {
        try {
            this.out = out;
            Command command = new Command(in.readLine());
            log.debug(command.toString());
            playGame(command);
        } catch (EntityNotFoundException | InventoryNotFoundException | ActionRefusedException e) {
            out.write(e.toString());
        }
    }
}