package Entities;

public class Character extends Entity {
    public Character(String name, String description) {
        super.setName(name);
        super.setDescription(description);
        super.setEntityType(EntityType.CHARACTER);
    }
}
