
import com.sun.net.httpserver.HttpServer;

import javax.print.SimpleDoc;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class httpServer {

//    private static int port=8080;
//    public static Socket socket;

    private static HashMap<String, Room> Rooms=new HashMap<>();


    public static void main(String[] args) throws IOException {

//        ServerSocket serverSocket=new ServerSocket(port);
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(true);


        while(true) {

            SocketChannel clientSocketChannel = serverSocketChannel.accept();

            Thread t=new Thread(() -> {
                try {
                    httpRequest request=new httpRequest(clientSocketChannel.socket());
//                    httpRequest request;
//                    request = new httpRequest(socket);
//                    httpResponse response = new httpResponse(request);
//                    response.sendResponse(socket);
                    WebSocket.handshakeBack(request,clientSocketChannel.socket());
                    WebSocket ws=new WebSocket();
                    ws.readMessage(clientSocketChannel.socket());

                    if(Rooms.containsKey(ws.getRoomName())){
                        System.out.println("Enter");
                        try{
                            Rooms.get(ws.getRoomName()).addClient(clientSocketChannel);
                            System.out.println("Room name is"+ws.getRoomName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Room newRoom=new Room(ws.getRoomName());
                        try{
                            Rooms.put(ws.getRoomName(),newRoom);
                            newRoom.addClient(clientSocketChannel);
                            newRoom.listenClient();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();

        }
    }
}
