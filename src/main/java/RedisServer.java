import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    private static final int PORT = 6379;

    private ServerSocket serverSocket;
    private int clientNo = 0;
    private Socket clientSocket;

    public void start() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
            while (true) {
                this.clientNo++;
                this.serverSocket.setReuseAddress(true);
                this.clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new RedisClientHandler(this.clientSocket, this.clientNo));
                System.out.println("Servicing client number " + this.clientNo);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error connecting with client number " + this.clientNo);
        }
    }


    public static void main(String[] args) {
        new RedisServer().start();
    }
}
