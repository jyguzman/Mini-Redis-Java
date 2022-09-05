import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    private static final int PORT = 6379;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private BufferedReader in;
    private PrintWriter out;

    private void openConnection() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
            this.serverSocket.setReuseAddress(true);
            this.clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Error connecting.");
        }
    }

    private void openInputAndOutputStreams() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e){
            System.out.println("Error opening input and output streams.");
        }
    }
    private void readClientInputsAndRespond() {
        String clientMessage = "";
        try {
            clientMessage = in.readLine();
        } catch (IOException e) {
            System.out.println("Error reading line.");
        }

        while (clientMessage != null) {
            out.print("+PONG\r\n");
        }
    }

    private void stopServer() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.openConnection();
        this.openInputAndOutputStreams();
        this.readClientInputsAndRespond();
        this.stopServer();
    }

    public static void main(String[] args) {
        new RedisServer().start();
    }
}
