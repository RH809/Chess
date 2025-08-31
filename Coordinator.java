import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.*;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class that acts as a client for the server. It holds its own board
 * and keeps track of its own game state while sending/receiving messages to
 * the other client to update its board accordingly.
 */
public class Coordinator implements WindowListener{

	public static int turn, winner = -2; // turn - whose turn it is (0 = white, 1 = black), winner - who won (-2 = game has not ended, -1 = tie, 0 = white win, 1 = black win)

	private Socket socket; // the socket used for communication to the server
    private BufferedReader br; // input stream reader to receive messages from the server
    private static PrintWriter out; // output stream to send messages to other clients
    private String username; // username of the client used to distinguish clients
	private int color; // color of this Coordinator (determines which piece the player can move)
	private Board board; // the board connected to this Coordinator
	private static JFrame frame; // the JFrame for this Coordinator
	private Notations notations; // the Notations object connected to the board
	private JLayeredPane lPane; // the JLayeredPane in the frame
	private String operations = ""; // the operations done to get to the same game state as this Coordinator's board (for syncing)
	private JFrame confirmationWindow; // window that allows the client to accept to load games
	private JFrame waitForRequestWindow;
	private static String answerFromClient; // answer regarding loading games
	private JLabel labelForConfirmation; // label to distinguish client requests
	private static int numConnected = 1; // number of clients connected to the server
	private GameSaver gs; 
	private Chat chat;
	private ChatFocusListener cfl;

