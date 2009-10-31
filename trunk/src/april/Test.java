package april;

import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;


class Test {

  public static void main(String argv[]) {
	  String userNameA = JOptionPane.showInputDialog("Enter user name for Player A");
	  String userNameB = JOptionPane.showInputDialog("Enter user name for Player B");
	  Player a = new Player(userNameA, new Socket(), 1); // dummy info (socket and threadId)
	  Player b = new Player(userNameB, new Socket(), 2); // dummy info (socket and threadId)
	  
	  Game game = new Game(a, b);
	  System.out.print(game.initialize());
	  
	  while(!game.gameOver()) {
		  String input = JOptionPane.showInputDialog("Enter Your Move (n s)\n" +
		  		"n = number of items to remove\n" +
		  		"s = set to remove the items from");
		  StringTokenizer strTok = new StringTokenizer(input, " ");
		  int n = Integer.parseInt(strTok.nextToken());
		  int s = Integer.parseInt(strTok.nextToken());
		  if (game.isValidMove(n, s)) {
			  System.out.println(game.remove(n, s));
		  }
	  }
	  
  }

}