package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTaskInitiate implements Event{

    private byte type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    private int messageNumber;

    public RegistryRequestsTaskInitiate(){

    }

    public RegistryRequestsTaskInitiate(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        this.messageNumber = din.readInt();
        inStream.close();
        din.close();
    }



    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());
        dout.writeInt(messageNumber);
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
    public void setMessageNumber(int messageNumber){
        this.messageNumber = messageNumber;
    }
    public int getMessageNumber(){
        return messageNumber;
    }
}
