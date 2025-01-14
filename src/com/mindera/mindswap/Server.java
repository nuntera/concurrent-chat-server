package com.mindera.mindswap;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server class that handles multiple client connections for a chat application.
 * Uses a thread pool to manage concurrent client connections.
 */
public class Server {
    /** Thread-safe list to store all connected clients */
    List<ClientHandler> clientHandlers;

    /**
     * Constructs a new Server instance.
     * Initializes the thread-safe list for client handlers.
     */
    public Server() {
        clientHandlers = new CopyOnWriteArrayList<>();
    }

    /**
     * Alternative entry point to run the server as a standalone application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Alternative entry point to run server standalone
        Server server = new Server();
        try {
            server.run();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Starts the server and begins accepting client connections.
     * Creates a thread pool to handle multiple clients concurrently.
     *
     * @throws IOException If an I/O error occurs when opening the server socket
     */
    public void run() throws IOException {
        // Create a thread pool that creates new threads as needed
        ExecutorService cachedPool = Executors.newCachedThreadPool();

        // Create server socket listening on port 8080
        ServerSocket serverSocket = new ServerSocket(8080);

        // Continuously accept new client connections
        while (true) {
            cachedPool.submit(new ClientHandler(serverSocket.accept()));
        }
    }

    /**
     * Inner class that handles individual client connections.
     * Each instance runs in its own thread from the thread pool.
     */
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final BufferedReader reader;
        private final PrintWriter writer;

        /**
         * Constructs a new ClientHandler for a specific client connection.
         *
         * @param clientSocket The socket connection to the client
         * @throws RuntimeException If unable to initialize the client handler
         */
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                // Initialize input/output streams for client communication
                this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                clientHandlers.add(this);
            } catch (IOException e) {
                throw new RuntimeException("Error initializing client handler", e);
            }
        }

        /**
         * Broadcasts a message to all connected clients.
         *
         * @param message The message to broadcast
         */
        public void broadcast(String message) {
            // Send message to all connected clients
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.writer.println(message);
            }
        }

        /**
         * Handles the client connection by processing incoming messages.
         * Supports commands like /quit and /broadcast.
         */
        @Override
        public void run() {
            try {
                String line;
                // Continue reading messages until client disconnects
                while ((line = reader.readLine()) != null) {
                    System.out.println("Received: " + line);

                    // Handle quit command
                    if (line.equals("/quit")) {
                        break;
                    }

                    // Handle broadcast messages
                    if (line.startsWith("/broadcast ")) {
                        broadcast(line.split(" ", 2)[1]);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        /**
         * Closes the client connection and cleans up resources.
         * Removes the client from the active clients list.
         */
        private void closeConnection() {
            try {
                // Clean up resources when client disconnects
                clientHandlers.remove(this);
                clientSocket.close();
                reader.close();
                writer.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
