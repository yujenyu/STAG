package Exception;

public class ActionRefusedException extends Exception{

    private String playerName;
    public ActionRefusedException(String playerName) {
       this.playerName = playerName;
    }

    public String toString() {
        return  playerName + " cannot do this at the moment.";
    }
}
