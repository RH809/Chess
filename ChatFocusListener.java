import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class ChatFocusListener implements MouseListener {
    private JFrame frame;
    private JTextField textField;

    public ChatFocusListener(JFrame frame){
        this.frame = frame;
    }

    public void setTextField(JTextField textField) {
        this.textField = textField;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("mouse pressed");
        if(textField.equals(e.getComponent())){
            System.out.println("text field");
            textField.requestFocus();
        }
        else{
            System.out.println("frame");
            frame.requestFocus();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
