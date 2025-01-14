package com.mindera.mindswap;

import java.io.*;
import java.net.Socket;

/**
 * Client class for the chat application.
 * Handles connection to the server and provides user interface for sending/receiving messages.
 */
public class Client {

    /**
     * Alternative entry point to run the client as a standalone application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Alternative entry point to run client standalone
        Client client = new Client();
        try {
            client.start("localhost", 8080);
        } catch (IOException e) {
            System.out.println("Connection closed...");
        }
    }

    /**
     * Starts the client connection to the server.
     * Creates separate threads for handling keyboard input and server messages.
     *
     * @param host The server hostname
     * @param port The server port number
     * @throws IOException If an I/O error occurs when creating the socket connection
     */
    public void start(String host, int port) throws IOException {
        // Create socket connection to server
        Socket socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Display connection info and usage instructions
        System.out.println("Connected to server at " + host + ":" + port);
        System.out.println("Type your messages. Type '/quit' to exit.");

        // Start a separate thread to handle keyboard input
        new Thread(new KeyboardHandler(out, socket)).start();
        
        // Main thread handles incoming messages from server
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        socket.close();
    }

    /**
     * Inner class that handles keyboard input from the user.
     * Runs in a separate thread to allow concurrent input/output.
     */
    private class KeyboardHandler implements Runnable {
        private BufferedWriter out;
        private Socket socket;
        private BufferedReader in;

        /**
         * Constructs a new KeyboardHandler.
         *
         * @param out The writer for sending messages to the server
         * @param socket The socket connection to the server
         */
        public KeyboardHandler(BufferedWriter out, Socket socket) {
            this.out = out;
            this.socket = socket;
            // Initialize reader for keyboard input
            this.in = new BufferedReader(new InputStreamReader(System.in));
        }

        /**
         * Continuously reads keyboard input and sends it to the server.
         * Handles the /quit command to close the connection.
         */
        @Override
        public void run() {
            // Continue handling keyboard input until socket is closed
            while (!socket.isClosed()) {
                try {
                    // Read line from keyboard
                    String line = in.readLine();

                    // Send message to server
                    out.write(line);
                    out.newLine();
                    out.flush();

                    // Handle quit command
                    if (line.equals("/quit")) {
                        socket.close();
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println("Something went wrong with the server. Connection closing...");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
