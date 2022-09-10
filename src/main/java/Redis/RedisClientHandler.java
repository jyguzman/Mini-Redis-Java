package Redis;

import RESPUtils.RESPDeserializer;

import java.io.*;
import java.net.Socket;

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

    private String getClientRequest() {
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
        String[] clientRequestArgs = deserializer.deserializeRespArray(this.getClientRequest());
        while (clientRequestArgs.length > 0) {
            controller.fulfillClientRequest(clientRequestArgs, out);
            clientRequestArgs = deserializer.deserializeRespArray(this.getClientRequest());
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
