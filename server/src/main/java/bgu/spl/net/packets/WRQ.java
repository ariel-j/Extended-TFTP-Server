package bgu.spl.net.packets;

public class WRQ extends Packet{
    String fileName;
    
    public WRQ(byte[] bytes){
        this.bytes = bytes;
        this.len = bytes.length; 
        setFileName();
    }

    public String getFileName() {
        return fileName;
    }

    private void setFileName() {
        byte[] byteName = new byte[len-3];
        for(int i = 2; i < len - 1; i++)
            byteName[i-2] = bytes[i];
        this.fileName = new String(byteName);
    } 
}