	/**
	 * @param color - the color of this Coordinator
	 * @param username - the username of this Coordinator
	 * 
	 * initializes everything for the game, including the graphics,
	 * the board, and the notations
	 */
	public Coordinator(String username, int color) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		this.color = color;
		this.username = username;
		frame = new JFrame();
		frame.setBounds(150, 50, 1400, 800);

		
		frame.setLocationRelativeTo(null);
		if(color != 2){
			frame.setTitle("Chess   Multi-player   " + username + " - " + ((color == 0) ? "White":"Black"));
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		else{
			frame.setTitle("Chess   Single-player");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		lPane = new JLayeredPane();
		frame.add(lPane);
		lPane.setBounds(0, 0, 1400, 800);

		notations = new Notations(frame);
		board = new Board(notations, frame, lPane, color);
		board.getChessBoard().setMyBoard(board);
		notations.saveStartingPos();
		frame.addKeyListener(notations.getSwitchMove());

		turn = 0;
		gs = new GameSaver(frame, board);
		if(color != 2) {
			cfl = new ChatFocusListener(frame);
			chat = new Chat(frame, cfl);
			cfl.setTextField(chat.getTextField());
			frame.addMouseListener(cfl);
			board.addCFL(cfl);
			notations.addCFL(cfl);
		}
		frame.repaint();
		frame.setVisible(true);	

		// set up confirmation window for accepting to load games
		confirmationWindow = createConfirmationWindow();
		waitForRequestWindow = createRequestWindow();
		answerFromClient = null;

		frame.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focus gained");
			}

			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("focus lost");
			}
			
		});
	}

	public Coordinator(int color){ // single player
		this("", color);
		board.setSingleplayer();
	}

	/**
	 * returns answerFromClient (to help facilitate agreement of loading game)
	 * @return answerFromClient
	 */
	public static String getAnswerConfirmation() {
		return answerFromClient;
	}

	/**
	 * @return the number of clients connected to the server
	 */
	public static int numConnected(){
		return numConnected;
	}
	/**
	 * sets answerFromClient to value of str
	 * @param str value to change answerFromClient
	 */
	public static void setAnswerFromClient(String str) {
		if (str.equals("null")) {
			answerFromClient = null;
		}
		else answerFromClient = str;
		
	}

	/**
	 * @return the board connected to this Coordinator
	 */
	public Board getBoard() { return board; }

	public GameSaver getGameSaver() { return gs; }
	
	public Chat getChat() { return chat; }
	/**
	 * @return the operations done by this Coordinator 
	 */
	public String getOperations() { return operations; }

	public String getUsername() { return username; }


	/**
	 * @param socket - socket to connect with
	 * @param username - username to connect as
	 * @throws IOException
	 * 
	 * connects to the server using the given username
	 */
	public void connectToServer(Socket socket, String username) throws IOException {
        this.socket = socket;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true); // Enable auto-flush
        this.username = username;

        out.println(username);
    }

	/**
	 * closes the game (terminates program)
	 */
	public void closeGame() {
		sendDisconnect();
		if(out != null) out.println("[CLIENT CLOSED]");
		System.exit(0);
	}

	/**
	 * returns output stream of the coordinator
	 * @return PrintWriter object
	 */
	public static PrintWriter getOutStream() {
		return out;
	}

	/**
	 * helper method to create confirmation window
	 * @return confirmation window jframe
	 */
	public JFrame createConfirmationWindow() {
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setSize(200,100);
		
		frame.setLayout(new GridLayout(2,1));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JPanel panel1 = new JPanel(new GridBagLayout());
		panel1.setBackground(Color.darkGray);

		labelForConfirmation = new JLabel("Load Game?");
		labelForConfirmation.setForeground(Color.white);
		panel1.add(labelForConfirmation);

		JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.setBackground(Color.darkGray);

		JButton yesButton = new JButton("Yes");
		yesButton.setPreferredSize(new Dimension(85,35));

		yesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
				out.println("*Yes");
				out.flush();
				gs.changeStatus("Accepted request");
			}
			
		});

		JButton noButton = new JButton("No");
		noButton.setPreferredSize(new Dimension(85,35));

		noButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
				out.println("*No");
				out.flush();
				gs.changeStatus("Denied request");
			}
			
		});

		panel2.add(yesButton);
		panel2.add(noButton);

		frame.add(panel1);
		frame.add(panel2);
		return frame;
	}

	public JFrame createRequestWindow() {
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setSize(200,100);
		
		frame.setLayout(new GridLayout(2,1));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JPanel panel1 = new JPanel(new GridBagLayout());
		panel1.setBackground(Color.darkGray);

		JLabel labelForRequest = new JLabel("Awaiting Response...");
		labelForRequest.setForeground(Color.white);
		panel1.add(labelForRequest);

		JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.setBackground(Color.darkGray);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(85,35));

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
				out.println("*Cancel");
				out.flush();
				setAnswerFromClient("*Cancel");
				System.out.println("pressed cancel");
			}
			
		});

		panel2.add(cancelButton);

		frame.add(panel1);
		frame.add(panel2);
		return frame;
	}

	/**
	 * listens for messages from server
	 * 
	 * if the message starts with "*" it means it is a message for the game, not a text message
	 * - can receive message for resetting the board
	 * - can receive message for playing from a certain move
	 * - can receive message for playing from a new game state
	 * - can receive message for promoting
	 * - can receive message for a regular move
	 * 
	 */
	public synchronized void listenForMessages() {
		// distinguish between text messages and game server messages

		// update gameBoard
		
		Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String msgFromGroup;
				System.out.println("Successfully Connected!");
                while (socket != null && !socket.isClosed()) {
                    try {
                        msgFromGroup = br.readLine();
						System.out.println(msgFromGroup);
						// can't send blank messages
                        if (msgFromGroup==null) break;

						if (msgFromGroup.equals("*Yes")) {
							answerFromClient = "*Yes";
							waitForRequestWindow.setVisible(false);
						}
						else if (msgFromGroup.equals("*No")) {
							answerFromClient = "*No";
							waitForRequestWindow.setVisible(false);
						}
						else if (msgFromGroup.equals("*Cancel")) {
							System.out.println("request cancelled");
							answerFromClient = "*Cancel";
							confirmationWindow.setVisible(false);
							gs.changeStatus("Request was cancelled");
						}
						else if(msgFromGroup.equals("*Request")){
							waitForRequestWindow.setLocation(frame.getX() + frame.getWidth() / 2 - waitForRequestWindow.getWidth() / 2, frame.getY() + frame.getHeight() / 2 - waitForRequestWindow.getHeight() / 2);
							waitForRequestWindow.setVisible(true);
							waitForRequestWindow.setAlwaysOnTop(true);
							waitForRequestWindow.toFront();
						}
						else if(msgFromGroup.indexOf("CLIENT") != -1){
							numConnected = msgFromGroup.charAt(1) - '0';
						}
						else if(msgFromGroup.indexOf("[CHAT]") != -1){ // chat
							String username = br.readLine();
							String text = br.readLine();
							chat.display(username + " [OPPONENT]: " + text + "\n");
						}
						else if(msgFromGroup.indexOf("[CONNECTION]") != -1){
							if(msgFromGroup.indexOf("SELF") != -1){
								chat.display(username + " [YOU] Connected!\n");
								if(Coordinator.numConnected == 2){ // send connection status to other coordinator if there are two
									out.println("[CONNECTION] OPPONENT " + username);
									out.flush();
								}
							}
							else if(msgFromGroup.indexOf("OPPONENT") != -1){
								String username = msgFromGroup.substring(msgFromGroup.indexOf("OPPONENT") + 9);
								chat.display(username + " [OPPONENT] Connected!\n");
							}
						}
						else if(msgFromGroup.indexOf("[DISCONNECT]") != -1){
							String username = msgFromGroup.substring(13);
							chat.display(username + " [OPPONENT] Disconnected.\n");
						}
						else if(msgFromGroup.indexOf("[CHAT HISTORY]") != -1){
							String line = br.readLine();
							String text = "";
							while(line.indexOf("[END CHAT HISTORY]") == -1){
								if(line.indexOf("[SERVER]") != -1){
									line = br.readLine();
									continue;
								}
								if(line.indexOf("[YOU]") != -1) line = line.substring(0, line.indexOf("[YOU]")) + "[OPPONENT]" + line.substring(line.indexOf("[YOU]") + 5); // line.replaceFirst("[YOU]", "[OPPONENT]");
								else if(line.indexOf("[OPPONENT]") != -1) line = line.substring(0, line.indexOf("[OPPONENT]")) + "[YOU]" + line.substring(line.indexOf("[OPPONENT]") + 10); // line.replaceFirst("[OPPONENT]", "[YOU]");
								text += line;
								if(line.length() > 0 && line.charAt(line.length() - 1) != '\n') text += "\n";
								line = br.readLine();
							}
							chat.display(text);
							// here is where to say connected
							chat.display(username + " [YOU] Connected!\n");
							if(Coordinator.numConnected == 2){ // send connection status to other coordinator if there are two
								out.println("[CONNECTION] OPPONENT " + username);
								out.flush();
							}
						}
						else if (msgFromGroup.indexOf("****Do you want to load?") != -1) {
							System.out.println("received confirmation");
							out.println("*Request");
							out.flush();
							labelForConfirmation.setText(msgFromGroup.substring("****Do you want to load?".length()));
							System.out.println("showing confirmation window");
							confirmationWindow.setLocation(frame.getX() + frame.getWidth() / 2 - confirmationWindow.getWidth() / 2, frame.getY() + frame.getHeight() / 2 - confirmationWindow.getHeight() / 2);
							confirmationWindow.setVisible(true);
							confirmationWindow.setAlwaysOnTop(true);
							confirmationWindow.toFront();
						}
						
						else if(msgFromGroup.indexOf("*****send operations") != -1){
							out.println(operations);
							out.flush();
						}
						else if(msgFromGroup.indexOf("*****send chat") != -1){
							String chatHistory = chat.getChatHistory();
							out.println("[CHAT HISTORY]");
							out.println(chatHistory);
							out.println("[END CHAT HISTORY]");
							out.flush();
						}
						else if (msgFromGroup.indexOf("***") != -1) {
							System.out.println("received initial recognition of color");
							color = Integer.valueOf(msgFromGroup.substring(3, 4));
							board.setColor(color);
							frame.setTitle("Chess   Multi-player   " + username + " - " + ((color == 0) ? "White":"Black"));
							frame.repaint();
							System.out.println(msgFromGroup);

							// update debug message
							String debugMessage = "";
							if (color == 0) debugMessage = "white"; else debugMessage = "black";
							System.out.println("color is now: " + debugMessage);
						} 
						else if (msgFromGroup.indexOf("*") != -1) { // * server identifier sample move : 1,2,3,4
							System.out.println(msgFromGroup);
							if(msgFromGroup.indexOf("reset board") != -1){
								operations = "";
								board.resetBoard(false);
							}
							else if(msgFromGroup.indexOf("play from") != -1){ //play from here
								int move;
								if(msgFromGroup.charAt(12) == 'S') move = -1;
								else move = Integer.valueOf(msgFromGroup.substring(12));
								operations += msgFromGroup + "\n";
								board.getNotations().setSelected(move);
								board.getNotations().startPlayHere(false);
							}
							else if(msgFromGroup.indexOf("new game state") != -1){ // load game state
								String info = br.readLine();
								String pieces = "";
								while(info.indexOf("end of game state") == -1){
									pieces += info + "\n";
									info = br.readLine();
								}
								operations = "*new game state\n" + pieces + "*end of game state\n";
								board.loadGameState(new Scanner(pieces));
							}
							else if (msgFromGroup.indexOf("P") != -1) { //promotion
								System.out.println("promote received");
								int startIdx = msgFromGroup.indexOf("*");
								int startR = Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 1)));
								int startC = (int) Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 2)));
								int endR = (int) Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 3)));
								int endC = (int) Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 4)));
								String promote = String.valueOf(msgFromGroup.charAt(startIdx + 5));

								board.makePieceMove(startR, startC, endR, endC, promote);
								operations += msgFromGroup + "\n";
							}
							else{ // regular move
								int startIdx = msgFromGroup.indexOf("*");
								int startR = Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 1)));
								int startC = (int) Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 2)));
								int endR = (int) Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 3)));
								int endC = (int) Integer.valueOf(String.valueOf(msgFromGroup.charAt(startIdx + 4)));
								
								board.makePieceMove(startR, startC, endR, endC);
								operations += msgFromGroup + "\n";
							}
							
							System.out.println("received move");
							

						}
						else {
							System.out.println(msgFromGroup);
						}
                        
                    } catch (IOException e) {
						e.printStackTrace();
						cleanup();
                        
                    }
			
                }
				System.out.println("Server connection was terminated");
				cleanup();
            }
             
        });
        thread.start();
		
	}

	/**
	 * closes the connection to the server and exits the program
	 */
	public void cleanup() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * sends messages to the other client when a move is made
	 * continuously checks if a move was made
	 * - if it is a regular move, it immediately sends the move
	 * - if it is a promotion, it waits for the promotion to finish, then sends
	 * 		the move and which type of piece was promoted to
	 */
	public void sendMoves() {
		// check if message came from text box gui

		//get move from Board

		Thread t = new Thread(new Runnable() {

			@Override
			public void run()
			{
				while (!socket.isClosed() && socket != null) {

					try
					{
						Thread.sleep(20);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if (board.justMadeMove()) { 
						if (board.isPromoting()){ //promotion event (wait until a piece is selected)
							while(board.getPromote().equals("")){
								try
								{
									Thread.sleep(20);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
							}
							if(board.getPromote() != "") {
								board.setJustMadeMove(false);

								int receivedStartR = board.getReceivedStartR();
								int receivedStartC = board.getReceivedStartC();
								int receivedendR = board.getReceivedEndR();
								int receivedendC = board.getReceivedEndC();
								operations += "*" + receivedStartR + receivedStartC + receivedendR + receivedendC + board.getPromote() + "P\n";
								out.println("*" + receivedStartR + receivedStartC + receivedendR + receivedendC + board.getPromote() + "P");
								out.flush();
								board.resetPromote();
							}
						}
						else { // not promotion event
							board.setJustMadeMove(false);
							int receivedStartR = board.getReceivedStartR();
							int receivedStartC = board.getReceivedStartC();
							int receivedendR = board.getReceivedEndR();
							int receivedendC = board.getReceivedEndC();
							operations += "*" + receivedStartR + receivedStartC + receivedendR + receivedendC + "\n";
							out.println("*" + receivedStartR + receivedStartC + receivedendR + receivedendC);
							out.flush();
						}
						
					}

				}
				
			}
			
		});
		t.start();
			
		
	}

	/**
	 * @param move - string for the move to send
	 * 
	 * sends a single move to the other client (used when loading
	 * games to automatically send moves instead waiting for a player
	 * to make a move)
	 */
	public void sendMove(String move){
		operations += move + "\n";
		out.println(move);
		out.flush();
	}

	/**
	 * sends a message to the other client to reset the board
	 */
	public void sendReset(){
		operations = "";
		out.println("*reset board");
		out.flush();
	}

	/**
	 * @param move - the move number to play from
	 * 
	 * sends a message to the other client to restart play
	 * from a certain move
	 */
	public void sendPlayFromHere(int move){
		if(move == -1) {
			out.println("* play from S");
			operations += "* play from S\n";
		}
		else {
			out.println("* play from " + move);
			operations += "* play from " + move + "\n";
		}
		out.flush();
	}

	/**
	 * @param pieces - a string of the position of all of the pieces (
	 * 					same information as in the game state text file)
	 * 
	 * sends a message to the other client for a new game state that includes
	 * all of the positions and conditions of the pieces on the board
	 */
	public void sendGameState(String pieces){
		operations = "*new game state\n" + pieces + "*end of game state\n";
		out.println("*new game state");
		out.print(pieces);
		out.println("*end of game state");
		out.flush();
	}

	public void sendChat(String text){
		out.println("[CHAT]");
		out.println(username);
		out.println(text);
		out.flush();
	}

	public void sendDisconnect(){
		out.println("[DISCONNECT] " + username);
		out.flush();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length > 0) { 
			String ipAddress = args[0];
			int port = Integer.valueOf(args[1]);
			String username = args[2];
			int color = Integer.valueOf(args[3]);
			boolean canConnect = true;
			try {
				
				Socket s = new Socket(ipAddress, port);
				
				if (s.isClosed()) System.exit(0);
				Coordinator c = new Coordinator(username, color);
				c.connectToServer(s, username);
				c.listenForMessages();
				c.sendMoves();
				c.getBoard().setCoordinator(c);
				c.getChat().getChatListener().setCoordinator(c);
				frame.addWindowListener(c);
				

				
			} catch (Exception e) {
				e.printStackTrace();
				canConnect = false;
			}
			if (!canConnect) System.out.println("No Server Found!");
			
			
		} else if (args.length == 0) {
			Coordinator c = new Coordinator(2);
			c.getBoard().setCoordinator(c);
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		closeGame();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		closeGame();
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}


}