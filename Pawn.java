
import javax.swing.ImageIcon;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A child class that overrides methods in the Piece class to fit
 * how a pawn moves in Chess (moves one/two spaces forward, captures
 * one space forward diagonally, can promote).
 */

public class Pawn extends Piece{
	
	private boolean EnPassantable; // whether or not this pawn can be enpassanted
	
	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * 
	 * creates a new pawn with a unique id
	 */
	public Pawn(int r, int c, int color, ChessBoard board) {
		super(r, c, color, board);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Pawn.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Pawn.png");
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * @param enPassantable - whether or not the pawn can be enpassanted
	 * 
	 * creates a complete copy of a pawn including its id and whether or not it can be enpassanted
	 */
	public Pawn(int r, int c, int color, ChessBoard board, int id, boolean enPassantable) {
		super(r, c, color, board, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Pawn.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Pawn.png");

		EnPassantable = enPassantable;
	}
	
	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * 
	 * creates a copy of a pawn using its unique id
	 */
	public Pawn(int r, int c, int color, ChessBoard board, int id) {
		super(r, c, color, board, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_Pawn.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_Pawn.png");
	}

	
	/**
	 * @return color + Pawn + coords
	 */
	public String toString() {
		String c;
		if(color == 0) c = "White ";
		else c = "Black ";
		return c + "Pawn " + coords.toString();
	}
	
	/**
	 * @return whether or not this pawn is enpassantable
	 */
	public boolean isEnPassantable() { return EnPassantable; }

	/**
	 * sets enpassantable to false
	 */
	public void notEnPassatable() { EnPassantable = false; }
	
	/**
	 * sets legal moves of the pawn based on what a pawn can do
	 * - move two spaces forward if on starting row and not blocked
	 * - move one space forward if not blocked
	 * - capture forward one diagonally (including enpassant)
	 * - promote
	 * 
	 * checks to see if moving to each move places/leaves its king in check, if so, remove it
	 */
	public void setLegalMoves() {
		legalMoves.clear();
		Pawn temp;
		if(color == 0) {
			if(coords.getR() != 0) {
				if(chessBoard.checkEmpty(coords.getR() - 1, coords.getC())) legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC()));
				
				if(coords.getC() != 0 && chessBoard.getPiece(coords.getR() - 1, coords.getC() - 1) != null && chessBoard.getPiece(coords.getR() - 1, coords.getC() - 1).getColor() == 1) legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC() - 1));
				if(coords.getC() != 7 && chessBoard.getPiece(coords.getR() - 1, coords.getC() + 1) != null && chessBoard.getPiece(coords.getR() - 1, coords.getC() + 1).getColor() == 1) legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC() + 1));
				
			}
			if(coords.getR() == 6 && chessBoard.checkEmpty(coords.getR() - 1, coords.getC()) && chessBoard.checkEmpty(coords.getR() - 2, coords.getC())) legalMoves.add(new Coordinates(coords.getR() - 2, coords.getC()));
			if(coords.getC() > 0 && !chessBoard.checkEmpty(coords.getR(), coords.getC() - 1) && chessBoard.getPiece(coords.getR(), coords.getC() - 1).getColor() == 1 && (chessBoard.getPiece(coords.getR(), coords.getC() - 1) instanceof Pawn)) {
				temp = (Pawn)chessBoard.getPiece(coords.getR(), coords.getC() - 1);
				if(temp.isEnPassantable()) legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC() - 1));
			}
			if(coords.getC() < 7 && !chessBoard.checkEmpty(coords.getR(), coords.getC() + 1) && chessBoard.getPiece(coords.getR(), coords.getC() + 1).getColor() == 1 && (chessBoard.getPiece(coords.getR(), coords.getC() + 1) instanceof Pawn)) {
				temp = (Pawn)chessBoard.getPiece(coords.getR(), coords.getC() + 1);
				if(temp.isEnPassantable()) legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC() + 1));
			}
				
		}
		else {
			if(coords.getR() != 7) {
				if(chessBoard.checkEmpty(coords.getR() + 1, coords.getC())) legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC()));
			
				if(coords.getC() != 0 && chessBoard.getPiece(coords.getR() + 1, coords.getC() - 1) != null && chessBoard.getPiece(coords.getR() + 1, coords.getC() - 1).getColor() == 0) legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC() - 1));
				if(coords.getC() != 7 && chessBoard.getPiece(coords.getR() + 1, coords.getC() + 1) != null && chessBoard.getPiece(coords.getR() + 1, coords.getC() + 1).getColor() == 0) legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC() + 1));

			}
			if(coords.getR() == 1 && chessBoard.checkEmpty(coords.getR() + 1, coords.getC()) && chessBoard.checkEmpty(coords.getR() + 2, coords.getC())) legalMoves.add(new Coordinates(coords.getR() + 2, coords.getC()));
			if(coords.getC() > 0 && !chessBoard.checkEmpty(coords.getR(), coords.getC() - 1) && chessBoard.getPiece(coords.getR(), coords.getC() - 1).getColor() == 0 && (chessBoard.getPiece(coords.getR(), coords.getC() - 1) instanceof Pawn)) {
				temp = (Pawn)chessBoard.getPiece(coords.getR(), coords.getC() - 1);
				if(temp.isEnPassantable()) legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC() - 1));
			}
			if(coords.getC() < 7 && !chessBoard.checkEmpty(coords.getR(), coords.getC() + 1) && chessBoard.getPiece(coords.getR(), coords.getC() + 1).getColor() == 0 && (chessBoard.getPiece(coords.getR(), coords.getC() + 1) instanceof Pawn)) {
				temp = (Pawn)chessBoard.getPiece(coords.getR(), coords.getC() + 1);
				if(temp.isEnPassantable()) legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC() + 1));
			}
		}
		for(int i = legalMoves.size() - 1; i >= 0; i--) {
			if(chessBoard.checkChecks(this, legalMoves.get(i).getR(), legalMoves.get(i).getC())) legalMoves.remove(i);
		}
	}
	
	/**
	 * @param cBoard - board of spaces that the pieces are on
	 * 
	 * sets where the pawn can check/attack
	 * 
	 */
	public void setCheckMoves(Spaces[][] board) {
		checkMoves.clear();
		if(color == 0) {
			if(coords.getR() != 0) {
				checkMoves.add(new Coordinates(coords.getR() - 1, coords.getC() - 1));
				checkMoves.add(new Coordinates(coords.getR() - 1, coords.getC() + 1));
			}
				
		}
		else {
			if(coords.getR() != 7) {
				checkMoves.add(new Coordinates(coords.getR() + 1, coords.getC() - 1));
				checkMoves.add(new Coordinates(coords.getR() + 1, coords.getC() + 1));
			}
		}
	}
	
	/**
	 * @param r - row to move to
	 * @param c - column to move to
	 * 
	 * moves this pawn based on the rules of pawns (overrides Piece move because it needs to perform enpassanting and update enpassantable)
	 * - checks if it has captured a piece (including enpassanting)
	 * - checks if it caused a discovered check
	 * - checks if the move causes the game to end
	 * 
	 * creates a notation based on the conditions above
	 * switches the turn to the other player's turn
	 */
	public void move(int r, int c) {
		int oldR = coords.getR(), oldC = coords.getC();
		boolean captured = !chessBoard.checkEmpty(r, c), enpassanted = false;
		if(r != coords.getR() && c != coords.getC() && !chessBoard.checkEmpty(coords.getR(), c) && (chessBoard.getPiece(coords.getR(), c) instanceof Pawn)) {
			Pawn temp = (Pawn)chessBoard.getPiece(coords.getR(), c);
			if(temp.isEnPassantable()) {
				chessBoard.remove(chessBoard.getPiece(coords.getR(), c));
				captured = true;
				enpassanted = true;
			}
		}
		if(r == coords.getR() + 2 || r == coords.getR() - 2) {
			EnPassantable = true;
		}
		
		coords.setR(r);
		coords.setC(c);
		if(board != null){
			boolean checked;
			if(color == 0) checked = (checkCheck(chessBoard.getBKingCoords().getR(), chessBoard.getBKingCoords().getC(), chessBoard.getBoard()) || chessBoard.discoveredCheck(this, r, c));
			else checked = (checkCheck(chessBoard.getWKingCoords().getR(), chessBoard.getWKingCoords().getC(), chessBoard.getBoard()) || chessBoard.discoveredCheck(this, r, c));
			
			if(captured) {
				if(!chessBoard.checkEmpty(r, c)) chessBoard.remove(chessBoard.getPiece(r, c));
			}
			boolean promoted = (r == 0 || r == 7);
			String sound;
			if(checked){
				board.getSoundPlayer().playCheck();
				sound = SoundPlayer.check;
			}
			else if(captured){
				board.getSoundPlayer().playCapture();
				sound = SoundPlayer.capture;
			}
			else if(promoted){
				board.getSoundPlayer().playPromote();
				sound = SoundPlayer.promote;
			}
			else{
				board.getSoundPlayer().playMove();
				sound = SoundPlayer.move;
			}
			if(promoted) {
				board.updateBoard();
				board.promote(this, color, createPromoteNotation(captured, oldR, oldC, r, c), captured);
			}
			else {
				boolean ended = board.checkGameEnd(color, checked);
				makeNotation(checked, captured, ended, enpassanted, oldR, oldC, r, c, sound);
				Coordinator.turn = 1 - Coordinator.turn;
			}
			if(Coordinator.winner != -2){
				board.getSoundPlayer().playGameEnd();
			}
		}
		
		chessBoard.updateEnPassant(1 - color);
		
	}

	/**
	 * 
	 * @param r - row to move to
	 * @param c - column to move to
	 * @param promote - piece to promote to
	 * 
	 * simulates moving + promotion (automatically promotes)
	 */
	public void move(int r, int c, String promote) {
		int oldR = coords.getR(), oldC = coords.getC();
		boolean captured = !chessBoard.checkEmpty(r, c);
		
		coords.setR(r);
		coords.setC(c);
		
		if(captured && !chessBoard.checkEmpty(r, c)) chessBoard.remove(chessBoard.getPiece(r, c));
		
		board.updateBoard();
		board.promote(this, color, createPromoteNotation(captured, oldR, oldC, r, c), promote, captured);
		
		chessBoard.updateEnPassant(1 - color);
		
	}
	
	/**
	 * 
	 * @param r - row to check
	 * @param c - column to check
	 * @return whether or not this pawn is checking/attacking the specified row and column
	 */
	public boolean checkCheck(int r, int c, Spaces[][] board) {
		if(color == 0) return (r == coords.getR() - 1) && (c == coords.getC() - 1 || c == coords.getC() + 1); 
		else return (r == coords.getR() + 1) && (c == coords.getC() - 1 || c == coords.getC() + 1); 
	}
	
	/**
	 * 
	 * @param checked - whether or not the move resulted in a check
	 * @param captured - whether or not a piece was captured in the move
	 * @param ended - whether or not the game was ended by the move
	 * @param enpassanted - whether or not this pawn enpassanted another pawn
	 * @param oldR - the old row of the piece
	 * @param oldC - the old column of the piece
	 * @param newR - the new row of the piece
	 * @param newC - the new column of the piece
	 * @param sound - sound of the move to be saved
	 * 
	 * creates a notation based on the above conditions and adds it to the board
	 */
	public void makeNotation(boolean checked, boolean captured, boolean ended, boolean enpassanted, int oldR, int oldC, int newR, int newC, String sound) {
		String text = Board.letters[oldC];
		if(captured) {
			text += "x";
			text += Board.letters[newC];
		}
		text += Board.numbers[7 - newR];
		if(enpassanted) {
			text += "e.p";
		}
		if(checked) {
			if(ended) text += "#";
			else text += "+";
		}
		else if(ended) {
			text += " 1/2-1/2";
		}
		
		board.addNotation(text, color, oldR, oldC, newR, newC, "", sound);
	}
	
	/**
	 * 
	 * @param captured - whether or not a piece was captured
	 * @param oldR - the old row of the piece
	 * @param oldC - the old column of the piece
	 * @param newR - the new row of the piece
	 * @param newC - the new column of the piece
	 * 
	 * @return first part of the notation for promotion
	 * 
	 * creates the first part of the notation for a promotion move
	 */
	public String createPromoteNotation(boolean captured, int oldR, int oldC, int newR, int newC) {
		String text = Board.letters[oldC];
		if(captured) {
			text += "x";
			text += Board.letters[newC];
		}
		text += Board.numbers[7 - newR];
		text += "=";
		return text;
	}

}
