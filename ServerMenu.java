import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to handle the GUI for the server menu, which is the menu
 * where the player opens server and clients
 */
public class ServerMenu {

    private JFrame frame; // the frame of the menu
    private ArrayList<JPanel> allPanels; // the ArrayList of all of the panels in the frame
    private JButton returnButton; // the return button
    private JTextArea usernameTextArea; // the text area for inputting username
    private JTextArea portTextArea; // the text area for inputting the port for server creation
    private JButton makeLocalServerButton; // the button to make the local server 
    private JTextArea servertextArea; // the text area for inputting the server
    private JTextArea connectToServerPortTextArea; // the text area for inputting the port for server connection
    private JButton connectToServerButton; // the button to connect the server
    private JLabel debugLabel; // the label to display debug text for the client in the menu
    private JLabel debugLabelLocalServer; // the label to display debug text for the server in the menu
    private static String platformCurrentlyOn; // the platform that is being played on (Windows or Mac)

    /*
     * below are labels to show text on the menu
     */
    private JLabel ipAddress; 
    private JLabel usernameLabel; 
    private JLabel localServerLabel;
    private JLabel portLabel;
    private JLabel outsideServerLabel;
    private JLabel serverLabel;
    private JLabel portLabel2;

    /**
     * sets up the GUI for the server menu
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     */
    public ServerMenu() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        if (UIManager.getLookAndFeel().getName().equals("Windows")) {
            platformCurrentlyOn = "Windows";
        } else {
            platformCurrentlyOn = "Mac";
        }

        int numPanels = 16;

