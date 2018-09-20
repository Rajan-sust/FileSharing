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
    private JTextArea          textArea;
    private JScrollPane        scrollPane;
    private ObjectOutputStream output;
    private ObjectInputStream  input;
    private Socket             connection;
    private FileInputStream    fin;
    private FileOutputStream   fout;

    private final static String endl = "\n";




    public Client() {
        super("Client");
        createAndShowGUI();
    }

    private void createAndShowGUI() {

        textBox = new JTextField();
        new TextPrompt("Type Filename and press Enter Key...",textBox);

        textBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                textBox.setText( "" );
            }
        });

        textArea = new JTextArea();
        textArea.setBackground(Color.GRAY);
        textArea.setForeground(Color.cyan);
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(22);

        add(textBox,BorderLayout.NORTH);
        add(scrollPane,BorderLayout.CENTER);

        setSize(350,290);
        setVisible(true);
    }

    public void runClient() {
        try {
            connection = new Socket("localhost",1111);
            getStream();
            processConnection();
        }
        catch (Exception e) {}

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
                displayMessage(filename+" is receving from Server");

                fout = new FileOutputStream("/home/rajan/IdeaProjects/FileSharing/src/"+"copy"+filename);

                int ch;

                do{
                    ch = input.readChar();
                    if(ch!=-1){
                        fout.write(ch);
                        fout.flush();
                    }

                }while(ch!=-1);


            }
            catch (Exception ex){}
        }
    }





    private void sendData(String filename) {

        try {

            fin = new FileInputStream("/home/rajan/IdeaProjects/FileSharing/src/"+filename);

            output.writeObject(filename);
            output.flush();

            displayMessage(filename + " is Client to Server");

            int ch;

            do{
                ch = fin.read();
                if(ch!=-1){
                    output.writeChar(ch);
                    output.flush();
                }

            }while(ch!=-1);



        }
        catch (FileNotFoundException e) {
            displayMessage("Woops , File Not Found !");
        }
        catch (IOException e) {}
    }

    private void displayMessage(String msg) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(endl + msg);

            }
        });

    }


}
