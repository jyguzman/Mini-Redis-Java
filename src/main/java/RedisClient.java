import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisClient {
    private static final int PORT = 6379;
    private static final String HOST = "127.0.0.1";
    private BufferedReader in;
    private PrintWriter out;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void connect() {
        try {
            this.clientSocket = new Socket(this.HOST, this.PORT);
        } catch (IOException e) {
            System.out.println("Error connecting.");
        }

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error opening streams.");
        }
    }

    public String sendMessage(String message) {
        out.print(message);
        String response = "";
        try {
            response = in.readLine();
        } catch (IOException e) {
            System.out.println("Error receiving response.");
        }
        return response;
    }

    public void end() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing resources.");
        }
    }
}
