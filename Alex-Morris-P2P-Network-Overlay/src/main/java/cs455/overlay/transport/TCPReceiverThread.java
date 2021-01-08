package cs455.overlay.transport;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;

public class TCPReceiverThread implements Runnable{

    public static final Logger LOG = LogManager.getLogger(TCPReceiverThread.class);

    private Socket socket;
    private DataInputStream din;
    Node node;
    public TCPReceiverThread(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.node = node;
        this.din = new DataInputStream(socket.getInputStream());
    }
    public void run() {
        int dataLength = 0;
        while (socket != null) {
            try {
                dataLength = din.readInt();
                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
                node.onEvent(data, this.socket);

            } catch (IOException se) {
                System.out.println("ReceiverThread  se: " + se.getMessage());
                se.printStackTrace();
                break;
            }
        }
    }
}
