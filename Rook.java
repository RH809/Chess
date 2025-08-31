
import java.util.ArrayList;

import javax.swing.ImageIcon;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A child class that overrides methods in the Piece class to fit
 * how a rook moves in Chess (horizontally, vertically, can castle).
 */
public class Rook extends Piece{
	
	private boolean castle; // whether or not this rook can still participate in castling
	
	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * 
	 * creates a new rook with a unique id
	 */
	public Rook(int r, int c, int color, ChessBoard chessBoard) {
		super(r, c, color, chessBoard);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Rook.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Rook.png");
		castle = true;
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * 
	 * creates a copy of a rook using its unique id
	 */
	public Rook(int r, int c, int color, ChessBoard chessBoard, int id) {
		super(r, c, color, chessBoard, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Rook.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Rook.png");
		castle = true;
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * @param castle - whether or not this rook can castle
	 * 
	 * creates a complete copy of a rook including its id and whether or not it can castle
	 */
	public Rook(int r, int c, int color, ChessBoard chessBoard, int id, boolean castle) {
		super(r, c, color, chessBoard, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Rook.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Rook.png");
		this.castle = castle;
	}
	
	/**
	 * @return color + Rook + coords
	 */
	public String toString() {
		String c;
		if(color == 0) c = "White ";
		else c = "Black ";
		return c + "Rook " + coords.toString();
	}
	
	/**
	 * 
	 * @return whether or not this rook can participate in castling
	 */
	public boolean canCastle() { return castle; }
	
	/**
	 * sets legal moves of the king based on what a king can do
	 * - can move up/down and left/right until it reaches another piece or the end of the board
	 * 
	 * checks to see if moving to each move places/leaves its king in check, if so, remove it
	 */
	public void setLegalMoves() {
		legalMoves.clear();
		int i = 1;
		while(coords.getR() + i < 8) {
			if(!chessBoard.checkEmpty(coords.getR() + i, coords.getC())) {
				if(chessBoard.getPiece(coords.getR() + i, coords.getC()).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR() + i, coords.getC()));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR() + i, coords.getC()));
			i++;
		}
		i = 1;
		while(coords.getR() - i >= 0) {
			if(!chessBoard.checkEmpty(coords.getR() - i, coords.getC())) {
				if(chessBoard.getPiece(coords.getR() - i, coords.getC()).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR() - i, coords.getC()));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR() - i, coords.getC()));
			i++;
		}
		i = 1;
		while(coords.getC() - i >= 0) {
			if(!chessBoard.checkEmpty(coords.getR(), coords.getC() - i)) {
				if(chessBoard.getPiece(coords.getR(), coords.getC() - i).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR(), coords.getC() - i));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR(), coords.getC() - i));
			i++;
		}
		i = 1;
		while(coords.getC() + i < 8) {
			if(!chessBoard.checkEmpty(coords.getR(), coords.getC() + i)) {
				if(chessBoard.getPiece(coords.getR(), coords.getC() + i).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR(), coords.getC() + i));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR(), coords.getC() + i));
			i++;
		}
		
		for(int j = legalMoves.size() - 1; j >= 0; j--) {
			if(chessBoard.checkChecks(this, legalMoves.get(j).getR(), legalMoves.get(j).getC())) legalMoves.remove(j);
		}
	}
	
	/**
	 * @param board - board of spaces that the pieces are on
	 * 
	 * sets the moves that the rook is checking/attacking
	 * 
	 */
	public void setCheckMoves(Spaces[][] board) {
		checkMoves.clear();
		int i = 1;
		while(coords.getR() + i < 8) {
			if(!board[coords.getR() + i][coords.getC()].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() + i, coords.getC()));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() + i, coords.getC()));
			i++;
		}
		i = 1;
		while(coords.getR() - i >= 0) {
			if(!board[coords.getR() - i][coords.getC()].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() - i, coords.getC()));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() - i, coords.getC()));
			i++;
		}
		i = 1;
		while(coords.getC() - i >= 0) {
			if(!board[coords.getR()][coords.getC() - i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR(), coords.getC() - i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR(), coords.getC() - i));
			i++;
		}
		i = 1;
		while(coords.getC() + i < 8) {
			if(!board[coords.getR()][coords.getC() + i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR(), coords.getC() + i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR(), coords.getC() + i));
			i++;
		}
	}
	
	/**
	 * @param r - row to move to
	 * @param c - column to move to
	 * 
	 * moves using Piece's move method but also sets castle to false
	 */
	public void move(int r, int c) {
		castle = false;
		super.move(r, c);
	}
	
	/**
	 * castles, moving the piece to the appropriate location
	 * @return whether or not castling places the other king in check from the rook being moved
	 */
	public boolean castling() {
		if(coords.getC() == 0) {
			coords.setC(3);
		}
		else {
			coords.setC(5);
		}
		castle = false;
		if(color == 0) {
			return checkCheck(chessBoard.getBKingCoords().getR(), chessBoard.getBKingCoords().getC(), chessBoard.getBoard());
		}
		else {
			return checkCheck(chessBoard.getWKingCoords().getR(), chessBoard.getWKingCoords().getC(), chessBoard.getBoard());
		}
	}
	/**
	 * 
	 * @param checked - whether or not the move resulted in a check
	 * @param captured - whether or not a piece was captured in the move
	 * @param ended - whether or not the game was ended by the move
	 * @param oldR - the old row of the piece
	 * @param oldC - the old column of the piece
	 * @param newR - the new row of the piece
	 * @param newC - the new column of the piece
	 * @param sound - sound of the move to be saved
	 * 
	 * creates a notation based on the above conditions and adds it to the board
	 */
	public void makeNotation(boolean checked, boolean captured, boolean ended, int oldR, int oldC, int newR, int newC, String sound) {
		boolean sameRow = false, sameCol = false, sameMove = false;
		Coordinates newCoords = new Coordinates(newR, newC);
		String text = "R";
		ArrayList<Piece> pieces = chessBoard.getPieces();
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i) instanceof Rook && !pieces.get(i).equals(this) && pieces.get(i).getColor() == color && pieces.get(i).getCheckMoves().contains(newCoords)) {
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
