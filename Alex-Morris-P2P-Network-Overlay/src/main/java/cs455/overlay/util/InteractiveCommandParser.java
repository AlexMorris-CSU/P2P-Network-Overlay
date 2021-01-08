package cs455.overlay.util;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

public class InteractiveCommandParser implements Runnable {

    private Node node;
    private boolean close;

    public InteractiveCommandParser(Node node){
        this.node = node;
        close = false;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(!close){
            String commandRaw = scanner.nextLine();
            String command[] = commandRaw.split(" ");
            switch(command[0]){
                case "list-messaging-nodes":
                    Registry registry = (Registry) this.node;
                    registry.listMessagingNodes();
                    break;
                case "setup-overlay":
                    int routingTableSize = 3;
                    Registry registry1 = (Registry) this.node;
                    if(command.length == 2){
                        routingTableSize = Integer.parseInt(command[1]);
                    }
                    registry1.setupOverlay(routingTableSize);
                    break;
                case "list-routing-tables":
                    Registry reg = (Registry) this.node;
                    if(reg.doneWithSetup){
                        reg.printRoutingTables();
                    }else{
                        System.out.println("Overlay not setup");
                    }
                    break;
                case "start":
                    Registry reg1 = (Registry) this.node;
                    if(reg1.doneWithSetup){
                        if(command.length == 2){
                            int messageNumber = Integer.parseInt(command[1]);
                            try {
                                reg1.startSending(messageNumber);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            System.out.println("No message number given");
                        }
                    }else{
                        System.out.println("Overlay not setup");
                    }
                    break;
                case "print-counters-and-diagnostics":
                    MessagingNode mn = (MessagingNode) this.node;
                    mn.printStats();
                    break;
                case "exit-overlay":
                    MessagingNode messagingNode = (MessagingNode) this.node;
                    try {
                        messagingNode.deregisterNode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Node sent deregistration");
                    break;

                default:
                    System.out.println("Unknown Command");
            }
        }
    }

    public void closeCommandParser(){
        close = true;
    }
}
