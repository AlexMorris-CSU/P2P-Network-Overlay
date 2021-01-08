package cs455.overlay.wireformats;

public interface Event {
    byte[] getByte() throws Exception;
    byte getType();
}
