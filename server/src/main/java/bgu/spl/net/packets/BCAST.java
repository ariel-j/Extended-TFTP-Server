package bgu.spl.net.packets;

public class BCAST extends Packet{
    //true if deleted false if added
    boolean added;
    String fileName;

    public BCAST (boolean added, String fileName){
        this.len = 0;
        this.added = added;
        this.fileName = fileName;
        this.bytes = new byte[4 + fileName.getBytes().length];
        setBytes();

    }

    private void setBytes() {
         //opCode
         enterBytes(2,new byte[]{0,9});
         //deleted/added
         this.bytes[2] = added ? (byte)1 : (byte)0;
         this.len++;
         //file name
         enterBytes(fileName.length(), fileName.getBytes());
         this.bytes[len] = 0;
         this.len++;
    }

    public byte[] getBytes(){
        return bytes;
    }
    
}
