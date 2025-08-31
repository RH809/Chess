import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to handle the sending of messages between the 
 * Server and Client
 */
public class ClientHandler implements Runnable{
    
    private Socket socket; // the socket used to communicate with the server
    private BufferedReader br; // input stream reader to read messages from the socket's input stream
    private PrintWriter out; // output stream for this client to send messages through the server
    private String username; // the username of the client
    private String hostAddress; // the host address of the socket
    private int port; // the port of the socket
    private int color; // the color of the client
    /**
     * @param socket - socket used to communicate with server
     * @throws IOException
     * 
     * initalizes the fields using information from the socket
     */
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        username = br.readLine(); 
        
        hostAddress = socket.getInetAddress().getHostAddress();
        port = socket.getPort();

    }

    /**
     * @return the client's color
     */
    public int getColor() {
        return color;
    }

    /**
     * @param c - the color to set to
     * 
     * sets the color of the client
     */
    public void setColor(int c) {
        color = c;
    }

    /**
     * @param msg - message to broadcast
     * 
     * broadcasts the message to all other client handlers in the server
     */
    public void broadcastMessage(String msg) { 
        for (ClientHandler clientHandler : Server.clientHandlers) {
            if (!clientHandler.username.equals(username)) {
                // print this message to everyone who isnt themselves
                clientHandler.out.println(msg);
                clientHandler.out.flush();
            }
        }
    }

    /**
     * @param username - username of the client to send the message to
     * @param msg - message to send
     * 
     * broadcasts a message to one specific client based on the username
     */
    public static void broadcastMessageForSpecificClient(String username, String msg) {
        for (ClientHandler ch : Server.clientHandlers) {
            if (ch.username.equals(username)) {
                ch.out.println(msg);
                ch.out.flush();
                break;
            }
            
        }
    }

    /**
     * tells the Coordinator to send the operations to the other
     * Coordinator so that the boards can be synced
     */
    public void getOperations(){
        out.println("*****send operations");
        out.flush();
    }

    public void getChatHistory(){
        out.println("*****send chat");
        out.flush();
    }

    /**
     * @return the number of clients connected to the server
     */
    public static int numOfConnectedClients() {
        return Server.clientHandlers.size();
    }

    /**
     * @return the ArrayList of ClientHandlers in the server
     */
    public static ArrayList<ClientHandler> getClientHandlers() {
        return Server.clientHandlers;
    }

    /**
     * @throws IOException
     * 
     * terminates the connection to the server
     */
    public void closeConnection() throws IOException {
        socket.close();
    }

    /**
     * @return the host address of the socket
     */
    public String getAddress() {
        return hostAddress;
    }

    /**
     * @return the username of the client
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param ns - the username to set to
     * 
     * sets the username of the client
     */
    public void setUsername(String ns) {
        username = ns;
    }

    /**
     * @return the port of the socket
     */
    public int getPort() {
        return port;
    }

    /**
     * continuously send messages from the ClientHandler's client to
     * the other clients if there is a message
     * if the message is that the client is closed, close the client handler
     */
    @Override
    public void run()
    {
        while (socket != null && !socket.isClosed()) {
            String msgFromClient;
            try {
                msgFromClient = br.readLine();
                System.out.println(msgFromClient);
                if (msgFromClient == null || msgFromClient.equals("[CLIENT CLOSED]")){
                    break;
                }
                broadcastMessage(msgFromClient);
            } catch (Exception e) {
                if (!socket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }
        // left game or lost connection
        try {
            broadcastMessage("SERVER: " + username + " has lost connection!");
            socket.close();
            Server.removeClientHandler(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
   
}