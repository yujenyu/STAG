package Entities;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {
    private int blood;
    private Location currLocation;
    private final Map<String, Entity> inventories;

    public Player(String playerName) {
        super(playerName, String.format("[Player-%s]", playerName), EntityType.PLAYER);
        super.setName(playerName);
        this.currLocation = null;
        this.inventories = new HashMap<>();
        this.blood = 3;
    }

    public int getBlood() {
        return this.blood;
    }

    public void injury(int degree) {
        this.blood -= degree;
    }

    public void cure(int degree) {
        this.blood += degree;
    }

    public boolean isDead() {
        return blood == 0;
    }

    public void setPlayerLocation(Location newLocation) {
        currLocation = newLocation;
    }

    public Location getCurrLocation() {
        return currLocation;
    }

    public boolean isInventory(String name) {
        return inventories.containsKey(name);
    }

    public void addInv(Entity artefact) {
        String artefactName = artefact.getName();
        inventories.put(artefactName, artefact);
    }

    public Map<String, Entity> getInv() {
        return inventories;
    }

    public void removeInv(String item) {
        inventories.remove(item);
    }
}