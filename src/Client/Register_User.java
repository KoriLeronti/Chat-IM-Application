package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Register_User extends JDialog{
    private JTextField NameField;
    private JTextField PhoneField;
    private JPasswordField passWordField;
    private JPasswordField passwordField2;
    private JPanel RegisterPanel;
    private JPanel Registers;
    private JPanel LOgo;
    private JLabel NameLabel;
    private JLabel PhoneLabel;
    private JLabel PasswordL;
    private JLabel ConfirmL;
    private JLabel Regst;
    private JButton buttonSign;


    public Register_User(LoginInterface parent) {
        super(parent);
        setTitle("New user Registry");
        setContentPane(Registers);
        setMinimumSize(new Dimension(1000, 580));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        buttonSign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
                LoginInterface loginInterface = new LoginInterface(null);
            }
        });
        setVisible(true);
    }

    private void registerUser() {
        String Names = NameField.getText();
        String PhoneNumber = PhoneField.getText();
        String Password = String.valueOf(passWordField.getPassword());
        String ConfirmPassword = String.valueOf((passwordField2.getPassword()));

        if(Names.isEmpty() || PhoneNumber.isEmpty() || Password.isEmpty() || ConfirmPassword.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please Fill all the Fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
        else if(!Password.equals(ConfirmPassword)){
            JOptionPane.showMessageDialog(this, "Confirm password does not match password entered",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
        User = addUserToDatabase(Names, PhoneNumber, Password);
            if (User != null)
                dispose();
            else
                JOptionPane.showMessageDialog(this, "Failed to register new user",
                        "Try Again",
                        JOptionPane.ERROR_MESSAGE);
    }

    public UserInfo User;
    private UserInfo addUserToDatabase(String Names, String PhoneNumber, String Password){
        UserInfo User = null;
        final String DB_URL = "jdbc:mysql://localhost/skychat";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            Statement state = conn.createStatement();
            String sql = "INSERT INTO users (Names, PhoneNumber, Password)" + "VALUES(?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, Names);
            preparedStatement.setString(2,PhoneNumber);
            preparedStatement.setString(3,Password);

            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0){
                User = new UserInfo();
                User.Names = Names;
                User.PhoneNumber = PhoneNumber;
                User.Password = Password;
            }
            state.close();
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return User;
    }
    public static void main(String[] args) {
        Register_User myuser = new Register_User(null);

    }
}
