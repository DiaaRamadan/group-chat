import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientUsername = bufferedReader.readLine();
            clients.add(this);
            broadcastMessage("Server: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                broadcastMessage(message);
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            if (!client.clientUsername.equals(clientUsername)) {
                try {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }
            }
        }
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

    public void removeClientHandler() {
        clients.remove(this);
        broadcastMessage("Server " + clientUsername + " has left the chat!");
    }

}
