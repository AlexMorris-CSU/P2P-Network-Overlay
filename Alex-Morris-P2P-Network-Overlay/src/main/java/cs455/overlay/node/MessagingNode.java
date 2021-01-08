package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.DataPacket;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.*;

public class MessagingNode extends Thread implements Node {

    public static final Logger LOG = LogManager.getLogger(MessagingNode.class);

    public Socket socket;
    public ServerSocket nodeServerSocket;
    private TCPConnection connector;
    private int nodeID;
    private RoutingTable rt;
    private ArrayList<Integer> overlayNodes;
    private TCPConnectionsCache[] outgoingNodes;
    public Map<Socket, TCPConnection> connectionCache = new HashMap<Socket, TCPConnection>();
    private Queue<DataPacket> dataQueue = new LinkedList<DataPacket>();
    private long sumPayloadReceived;
    private long sumPayloadSent;
    private int receivedCount;
    private int sentCount;
    private int relayCount;

    public MessagingNode(String ip, int port) throws Exception {

        socket = new Socket(ip, port);
        connector = new TCPConnection(socket, this);
        nodeServerSocket = new ServerSocket(0);
        Thread nodeThread = new Thread(this);
        nodeThread.start();
        Thread nodeCommands = new Thread(new InteractiveCommandParser(this));
        nodeCommands.start();
        EventFactory eventFactory = EventFactory.getInstance();
        OverlayNodeSendsRegistration nodeRegistration = (OverlayNodeSendsRegistration) eventFactory.newEvent(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
        nodeRegistration.setIP(InetAddress.getLocalHost().getHostAddress().getBytes());
        nodeRegistration.setPort(nodeServerSocket.getLocalPort());
        connector.getSender().sendData(nodeRegistration.getByte());

        while (true) {
            Socket s = nodeServerSocket.accept();
            TCPConnection incomingConn = new TCPConnection(s, this);

            synchronized (this) {
                connectionCache.put(s, incomingConn);
            }

        }
    }

    public static void main(String[] args) throws Exception{
        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        MessagingNode messagingNode = new MessagingNode(ip,port);
    }

    @Override
    public void run() {
        System.out.println("MessagingNode Started");
        sumPayloadReceived = 0;
        sumPayloadSent = 0;
        sentCount = 0;
        receivedCount = 0;
        relayCount = 0;
        while(true){
            OverlayNodeSendsData ONSD;
            TCPConnection connect;
            DataPacket dp;
            synchronized (dataQueue) {
                dp = dataQueue.poll();
            }
            if(dp != null){
                try{
                    connect = dp.getConnection();
                    ONSD = dp.getData();
                    byte[] bytesToSend = ONSD.getByte();
                    connect.getSender().sendData(bytesToSend);
                    //LOG.info(ONSD.getSourceNodeID() + " " + ONSD.getDestNodeID() + " " + ONSD.getPayload());
                    synchronized (this){
                        relayCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void addDataReceived(OverlayNodeSendsData ONSD){
        int payload = ONSD.getPayload();
        sumPayloadReceived += payload;
        receivedCount++;
    }

    public synchronized void addDataRelayed(){
        relayCount++;
    }

    @Override
    public void onEvent(byte[] data, Socket s) throws IOException {
        EventFactory eventFactory = EventFactory.getInstance();
        switch (data[0]){
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                RegistryReportsRegistrationStatus RRRS = new RegistryReportsRegistrationStatus(data);
                this.nodeID = RRRS.getID();
                System.out.println("My ID: " + nodeID);
                System.out.println(RRRS.getMsg());
                break;

            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                RegistryReportsDeregistrationStatus RRDS = new RegistryReportsDeregistrationStatus(data);
                System.out.println(RRDS.getMsg());
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                System.out.println("Manifest received");
                System.out.println("Routing table for Node " + nodeID);
                RegistrySendsNodeManifest RSNM = new RegistrySendsNodeManifest(data);
                rt = RSNM.getRoutingTable();
                rt.printRoutingTable();
                overlayNodes = RSNM.getOverlayNodes();
                setupConnections();
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                RegistryRequestsTaskInitiate RRTI = new RegistryRequestsTaskInitiate(data);
                int messageNumber = RRTI.getMessageNumber();
                //LOG.info(messageNumber);
                startMessages(messageNumber);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                OverlayNodeSendsData ONSD = new OverlayNodeSendsData(data);

                if(ONSD.getDestNodeID() == nodeID){
                    addDataReceived(ONSD);
                }else{
                    routeMessage(ONSD);
                }
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                System.out.println("Sending Traffic Summary");
                OverlayNodeReportsTrafficSummary ONRTS = (OverlayNodeReportsTrafficSummary) eventFactory.newEvent(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY);
                ONRTS.setNodeID(nodeID);
                ONRTS.setReceived(receivedCount);
                ONRTS.setReceivedSum(sumPayloadReceived);
                ONRTS.setRelayed(relayCount);
                ONRTS.setSent(sentCount);
                ONRTS.setSentSum(sumPayloadSent);

                try {
                    connector.getSender().sendData(ONRTS.getByte());
                    resetCounters();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error sending traffic summary");
                }
                break;
        }

    }

    public synchronized void resetCounters(){
        receivedCount = 0;
        sumPayloadReceived = 0;
        relayCount = 0;
        sentCount = 0;
        sumPayloadSent = 0;
    }

    public void startMessages(int messageNumber) throws IOException {
        EventFactory eventFactory = EventFactory.getInstance();
        for(int i = 0; i < messageNumber; i++){
            int payload = getRandomMsg();
            int sendToNodeID = getSendToNode();
            //System.out.println("Sending messge to: " + sendToNodeID);
            synchronized (this){
                sentCount++;
                sumPayloadSent += payload;
            }
            OverlayNodeSendsData ONSD = (OverlayNodeSendsData) eventFactory.newEvent(Protocol.OVERLAY_NODE_SENDS_DATA);
            ONSD.setSourceNodeID(nodeID);
            ONSD.setDestNodeID(sendToNodeID);
            ONSD.setPayload(payload);
            routeMessage(ONSD);
            //System.out.println("SENDING: " + nodeID + " " + sendToNodeID + " " + payload);
        }

        OverlayNodeReportsTaskFinished ONRTF = (OverlayNodeReportsTaskFinished) eventFactory.newEvent(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED);
        ONRTF.setNodeID(nodeID);
        ONRTF.setIP(InetAddress.getLocalHost().getHostAddress().getBytes());
        ONRTF.setPort(nodeServerSocket.getLocalPort());
        try {
            connector.getSender().sendData(ONRTF.getByte());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void routeMessage(OverlayNodeSendsData dataPacket) throws IOException {
        Boolean nodeInRoutingTable = false;
        Boolean nodeInbetweenRoutingTable = false;
        RoutingEntry sendTo = rt.getEntryAtIndex(0);
        int payload = dataPacket.getPayload();
        int sendToNodeID = dataPacket.getDestNodeID();
        int fromNodeID = dataPacket.getSourceNodeID();

        if(payload == 0 && sendToNodeID == 0 && fromNodeID == 0){
            return;
        }

        for(int i = 0; i < rt.getLength(); i++){
            if(rt.getEntryAtIndex(i).getID() == sendToNodeID){
                nodeInRoutingTable = true;
                sendTo = rt.getEntryAtIndex(i);
            }
        }
        if(!nodeInRoutingTable){

            for(int j = 1; j < rt.getLength(); j++){
                if(rt.getEntryAtIndex(j-1).getID() < sendToNodeID && rt.getEntryAtIndex(j).getID() > sendToNodeID){
                    nodeInbetweenRoutingTable = true;
                    sendTo = rt.getEntryAtIndex(j-1);
                }else{
                    continue;
                }
            }

        }

        if(!nodeInRoutingTable && !nodeInbetweenRoutingTable){
            sendTo = rt.getEntryAtIndex(rt.getLength()-1);
        }

        int index = 0;

        for(int i = 0; i < outgoingNodes.length; i++){
            if(outgoingNodes[i].getID() == sendTo.getID()){
                index = i;
            }
        }

        EventFactory eventFactory = EventFactory.getInstance();
        OverlayNodeSendsData ONSD = (OverlayNodeSendsData) eventFactory.newEvent(Protocol.OVERLAY_NODE_SENDS_DATA);
        ONSD.setDestNodeID(sendToNodeID);
        ONSD.setSourceNodeID(fromNodeID);
        ONSD.setPayload(payload);

        TCPConnection conn = outgoingNodes[index].getConnection();
        DataPacket dp = new DataPacket(ONSD, conn);

        synchronized (this){
            dataQueue.add(dp);
        }
    }

    public int getRandomMsg(){
        Random r = new Random();
        return r.nextInt();
    }
    public int getSendToNode(){
        Random r = new Random();
        int index;
        while(true){
            index = r.nextInt(overlayNodes.size());
            if(overlayNodes.get(index) == nodeID){
                continue;
            }else{
                return overlayNodes.get(index);
            }
        }
    }

    public void setupConnections() throws IOException {
        EventFactory eventFactoy = EventFactory.getInstance();
        outgoingNodes = new TCPConnectionsCache[rt.getLength()];
        for(int i = 0; i < rt.getLength(); i++){
            RoutingEntry temp = rt.getEntryAtIndex(i);
            Socket s = new Socket(temp.getIP(), temp.getPort());
            int tempID = temp.getID();
            TCPConnection connect = new TCPConnection(s, this);
            TCPConnectionsCache tempObj = new TCPConnectionsCache(tempID, temp.getIP(),temp.getPort(), s, connect);
            outgoingNodes[i] = tempObj;
        }

        NodeReportsOverlaySetupStatus NROSS = (NodeReportsOverlaySetupStatus) eventFactoy.newEvent(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS);
        NROSS.setID(nodeID);
        NROSS.setMsg("Node overlay setup Successful");
        try {
            connector.getSender().sendData(NROSS.getByte());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Overlay setup");
    }

    public void deregisterNode() throws Exception {
        EventFactory eventFactory = EventFactory.getInstance();
        OverlayNodeSendsDeregistration ONSD =(OverlayNodeSendsDeregistration) eventFactory.newEvent(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION);
        ONSD.setNodeID(nodeID);
        ONSD.setIP(InetAddress.getLocalHost().getHostAddress().getBytes());
        ONSD.setPort(nodeServerSocket.getLocalPort());
        connector.getSender().sendData(ONSD.getByte());
    }

    public void printStats(){
        System.out.format("%9s%10s%10s%10s%20s%20s", "Node ID", "Sent", "Received", "Relayed", "Sent payload", "Received payload");
        System.out.println("");
        System.out.format("%9s%10s%10s%10s%20s%20s", nodeID, sentCount, receivedCount, relayCount, sumPayloadSent, sumPayloadReceived);
        System.out.println("");
    }

}
