package Redis;

import RESPUtils.RESPDeserializer;

import java.io.*;
import java.net.Socket;

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
        StringBuilder clientMessage = new StringBuilder();
        int c = 0;
        int numStrings = 0;
        int count = 0;
        try {
            /*char first = (char)in.read();
            if (first == '\n')
                return "\n";*/
            clientMessage.append((char)in.read());
            numStrings = Integer.parseInt("" + (char)in.read());
            clientMessage.append(numStrings);

            while (count < 1 + 2 * numStrings) {
                c = in.read();
                if ((char) c == '\r') {
                    System.out.println("\\r " + count);
                }
                else if ((char) c == '\n') {
                    count++;
                    System.out.println("\\n " + count);
                } else {
                    System.out.println((char)c + " " + count);
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

            if (clientMessageArgs[0].equalsIgnoreCase("ECHO")) {
                out.println("+" + clientMessageArgs[1]);
            } else {
                out.println("+PONG");
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
