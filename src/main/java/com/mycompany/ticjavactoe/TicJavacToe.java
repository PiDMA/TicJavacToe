package com.mycompany.ticjavactoe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
    private Font smallerFont = new Font("Verdana", Font.BOLD, 20);
    private Font largerFont = new Font("Verdana", Font.BOLD, 50);

    //Game Strings
    private String waitingString = "Waiting for another player to Connect...";
    private String unableToCommunicateWithOpponentString = "Unable to communicate with opponent.";
    private String playerVictoryString = "You have won!";
    private String opponentVictoryString = "Opponent has won.";

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
        while (true) {
            tick();
            painter.repaint();
            if (!circle && !accepted) {
                listenForServerRequest();
            }
        }
    }

    //MAIN
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        //creating an instance of our class to use, naming it something easier to type 100 times
        TicJavacToe tictactoe = new TicJavacToe();
    }

    private void tick() {
        if (errors >= 10) {
            unableToCommunicateWithOpponent = true;
        }

        if (!yourTurn && unableToCommunicateWithOpponent) {
            try {
                int space = dis.readInt();
                if (circle) {
                    spaces[space] = "X";
                } else {
                    spaces[space] = "O";
                }
                checkForEnemyWin();
                yourTurn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listenForServerRequest() {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
            System.out.println("Client has requested to join and we have accepted!");

        } catch (IOException e) {
            errors++;
            e.printStackTrace();
        }
    }

    private boolean connect() {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
            return true;
        } catch (IOException e) {
            errors++;
            System.out.println("Unable to connect to IP Address " + ip + " on port " + port + " | Starting a server");
            e.printStackTrace();
            return false;
        }

    }

    private void render(Graphics g) {
        g.drawImage(board, 0, 0, null);
        if (unableToCommunicateWithOpponent) {
            g.setColor(Color.red);
            g.setFont(smallerFont);
            Graphics2D g2 = (Graphics2D) g;
            //these two lines below allow us to set the anti-aliasing on text since this isnt 2006
            //and also get the WIDTH of the string so we can center it
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int stringWidth = g2.getFontMetrics().stringWidth(unableToCommunicateWithOpponentString);
            g.drawString(unableToCommunicateWithOpponentString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
            return;
        }

        if (accepted) {
            for (int i = 0; i < spaces.length; i++) {
                if (spaces[i].equals("X")) {
                    if (circle) {
                        //the below formula will be used to calculate the proper square to draw images to
                        g.drawImage(redX, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3), null);
                    } else {
                        g.drawImage(blueX, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3), null);
                    }
                } else if (spaces[i].equals("O")) {
                    if (circle) {
                        g.drawImage(blueCircle, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3), null);
                    } else {
                        g.drawImage(redCircle, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3), null);
                    }
                }
            }
            if (won || enemyWon) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(10));
                g.setColor(Color.BLACK);
                g.drawLine(firstSpot % 3 * lengthOfSpace + 10 * firstSpot % 3 + lengthOfSpace / 2, (int) (firstSpot / 3) * lengthOfSpace + 10 * (int) (firstSpot / 3) + lengthOfSpace / 2, secondSpot % 3 * lengthOfSpace + 10 * secondSpot % 3 + lengthOfSpace / 2, (int) (secondSpot) / 3 + lengthOfSpace / 2);

                g.setColor(Color.RED);
                g.setFont(largerFont);
                if (won) {
                    int stringWidth = g2.getFontMetrics().stringWidth(playerVictoryString);
                    g.drawString(playerVictoryString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
                } else if (enemyWon) {
                    int stringWidth = g2.getFontMetrics().stringWidth(opponentVictoryString);
                    g.drawString(opponentVictoryString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
                }
            }
        } else {
            g.setColor(Color.red);
            g.setFont(normalFont);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int stringWidth = g2.getFontMetrics().stringWidth(waitingString);
            g.drawString(unableToCommunicateWithOpponentString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);

        }
    }

    private void checkForEnemyWin() {
        System.out.println("test");
    }

    private class Painter extends JPanel implements MouseListener {

        public Painter() {
            setFocusable(true);
            requestFocus();
            setBackground(Color.WHITE);
            addMouseListener(this); //Keep an eye on this as a leak in the constructor
        }

        @Override
        public void paintComponent(Graphics g) {

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

        } catch (Exception e) {
            errors++;
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

}
