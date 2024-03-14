package bgu.spl.net.packets;

public class DISC extends Packet {
    
    public DISC (byte[] bytes){
        this.bytes = bytes;
        this.len = 2;
    }
}
