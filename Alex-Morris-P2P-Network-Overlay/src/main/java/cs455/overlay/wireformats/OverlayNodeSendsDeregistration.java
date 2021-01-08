package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration implements Event{

    private byte type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    private byte[] IP;
    private int Port;
    private byte[] data;
    private int nodeID;

    public OverlayNodeSendsDeregistration(){

    }

    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
        byte[] IPb = this.IP;
        dout.write(IPb.length);
        dout.write(IPb);
        int port = this.Port;
        dout.writeInt(port);
        dout.write(nodeID);
        dout.flush();
        marshalledBytes = outStream.toByteArray();
        outStream.close();
        dout.close();
        return marshalledBytes;
    }

    public OverlayNodeSendsDeregistration(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        int iplength = din.readByte();
        this.IP = new byte[iplength];
        din.readFully(this.IP, 0, iplength);
        this.Port = din.readInt();
        this.nodeID = din.read();
        inStream.close();
        din.close();
    }

    public void setNodeID(int nodeID){
        this.nodeID = nodeID;
    }
    public int getNodeID(){
        return nodeID;
    }

    public void setIP(byte[] IP){
        this.IP = IP;
    }

    public byte[] getIP(){
        return IP;
    }

    @Override
    public byte getType() {
        return this.type;
    }

    public void setData(byte[] data){
        this.data = data;
    }

    public int getPort(){
        return Port;
    }

    public void setPort(int Port){
        this.Port = Port;
    }
}
