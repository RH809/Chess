import javax.swing.ImageIcon;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A child class that overrides methods in the Piece class to fit
 * how a king moves in Chess (moves to surrounding eight spaces and can castle).
 */
public class King extends Piece{
	
	private boolean castle; // whether or not the king can still castle
	
	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * 
	 * creates a new king with a unique id
	 */
	public King(int r, int c, int color, ChessBoard board) {
		super(r, c, color, board);
		
		wImage = new ImageIcon("Images_and_Sounds/White_King.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_King.png");
		
		castle = true;
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * @param castle - whether or not this king can castle
	 * 
	 * creates a complete copy of a king including its id and whether or not it can castle
	 */
	public King(int r, int c, int color, ChessBoard board, int id, boolean castle) {
		super(r, c, color, board, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_King.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_King.png");
		
		this.castle = castle;
	}

	/**
	 * 
	 * @param r - row of the piece
	 * @param c - column of the piece
	 * @param color - color of the piece
	 * @param board - reference to the board that the piece is in
	 * @param id - id to copy
	 * 
	 * creates a copy of a king using its unique id
	 */
	public King(int r, int c, int color, ChessBoard board, int id) {
		super(r, c, color, board, id);
		
		wImage = new ImageIcon("Images_and_Sounds/White_King.png");
		bImage = new ImageIcon("Images_and_Sounds/Black_King.png");
		
		castle = true;
	}
	
	/**
	 * @return color + King + coords
	 */
	public String toString() {
		String c;
		if(color == 0) c = "White ";
		else c = "Black ";
		return c + "King " + coords.toString();
	}
	
	/**
	 * 
	 * @return whether or not this king can still castle
	 */
	public boolean canCastle() { return castle; }
	
	/**
	 * sets legal moves of the king based on what a king can do
	 * - adds all eight moves around the king as long as there isn't a piece of the same color
	 * 
	 * checks to see if moving to each move places the king in check, if so, remove it
	 */
	public void setLegalMoves() {
		legalMoves.clear();
		if(coords.getR() + 1 < 8 && (chessBoard.checkEmpty(coords.getR() + 1, coords.getC()) || chessBoard.getPiece(coords.getR() + 1, coords.getC()).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC()));
		}
		if(coords.getR() - 1 >= 0 && (chessBoard.checkEmpty(coords.getR() - 1, coords.getC()) || chessBoard.getPiece(coords.getR() - 1, coords.getC()).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC()));
		}
		if(coords.getC() - 1 >= 0 && (chessBoard.checkEmpty(coords.getR(), coords.getC() - 1) || chessBoard.getPiece(coords.getR(), coords.getC() - 1).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR(), coords.getC() - 1));
		}
		if(coords.getC() + 1 < 8 && (chessBoard.checkEmpty(coords.getR(), coords.getC() + 1) || chessBoard.getPiece(coords.getR(), coords.getC() + 1).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR(), coords.getC() + 1));
		}
		if(coords.getR() + 1 < 8 && coords.getC() + 1 < 8 && (chessBoard.checkEmpty(coords.getR() + 1, coords.getC() + 1) || chessBoard.getPiece(coords.getR() + 1, coords.getC() + 1).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC() + 1));
		}
		if(coords.getR() + 1 < 8 && coords.getC() - 1 >= 0 && (chessBoard.checkEmpty(coords.getR() + 1, coords.getC() - 1) || chessBoard.getPiece(coords.getR() + 1, coords.getC() - 1).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR() + 1, coords.getC() - 1));
		}
		if(coords.getR() - 1 >= 0 && coords.getC() + 1 < 8 && (chessBoard.checkEmpty(coords.getR() - 1, coords.getC() + 1) || chessBoard.getPiece(coords.getR() - 1, coords.getC() + 1).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC() + 1));
		}
		if(coords.getR() - 1 >= 0 && coords.getC() - 1 >= 0 && (chessBoard.checkEmpty(coords.getR() - 1, coords.getC() - 1) || chessBoard.getPiece(coords.getR() - 1, coords.getC() - 1).getColor() != this.color)) {
			legalMoves.add(new Coordinates(coords.getR() - 1, coords.getC() - 1));
		}
		Rook temp;
		// castling
		if(!chessBoard.inCheck()){
			if(castle && chessBoard.checkEmpty(coords.getR(), coords.getC() + 1) && chessBoard.checkEmpty(coords.getR(), coords.getC() + 2) && !chessBoard.checkEmpty(coords.getR(), coords.getC() + 3) && chessBoard.getPiece(coords.getR(), coords.getC() + 3) instanceof Rook && chessBoard.getPiece(coords.getR(), coords.getC() + 3).getColor() == color) {
				temp = (Rook)chessBoard.getPiece(coords.getR(), coords.getC() + 3);
				if(temp.canCastle()) legalMoves.add(new Coordinates(coords.getR(), coords.getC() + 2));
			}
			if(castle && chessBoard.checkEmpty(coords.getR(), coords.getC() - 1) && chessBoard.checkEmpty(coords.getR(), coords.getC() - 2) && chessBoard.checkEmpty(coords.getR(), coords.getC() - 3) && !chessBoard.checkEmpty(coords.getR(), coords.getC() - 4) && chessBoard.getPiece(coords.getR(), coords.getC() - 4) instanceof Rook && chessBoard.getPiece(coords.getR(), coords.getC() - 4).getColor() == color) {
				temp = (Rook)chessBoard.getPiece(coords.getR(), coords.getC() - 4);
				if(temp.canCastle()) legalMoves.add(new Coordinates(coords.getR(), coords.getC() - 2));
			}
		}
		
		for(int i = legalMoves.size() - 1; i >= 0; i--) {
			if(chessBoard.checkChecks(this, legalMoves.get(i).getR(), legalMoves.get(i).getC())) legalMoves.remove(i);
		}
	}
	
	/**
	 * @param board - board of spaces that the pieces are on
	 * 
	 * sets the check moves (kings cannot be next to each other,
	 * so setting adjacent spaces as "checked" so that other king cannot move there)
	 * 
	 */
	public void setCheckMoves(Spaces[][] board) {
		checkMoves.clear();
		if(coords.getR() + 1 < 8 ) {
			checkMoves.add(new Coordinates(coords.getR() + 1, coords.getC()));
		}
		if(coords.getR() - 1 >= 0) {
			checkMoves.add(new Coordinates(coords.getR() - 1, coords.getC()));
		}
		if(coords.getC() - 1 >= 0) {
			checkMoves.add(new Coordinates(coords.getR(), coords.getC() - 1));
		}
		if(coords.getC() + 1 < 8) {
			checkMoves.add(new Coordinates(coords.getR(), coords.getC() + 1));
		}
		if(coords.getR() + 1 < 8 && coords.getC() + 1 < 8) {
			checkMoves.add(new Coordinates(coords.getR() + 1, coords.getC() + 1));
		}
		if(coords.getR() + 1 < 8 && coords.getC() - 1 >= 0) {
			checkMoves.add(new Coordinates(coords.getR() + 1, coords.getC() - 1));
		}
		if(coords.getR() - 1 >= 0 && coords.getC() + 1 < 8) {
			checkMoves.add(new Coordinates(coords.getR() - 1, coords.getC() + 1));
		}
		if(coords.getR() - 1 >= 0 && coords.getC() - 1 >= 0) {
			checkMoves.add(new Coordinates(coords.getR() - 1, coords.getC() - 1));
		}
	}
	
	/**
	 * @param r - row to move to
	 * @param c - column to move to
	 * 
	 * moves this king based on the rules of king (overrides Piece move because it needs to perform castling and update castling)
	 * - if castling, move self and rook
	 * - checks if it has captured a piece
	 * - checks if it caused a discovered check
	 * - checks if the move causes the game to end
	 * 
	 * creates a notation based on the conditions above
	 * switches the turn to the other player's turn
	 */
	public void move(int r, int c) {
		int oldR = coords.getR(), oldC = coords.getC();
		boolean castledRight = false, castledLeft = false;
		Rook temp;
		boolean checked = false;
		if(c == coords.getC() - 2) {
			temp = (Rook)chessBoard.getPiece(coords.getR(), 0);
			checked = temp.castling();
			castledLeft = true;
		}
		else if(c == coords.getC() + 2) {
			temp = (Rook)chessBoard.getPiece(coords.getR(), 7);
			checked = temp.castling();
			castledRight = true;
		}
		if(!checked){
			checked = (chessBoard.discoveredCheck(this, r, c));
		}
		//System.out.println("castled: " + castledRight + " " + castledLeft);
		castle = false;
		coords.setR(r);
		coords.setC(c);
		boolean captured = !chessBoard.checkEmpty(r, c);
		if(captured) {
			chessBoard.remove(chessBoard.getPiece(r, c));
		}
		// play sound
		if(board != null){
		String sound;
			if(checked){
				board.getSoundPlayer().playCheck();
				sound = SoundPlayer.check;
			}
			else if(castledRight || castledLeft){
				board.getSoundPlayer().playCastle();
				sound = SoundPlayer.castle;
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
			makeNotation(checked, captured, ended, castledRight, castledLeft, oldR, oldC, r, c, sound);
			Coordinator.turn = 1 - Coordinator.turn;

			
			if(Coordinator.winner != -2){
				board.getSoundPlayer().playGameEnd();
			}
		}
		chessBoard.updateEnPassant(1 - color);
	}
	
	/**
	 * 
	 * @param checked - whether or not the move resulted in a check
	 * @param captured - whether or not a piece was captured in the move
	 * @param ended - whether or not the game was ended by the move
	 * @param castledRight - whether or not the king castled to the right (short castle)
	 * @param castledLeft - whether or not the king castled to the left (long castle)
	 * @param oldR - the old row of the piece
	 * @param oldC - the old column of the piece
	 * @param newR - the new row of the piece
	 * @param newC - the new column of the piece
	 * @param sound - sound of the move to be saved
	 * 
	 * creates a notation based on the above conditions and adds it to the board
	 */
	public void makeNotation(boolean checked, boolean captured, boolean ended, boolean castledRight, boolean castledLeft, int oldR, int oldC, int newR, int newC, String sound) {
		String text = "";
		if(castledRight) {
			text += "O-O";
			if(checked) {
				if(ended) text += "#";
				else text += "+";
			}
		}
		else if(castledLeft) {
			text += "O-O-O";
			if(checked) {
				if(ended) text += "#";
				else text += "+";
			}
		}
		else {
			text += "K";
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
		}
		board.addNotation(text, color, oldR, oldC, newR, newC, "", sound);
	}

}
