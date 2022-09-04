import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 6379;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      // Wait for connection from client.
      clientSocket = serverSocket.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      //RespResponse response = new RespResponse("PONG", RespResponse.RespResponseType.SIMPLE_STRING);
      out.println("+PONG\r\n");
      //BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      //String clientInput = in.readLine();


      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      } finally {
        try {
          if (clientSocket != null) {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            //RespResponse response = new RespResponse("PONG", RespResponse.RespResponseType.SIMPLE_STRING);
            out.println("+PONG/r/n");
            clientSocket.close();
          }
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        }
    }
  }
}
