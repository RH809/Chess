import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
/**
 * @author Sanaa, Amine, Ryan
 * 
 * class handling settings menu GUI
 * 
 */

public class SettingsMenu extends JFrame {
    private JFrame frame; // the frame of the settings menu
    private JButton goHome; // the button to return to the main menu
    private JLabel settingsTitle; // the label for the title of the frame
    private JRadioButton mode; // the radio button for the mode (light/dark)
    private ArrayList<JPanel> sPanels; // an ArrayList of all of the panels in the settings menu
    private Map<String, String> settings; // a map for the settings for be saved to the settings.txt file

    /**
     * constructs a new SettingsMenu object
     * initializing the settings and GUI parts
     */
    public SettingsMenu() { 
        settings = new HashMap<>();
        loadSettings();
        initialize();
        saveSettings();
    }

    /**
     * initializes the settings menu GUI components and layout
     */
    public void initialize() {
        int panelNums = 12;
		frame = new JFrame();
        frame.setTitle("Settings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);
		frame.setLayout(new GridLayout(panelNums,1));
		sPanels = new ArrayList<JPanel>();
		
		for (int i = 0; i < panelNums; i++) {
			JPanel panel = new JPanel();
			panel.setBackground(Color.DARK_GRAY);
			sPanels.add(panel);
			frame.add(panel);
		}

		JPanel panel = new JPanel(new GridLayout(10, 1));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		panel.setBackground(Color.DARK_GRAY);

        settingsTitle =  new JLabel("SETTINGS");
		settingsTitle.setFont(new Font("Serif", Font.BOLD, 50));;
		settingsTitle.setForeground(Color.WHITE);
		settingsTitle.setHorizontalTextPosition(JButton.CENTER);
		sPanels.get(1).add(settingsTitle);

        // dark light mode button
        mode = new JRadioButton("LIGHT MODE");
        mode.setForeground(Color.WHITE);
		mode.setVerticalTextPosition(JButton.CENTER);
		mode.setPreferredSize(new Dimension(200, 100));
		sPanels.get(2).add(mode);

        // add return to main menu button
        goHome = new JButton("Return");
        goHome.setPreferredSize(new Dimension(85,40));
        goHome.setContentAreaFilled(false);
        goHome.setOpaque(true);
        goHome.setFocusable(false);
        goHome.setBorderPainted(false);
        goHome.setBackground(Color.gray);
        goHome.setForeground(Color.white);
        goHome.setBorderPainted(false);
        sPanels.get(panelNums-1).add(goHome);
    }

    /**
     * reads settings from text file and stores in the hash map
     */
    private void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    settings.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes settings from hash map to the settings.txt text file
     */
    private void saveSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("settings.txt"))) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return the settings title JLabel
     */
    public JLabel settingsTitle() {
		return settingsTitle;
	}
    
    /**
     * controls visibility of settingsMenu frame
     * 
     * @param bool true --> makes  settingsMenu frame visible, false --> hides frame
     */
    public void setFrameVisible(boolean bool) {
        frame.setVisible(bool);
    }

    /**
     * 
     * @return the mode JRadioButton
     */
    public JRadioButton getModeButton() {
		return mode;
    }

    /**
     * 
     * @return the goHome JButton
     */
    public JButton getReturnButton() {
        return goHome;
    }

    /**
     * sets the colors of the settings menu based on the user selected mode
     * 
     * @param light true for light mode, false for dark mode
     */
    public void setColors(boolean light) {
        if (light) {
			for (int i = 0; i < sPanels.size(); i++) {
                sPanels.get(i).setBackground(Color.LIGHT_GRAY);
            }
            settingsTitle.setForeground(Color.DARK_GRAY);
            mode.setForeground(Color.BLACK);
        }
        
        else {
            for (int i = 0; i < sPanels.size(); i++) {
                sPanels.get(i).setBackground(Color.DARK_GRAY);
            }
            settingsTitle.setForeground(Color.WHITE);
            mode.setForeground(Color.WHITE);

        }
    }


    public static void main(String[] args) {
        new SettingsMenu();
    }
}