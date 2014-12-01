
package Juego;

import Juego.ai.AiSolver;
import Juego.dataobjects.ActionStatus;
import Juego.game.Board;
import Juego.dataobjects.Direction;


import java.io.IOException;
import java.io.InputStreamReader;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.json.JSONException;
import org.json.JSONObject;



@SuppressWarnings("serial")
public class ConsoleGame extends JFrame  {
    
    private JTextArea textArea;
     
    private JButton buttonStart = new JButton("Start");
    private JButton buttonClear = new JButton("Clear");
     
    private PrintStream standardOut;    
    
    private static int count = 0;
    
    private static boolean play = true;
    
    public ConsoleGame(){
        super("2048");
         
        textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
         
        // keeps reference of standard output stream
        standardOut = System.out;
         
        // re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);
 
        // creates the GUI
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;
         
        add(buttonStart, constraints);
         
        constraints.gridx = 1;
        add(buttonClear, constraints);
         
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
         
        add(new JScrollPane(textArea), constraints);
         
        // adds event handler for button Start
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                
                //printLog();
                ejecutarPrograma();

            }
        });
         
        // adds event handler for button Clear
        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // clears the text area
                try {
                    textArea.getDocument().remove(0,
                            textArea.getDocument().getLength());
                    standardOut.println("Text area cleared");
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
         
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);    // centers on screen
    }

        /**
     * Prints log statements for testing in a thread
     */
    private void printLog() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Time now is " + count);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    
        private void ejecutarPrograma() {
            
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (play == true) {

                try {
                    redHat();
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(ConsoleGame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ConsoleGame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JSONException ex) {
                    Logger.getLogger(ConsoleGame.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
            }
        });
        thread.start();
    }
    

    
       public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConsoleGame().setVisible(true);
            }
        });
    }
       

  
//    public static void main(String[] args) throws CloneNotSupportedException {
//        
//        System.out.println("The 2048 Game in JAVA!");
//        System.out.println("======================");
//        System.out.println();
//        try {
//            redHat();
//        } catch (Exception e) {
//            System.out.println("Wrong choice");
//        }
        
