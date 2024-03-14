package bgu.spl.net.packets;

abstract public class Packet {
    protected byte[] bytes;
    protected int len;


    public Packet() {
        bytes = new byte[518];
        len = 0;
    }

    // Turn the packet to byte array
    //public abstract byte[] getByteArray();

    protected byte[] shortToByteArray (short s) {
        return new byte []{(byte) (s >> 8) , ( byte ) ( s & 0xff ) };
     }

     // converting 2 byte array to a short
    protected short byteArrayToShort (byte[] b){
        return ( short ) ((( short ) b [0]) << 8 | ( short ) ( b [1]) & 0x00ff);
    }
     
    protected void enterBytes (int size, byte[] b){
         for(int i=0; i<size; i++){
             bytes[len] = b[i];
             len ++;
         }
     } 
}