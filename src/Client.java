import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bufferedWriter.write("username: " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (Exception e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String message = bufferedReader.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    closeEverything(socket, bufferedWriter, bufferedReader);
                    break;
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {


        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static  void  main(String[] args) throws IOException {
        System.out.println("Enter your username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
