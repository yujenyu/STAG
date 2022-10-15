import Actions.Action;
import Entities.Location;

import java.util.*;

public class GameState {
    private final List<Action> actions;
    private Map <String, Location> locations;
    private Location startLocation;

    public GameState() {
        actions = new ArrayList<>();
        locations = new HashMap<>();
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public List<Action> getActions() {
        return actions;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location setStartLocation(Location location){
        startLocation = location;
        return startLocation;
    }

    public void setLocations(Map<String, Location> locations) {
        this.locations = locations;
    }

    public Map<String, Location> getAllWorldLocations(){ return locations; }

}