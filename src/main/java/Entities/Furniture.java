package Entities;

public class Furniture extends Entity {
    public Furniture(String name, String description) {
        super.setName(name);
        super.setDescription(description);
        super.setEntityType(EntityType.FURNITURE);
    }
}
