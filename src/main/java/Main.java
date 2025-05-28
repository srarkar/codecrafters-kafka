import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class Main {
  private static final int UNSUPPORTED_VERSION = 35;
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.err.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    try {
      serverSocket = new ServerSocket(port);
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      // Wait for connection from client.
      clientSocket = serverSocket.accept();
      int error_code = 0;
      InputStream inputStream = clientSocket.getInputStream();
      OutputStream outputStream = clientSocket.getOutputStream();
      
      // Data Streams
      DataInputStream in = new DataInputStream(inputStream);
      DataOutputStream out = new DataOutputStream(outputStream);

      // parse client input
      int message_size = in.readInt();
      int request_api_key = in.readShort();
      int request_api_version = in.readShort();
      int correlation_id = in.readInt(); 
      if (request_api_version > 4) {
        error_code = UNSUPPORTED_VERSION;
      }

      // broker response
      out.writeInt(message_size);
      out.writeInt(correlation_id);
      
      if (error_code != 0) {
        out.writeShort(error_code);
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
}
