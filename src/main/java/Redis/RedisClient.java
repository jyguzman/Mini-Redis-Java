package Redis;

import RESPUtils.RESPSerializer;

import java.io.*;
import java.net.Socket;

public class RedisClient {
    private static final int PORT = 6379;
    private static final String HOST = "127.0.0.1";
    private DataInputStream in;
    private BufferedReader stdIn;
    private DataOutputStream out;
    private Socket clientSocket;

    private RESPSerializer serializer = new RESPSerializer();

    public void connect() {
        try {
            this.clientSocket = new Socket(this.HOST, this.PORT);
        } catch (IOException e) {
            System.out.println("Error connecting.");
        }

        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.out.println("Error opening streams.");
        }
    }

    private String getClientInput() {
        System.out.print(this.HOST + ":" + this.PORT + "> ");
        String userInput = "";
        try {
            userInput = stdIn.readLine();
        } catch (IOException e) {
            System.out.println("Error reading user input.");
        }
        return userInput;
    }

    public String sendMessage(String message) {
        String respArray = serializer.serializeToRespArray(message);
        try {
            System.out.println("Your message: " + respArray);
            out.writeUTF(respArray);
            out.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }

        String response = "";
        try {
            response = in.readUTF();
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
        while (!(userInput.equals("exit"))) {
            String response = client.sendMessage(userInput);
            System.out.println(response);
            userInput = client.getClientInput();
        }

        client.end();
    }
}
