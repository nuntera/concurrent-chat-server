A Mindera mindswap bootcamp exercise.  

# Java Multi-Client Chat Server  

A concurrent chat server implementation in Java that allows multiple clients to connect and communicate through a simple command-line interface.  

## Features  

- Multi-client support using thread pool  
- Real-time broadcast messaging  
- Command-line interface  
- Robust error handling and resource management  
- Support for commands:  
  - `/broadcast <message>` - Send message to all connected clients  
  - `/quit` - Disconnect from the server  

## Technical Details  

- Built with Java Socket Programming  
- Uses `CopyOnWriteArrayList` for thread-safe client management  
- Implements separate threads for input/output handling  
- Utilizes `ExecutorService` for managing client connections  
