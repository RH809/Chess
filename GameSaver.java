import java.io.File;
import java.io.IOException;
import javax.swing.Timer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import java.awt.event.*;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to handle the saving and loading options. It includes the drop-down menu
 * itself and the selecting of files.
 */

public class GameSaver implements ActionListener{
	private String pathname = System.getProperty("user.dir") + "/Games/"; // path to the games folder where games will be saved to/loaded from
	private JFrame frame; // reference to the frame being used
    private Board board; // reference to the board object being used
	private JMenuBar menuBar; // menuBar that holds the drop-down menu and the status
	private JMenu menu;
	private JLabel status; // a JLabel to show the status of saving/loading operations
	private JMenuItem sound, start; // the menu item for sound
	private Timer timer;

	/**
	 * 
	 * @param frame - frame being used
	 * @param board - board being used
	 * 
	 * initializes the GameSaver and adds the JComponents to the frame
	 */
	public GameSaver(JFrame frame, Board board){
		this.frame = frame;
        this.board = board;
		JLabel label1 = new JLabel("     ");
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		menu = new JMenu("Options");
		JMenuItem save = new JMenuItem("Save");
		start = new JMenuItem("Start play from here");
		JMenuItem load = new JMenuItem("Load");
		JMenuItem reset = new JMenuItem("New game");
		sound = new JMenuItem("Turn off sound");
		status = new JLabel();
		save.setActionCommand("Save");
		start.setActionCommand("Start play");
		sound.setActionCommand("Sound");
		load.setActionCommand("Load");
		reset.setActionCommand("Reset");
		save.addActionListener(this);
		start.addActionListener(this);
		sound.addActionListener(this);
		load.addActionListener(this);
		reset.addActionListener(this);
		menu.add(reset);
		menu.add(load);
		menu.add(save);
		//menu.add(start);
		menu.add(sound);
		menuBar.add(menu);
		menuBar.add(label1);
		menuBar.add(status);

		timer = new Timer(7000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				status.setText("");
			}
			
		});
	}


	/**
	 * returns true or false if the other client wants to load the game
	 * @return true or false
	 */
	public void checkConfirmation(String str) {
		Coordinator.getOutStream().println("****Do you want to load?" + str);
		Coordinator.getOutStream().flush();
	}

	public void changeStatus(String s){
		status.setText(s);
		timer.stop();
		timer.start();
	}

	public void cannotStartPlayHere(){
		menu.remove(start);
	}

	public void canStartPlayHere(){
		if(!menu.getMenuComponents()[3].equals(start)) menu.add(start, 3);
	}

	/**
	 * handles the operations when one of the options in the drop-down menu is chosen
	 * if save is selected - opens the games folder with folder chooser and creates a text file with the file name chosen
	 * if load game is selected - opens the games folder with folder chooser and loads the selected text file using Board's loadGame
	 * if load game state is selected - opens the games folder with folder chooser and loads the selected text file using Board's loadGameState
	 * if start play from here is selected - calls the Notations of Board's startPlayFromHere
	 * updates the status JLabel whenever something is selected that changes the state of the board
	 * if sound is selected - toggles sound
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Save")){ // save 
			JFileChooser fileChooser = new JFileChooser(pathname);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = fileChooser.showSaveDialog(frame);
			if (result == JFileChooser.CANCEL_OPTION)
				return;

			File file = fileChooser.getSelectedFile();
			if (file != null)
			{
				try
				{
					String name = file.getCanonicalPath();
					if(!name.endsWith(".txt")) file = new File(name + ".txt");
                    board.saveGame(file);
					status.setText("Game saved to " + file.getName());
					timer.stop();
					timer.start();
					board.getSoundPlayer().playNotify();
					frame.repaint();
				}
				catch (IOException ex)
				{
					System.out.println("*** Can't create file ***");
					return;
				}
			}
		}
		else if(e.getActionCommand().equals("Load")){
			JFileChooser fileChooser = new JFileChooser(pathname);
			FileFilter filter = new FileFilter(){
				public boolean accept(File pathname) {
					String path = pathname.getAbsolutePath();
					return path.endsWith(".txt");
				}

				@Override
				public String getDescription() {
					return "Text File";
				}
			};
			fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
			fileChooser.setFileFilter(filter);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = fileChooser.showOpenDialog(frame);
			if(result == JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				if (!board.isMultiplayer()){ // load on their own (singleplayer)
					if(board.load(file)){
						status.setText("Loaded game from " + file.getName());
					}
					else{
						status.setText("Load failed - invalid file");
					}
					timer.stop();
					timer.start();
				}
				else if(Coordinator.getOutStream() == null || Coordinator.numConnected() == 1){ // cannot load until other player joins
					status.setText("Cannot load yet - waiting for opponent to join");
					timer.stop();
					timer.start();
				}
				else{
					checkConfirmation("Load game?");
					SwingWorker worker = new SwingWorker<String, String>() {

						@Override
						protected String doInBackground() throws Exception {
							while (Coordinator.getAnswerConfirmation() == null) {
								Thread.sleep(100);
							}
							return null;
						}
			
						protected void done(){
							System.out.println("done");
							if (Coordinator.getAnswerConfirmation().equals("*No")) {
								status.setText("Request denied");
								timer.stop();
								timer.start();
								System.out.println("false - denied");
							}
							else if (Coordinator.getAnswerConfirmation().equals("*Cancel")){
								status.setText("Cancelled request");
								timer.stop();
								timer.start();
								System.out.println("false - cancelled");
							}
							else{
								System.out.println("true - accepted");
								if(board.load(file)){
									status.setText("Request accepted - Loaded game from " + file.getName());
								}
								else{
									status.setText("Load failed - invalid file");
								}
								timer.stop();
								timer.start();
								
							}
							Coordinator.setAnswerFromClient("null");
							
						}
						
					};
					worker.execute();
				}
			}
		}
		else if(e.getActionCommand().equals("Start play")){ // start play from here
			if (!board.isMultiplayer()){ // load on their own (singleplayer)
				board.getNotations().startPlayHere(false);
				status.setText("Started play from previous move");
				timer.stop();
				timer.start();
			}
			else if(Coordinator.getOutStream() == null || Coordinator.numConnected() == 1){ // cannot load until other player joins
				status.setText("Cannot start play from previous move yet - waiting for opponent to join");
				timer.stop();
				timer.start();
			}
			else{
				checkConfirmation("Start play from previous move?");
				SwingWorker worker = new SwingWorker<String, String>() {

					@Override
					protected String doInBackground() throws Exception {
						while (Coordinator.getAnswerConfirmation() == null) {
							Thread.sleep(100);
						}
						return null;
					}
		
					protected void done(){
						if (Coordinator.getAnswerConfirmation().equals("*No")) {
							status.setText("Request denied");
							timer.stop();
							timer.start();
							System.out.println("false - denied");
						}
						else if (Coordinator.getAnswerConfirmation().equals("*Cancel")){
							status.setText("Cancelled request");
							timer.stop();
							timer.start();
							System.out.println("false - cancelled");
						}
						else{
							System.out.println("true - accepted");
							board.getNotations().startPlayHere(true);
							status.setText("Request accepted - Started play from previous move");
							timer.stop();
							timer.start();
							
						}
						Coordinator.setAnswerFromClient("null");
						
					}
					
				};
				worker.execute();
			}
		}
		else if(e.getActionCommand().equals("Sound")){ // toggle sound
			if(sound.getText().equals("Turn off sound")){
				board.getSoundPlayer().turnOff();
				sound.setText("Turn on sound");
				frame.repaint();
			}
			else{
				board.getSoundPlayer().turnOn();
				sound.setText("Turn off sound");
				frame.repaint();
			}
		}
		else if(e.getActionCommand().equals("Reset")){
			if (!board.isMultiplayer()){ // load on their own (singleplayer)
				board.resetBoard(false);
				status.setText("Started new game");
				timer.stop();
				timer.start();
			}
			else if(Coordinator.getOutStream() == null || Coordinator.numConnected() == 1){ // cannot load until other player joins
				status.setText("Cannot reset board yet - waiting for opponent to join");
				timer.stop();
				timer.start();
			}
			else{
				checkConfirmation("Start new game?");
				SwingWorker worker = new SwingWorker<String, String>() {

					@Override
					protected String doInBackground() throws Exception {
						while (Coordinator.getAnswerConfirmation() == null) {
							Thread.sleep(100);
						}
						return null;
					}
		
					protected void done(){
						if (Coordinator.getAnswerConfirmation().equals("*No")) {
							status.setText("Request denied");
							timer.stop();
							timer.start();
							System.out.println("false - denied");
						}
						else if (Coordinator.getAnswerConfirmation().equals("*Cancel")){
							status.setText("Cancelled request");
							timer.stop();
							timer.start();
							System.out.println("false - cancelled");
						}
						else{
							System.out.println("true - accepted");
							board.resetBoard(true);
							status.setText("Request accepted - Started new game");
							timer.stop();
							timer.start();
							
						}
						Coordinator.setAnswerFromClient("null");
						
					}
					
				};
				worker.execute();
			}
		}
	}
}
