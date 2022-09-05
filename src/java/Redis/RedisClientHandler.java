package Redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RedisClientHandler extends Thread {
    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

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

    private void readClientInputsAndRespond() {
        String clientMessage = "";
        try {
            clientMessage = in.readLine();
        } catch (IOException e) {
            System.out.println("Error receiving client input.");
        }

        while (clientMessage != null) {
            out.print("+PONG\r\n");
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
        this.readClientInputsAndRespond();
        this.end();
    }
}
