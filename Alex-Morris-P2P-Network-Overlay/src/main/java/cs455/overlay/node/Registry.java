package cs455.overlay.node;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.util.TrafficSummary;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.*;

public class Registry implements Node{

    public static final Logger LOG = LogManager.getLogger(Registry.class);
    public TCPConnectionsCache[] registeredNodes;
    public RoutingTable[] routingTableArray;
    public Map<Socket, TCPConnection> connectionCache = new HashMap<Socket, TCPConnection>();
    public ArrayList<Integer> currentNodeIDList;
    private int nodesDone;
    public ArrayList<TrafficSummary> trafficSummaryArray;
    public boolean doneWithSetup;


    public Registry(int registryPort) throws Exception {
        ServerSocket regServSock = new ServerSocket(registryPort);
        registeredNodes = new TCPConnectionsCache[127];
        currentNodeIDList = new ArrayList<Integer>();
        trafficSummaryArray = new ArrayList<TrafficSummary>();
        nodesDone = 0;
        doneWithSetup = false;
        Thread registryCommands = new Thread(new InteractiveCommandParser(this));
        registryCommands.start();
        while (true) {
            Socket sock;
            try {
                sock = regServSock.accept();
                TCPConnection conn = new TCPConnection(sock, this);
                int port = conn.getSocket().getLocalPort();
                String IP = conn.getSocket().getInetAddress().getHostName();
                Socket s = conn.getSocket();
                synchronized (this) {
                   connectionCache.put(s, conn);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Registry");
        Registry registry = new Registry(Integer.parseInt(args[0]));
    }

    @Override
    public void onEvent(byte[] data, Socket s) throws IOException {
        EventFactory eventFactory = EventFactory.getInstance();
        switch (data[0]) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                synchronized (this) {
                    OverlayNodeSendsRegistration nodeRegistration = new OverlayNodeSendsRegistration(
                            data);
                    String string = new String(nodeRegistration.getIP());
                    TCPConnection connect = connectionCache.get(s);
                    int currentNodeID;
                    while(true){
                        currentNodeID = getRandomNodeID();
                        if(currentNodeIDList.contains(currentNodeID)){
                            continue;
                        }else{
                            break;
                        }
                    }

                    currentNodeIDList.add(currentNodeID);
                    System.out.println("Node " + currentNodeID + " on " + new String(nodeRegistration.getIP()) + "::" + nodeRegistration.getPort() + ", successfully registered. Current Nodes Registered " + currentNodeIDList.size());
                    registeredNodes[currentNodeID] = new TCPConnectionsCache(currentNodeID, string, nodeRegistration.getPort(), s, connect);

                    RegistryReportsRegistrationStatus RRRS = (RegistryReportsRegistrationStatus) eventFactory.newEvent(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);

                    RRRS.setID(currentNodeID);
                    RRRS.setMsg("Registration request Successful. Current number of nodes in overlay: " + currentNodeIDList.size());

                    try {
                        connect.getSender().sendData(RRRS.getByte());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                    synchronized (this) {
                        OverlayNodeSendsDeregistration ONSD = new OverlayNodeSendsDeregistration(data);
                        int deregNodeID = ONSD.getNodeID();
                        int port = ONSD.getPort();
                        String ip = new String(ONSD.getIP());
                        if(currentNodeIDList.contains(deregNodeID)){
                            TCPConnectionsCache temp = registeredNodes[deregNodeID];
                            if(temp.getPort() == port && temp.getIP().equals(ip)){
                                TCPConnection connect = registeredNodes[deregNodeID].getConnection();
                                registeredNodes[deregNodeID] = null;
                                currentNodeIDList.remove(Integer.valueOf(deregNodeID));

                                RegistryReportsDeregistrationStatus RRDS = (RegistryReportsDeregistrationStatus) eventFactory.newEvent(Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS);
                                RRDS.setID(deregNodeID);
                                RRDS.setMsg("Node successfully deregistered");
                                try {
                                    connect.getSender().sendData(RRDS.getByte());
                                } catch (Exception e) {
                                    System.out.println("Error deregistering node, " + deregNodeID + " on " + ip + "::" + port);
                                    e.printStackTrace();
                                }

                                System.out.println("Node " + deregNodeID + " on " + ip + "::" + port + ", successfully deregistered");
                            }else{
                                System.out.println("Node " + deregNodeID + " on " + ip + "::" + port + ", has already been deregistered");
                            }
                        }else{
                            System.out.println("Node " + deregNodeID + " on " + ip + "::" + port + ", is not registered");
                        }
                    }
                    break;
                case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                    NodeReportsOverlaySetupStatus NROSS = new NodeReportsOverlaySetupStatus(data);
                    System.out.println("Node " + NROSS.getID() + ", " + NROSS.getMsg());
                    synchronized (this){
                        nodesDone++;
                    }
                    if(nodesDone == currentNodeIDList.size()) {
                        nodesDone = 0;
                        doneWithSetup = true;
                        System.out.println("Registry now ready to initiate tasks.");
                    }
                    break;
                case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                    OverlayNodeReportsTaskFinished ONRTF = new OverlayNodeReportsTaskFinished(data);
                    System.out.println("Node " + ONRTF.getNodeID() + " on " + new String(ONRTF.getIP()) + "::" + ONRTF.getPort() + ", finished task");
                    synchronized (this){
                        nodesDone++;
                    }
                    if(nodesDone == currentNodeIDList.size()){
                        nodesDone = 0;
                        System.out.println("All nodes finished sending, waiting 60 seconds before collecting sums");
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Registry Requesting Traffic Summary");
                        sendTrafficSummary();
                    }
                    break;
                case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                    OverlayNodeReportsTrafficSummary ONRTS = new OverlayNodeReportsTrafficSummary(data);
                    TrafficSummary temp = new TrafficSummary(ONRTS.getNodeID(),ONRTS.getSent(), ONRTS.getSentSum(), ONRTS.getReceived(), ONRTS.getReceivedSum(), ONRTS.getRelayed());
                    synchronized (this){
                        trafficSummaryArray.add(temp);
                        nodesDone++;
                    }
                    if(nodesDone == currentNodeIDList.size()){
                        nodesDone = 0;
                        new StatisticsCollectorAndDisplay(trafficSummaryArray);
                        trafficSummaryArray.clear();
                    }
                    break;

        }
    }

    public void sendTrafficSummary() throws IOException {
        EventFactory eventFactory = EventFactory.getInstance();
        for(int i: currentNodeIDList){
            RegistryRequestsTrafficSummary RRTS = (RegistryRequestsTrafficSummary) eventFactory.newEvent(Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY);
            TCPConnection connect = registeredNodes[i].getConnection();
            try {
                connect.getSender().sendData(RRTS.getByte());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error sending traffic summary request to node: " + i);
            }
        }

    }

    public void startSending(int messageNumber) throws IOException {
        EventFactory eventFactory = EventFactory.getInstance();
        for(int i: currentNodeIDList){
            RegistryRequestsTaskInitiate RRTI = (RegistryRequestsTaskInitiate) eventFactory.newEvent(Protocol.REGISTRY_REQUESTS_TASK_INITIATE);
            RRTI.setMessageNumber(messageNumber);

            TCPConnection connect = registeredNodes[i].getConnection();
            try {
                connect.getSender().sendData(RRTI.getByte());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error sending task Initiate to node: " + i);
            }
        }
    }

    public int getRandomNodeID(){
        Random r = new Random();
        return r.nextInt(127);
    }

    public void listMessagingNodes(){
        Collections.sort(currentNodeIDList);
        for(int i: currentNodeIDList){
            System.out.println("Node: " + registeredNodes[i].getID() + " on " + registeredNodes[i].getIP() + "::" + registeredNodes[i].getPort());
        }
    }

    public void setupOverlay(int routingTableSize){
        Collections.sort(currentNodeIDList);
        routingTableArray = new RoutingTable[127];
        for(int i: currentNodeIDList){
            routingTableArray[i] = new RoutingTable(routingTableSize, i);
            //LOG.info("i=" + i);
            for(int j = 0; j < routingTableSize; j++){
                //LOG.info("j=" + j);
                int hopsAway = ((int) Math.pow(2, j) + currentNodeIDList.indexOf(i)) % currentNodeIDList.size();
                //LOG.info("hops=" + hopsAway);
                RoutingEntry temp = new RoutingEntry(registeredNodes[currentNodeIDList.get(hopsAway)].getID(), registeredNodes[currentNodeIDList.get(hopsAway)].getIP(), registeredNodes[currentNodeIDList.get(hopsAway)].getPort());
                routingTableArray[i].addEntry(j, temp);
            }

        }
        try {
            sendManifests();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending manifests");
        }
    }

    public void sendManifests() throws IOException {
        EventFactory eventFactory = EventFactory.getInstance();
        for(int i: currentNodeIDList){
            RegistrySendsNodeManifest RSNM = (RegistrySendsNodeManifest) eventFactory.newEvent(Protocol.REGISTRY_SENDS_NODE_MANIFEST);
            RSNM.setNodeID(i);
            RSNM.setRoutingTable(routingTableArray[i]);
            RSNM.setRoutingTableSize(routingTableArray[i].getLength());
            RSNM.setTotalNodes(currentNodeIDList.size());
            RSNM.setOverlayNodes(currentNodeIDList);

            TCPConnection connect = registeredNodes[i].getConnection();
            try {
                connect.getSender().sendData(RSNM.getByte());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error sending manifest to node: " + i);
            }
        }
    }


    public void printRoutingTables(){
        for(int i : currentNodeIDList){
            System.out.println("---------------------------");
            System.out.println("Routing table for Node " + i);
            routingTableArray[i].printRoutingTable();
        }
    }

}
