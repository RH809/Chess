import java.util.ArrayList;

public class ChessBoard {
    // class for holding one specific board (not necessarily the actual board, might be a temporary board)
    // operations based on its own board
    // Board class will use operations to change actual board

    private Spaces[][] board;
    private Coordinates bKingCoords, wKingCoords;
    private ArrayList<Piece> pieces;
    private Board myBoard = null;
    private boolean check;

    public ChessBoard(Board myBoard){ // default chess board for displaying
        this.myBoard = myBoard;
        board = new Spaces[Board.BOARDSIZE][Board.BOARDSIZE];
        pieces = new ArrayList<Piece>();
        initializeDefaultBoard();
    }

    public ChessBoard(boolean def){ // creates empty board to be filled
        board = new Spaces[Board.BOARDSIZE][Board.BOARDSIZE];
        pieces = new ArrayList<Piece>();
        if(def){
            initializeDefaultBoard();
        }
        else{
            for(int i = 0; i < Board.BOARDSIZE; i++){
                for(int j = 0; j < Board.BOARDSIZE; j++){
                    board[i][j] = new Spaces(i, j, true);
                }
            }
        }
    }

    public void initializeDefaultBoard(){
        pieces.add(new King(7, 4, 0, this));
        board[7][4] = new Spaces(7, 4, pieces.get(pieces.size() - 1));
        wKingCoords = board[7][4].getPiece().getCoords();

        pieces.add(new King(0, 4, 1, this));
        board[0][4] = new Spaces(0, 4, pieces.get(pieces.size() - 1));
        bKingCoords = board[0][4].getPiece().getCoords();
        // set the coords for the kings themselves

        pieces.add(new Pawn(6, 0, 0, this));
        board[6][0] = new Spaces(6, 0, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(6, 1, 0, this));
        board[6][1] = new Spaces(6, 1, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(6, 2, 0, this));
        board[6][2] = new Spaces(6, 2, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(6, 3, 0, this));
        board[6][3] = new Spaces(6, 3, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(6, 4, 0, this));
        board[6][4] = new Spaces(6, 4, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(6, 5, 0, this));
        board[6][5] = new Spaces(6, 5, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(6, 6, 0, this));
        board[6][6] = new Spaces(6, 6, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(6, 7, 0, this));
        board[6][7] = new Spaces(6, 7, pieces.get(pieces.size() - 1));
        
        pieces.add(new Rook(7, 0, 0, this));
        board[7][0] = new Spaces(7, 0, pieces.get(pieces.size() - 1));
        pieces.add(new Rook(7, 7, 0, this));
        board[7][7] = new Spaces(7, 7, pieces.get(pieces.size() - 1));
        
        pieces.add(new Knight(7, 1, 0, this));
        board[7][1] = new Spaces(7, 1, pieces.get(pieces.size() - 1));
        pieces.add(new Knight(7, 6, 0, this));
        board[7][6] = new Spaces(7, 6, pieces.get(pieces.size() - 1));
        
        pieces.add(new Bishop(7, 2, 0, this));
        board[7][2] = new Spaces(7, 2, pieces.get(pieces.size() - 1));
        pieces.add(new Bishop(7, 5, 0, this));
        board[7][5] = new Spaces(7, 5, pieces.get(pieces.size() - 1));
        
        pieces.add(new Queen(7, 3, 0, this));
        board[7][3] = new Spaces(7, 3, pieces.get(pieces.size() - 1));
        
        
        
        pieces.add(new Pawn(1, 0, 1, this));
        board[1][0] = new Spaces(1, 0, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(1, 1, 1, this));
        board[1][1] = new Spaces(1, 1, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(1, 2, 1, this));
        board[1][2] = new Spaces(1, 2, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(1, 3, 1, this));
        board[1][3] = new Spaces(1, 3, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(1, 4, 1, this));
        board[1][4] = new Spaces(1, 4, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(1, 5, 1, this));
        board[1][5] = new Spaces(1, 5, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(1, 6, 1, this));
        board[1][6] = new Spaces(1, 6, pieces.get(pieces.size() - 1));
        pieces.add(new Pawn(1, 7, 1, this));
        board[1][7] = new Spaces(1, 7, pieces.get(pieces.size() - 1));
        
        pieces.add(new Rook(0, 0, 1, this));
        board[0][0] = new Spaces(0, 0, pieces.get(pieces.size() - 1));
        pieces.add(new Rook(0, 7, 1, this));
        board[0][7] = new Spaces(0, 7, pieces.get(pieces.size() - 1));
        
        pieces.add(new Knight(0, 1, 1, this));
        board[0][1] = new Spaces(0, 1, pieces.get(pieces.size() - 1));
        pieces.add(new Knight(0, 6, 1, this));
        board[0][6] = new Spaces(0, 6, pieces.get(pieces.size() - 1));
        
        pieces.add(new Bishop(0, 2, 1, this));
        board[0][2] = new Spaces(0, 2, pieces.get(pieces.size() - 1));
        pieces.add(new Bishop(0, 5, 1, this));
        board[0][5] = new Spaces(0, 5, pieces.get(pieces.size() - 1));
        
        pieces.add(new Queen(0, 3, 1, this));
        board[0][3] = new Spaces(0, 3, pieces.get(pieces.size() - 1));
        
        
        for(int i = 2; i <= 5; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = new Spaces(i, j, true);
            }
        }
    }

    public ChessBoard(ChessBoard otherCBoard){ // creates copy of other chess board
        board = new Spaces[Board.BOARDSIZE][Board.BOARDSIZE];
        pieces = new ArrayList<Piece>();
        for(int i = 0; i < Board.BOARDSIZE; i++){
            for(int j = 0; j < Board.BOARDSIZE; j++){
                board[i][j] = new Spaces(i, j, true);
            }
        }

        ArrayList<Piece> otherPieces = otherCBoard.getPieces();
        for(Piece p : otherPieces){ // creates copies of the pieces
            if(p instanceof King) pieces.add(new King(p.getCoords().getR(), p.getCoords().getC(), p.getColor(), this, p.getID(), ((King)p).canCastle()));
            else if(p instanceof Queen) pieces.add(new Queen(p.getCoords().getR(), p.getCoords().getC(), p.getColor(), this));
            else if(p instanceof Rook) pieces.add(new Rook(p.getCoords().getR(), p.getCoords().getC(), p.getColor(), this, p.getID(), ((Rook)p).canCastle()));
            else if(p instanceof Knight) pieces.add(new Knight(p.getCoords().getR(), p.getCoords().getC(), p.getColor(), this));
            else if(p instanceof Bishop) pieces.add(new Bishop(p.getCoords().getR(), p.getCoords().getC(), p.getColor(), this));
            else if(p instanceof Pawn) pieces.add(new Pawn(p.getCoords().getR(), p.getCoords().getC(), p.getColor(), this, p.getID(), ((Pawn)p).isEnPassantable()));
            board[p.getCoords().getR()][p.getCoords().getC()].setPiece(p);
        }

    }

    public boolean inCheck() { return check; }

    public Spaces[][] getBoard() { return board; }

    public ArrayList<Piece> getPieces() { return pieces; }

    public Coordinates getBKingCoords() { return bKingCoords; }

    public Coordinates getWKingCoords() { return wKingCoords; }

    public Board getMyBoard() { return myBoard; }

    public Piece getPiece(int r, int c){ return board[r][c].getPiece(); }

    public boolean checkEmpty(int r, int c) { return board[r][c].isEmpty(); }

    public void setMyBoard(Board myBoard) {
        this.myBoard = myBoard;
    }

    public void updateBoard(){
        for(int i = 0; i < Board.BOARDSIZE; i++){
            for(int j = 0; j < Board.BOARDSIZE; j++){
                board[i][j].setEmpty();
            }
        }
        for(Piece p : pieces){
            if(p.getCoords().getR() == 24 || p.getCoords().getC() == 24){
                System.out.println(p);
                continue;
            }
            board[p.getCoords().getR()][p.getCoords().getC()].setPiece(p); 
        }
    }

    public void updateEnPassant(int moveColor) {
		Pawn temp;
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i) instanceof Pawn && pieces.get(i).getColor() == moveColor) {
				temp = (Pawn)pieces.get(i);
				temp.notEnPassatable();
			}
		}
	}

    public void remove(Piece piece){
        pieces.remove(piece);
        board[piece.getCoords().getR()][piece.getCoords().getC()].setEmpty();
    }

    private boolean checkStaleMate(){
        if(pieces.size() == 2) return true;
		int wP = 0, bP = 0, wQ = 0, bQ = 0, wR = 0, bR = 0, wN = 0, bN = 0, wB = 0, bB = 0;
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i).getColor() == 0) {
				if(pieces.get(i) instanceof Pawn) wP ++;
				else if(pieces.get(i) instanceof Queen) wQ ++;
				else if(pieces.get(i) instanceof Rook) wR ++;
				else if(pieces.get(i) instanceof Knight) wN ++;
				else if(pieces.get(i) instanceof Bishop) wB ++;
			}
			else {
				if(pieces.get(i) instanceof Pawn) bP ++;
				else if(pieces.get(i) instanceof Queen) bQ ++;
				else if(pieces.get(i) instanceof Rook) bR ++;
				else if(pieces.get(i) instanceof Knight) bN ++;
				else if(pieces.get(i) instanceof Bishop) bB ++;
			}
		}
		return !(wP > 0 || bP > 0 || wQ > 0 || bQ > 0 || wR > 0 || bR > 0 || wN > 1 || bN > 1 || wB > 1 || bB > 1 || (wN > 0 && wB > 0) || (bN > 0 && bB > 0));
    }

    public int checkGameEnd(int movingColor, boolean checking) { // may need to change piece move when calling checkGameEnd
		updateBoard();
        check = checking;
		if(checkStaleMate()) {
			return -1;
		}
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i).getColor() != movingColor) {
				pieces.get(i).setLegalMoves();
				if(pieces.get(i).hasLegalMoves()) {
					return -2;
				}
			}
		}
		if(checking) {
			return movingColor;
		}
		else{
			return -1;
		}
	}

    public boolean discoveredCheck(Piece movingPiece, int newR, int newC) { // creates temp chess board so that it doesnt edit its own actual board
        ChessBoard temp = new ChessBoard(false);
        Spaces[][] tempBoard = temp.getBoard();
		//ArrayList<Piece> pieces = new ArrayList<Piece>();
		for(int i = 0; i < Board.BOARDSIZE; i++) {
			for(int j = 0; j < Board.BOARDSIZE; j++) {
				if(board[i][j].isEmpty() || (newR == i && newC == j)) tempBoard[i][j] = new Spaces(i, j, true);
				else if(!board[i][j].getPiece().equals(movingPiece)) {
					tempBoard[i][j] = new Spaces(i, j, new Piece(i, j, board[i][j].getPiece().getColor(), temp, board[i][j].getPiece().getID()));
					//pieces.add(tempBoard[i][j].getPiece());
				}
				else tempBoard[i][j] = new Spaces(i, j, true);
			}
		}
		tempBoard[newR][newC] = new Spaces(newR, newC, movingPiece);
		//pieces.add(movingPiece);
		// do not need to find new king coords because other king cannot move
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i).getColor() != movingPiece.getColor()) continue;
			if(movingPiece.getColor() == 0 && pieces.get(i).checkCheck(bKingCoords.getR(), bKingCoords.getC(), tempBoard)) return true;
			if(movingPiece.getColor() == 1 && pieces.get(i).checkCheck(wKingCoords.getR(), wKingCoords.getC(), tempBoard)) return true;
		}
		return false;
	}

    public boolean checkChecks(Piece movingPiece, int newR, int newC) {
		boolean isKing = (movingPiece instanceof King);
		int castleDir = -1; // 0 - right, 1 - left
		if(isKing && newC == movingPiece.getCoords().getC() + 2) castleDir = 0;
		else if(isKing && newC == movingPiece.getCoords().getC() - 2) castleDir = 1;
		ChessBoard temp = new ChessBoard(false);
		Spaces[][] tempBoard = temp.getBoard();
		//ArrayList<Piece> pieces = new ArrayList<Piece>();
        Coordinates wKingCoords = null, bKingCoords = null; // may have different king coords if it is the moving piece
		for(int i = 0; i < Board.BOARDSIZE; i++) {
			for(int j = 0; j < Board.BOARDSIZE; j++) {
				if(board[i][j].isEmpty() || (newR == i && newC == j)) tempBoard[i][j] = new Spaces(i, j, true);
				else if(!board[i][j].getPiece().equals(movingPiece)) {
					tempBoard[i][j] = new Spaces(i, j, new Piece(i, j, board[i][j].getPiece().getColor(), temp, board[i][j].getPiece().getID()));
					//pieces.add(tempBoard[i][j].getPiece());
					if(board[i][j].getPiece() instanceof King){
						if(board[i][j].getPiece().getColor() == 0) wKingCoords = board[i][j].getPiece().getCoords();
						else bKingCoords = board[i][j].getPiece().getCoords();
					}
				}
				else tempBoard[i][j] = new Spaces(i, j, true);
			}
		}
		tempBoard[newR][newC] = new Spaces(newR, newC, movingPiece);
		//pieces.add(movingPiece); // TODO copy of moving piece
		if(movingPiece instanceof King){ // switch coords if king is moving piece
			if(movingPiece.getColor() == 0) wKingCoords = movingPiece.getCoords();
			else bKingCoords = movingPiece.getCoords();
		}

		Coordinates newCoords = new Coordinates(newR, newC);
		// pieces are not part of the temp board in check check
		for(int i = pieces.size() - 1; i >= 0; i--) {
			if(pieces.get(i).getColor() != movingPiece.getColor() && !pieces.get(i).getCoords().equals(newCoords)) {
				if(movingPiece.getColor() == 0) {
					if(isKing) {
						if(castleDir == 1 && pieces.get(i).checkCheck(newR, newC + 1, tempBoard)) return true; // checking for castling through check
						if(castleDir == 0 && pieces.get(i).checkCheck(newR, newC - 1, tempBoard)) return true; // checking for castling through check
						if (pieces.get(i).checkCheck(newR, newC, tempBoard)) return true;
					}
					else if(!isKing && pieces.get(i).checkCheck(wKingCoords.getR(), wKingCoords.getC(), tempBoard)) return true;
				}
				else {
					if(isKing) {
						if(castleDir == 1 && pieces.get(i).checkCheck(newR, newC + 1, tempBoard)) return true; // checking for castling through check
						if(castleDir == 0 && pieces.get(i).checkCheck(newR, newC - 1, tempBoard)) return true; // checking for castling through check
						if(pieces.get(i).checkCheck(newR, newC, tempBoard)) return true;
					}
					else if(!isKing && pieces.get(i).checkCheck(bKingCoords.getR(), bKingCoords.getC(), tempBoard)) return true;
				}
			}
		}
		return false;
	}

    public void resetBoard(){
        pieces.clear();

		pieces.add(new Pawn(6, 0, 0, this));
		pieces.add(new Pawn(6, 1, 0, this));
		pieces.add(new Pawn(6, 2, 0, this));
		pieces.add(new Pawn(6, 3, 0, this));
		pieces.add(new Pawn(6, 4, 0, this));
		pieces.add(new Pawn(6, 5, 0, this));
		pieces.add(new Pawn(6, 6, 0, this));
		pieces.add(new Pawn(6, 7, 0, this));
		
		pieces.add(new Rook(7, 0, 0, this));
		pieces.add(new Rook(7, 7, 0, this));
		
		pieces.add(new Knight(7, 1, 0, this));
		pieces.add(new Knight(7, 6, 0, this));
		
		pieces.add(new Bishop(7, 2, 0, this));
		pieces.add(new Bishop(7, 5, 0, this));
		
		pieces.add(new Queen(7, 3, 0, this));
		
		pieces.add(new King(7, 4, 0, this));
		wKingCoords = pieces.get(pieces.size() - 1).getCoords();
		
		pieces.add(new Pawn(1, 0, 1, this));
		pieces.add(new Pawn(1, 1, 1, this));
		pieces.add(new Pawn(1, 2, 1, this));
		pieces.add(new Pawn(1, 3, 1, this));
		pieces.add(new Pawn(1, 4, 1, this));
		pieces.add(new Pawn(1, 5, 1, this));
		pieces.add(new Pawn(1, 6, 1, this));
		pieces.add(new Pawn(1, 7, 1, this));
		
		pieces.add(new Rook(0, 0, 1, this));
		pieces.add(new Rook(0, 7, 1, this));
		
		pieces.add(new Knight(0, 1, 1, this));
		pieces.add(new Knight(0, 6, 1, this));
		
		pieces.add(new Bishop(0, 2, 1, this));
		pieces.add(new Bishop(0, 5, 1, this));
		
		pieces.add(new Queen(0, 3, 1, this));
		
		pieces.add(new King(0, 4, 1, this));
        bKingCoords = pieces.get(pieces.size() - 1).getCoords();
    }

    public void setBoard(Spaces[][] newBoard){
        pieces.clear();
		for(int i = 0; i < Board.BOARDSIZE; i++) {
			for(int j = 0; j < Board.BOARDSIZE; j++) {
				if(!board[i][j].isEmpty()){
					if(board[i][j].getPiece() instanceof Queen) pieces.add(new Queen(i, j, board[i][j].getPiece().getColor(), this, board[i][j].getPiece().getID()));
					else if(board[i][j].getPiece() instanceof Rook) pieces.add(new Rook(i, j, board[i][j].getPiece().getColor(), this, board[i][j].getPiece().getID(), ((Rook)board[i][j].getPiece()).canCastle()));
					else if(board[i][j].getPiece() instanceof King) {
						pieces.add(new King(i, j, board[i][j].getPiece().getColor(), this, board[i][j].getPiece().getID(), ((King)board[i][j].getPiece()).canCastle()));
						if(board[i][j].getPiece().getColor() == 0){
							wKingCoords = pieces.get(pieces.size() - 1).getCoords();
						}
						else{
							bKingCoords = pieces.get(pieces.size() - 1).getCoords();	
						}
					}
					else if(board[i][j].getPiece() instanceof Bishop) pieces.add(new Bishop(i, j, board[i][j].getPiece().getColor(), this, board[i][j].getPiece().getID()));
					else if(board[i][j].getPiece() instanceof Knight) pieces.add(new Knight(i, j, board[i][j].getPiece().getColor(), this, board[i][j].getPiece().getID()));
					else  pieces.add(new Pawn(i, j, board[i][j].getPiece().getColor(), this, board[i][j].getPiece().getID(), ((Pawn)board[i][j].getPiece()).isEnPassantable()));
				}
			}
		}
    }

    public void setPieces(ArrayList<Piece> newPieces, Coordinates wKingCoords, Coordinates bKingCoords){
        this.pieces = newPieces;
        this.wKingCoords = wKingCoords;
        this.bKingCoords = bKingCoords;
        System.out.println(pieces);
    }
    
}
