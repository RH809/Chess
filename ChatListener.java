import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatListener implements ActionListener{

    private JTextField textField;
    private JTextArea textArea;
    private Coordinator coordinator;
    private String username;

    public ChatListener(JTextField textField, JTextArea textArea, JFrame frame){
        this.textField = textField;
        this.textArea = textArea;
        frame.requestFocus();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // send message
        if(Coordinator.getOutStream() == null || Coordinator.numConnected() == 1){
            textArea.append("[SERVER] Cannot send messages - no opponent connected\n");
            textField.setText("");
            return;
        }
        String text = textField.getText();
        textArea.append(username + " [YOU]: " + text + "\n");
        textField.setText("");
        if(coordinator != null) coordinator.sendChat(text);
    }

    public void setCoordinator(Coordinator coordinator) { 
        this.coordinator = coordinator;
        username = coordinator.getUsername();
    }

    

    // unselect when clicking outside of the chat

}