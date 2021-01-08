package cs455.overlay.routing;

public class RoutingEntry {
    int ID;
    String IP;
    int Port;

    public RoutingEntry(int ID, String IP, int Port){
        this.ID = ID;
        this.IP = IP;
        this.Port = Port;
    }

    public int getID(){
        return this.ID;
    }
    public int getPort(){
        return this.Port;
    }
    public String getIP(){
        return this.IP;
    }
}
