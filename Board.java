import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to handle everything involving the chess board. It will keep track of 
 * all of the pieces and the graphics of the board and handle inputting of moves, promotion, 
 * and any functionality associated with all of the pieces
 */
public class Board implements MouseListener, MouseMotionListener, ActionListener{

	public static final int SQUARESIZE = 80, BOARDSIZE = 8; // constants of the size of each square when drawn and the length and width of the board respectively
	private Spaces[][] board; // an 8x8 2D-array of the spaces of the board
	private JButton[] promoteSelection; // an array of the JButtons to be clicked on when promoting
	private ArrayList<Piece> pieces; // an ArrayList of all of the pieces on the board
	private ArrayList<JLabel> legalMoves; // an ArrayList of JLabels that will be used as the dots/indicators of the legal moves for the selected piece
	private JLabel draggedPieceLabel; // the JLabel to show the piece being dragged
	private boolean pieceSelected, pieceDragged, wasDragged, stoppedDrag, click, promoting, onPreviousMove, promoteCheck, promoteCapture, promoteEnd, drawByRep;
	/*
	 * pieceSelected - whether or not a piece is selected
	 * pieceDragged - whether or not a piece is being dragged
	 * wasDragged - whether or not a piece was being dragged before the mouse was released (used to determine whether or not to deselect a piece),
	 * stoppedDrag - whether or not the dragging was stopped
	 * click - whether or not a click occurred (used to determine if it is a click or a drag)
	 * promoting - whether or not a promotion is happening
	 * onPreviousMove - whether or not the move being viewed the most recent move or a previous move
	 * promoteCheck - whether or not the promotion resulted in a check
	 * promoteCapture - whether or not a piece was captured during the prmotion
	 * promoteEnd - whether or not the promotion led to the game ending
	 * drawByRep - whether or not the draw was due to repetition
	 */
	private SavedMove previousMove; // the previous move being viewed
	private Piece selectedPiece, draggedPiece, promotingPiece;
	/*
	 * selectedPiece - the piece selected
	 * draggedPiece - the piece being dragged
	 * promotingPiece - the pawn that is promoting
	 */
	private int promotingColor; // the color of the pawn that is promoting
	private double dragX, dragY; // the coordinates of where to display the dragged piece
	private Notations notations; // reference to the Notations object connected to this board
	private String promoteNotation; // the string for the promote notation
	private JLabel whoseMove, promoteText, promoteText2; // JLabels to show whose move it is and to give directions when promoting
	private JLayeredPane lPane; // the layered pane used to hold all the JComponents in the JFrame (layered pane to allow multiple components on top of each other)
	private JFrame frame; // the JFrame for this board
	private Timer timer; // timer used to time how long a piece is pressed for to determine if it is a click of a drag
	private int myColor; // the color of this board's coordinator (used to determine which pieces can be cliked on)
	private boolean justMadeMove = false; // whether or not a move was just made (for sending messages to other coordinator)
	private int receivedStartR; // the starting row of the piece moving received from other coordinator
	private int receivedStartC; // the starting column of the piece moving received from the other coordinator
	private int receivedendR; // the ending row of the piece moving received from the other coordinator
	private int receivedendC; // the ending column of the piece moving received from the other coordinator
	private String promote = ""; // the string indicating which type of piece the pawn promoted to 
	private Coordinator myCoordinator; // the Coordinator object connected to this board
	private SoundPlayer soundPlayer; // a SoundPlayer class for playing sounds
	private ChessBoard chessBoard;
	private boolean multiplayer = true;
	
	public static final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"}, numbers = {"1", "2", "3", "4", "5", "6", "7", "8"}; // constant arrays for the numbers/letters used in notations on the side of the board
	public static final String lettersStr = "abcdefgh"; // string constant of the letters (used for indexOf)
	// letters are the columns, numbers are the rows

