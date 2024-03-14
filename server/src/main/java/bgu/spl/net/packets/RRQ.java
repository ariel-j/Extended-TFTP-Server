package bgu.spl.net.packets;

public class RRQ extends Packet{
    String fileName;

    public RRQ(byte[] bytes){
        this.bytes = bytes;
        this.len = bytes.length; 
        setFileName();
    }

    public String getFileName() {
        return fileName;
    }

    private void setFileName() {
        byte[] nameBytes = new byte[len-3];
        for(int i = 2; i < len - 1; i++)
            nameBytes[i-2] = bytes[i];
        this.fileName = new String(nameBytes);
    }
}
