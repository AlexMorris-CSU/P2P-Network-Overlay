package cs455.overlay.wireformats;

import java.io.*;

public class NodeReportsOverlaySetupStatus implements Event{

    private byte type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    private int nodeID;
    private String msg;
    private byte[] data;

    public NodeReportsOverlaySetupStatus(){

    }

    public NodeReportsOverlaySetupStatus(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        this.nodeID = din.readByte();
        int msgLength = din.readByte();
        byte[] message = new byte[msgLength];
        din.readFully(message, 0, msgLength);
        this.msg = new String(message);
        inStream.close();
        din.close();
    }

    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
        dout.write(nodeID);
        dout.write(msg.length());
        dout.write(msg.getBytes());
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

    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return msg;
    }
    public void setID(int ID){
        this.nodeID = ID;
    }
    public int getID(){
        return nodeID;
    }
}
