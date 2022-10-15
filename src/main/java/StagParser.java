import Actions.Action;
import Actions.Item;
import Entities.Location;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public class StagParser {

    static private final JSONParser jsonParser = new JSONParser();
    static private final Parser parser = new Parser();

    private final String entityFilename;
    private final String actionFilename;
    private GameState gameState;

    public StagParser(String entityFilename, String actionFilename) {
        this.entityFilename = entityFilename;
        this.actionFilename = actionFilename;
        gameState = new GameState();
    }

    private final HashMap <String, Location> locationMap = new HashMap<>();
    boolean startFlag = true;

    public GameState parseGame() throws Exception {
        parseActions();
        parseEntities();
        return gameState;
    }

    private void parseActions() {
        JSONArray actionArray;

        try (FileReader reader = new FileReader(actionFilename)) {

            JSONObject jsonObj = (JSONObject) jsonParser.parse(reader);
            actionArray = (JSONArray) jsonObj.get("actions");

            for (Object actionObj : actionArray) {
                gameState.addAction(parseActionsArray((JSONObject) actionObj));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Action parseActionsArray(JSONObject actionsObj) {
        Item item = new Item();
        Set<String> triggers = null;
        String narration = null;
        for (String actionKey : (Set<String>) actionsObj.keySet()) {
            String stringValue = actionsObj.get(actionKey).toString();
            switch (actionKey) {
                case "triggers":
                    triggers = toSet(actionsObj, actionKey);
                    break;
                case "subjects":
                    item.setSubjects(toSet(actionsObj, actionKey));
                    break;
                case "consumed":
                    item.setConsumed(toSet(actionsObj, actionKey));
                    break;
                case "produced":
                    item.setProduced(toSet(actionsObj, actionKey));
                    break;
                case "narration":
                    narration = stringValue;
                    break;
            }
        }
        return new Action(item, triggers, narration);
    }

    private Set<String> toSet(JSONObject jsonObject, String actionKey) {
        return new HashSet<>(
                new ArrayList<>((JSONArray) jsonObject.get(actionKey))
        );
    }

    private void parseEntities() throws Exception {
        ArrayList<Graph> mainSubgraph;
        try (FileReader reader = new FileReader(this.entityFilename))
        {
            parser.parse(reader);
            mainSubgraph = parser.getGraphs().get(0).getSubgraphs();
            gameState.setLocations(toLocationGraphs(mainSubgraph));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Location> toLocationGraphs(List<Graph> maninSubgraph) {
        for (Graph location : maninSubgraph) {
            buildLocation(location.getSubgraphs());
            buildPaths(location);
        }
        return this.locationMap;
    }

    private void buildLocation(List<Graph> locationGraphs){
        for (Graph currLoc : locationGraphs) {

            Location currLocation = new Location();
            List<Node> nodesAtCurrLoc = currLoc.getNodes(false);
            List<Graph> itemsAtCurrLoc = currLoc.getSubgraphs();

            currLocation.setupItems(itemsAtCurrLoc);
            currLocation.setDescription(toDescription(nodesAtCurrLoc));

            if(startFlag){
                currLocation = gameState.setStartLocation(currLocation);
                startFlag = false;
            }
            locationMap.put(toMetaNodeId(nodesAtCurrLoc), currLocation);
        }
    }

    private void buildPaths(Graph graph) {
        for (Edge edge : graph.getEdges()) {
            Location location = locationMap.getOrDefault(getSourceName(edge), null);
            if (location != null)
                location.addPath(getTargetName(edge));
        }
    }

    private String getSourceName(Edge edge) {
        return edge.getSource().getNode().getId().getId();
    }

    private String getTargetName(Edge edge) {
        return edge.getTarget().getNode().getId().getId();
    }

    private String toDescription(List<Node> nodesAtCurrLoc) {
        return nodesAtCurrLoc.get(0).getAttribute("description");
    }

    private String toMetaNodeId(List<Node> nodesAtCurrLoc) {
        return nodesAtCurrLoc.get(0).getId().getId();
    }
}