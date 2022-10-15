package Exception;

public class EntityNotFoundException extends Exception{
    String entName;

    public EntityNotFoundException (String entName) {
        this.entName = entName;
    }

    public String toString() {
        return  entName + " does not exist in your inventory.";
    }

}