        frame = new JFrame();
        frame.setLayout(new GridLayout(numPanels,1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);
        frame.setBackground(Color.decode("#323437"));

        allPanels = new ArrayList<JPanel>();

        for (int i = 0; i < numPanels; i++) {
            JPanel panel = new JPanel();
            allPanels.add(panel);
            frame.add(panel);
            panel.setBackground(Color.decode("#323437"));
        }

        //text label with IP Address
        ipAddress = new JLabel("Local IP Address: ");
        ipAddress.setForeground(Color.white);
        
        String ip = getLocalIPAddress(platformCurrentlyOn);
        ipAddress.setText(ipAddress.getText() + ip.substring(ip.indexOf("/")+1));
        allPanels.get(1).add(ipAddress);

        // connect to server and have username
        allPanels.get(2).setLayout(new GridLayout(1,2));
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        allPanels.add(panel1);
        allPanels.add(panel2);

        allPanels.get(2).add(panel1);
        allPanels.get(2).add(panel2);

        panel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        usernameLabel = new JLabel("Username: ");
        usernameLabel.setForeground(Color.white);
        panel1.add(usernameLabel);

        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        usernameTextArea = new JTextArea();
        usernameTextArea.setPreferredSize(new Dimension(100,20));
        panel2.add(usernameTextArea);

        // add local server creation JLabel
        localServerLabel = new JLabel("Local Server Creation");
        localServerLabel.setForeground(Color.white);
        allPanels.get(4).add(localServerLabel);

        // make local server
        int panelNum2 = 5;
        allPanels.get(panelNum2).setLayout(new GridLayout(1,2));
        JPanel panel5 = new JPanel();
        JPanel panel6 = new JPanel();
        allPanels.add(panel5);
        allPanels.add(panel6);

        allPanels.get(panelNum2).add(panel5);
        allPanels.get(panelNum2).add(panel6);

        panel5.setLayout(new FlowLayout(FlowLayout.RIGHT));
        portLabel = new JLabel("Port: ");
        portLabel.setForeground(Color.white);
        panel5.add(portLabel);

        portTextArea = new JTextArea();
        portTextArea.setPreferredSize(new Dimension(50,20));
        panel5.add(portTextArea);

        panel6.setLayout(new FlowLayout(FlowLayout.LEFT));
        makeLocalServerButton = new JButton("Create Server");
        makeLocalServerButton.setPreferredSize(new Dimension(130,30));
        makeLocalServerButton.setContentAreaFilled(false);
        makeLocalServerButton.setOpaque(true);
        makeLocalServerButton.setFocusable(false);
        makeLocalServerButton.setBackground(Color.gray);
        makeLocalServerButton.setForeground(Color.white);
        makeLocalServerButton.setBorderPainted(false);
        
        panel6.add(makeLocalServerButton);

        // add debuglabel here
        debugLabelLocalServer = new JLabel();
        debugLabelLocalServer.setForeground(Color.white);

        allPanels.get(panelNum2+1).add(debugLabelLocalServer);

        // add server connection label
        outsideServerLabel = new JLabel("Connect to Server");
        outsideServerLabel.setForeground(Color.white);
        allPanels.get(8).add(outsideServerLabel);

        // add server connection
        
        int panelNum3 = 9;
        allPanels.get(panelNum3).setLayout(new GridLayout(1,2));
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        allPanels.add(panel3);
        allPanels.add(panel4);

        allPanels.get(panelNum3).add(panel3);
        allPanels.get(panelNum3).add(panel4);

        panel3.setLayout(new FlowLayout(FlowLayout.RIGHT));
        serverLabel = new JLabel("Server IP Address: ");
        serverLabel.setForeground(Color.white);
        panel3.add(serverLabel);

        panel4.setLayout(new FlowLayout(FlowLayout.LEFT));
        servertextArea = new JTextArea("localhost");
        servertextArea.setPreferredSize(new Dimension(120,20));
        panel4.add(servertextArea);

        connectToServerButton = new JButton("Connect");
        connectToServerButton.setPreferredSize(new Dimension(100,30));
        connectToServerButton.setContentAreaFilled(false);
        connectToServerButton.setOpaque(true);
        connectToServerButton.setFocusable(false);
        connectToServerButton.setBackground(Color.gray);
        connectToServerButton.setForeground(Color.white);
        connectToServerButton.setBorderPainted(false);

        // add port for connecting to outside server

        int panelNum4 = 10;

        allPanels.get(panelNum4).setLayout(new GridLayout(1,2));
        JPanel panel7 = new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JPanel panel8 = new JPanel();
        panel8.setLayout(new FlowLayout(FlowLayout.LEFT));
        allPanels.add(panel7);
        allPanels.add(panel8);

        allPanels.get(panelNum4).add(panel7);
        allPanels.get(panelNum4).add(panel8);

        portLabel2 = new JLabel("Port: ");
        portLabel2.setForeground(Color.white);

        panel7.add(portLabel2);

        connectToServerPortTextArea = new JTextArea();
        connectToServerPortTextArea.setPreferredSize(new Dimension(50,20));
        panel8.add(connectToServerPortTextArea);

        // add connect to server button
        allPanels.get(panelNum4 + 1).add(connectToServerButton);

        // add debug label
        debugLabel = new JLabel();
        
        debugLabel.setForeground(Color.white);

        allPanels.get(panelNum4+2).add(debugLabel);

        // add return to main menu button
        returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(85,40));
        returnButton.setContentAreaFilled(false);
        returnButton.setOpaque(true);
        returnButton.setFocusable(false);
        returnButton.setBorderPainted(false);
        returnButton.setBackground(Color.gray);
        returnButton.setForeground(Color.white);
        returnButton.setBorderPainted(false);

        allPanels.get(numPanels -1).add(returnButton);
        

