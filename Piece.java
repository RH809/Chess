import java.util.ArrayList;
import javax.swing.*;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A parent class of all the different types of pieces. This class will 
 * define the methods for all of the pieces, and the individual piece
 * classes will override the methods based on the rules involving the piece.
 */
public class Piece { // not abstract because will be instantiated as a "placeholder"

	public static int nextId = 1; // id of next piece to be created (creates a unique ID for each new piece)
	protected Coordinates coords; // coordinates of the piece
	protected ArrayList<Coordinates> legalMoves, checkMoves; // legalMoves - array list of all places the piece can move,
															 // checkMoves - array list of all the spots the piece is attacking that can check the king
	protected ImageIcon wImage, bImage; // white and black images of the piece
	protected int color, id; //color - color of the piece (0 = white, 1 = black)
							 // id - unique id of the piece
	protected ChessBoard chessBoard; // reference to the board that the piece is in
	protected Board board;
	
	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * 
	 * initializes a brand new piece with a unique id
	 */
	public Piece(int r, int c, int color, ChessBoard chessBoard) {
		coords = new Coordinates(r, c);
		legalMoves = new ArrayList<Coordinates>();
		checkMoves = new ArrayList<Coordinates>();
		this.chessBoard = chessBoard;
		this.board = chessBoard.getMyBoard();
		this.color = color;
		this.id = nextId++;
		
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * 
	 * creates copy of another piece using the same id
	 */
	public Piece(int r, int c, int color, ChessBoard chessBoard, int id){
		coords = new Coordinates(r, c);
		legalMoves = new ArrayList<Coordinates>();
		checkMoves = new ArrayList<Coordinates>();
		this.chessBoard = chessBoard;
		this.board = chessBoard.getMyBoard();
		this.color = color;
		this.id = id;
	}

	/**
	 * 
	 * @param r - row of space to check
	 * @param c - column of space to check
	 * @param board - reference to board that pieces are in
	 * @return whether or not the space is being checked/attacked by this piece
	 * 
	 * sets the check moves of the piece and then checks if the space is one of those moves
	 */
	public boolean checkCheck(int r, int c, Spaces[][] board) {
		setCheckMoves(board);
		return checkMoves.contains(new Coordinates(r, c));
	}
	
	/**
	 * method to be overriden by child classes to set their legal moves
	 */
	public void setLegalMoves(){

	}
	
	/**
	 * @return whether or not this piece has any legal moves
	 */
	public boolean hasLegalMoves() { return !legalMoves.isEmpty(); }

	/**
	 * 
	 * @param board - reference to board that the piece is in
	 * 
	 * method to be overriden by child classes to set their check moves
	 */
	public void setCheckMoves(Spaces[][] board) {
		
	}

	/**
	 * 
	 * @param clickCoords - coordinates that are being clicked on for potential move
	 * @return whether or not the coordinates being clicked on is a legal move for this piece
	 */
	public boolean isLegalMove(Coordinates clickCoords) {
		return legalMoves.contains(clickCoords);
	}
	
	/**
	 * 
	 * @param r - row to move the piece to
	 * @param c - column to move the piece to
	 * 
	 * moves the piece to this new row and column and performs the following operations:
	 * - "take" piece if there is a piece there (remove from the board)
	 * - check to see if the other king is placed in check
	 * - check to see if the game has ended from this move
	 * - create a notation based on the conditions above
	 * - switch the turn so that it is the other player's turn
	 * 
	 * this move will be used for all pieces that don't have any other special conditions (castling, enpassant)
	 */
	public void move(int r, int c) {
		int oldR = coords.getR(), oldC = coords.getC();
		coords.setR(r);
		coords.setC(c);
		boolean captured = !chessBoard.checkEmpty(r, c);
		if(captured) {
			chessBoard.remove(chessBoard.getPiece(r, c));
		}
		if(board != null){ // following is not needed if simulating for legal moves
			boolean checked; 
			if(color == 0) checked = (checkCheck(chessBoard.getBKingCoords().getR(), chessBoard.getBKingCoords().getC(), chessBoard.getBoard()) || chessBoard.discoveredCheck(this, r, c));
			else checked = (checkCheck(chessBoard.getWKingCoords().getR(), chessBoard.getWKingCoords().getC(), chessBoard.getBoard()) || chessBoard.discoveredCheck(this, r, c));
			String sound;
			if(checked){
				board.getSoundPlayer().playCheck();
				sound = SoundPlayer.check;
			}
			else if(captured){
				board.getSoundPlayer().playCapture();
				sound = SoundPlayer.capture;
			}
			else{
				board.getSoundPlayer().playMove();
				sound = SoundPlayer.move;
			}
			boolean ended = board.checkGameEnd(color, checked);
			makeNotation(checked, captured, ended, oldR, oldC, r, c, sound);
			Coordinator.turn = 1 - Coordinator.turn;
			
			if(Coordinator.winner != -2){
				board.getSoundPlayer().playGameEnd();
			}
		}
		chessBoard.updateEnPassant(1 - color);
	}
	
	/**
	 * 
	 * @param checked - whether or not the remove resulted in a check
	 * @param captured - whether or not a piece was captured in the move
	 * @param ended - whether or not the game was ended by this move
	 * @param oldR - the old row of the piece
	 * @param oldC - the old column of the piece
	 * @param newR - the new row of the piece
	 * @param newC - the new column of the piece
	 * @param sound - the sound of the move to be saved
	 * 
	 * method to be overriden by the child classes to create a notation based on the conditions above
	 */
	public void makeNotation(boolean checked, boolean captured, boolean ended, int oldR, int oldC, int newR, int newC, String sound){

	}
	
	/**
	 * 
	 * @return an arraylist of all of the coordinates that this piece is checking/attacking
	 */
	public ArrayList<Coordinates> getCheckMoves() { return checkMoves; }
	
	/**
	 * 
	 * @return color of this piece (0 = black, 1 = white)
	 */
	public int getColor() { return color; }

	/**
	 * @return unique id of this piece
	 */
	public int getID() { return id; }

	public Board getBoard() { return board; }
	
	/**
	 * 
	 * @return the coordinates of this piece
	 */
	public Coordinates getCoords() { return coords; }
	/**
	 * 
	 * @param coords - the new coords to set the coords to
	 * updates the coordinates of this piece
	 */
	public void setCoords(Coordinates coords) {
		this.coords.setR(coords.getR());
		this.coords.setC(coords.getC());
	 }

	/**
	 * 
	 * @return an arraylist of all of the coordinates of the legal moves of this piece
	 */
	public ArrayList<Coordinates> getLegalMoves(){ return legalMoves; }

	/**
	 * 
	 * @return the imageicon of this piece based on its color
	 */
	public ImageIcon getImage(){
		if(color == 0) return wImage;
		else return bImage;
	}
	
	/**
	 * @return the coords of this piece
	 */
	public String toString() {
		return coords.toString();
	}
	
	/**
	 * @return whether or not the object being compared to is equal to this object
	 */
	public boolean equals(Object o) {
		if(!(o instanceof Piece)) return false;
		Piece other = (Piece)o;
		return this.id == other.id;
	}
	
}
