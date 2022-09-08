package Redis;

import DataUtils.Cache;
import RESPUtils.RESPDeserializer;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class RedisClientHandler extends Thread {
    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

    private RESPDeserializer deserializer = new RESPDeserializer();

    private Cache cache = new Cache();

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
        StringBuilder clientMessage = new StringBuilder();
        int c = 0;
        int numStrings = 0;
        int count = 0;
        try {
            clientMessage.append((char)in.read());
            numStrings = Integer.parseInt("" + (char)in.read());
            clientMessage.append(numStrings);

            while (count < 1 + 2 * numStrings) {
                c = in.read();
                if ((char) c == '\n') {
                    count++;
                }
                clientMessage.append((char) c);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return clientMessage.toString();
    }

    private void communicate() {
        String clientMessage = this.getClientInput();
        while (clientMessage != null) {
            String[] clientMessageArgs = deserializer.deserializeRespArray(clientMessage);

            String command = clientMessageArgs[0].toLowerCase();
            switch (command) {
                case "echo":
                    out.println("+" + clientMessageArgs[1]);
                    break;
                case "set":
                    cache.put(clientMessageArgs[1], clientMessageArgs[2]);
                    out.println("+OK");
                    break;
                case "get":
                    out.println("+" + cache.get(clientMessageArgs[1]));
                    break;
                default:
                    out.println("+PONG");
            }

            clientMessage = this.getClientInput();
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
