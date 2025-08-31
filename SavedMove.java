import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to represent a single game state for one move
 */
public class SavedMove implements ActionListener{

	/*
	 * Saving moves - save position of piece before and after move
	 * Call move method for each one (should create notations and saved moves for all of them)
	 * If reached the end of the game, all moves should be considered "Previous moves"
	 * Other Features:
	 * Start playing from move (delete all moves after in notations and saved move, set board to that current move)
	 * Continue game (default - last move will be available to play from, not a "previous move")
	 */

	private Notations notations; // the Notations class that this belongs to
	private int num, color; // the move number and which color made the move
	private Spaces[][] savedBoard; // the board for this game state
	private int oldR, oldC, newR, newC; // the starting and ending positions of the piece that moved
	private String promote = ""; // the piece that was promoted to in the case of a promotion
	private int winner; // who, if anyone, won the game at this move
	private String sound; // sound file to play
	
	/**
	 * 
	 * @param num - the move number
	 * @param notations - the Notations class that this belongs to
	 * @param oldR - old row of the piece that moved
	 * @param oldC - old column of the piece that moved
	 * @param newR - new row of the piece that moved
	 * @param newC - new column of the piece that moved
	 * @param promote - piece that was promoted to
	 * 
	 * initailizes this saved move
	 */
	public SavedMove(int num, Notations notations, int oldR, int oldC, int newR, int newC, String promote, String sound) {
		this.num = num;
		this.notations = notations;
		savedBoard = new Spaces[Board.BOARDSIZE][Board.BOARDSIZE];
		color = Coordinator.turn;
		this.oldR = oldR;
		this.newR = newR;
		this.oldC = oldC;
		this.newC = newC;
		this.promote = promote;
		winner = Coordinator.winner;
		this.sound = sound;
	}
	
	/**
	 * @param board - new board to set the board to
	 * 
	 * updates/initailizes the board of this game state
	 * also updates pieces arraylist based on the new board
	 */
	public void setBoard(Spaces[][] board) {
		savedBoard = board;
	}
	
	/**
	 * when the JButton that corresponds to this move it clicked on,
	 * set this move to selected to update the display to view this move
	 */
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o instanceof JButton) {
			notations.setSelected(num);
		}
	}
	
	/**
	 * @return the board of this game state
	 */
	public Spaces[][] getBoard(){
		return savedBoard;
	}

	/**
	 * @return the color that made the move
	 */
	public int getColor(){
		return color;
	}

	/**
	 * @return the winner, if anybody, at this move
	 */
	public int getWinner(){
		return winner;
	}

	/**
	 * @return the sound of this move
	 */
	public String getSound(){
		return sound;
	}
	
	/**
	 * @return whether or not this game state is equal to another game state
	 * 
	 * checks every space to make sure that the pieces match (same id) and
	 * are in the same condition (castle, enpassant, etc.)
	 */
	public boolean equals(Object o) {
		if(!(o instanceof SavedMove)) return false;
		SavedMove other = (SavedMove)o;
		if(other.color != this.color) return false;
		for(int i = 0; i < Board.BOARDSIZE; i++) {
			for(int j = 0; j < Board.BOARDSIZE; j++) {
				if(!other.savedBoard[i][j].equals(this.savedBoard[i][j])) {
					return false;
				}
				if(savedBoard[i][j].getPiece() instanceof King || savedBoard[i][j].getPiece() instanceof Pawn) {
					other.savedBoard[i][j].getPiece().setLegalMoves();
					savedBoard[i][j].getPiece().setLegalMoves();
					if(savedBoard[i][j].getPiece().getLegalMoves().size() != other.savedBoard[i][j].getPiece().getLegalMoves().size()) return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return the string that represents the move (for saving and loading games)
	 */
	public String getString(){
		if(promote != "") return oldR + " " + oldC + " " + newR + " " + newC + " " + promote;
		return oldR + " " + oldC + " " + newR + " " + newC;
	}

}
