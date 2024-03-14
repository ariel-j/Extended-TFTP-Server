package bgu.spl.net.packets;

public class ERROR extends Packet {   
    
    private short errorCode;
    private String errMsg;
    final private String[] errors = {
        "Not defined", 
        "File not found",
        "Access violation - File cannot be written,read or deleted." , 
        "No room in disk",
        "Illegal TFTP operation - Unknown Opcode.",
        "File already exists",
        "User not logged in",
        "User already logged in"};

    public ERROR (short errorCode) {
        this.len = 0;
        this.errMsg = errors[errorCode];
        this.bytes = new byte[5 + errMsg.getBytes().length];
        this.errorCode = errorCode;
        setBytes();

    }
    
    public void setBytes() {
        // OpCode
        enterBytes(2,new byte[]{0,5});
        // Error code
        enterBytes(2,shortToByteArray(errorCode));
        // Error message
        byte[] msg = errMsg.getBytes();
        enterBytes(msg.length, msg);
        // End packet
        bytes[len] = 0;
        len++;
    }
    
    public byte[] getByteArray() {
        return bytes;
    } 
}