package bgu.spl.net.packets;

public class LOGRQ extends Packet {
    private String userName;

    public LOGRQ (byte[] bytes) {
        this.bytes = bytes;
        this.len = bytes.length;
        setUserName();
    }

    public String getUserName(){
        return userName;
    }

    private void setUserName(){
        byte[] nameBytes = new byte[len-3];
        for(int i = 2; i < len-1; i++)
            nameBytes[i-2] = bytes[i];
        this.userName = new String(nameBytes);
    } 
}
