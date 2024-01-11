package Controller;
import Client.LoginInterface;
import Client.UserInfo;
import Emoji.EmojiPicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import static Client.LoginInterface.User;
public class ClientFormController {

    private JFrame frame;
    private JPanel messagePanel;
    private JScrollPane scrollPane;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName = "Client";
    private UserInfo recipient;

    private JTextArea txtMsg;
    private JLabel txtLabel;
    private JButton emojiButton;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientFormController();
            }
        });
    }
    public ClientFormController() {
        initializeUI(User);
        initializeClient();
    }

    public void initializeUI(UserInfo user) {
        LoginInterface loginInterface = new LoginInterface(null);

        frame = new JFrame(loginInterface.getLoggedInUserName());
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(messagePanel);

        txtMsg = new JTextArea();
        txtLabel = new JLabel(User.Names);

        emojiButton = new JButton("Emoji");
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEmojiPicker();
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButtonOnAction(e);
            }
        });

        JButton attachedButton = new JButton("Attach");
        attachedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attachedButtonOnAction(e);
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(txtMsg, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(attachedButton, BorderLayout.WEST);

        JPanel labelPanel = new JPanel();
        labelPanel.add(txtLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(emojiButton);

        frame.add(labelPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.add(buttonPanel, BorderLayout.EAST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    private void initializeClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("localhost", 3001);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Client connected");
                    ServerFormController.receiveMessage(User.Names + " joined.");
                    //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (socket.isConnected()) {
                        String receivingMsg = dataInputStream.readUTF();
                        receiveMessage(receivingMsg, ClientFormController.this.messagePanel);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    private JWindow emojiPickerWindow;
    private JList emojiListView;
    private void showEmojiPicker() {
        // Create the EmojiPicker
        EmojiPicker emojiPicker = new EmojiPicker();

        // Set the layout and other properties for the emoji picker window
        JWindow emojiPickerWindow = new JWindow(frame);
        emojiPickerWindow.setLayout(new FlowLayout());
        emojiPickerWindow.setSize(200, 300);  // Set your preferred size
        emojiPickerWindow.getContentPane().add(emojiPicker);

        // Set the emoji picker as hidden initially
        emojiPickerWindow.setVisible(false);

        // Show the emoji picker when the button is clicked
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (emojiPickerWindow.isVisible()) {
                    emojiPickerWindow.setVisible(false);
                } else {
                    int x = emojiButton.getLocationOnScreen().x + emojiButton.getWidth();
                    int y = emojiButton.getLocationOnScreen().y;
                    emojiPickerWindow.setLocation(x, y);
                    emojiPickerWindow.setVisible(true);
                }
            }
        });

        // Set the selected emoji from the picker to the text area
        emojiPicker.getEmojiList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedEmoji = emojiPicker.getEmojiList().getSelectedValue();
                if (selectedEmoji != null) {
                    txtMsg.setText(txtMsg.getText() + selectedEmoji);

                }
                emojiPickerWindow.setVisible(false);
            }
        });
    }
    public void txtMsgOnAction(ActionEvent actionEvent) {
       sendButtonOnAction(actionEvent);
   }
    public void sendButtonOnAction(ActionEvent actionEvent) {
        sendMsg(txtMsg.getText());
    }
    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()) {
            if (!msgToSend.matches(".*\\.(png|jpe?g|gif|txt)$")){

                JPanel hBox = new JPanel();
                hBox.setLayout(new FlowLayout(FlowLayout.RIGHT));

                JLabel text = new JLabel(msgToSend);
                text.setFont(new Font("Arial", Font.PLAIN, 14));

                JPanel textPanel = new JPanel();
                textPanel.setBackground(new Color(6, 147, 227));
                textPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                textPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                textPanel.add(text);

                hBox.add(textPanel);

                JPanel hBoxTime = new JPanel();
                hBoxTime.setLayout(new FlowLayout(FlowLayout.RIGHT));
                String stringTime = getTime();
                JLabel time = new JLabel(stringTime);
                time.setFont(new Font("Arial", Font.PLAIN, 8));

                JPanel timePanel = new JPanel();
                timePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                timePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
                timePanel.add(time);

                hBoxTime.add(timePanel);

                messagePanel.add(hBox);
                messagePanel.add(hBoxTime);

                try {
                    dataOutputStream.writeUTF(User.Names + "-" + msgToSend);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                txtMsg.setText("");
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        }
    }
    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
   public static void receiveMessage(String msg, JPanel panel) throws IOException {
       if (msg.matches(".*\\.(png|jpe?g|gif)$")) {
           String[] parts = msg.split("[-]");
           String name = parts[0];
           String imageUrl = parts[1];

           JPanel hBoxName = new JPanel();
           hBoxName.setLayout(new FlowLayout(FlowLayout.LEFT));
           JLabel textName = new JLabel(name);
           hBoxName.add(textName);

           ImageIcon imageIcon = new ImageIcon(imageUrl);
           Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
           JLabel imageView = new JLabel(new ImageIcon(image));

           JPanel hBox = new JPanel();
           hBox.setLayout(new FlowLayout(FlowLayout.LEFT));
           hBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
           hBox.add(imageView);

           SwingUtilities.invokeLater(() -> {
               panel.add(hBoxName);
               panel.add(hBox);
               panel.revalidate();
               panel.repaint();
           });

       } else {
           String[] parts = msg.split("-");
           String name = parts[0];
           String msgFromServer = parts[1];

           JPanel hBoxName = new JPanel();
           hBoxName.setLayout(new FlowLayout(FlowLayout.LEFT));

           JTextPane textPane = new JTextPane();
           textPane.setText(msgFromServer);
           textPane.setBackground(new Color(171, 184, 195));
           textPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
           textPane.setFont(new Font("Arial", Font.BOLD, 12));
           textPane.setEditable(false);

           JPanel hBox = new JPanel();
           hBox.setLayout(new FlowLayout(FlowLayout.LEFT));
           hBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
           hBox.add(textPane);

           SwingUtilities.invokeLater(() -> {
               panel.add(hBoxName);
               panel.add(hBox);
               panel.revalidate();
               panel.repaint();
           });
       }
   }
    private void attachedButtonOnAction(ActionEvent actionEvent) {
        FileDialog dialog = new FileDialog(frame, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory() + dialog.getFile();
        dialog.dispose();
        sendImage(file);
        System.out.println(file + " chosen.");
    }
    private void sendImage(String msgToSend) {
        // Assuming msgToSend is the URL or path of the image file for simplicity
        ImageIcon imageIcon = new ImageIcon(msgToSend);
        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel imageView = new JLabel(new ImageIcon(image));

        JPanel hBox = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Using FlowLayout to center-align
        hBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        hBox.add(imageView);

        // Assuming chatPanel is your main container (similar to vBox in JavaFX)
        messagePanel.add(hBox);
        messagePanel.revalidate();
        messagePanel.repaint();

        try {
            dataOutputStream.writeUTF(clientName + "-" + msgToSend);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
