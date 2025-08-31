import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public class Chat {
    private JPanel displayPanel, inputPanel;
    private JScrollPane displayScrollPane, inputScrollPane;
    private JTextArea textArea;
    private JTextField textField;
    private ChatListener chatListener;
    private JButton enter;
    private static final int VIEW_WIDTH = 425, PANEL_HEIGHT = 700, VIEW_HEIGHT = 630, CHAT_WIDTH = 350; 
    // panel height - total height of everything
    // view height - height of only the text area

    public Chat(JFrame frame, ChatFocusListener cfl){
        frame.setBounds(150, 50, 1900, 800); // resize frame if chat is added
        frame.getLayeredPane().setBounds(0, 0, 1900, 800);
        

        displayPanel = new JPanel();
		frame.getLayeredPane().add(displayPanel);
		displayPanel.setBackground(Color.WHITE);
		displayPanel.setBounds(1400, 30, VIEW_WIDTH, VIEW_HEIGHT + 20);
		displayPanel.setAutoscrolls(true);
		displayPanel.setLayout(null);
		
		displayScrollPane = new JScrollPane();
		displayPanel.add(displayScrollPane);
		displayScrollPane.setBounds(0, 20, VIEW_WIDTH, VIEW_HEIGHT);
        displayScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        displayScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		textArea = new JTextArea();
		textArea.setFont(new Font(new JLabel().getFont().getName(), 15, 15));
		displayScrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		
		displayScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textArea.setAutoscrolls(false);

        JLabel header = new JLabel("Chat", JLabel.CENTER);
        header.setBounds(0, 0, VIEW_WIDTH, 20);
        displayPanel.add(header);

        inputPanel = new JPanel();
        frame.getLayeredPane().add(inputPanel);
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBounds(1400, VIEW_HEIGHT + 50, VIEW_WIDTH, PANEL_HEIGHT - VIEW_HEIGHT - 20);
        inputPanel.setAutoscrolls(true);
        inputPanel.setLayout(null);

        inputScrollPane = new JScrollPane();
        inputPanel.add(inputScrollPane);
        inputScrollPane.setBounds(0, 0, CHAT_WIDTH, PANEL_HEIGHT - VIEW_HEIGHT - 20);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        textField = new JTextField();
        textField.setFont(new Font(new JLabel().getFont().getName(), 15, 15));
        inputScrollPane.setViewportView(textField);

        enter = new JButton("Send");
        inputPanel.add(enter);
        enter.setBounds(CHAT_WIDTH, 0, VIEW_WIDTH - CHAT_WIDTH, PANEL_HEIGHT - VIEW_HEIGHT - 20);
        enter.setBackground(Color.BLACK);
        enter.setFocusable(false);
        chatListener = new ChatListener(textField, textArea, frame);
        textField.addActionListener(chatListener);
        enter.addActionListener(chatListener);

        header.addMouseListener(cfl);
        textArea.addMouseListener(cfl);
        textField.addMouseListener(cfl);
    }

    public void display(String text){
        textArea.append(text);
    }

    public ChatListener getChatListener() { return chatListener; }

    public JTextField getTextField() { return textField; }

    public String getChatHistory(){
        return textArea.getText();
    }
}