	/**
	 *
	 * @param notations - reference to the Notations object connected to this board
	 * @param frame - JFrame of this board
	 * @param lPane - layeredPane of the JFrame
	 * @param color - color of this board's Coordinator
	 * 
	 * initializes all of the necessary fields above, including creating all of the pieces/spaces
	 */
	public Board(Notations notations, JFrame frame, JLayeredPane lPane, int color) {
		// initialize things
		myColor = color;
		this.lPane = lPane;
		this.frame = frame;
		this.notations = notations;
		notations.setChessBoard(this);
		//board = new Spaces[BOARDSIZE][BOARDSIZE];
		pieces = new ArrayList<Piece>();
		legalMoves = new ArrayList<JLabel>();
		chessBoard = new ChessBoard(this);
		board = chessBoard.getBoard();
		pieces = chessBoard.getPieces();

		// create buttons for each spot
		JButton newButton = new JButton();
		for(int i = 0; i < BOARDSIZE; i++){
			for(int j = 0; j < BOARDSIZE; j++){
				if(board[i][j].isEmpty()) newButton = new JButton("");
				else newButton = new JButton(board[i][j].getPiece().getImage());

				if(i % 2 != j % 2) newButton.setBackground(Color.decode("#C78E53"));
				else newButton.setBackground(Color.decode("#F7D0A4"));

				newButton.setBounds((j + 1) * SQUARESIZE, (i + 1) * SQUARESIZE, SQUARESIZE, SQUARESIZE);
				newButton.setOpaque(true);
				newButton.setBorder(null);
				newButton.setBorderPainted(false);
				newButton.addActionListener(this);
				newButton.setActionCommand("button: " + i + " " + j);
				newButton.setFocusable(false);
				newButton.addMouseListener(this);
				newButton.addMouseMotionListener(this);
				board[i][j].setButton(newButton);
				lPane.add(newButton);
			}
		}

		// create labels for the markings on the side
		JLabel newLabel;
		for(int i = 0; i < letters.length; i++){
			newLabel = new JLabel(letters[i], SwingConstants.CENTER);
			newLabel.setFont(new Font("SanSerif", 15, 15));
			newLabel.setBounds(i * SQUARESIZE + SQUARESIZE, 0, SQUARESIZE, SQUARESIZE);
			lPane.add(newLabel);
		}
		for(int i = 0; i < numbers.length; i++){
			newLabel = new JLabel(numbers[numbers.length - i - 1], SwingConstants.CENTER);
			newLabel.setFont(new Font("SanSerif", 15, 15));
			newLabel.setBounds(0, i * SQUARESIZE + SQUARESIZE, SQUARESIZE, SQUARESIZE);
			lPane.add(newLabel);
		}

		// create label for dragged piece
		draggedPieceLabel = new JLabel();
		draggedPieceLabel.setOpaque(true);
		draggedPieceLabel.setBorder(null);
		draggedPieceLabel.setBackground(new Color(0, 0, 0, 0));
		lPane.add(draggedPieceLabel, javax.swing.JLayeredPane.PALETTE_LAYER);

		// create label for displaying whose move/who won
		whoseMove = new JLabel("White Move", SwingConstants.CENTER);
		lPane.add(whoseMove);
		whoseMove.setFont(new Font("SanSerif", 50, 30));
		whoseMove.setBounds(725, 50, 200, 50);

		// create buttons for promotion
		promoteSelection = new JButton[4];
		for(int i = 0; i < 4; i++){
			newButton = new JButton("");
			//lPane.add(newButton);
			newButton.setBounds(780, 170 + i * SQUARESIZE, SQUARESIZE, SQUARESIZE);
			newButton.addActionListener(this);
			newButton.setFocusable(false);
			promoteSelection[i] = newButton;
		}

		promoteSelection[0].setActionCommand("promote Queen");
		promoteSelection[1].setActionCommand("promote Rook");
		promoteSelection[2].setActionCommand("promote Bishop");
		promoteSelection[3].setActionCommand("promote Knight");

		promoteText = new JLabel("");
		promoteText.setBounds(760, 100, 150, 30);
		promoteText.setFont(new Font("SanSerif", 30, 20));
		lPane.add(promoteText);

		promoteText2 = new JLabel("");
		promoteText2.setBounds(775, 125, 100, 30);
		promoteText2.setFont(new Font("SanSerif", 30, 20));
		lPane.add(promoteText2);

		// create sound player
		soundPlayer = new SoundPlayer();
		soundPlayer.playGameStart();
	}
	
	/**
	 * method used to update the JFrame
	 * - updates the whoseMove JLabel to either show the move or the game result
	 * - adds the dots/indicators for the legal moves of the selected piece
	 * - draws the dragged piece at the drag coordinates
	 * - draws promoting buttons
	 */
	public void draw() {
		if(!onPreviousMove){
			if(Coordinator.winner == -2){
				if(Coordinator.turn == 0) whoseMove.setText("White Move");
				else whoseMove.setText("Black Move");
			}
			else if(Coordinator.winner == -1){
				if(drawByRep){
					whoseMove.setText("Draw by rep.");
				}
				else{
					whoseMove.setText("Draw");
				}
				
			}
			else if(Coordinator.winner == 0){
				whoseMove.setText("White Wins");
			}
			else{
				whoseMove.setText("Black wins");
			}

			
		}
		else{
			whoseMove.setText("");
		}

		while(!legalMoves.isEmpty()){
			lPane.remove(legalMoves.remove(0));
		}
		draggedPieceLabel.setIcon(null);
		
		if(pieceSelected && !onPreviousMove && !promoting){
			
			for(Coordinates c : selectedPiece.getLegalMoves()){
				JLabel move = new JLabel(new ImageIcon((new ImageIcon("Images_and_Sounds/pp.png")).getImage().getScaledInstance(SQUARESIZE / 3, SQUARESIZE / 3, java.awt.Image.SCALE_SMOOTH)), SwingConstants.CENTER);
				lPane.add(move, javax.swing.JLayeredPane.MODAL_LAYER);
				move.setBounds((c.getC() + 1) * SQUARESIZE + SQUARESIZE / 3, (c.getR() + 1) * SQUARESIZE + SQUARESIZE / 3, SQUARESIZE / 3, SQUARESIZE / 3);
				move.setOpaque(true);
				move.setBackground(new Color(0, 0, 0, 0));
				legalMoves.add(move);
				
			}

			if(pieceDragged){
				draggedPieceLabel.setBounds((int)dragX, (int)dragY, SQUARESIZE, SQUARESIZE);
				draggedPieceLabel.setIcon(draggedPiece.getImage());
				board[selectedPiece.getCoords().getR()][selectedPiece.getCoords().getC()].setImage(null);;
			}
			else{
				draggedPieceLabel.setIcon(null);
				board[selectedPiece.getCoords().getR()][selectedPiece.getCoords().getC()].setImage(selectedPiece.getImage());
			}
		}
		else{
			draggedPieceLabel.setIcon(null);
		}

		if(!onPreviousMove && promoting){
			promoteText.setText("Select piece to");
			promoteText2.setText("promote to");
			if(lPane.getComponentsInLayer(JLayeredPane.DRAG_LAYER).length == 0){
				for(int i = 0; i < 4; i++){
					lPane.add(promoteSelection[i], JLayeredPane.DRAG_LAYER);
				}
			}
		}
		else{
			promoteText.setText("");
			promoteText2.setText("");
			if(lPane.getComponentsInLayer(JLayeredPane.DRAG_LAYER).length != 0){
				for(int i = 0; i < 4; i++){
					lPane.remove(promoteSelection[i]);
				}
			}
		}
		frame.repaint();
		frame.validate();
	}

	/**
	 * 
	 * @param startR - starting row of the piece to move
	 * @param startC - starting column of the piece to move
	 * @param endR - ending row of the piece to move
	 * @param endC - ending column of the piece to move
	 * 
	 * method used to move a piece based on information sent from other coordinator
	 */
	public void makePieceMove(int startR, int startC, int endR, int endC) {
		if(notations.getSize() >= 0) notations.setSelected(notations.getSize());
		Piece piece = board[startR][startC].getPiece();
		piece.move(endR, endC);
		updateBoard();
	}