//        while(true) {
//            printMenu();
//            int choice;
//            try {
//                Scanner sc = new Scanner (System.in);     
//                choice = sc.nextInt();
//                switch (choice) {
//                    case 1:  playGame();
//                             break;
//                    case 2:  calculateAccuracy();
//                             break;
//                    case 3:  redHat();
//                             break;
//                    case 4:  return;
//                    default: throw new Exception();
//                }
//            }
//            catch(Exception e) {
//                System.out.println("Wrong choice");
//            }
//        }
//    }
    
    
     private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    // Desde el Trabajo CON PROXY   
    SocketAddress addr = new InetSocketAddress("proxy.corp.globant.com", 3128);
    Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
    
    URL url2 = new URL(url);
    URLConnection conn = url2.openConnection(proxy);
    InputStream is = conn.getInputStream();
       
    // Desde la Casa SIN PROXY   
    //InputStream is = new URL(url).openStream();
    
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
   }
  
   public static void movementJson(Board game, Direction hint) throws IOException, JSONException {
       int mov;
       mov = dirToInt(hint);
       String Session = game.getSession();
       JSONObject json = readJsonFromUrl("http://nodejs2048-universidades.rhcloud.com/hi/state/" + Session + "/move/" + mov + "/json");
       game.setBoard(json.get("grid").toString());
       setBoardStatus(game, json);
       printFullBoard(game, json, hint);
   }
    



    public static void printMenu() {
        System.out.println();
        System.out.println("Choices:");
        System.out.println("1. Play the 2048 Game");
        System.out.println("2. Run 100 Games with local API");
        System.out.println("3. Red Hat");
        System.out.println("4. Quit");
        System.out.println();
        System.out.println("Enter a number from 1-6:");
    }
    
    
      static void updateProgress(double percent) {

    StringBuilder bar = new StringBuilder("[");

    for(double i = 0; i < 50; i++){
        if( i < (percent/2)){
            bar.append("=");
        }else if( i == (percent/2)){
            bar.append(">");
        }else{
            bar.append(" ");
        }
    }

    bar.append("]   " + percent + "%     ");
    System.out.print("\r" + bar.toString());
  }

 
 
    
    
    public static void calculateAccuracy() throws CloneNotSupportedException {
        int wins = 0;
        int total = 100;
        int moves = 0;
        int movesTotal = 0;
        int tiempoTotal = 0;
        System.out.println("Running "+total+" games to estimate the accuracy:");

        
        for(int i=0;i<total;++i) {
            long startTime = System.nanoTime(); 
            System.out.println("Start Time: " + startTime);
            AiSolver.cache.clear();
            int hintDepth = 5;
            Board theGame = new Board(hintDepth);
            Direction hint = AiSolver.findBestMove(theGame);
            ActionStatus result=ActionStatus.CONTINUE;
            while(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE) {
                double y = moves;
                double x = y/10;
                updateProgress(x);
                moves++;
                result=theGame.action(hint);

                if(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE ) {
                    hint = AiSolver.findBestMove(theGame);
                }
            }

            if(result == ActionStatus.WIN) {
                ++wins;
                System.out.println("Game "+(i+1)+" - won in " + moves + " moves.");
                
                long estimatedTime = System.nanoTime() - startTime;
                System.out.println("Elapsed Time: " + estimatedTime);
                tiempoTotal = tiempoTotal + (int)(estimatedTime/100000000);
                movesTotal = movesTotal + moves;
                moves = 0;
            }
            else {
                System.out.println("Game "+(i+1)+" - lost in " + moves + " moves.");
                long estimatedTime = System.nanoTime() - startTime;
                System.out.println("Elapsed Time: " + estimatedTime);
                tiempoTotal = tiempoTotal + (int)(estimatedTime/100000000);
                movesTotal = movesTotal + moves;
                moves = 0;
            }
        }
        
        System.out.println(wins+" wins out of "+total+" games.");
        System.out.println("Tiempo Total Promedio: " + tiempoTotal / total);
        System.out.println("Movimientos Totales Promedio: " + movesTotal / total);
    }
    
    public static int dirToInt (Direction hint){
        if (hint==Direction.UP){
            return 0;
        }
        else if (hint==Direction.RIGHT){
            return 1;
        }
        else if (hint==Direction.DOWN){
            return 2;
        }
        else{
            return 3;
        }

        
    }
    
    public static void setBoardStatus (Board game,JSONObject json) throws IOException, JSONException{
        game.setScore(json.get("score").toString());
        game.setWon(json.get("won").toString());
        game.setOver(json.get("over").toString());
        game.setMoves(json.get("moves").toString());
    }
    
    public static void printFullBoard(Board game, JSONObject json, Direction hint){
        printBoard(game.getBoardArray(), game.getScore(), hint);
        System.out.println("Movimiento N: " + game.getMoves());
        System.out.println("Session Id " + game.getSession());
        count++; 
    }
    
    public static void metodoC(){
        
    }
    
    public static void redHat() throws CloneNotSupportedException, IOException, JSONException{
        
        int hintDepth = 5;
        
        System.out.println("Running Red Hat Game: ");
        
        JSONObject json = readJsonFromUrl("http://nodejs2048-universidades.rhcloud.com/hi/start/MTG/json");
        Board game = new Board(json.get("grid").toString());
        game.setSession(json.get("session_id").toString());
        System.out.println("Session: " + game.getSession());
        setBoardStatus(game, json);

        
        Direction hint = AiSolver.findBestMove(game);
        movementJson(game, hint);
        setBoardStatus(game, json);
        printFullBoard(game, json, hint);
        
        while (game.getWon() == false && game.getOver() == false){
            hint = AiSolver.findBestMove(game);
            movementJson(game, hint);
            
        }
        play = false;
        System.out.println("=====================================");
        System.out.println("Finish Red Hat! ");
        System.out.println("=====================================");
        System.out.println("Won: " + game.getWon() );
        System.out.println("Over: " + game.getOver());
        System.out.println("Score: " + game.getScore());
        System.out.println("Total Number of Movements: " + game.getMoves());
        System.out.println("Session Id: " + game.getSession());
    }
    

    public static void playGame() throws CloneNotSupportedException {
        System.out.println("Play the 2048 Game!"); 
        System.out.println("Use 8 for UP, 6 for RIGHT, 2 for DOWN and 4 for LEFT. Type a to play automatically and q to exit. Press enter to submit your choice.");
        
        int hintDepth = 4;
        Board theGame = new Board(hintDepth);
        Direction hint = AiSolver.findBestMove(theGame);
        printBoard(theGame.getBoardArray(), theGame.getScore(), hint);


        try {
            InputStreamReader unbuffered = new InputStreamReader(System.in, "UTF8");
            char inputChar;
            
            ActionStatus result=ActionStatus.CONTINUE;
            while(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE) {
                inputChar = (char)unbuffered.read();

                if(inputChar=='\n' || inputChar=='\r') {
                    continue;
                }
                else if(inputChar=='8') {
                    result=theGame.action(Direction.UP);
                }
                else if(inputChar=='6') {
                    result=theGame.action(Direction.RIGHT);
                }
                else if(inputChar=='2') {
                    result=theGame.action(Direction.DOWN);
                }
                else if(inputChar=='4') {
                    result=theGame.action(Direction.LEFT);
                }
                else if(inputChar=='a') {
                    result=theGame.action(hint);
                }
                else if(inputChar=='q') {
                    System.out.println("Game ended, user quit.");
                    break;
                }
                else {
                    System.out.println("Invalid key! Use 8 for UP, 6 for RIGHT, 2 for DOWN and 4 for LEFT. Type a to play automatically and q to exit. Press enter to submit your choice.");
                    continue;
                }
                
                if(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE ) {
                    hint = AiSolver.findBestMove(theGame);
                }
                else {
                    hint = null;
                }
                printBoard(theGame.getBoardArray(), theGame.getScore(), hint);
                
                if(result!=ActionStatus.CONTINUE) {
                    System.out.println(result.getDescription());
                }
            }
        } 
        catch (IOException e) {
            System.err.println(e);
        }
    }
    

    public static void printBoard(int[][] boardArray, int score, Direction hint) {
        System.out.println("-------------------------");
        System.out.println("Score:\t" + String.valueOf(score));
        System.out.println();
        System.out.println("Hint:\t" + hint);
        System.out.println();
        
        for(int i=0;i<boardArray.length;++i) {
            for(int j=0;j<boardArray[i].length;++j) {
                System.out.print(boardArray[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("-------------------------");
    }
}
