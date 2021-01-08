package cs455.overlay.transport;

import java.net.*;
import java.io.*;

public class TCPConnectionsCache {

    private int id;
    private String IP;
    private int port;
    private Socket s;
    private TCPConnection connect;

    public TCPConnectionsCache(int id, String IP, int port, Socket s, TCPConnection connect){
        this.id = id;
        this.IP = IP;
        this.port = port;
        this.s = s;
        this.connect = connect;
    }

    public int getID(){
        return id;
    }

    public String getIP(){
        return IP;
    }

    public int getPort(){
        return port;
    }

    public Socket getSocket(){
        return s;
    }
    public TCPConnection getConnection(){
        return connect;
    }

}
