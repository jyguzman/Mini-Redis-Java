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

    private int clientNumber;

    private RESPDeserializer deserializer = new RESPDeserializer();

    private RedisController controller;
    public RedisClientHandler(Socket clientSocket, RedisController controller, int clientNumber) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.clientNumber = clientNumber;
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
        String numStrings = "";
        int count = 0;
        try {
            clientMessage.append((char)in.read());
            char nextChar = (char)in.read();
            if (!Character.isDigit(nextChar))
                return null;

            while (Character.isDigit(nextChar)) {
                numStrings += ("" + nextChar) ;
                nextChar = (char)in.read();
            }

            clientMessage.append(numStrings);
            while (count < 1 + 2 * Integer.parseInt(numStrings)) {
                c = in.read();
                if ((char) c == '\n') {
                    count++;
                }
                clientMessage.append((char) c);
            }
        } catch (IOException e) {
            System.out.println("Client " + this.clientNumber + " disconnected.");
        }

        return clientMessage.toString();
    }

    private void communicate() {
        String clientMessage = this.getClientInput();
        //System.out.println(clientMessage);
        while (clientMessage != null) {
            String[] clientMessageArgs = deserializer.deserializeRespArray(clientMessage);
            if (clientMessageArgs.length == 0) break;
            String command = clientMessageArgs[0].toLowerCase();
            switch (command) {
                case "echo":
                    out.println("+" + clientMessageArgs[1]);
                    break;
                case "set":
                    String key = clientMessageArgs[1];
                    controller.set(key, clientMessageArgs[2]);
                    if (clientMessageArgs.length > 3 && clientMessageArgs[3].equalsIgnoreCase("PX")) {
                        controller.deleteKeyAfterTimeMilliseconds(key, Long.parseLong(clientMessageArgs[4]));
                    }
                    out.println("+OK");
                    break;
                case "get":
                    out.println(controller.get(clientMessageArgs[1]));
                    break;
                default:
                    out.println("+PONG");
                    break;
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
            System.out.println("Error closing IO streams.");
        }
    }

    @Override
    public void run() {
        this.openInputAndOutputStreams();
        this.communicate();
        this.end();
    }
}
