package Redis;

import RESPUtils.RESPDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.*;
public class RedisClientHandler extends Thread {
    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

    private RESPDeserializer deserializer = new RESPDeserializer();

    public RedisClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private void openInputAndOutputStreams() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error opening input and output streams.");
        }
    }

    private String getClientInput() {
        String clientMessage = "";
        Gson gson = new Gson();
        try {
            clientMessage = gson.fromJson(in.readLine(), String.class);
        } catch (IOException e) {
            System.out.println(e);
        }
        return clientMessage;
    }

    private void communicate() {
        String clientMessage = this.getClientInput();
        while (clientMessage != null) {
            System.out.print(clientMessage);
            String[] clientMessageArgs = deserializer.deserializeRespArray(clientMessage);
            if (clientMessageArgs[0].equals("ECHO")) {
                out.println(clientMessageArgs[1]);
            } else {
                out.println("LKDSFMLSKD");
            }
            clientMessage = getClientInput();
        }
    }

    private void end() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.openInputAndOutputStreams();
        this.communicate();
        this.end();
    }
}