        for (JPanel panel : allPanels) {
            panel.setBackground(Color.decode("#323437"));
        }

    }

    /**
     * @param str - string to check if it is an integer
     * @return whether or not the string is an integer
     * 
     * a helper method to determine whether or not a string is an integer
     */
    private boolean isInteger(String str) {
        boolean isInteger = true;
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            isInteger = false;
        }
        return isInteger;
    }

    /**
     * @return the port number entered to make local server (default is 1234)
     */
    public int getLocalHostPortNumber() {
        String str = portTextArea.getText();
        
        if (isInteger(str)) return Integer.valueOf(str);
        else return 1234; // default port 
    }


    /**
     * @return the port number entered to join the server (default is 1234)
     */
    public int getServerPortNumber() {
        String str = connectToServerPortTextArea.getText();

        if (isInteger(str)) return Integer.valueOf(str);
        else return 1234; // default port 
    }

     /**
     * @return the ip address entered to join the server
     */
    public String getServerIPAddress() {
        return servertextArea.getText();
    }

    /**
     * @return the label for debugging for the client
     */
    public JLabel getDebugLabel() {
        return debugLabel;
    }

    /**
     * 
     * @param platform - platform being used
     * @return the local ip address
     * @throws IOException
     * 
     * gets the local ip address from the ipinfo.txt (different methods
     * based on the platform being used)
     */
    public static String getLocalIPAddress(String platform) throws IOException {
        // run script
        Runtime rn = Runtime.getRuntime();

        if (platform.equals("Mac")) {
            rn.exec(new String[]{"sh", "getIP.sh"});
        } else {
            rn.exec(new String[] {"cmd", "/c", "start", "getIP.bat"});
        }
        
        if (platform.equals("Mac")) {
            Scanner scanner = new Scanner(new File("ipinfo.txt"));
            while (scanner.hasNextLine()) {
                String scannedLine = scanner.nextLine();
                if (scannedLine.trim().equals("Addresses : <array> {")) {
                    break;
                }
            }
            String scannedLine = scanner.nextLine();
            scanner.close();
            return scannedLine.split(":")[1].trim();

        } else {
            Scanner scanner = new Scanner(new File("ipinfo.txt"));
            while (scanner.hasNextLine()) {
                String scannedLine = scanner.nextLine();
                if (scannedLine.indexOf("Link-local IPv6 Address") != -1) {
                    break;
                }
            }
            String scannedLine = scanner.nextLine();
            scanner.close();
            return scannedLine.trim().split(":")[1];
        }
        
        
    }

    /**
     * @param bool - boolean for setting the visibility
     * 
     * sets frame to be visible/not visible based on the boolean
     */
    public void setFrameVisible(boolean bool) {
        frame.setVisible(bool);
    }

    /**
     * @return the label for debugging for the server
     */
    public JLabel getDebugLocalServerLabel() {
        return debugLabelLocalServer;
    }

    /**
     * @return the entered username
     */
    public String getUsername() {
        return usernameTextArea.getText();
    }

    /**
     * @return the return button
     */
    public JButton getReturnButton() {
        return returnButton;
    }

    /**
     * @return the connect to server button
     */
    public JButton getConnectToServerButton() {
        return connectToServerButton;
    }

    /**
     * @return the make local server button
     */
    public JButton getMakeLocalServerButton() {
        return makeLocalServerButton;
    }

    /**
     * sets the colors of the server menu based on the user selected mode
     * 
     * @param light true for light mode, false for dark mode
     */
    public void setColors(boolean light) {
        if (light) {
			for (int i = 0; i < allPanels.size(); i++) {
                allPanels.get(i).setBackground(Color.LIGHT_GRAY);
            }
            ipAddress.setForeground(Color.black);;
            usernameLabel.setForeground(Color.black);;
            localServerLabel.setForeground(Color.black);;
            portLabel.setForeground(Color.black);
            outsideServerLabel.setForeground(Color.black);
            serverLabel.setForeground(Color.black);
            portLabel2.setForeground(Color.black);
        }
        
        else {
            for (int i = 0; i < allPanels.size(); i++) {
                allPanels.get(i).setBackground(Color.DARK_GRAY);
            }
            ipAddress.setForeground(Color.white);
            usernameLabel.setForeground(Color.white);
            localServerLabel.setForeground(Color.white);
            portLabel.setForeground(Color.white);
            outsideServerLabel.setForeground(Color.white);
            serverLabel.setForeground(Color.white);
            portLabel2.setForeground(Color.white);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        ServerMenu sm = new ServerMenu();
        sm.setFrameVisible(true);

    }
}