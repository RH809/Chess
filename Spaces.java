
import javax.swing.*;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to represent a "space" on the board. The space will still stationary,
 * but the piece in the space can change.
 */
public class Spaces extends JLabel{
	protected int r, c; // row and column of the piece
	protected boolean empty; // whether or not this space is empty
	protected Piece piece; // the piece occupying this space
	protected JButton button; // the button (display) of this space
	
	/**
	 * 
	 * @param r - row of the space
	 * @param c - column of the space
	 * @param empty - whether or not the space is empty
	 * 
	 * initalizes space based on above parameters
	 */
	public Spaces(int r, int c, boolean empty) {
		this.r = r;
		this.c = c;
		this.empty = empty;
		button = new JButton();
	}
	
	/**
	 * 
	 * @param r - row of the space
	 * @param c - column of the space
	 * @param piece - piece occupying the space
	 * 
	 * initalizes space based on above parameters
	 */
	public Spaces(int r, int c, Piece piece) {
		this.r = r;
		this.c = c;
		this.piece = piece;
		this.empty = false;
		button = new JButton(piece.getImage());
	}
	
	/**
	 * @return row of the space
	 */
	public int getR() { return r; }
	/**
	 * @return column of the space
	 */
	public int getC() { return c; }
	/**
	 * @return whether or not the space is empty
	 */
	public boolean isEmpty() { return empty; }
	/**
	 * sets the space to empty by removing any piece in it
	 * and updates the JButton to be empty
	 */
	public void setEmpty() {
		empty = true;
		piece = null;
		button.setIcon(null);
	}
	/**
	 * @return the object of the piece occupying this space
	 */
	public Piece getPiece() { return piece; }
	/**
	 * @param piece - piece that will now occupy this space
	 * 
	 * updates the space to have a new piece occupy it
	 * will update the JButton to match too
	 */
	public void setPiece(Piece piece) {
		empty = false;
		this.piece = piece;
		setImage(piece.getImage());
	}

	/**
	 * @return the JButton in this space
	 */
	public JButton getButton(){
		return button;
	}

	/**
	 * @param newImage - image to set the image to
	 * 
	 * updates the image of the JButton to a new image
	 */
	public void setImage(ImageIcon newImage){
		button.setIcon(newImage);
		button.setOpaque(true);
	}

	/**
	 * @param newButton - button to set the button to
	 * 
	 * updates/initializes the JButton of this space
	 */
	public void setButton(JButton newButton){
		button = newButton;
	}
	
	/**
	 * @return either "empty" if it is empty or the piece that is occupying it
	 */
	public String toString() {
		if(empty) return "empty";
		return piece.toString();
	}
	
	/**
	 * @return whether or not this space is equal to another object
	 */
	public boolean equals(Object o) {
		if(!(o instanceof Spaces) || o == null) return false;
		Spaces other = (Spaces)o;
		if(empty) return other.isEmpty();
		else if(other.isEmpty()) return false;
		return other.getPiece().equals(this.getPiece());
	}
}
