package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;

public interface Node {
    public void onEvent(byte[] data, Socket s) throws IOException;
}
