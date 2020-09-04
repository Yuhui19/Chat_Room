import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class WebSocket {

    private String name;
    private String message;
    private byte[] decode;
    private byte[] jsonBytes;
    private byte[] header;

    public static void handshakeBack(httpRequest request, Socket clientSocket) {
        try {
            OutputStream toClient = clientSocket.getOutputStream();
            toClient.write(("HTTP/1.1 101 Switching Protocols\r\n" + "Upgrade: websocket\r\n"
                    + "Connection: Upgrade\r\n" + "Sec-WebSocket-Accept: ").getBytes());
            String wsValue = request.hashMap.get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String encode = Base64.getEncoder().encodeToString(md.digest(wsValue.getBytes()));
            toClient.write((encode + "\r\n\r\n").getBytes());

//            PrintWriter pw=new PrintWriter("HTTP/1.1 101 Switching Protocols\r\n" + "Upgrade: websocket\r\n"
//                    + "Connection: Upgrade\r\n" + "Sec-WebSocket-Accept: ");
//            String wsValue=request.hashMap.get("Sec-WebSocket-Key")+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            String encode = Base64.getEncoder().encodeToString(md.digest(wsValue.getBytes()));
//            pw.write(encode+"\r\n\r\n");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("unable to write handshake header to client");
        }
    }

    public void readMessage(Socket clientSocket){
        try{
            DataInputStream input=new DataInputStream(clientSocket.getInputStream());

            //create byteArray to store bytes
            int payLoadLength=0;
            header=new byte[2];
            byte[] extend;

            //read message from input
            input.read(header,0,2);
            byte payLoadLen= (byte)(header[1] & 0x7f);

            if(payLoadLen<=125){
                payLoadLength=payLoadLen;
            }
            else if(payLoadLen == 126){
                extend=new byte[2];
                input.read(extend,0,2);
                payLoadLength=(extend[0] << 8) | extend[1];
            }
            else if(payLoadLen == 127){
                extend=new byte[8];
                for(int i=0;i<extend.length;i++){
                    payLoadLength=(payLoadLength << 8)+(extend[i] & 0xff);
                }
            }

            byte[] key =new byte[4];
            input.read(key,0,4);
            byte[] encode = new byte[payLoadLength];
            input.read(encode);

            decode = new byte[payLoadLength];
            for(int i=0;i<payLoadLength;i++){
                decode[i]=(byte)(encode[i]^key[i & 0x3]);
            }

            String s=new String(decode);
            String[] split=s.split("\\s+", 2);
            if(split.length>1){
                name=split[0];
                message=split[1];
                System.out.println(name+":"+message);
            }
            byte[] temp=getJson().getBytes();
            jsonBytes=new byte[2+temp.length];
            jsonBytes[0]=header[0];
            jsonBytes[1]=(byte)temp.length;
            for(int i=0;i<temp.length;i++){
                jsonBytes[i+2]=temp[i];
            }

//            System.out.println(payLoadLen);

        } catch (Exception e) {
            System.out.println("Unable to read message from client socket");
        }
    }

    public void sendMessage(OutputStream output) throws IOException {
        output.write(jsonBytes);
        output.flush();
    }

    public String getJson(){
        return "{ \"user\" : \""+this.name + "\", \"message\" : \"" + this.message + "\" }";
    }

    public byte[] getJsonBytes(){
        return jsonBytes;
    }

    public byte[] getDecode(){
        return decode;
    }

    public String getRoomName(){
        String output = null;
        String s=new String(decode);
//        System.out.println(s);
        int i=s.indexOf(" ");
        if(s.substring(0,i).equals("join")) {
            output = s.substring(i, s.length());
        }
        return output;
    }
}
