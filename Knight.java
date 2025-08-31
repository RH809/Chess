
import java.util.ArrayList;

import javax.swing.ImageIcon;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A child class that overrides methods in the Piece class to fit
 * how a knight moves in Chess (Moves in L shape and can jump over other pieces).
 */
public class Knight extends Piece{
	
	private static int[][] moves = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {1, -2}, {2, -1}, {-1, -2}, {-2, -1}}; // all of the possible moves (changes in row and column) for knights
	
	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * 
	 * creates a new knight with a unique id
	 */
	public Knight(int r, int c, int color, ChessBoard board) {
		super(r, c, color, board);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Knight.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Knight.png");
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * 
	 * creates a copy of a knight using another knight's id
	 */
	public Knight(int r, int c, int color, ChessBoard board, int id) {
		super(r, c, color, board, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Knight.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Knight.png");
	}
	
	/**
	 * @return color + Knight + coords
	 */
	public String toString() {
		String c;
		if(color == 0) c = "White ";
		else c = "Black ";
		return c + "Knight " + coords.toString();
	}
	
	/**
	 * sets the legal moves based on what a knight can do
	 * - can perform any of the moves (because it can jump over pieces) as long as another piece of the same color is in that space
	 * 
	 * checks each move to see if moving there will place its king in check, if so, remove that move
	 */
	public void setLegalMoves() {
		legalMoves.clear();
		int r, c;
		for(int i = 0; i < 8; i++) {
			r = coords.getR() + moves[i][0];
			c = coords.getC() + moves[i][1];
			if(0 <= r && r < 8 && 0 <= c && c < 8 && (chessBoard.checkEmpty(r, c) || chessBoard.getPiece(r, c).getColor() != this.color)) {
				legalMoves.add(new Coordinates(r, c));
			}
		}
		
		for(int i = legalMoves.size() - 1; i >= 0; i--) {
			if(chessBoard.checkChecks(this, legalMoves.get(i).getR(), legalMoves.get(i).getC())) legalMoves.remove(i);
		}
	}
	
	/**
	 * @param board - board that the pieces are in
	 * 
	 * sets all of the moves that this knight is checking/attacking
	 */
	public void setCheckMoves(Spaces[][] board) {
		checkMoves.clear();
		int r, c;
		for(int i = 0; i < 8; i++) {
			r = coords.getR() + moves[i][0];
			c = coords.getC() + moves[i][1];
			if(0 <= r && r < 8 && 0 <= c && c < 8) {
				checkMoves.add(new Coordinates(r, c));
			}
		}
	}
	
	/**
	 * @param checked - whether or not the move resulted in a check
	 * @param captured - whether or not a piece was captured in the move
	 * @param ended - whether or not the game was ended by this move
	 * @param oldR - the old row of the piece
	 * @param oldC - the old column of the piece
	 * @param newR - the new row of the piece
	 * @param newC - the new column of the piece
	 * @param sound - the sound of the moved to be saved
	 * 
	 * creates a notation based on the conditions above and adds it to board
	 */
	public void makeNotation(boolean checked, boolean captured, boolean ended, int oldR, int oldC, int newR, int newC, String sound) {
		boolean sameRow = false, sameCol = false, sameMove = false;
		Coordinates newCoords = new Coordinates(newR, newC);
		String text = "N";
		ArrayList<Piece> pieces = chessBoard.getPieces();
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i) instanceof Knight && !pieces.get(i).equals(this) && pieces.get(i).getColor() == color && pieces.get(i).getCheckMoves().contains(newCoords)) {
				sameMove = true;
				if(pieces.get(i).getCoords().getR() == oldR) sameRow = true;
				if(pieces.get(i).getCoords().getC() == oldC) sameCol = true;
			}
		}
		if(sameMove) {
			if((sameRow && !sameCol) || (!sameRow && !sameCol)) {
				text += Board.letters[oldC];
			}
			else if(sameCol && !sameRow) {
				text += Board.numbers[7 - oldR];
			}
			else {
				text += Board.letters[oldC];
				text += Board.numbers[7 - oldR];
			}
		}
		if(captured) {
			text += "x";
		}
		text += Board.letters[newC];
		text += Board.numbers[7 - newR];
		if(checked) {
			if(ended) text += "#";
			else text += "+";
		}
		else if(ended) {
			text += " 1/2-1/2";
		}
		board.addNotation(text, color, oldR, oldC, newR, newC, "", sound);
	}
}
