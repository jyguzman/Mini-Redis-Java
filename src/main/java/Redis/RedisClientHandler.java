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
        StringBuilder clientMessage = new StringBuilder();
        int c = 0;
        int[] buf = new int[1024];
        int numStrings = 0;
        int count = 0;
        try {
            clientMessage.append((char)in.read());
            numStrings = Integer.parseInt("" + (char)in.read());
            clientMessage.append(numStrings);
            //System.out.println(numStrings);
            while (count < 1 + 2 * numStrings) {
                c = in.read();
                clientMessage.append((char) c);
                //System.out.print((char)c);
                if ((char) c == '\n')
                    count++;
            }

            //clientMessage = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clientMessage.toString();
    }

    private void communicate() {
        String clientMessage = this.getClientInput();
        System.out.println(clientMessage);
        while (clientMessage != null) {
            String[] clientMessageArgs = deserializer.deserializeRespArray(clientMessage);

            if (clientMessageArgs[0].equals("ECHO")) {
                out.println(clientMessageArgs[1]);
            } else {
                out.println("+PONG\r");
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
