
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to act as a key listener for the Notations class so that
 * when an arrow key is pressed, the Notations class will update
 */
public class SwitchMove implements KeyListener{

	private Notations notations; // the Notations class for which this is listening
	
	/**
	 * @param notations - the Notations class to listen for
	 * 
	 * initializes the notations object
	 */
	public SwitchMove(Notations notations) {
		this.notations = notations;
	}

	/**
	 * @param e - key event
	 * if an arrow key is pressed, the notations class will be updated accordingly
	 * - if right arrow: go to next move if there is a next move
	 * - if left arrow: go to previous move if there is a previous move
	 * - up arrow: go one row up (2 moves back) if possible, if not go to previous move if possible
	 * - down arrow: go one row down (2 moves forward) if possible, if not go to next move if possible
	 */
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == 39 && notations.getSelected() < notations.getSize()) notations.setSelected(notations.getSelected() + 1);
		else if(e.getKeyCode() == 37 && notations.getSelected() >= 0) notations.setSelected(notations.getSelected() - 1);
		else if(e.getKeyCode() == 38 && notations.getSelected() >= 1) notations.setSelected(notations.getSelected() - 2);
		else if(e.getKeyCode() == 38 && notations.getSelected() == 0) notations.setSelected(-1);
		else if(e.getKeyCode() == 40 && notations.getSelected() < notations.getSize()) {
			if(notations.getSelected() == notations.getSize() - 1) notations.setSelected(notations.getSize());
			else notations.setSelected(notations.getSelected() + 2);
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

}
