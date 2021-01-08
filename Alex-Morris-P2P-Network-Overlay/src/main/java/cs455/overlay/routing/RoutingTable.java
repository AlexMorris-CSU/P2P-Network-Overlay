package cs455.overlay.routing;

public class RoutingTable {

    public RoutingEntry[] routingTable;
    public int routingTableSize;
    public int nodeID;

    public RoutingTable(int routingTableSize, int ID){
        this.routingTableSize = routingTableSize;
        routingTable = new RoutingEntry[routingTableSize];
        this.nodeID = ID;
    }

    public void addEntry(int index, RoutingEntry entry){
        routingTable[index] = entry;
    }

    public void printRoutingTable(){
        System.out.format("%7s%10s%7s", "NodeID", "IP", "Port");
        System.out.println("");
        for(int i = 0; i < routingTable.length; i++){
            System.out.format("%7s%10s%7s", routingTable[i].getID(), routingTable[i].getIP(), routingTable[i].getPort());
            System.out.println("");
        }
    }
    public int getNodeID(){
        return nodeID;
    }
    public int getLength(){return routingTable.length;}

    public RoutingEntry getEntryAtIndex(int i) {
        return routingTable[i];
    }
}
