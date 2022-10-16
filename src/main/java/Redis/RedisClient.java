package Redis;

import RESPUtils.RESPSerializer;
import RESPUtils.RESPDeserializer;

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
    private RESPDeserializer deserializer = new RESPDeserializer();

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

    private String sendMessage(String message) {
        String respArray = serializer.serializeToRespArray(message);
        try {
            out.writeUTF(respArray.substring(0, respArray.length() - 2));
            out.flush();
        } catch(IOException e) {
            System.out.println("Trouble sending client request.");
        }

        String response = "";

        try {
            response = in.readUTF();
        } catch (IOException e) {
            System.out.println("Error receiving response.");
        }
        return response;
    }

    public String readRedisResponse() {
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

    public void communicate() {
        String userInput = this.getClientInput();
        while (!(userInput.equalsIgnoreCase("quit"))) {
            String redisResponse = this.sendMessage(userInput);
            System.out.println(this.deserializer.formatRedisResponse(redisResponse));
            userInput = this.getClientInput();
        }
    }

    public static void main(String[] args) {
        RedisClient client = new RedisClient();
        client.connect();
        client.communicate();
        client.end();
    }
}
