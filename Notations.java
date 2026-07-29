
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to handle the creation and displaying of notations
 * including selecting previous move notations
 */
public class Notations {
	private JFrame frame; // the frame to draw in
	private JPanel panel; // the panel for the notations
	private JScrollPane scrollPane; // the scroll pane to allow for scrolling when there are too many notations to display all at the same time
	private JTextArea textArea; // the text area of for the scroll pane that will hold the buttons and labels for the notations
	private ArrayList<JButton> buttons; // the ArrayList for all of the JButtons of the moves
	private ArrayList<JLabel> numbers; // the ArrayList for all of the JLabels of the move numbers
	private JLabel white, black; // the JLabel for "white" and "black" at the top of the notations
	private ArrayList<SavedMove> savedMoves; // the ArrayList for all of the saved moves
	private SavedMove startingPos; // the starting position before any move was made
	private int row = 0, selected = -1, num = 0, viewRow = 0; // row - the number of rows of moves, selected - the selected move, num - the number of moves, viewRow - the row being viewed
	private Board chessBoard; // the Board object connected to this Notations object
	private SwitchMove switchMove; // the SwitchMove object of this Notations object to handle the switching of moves from arrow keys
	private static final int VIEW_WIDTH = 325, VIEW_HEIGHT = 700, SPACE_SIZE = 15, BUTTON_HEIGHT = 20, BUTTON_WIDTH = 130, NUM_BUTTONS = 34, NUM_ROWS = 35; // constants for the graphics
	private boolean save, justMadeMove; // save - whether or not to save the moves to the file, justMadeMove - whether or not a move was just made (to tell whether or not to make sound when doing setSelected())
	private FileWriter writer; // the FileWriter for the file to write to
	private String newGameState = "";
	private ChatFocusListener cfl;
	
