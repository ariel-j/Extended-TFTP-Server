package bgu.spl.net.impl.tftp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.packets.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class holder {
    static ConcurrentHashMap<Integer, Boolean> ids_logins = new ConcurrentHashMap<>();
    static ConcurrentHashMap<Integer, String> connectedUsernames = new ConcurrentHashMap<>();
}

public class TftpProtocol implements BidiMessagingProtocol<byte[]>  {
    int connectionId;
    Connections<byte[]> connections;
    private boolean shouldTerminate;
    boolean loggedIn;
    boolean finished;
    String filesPath;
    String wFile;
    byte[] file;
    
    @Override
    public void start(int connectionId, Connections<byte[]> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
        this.shouldTerminate = false;
        holder.ids_logins.put(connectionId, true);
        loggedIn =false;
        filesPath = ".\\Files"; 
        file = null;
        wFile = null;
        finished=true;
    }
    /*
     * to check if the packets are leagall
     */
    @Override
    public void process(byte[] message) {
        // First message
        if(!loggedIn) logIn(message);
        else{
            
            switch (message[1]) {
                case 1: // RRQ -  Read request
                   handleRRQ(message);   
                    break;
                    
                case 2: //WRQ - Write request
                    handleWRQ(message);   
                    break;
                    
                case 3: //DATA
                    handleData(message);
                    break;
                case 4: //ACK
                    handleACK(message);
                    break;
                case 5: //ERROR
                    //handleERROR(message);
                    break;
                case 6: //DIRQ
                    handleDIRQ(message);
                    break;
                case 7: //logrq
                    connections.send(connectionId, (new ERROR((short)0)).getByteArray()); // Already connected
                    break;
                case 8: //DELRQ
                    handleDELRQ(message);
                    break;
                case 9://BCAST
                    //handleBCAST(message);
                    break;
                case 10: //DISC
                    handleDISC();
                    break;   
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    } 

    // hundling methods
 
    private void handleRRQ(byte[] message){
        System.out.println("enter handleRRQ");
        RRQ rrq = new RRQ(message);
        // Creating the file path
        File f = new File(filesPath + "\\" + rrq.getFileName());
        // Convert the file to a byte array and send it
        if(f.exists()) {
            try {
                file = Files.readAllBytes(f.toPath());
                System.out.println("file.length: " + file.length);
                sendData(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {connections.send(connectionId, (new ERROR((short)1)).getByteArray());} //file not found

    }

    private void handleWRQ(byte[] msg){
        WRQ wrq = new WRQ(msg);
        // remember to check if important
        if(wrq.getFileName().length()==0) connections.send(connectionId, (new ERROR((short)0)).getByteArray()); //file name is iilagal
        File f = new File(filesPath + "\\" + wrq.getFileName());
        //Path filePath = Paths.get(filesPath, wrq.getFileName());
        //if(!Files.exists(filePath)) {
        if(!f.exists()) {
            connections.send(connectionId, (new ACK((short)0)).getByteArray()); //file doesn't exists - wrq success
            file = new byte[0];
            wFile = wrq.getFileName();
        }
        else {connections.send(connectionId, (new ERROR((short)5)).getByteArray()); } //file exsist

    }
    
    private void handleData(byte[] msg) {
        System.out.println("handeling data");
        DATA data = new DATA(msg);
        System.out.println(Arrays.toString(data.getByteArray()));
        // Add the data to the file
        addToFile(data.getData(), data.getpactSize() + file.length);
        // If it was the last block:
        if(data.getpactSize()<512) {
            // Create the file
            Path filePath = Paths.get(filesPath, wFile);
            try {
                Files.write(filePath,file);
            } catch (IOException e) {
                e.printStackTrace();
                connections.send(connectionId, (new ERROR((short)0).getByteArray()));
            }
            // Send to client that the blocked accepted and broadcast that there is a new file
            connections.send(connectionId, (new ACK(data.getBlockNum()).getByteArray()));
            sendBroadcast(new BCAST(true, wFile));            
        }
        else {
            connections.send(connectionId, (new ACK(data.getBlockNum()).getByteArray()));
        }        
    }

   private void handleDIRQ(byte[] msg){
        File dirfile = new File(filesPath);
        String[] dir = dirfile.list();
        LinkedList<Byte> dirList = new LinkedList<>();
        // For each file
        for(String s : dir) {
            byte[] bytes = s.getBytes(); // get the bytes 
            for(byte b: bytes)
                dirList.addLast(b);     // add them to the list
           dirList.addLast((byte)0);    // and separete each file with 0
        }
        // convert to array
        byte[] dirBytes = new byte[dirList.size()];
        for(int i=0; i<dirBytes.length; i++) dirBytes[i] = dirList.get(i); 
        
        // Send to client
        this.file = dirBytes; // for the ack returning
        sendData(0);
   }


    private void handleACK(byte[] msg){
        System.out.println("handling ack");
        // file == null iff the ack is the last one        
        ACK ack = new ACK(msg);
        System.out.println(Arrays.toString(ack.getByteArray()));
        if(file != null ) sendData(ack.getBlockNum());
        else if(!finished) {
            connections.send(connectionId, (new DATA(new byte[0],(short)(ack.getBlockNum()+1))).getByteArray());
            finished = true;
        }   
    }

    private void handleDELRQ(byte[] msg) {
        DELRQ delrq = new DELRQ(msg);
        String filename = delrq.getFileName();
        File dfile = new File(filesPath + "\\" + filename);
        if(dfile.delete()) {
            connections.send(connectionId,(new ACK((short)0)).getByteArray());
            BCAST bcast = new BCAST(false, filename);
            sendBroadcast(bcast);    
        }
        else connections.send(connectionId,(new ERROR((short)1)).getByteArray());
    }

    private void handleDISC(){
        holder.connectedUsernames.remove(connectionId);
        holder.ids_logins.remove(connectionId);
        this.loggedIn = false;
        this.shouldTerminate =true;
        connections.send(connectionId, new ACK((short)0).getByteArray());
    }

    //help methods

    private void addToFile (byte[] msg, int size){
        byte [] newFile = new byte[size];
        for(int i=0; i<file.length; i++) newFile[i] = file[i];
        for(int i=file.length;i<size;i++) newFile[i]=msg[i-file.length];
        this.file = newFile;
    }

    /*
    * @param block: the block number from the ack we got(meaning the last block that the client recived)
    */
    private void sendData(int block){
        System.out.println("enter sendData, blockNum: " + block);
        //seting the range for the data to be hundled
        int start = 512 * block;
        int end = Math.min(start + 512, file.length);
        System.out.println("start: " + start);
        System.out.println("end: " + end);
        byte[] data = new byte[end-start];
        // Copying the data to send
        for(int i=start; i<end; i++) data[i-start] = file[i];
        //sending the packet to the clinet
        DATA d = new DATA(data,(short)(block+1));
        connections.send(connectionId, (d.getByteArray()));
        System.out.println(Arrays.toString(d.getByteArray()));
        // Finish sending
        if(end == file.length) {
            file = null;
            if(data.length == 512) finished = false;
        }

    }

    private void logIn(byte[] msg) {
        // If the first command is not logrq
        if(msg[1] !=7) connections.send(connectionId, (new ERROR((short)6).getByteArray()));

        else {
            // Else if the username is already occupied
            LOGRQ logrq = new LOGRQ(msg);
            if(holder.connectedUsernames.containsValue(logrq.getUserName())) connections.send(connectionId, (new ERROR((short)7).getByteArray()));
            
            else {
                // Else login
                loggedIn = true;
                holder.connectedUsernames.put(connectionId, logrq.getUserName());
                connections.send(connectionId, (new ACK((short)0).getByteArray())); 
            } 
    
        }
    }
    // Converting 2 byte array to a short
    /*private short byteArrayToShort (byte[] b){
        return ( short ) ((( short ) b [0]) << 8 | ( short ) ( b [1]) & 0x00ff);
    } */

    private void sendBroadcast(BCAST bsc){
        //holder.connectedUsernames.forEachKey(connectionId -> connections.send(connectionId,bsc.getBytes()));
        holder.connectedUsernames.forEach((key, value) -> { connections.send(key,bsc.getBytes());});
    }

    


}
