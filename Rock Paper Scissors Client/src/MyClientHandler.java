import java.io.*;
import java.net.Socket;

public class MyClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private MyServer server;
    private String username;
    private boolean connected = true;
    private String choice;

    public MyClientHandler(Socket socket, MyServer server) {
        try {
            this.socket = socket;
            this.server = server;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            server.broadcast("SERVER: " + username + " has entered the game.");
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            while (connected) {
                server.broadcast("SERVER: " + username + ", make your choice (rock, paper, scissors): ");
                choice = bufferedReader.readLine();
                if (choice != null && (choice.equalsIgnoreCase("rock") || choice.equalsIgnoreCase("paper") || choice.equalsIgnoreCase("scissors"))) {
                    waitForOtherPlayer();
                    determineWinner();
                    break; // Exit the loop after determining the winner
                } else {
                    server.broadcast("SERVER: Invalid choice. Please choose rock, paper, or scissors.");
                }
            }

            // End of game
            server.broadcast("SERVER: Game over! " + username + " has left the game.");
            server.removeClientHandler(this);
            closeEverything();
        } catch (IOException e) {
            closeEverything();
        }
    }



    private void waitForOtherPlayer() throws IOException {
        while (true) {
            if (server.getOtherPlayerChoice(this) != null) {
                break;
            }
        }
    }

    void determineWinner() {
        MyClientHandler otherPlayer = server.getOtherPlayerHandler(this);
        String otherPlayerChoice = otherPlayer.getChoice();

        if (choice.equals(otherPlayerChoice)) {
            server.broadcast("SERVER: It's a tie!");
        } else if ((choice.equals("rock") && otherPlayerChoice.equals("scissors")) ||
                (choice.equals("paper") && otherPlayerChoice.equals("rock")) ||
                (choice.equals("scissors") && otherPlayerChoice.equals("paper"))) {
            server.broadcast("SERVER: " + username + " wins!");
        }

    }

    public String getChoice() {
        return choice;
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
            connected = false;
            server.removeClientHandler(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
