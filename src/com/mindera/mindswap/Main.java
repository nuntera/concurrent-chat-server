package com.mindera.mindswap;

/**
 * Main entry point for the chat application.
 * Starts both the server and one client instance.
 */
public class Main {
    /**
     * Main method that initializes both server and client.
     * The server runs in a separate thread while the client runs in the main thread.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Start server in a separate thread to allow concurrent execution
        new Thread(() -> {
            Server server = new Server();
            try {
                server.run();
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }).start();

        // Wait 1 second to ensure server is initialized before starting client
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Start the first client in the main thread
        Client client = new Client();
        try {
            client.start("localhost", 8080);
        } catch (Exception e) {
            System.out.println("Client connection closed...");
        }
    }
}