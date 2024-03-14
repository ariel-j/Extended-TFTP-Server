package bgu.spl.net.impl.tftp;

import bgu.spl.net.srv.Server;

public class TftpServer<T>{
    
 public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        Server.threadPerClient(
            port, 
            () -> new TftpProtocol(),
            () -> new TftpEncoderDecoder()).serve();
    }

}



