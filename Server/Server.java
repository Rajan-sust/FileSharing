import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends JFrame {

    private JTextField         textBox;
    private ObjectOutputStream output;
    private ObjectInputStream  input;
    private Socket             connection;
    private ServerSocket       server;
    private String             msgFromClient;
    private FileInputStream    fin;
    private FileOutputStream   fout;

    public Server() {

        super("ServerChatBox");
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

        setSize(300,300);
        setVisible(true);
    }

    public void runServer() {

        try {

            server = new ServerSocket(1111);
            waitForConnection();
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


    private void waitForConnection()  {

        System.out.println("Server is waiting........");

        try {
            connection = server.accept();
        } catch (IOException e) {}

        System.out.println("Connected With Ip : " + connection.getInetAddress().getHostName());
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
                fout = new FileOutputStream("/home/rajan/IdeaProjects/FileSharing/src/"+"replica"+filename);

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
            catch (Exception ex) {}
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

                if(ch!=-1) {

                    output.writeChar(ch);
                    output.flush();
                }

            }while(ch!=-1);



        } catch (IOException e) {}
    }


}
