import java.io.*;
import java.util.Scanner;
import java.net.Socket;

public class MyClient {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: "); //grab username for connection, server won't accept w/out username
        String username = scanner.nextLine();
        Socket socket = new Socket("localHost",8060);
        MyClient client  = new MyClient(socket, username);

        client.checkForMessage();
        client.sendMessage();
    }//end main

    public MyClient(Socket socket, String username) {
        try{
            this.socket = socket;
            this.username = username;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }//end client
    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            //read messages from the user -> server
            Scanner scanner = new Scanner(System.in); //input
            while(socket.isConnected()){
                String sendThisMessage = scanner.nextLine();
                bufferedWriter.write(username + ": " + sendThisMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }//end sendMessage

    public void checkForMessage() { //If there is a message from the system, then server -> client
        new Thread(() -> {
        String chatMSG;
        while (socket.isConnected()){
            try{
                chatMSG = bufferedReader.readLine();
                System.out.println(chatMSG);

            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);           }
        }
        }).start();
    }

    //close socket and streams
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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


