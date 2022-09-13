package Redis;

import RESPUtils.RESPDeserializer;

import java.io.*;
import java.net.Socket;

public class RedisClientHandler extends Thread {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
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
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error opening input and output streams.");
        }
    }

    private String getClientRequest() {
        String clientRequest = "";

        try {
            clientRequest = in.readUTF();
        } catch (IOException e) {
            System.out.println("Client " + this.clientNumber + " disconnected.");
        }

        return clientRequest;
    }

    private void communicate() {
        String clientRequest = this.getClientRequest();
        String[] clientRequestArgs = deserializer.deserializeRespArray(clientRequest);
        while (clientRequestArgs.length > 0) {
            controller.fulfillClientRequest(clientRequestArgs, out);
            clientRequest = this.getClientRequest();
            clientRequestArgs = deserializer.deserializeRespArray(clientRequest);
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
