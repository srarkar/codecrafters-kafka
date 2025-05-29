import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {
  private static final byte[] UNSUPPORTED_VERSION = {0, 35};

  public static void main(String[] args){
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;

    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      // connection from client
      clientSocket = serverSocket.accept();
      System.out.println("Connected to client.");

      byte[] error;
      InputStream inputStream = clientSocket.getInputStream();
      OutputStream outputStream = clientSocket.getOutputStream();
      var messageBlock = new byte[32];

      inputStream.read(messageBlock); // read 32 bytes from client
      final var correlationId = Arrays.copyOfRange(messageBlock, 8, 12);
      final var api_version = Arrays.copyOfRange(messageBlock, 6, 8);
      if (api_version[0] != 0 || api_version[1] > 4) { 
        error = UNSUPPORTED_VERSION;
      } else {
        error = new byte[] {0, 0};
      }
      

      outputStream.write(new byte[] {0, 0, 0, 19}); // message len: 19 bytes
      outputStream.write(correlationId); // correlation_id: 4 bytes
      outputStream.write(error); // api_version: 2 bytes
      // api versions body: 13 bytes
      outputStream.write(new byte[] {2}); // num API keys?? not sure why
      outputStream.write(new byte[] {0, 18}); // api key
      outputStream.write(new byte[] {0, 0}); // min version 0
      outputStream.write(new byte[] {0, 4}); // max version 4
      outputStream.write(new byte[] {0, 0, 0, 0, 0, 0}); // TAG_BUFFER and throttle_time (0)

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
