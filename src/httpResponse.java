
import java.io.File;
import java.io.IOException;

import java.io.*;
import java.net.Socket;
public class httpResponse {
    private String responseCode;
    private String contentType;
    private httpRequest request;

    private final String workingDir = "resources/";

    public httpResponse(httpRequest request, Socket socket) {

        

        String fileName = request.getFile();
        File tmpFile = new File(workingDir + fileName);
        if (tmpFile.exists()) {
            responseCode = "HTTP/1.1 200 OK";
        } else {
            responseCode = "HTTP/1.1 404 NOT FOUND";
            contentType = "text/html";
        }

        if (fileName.endsWith(".html")) {
            contentType = "text/html";
        } else if (fileName.endsWith(".css")) {
            contentType = "text/css";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
            contentType = "image/gif";
        }

        this.request = request;
    }

    public void sendResponse(Socket socket) {
        if (request.fileExists("resources")) {
            sendFile(socket, "resources/" + request.getFile());
        } else {
            System.out.println("Sending 404");
            System.out.println();
            send_404(socket);
        }
    }

    public void sendFile(Socket socket, String filePath) {
        try {

            File file = new File(filePath);
            byte[] data = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
//            fis.read(data);
//            fis.close();

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBytes(responseCode + "\r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + data.length);
            dos.writeBytes("\r\n\r\n");
//            dos.write(data);
//            dos.close();

            while(fis.available() > 0){
                dos.write(fis.read());
                dos.flush();
//                Thread.sleep(10);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void send_404(Socket socket) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeBytes(responseCode + "\r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("\r\n\r\n");
            dos.writeBytes("<h1> [404] The file you have requested is not found. </h1>\r\n");
            dos.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    public static void handshakeBack(httpRequest request, Socket clientSocket) {
//        try {
//            OutputStream toClient = clientSocket.getOutputStream();
//            toClient.write(("HTTP/1.1 101 Switching Protocols\r\n" + "Upgrade: websocket\r\n"
//                    + "Connection: Upgrade\r\n" + "Sec-WebSocket-Accept: ").getBytes());
//            String wsValue = request.hashMap.get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            String encode = Base64.getEncoder().encodeToString(md.digest(wsValue.getBytes()));
//            toClient.write((encode + "\r\n\r\n").getBytes());
//
////            PrintWriter pw=new PrintWriter("HTTP/1.1 101 Switching Protocols\r\n" + "Upgrade: websocket\r\n"
////                    + "Connection: Upgrade\r\n" + "Sec-WebSocket-Accept: ");
////            String wsValue=request.hashMap.get("Sec-WebSocket-Key")+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
////            MessageDigest md = MessageDigest.getInstance("SHA-1");
////            String encode = Base64.getEncoder().encodeToString(md.digest(wsValue.getBytes()));
////            pw.write(encode+"\r\n\r\n");
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("unable to write handshake header to client");
//        }
//    }
//
//    public void readMessage(Socket clientSocket){
//        try{
//            DataInputStream input=new DataInputStream(clientSocket.getInputStream());
//
//            //create byteArray to store bytes
//            int payLoadLength=0;
//            byte[] header=new byte[2];
//            byte[] extend;
//
//            //read message from input
//            input.read(header,0,2);
//            byte payLoadLen= (byte)(header[1] & 0x7f);
//            if(payLoadLen<=125){
//                payLoadLength=payLoadLen;
//            }
//            else if(payLoadLen == 126){
//                extend=new byte[2];
//                input.read(extend,0,2);
//                payLoadLength=(extend[0] << 8) | extend[1];
//            }
//            else if(payLoadLen == 127){
//                extend=new byte[8];
//                for(int i=0;i<extend.length;i++){
//                    payLoadLength=(payLoadLength << 8)+(extend[i] & 0xff);
//                }
//            }
//
//            byte[] key =new byte[4];
//            input.read(key,0,4);
//            byte[] encode = new byte[payLoadLength];
//            input.read(encode);
//
//            decode = new byte[payLoadLength];
//            for(int i=0;i<payLoadLength;i++){
//                decode[i]=(byte)(encode[i]^key[i & 0x3]);
//            }
//
//            String s=new String(decode);
//            String[] split=s.split("\\s+", 2);
//            if(split.length>1){
//                name=split[0];
//                message=split[1];
//                System.out.println(name+":"+message);
//            }
//            byte[] temp=getJson().getBytes();
//            jsonBytes=new byte[2+temp.length];
//            jsonBytes[0]=headerMsg[0];
//            jsonBytes[1]=(byte)temp.length;
//            for(int i=0;i<temp.length;i++){
//                jsonBytes[i+2]=temp[i];
//            }
//
////            System.out.println(payLoadLen);
//
//        } catch (Exception e) {
//            System.out.println("Unable to read message from client socket");
//        }
//    }
//
//    public void sendMessage(OutputStream output) throws IOException {
//        output.write(jsonBytes);
//        output.flush();
//    }
//
//    public String getJson(){
//        return "{ \"user\" : \""+this.name + "\", \"message\" : \"" + this.message + "\" }";
//    }
}