	/**
	 * 
	 * @param frame - JFrame to draw in
	 * 
	 * initializes all of the fields and creates the JComponents needed
	 */
	public Notations(JFrame frame) {
		this.frame = frame;
		switchMove = new SwitchMove(this);
		
		panel = new JPanel();
		frame.getLayeredPane().add(panel);
		frame.getLayeredPane().addKeyListener(switchMove);
		panel.setBackground(Color.WHITE);
		panel.setBounds(950, 30, VIEW_WIDTH, VIEW_HEIGHT);
		panel.setAutoscrolls(true);
		panel.setLayout(null);
		panel.addKeyListener(switchMove);
		
		
		scrollPane = new JScrollPane();
		panel.add(scrollPane);
		scrollPane.setBounds(0, 20, VIEW_WIDTH, VIEW_HEIGHT);
		
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Serif", 20, SPACE_SIZE));
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		
		textArea.setRows(NUM_ROWS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textArea.setAutoscrolls(false);
		
		white = new JLabel("White", JLabel.CENTER);
		white.setBounds(30, 0, BUTTON_WIDTH, 20);
		panel.add(white);
		
		black = new JLabel("Black", JLabel.CENTER);
		black.setBounds(30 + BUTTON_WIDTH, 0, BUTTON_WIDTH, 20);
		panel.add(black);
		
		buttons = new ArrayList<JButton>();
		numbers = new ArrayList<JLabel>();
		savedMoves = new ArrayList<SavedMove>();

		startingPos = new SavedMove(-1, this, -1, -1, -1, -1, "", "");
	}
	
	/**
	 * 
	 * @param chessBoard - Board object to set
	 * 
	 * sets this Notations object's Board object
	 * (needed because the Board object is created using the Notations, so 
	 * Notations cannot have Board in the constructor)
	 */
	public void setChessBoard(Board chessBoard) { this.chessBoard = chessBoard; }

	/**
	 * saves the starting position of the board to the startingPos field
	 */
	public void saveStartingPos() {
		chessBoard.saveBoard(startingPos);
	}

	/**
	 * @return the starting position
	 */
	public SavedMove getStartingPos() { return startingPos; }

	public void setNewGameState(String newGameState){ this.newGameState = newGameState; }
	
	/**
	 * 
	 * @param text - text of the notation
	 * @param color - color of the move
	 * @param oldR - the old row of the moving piece
	 * @param oldC - the old column of the moving piece
	 * @param newR - the new row of the moving piece
	 * @param newC - the new column of the moving piece
	 * @param promote - the string of the type of piece promoted to
	 * @param sound - the sound of the move
	 * 
	 * creates a new SavedMove object based on the information above
	 * creates the JButton for the move and the JLabel for the move number (if needed)
	 * also writes to the file if the game is being saved
	 */
	public void newMove(String text, int color, int oldR, int oldC, int newR, int newC, String promote, String sound) {
		int x;
		if(color == 0) {
			x = 30;
			row ++;
			if(row > NUM_BUTTONS) textArea.setRows(textArea.getRows() + 1);
			JLabel label = new JLabel(((Integer)((num + 2) / 2)).toString());
			label.setBounds(10, (row - 1) * BUTTON_HEIGHT, 20,BUTTON_HEIGHT);
			textArea.add(label);
			numbers.add(label);
			if(cfl != null) {
				label.addMouseListener(cfl);
			}
		}
		else {
			x = 30 + BUTTON_WIDTH;
		}
		JButton button = new JButton(text);
		button.setBounds(x, (row - 1) * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		button.setBackground(Color.WHITE);
		button.setBorderPainted(true);
		button.setFocusable(false);
		
		SavedMove savedMove = new SavedMove(num, this, oldR, oldC, newR, newC, promote, sound);
		chessBoard.saveBoard(savedMove);
		
		num ++;
		
		button.addActionListener(savedMove);
		if(cfl != null) {
			button.addMouseListener(cfl);
		}
		
		textArea.add(button);
		buttons.add(button);
		savedMoves.add(savedMove);
		
		if(save){
			try{
				writer.write(savedMove.getString() + "\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No file found");
				return;
			}
		}

		if(buttons.size() > 1) buttons.get(buttons.size() - 2).setBorderPainted(false);
		justMadeMove = true;
		setSelected(buttons.size() - 1);
		justMadeMove = false;
		
		frame.repaint();
	}
	
	/**
	 * 
	 * @param selected - move to select
	 * 
	 * sets the selected move and deselects the previous selected move
	 * also tells the board whether or not it is the most recent move
	 * if it is a previous move, also plays the sound of that move
	 */
	public void setSelected(int selected) {
		if(selected == this.selected) return;
		if(this.selected != -1) buttons.get(this.selected).setBorderPainted(false);
		this.selected = selected;
		if(selected == -1){
			chessBoard.onPreviousMove(startingPos);
			chessBoard.updateBoard();
		}
		else{
			buttons.get(selected).setBorderPainted(true);
			if(selected != buttons.size() - 1) {
				chessBoard.onPreviousMove(savedMoves.get(selected));
				chessBoard.updateBoard();
			}
			else {
				chessBoard.currentMove();
				chessBoard.updateBoard();
			}
			if(!justMadeMove) chessBoard.getSoundPlayer().playSound(savedMoves.get(selected).getSound());
		}
		setView();
		if(selected == savedMoves.size() - 1 || savedMoves.size() == 0) chessBoard.getCoordinator().getGameSaver().cannotStartPlayHere();
		else chessBoard.getCoordinator().getGameSaver().canStartPlayHere();
	}
	
	/**
	 * updates the view rectangle of the scroll pane so that the selected row is in view
	 */
	private void setView() {
		if(row <= NUM_BUTTONS || selected == -1) {
			viewRow = 0;
		}
		else if(selected == buttons.size() - 1) {
			viewRow = row;
		}
		else {
			viewRow = (selected / 2) - 16;
		}
		textArea.scrollRectToVisible(new Rectangle(0, viewRow * BUTTON_HEIGHT, VIEW_WIDTH, VIEW_HEIGHT));
		frame.repaint();
	}
	
	/**
	 * @return whether or not the game ended by repetition
	 * 
	 * checks for repetition (three exact same game states)
	 */
	public boolean checkRepetition() {
		int count = 0;
		if(savedMoves.size() < 3) return false;
		SavedMove lastMove = savedMoves.get(savedMoves.size() - 1);
		for(int i = 0; i < savedMoves.size() - 1; i++){
			if(savedMoves.get(i).equals(lastMove)) {
				count ++;
				if(count == 2) return true;
			}
		}
		return false;
		
	}
	/**
	 * @return the selected move
	 */
	public int getSelected() { return selected; }
	/**
	 * @return the index of the last move in the savedMoves ArrayList
	 */
	public int getSize() { return savedMoves.size() - 1; }
	/**
	 * @return the SwitchMove object of this Notations object
	 */
	public SwitchMove getSwitchMove() { return switchMove; }

	/**
	 * @param file - file to save to
	 * 
	 * initializes the file writer, writes all of the moves made before to the file, and 
	 * sets save to true so that all following moves will be saved too
	 */
	public void saveGame(File file){
		try {
			writer = new FileWriter(file);
			if(!newGameState.equals("")){
				writer.write("new game state\n");
				writer.write(newGameState);
				writer.write("end game state\n");
			}
			else{
				writer.write("moves only\n");
			}
			for(int i = 0; i < savedMoves.size(); i++){
				writer.write(savedMoves.get(i).getString() + "\n");
			}
			save = true;
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No file found");
			return;
		}
	}

	/**
	 * resets this Notations object by deleting all saved moves and the corresponding
	 * JButtons and JLabels, also replaces the textArea
	 */
	public void reset(){
		buttons.clear();
		numbers.clear();
		savedMoves.clear();
		row = 0;
		num = 0;
		viewRow = 0;
		selected = -1;
		chessBoard.currentMove();

		textArea = new JTextArea();
		textArea.setFont(new Font("San Serif", 20, SPACE_SIZE));
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setFocusable(false);

		textArea.setRows(NUM_ROWS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textArea.setAutoscrolls(false);

		save = false;
		writer = null;
		setView();
		saveStartingPos();
		newGameState = "";
	}

	/**
	 * @param send - whether or not to send the message to the other client (prevents infintie loops)
	 * 
	 * @return whether or not it successfully started play from a previous move
	 * 
	 * starts play from the selected move if it is not the most recent move
	 * deletes all the saved moves and corresponding JButtons and JLabels after the move
	 */
	public void startPlayHere(boolean send){
		// check game end for imported game state
		chessBoard.deselect();
		if(selected == -1){
			save = false;
			writer = null;
			savedMoves.clear();
			while(!buttons.isEmpty()) textArea.remove(buttons.remove(0));
			while(!numbers.isEmpty()) textArea.remove(numbers.remove(0));
			textArea.setRows(NUM_ROWS);
			row = 0;
			num = 0;
			setView();

			Coordinator.turn = startingPos.getColor();
			Coordinator.winner = startingPos.getWinner();
			chessBoard.setBoard(startingPos.getBoard());
		}
		else{
			save = false;
			writer = null;
			int index = savedMoves.size() - 1;
			while(index > selected){
				savedMoves.remove(index);
				textArea.remove(buttons.remove(index));
				if(index % 2 == 0) textArea.remove(numbers.remove(numbers.size() - 1));
				index--;
			}
			textArea.setRows(NUM_ROWS  + ((selected / 2 > NUM_ROWS) ? selected / 2 - NUM_ROWS : 0));
			row = selected / 2 + 1;
			num = selected + 1;
			setView();
			Coordinator.turn = 1 - (selected % 2);
			Coordinator.winner = -2;
			chessBoard.setBoard(savedMoves.get(selected).getBoard());
		}
		if(send) chessBoard.sendPlayFromHere(selected);
		chessBoard.getSoundPlayer().playNotify();
	}
	
	public void addCFL(ChatFocusListener cfl){
		panel.addMouseListener(cfl);
		textArea.addMouseListener(cfl);
		this.cfl = cfl;
	}
}
