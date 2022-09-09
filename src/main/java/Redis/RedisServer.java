package Redis;

import DataUtils.Cache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    private static final int PORT = 6379;

    private ServerSocket serverSocket;
    private int clientNumber = 0;
    private Socket clientSocket;

    private Cache cache = new Cache();

    private  RedisController controller = new RedisController(this.cache);

    public void start() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
            while (true) {
                this.clientNumber++;
                this.serverSocket.setReuseAddress(true);
                this.clientSocket = serverSocket.accept();
                Thread redisClientThread = new Thread(new RedisClientHandler(this.clientSocket, this.controller, this.clientNumber));
                System.out.println("Accepted client number " + this.clientNumber);
                redisClientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error connecting with client number " + this.clientNumber);
        }
    }

    public static void main(String[] args) {
        new RedisServer().start();
    }
}
