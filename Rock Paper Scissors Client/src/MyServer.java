import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyServer {
    private ServerSocket serverSocket;
    private ArrayList<MyClientHandler> clientHandlers = new ArrayList<>();
    private MyClientHandler player1 = null;
    private MyClientHandler player2 = null;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8060);
            MyServer server = new MyServer(serverSocket);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MyServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                MyClientHandler clientHandler = new MyClientHandler(socket, this);

                if (player1 == null) {
                    player1 = clientHandler;
                } else if (player2 == null) {
                    player2 = clientHandler;
                }

                clientHandlers.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();

                if (player1 != null && player2 != null) {
                    player1.sendMessage("SERVER: Player 2 has joined. Let's start the game!");
                    player2.sendMessage("SERVER: Player 1 has joined. Let's start the game!");

                    Thread gameThread = new Thread(() -> {
                        while (true) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (player1.getChoice() != null && player2.getChoice() != null) {
                                player1.determineWinner();
                                player2.determineWinner();

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                player1.sendMessage("SERVER: Make your choice (rock, paper, scissors): ");
                                player2.sendMessage("SERVER: Make your choice (rock, paper, scissors): ");

                                break;
                            }
                        }
                    });

                    gameThread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcast(String message) {
        for (MyClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void removeClientHandler(MyClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public synchronized MyClientHandler getOtherPlayerHandler(MyClientHandler currentHandler) {
        return (currentHandler == player1) ? player2 : player1;
    }

    public synchronized String getOtherPlayerChoice(MyClientHandler currentHandler) {
        return (currentHandler == player1) ? player2.getChoice() : player1.getChoice();
    }


}
