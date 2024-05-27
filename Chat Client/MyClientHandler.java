
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MyClientHandler implements Runnable{


    public static ArrayList<MyClientHandler> clientHandlers = new ArrayList<>(); //keeps track of all users connected to server
    private String clientUsername;
    private MyServer server;
    private Socket socket;
    private BufferedReader bufferedReader; //reads messages
    private BufferedWriter bufferedWriter; //writes

    public MyClientHandler (Socket socket, MyServer server){
        this.server = server;
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            //Welcome the users
            Broadcast("SERVER: " + clientUsername + " has entered the chat.");
            System.out.println("SERVER: " + clientUsername + " has entered the chat.");
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    @Override
    public void run(){

        String clientMessage;

        //while... check for messages from clients
        while(socket.isConnected()){
            try{
                clientMessage = bufferedReader.readLine();

                //commands
                if (clientMessage.equalsIgnoreCase("allusers")){
                  listAllUsers();
                  continue;
                } else if(clientMessage.equalsIgnoreCase("bye")) {
                    Broadcast("Server: Bye " + clientUsername );
                    System.out.println("Server: Bye " + clientUsername);
                    removeClientHandler();
                    return;
                } else{
                server.Broadcast(clientMessage);
                Broadcast(clientMessage);
                }
            } catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }

        }
    }//end run


//note: for some reason doesn not print list of client handlers/usernames.
    public void listAllUsers() {// For all the users int eh array list, print each element.
        for (MyClientHandler clientHandler : clientHandlers) {
            try {
                    clientHandler.bufferedWriter.write(clientHandler.clientUsername);
                    clientHandler.bufferedWriter.newLine(); //look for next line
                    clientHandler.bufferedWriter.flush();
                    server.Broadcast(String.valueOf(clientHandlers));

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void Broadcast(String sendThisMessage){
        for(MyClientHandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.equals(this)){

                    clientHandler.bufferedWriter.write(sendThisMessage);
                    clientHandler.bufferedWriter.newLine(); //need to send new line because readline looks for the line.
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }

        }
    }//end broadcastmessage
    public void removeClientHandler() {//Bye!
        clientHandlers.remove(this);
        Broadcast("Server: Bye " + clientUsername);
        System.out.println("Server: Bye " + clientUsername );
        Broadcast("Server:" + clientUsername + " has left the chat.");
        System.out.println("Server:" + clientUsername + " has left the chat.");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
    removeClientHandler();
    try{
        if(bufferedReader != null){
            bufferedReader.close();
        }
        if(bufferedWriter != null){
            bufferedWriter.close();
        }
        if (socket != null) {
            socket.close();
        }
    }catch(IOException e){
        e.printStackTrace();
    }
    }//end closeEverything

}



