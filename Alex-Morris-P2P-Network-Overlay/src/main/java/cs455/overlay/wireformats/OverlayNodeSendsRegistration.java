package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration implements Event{

    private byte type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
    private byte[] IP;
    private int port;
    private byte[] data;


    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
        byte[] ipBytes = this.IP;
        dout.write(ipBytes.length);
        dout.write(ipBytes);
        int port = this.port;
        dout.writeInt(port);
        dout.flush();
        marshalledBytes = outStream.toByteArray();
        outStream.close();
        dout.close();
        return marshalledBytes;
    }

    public OverlayNodeSendsRegistration(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        int iplength = din.readByte();
        this.IP = new byte[iplength];
        din.readFully(this.IP, 0, iplength);
        this.port = din.readInt();
        inStream.close();
        din.close();
    }

    public byte[] getIP() {
        return IP;
    }

    public void setIP(byte[] IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public  OverlayNodeSendsRegistration(){
    }

    public byte getType(){
        return this.type;
    }
}
