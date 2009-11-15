package april;

/**
 * 
 */

/**
 * @author April
 *
 */
public class Game {
	private static int counter;
	private int id;
	private Player playerA;
	private Player playerB;
	private Player turn; // the player who's turn it is
	private int items[];
	private int numOfSets;
	private String newline = "~";
		
	public Game(Player playerA, Player playerB) {
		this.playerA = playerA;
		this.playerB = playerB;
		this.turn = playerA;
		id=counter;
		counter++;
	}

	/**
	 * @param playerA the playerA to set
	 */
	public void setPlayerA(Player playerA) {
		this.playerA = playerA;
	}


	/**
	 * @return the playerA
	 */
	public Player getPlayerA() {
		return playerA;
	}


	/**
	 * @param playerB the playerB to set
	 */
	public void setPlayerB(Player playerB) {
		this.playerB = playerB;
	}


	/**
	 * @return the playerB
	 */
	public Player getPlayerB() {
		return playerB;
	}
	
	public Player getTurn(){
		return this.turn;
	}
	
	public int getID(){
		return id;
	}
	
	/**
	 * Initializes the game with random number of sets (btwn 3 and 5)
	 * and random number of items per set (btwn 1 and 7)
	 * @return String of the sets and sizes of each set
	 */
	public String initialize() {
		StringBuffer buffer = new StringBuffer();
		
		// Initialize the game
		// Random number of sets
		numOfSets = (int)(Math.random() * 3) + 3;
		items = new int[numOfSets];
		// Initialize the number of items in each set
		for(int i = 0; i < numOfSets; i++) {
			items[i] = (int)(Math.random() * 7) + 1;
		}
		
		buffer.append("set  ");
		for(int i = 1; i < numOfSets + 1; i++) {
			buffer.append(i + " ");
		}
		buffer.append(newline);
		
		buffer.append("size ");
		for(int i = 0; i < numOfSets; i++) {
			buffer.append(items[i] + " ");
		}
		buffer.append(newline);
		
		return buffer.toString();
	}

	/**
	 * Removes n items from set s
	 * @return the move made by the current player and the 
	 * result of the move
	 */
	public String remove(int n, int s) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(turn.getUserName() + " takes " + n + " from set " + s);
		buffer.append(newline + "\t");

		items[s-1] -= n;
		
		for(int i = 0; i < numOfSets; i++) {
			buffer.append(items[i] + " ");
		}
		
		if (!gameOver()) {
			if( turn.getThreadId() == playerA.getThreadId() ) {
				turn = playerB;
			} else {
				turn = playerA;
			}			
		} else {
			// Print winner
			buffer.append(newline + turn.getUserName() + " wins.");
		}
		
		return buffer.toString();
	}
	
	/**
	 * Checks whether the given parameters are valid
	 * @param n number of items to be removed
	 * @param s set to remove items from
	 * @return true if it is a valid move, otherwise return false
	 */
	public boolean isValidMove(int n, int s) {
		boolean valid = true; 
		
		if (s > numOfSets) { // checks if s is a valid set number
			valid = false;
		}else if (s <= 0){ 
			valid = false;
		} else if (items[s-1]-n < 0) { // checks there are enough items to be removed
			valid = false;
		} 
		
		return valid;
	}
	
	/**
	 * Check if the current move made the player win the game
	 * @return boolean
	 */
	public boolean gameOver() { //TODO: change back to private
		boolean gameOver = true;
		
		for(int i = 0; i < numOfSets; i++) {
			if (items[i] != 0) {
				gameOver = false;
			}
		}
		
		return gameOver;
	}
}
