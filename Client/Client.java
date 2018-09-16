import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame {

    private JTextField         textBox;
    private ObjectOutputStream output;
    private ObjectInputStream  input;
    private Socket             connection;
    private String             msgFromServer;
    private FileInputStream    fin;
    private FileOutputStream   fout;




    public Client() {
        super("ClientChatBox");
        createAndShowGUI();
    }

    private void createAndShowGUI() {

        textBox = new JTextField();
        new TextPrompt("Type Filename Here...",textBox);

        textBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                textBox.setText( "" );
            }
        });




        add(textBox, BorderLayout.NORTH);

        setSize(250,300);
        setVisible(true);
    }

    public void runClient() {
        try {
            connection = new Socket("127.0.0.1",1111);
            getStream();
            processConnection();
        }
        catch (IOException e) {}

        finally {

            try {
                input.close();
                output.close();
                connection.close();
                fin.close();
                fout.close();

            } catch (IOException e) {}
        }
    }


    private void getStream() {
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {}
    }

    private void processConnection() {

        while(true) {

            try {
                String filename = (String) input.readObject();
                fout = new FileOutputStream("/home/rajan/IdeaProjects/FileSharing/src/"+filename);

                int ch;

                do{
                    ch = input.readChar();
                    if(ch!=-1){
                        fout.write(ch);
                        fout.flush();
                    }

                }while(ch!=-1);

            }
            catch (IOException e) {}
            catch (ClassNotFoundException e) {}
        }
    }





    private void sendData(String filename) {

        try {
            output.writeObject(filename);
            output.flush();

            fin = new FileInputStream("/home/rajan/IdeaProjects/FileSharing/src/"+filename);

            int ch;

            do{
                ch = fin.read();
                if(ch!=-1){
                    output.writeChar(ch);
                    output.flush();
                }

            }while(ch!=-1);
        } catch (IOException e) {}
    }


}
