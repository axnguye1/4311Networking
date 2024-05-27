
/**
 *  axnguye1@uno.edu | 2621252
 * @author Amanda Nguyen
 */
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;


public class MyServer  {
    private ServerSocket serverSocket;
    private ArrayList<MyClientHandler> clientHandlers = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8060);
        MyServer server = new MyServer(serverSocket);
        server.startServer();
    }//end main

    public MyServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientHandlers = new ArrayList<>();
    }
    public void startServer() {
        try{ //Server socket open for connections
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                //For each client, a new client handler for each client connection.
                MyClientHandler clientHandler = new MyClientHandler(socket, this);
                //New thread for each connection
                Thread thread = new Thread(clientHandler);
                thread.start();

            }//end while


        } catch (IOException e) {
            closeServerSocket();
        }//end catch

    }//end start server
    public void closeServerSocket(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end close serversocket
    public void Broadcast(String clientMessage) {

        System.out.println( clientMessage);
    }
}//end MyServer
