package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;

import java.io.*;
import java.util.ArrayList;

public class RegistrySendsNodeManifest implements Event{

    private byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    private int nodeID;
    private int routingTableSize;
    private ArrayList<Integer> overlayNodes;
    private int totalNodes;
    private RoutingTable routingTable;

    public RegistrySendsNodeManifest() {

    }

    @Override
    public byte[] getByte() throws Exception {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(outStream));
        dout.write(getType());

        routingTableSize = routingTable.getLength();
        dout.write(routingTableSize);

        for (int i = 0; i < routingTableSize; i++) {
            RoutingEntry temp = routingTable.getEntryAtIndex(i);
            dout.write(temp.getID());
            String IP = routingTable.getEntryAtIndex(i).getIP();
            int IPLength = IP.length();
            dout.write(IPLength);
            dout.write(IP.getBytes());
            dout.writeInt(routingTable.getEntryAtIndex(i).getPort());
        }

        dout.write(totalNodes);
        for (int i = 0; i < totalNodes; i++) {
            dout.write(overlayNodes.get(i));
        }

        dout.flush();
        marshalledBytes = outStream.toByteArray();
        outStream.close();
        dout.close();
        return marshalledBytes;
    }

    public RegistrySendsNodeManifest(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(inStream));
        this.type = din.readByte();
        routingTableSize = din.readByte();
        routingTable = new RoutingTable(routingTableSize, nodeID);
        for (int i = 0; i < routingTableSize; i++) {
            int tempID = din.readByte();
            int IPLength = din.readByte();
            byte[] IP = new byte[IPLength];
            din.readFully(IP, 0, IPLength);
            String tempIP = new String(IP);
            int tempPort = din.readInt();
            RoutingEntry temp = new RoutingEntry(tempID, tempIP, tempPort);
            routingTable.addEntry(i, temp);
        }

        totalNodes = din.read();
        overlayNodes = new ArrayList<Integer>();
        for (int i = 0; i < totalNodes; i++) {
            overlayNodes.add(din.read());
        }
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
    public void setRoutingTableSize(int size){
        this.routingTableSize = size;
    }
    public void setRoutingTable(RoutingTable rt){
        this.routingTable = rt;
    }
    public void setTotalNodes(int nodes){
        this.totalNodes = nodes;
    }
    public void setOverlayNodes(ArrayList<Integer> nodes){
        this.overlayNodes = nodes;
    }
    public int getNodeID(){
        return nodeID;
    }
    public RoutingTable getRoutingTable(){
        return routingTable;
    }
    public ArrayList<Integer> getOverlayNodes(){
        return overlayNodes;
    }
}
