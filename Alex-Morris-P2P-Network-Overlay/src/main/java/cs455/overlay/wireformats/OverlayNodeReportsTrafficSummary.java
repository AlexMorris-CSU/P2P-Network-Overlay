package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTrafficSummary implements Event{

    private byte type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    private int nodeID;
    private int sent;
    private long sentSum;
    private int received;
    private long receivedSum;
    private int relayed;

    public OverlayNodeReportsTrafficSummary(){

    }

    public OverlayNodeReportsTrafficSummary(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        this.nodeID = din.readInt();
        this.sent = din.readInt();
        this.relayed = din.readInt();
        this.sentSum = din.readLong();
        this.received = din.readInt();
        this.receivedSum = din.readLong();
        inStream.close();
        din.close();
    }

    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
        dout.writeInt(this.nodeID);
        dout.writeInt(this.sent);
        dout.writeInt(this.relayed);
        dout.writeLong(this.sentSum);
        dout.writeInt(this.received);
        dout.writeLong(this.receivedSum);
        dout.flush();
        marshalledBytes = outStream.toByteArray();
        outStream.close();
        dout.close();
        return marshalledBytes;
    }

    @Override
    public byte getType() {
        return type;
    }
    public void setNodeID(int nodeID){
        this.nodeID = nodeID;
    }
    public void setSent(int sent){
        this.sent = sent;
    }
    public void setRelayed(int relayed){
        this.relayed = relayed;
    }
    public void setSentSum(long sentSum){
        this.sentSum = sentSum;
    }
    public void setReceived(int received){
        this.received = received;
    }
    public void setReceivedSum(long receivedSum){
        this.receivedSum = receivedSum;
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
