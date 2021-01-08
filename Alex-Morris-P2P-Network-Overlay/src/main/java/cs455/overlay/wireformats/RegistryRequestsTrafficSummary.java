package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTrafficSummary implements Event{

    private byte type = Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    private byte[] data;

    public RegistryRequestsTrafficSummary(){

    }

    public RegistryRequestsTrafficSummary(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        inStream.close();
        din.close();
    }

    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
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
}
