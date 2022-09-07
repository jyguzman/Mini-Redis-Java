package Redis;

import RESPUtils.RESPDeserializer;

import java.io.*;
import java.net.Socket;
public class RedisClientHandler extends Thread {
    private Socket clientSocket;

    private DataOutputStream out;
    private DataInputStream in;

    private RESPDeserializer deserializer = new RESPDeserializer();

    public RedisClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private void openInputAndOutputStreams() {
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error opening input and output streams.");
        }
    }

    private String getClientInput() {
        String clientMessage = "";
        try {
            clientMessage = in.readUTF();
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
            try {
                if (clientMessageArgs[0].equals("ECHO")) {
                    out.writeUTF(clientMessageArgs[1]);
                } else {
                    out.writeUTF("+PONG\r\n");
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
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
