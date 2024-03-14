package bgu.spl.net.packets;

public class ACK extends Packet {

    private short blockNum;
    
    public ACK (short blockNum){
        this.len = 0;
        this.blockNum = blockNum;
        this.bytes = new byte[4];
        setBytes();
    }

    public ACK (byte[] bytes){
        this.bytes = bytes;
        this.len = 4;
        this.blockNum = byteArrayToShort(new byte[]{bytes[2],bytes[3]}); 
    }

    private void setBytes() {
        // OpCode
        enterBytes(2,new byte[]{0,4});
        // block number code
        enterBytes(2,shortToByteArray(blockNum));
    }
    
    public byte[] getByteArray() {
         return this.bytes;
    }

    public int getBlockNum(){
        return this.blockNum;
    }

}