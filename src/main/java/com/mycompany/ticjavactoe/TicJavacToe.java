package com.mycompany.ticjavactoe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Painter;
/**
 *
 * @author davidpinto
 */
public class TicJavacToe implements Runnable {

    //Game Variables
    //Networking
    private String ip = "localhost";
    private Scanner scanner = new Scanner(System.in);
    private int port = 22222;
    private Socket socket;  //endpoints for communcating between user and server
    private ServerSocket serverSocket;

    //Window creation
    private JFrame frame;
    final int WIDTH = 506;
    final int HEIGHT = 527;
    private Thread thread;
    private Painter painter;    //drawing graphics

    //I/O
    private DataOutputStream dos;
    private DataInputStream dis;

    //Images
    private BufferedImage board;
    private BufferedImage redX;
    private BufferedImage blueX;
    private BufferedImage redCircle;
    private BufferedImage blueCircle;

    private String[] spaces = new String[9];
    /*     We'll need to represent our spaces as show below
       | 0 | 1 | 2 |  
       | 3 | 4 | 5 |
       | 6 | 7 | 8 | 
     */

    //Game Logic
    private boolean yourTurn = false;
    private boolean circle = true;
    private boolean accepted = false;
    private boolean unableToCommunicateWithOpponent = false;
    private boolean won = false;
    private boolean enemyWon = false;
    private int errors = 0;

    //Board and line variables lengths/positions.
    private int lengthOfSpace = 160;
    private int firstSpot = -1;
    private int secondSpot = -1;

    //Fonts 
    private Font normalFont = new Font("Verdana", Font.BOLD, 32);
    private Font smallFont = new Font("Verdana", Font.BOLD, 20);
    private Font largeFont = new Font("Verdana", Font.BOLD, 50);

    //Game Strings
    private String waitingString = "Waiting for another player to Connect...";
    private String unableToCommunicateString = "Unable to communicate with opponent.";
    private String playerWonString = "You have won!";
    private String opponentWonString = "Opponent has won.";

    public TicJavacToe() {

        //setting ip and checking the port is valid. IP can have so many variables I'll create a proper validator later
        System.out.println("Please input the IP: ");
        ip = scanner.nextLine();
        System.out.println("Please input the port: ");
        port = scanner.nextInt();

        while (port < 1 && port > 65535) {
            System.out.println("Please enter a valid port number.");
            System.out.println("Please input the poÏrt: ");
            port = scanner.nextInt();
        }

        loadImages();
        
        painter = new Painter();
        painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    //Abstract run method since we've implemented "Runnable" in our class
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        //creating an instance of our class to use, naming it something easier to type 100 times
        TicJavacToe tictactoe = new TicJavacToe();
    }

    private class Painter extends JPanel implements MouseListener{

        public Painter(){
            setFocusable(true);
            requestFocus();
            setBackground(Color.WHITE);
            addMouseListener(this); //Keep an eye on this as a leak in the constructor
        }
        
        
        
        @Override
        public void mouseClicked(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void mousePressed(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void mouseExited(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    
}
    private void loadImages() {
        try {
            board = ImageIO.read(getClass().getResourceAsStream("/board.png"));
            redX = ImageIO.read(getClass().getResourceAsStream("/redX.png"));
            redCircle = ImageIO.read(getClass().getResourceAsStream("/redCircle.png"));
            blueX = ImageIO.read(getClass().getResourceAsStream("/blueX.png"));
            blueCircle = ImageIO.read(getClass().getResourceAsStream("/blueCircle.png"));
//            board = ImageIO.read(getClass().getResourceAsStream("/board.png"));
//            board = ImageIO.read(getClass().getResourceAsStream("/board.png"));

        } catch (Exception e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

}
