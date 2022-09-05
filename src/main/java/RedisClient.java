import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RedisClient {
    private static final int PORT = 6379;
    private static final String HOST = "127.0.0.1";
    private BufferedReader in;
    private BufferedReader stdIn;
    private PrintWriter out;
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
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.out.println("Error opening streams.");
        }
    }

    private String getClientInput() {
        System.out.println("Accepting input: ");
        String userInput = "";
        try {
            userInput = stdIn.readLine();
        } catch (IOException e) {
            System.out.println("Error reading user input.");
        }
        return userInput;
    }

    public String sendMessage(String message) {
        out.println(message);
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
            stdIn.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing resources.");
        }
    }

    public static void main(String[] args) {
        RedisClient client = new RedisClient();
        client.connect();
        String userInput = client.getClientInput();
        while (!((userInput).equals("exit"))) {
            String response = client.sendMessage(userInput);
            System.out.println(response);
            userInput = client.getClientInput();
        }

        client.end();
    }
}
