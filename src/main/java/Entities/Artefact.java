package Entities;

public class Artefact extends Entity {
    public Artefact(String name, String description) {
        super.setName(name);
        super.setDescription(description);
        super.setEntityType(EntityType.ARTEFACT);
    }
}
