package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author huseyinaydin
 */
public class Server {

  public static void main(String[] args) {
    Server server = new Server();
    server.start();
  }

  public void start() {
    try (ServerSocket server = new ServerSocket(9898)) {

      while (!server.isClosed()) {
        Socket socket = server.accept();
        ClientHandler clientHandler = new ClientHandler(socket);
        Thread thread = new Thread(clientHandler);
        thread.start();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