	/**
	 * 
	 * @param startR - starting row of the piece to move
	 * @param startC - starting column of the piece to move
	 * @param endR - ending row of the piece to move
	 * @param endC - ending column of the piece to move
	 * @param promote - the string of the type of piece that the pawn promoted to
	 * 
	 * method used to move and promote a pawn based on information sent from other coordinator
	 */
	public void makePieceMove(int startR, int startC, int endR, int endC, String promote) {
		if(notations.getSize() >= 0) notations.setSelected(notations.getSize());
		Piece piece = board[startR][startC].getPiece();
		((Pawn)piece).move(endR, endC, promote);
		updateBoard();
	}
	
	/**
	 * updates the board based on the position of the pieces in the pieces ArrayList
	 * used because the pieces do not have direct access to edit the board 2D-array, the pieces
	 * can only edit their own coords
	 * draws the updated board too
	 */
	public void updateBoard() {
		if(onPreviousMove){
			Spaces[][] previousBoard = previousMove.getBoard();
			for(int i = 0; i < previousBoard.length; i++){
				for(int j = 0; j < previousBoard[0].length; j++){
					if(previousBoard[i][j].isEmpty()) board[i][j].setEmpty();
					else board[i][j].setPiece(previousBoard[i][j].getPiece());
				}
			}
		}
		else{
			chessBoard.updateBoard();
		}
		draw();
	}
	
	/**
	 * 
	 * @param promotingPiece - pawn that is promoting
	 * @param color - color of the promoting piece
	 * @param promotingText - string that is the beginning of the notation of the promotion (will be added to)
	 * @param captured - whether or not a piece was captured during the promotion
	 * 
	 * starts the process of promotion by changing fields involved with promotion
	 */
	public void promote(Piece promotingPiece, int color, String promotingText, boolean captured) {
		this.promotingPiece = promotingPiece;
		promotingColor = color;
		promoting = true;
		promoteNotation = promotingText;
		promoteCheck = false;
		promoteEnd = false;
		promoteCapture = captured;

		String c = "";
		if(color == 0){
			c = "White";
		}
		else{
			c = "Black";
		}
		promoteSelection[0].setIcon(new ImageIcon("Images_and_Sounds/" + c + "_Queen.png"));
		promoteSelection[1].setIcon(new ImageIcon("Images_and_Sounds/" + c + "_Rook.png"));
		promoteSelection[2].setIcon(new ImageIcon("Images_and_Sounds/" + c + "_Bishop.png"));
		promoteSelection[3].setIcon(new ImageIcon("Images_and_Sounds/" + c + "_Knight.png"));
	}

