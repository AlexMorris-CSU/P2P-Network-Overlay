package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTaskFinished implements Event{

    private byte type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    private int nodeID;
    private int Port;
    private byte[] data;
    private byte[] IP;


    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
        byte[] ipBytes = this.IP;
        dout.write(ipBytes.length);
        dout.write(ipBytes);
        int port = this.Port;
        dout.writeInt(port);
        dout.writeInt(nodeID);
        dout.flush();
        marshalledBytes = outStream.toByteArray();
        outStream.close();
        dout.close();
        return marshalledBytes;
    }

    public OverlayNodeReportsTaskFinished(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        int iplength = din.readByte();
        this.IP = new byte[iplength];
        din.readFully(this.IP, 0, iplength);
        this.Port = din.readInt();
        this.nodeID = din.readInt();
        inStream.close();
        din.close();
    }

    @Override
    public byte getType() {
        return type;
    }

    public void setNodeID(int nodeID){
        this.nodeID = nodeID;
    }

    public int getNodeID(){
        return nodeID;
    }

    public byte[] getIP() {
        return IP;
    }

    public void setIP(byte[] IP) {
        this.IP = IP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        this.Port = port;
    }

    public  OverlayNodeReportsTaskFinished(){
    }
}

