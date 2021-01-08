package cs455.overlay.transport;

import cs455.overlay.wireformats.OverlayNodeSendsData;

public class DataPacket {

    private OverlayNodeSendsData ONSD;
    private TCPConnection connect;

    public DataPacket(OverlayNodeSendsData ONSD, TCPConnection connect){
        this.ONSD = ONSD;
        this.connect = connect;
    }

    public OverlayNodeSendsData getData() {
        return ONSD;
    }

    public TCPConnection getConnection() {
        return connect;
    }

}
