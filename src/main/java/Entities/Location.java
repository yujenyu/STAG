package Entities;
import com.alexmerz.graphviz.objects.*;

import java.util.*;
import java.util.stream.Collectors;

public class Location {
    private String description;
    private final ArrayList<String> paths;
    private final Map<String, Entity> entities;

    public Location(){
        entities = new HashMap<>();
        paths = new ArrayList<>(); }

    public void setDescription(String description){ this.description = description; }

    public String describe(){ return description; }

    private Map<String, Entity> getTypeEntities(EntityType entityType) {
        return entities.entrySet()
                .stream()
                .filter(entity -> entity.getValue().getEntityType().equals(entityType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Set<String> getEntities() {
        return entities.keySet();
    }

    public Map <String, Entity> getArtefacts(){
        return getTypeEntities(EntityType.ARTEFACT);
    }

    public void addArtefact(String name, String description){
        entities.put(name, new Entity(name, description, EntityType.ARTEFACT));
    }

    public void addArtefact(Entity artefact) {
        entities.put(artefact.getName(), new Entity(artefact.getName(), artefact.getDescription(), EntityType.ARTEFACT));
    }

    public void removeArtefact(String artefactName){ entities.remove(artefactName); }

    public Entity getEntity(String name) {
        return entities.get(name);
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getName(), new Entity(entity.getName(), entity.getDescription(), entity.getEntityType()));
    }

    public void removeEntity(String entityName) { entities.remove(entityName); }

    public void addFurniture(String name, String description){
        entities.put(name, new Entity(name, description, EntityType.FURNITURE));
    }
    public Map<String, Entity> getAllFurniture(){ return getTypeEntities(EntityType.FURNITURE); }

    public void removeFurniture(String furnitureName){ entities.remove(furnitureName); }

    public boolean isArtefact(String request) {
            return getTypeEntities(EntityType.ARTEFACT).containsKey(request);
    }

    public boolean isEntity(String name) {
        return entities.containsKey(name);
    }

    public boolean isForFurniture(String request) {
        return getTypeEntities(EntityType.FURNITURE).containsKey(request);}

    public void addCharacter(String name, String description) {
        entities.put(name, new Entity(name, description, EntityType.CHARACTER));
    }
    public void removeCharacter(String characterName) {
        getTypeEntities(EntityType.CHARACTER).remove(characterName);
    }

    public Map<String, Entity> getAllCharacters() {
        return getTypeEntities(EntityType.CHARACTER);
    }

    private String toArtefactName(Node aNode) {
        return aNode.getId().getId();
    }

    private String toArtefactName(Graph aGraph) {
        return aGraph.getId().getId();
    }

    private String toDescription(Node aNode) {
        return aNode.getAttribute("description");
    }

    public void addPath(String path){ paths.add(path); }

    public List<String> getPaths() { return paths; }

    public void setupItems(List<Graph> locationItems) {
        for (Graph locationItem : locationItems) {
            for(Node entity : locationItem.getNodes(false)) {
                switch (toArtefactName(locationItem)) {
                    case "artefacts":
                        this.addArtefact(toArtefactName(entity), toDescription(entity));
                        break;
                    case "characters":
                        this.addCharacter(toArtefactName(entity), toDescription(entity));
                        break;
                    case "furniture":
                        this.addFurniture(toArtefactName(entity), toDescription(entity));
                        break;
                }
            }
        }
    }

    public void transitItem(Location to, String itemName) {
        to.addEntity(this.getEntity(itemName));
        this.removeEntity(itemName);
    }
}