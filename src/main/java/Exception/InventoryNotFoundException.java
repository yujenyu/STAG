package Exception;

public class InventoryNotFoundException extends Exception{

    public InventoryNotFoundException() {
    }

    public String toString() {
        return  "You don't have match inventory to execute.";
    }

}