	/**
	 * 
	 * @param promotingPiece - pawn that is promoting
	 * @param color - color of the promoting piece
	 * @param promotingText - string that is the beginning of the notation of the promotion (will be added to)
	 * @param promote - the string of the type of piece to promote to
	 * @param captured - whether or not a piece was captured during the promotion
	 * 
	 * automatically promotes the pawn to a piece based on the promote string
	 */
	public void promote(Piece promotingPiece, int color, String promotingText, String promote, boolean captured){
		Piece newPiece = null;
		if(promote.equals("Q")){
			newPiece = new Queen(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
		}
		else if(promote.equals("R")){
			newPiece = new Rook(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
		}
		else if(promote.equals("B")){
			newPiece = new Bishop(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
		}
		else{
			newPiece = new Knight(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
		}
		pieces.add(newPiece);
		pieces.remove(promotingPiece);

		promoting = false;
		if((newPiece.getColor() == 0 && newPiece.checkCheck(chessBoard.getBKingCoords().getR(), chessBoard.getBKingCoords().getC(), chessBoard.getBoard())) ||
			(newPiece.getColor() == 1 && newPiece.checkCheck(chessBoard.getWKingCoords().getR(), chessBoard.getWKingCoords().getC(), chessBoard.getBoard()))) {
			promoteCheck = true;
		}
		promoteEnd = checkGameEnd(promotingColor, promoteCheck);
		promoteNotation = promotingText;
		promotingColor = color;
		promoteCapture = captured;
		finishPromoteNotation(promote, newPiece.getColor() == 0? newPiece.getCoords().getR() + 1:newPiece.getCoords().getR() - 1, newPiece.getCoords().getC(), newPiece.getCoords().getR(), newPiece.getCoords().getC());
		Coordinator.turn = 1 - Coordinator.turn;
		updateBoard();
	}

	/**
	 * 
	 * @param piece - type of piece promoted to
	 * @param oldR - the old row of the promoting piece
	 * @param oldC - the old column of the promoting piece
	 * @param newR - the new row of the promoting piece
	 * @param newC - the new column of the promoting piece
	 * 
	 * finishes the promote notation string by adding to the promoteNotation string and
	 * then creates the notation
	 */
	private void finishPromoteNotation(String piece, int oldR, int oldC, int newR, int newC) {
		promoteNotation += piece;
		String sound;
		if(promoteCheck) {
			if(promoteEnd) promoteNotation += "#";
			else promoteNotation += "+";
		}
		else if(promoteEnd) {
			promoteNotation += " 1/2-1/2";
		}
		if(promoteCheck){
			sound = SoundPlayer.check;
		}
		else if(promoteCapture){
			sound = SoundPlayer.capture;
		}
		else{
			sound = SoundPlayer.promote;
		}
		addNotation(promoteNotation, promotingColor, oldR, oldC, newR, newC, piece, sound);
	}
	
	/**
	 * 
	 * @param movingColor - which color is going to move
	 * @param checking - whether or not a king is in check
	 * @return whether or not the game ended
	 * 
	 * checks if the game ended and updates Coordinator.winner accordingly 
	 * checks each piece's legal moves to see if a move is possible
	 * -1 = draw, 0 = white win, 1 = black win
	 */
	public boolean checkGameEnd(int movingColor, boolean checking) {
		updateBoard();
		if(notations.checkRepetition()){
			drawByRep = true;
			Coordinator.winner = -1;
			return true;
		}
		int winner = chessBoard.checkGameEnd(movingColor, checking);
		Coordinator.winner = winner;
		return winner != -2;
	}
	
	/**
	 * 
	 * @param text - the text of the notation
	 * @param color - the color of the piece that moved 
	 * @param oldR - the old row of the moving piece
	 * @param oldC - the old column of the moving piece
	 * @param newR - the new row of the moving piece
	 * @param newC - the new column of the moving piece
	 * @param promote - the string of the type of piece promoted to
	 * @param sound - the path to the sound that was made
	 * 
	 * creates a new saved move for the Notations class of this board using 
	 * the information above
	 */
	public void addNotation(String text, int color, int oldR, int oldC, int newR, int newC, String promote, String sound) {
		notations.newMove(text, color, oldR, oldC, newR, newC, promote, sound);
	}
	
	/**
	 * @param c - color to set to
	 * 
	 * sets the color of the board
	 */
	public void setColor(int c){
		myColor = c;
	}

	public void setSingleplayer() { multiplayer = false; }
	public boolean isMultiplayer() { return multiplayer; }

	/**
	 * @return the Notations object for this board
	 */
	public Notations getNotations() { return notations; }


	public ChessBoard getChessBoard() { return chessBoard; }

	public Coordinator getCoordinator(){ return myCoordinator; }


	/**
	 * @return the SoundPlayer object for this board
	 */
	public SoundPlayer getSoundPlayer(){ return soundPlayer; }
	
	/**
	 * @return whether or not a promotion is going on
	 */
	public boolean isPromoting() { return promoting; }
	
	/**
	 * @param previousMove - the previous SavedMove that is being viewed
	 * 
	 * indicates that the move being viewed is not the most recent move
	 * (no clicking actions will do anything)
	 */
	public void onPreviousMove(SavedMove previousMove) { 
		onPreviousMove = true;
		this.previousMove = previousMove;
	}
	/**
	 * indicates that the move being viewed is the most recent move 
	 * (clicking actions will do stuff)
	 */
	public void currentMove() { 
		onPreviousMove = false;
		previousMove = null;
	}

	/**
	 * completely resets the board with a whole new set of pieces
	 * also resets the notations 
	 */
	public void resetBoard(boolean send){
		Coordinator.turn = 0;
		Coordinator.winner = -2;
		notations.reset();
		chessBoard.resetBoard();
		updateBoard();
		draw();
		if(send && myCoordinator != null && myCoordinator.getColor() != 2) myCoordinator.sendReset();
	}
	
	/**
	 * 
	 * @param savedMove - SavedMove object to save this board to
	 * 
	 * creates a 2D Space Array that is a copy of this board's 2D Spaces Array
	 * and saves it to the given SavedMove object
	 */
	public void saveBoard(SavedMove savedMove) {
		ChessBoard temp = new ChessBoard(chessBoard);
		savedMove.setBoard(temp.getBoard());
	}

	/**
	 * 
	 * @param newBoard - board to copy
	 * 
	 * copies a board onto this board's 2D Spaces Array
	 */
	public void setBoard(Spaces[][] newBoard){
		chessBoard.setBoard(newBoard);
		currentMove();
		updateBoard();
	}
	
	/**
	 * deselects the selected piece 
	 */
	public void deselect(){
	    selectedPiece = null;
		pieceSelected = false; 
		draggedPiece = null;
		pieceDragged = false;
	}

	public void addCFL(ChatFocusListener cfl){
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++){
				board[i][j].getButton().addMouseListener(cfl);
			}
		}
		for(int i = 0; i < promoteSelection.length; i++){
			promoteSelection[i].addMouseListener(cfl);
		}
	}

	// send the game state immediately after having it for yourself
	// send the moves through move (normal)
	public boolean load(File file){
		selectedPiece = null;
		pieceSelected = false; 
		draggedPiece = null;
		pieceDragged = false;
		try {
			if(!checkValid(new Scanner(file))) {
				System.out.println("failed validation");
				return false;
			}
			// verified - can now directly edit board
			Scanner scanner = new Scanner(file);
			String header = scanner.nextLine();
			if(header.equals("new game state")){
				String line = "";
				char color, piece, letter, condition;
				boolean con;
				int row, col, c;
				Coordinates wKCoords = null, bKCoords = null;
				ArrayList<Piece> loadPieces = new ArrayList<Piece>();
				String turnColor = scanner.nextLine().trim();
				String piecesStr = turnColor + "\n";
				while(scanner.hasNextLine()){
					line = scanner.nextLine().trim();
					if(line.equals("end game state")) break;
					piecesStr += line + "\n";
					color = line.charAt(0);
					piece = line.charAt(1);
					letter = line.charAt(2);
					row = 8 - (line.charAt(3) - '0');
					col = lettersStr.indexOf(letter);
					c = (color == 'W') ? 0 : 1;
					if(piece == 'K'){
						condition = line.charAt(5);
						con = (condition == 'T');
						King temp = new King(row, col, c, chessBoard, Piece.nextId++, con);
						loadPieces.add(temp);
						if(c == 0){
							wKCoords = temp.getCoords();
						}
						else{
							bKCoords = temp.getCoords();
						}
					}
					else if(piece == 'Q'){
						loadPieces.add(new Queen(row, col, c, chessBoard));
					}
					else if(piece == 'R'){
						condition = line.charAt(5);
						con = (condition == 'T');
						loadPieces.add(new Rook(row, col, c, chessBoard, Piece.nextId++, con));
					}
					else if(piece == 'N'){
						loadPieces.add(new Knight(row, col, c, chessBoard));
					}
					else if(piece == 'B'){
						loadPieces.add(new Bishop(row, col, c, chessBoard));
					}
					else{
						condition = line.charAt(5);
						con = (condition == 'T');
						loadPieces.add(new Pawn(row, col, c, chessBoard, Piece.nextId++, con));
					}
				}
				chessBoard.setPieces(loadPieces, wKCoords, bKCoords);
				this.pieces = chessBoard.getPieces();
				if(turnColor.equals("W")){
					Coordinator.turn = 0;
				}
				else if(turnColor.equals("B")){
					Coordinator.turn = 1;
				}
				Piece checkPiece = null;
				for(Piece p : pieces){
					if(p.getColor() == Coordinator.turn){
						checkPiece = p;
						break;
					}
				}
				updateBoard();
				Coordinator.winner = -2;
				boolean check = chessBoard.checkChecks(checkPiece, checkPiece.getCoords().getR(), checkPiece.getCoords().getC());
				checkGameEnd(1 - Coordinator.turn, check);
				//game end check
				notations.reset();
				notations.setNewGameState(piecesStr);
				draw();
				if(myCoordinator != null && myCoordinator.getColor() != 2) myCoordinator.sendGameState(piecesStr); // send game state
			}
			else{
				System.out.println("moves only");
				resetBoard(true);
			}
			
			// load moves

			Thread thread = new Thread(new Runnable(){

				@Override
				public void run() {
					String line = "", promote = "";
					int oldR, oldC, newR, newC;
					int moves = 0;
					while(scanner.hasNextLine()){
						line = scanner.nextLine();
						oldR = line.charAt(0) - '0';
						oldC = line.charAt(2) - '0';
						newR = line.charAt(4) - '0';
						newC = line.charAt(6) - '0';
						moves ++;
						System.out.println(moves);
						if(line.length() > 7){
							promote = line.substring(8);
							((Pawn)(board[oldR][oldC].getPiece())).move(newR, newC, promote);
							Thread thread1 = new Thread( new Runnable(){
								@Override
								public void run(){
									updateBoard();
								}
							});
							thread1.start();
							try {
								thread1.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(myCoordinator != null && myCoordinator.getColor() != 2) myCoordinator.sendMove("*" + oldR + oldC + newR + newC + promote); // send moves
						}
						else{
							board[oldR][oldC].getPiece().move(newR, newC);
							Thread thread1 = new Thread(){
								public void run(){
									updateBoard();
								}
							};
							thread1.start();
							try {
								thread1.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(myCoordinator != null && myCoordinator.getColor() != 2) myCoordinator.sendMove("*" + oldR + oldC + newR + newC); // send moves
						}
					}
					Coordinator.turn = (Coordinator.turn == 0) ? moves % 2 : 1 - (moves % 2);
					scanner.close();
				}
			});
			thread.start();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @param scanner
	 * @return
	 * 
	 * creates temporary board to test moves
	 */
	private boolean checkValid(Scanner scanner){
		if(!scanner.hasNextLine()) return false;
		String header = scanner.nextLine();
		ChessBoard temp = null;
		System.out.println(header);
		if(header.equals("new game state")){
			// check game state for validity
			temp = new ChessBoard(false);
			ArrayList<Piece> pieces = checkGameState(scanner, temp);
			if(pieces == null) return false;
			Coordinates bKingCoords = null, wKingCoords = null;
			for(Piece p : pieces){
				if(p instanceof King){
					if(p.getColor() == 0) wKingCoords = p.getCoords();
					else bKingCoords = p.getCoords();
				}
			}
			temp.setPieces(pieces, wKingCoords, bKingCoords);
			temp.updateBoard();
		}
		else if(header.equals("moves only")){
			temp = new ChessBoard(true);
		}
		else{
			return false;
		}

		// check moves for validity
		String line = "", promote = "";
		int oldR, oldC, newR, newC;
		int moveColor = 0;
		Spaces[][] tempBoard = temp.getBoard();
		while(scanner.hasNextLine()){
			line = scanner.nextLine();
			oldR = line.charAt(0) - '0';
			oldC = line.charAt(2) - '0';
			newR = line.charAt(4) - '0';
			newC = line.charAt(6) - '0';
			System.out.println(oldR + " " + oldC + " " + newR + " " + newC);
			if(!(oldR >= 0 && oldR < 8 && oldC >= 0 && oldC < 8 && newR >= 0 && newR < 8 && newC >= 0 && newC < 8) || 
				tempBoard[oldR][oldC].isEmpty() || tempBoard[oldR][oldC].getPiece().getColor() != moveColor) {
					System.out.println("invalid " + tempBoard[oldR][oldC].isEmpty());
					return false;
				}
			tempBoard[oldR][oldC].getPiece().setLegalMoves();
			
			if(!tempBoard[oldR][oldC].getPiece().isLegalMove(new Coordinates(newR, newC))) {
				System.out.println("illegal " + tempBoard[oldR][oldC].getPiece());
				return false;
			}
			if(line.length() > 7){
				promote = line.substring(8);
				
				Piece newPiece = null;
				int color = tempBoard[oldR][oldC].getPiece().getColor();
				if(promote.equals("Q")){
					newPiece = new Queen(newR, newC, color, null);
				}
				else if(promote.equals("R")){
					newPiece = new Rook(newR, newC, color, null, -1, false);
				}
				else if(promote.equals("N")){
					newPiece = new Knight(newR, newC, color, null);
				}
				else if(promote.equals("B")){
					newPiece = new Bishop(newR, newC, color, null);
				}
				else{
					System.out.println("invalid promotion");
					return false;
				}
				tempBoard[newR][newC].getPiece().setCoords(new Coordinates(newR, newC));
				tempBoard[oldR][oldC].setEmpty();
				tempBoard[newR][newC].setPiece(newPiece);
				
				// use move instead
			}
			else{
				
				tempBoard[oldR][oldC].getPiece().move(newR, newC);
				tempBoard[newR][newC].setPiece(tempBoard[oldR][oldC].getPiece());
				tempBoard[oldR][oldC].setEmpty();
				if (tempBoard[newR][newC].getPiece() instanceof King && Math.abs(oldC - newC) == 2) {
					int oldRookR = newR;
					int oldRookC = (newC == 6) ? 7 : 0;
					Piece rook = tempBoard[oldRookR][oldRookC].getPiece();
					tempBoard[oldRookR][oldRookC].setEmpty();
					tempBoard[rook.getCoords().getR()][rook.getCoords().getC()].setPiece(rook);
				}
				
			}
			moveColor = 1 - moveColor;
		}
		return true;
		
	}

	/**
	 * 
	 * @param scanner
	 * @return
	 * 
	 * checks game state for validity then returns an arraylist of all of the pieces
	 */

	private ArrayList<Piece> checkGameState(Scanner scanner, ChessBoard temp){
		String line = "";
		char color, piece, letter, condition;
		boolean con;
		int row, col, c;
		int[] numPieces = {0, 0};
		int[] numKings = {0, 0};
		int[] numPawns = {0, 0};
		ArrayList<Coordinates> coords = new ArrayList<Coordinates>();
		ArrayList<Piece> loadPieces = new ArrayList<Piece>();
		if(!scanner.hasNext()) {
			System.out.println("Invalid file - empty file");
			scanner.close();
			return null;
		}
		String turnColor = scanner.nextLine().trim();
		System.out.println(turnColor);
		if(!turnColor.equals("W") && !turnColor.equals("B")){
			System.out.println("Invalid file - invalid turn color");
			scanner.close();
			return null;
		}
		if(!scanner.hasNext()){
			System.out.println("Invalid file - no pieces");
			scanner.close();
			return null;
		}
		while(scanner.hasNextLine()){
			line = scanner.nextLine().trim();
			if(line.equals("end game state")) break;
			if((line.length() != 4 && line.length() != 6) || 
				(line.charAt(0) != 'B' && line.charAt(0) != 'W') || 
				!Character.isDigit(line.charAt(3))
				){
				System.out.println("Invalid file - invalid format");
				scanner.close();
				return null;
			}
			color = line.charAt(0);
			piece = line.charAt(1);
			letter = line.charAt(2);
			row = 8 - (line.charAt(3) - '0');
			col = lettersStr.indexOf(letter);
			if(lettersStr.indexOf(letter) == -1 || !(0 <= row && row < 8) || !(0 <= col && col < 8)){
				System.out.println("Invalid file - invalid coordinate");
				scanner.close();
				return null;
			}
			c = (color == 'W') ? 0 : 1;
			if(piece == 'K'){
				if(line.length() == 6){
					condition = line.charAt(5);
					con = (condition == 'T');
					if(con && ((c == 0 && (row != 7 || col != 4)) || (c == 1 && (row != 0 || col != 4)))){
						System.out.println(letter + " " + row + " " + col);
						System.out.println("Invalid file - king condition does not match");
						scanner.close();
						return null;
					}
				}
				else{
					System.out.println("Invalid file - missing condition");
					scanner.close();
					return null;
				}
				King tempK = new King(row, col, c, temp, -1, con);
				loadPieces.add(tempK);
				numKings[c]++;
				if(numKings[c] > 1){
					System.out.println("Invalid file - too many kings");
				}
			}
			else if(piece == 'Q'){
				loadPieces.add(new Queen(row, col, c, temp));
			}
			else if(piece == 'R'){
				if(line.length() == 6){
					condition = line.charAt(5);
					con = (condition == 'T');
					if(con && ((c == 0 && (row != 7 || (col != 0 && col != 7))) || (c == 1 && (row != 0 || (col != 0 && col != 7))))){
						System.out.println("Invalid file - rook condition does not match");
						scanner.close();
						return null;
					}
				}
				else{
					System.out.println("Invalid file - missing condition");
					scanner.close();
					return null;
				}
				loadPieces.add(new Rook(row, col, c, temp, -1, con));
			}
			else if(piece == 'N'){
				loadPieces.add(new Knight(row, col, c, temp));
			}
			else if(piece == 'B'){
				loadPieces.add(new Bishop(row, col, c, temp));
			}
			else if(piece == 'P'){
				if(line.length() == 6){
					condition = line.charAt(5);
					con = (condition == 'T');
					if(con && ((c == 0 && row != 4) || (c == 1 && row != 3))){
						System.out.println("Invalid file - pawn condition does not match");
						scanner.close();
						return null;
					}
				}
				else{
					System.out.println("Invalid file - missing condition");
					scanner.close();
					return null;
				}
				loadPieces.add(new Pawn(row, col, c, temp, -1, con));
				numPawns[c]++;
				if(numPawns[c] > 8){
					System.out.println("Invalid file - too many pawns");
				}
			}
			else{
				System.out.println("Invalid file - invalid piece");
				scanner.close();
				return null;
			}
			Coordinates newCoords = loadPieces.get(loadPieces.size() - 1).getCoords();
			if(coords.contains(newCoords)){
				System.out.println("Invalid file - multiple pieces in same location");
				scanner.close();
				return null;
			}
			coords.add(newCoords);
			numPieces[c]++;
			if(numPieces[c] > 16){
				if(c == 0) System.out.println("Invalid file - too many white pieces");
				else System.out.println("Invalid file - too many black pieces");
				scanner.close();
				return null;
			}
		}
		if(numKings[0] == 0 || numKings[1] == 0){
			System.out.println("Invalid file - missing kings");
				scanner.close();
				return null;
		}
		return loadPieces;
	}

	public void loadGameState(Scanner scanner){
		String line = "";
		char color, piece, letter, condition;
		boolean con;
		int row, col, c;
		Coordinates wKCoords = null, bKCoords = null;
		ArrayList<Piece> loadPieces = new ArrayList<Piece>();
		String turnColor = scanner.nextLine().trim();
		String piecesStr = turnColor + "\n";
		while(scanner.hasNextLine()){
			line = scanner.nextLine().trim();
			piecesStr += line + "\n";
			color = line.charAt(0);
			piece = line.charAt(1);
			letter = line.charAt(2);
			row = 8 - (line.charAt(3) - '0');
			col = lettersStr.indexOf(letter);
			c = (color == 'W') ? 0 : 1;
			if(piece == 'K'){
				condition = line.charAt(5);
				con = (condition == 'T');
				King temp = new King(row, col, c, chessBoard, Piece.nextId++, con);
				loadPieces.add(temp);
				if(c == 0){
					wKCoords = temp.getCoords();
				}
				else{
					bKCoords = temp.getCoords();
				}
			}
			else if(piece == 'Q'){
				loadPieces.add(new Queen(row, col, c, chessBoard));
			}
			else if(piece == 'R'){
				condition = line.charAt(5);
				con = (condition == 'T');
				loadPieces.add(new Rook(row, col, c, chessBoard, Piece.nextId++, con));
			}
			else if(piece == 'N'){
				loadPieces.add(new Knight(row, col, c, chessBoard));
			}
			else if(piece == 'B'){
				loadPieces.add(new Bishop(row, col, c, chessBoard));
			}
			else{
				condition = line.charAt(5);
				con = (condition == 'T');
				loadPieces.add(new Pawn(row, col, c, chessBoard, Piece.nextId++, con));
			}
		}
		chessBoard.setPieces(loadPieces, wKCoords, bKCoords);
		this.pieces = chessBoard.getPieces();
		if(turnColor.equals("W")){
			Coordinator.turn = 0;
		}
		else if(turnColor.equals("B")){
			Coordinator.turn = 1;
		}
		Piece checkPiece = null;
		for(Piece p : pieces){
			if(p.getColor() == Coordinator.turn){
				checkPiece = p;
				break;
			}
		}
		updateBoard();
		Coordinator.winner = -2;
		boolean check = chessBoard.checkChecks(checkPiece, checkPiece.getCoords().getR(), checkPiece.getCoords().getC());
		checkGameEnd(1 - Coordinator.turn, check);
		//game end check
		notations.reset();
		notations.setNewGameState(piecesStr);
		draw();
	}
	// create starting board (temporary) (either through file or starting)
	// 
	// test moves
	// if moves work, reset board/change board
	// make moves
	// send info to other client
	// update game state info

	/**
	 * 
	 * @param file - file to save the game to
	 * 
	 * calls this board's Notations object's saveGame method
	 */
	public void saveGame(File file){
		notations.saveGame(file);
	}

	/**
	 * 
	 * @param move - move to play from
	 * 
	 * calls this board's Coordinator object's sendPlayFromHere method to
	 * tell other board to also start play from the same move
	 */
	public void sendPlayFromHere(int move){
		if(myCoordinator != null && myCoordinator.getColor() != 2) myCoordinator.sendPlayFromHere(move);
	}

	/**
	 * handles the mouse being pressed
	 * if the mouse press is on a piece, starts a timer for 100 ms
	 * if the timer goes off, it means the piece is being dragged, so the 
	 * according fields are updated
	 * otherwise if the timer is stopped before it goes off, nothing happens (it was a click and not a drag)
	 */
	public void mousePressed(MouseEvent e) {
		if(timer != null && timer.isRunning()) timer.stop();
		wasDragged = false;
		pieceDragged = false;
		draggedPiece = null;
		if(promoting || onPreviousMove) return;

		click = false;

		if(!(e.getComponent() instanceof JButton)) {
			pieceDragged = false;
			draggedPiece = null;
			return;
		}
		dragX = e.getComponent().getX() + e.getX() - SQUARESIZE / 2 + 5;
		dragY = e.getComponent().getY() + e.getY() - SQUARESIZE / 2;
		int r = e.getComponent().getY() / SQUARESIZE - 1;
		int c = e.getComponent().getX() / SQUARESIZE - 1;
		if(!pieceDragged) {
			if(timer != null && timer.isRunning()) timer.stop();
			timer = new Timer(100, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(stoppedDrag){
						stoppedDrag = false;
						timer.stop();
					}
					if(!click && 0 <= r && r < 8 && 0 <= c && c < 8) {
						if(!board[r][c].isEmpty() && board[r][c].getPiece().getColor() == Coordinator.turn && (board[r][c].getPiece().getColor() == myColor || myColor == 2)) {
							pieceDragged = true;
							pieceSelected = true;
							wasDragged = true;
							selectedPiece = board[r][c].getPiece();
							draggedPiece = board[r][c].getPiece();
							selectedPiece.setLegalMoves();
							draw();	
						}
					}
					else{
						pieceDragged = false;
						draggedPiece = null;
					}
					timer.stop();
				}
				
			});
			timer.start();
			
		}
	}

	/**
	 * handles the release of the mouse
	 * makes sure that there was a piece being dragged, then if the 
	 * space it was moved to is a legal move, it makes the move
	 */
	public void mouseReleased(MouseEvent e) {
		if(click || promoting || onPreviousMove || !pieceDragged || !pieceSelected) {
			pieceDragged = false;
			draggedPiece = null;
			draw();
			return;
		}
		int r = (e.getComponent().getY() + e.getY()) / SQUARESIZE - 1;
		int c = (e.getComponent().getX() + e.getX()) / SQUARESIZE - 1;
		boolean legal = draggedPiece.isLegalMove(new Coordinates(r, c));
		if(legal) {
			receivedStartR = selectedPiece.getCoords().getR();
			receivedStartC = selectedPiece.getCoords().getC();
			receivedendR = r;
			receivedendC = c;

			draggedPiece.move(r, c);
			justMadeMove = true;
			pieceSelected = false;
			selectedPiece = null;
		}
		stoppedDrag = true;
		pieceDragged = false;
		draggedPiece = null;
		timer.stop();
		updateBoard();
		
	}
	
	/**
	 * handles the dragging of the mouse
	 * updates the drag coordinates based on where the mouse is
	 */
	public void mouseDragged(MouseEvent e) {
		if(click || promoting) return;
		if(pieceDragged) {
			wasDragged = true;
			dragX = e.getComponent().getX() + e.getX() - SQUARESIZE / 2 + 5;
			dragY = e.getComponent().getY() + e.getY() - SQUARESIZE / 2;
			draw();
		}
		
	}

	/**
	 * @return whether or not a move was just made (for sending moves to other Coordinator)
	 */
	public boolean justMadeMove() {
		return justMadeMove;
	}

	/**
	 * @return the starting row of the moving piece received from the other Coordinator
	 */
	public int getReceivedStartR() {
		return receivedStartR;
	}

	/**
	 * @return the starting column of the moving piece received from the other Coordinator
	 */
	public int getReceivedStartC() {
		return receivedStartC;
	}

	/**
	 * @return the ending row of the moving piece received from the other Coordinator
	 */
	public int getReceivedEndR() {
		return receivedendR;
	}

	/**
	 * @return the ending column of the moving piece received from the other Coordinator
	 */
	public int getReceivedEndC() {
		return receivedendC;
	}

	/**
	 * @return the string of the type of piece that was promoted to
	 */
	public String getPromote() { return promote; }

	/**
	 * sets the promote string to an empty string
	 */
	public void resetPromote() { promote = ""; }

	/**
	 * @param bool - new boolean state
	 * 
	 * updates the justMadeMove boolean
	 */
	public void setJustMadeMove(boolean bool) {
		justMadeMove = bool;
	}

	/**
	 * @param coordinator - the Coordinator object to set to
	 * 
	 * sets the Coordinator object for this board (cannot be in constructor 
	 * because the Board object is created in Coordinator's constructor)
	 */
	public void setCoordinator(Coordinator coordinator){
		myCoordinator = coordinator;
	}
 
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) {	}
	public void mouseMoved(MouseEvent e) { }

	/**
	 * handles an action being performed
	 * 
	 * if the button clicked is a space, updates the fields accordingly
	 * - if clicking on a new piece, selects that piece
	 * - if clicking on an already selected piece, deselects the piece
	 * - if clicking on a legal move of the selected piece, makes the move
	 * - if clicking on another space that is not any of the above, deselects selected piece
	 * 
	 * if the button clicked is a promotion button, finishes the promotion based
	 * on the button clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(Coordinator.winner != -2) return;
		if(e.getActionCommand().contains("button") && !onPreviousMove){
			int r = e.getActionCommand().charAt(8) - '0';
			int c = e.getActionCommand().charAt(10) - '0';

			if(!pieceSelected && !board[r][c].isEmpty() && board[r][c].getPiece().getColor() == Coordinator.turn && (board[r][c].getPiece().getColor() == myColor || myColor == 2)){
				pieceSelected = true;
				selectedPiece = board[r][c].getPiece();
				selectedPiece.setLegalMoves();
				pieceDragged = false;
				draggedPiece = null;
				draw();
				click = true;
			}
			else if(pieceSelected){
				if(selectedPiece.isLegalMove(new Coordinates(r, c))){
					
					receivedStartR = selectedPiece.getCoords().getR();
					receivedStartC = selectedPiece.getCoords().getC();
					receivedendR = r;
					receivedendC = c;

					selectedPiece.move(r, c);
					justMadeMove = true;
					pieceSelected = false;
					selectedPiece = null;
					pieceDragged = false;
					draggedPiece = null;
					updateBoard();
					click = false;
				}
				else{
					if(!board[r][c].isEmpty() && !board[r][c].getPiece().equals(selectedPiece) && board[r][c].getPiece().getColor() == Coordinator.turn){
						selectedPiece = board[r][c].getPiece();
						selectedPiece.setLegalMoves();
						click = true;
					}
					else if(!board[r][c].isEmpty() && board[r][c].getPiece().equals(selectedPiece)){
						if(wasDragged){
							wasDragged = false;
						}
						else{
							pieceSelected = false;
							selectedPiece = null;
						}
						click = true;
					}
					else{
						pieceSelected = false;
						selectedPiece = null;
						click = false;
					}
					pieceDragged = false;
					draggedPiece = null;
					draw();
				}
			}
			pieceDragged = false;
			draggedPiece = null;
		}
		else if(e.getActionCommand().contains("promote")){
			String piece = e.getActionCommand().substring(8);
			String notation = "";
			Piece newPiece = null;
			if(piece.equals("Queen")){
				newPiece = new Queen(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
				notation = "Q";
			}
			else if(piece.equals("Rook")){
				newPiece = new Rook(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
				notation = "R";
			}
			else if(piece.equals("Bishop")){
				newPiece = new Bishop(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
				notation = "B";
			}
			else{
				newPiece = new Knight(promotingPiece.getCoords().getR(), promotingPiece.getCoords().getC(), promotingPiece.getColor(), chessBoard);
				notation = "N";
			}
			pieces.add(newPiece);
			pieces.remove(promotingPiece);
			promoting = false;
			if((newPiece.getColor() == 0 && newPiece.checkCheck(chessBoard.getBKingCoords().getR(), chessBoard.getBKingCoords().getC(), chessBoard.getBoard())) ||
				(newPiece.getColor() == 1 && newPiece.checkCheck(chessBoard.getWKingCoords().getR(), chessBoard.getWKingCoords().getC(), chessBoard.getBoard()))) {
				promoteCheck = true;
			}
			promote = notation;
			promoteEnd = checkGameEnd(promotingColor, promoteCheck);
			finishPromoteNotation(notation, newPiece.getColor() == 0? newPiece.getCoords().getR() + 1:newPiece.getCoords().getR() - 1, newPiece.getCoords().getC(), newPiece.getCoords().getR(), newPiece.getCoords().getC());
			pieceDragged = false;
			draggedPiece = null;
			selectedPiece = null;
			pieceSelected = false;
			if(promoteCheck){
				soundPlayer.playCheck();
			}
			if(promoteEnd){
				soundPlayer.playGameEnd();
			}

			Coordinator.turn = 1 - Coordinator.turn;
			updateBoard();
		}
		
	}
}
