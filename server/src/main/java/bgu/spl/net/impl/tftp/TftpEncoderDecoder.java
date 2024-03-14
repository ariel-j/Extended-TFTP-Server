package bgu.spl.net.impl.tftp;

import java.util.Arrays;

import bgu.spl.net.api.MessageEncoderDecoder;

public class TftpEncoderDecoder implements MessageEncoderDecoder<byte[]> {
    
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    int opCode = -1;  
    byte[] twoBytes = {-1,-1};
    int len = 0;
    int [] cases = {-1,1,1,3,4,1,6,1,1,1,6} ;
    int counter=0, size=-1;
    boolean start = true;
     
    @Override
    public byte[] decodeNextByte(byte nextByte) {
        if(start) {
            byte[] bytes = new byte[1 << 10];
            start = false;
        }
        // First two bytes
        if(opCode == -1) findOpcode(nextByte);
        
        // Message content:

        else{
            switch (opCode) {
                case 1:         // Case opCode = 1/2/5/7/8/9
                    pushByte(nextByte);
                    if(nextByte == 0) return returnArray();
                    break;
                case 3:
                    // Calculate size using bitwise operations
                    if(size == -1) {
                        if(insertTwoBytes(nextByte)) size = byteArrayToShort(twoBytes)+2;
                    }
                    
                    else{
                        pushByte(nextByte);
                        counter++;
                    }
                     
                    if(counter == size) {
                        return returnArray();}
                    break;
                case 4:
                    pushByte(nextByte);
                    counter++;
                    if(counter == 2) return returnArray();
                    break;
            }
        }
            /*    
            if(opCode == 1) {
            pushByte(nextByte);
            if(nextByte == 0) return returnArray();
            }

        // Case opCode = 3
        else if (opCode == 3){
            if(size == -1 && insertTwoBytes(nextByte))  size = byteArrayToShort(twoBytes) + 2;
            else{
                pushByte(nextByte);
                counter++;
            } 
            if(counter == size) return returnArray();
        }

        // Case opCode = 4
        else if(opCode == 4){
            pushByte(nextByte);
            counter++;
            if(counter == 2) return returnArray();
            } */

        //case opCode = 6/10
        if(opCode == 6)
            return returnArray();

    return null; 
    }

    @Override
    public byte[] encode(byte[] message) {
        return message;
    }

    // Intturept the first two bytes to opCode
    private void findOpcode (byte nextByte){
        if(insertTwoBytes(nextByte)) {
            opCode = cases[byteArrayToShort(twoBytes)];
            twoBytes[0] = -1; 
        }
    }

    private boolean insertTwoBytes (byte nextByte){
        pushByte(nextByte);
        if(twoBytes[0] == -1) {
            twoBytes[0] = nextByte;
        }
        else {
         twoBytes[1] = nextByte;

         return true;
        }
        return false;
    }
    /*
    // converting short to byte array
    private byte[] shortToByteArray (short s) {
       return new byte []{(byte) (s >> 8) , ( byte ) ( s & 0xff ) };

    } */
    

    // converting 2 byte array to a short
    private short byteArrayToShort (byte[] b){
        return ( short ) ((( short ) b [0]) << 8 | ( short ) ( b [1]) & 0x00ff);
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) bytes = Arrays.copyOf(bytes, len * 2);
        bytes[len] = nextByte;
        len++;
    }

    private byte[] returnArray(){   
        byte[] output = Arrays.copyOf(bytes, len); 
        opCode = -1;
        twoBytes[0] = -1;
        len = 0;
        counter = 0;
        size = -1;
        start = true;
        return output;
    }
     
}   