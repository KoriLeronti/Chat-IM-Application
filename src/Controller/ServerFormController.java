package Controller;

import Client.LoginInterface;
import Server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerFormController extends JFrame {
    private static Map<String, Socket> connectedClients = new ConcurrentHashMap<>();
    private JPanel messagePanel;
    private JScrollPane scrollPane;
    private static JPanel staticMessagePanel;

    public ServerFormController() {

        initializeUI();
        initializeServer();
        receiveMessage("Server Starting..");
        receiveMessage("Server Running..");
        receiveMessage("Waiting for User..");
    }

    private void initializeUI() {
        setTitle("Server Form");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(messagePanel);

        staticMessagePanel = messagePanel;

        JButton addButton = new JButton("Add Client");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddClientWindow();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeServer() {
        new Thread(() -> {
            try {
                // Initialize and start the server
                Server server = Server.getInstance();
                server.makeSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()) {
            JPanel msgPanel = new JPanel();
            msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
            msgPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JTextArea textArea = new JTextArea(msgToSend);
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            textArea.setEditable(false);

            JPanel timePanel = new JPanel();
            timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
            timePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            JLabel timeLabel = new JLabel(stringTime);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 8));

            timePanel.add(Box.createHorizontalGlue());
            timePanel.add(timeLabel);

            msgPanel.add(textArea);
            msgPanel.add(timePanel);

            staticMessagePanel.add(msgPanel);
            staticMessagePanel.revalidate();
            staticMessagePanel.repaint();
        }
    }
    public static void receiveMessage(String msgFromClient) {

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea textArea = new JTextArea(msgFromClient);
        textArea.setFont(new Font("Arial", Font.BOLD, 14));
        textArea.setEditable(false);

        msgPanel.add(textArea);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                staticMessagePanel.add(msgPanel);
                staticMessagePanel.revalidate();
                staticMessagePanel.repaint();
            }
        });

    }
    private void openAddClientWindow() {
        LoginInterface loginInterface = new LoginInterface(null);

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                new ServerFormController();
            }
        });
    }
}
