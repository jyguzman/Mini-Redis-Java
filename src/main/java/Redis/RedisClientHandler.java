package Redis;

import RESPUtils.RESPDeserializer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

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
        try {
            clientMessage = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientMessage;
    }

    private void communicate() {
        String clientMessage = this.getClientInput();

        while (clientMessage != null) {
            //String[] clientMessageArgs = deserializer.deserializeRespArray(clientMessage);
            //System.out.println(clientMessageArgs[0]);
            //if (clientMessageArgs[0].equals("ECHO")) {
            //    out.println(clientMessageArgs[1]);
            //} else {
                out.print("+PONG\r\n");
            //}

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
