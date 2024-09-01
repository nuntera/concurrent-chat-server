package com.mindera.mindswap;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    List<ClientHandler> clientHandlers;

    public Server() {
        // Initialize clientHandlers
        clientHandlers = new CopyOnWriteArrayList<>();
    }

    public static void main(String[] args) {

        Server server = new Server();
        try {
            server.run();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void run() throws IOException {
        // Start the server
        ExecutorService cachedPool = Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            cachedPool.submit(new ClientHandler(serverSocket.accept()));
        }
    }

    private class ClientHandler implements Runnable {
        Socket clientSocket;
        BufferedReader reader;
        PrintWriter writer;


        public ClientHandler(Socket clientSocket) {
            // Initialize clientSocket
            this.clientSocket = clientSocket;
            clientHandlers.add(this);
        }

        public void broadcast(String message) {
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.writer.write(message);
                clientHandler.writer.flush();
            }
        }

        @Override
        public void run() {
            // Handle client
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.startsWith("/broadcast")) {
                        broadcast(line.split(" ", 2)[1]);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
