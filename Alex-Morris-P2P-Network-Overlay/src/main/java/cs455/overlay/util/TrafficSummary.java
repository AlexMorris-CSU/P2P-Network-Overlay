package cs455.overlay.util;

public class TrafficSummary {

    private int nodeID;
    private int sent;
    private long sentSum;
    private int received;
    private long receivedSum;
    private int relayed;

    public TrafficSummary(int nodeID, int sent, long sentSum, int received, long receivedSum, int relayed){
        this.nodeID = nodeID;
        this.sent = sent;
        this.sentSum = sentSum;
        this.received = received;
        this.receivedSum = receivedSum;
        this.relayed = relayed;
    }

    public int getNodeID(){
        return nodeID;
    }
    public int getSent(){
        return sent;
    }
    public int getRelayed(){
        return relayed;
    }
    public long getSentSum(){
        return sentSum;
    }
    public int getReceived(){
        return received;
    }
    public long getReceivedSum(){
        return receivedSum;
    }
}
