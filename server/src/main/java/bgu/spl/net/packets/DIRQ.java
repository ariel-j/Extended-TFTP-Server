package bgu.spl.net.packets;

public class DIRQ extends Packet {
    
    public DIRQ(byte[] bytes){
        this.bytes = bytes;
        this.len = 2;
    }
}
