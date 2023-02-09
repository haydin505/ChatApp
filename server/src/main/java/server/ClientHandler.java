package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author huseyinaydin
 */
public class ClientHandler implements Runnable {

  public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

  private final Socket socket;

  private BufferedReader bufferedReader;

  private BufferedWriter bufferedWriter;

  private String clientUsername;

  public ClientHandler(Socket socket) {
    this.socket = socket;
    try {
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.clientUsername = bufferedReader.readLine();
      clientHandlers.add(this);
      broadcastMessage("SERVER: " + clientUsername + " has entered the chat.");
    } catch (IOException e) {
      shutdown();
    }
  }

  private void shutdown() {
    try {
      socket.close();
      bufferedReader.close();
      bufferedWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void broadcastMessage(String message) {
    for (ClientHandler clientHandler : clientHandlers) {
      try {
        if (!clientUsername.equals(clientHandler.clientUsername)) {
          clientHandler.bufferedWriter.write(message);
          clientHandler.bufferedWriter.newLine();
          clientHandler.bufferedWriter.flush();
        }
      } catch (IOException e) {
        shutdown();
      }
    }
  }

  public void removeClientHandler() {
    clientHandlers.remove(this);
    broadcastMessage("SERVER: " + clientUsername + "has left the chat.");
    shutdown();
  }

  @Override
  public void run() {
    String messageFromClient;
    while (socket.isConnected()) {
      try {
        messageFromClient = bufferedReader.readLine();
        if (messageFromClient.equals("terminate")) {
          removeClientHandler();
          shutdown();
        }
        broadcastMessage(messageFromClient);
      } catch (IOException e) {
        shutdown();
        break;
      }
    }
  }
}
