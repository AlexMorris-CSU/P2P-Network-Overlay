package cs455.overlay.transport;
import java.io.*;
import java.net.*;

public class TCPSender {
    private Socket socket;
    private DataOutputStream dout;
    public TCPSender(Socket socket) throws IOException {
        this.socket = socket;
        this.dout = new DataOutputStream(socket.getOutputStream());
    }
    public void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length;
        //System.out.println(dataLength);
        try{
            dout.writeInt(dataLength);
            dout.write(dataToSend, 0, dataLength);
            dout.flush();
        }catch(IOException e){
            System.out.println(("Error sending data"));
        }
    }
}
