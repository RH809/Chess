import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to handle the GUI for the main menu
 */
public class JFrameOne extends JFrame {
	private JFrame frame;
	JButton settings;
	JButton multiplayer;
	JButton play;
	JButton exit;
	JLabel gameTitle;
	private ArrayList<JPanel> allPanels;
	private SettingsMenu settingsMenu;
	private ServerMenu serverMenu;
	private int elapsedTime;
	private boolean alreadyScanned;
	private String platformCurrentlyOn;

	/**
	 * JFrameOne constructor
	 * calls initialize
	 * @throws IOException
	 */
    public JFrameOne() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException { 
        initialize();
    }

	/**
	 * sets up the main menu as well as the serverMenu and settingsMenu
	 * 
	 * @throws IOException
	 */
    public void initialize() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException { 

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        if (UIManager.getLookAndFeel().getName().equals("Windows")) {
            platformCurrentlyOn = "Windows";
        } else {
            platformCurrentlyOn = "Mac";
        }

		// initialize different menus
		serverMenu = new ServerMenu();
		settingsMenu = new SettingsMenu();

		int panelNums = 7;
		frame = new JFrame();
        frame.setTitle("Chess Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);
		frame.setLayout(new GridLayout(panelNums,1));
		allPanels = new ArrayList<JPanel>();

		
		for (int i = 0; i < panelNums; i++) {
			JPanel panel = new JPanel();
			panel.setBackground(Color.DARK_GRAY);
			allPanels.add(panel);
			frame.add(panel);
		}

		JPanel panel = new JPanel(new GridLayout(10, 1));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		panel.setBackground(Color.DARK_GRAY);

		gameTitle = title();
		allPanels.get(1).add(gameTitle);


		play = playButton();
		play.setPreferredSize(new Dimension(200, 100));
		allPanels.get(2).add(play);

		multiplayer = multiplayerButton();
		multiplayer.setPreferredSize(new Dimension(200, 100));
		allPanels.get(3).add(multiplayer);

		settings = settingsButton();
        settings.setPreferredSize(new Dimension(200, 100));
		allPanels.get(4).add(settings);

		exit = exitButton();
		exit.setPreferredSize(new Dimension(200, 100));
		allPanels.get(5).add(exit);
		

		frame.setVisible(true);

		// go to main menu from settings menu
		settingsMenu.getReturnButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsMenu.setFrameVisible(false);
				frame.setVisible(true);
			}
		});

		// switch between dark and light mode from settings
		settingsMenu.getModeButton().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (settingsMenu.getModeButton().isSelected()) {
                    switchToLightMode();
                } else {
                    switchToDarkMode();
                }
            }
		});

		serverMenu.getReturnButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				serverMenu.setFrameVisible(false);
				frame.setVisible(true);
			}
			
		});

		serverMenu.getMakeLocalServerButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{	
				FileWriter fileWriter = null;
				FileWriter fileWriter2 = null;
				try
				{
					fileWriter = new FileWriter("startServer.sh");
					fileWriter2 = new FileWriter("startServer.bat");
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				try
				{
					fileWriter.write("java Server " + serverMenu.getLocalHostPortNumber() /*+ " > debug.txt"*/);
					fileWriter.flush();
					fileWriter2.write("java Server " + serverMenu.getLocalHostPortNumber() + /*" > debug.txt" + */ "\n" + "exit");
					fileWriter2.flush();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				Runtime rn = Runtime.getRuntime();
				try
				{
					if (platformCurrentlyOn.equals("Mac")) {
						rn.exec(new String[]{"sh", "startServer.sh"});
					} else {
						// windows
						rn.exec(new String[] {"cmd", "/c", "start", "startServer.bat"});
					}
					

					JLabel label = serverMenu.getDebugLocalServerLabel();
					elapsedTime = 0;
					alreadyScanned = false;
					Timer timer = new Timer(10, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e)
					{
						elapsedTime += 10;
						if (elapsedTime >= 3000) {
							label.setText("");
						}

						if (!(elapsedTime >= 3000)) {
							// check server is not already opened on this port

							String scannedLine = "";
							if (!alreadyScanned) {
								Scanner scanner = null;
								try
								{
									scanner = new Scanner(new File("debug.txt"));
								}
								catch (FileNotFoundException e1)
								{
									e1.printStackTrace();
								}
								while (scanner.hasNextLine()) {
									scannedLine = scanner.nextLine();
								}
							}
							if (scannedLine.trim().equals("Port Already In Use!")) {
								label.setText(scannedLine.trim());
							} else if (!scannedLine.equals("")) label.setText("Opened Server on Port: " + serverMenu.getLocalHostPortNumber());
						} else {
							// stop timer
							((Timer)e.getSource()).stop();
						}
						
						

						
					}
					
				});
				timer.start();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			
		});

		serverMenu.getConnectToServerButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// switch color based off of host, check if it is host, set color to white
				String ipAddress = serverMenu.getServerIPAddress();
				int portNum = serverMenu.getServerPortNumber();
				String username = serverMenu.getUsername();
				if (username.trim().length() == 0) username = "None";

				// make coordinator connected to server, check server is found 
				
				int color = 0;
				String localIP = "";


				if (username.equals("devtest0")) color = 0;
				else if (username.equals("devtest1")) {
					color = 1;
					ipAddress = localIP;
				}

				// write new startCoordinator based off of username ipaddress and port

				FileWriter fw = null;
				FileWriter fw2 = null;
				try
				{
					fw = new FileWriter("startCoordinator.sh");
					fw2 = new FileWriter("startCoordinator.bat");
					fw.write("java Coordinator " + ipAddress + " " + portNum + " " + username + " " + color/* + " > debug2.txt"*/);
					fw.flush();
					fw2.write("java Coordinator " + ipAddress + " " + portNum + " " + username + " " + color/* + " > debug2.txt"*/ + "\n" + "exit");
					fw2.flush();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
					
				}


				Runtime rn = Runtime.getRuntime();
				// macOS or Windowsscripting
				if (platformCurrentlyOn.equals("Mac")) {
					try
					{
						rn.exec(new String[] {"sh", "startCoordinator.sh"});
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				} else {
					try {
						rn.exec(new String[] {"cmd", "/c", "start", "startCoordinator.bat"});
						System.out.println("worked");
					}
					catch (Exception e3) {
						e3.printStackTrace();
					}
				}

				// change debuglabel
				JLabel label = serverMenu.getDebugLabel();
				elapsedTime = 0;
				Timer timer = null;
				timer = new Timer(10, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)
					{
						elapsedTime += 10;
						if (elapsedTime >= 3000) {
							label.setText("");
						}

						if (!(elapsedTime >= 3000)) {
							try
							{
								
								Scanner scanner = new Scanner(new File("debug2.txt"));
								String line = "";
								while (scanner.hasNextLine()) {
									line = scanner.nextLine();
								}
								
								label.setText(line);
								scanner.close();
							}
							catch (FileNotFoundException e1)
							{
								System.out.println("scan open");
								e1.printStackTrace();
								
							}
						} else {
							// stop timer
							((Timer)e.getSource()).stop();
						}
					}
					
				});
				timer.start();
			}
			
		});

    }

	/**
	 * creates and returns a "Play" button
	 * if pressed, runs the game (single player mode)
	 * @return a JButton representing the play button
	 */
	private JButton playButton() {
		JButton play = new JButton("Play");
		ImageIcon sIcon = new ImageIcon("Images_and_Sounds/playIcon.png");
		Image img = sIcon.getImage();
		Image newImg = img.getScaledInstance(60, 60, DO_NOTHING_ON_CLOSE);
		sIcon = new ImageIcon(newImg);
		play.setIcon(sIcon);
		play.setHorizontalTextPosition(JButton.CENTER);
		play.setVerticalTextPosition(JButton.BOTTOM);
		play.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				new Coordinator("", 2);
			}
			
		});
		return play;
	}

	/**
	 * creates and returns a "Multiplayer" button
	 * if pressed, switches to the serverMenu frame
	 * @return a JButton representing the multiplayer button
	 */
	private JButton multiplayerButton() {
		JButton multiplayerButton = new JButton("Multiplayer");
		ImageIcon sIcon = new ImageIcon("Images_and_Sounds/multiplayerIcon.png");
		Image img = sIcon.getImage();
		Image newImg = img.getScaledInstance(60, 60, DO_NOTHING_ON_CLOSE);
		sIcon = new ImageIcon(newImg);
		multiplayerButton.setIcon(sIcon);
		multiplayerButton.setHorizontalTextPosition(JButton.CENTER);
		multiplayerButton.setVerticalTextPosition(JButton.BOTTOM);
		multiplayerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				serverMenu.setFrameVisible(true);
				frame.setVisible(false);
			}
			
		});

		return multiplayerButton;
	}


	/**
	 * creates and returns a "Settings" button
	 * if pressed, swithces to the settingsMenu frame
	 * @return a JButton representing the settings button
	 */
	private JButton settingsButton() {
		JButton settings = new JButton("Settings");
		ImageIcon sIcon = new ImageIcon("Images_and_Sounds/settingsIcon.png");
		Image img = sIcon.getImage();
		Image newImg = img.getScaledInstance(60, 60, DO_NOTHING_ON_CLOSE);
		sIcon = new ImageIcon(newImg);
		settings.setIcon(sIcon);
		settings.setHorizontalTextPosition(JButton.CENTER);
		settings.setVerticalTextPosition(JButton.BOTTOM);

		settings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				settingsMenu.setFrameVisible(true);
				frame.setVisible(false);
			}
			
		});
		return settings;
	}

	/**
	 * creates and returns an "Exit" button
	 * if pressed, quits the entire application
	 * @return a JButton representing the exit button
	 */
	private JButton exitButton() {
		JButton exit = new JButton("Exit");
		ImageIcon sIcon = new ImageIcon("Images_and_Sounds/exitIcon.png");
		Image img = sIcon.getImage();
		Image newImg = img.getScaledInstance(90, 60, DO_NOTHING_ON_CLOSE);
		sIcon = new ImageIcon(newImg);
		exit.setIcon(sIcon);
		exit.setHorizontalTextPosition(JButton.CENTER);
		exit.setVerticalTextPosition(JButton.BOTTOM);

		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
			}
		});
		return exit;
	}

	/**
	 * creates and returns a JLabel that serves as the game title on the home page
	 * @return a JLabel representing the title
	 */
	public JLabel title() {
		JLabel chessTitle = new JLabel("Welcome to Chess!");
		chessTitle.setFont(new Font("Serif", Font.BOLD, 50));;
		chessTitle.setForeground(Color.WHITE);
		chessTitle.setHorizontalTextPosition(JButton.CENTER);
		return chessTitle;
	}

	/**
	 * switches all ui aspects to a lighter version
	 */
	public void switchToLightMode() {
        for (JPanel panel : allPanels) {
            panel.setBackground(Color.LIGHT_GRAY);
        }
        gameTitle.setForeground(Color.BLACK);
        play.setBackground(Color.LIGHT_GRAY);
        play.setForeground(Color.BLACK);
        multiplayer.setBackground(Color.LIGHT_GRAY);
        multiplayer.setForeground(Color.BLACK);
        settings.setBackground(Color.LIGHT_GRAY);
        settings.setForeground(Color.BLACK);
        exit.setBackground(Color.LIGHT_GRAY);
        exit.setForeground(Color.BLACK);

		settingsMenu.setColors(true);
		serverMenu.setColors(true);
    }

	/**
	 * switches all ui aspects to a darker version
	 */
    public void switchToDarkMode() {
        for (JPanel panel : allPanels) {
            panel.setBackground(Color.DARK_GRAY);
        }
        gameTitle.setForeground(Color.WHITE);
        play.setBackground(Color.DARK_GRAY);
        play.setForeground(Color.BLACK);
        multiplayer.setBackground(Color.DARK_GRAY);
        multiplayer.setForeground(Color.BLACK);
        settings.setBackground(Color.DARK_GRAY);
        settings.setForeground(Color.BLACK);
        exit.setBackground(Color.DARK_GRAY);
        exit.setForeground(Color.BLACK);
		
		settingsMenu.setColors(false);
		serverMenu.setColors(false);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		new JFrameOne();
    }

}