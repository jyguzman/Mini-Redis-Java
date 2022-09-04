import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    private static final int PORT = 6379;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private BufferedReader in;
    private PrintWriter out;

    public void startServer() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
            this.serverSocket.setReuseAddress(true);
            this.clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Error connecting.");
        }

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e){
            System.out.println("Error opening input and output streams.");
        }

        String inputLine = "";
        try {
            inputLine = in.readLine();
        } catch (IOException e) {
            System.out.println("Error reading line.");
        }
        while (true) {
            out.print("+PONG\r\n");
        }
    }

    public void stopServer() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
