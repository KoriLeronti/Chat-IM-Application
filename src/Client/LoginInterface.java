package Client;

import Controller.ClientFormController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginInterface extends JDialog{
    private JTextField LoginField;
    private JPasswordField passwordField1;
    private JPanel Logins;
    private JLabel Loggs;
    private JLabel LOGINLabel;
    private JLabel PassWord;
    private JButton registerButton;
    private JPanel LoginLogo;
    private JPanel Logo;
    private JButton LOGINButton;
    private String loggedInUserName; // Add a field to store the name of the logged-in user

    public LoginInterface(JFrame parent){
        super(parent);
        setTitle("Login to Application");
        setContentPane(LoginLogo);
        setMinimumSize(new Dimension(1000, 580));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register_User register_user = new Register_User(LoginInterface.this);
                UserInfo User = register_user.User;

            }
        });
        LOGINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Names = LoginField.getText();
                String Password = String.valueOf(passwordField1.getPassword());

                User = getAuthenticationUser(Names, Password);

                if(User != null){
                    dispose();
                    openClientFormController(User);
                }else{
                    JOptionPane.showMessageDialog(LoginInterface.this,
                            "Wrong Login details",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }// TODO: when Login info correct, Should go to the home page/ dashboard

        });
        setVisible(true);
    }
    private void openClientFormController(UserInfo user) {
        ClientFormController clientFormController = new ClientFormController();
        //clientFormController.initializeUI(User);

    }
    public String getLoggedInUserName(){
        return loggedInUserName;
    }
    public static UserInfo User;
    private UserInfo getAuthenticationUser(String Names, String Password){
        UserInfo User = null;

        final String DB_URL = "jdbc:mysql://localhost/skychat";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);

            Statement state = conn.createStatement();
            String sql = "SELECT * From users WHERE Names = ? AND Password = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, Names);
            preparedStatement.setString(2,Password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                User = new UserInfo();
                User.Names = resultSet.getString("Names");
                User.Password = resultSet.getString("Password");

            }
            state.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return User;
    }
    public static void main(String[] args) {
        LoginInterface loginInterface = new LoginInterface(null);
        UserInfo User = LoginInterface.User;
        if(User != null){
            System.out.println("Successfully logged in " + User.Names);

        }
        else{
            System.out.println("Unknown user");
        }
    }
}
