package Entities;

public class Entity {
    private String name;
    private String description;
    private EntityType entityType;

    public Entity() { }

    public Entity(String name, String description, EntityType entityType) {
        this.name = name;
        this.description = description;
        this.entityType = entityType;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setEntityType(EntityType entityType) { this.entityType = entityType; }
    public EntityType getEntityType() { return this.entityType; }
}
