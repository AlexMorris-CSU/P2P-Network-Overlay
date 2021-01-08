package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsRegistrationStatus implements Event{

    private byte type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    private int nodeID;
    private String msg;
    private byte[] data;

    public RegistryReportsRegistrationStatus(){

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

    public RegistryReportsRegistrationStatus(byte[] data) throws IOException {
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
    @Override
    public byte getType() {
        return this.type;
    }
}
