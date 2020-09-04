import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class httpRequest {

    private String method;
    private String file;
    private String protocol;
    public HashMap<String , String> hashMap=new HashMap<>();
    public boolean isWS=false;

    public httpRequest(Socket socket) throws Exception{

//        Scanner readClient;
        try{
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String content=bufferedReader.readLine();
            System.out.println(content);
            String[] str=content.split(" ");
            method=str[0];
            if(str[1].equals("/")){
                file= "index.html";
            }
            else{
                file=str[1].substring(1);
            }
            protocol=str[2];

//            readClient = new Scanner(socket.getInputStream());
//            method = readClient.nextLine();
//            System.out.println(method);
//            file = readClient.nextLine();
//            System.out.println(file);
//            protocol = readClient.nextLine();
//            System.out.println(protocol);
//            readClient.nextLine();

//            HashMap<String , String> hashMap=new HashMap<>();
            while(true) {
//                String content = readClient.nextLine();
                if (content.isEmpty()) {
                    break;
                }
//                System.out.println(content);
//                String key=content.split(": ")[0];
//                System.out.println(key);
//                String value=content.split(": ")[1];
//                System.out.println(value);
                String[] line = content.split(": ");
                if (line.length > 1) {
                    hashMap.put(line[0], line[1]);
                }
            }

            if(hashMap.containsKey("Sec-WebSocket-Key")){
                System.out.println(isWS);
                isWS=true;
            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Error!");
        }
    }

    public boolean fileExists(String input){
        String filepath = input + "/" + file;
//        System.out.println("checking " + filepath);
//        System.out.println(new File(".").getAbsolutePath());
        File tmp=new File(filepath);
        return tmp.exists();
    }

    public String getFile(){
        return file;
    }
    public String getMethod(){
        return method;
    }
    public String getProtocal(){
        return protocol;
    }
}
