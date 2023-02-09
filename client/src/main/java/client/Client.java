package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author huseyinaydin
 */
public class Client implements Runnable {

  private final Socket socket;

  private final String username;

  private BufferedReader bufferedReader;

  private BufferedWriter bufferedWriter;

  public Client(Socket socket, String username) {
    this.socket = socket;
    try {
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException ex) {

    }
    this.username = username;
  }

  public static void main(String[] args) throws IOException {
    Socket socket = new Socket("127.0.0.1", 9898);
    System.out.println("Enter your username for group chat: ");
    Scanner scanner = new Scanner(System.in);
    Client client = new Client(socket, scanner.nextLine());
    client.run();
  }

  @Override
  public void run() {
    try {
      bufferedWriter.write(username);
      bufferedWriter.newLine();
      bufferedWriter.flush();

      this.listenForMessage();
      while (socket.isConnected()) {
        Scanner scanner = new Scanner(System.in);
        try {
          String messageToSend = scanner.nextLine();
          bufferedWriter.write(username + ": " + messageToSend);
          bufferedWriter.newLine();
          bufferedWriter.flush();
        } catch (IOException e) {

        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void listenForMessage() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String messageFromGroupChat;
        while (socket.isConnected()) {
          try {
            messageFromGroupChat = bufferedReader.readLine();
            System.out.println(messageFromGroupChat);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }).start();
  }
}
