import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to act as the Server for the Coordinators to communicate with
 * This class will open the ServerSocket and allow Coordinators to connect
 */
public class Server {

    private int port; // the port of the server
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // the ArrayList of connected ClientHandlers
    private ServerSocket ss; // the ServerSocket of the server
    private JFrame frame; // the frame for the server GUI
    private ArrayList<JPanel> allPanels; // the ArrayList of panels in the frame
    private JPanel panel2TwoTwo; // the panel that holds both of the clientInGUI's
    private JPanel panel2Two; // the panel that holds panel2TwoOne (which holds connectedClientsLabel) and panel2TwoTwo
    private int colorOfHost; // the color of the host Coordinator
    private JButton chooseHostColor; // the button to select the host's color
    private String platformCurrentlyOn; // which platform the game is being played on (Windows or Mac)
    private JLabel chooseHostLabel; // the label that says prompts the user to select the host color

    /**
     * calls the other constructor using the default port of 1234
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     */
    public Server() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        // default port 1234
        this(1234);
    }

    /**
     * 
     * @param port - port of the server
     *
     * initializes the fields in the class 
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     */
    public Server(int port) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        this.port = port;
        allPanels = new ArrayList<JPanel>();
        colorOfHost = 0; // 0 white 1 black

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        if (UIManager.getLookAndFeel().getName().equals("Windows")) {
            platformCurrentlyOn = "Windows";
        } else {
            platformCurrentlyOn = "Mac";
        }
    }

    /**
     * opens the server and continuously checks for clients connecting
     * if a client connects:
     * - creates the ClientHandler to communicate with the Coordinator
     * - if its the first client, set its color to the host color
     * - if its the second client, set its color to opposite of the first color and sync the board
     * @throws IOException
     */
    public void openServer() throws IOException {
        ss = new ServerSocket(port);
        System.out.println("Opened Server!");

        // create gui
        createGUI();
        Timer timer = new Timer(500, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateGUI();
            }
            
        });
        timer.start();

        // end gui
        
        while (!ss.isClosed()) { 
            Socket s = null;
            try {
                s = ss.accept();
                
                if (clientHandlers.size() >= 2) {
                    System.out.println("Connection refused");
                    s.close();
                    continue;
                }

                System.out.println("A new client has connected!");
            
                ClientHandler ch = new ClientHandler(s);

                for (ClientHandler clients : clientHandlers) {
                    if (ch.getUsername().equals(clients.getUsername())) {
                        ch.setUsername(ch.getUsername() + 2);
                        break;
                    }
                }
                clientHandlers.add(ch);

                // check if usernames are the same
                

                if (clientHandlers.size() == 1) { // first client who joined 
                    ch.setColor(colorOfHost);
                    ClientHandler.broadcastMessageForSpecificClient(ch.getUsername(), "***" + colorOfHost);
                    ClientHandler.broadcastMessageForSpecificClient(ch.getUsername(), "[1 CLIENT]");
                    ClientHandler.broadcastMessageForSpecificClient(ch.getUsername(), "[CONNECTION] SELF");
                } else if (clientHandlers.size() == 2) { // second client who joined
                    // use not used color
                    int newC = 0;
                    ClientHandler other = null;
                    for(ClientHandler ch2 : clientHandlers){
                        if(ch2.getColor() == newC && !ch2.getUsername().equals(ch.getUsername())){
                            newC = 1; 
                        }
                        if (!ch2.getUsername().equals(ch.getUsername())) other = ch2;
                    }
                    ch.setColor(newC);
                    ClientHandler.broadcastMessageForSpecificClient(ch.getUsername(), "***" + newC);
                    ClientHandler.broadcastMessageForSpecificClient(ch.getUsername(), "[2 CLIENTS]");
                    ClientHandler.broadcastMessageForSpecificClient(other.getUsername(), "[2 CLIENTS]");
                    other.getOperations();
                    other.getChatHistory();
                }
                
                updateGUI();

                Thread t = new Thread(ch);
                t.start();
                
            } catch (Exception e) {
                break;
            }
            
            
        }

    }

    /**
     * 
     * @param hostAddress - hostAddress of the client handler
     * @param port - port of the client handler
     * 
     * removes a specific client handler from the list based on their host address and port
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void removeSpecificClient(String hostAddress, int port) throws IOException, InterruptedException {
        Iterator<ClientHandler> iterator = clientHandlers.iterator();
        while (iterator.hasNext()) {
            ClientHandler ch = iterator.next();
            if (ch.getAddress().equals(hostAddress) && (ch.getPort() == port)) {
                ch.closeConnection();
                iterator.remove(); // Use iterator to remove the client
                return;
            }
        }
        if(clientHandlers.size() == 1){
            clientHandlers.get(0).broadcastMessage("[1 CLIENT]");
        }
    }

    /**
     * creates the GUI for the server
     * @throws IOException
     */
    public void createGUI() throws IOException {
        int width = 220;
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width,250);
        frame.setLayout(new BorderLayout());
        frame.setTitle("Server");
        
        JPanel panel = new JPanel(new GridLayout(3,1));
        panel.setPreferredSize(new Dimension(width, 80));
        panel.setBackground(Color.red);
        allPanels.add(panel);

        JPanel panel2 = new JPanel();
        panel2.setSize(width, 80);
        panel2.setBackground(Color.blue);

        JPanel panel3 = new JPanel(new GridBagLayout());
        panel3.setPreferredSize(new Dimension(width, 30));
        allPanels.add(panel3);

        JButton button = new JButton("Close");
        button.setPreferredSize(new Dimension(80,30));

        button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
                try
				{
					closeServer();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
            
        });

        panel3.add(button);

        JLabel serverIPAddress = new JLabel("IP Address: ");
        serverIPAddress.setForeground(Color.white);
        serverIPAddress.setText(serverIPAddress.getText() + ServerMenu.getLocalIPAddress(platformCurrentlyOn));
        
        JPanel panel1One = new JPanel(new GridBagLayout());
        JPanel panel1Two = new JPanel(new GridBagLayout());
        JPanel panel1Three = new JPanel(new GridBagLayout());
        allPanels.add(panel1One);
        allPanels.add(panel1Two);
        allPanels.add(panel1Three);
        panel.add(panel1One);
        panel.add(panel1Two);
        panel.add(panel1Three);

        chooseHostColor = new JButton();
        chooseHostColor.setPreferredSize(new Dimension(35,15));
        updateHostColor();
        chooseHostColor.setContentAreaFilled(false);
        chooseHostColor.setOpaque(true);
        chooseHostColor.setFocusable(false);
        chooseHostColor.setBorderPainted(false);
        chooseHostColor.setBorderPainted(false);
        chooseHostColor.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (colorOfHost == 0) colorOfHost = 1;
                else colorOfHost = 0;
                updateHostColor();
            }
            
        });

        chooseHostLabel = new JLabel("Host Color: ");
        chooseHostLabel.setForeground(Color.white);

        panel1Three.add(chooseHostLabel);
        panel1Three.add(chooseHostColor);

        JLabel portLabel = new JLabel("Port: " + String.valueOf(ss.getLocalPort()));
        portLabel.setForeground(Color.white);

        panel1One.add(serverIPAddress);
        panel1Two.add(portLabel);


        frame.add(panel, BorderLayout.NORTH);
        frame.add(panel2);
        frame.add(panel3, BorderLayout.SOUTH);

        // add middle part

        panel2.setLayout(new BorderLayout());

        JPanel panel2One = new JPanel();
        allPanels.add(panel2One);
        panel2One.setPreferredSize(new Dimension(30,panel2.getHeight()));

        panel2Two = new JPanel();
        panel2Two.setLayout(new GridLayout(2,1));

        JPanel panel2TwoOne = new JPanel(new BorderLayout());
        allPanels.add(panel2TwoOne);

        JLabel connectedClientsLabel = new JLabel("Connected Clients:", SwingConstants.CENTER);
        connectedClientsLabel.setForeground(Color.white);
        panel2TwoOne.add(connectedClientsLabel, BorderLayout.SOUTH);
        
        panel2TwoTwo = new JPanel(new GridLayout(2,1)); // pane for the clientInGUI's

        // constantly update client gui if there are detected changes
        
        panel2Two.add(panel2TwoOne);
        panel2Two.add(panel2TwoTwo);

        JPanel panel2Three = new JPanel();
        allPanels.add(panel2Three);
        panel2Three.setPreferredSize(new Dimension(30,panel2.getHeight()));


        panel2.add(panel2One, BorderLayout.WEST);
        panel2.add(panel2Two, BorderLayout.CENTER);
        panel2.add(panel2Three, BorderLayout.EAST);

        frame.setVisible(true);
        
        for (JPanel p : allPanels) {
            p.setBackground(Color.darkGray);
        }
        
    }
    
    /**
     * closes the server and all of the connected client handlers
     * @throws IOException
     */
    public void closeServer() throws IOException {
        for (ClientHandler c : clientHandlers) {
            c.closeConnection();
        }
        clientHandlers.clear();

        if (ss != null && !ss.isClosed())
        ss.close();
        System.exit(0);
    }

    /**
     * @param ch - the client handler to be removed
     * 
     * removes the given client handler
     */
    public static void removeClientHandler(ClientHandler ch) {
        Iterator<ClientHandler> iterator = clientHandlers.iterator();
        while (iterator.hasNext()) {
            ClientHandler clientHandler = iterator.next();
            if (clientHandler.equals(ch)) {
                iterator.remove();
                break;
            }
        }
        if(clientHandlers.size() == 1){
            clientHandlers.get(0).broadcastMessage("[1 CLIENT]");
        }
    }
    
    /**
     * updates the GUI based on any new changes (for example
     * a client handler joined/left)
     */
    public void updateGUI() {
        panel2TwoTwo.removeAll();

        for (ClientHandler ch : clientHandlers) {
            ClientInGUI cig = new ClientInGUI(ch.getAddress(), ch.getPort());
            cig.getKickButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        removeSpecificClient(cig.getHostAddress(), cig.getPort());
                    }
                    catch (IOException | InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    
                    updateGUI();
                    
                }
                
            });

            panel2TwoTwo.add(cig);
        }

        if (clientHandlers.size() > 0) {
            chooseHostColor.setVisible(false);
            chooseHostLabel.setVisible(false);
        } else if (clientHandlers.size() == 0) {
            chooseHostColor.setVisible(true);
            chooseHostLabel.setVisible(true);
        }

        panel2TwoTwo.repaint();
        panel2TwoTwo.validate();

        panel2Two.repaint();
        panel2Two.validate();
    } 

    /**
     * updates the chooseHostColor JButton
     */
    public void updateHostColor() {
        if (colorOfHost == 0) {
            chooseHostColor.setBackground(Color.white);
            chooseHostColor.setForeground(Color.white);
        } else {
            chooseHostColor.setBackground(Color.black);
            chooseHostColor.setForeground(Color.black);
        }
        
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException { // make sure that server refuses connection if there are already 2 clients
        if (args.length != 0 && Integer.valueOf(args[0]) != -1) {

            boolean canConnect = true;
            try {
                Server s = new Server(Integer.valueOf(args[0]));
                s.openServer();
            } catch (Exception e) {
                canConnect = false;
            }

            if (!canConnect) System.out.println("Port Already In Use!");
            
        } else {

            boolean canConnect = true;
            try {
                Server s = new Server();
                s.openServer();
            } catch (Exception e) {
                canConnect = false;
            }

            if (!canConnect) System.out.println("Port Already In Use!");
            
        } 
    }   
    /**
    * A class to display the connected ClientHandlers, including the kick
    * button to remove the ClientHandler
    */
    private class ClientInGUI extends JPanel{

        private JButton kickButton; // the kick button
        private String IPa; // the IP Address of the ClientHandler
        private int port; // the port of the ClientHandler

        /**
         * @param ip - the IP Address of the ClientHandler
         * @param port - the port of the ClientHandler
         * 
         * sets up the panel
         */
        public ClientInGUI(String ip, int port) {
            IPa = ip;
            this.port = port;   

            // create gui
            setLayout(new BorderLayout());

            JPanel panel1 = new JPanel();
            panel1.setPreferredSize(new Dimension(110,20));
            JPanel panel2 = new JPanel();
            panel2.setPreferredSize(new Dimension(50, 20 ));

            panel1.setLayout(new GridBagLayout());
            
            JLabel ipAddress = new JLabel(ip);
            ipAddress.setForeground(Color.white);

            panel1.add(ipAddress);

            panel1.setBackground(Color.gray);
            add(panel1, BorderLayout.WEST);

            setBackground(Color.darkGray);

            kickButton = new JButton("<html>Kick</html>");
            kickButton.setPreferredSize(new Dimension(45,20));
            kickButton.setHorizontalAlignment(SwingConstants.CENTER);
            kickButton.setContentAreaFilled(false);
            kickButton.setOpaque(true);
            kickButton.setFocusable(false);
            kickButton.setBorderPainted(false);
            kickButton.setBackground(Color.gray);
            kickButton.setForeground(Color.white);

            panel2.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.black));
            panel2.add(kickButton, BorderLayout.CENTER);

            panel2.setBackground(Color.gray);
            
            add(panel2, BorderLayout.EAST);

        }

        /**
         * @return the kick button
         */
        public JButton getKickButton() {
            return kickButton;
        }

        /**
         * @return the IP address of the ClientHandler
         */
        public String getHostAddress() {
            return IPa;
        }

        /**
         * @return the port of the ClientHandler
         */
        public int getPort() {
            return port;
        }
    
    }
}

