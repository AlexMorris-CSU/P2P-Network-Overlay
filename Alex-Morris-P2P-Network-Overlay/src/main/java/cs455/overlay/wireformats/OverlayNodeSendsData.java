package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsData implements Event{

    private byte type = Protocol.OVERLAY_NODE_SENDS_DATA;
    private int sourceNodeID;
    private int destNodeID;
    private int payload;
    private byte[] data;

    public OverlayNodeSendsData(){

    }

    public OverlayNodeSendsData(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        this.destNodeID = din.readInt();
        this.sourceNodeID = din.readInt();
        this.payload = din.readInt();


        inStream.close();
        din.close();
    }


    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
        dout.writeInt(this.destNodeID);
        dout.writeInt(this.sourceNodeID);
        dout.writeInt(this.payload);
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
    public void setSourceNodeID(int sourceNodeID){
        this.sourceNodeID = sourceNodeID;
    }
    public void setDestNodeID(int destNodeID){
        this.destNodeID = destNodeID;
    }
    public void setPayload(int payload){
        this.payload = payload;
    }
    public int getSourceNodeID(){
        return sourceNodeID;
    }
    public int getDestNodeID(){
        return destNodeID;
    }
    public int getPayload(){
        return payload;
    }
}
