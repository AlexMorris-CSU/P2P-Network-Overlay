package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.*;
import java.net.*;

public class TCPConnection {

    private Socket socket;
    private TCPSender sender;
    private TCPReceiverThread receiver;

    public TCPConnection(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.sender = new TCPSender(socket);
        this.receiver = new TCPReceiverThread(socket, node);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public TCPSender getSender() {
        return sender;
    }

    public void setSender(TCPSender sender) {
        this.sender = sender;
    }

    public TCPReceiverThread getReceiver() {
        return receiver;
    }

    public void setReceiver(TCPReceiverThread receiver) {
        this.receiver = receiver;
    }
}
