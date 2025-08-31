
import java.util.ArrayList;

import javax.swing.ImageIcon;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A child class that overrides methods in the Piece class to fit
 * how a queen moves in Chess (diagonally, vertically, and horizontally)
 */
public class Queen extends Piece{
	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * 
	 * creates a new queen with a unique id
	 */
	public Queen(int r, int c, int color, ChessBoard board) {
		super(r, c, color, board);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Queen.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Queen.png");
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * 
	 * creates a copy of a queen using another queen's id
	 */
	public Queen(int r, int c, int color, ChessBoard board, int id) {
		super(r, c, color, board, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Queen.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Queen.png");
	}

	/**
	 * @return color + Queen + coords
	 */
	public String toString() {
		String c;
		if(color == 0) c = "White ";
		else c = "Black ";
		return c + "Queen " + coords.toString();
	}
	
	/**
	 * sets the legal moves based on what a queen can do
	 * - can move in all directions until reaching another piece or the end of the board
	 * 
	 * checks each move to see if moving there will place its king in check, if so, remove that move
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
		i = 1;
		while(coords.getR() + i < 8 && coords.getC() + i < 8) {
			if(!chessBoard.checkEmpty(coords.getR() + i, coords.getC() + i)) {
				if(chessBoard.getPiece(coords.getR() + i, coords.getC() + i).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR() + i, coords.getC() + i));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR() + i, coords.getC() + i));
			i++;
		}
		i = 1;
		while(coords.getR() - i >= 0 && coords.getC() + i < 8) {
			if(!chessBoard.checkEmpty(coords.getR() - i, coords.getC() + i)) {
				if(chessBoard.getPiece(coords.getR() - i, coords.getC() + i).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR() - i, coords.getC() + i));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR() - i, coords.getC() + i));
			i++;
		}
		i = 1;
		while(coords.getR() + i < 8 && coords.getC() - i >= 0) {
			if(!chessBoard.checkEmpty(coords.getR() + i, coords.getC() - i)) {
				if(chessBoard.getPiece(coords.getR() + i, coords.getC() - i).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR() + i, coords.getC() - i));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR() + i, coords.getC() - i));
			i++;
		}
		i = 1;
		while(coords.getR() - i >= 0 && coords.getC() - i >= 0) {
			if(!chessBoard.checkEmpty(coords.getR() - i, coords.getC() - i)) {
				if(chessBoard.getPiece(coords.getR() - i, coords.getC() - i).getColor() != this.color) {
					legalMoves.add(new Coordinates(coords.getR() - i, coords.getC() - i));
				}
				break;
			}
			else legalMoves.add(new Coordinates(coords.getR() - i, coords.getC() - i));
			i++;
		}
		
		for(int j = legalMoves.size() - 1; j >= 0; j--) {
			if(chessBoard.checkChecks(this, legalMoves.get(j).getR(), legalMoves.get(j).getC())) legalMoves.remove(j);
		}
	}
	
	/**
	 * @param cBoard - board that the pieces are in
	 * 
	 * sets all of the moves that this queen is checking/attacking
	 */
	public void setCheckMoves(Spaces[][] cBoard) {
		checkMoves.clear();
		int i = 1;
		while(coords.getR() + i < 8) {
			if(!cBoard[coords.getR() + i][coords.getC()].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() + i, coords.getC()));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() + i, coords.getC()));
			i++;
		}
		i = 1;
		while(coords.getR() - i >= 0) {
			if(!cBoard[coords.getR() - i][coords.getC()].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() - i, coords.getC()));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() - i, coords.getC()));
			i++;
		}
		i = 1;
		while(coords.getC() - i >= 0) {
			if(!cBoard[coords.getR()][coords.getC() - i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR(), coords.getC() - i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR(), coords.getC() - i));
			i++;
		}
		i = 1;
		while(coords.getC() + i < 8) {
			if(!cBoard[coords.getR()][coords.getC() + i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR(), coords.getC() + i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR(), coords.getC() + i));
			i++;
		}
		i = 1;
		while(coords.getR() + i < 8 && coords.getC() + i < 8) {
			if(!cBoard[coords.getR() + i][coords.getC() + i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() + i, coords.getC() + i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() + i, coords.getC() + i));
			i++;
		}
		i = 1;
		while(coords.getR() - i >= 0 && coords.getC() + i < 8) {
			if(!cBoard[coords.getR() - i][coords.getC() + i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() - i, coords.getC() + i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() - i, coords.getC() + i));
			i++;
		}
		i = 1;
		while(coords.getR() + i < 8 && coords.getC() - i >= 0) {
			if(!cBoard[coords.getR() + i][coords.getC() - i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() + i, coords.getC() - i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() + i, coords.getC() - i));
			i++;
		}
		i = 1;
		while(coords.getR() - i >= 0 && coords.getC() - i >= 0) {
			if(!cBoard[coords.getR() - i][coords.getC() - i].isEmpty()) {
				checkMoves.add(new Coordinates(coords.getR() - i, coords.getC() - i));
				break;
			}
			else checkMoves.add(new Coordinates(coords.getR() - i, coords.getC() - i));
			i++;
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
	 * @param sound - sound of the move to be saved
	 * 
	 * creates a notation based on the conditions above and adds it to board
	 */
	public void makeNotation(boolean checked, boolean captured, boolean ended, int oldR, int oldC, int newR, int newC, String sound) {
		boolean sameRow = false, sameCol = false, sameMove = false;
		Coordinates newCoords = new Coordinates(newR, newC);
		String text = "Q";
		ArrayList<Piece> pieces = chessBoard.getPieces();
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i) instanceof Queen && !pieces.get(i).equals(this) && pieces.get(i).getColor() == color && pieces.get(i).getCheckMoves().contains(newCoords)) {
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
