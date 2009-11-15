package bayan;

import java.net.*;
import java.io.*;
import java.util.*;

import justin.*;
import april.*;

public class Server implements Runnable
{
	private Socket connection;
	private int ID;
	//global arraylist for players, volatile so that it is synchronized
	public static volatile ArrayList<Player> playerList = new ArrayList<Player>();
	public static volatile ArrayList<Game> gameList = new ArrayList<Game>();
	public static volatile ArrayList<Thread> threadList = new ArrayList<Thread>();
	
	public static void main(String[] args)
	{
		//port provided via command line later
		//int port = Integer.parseInt(args[0]);
		
		//hardcoded port for now
		int port = 8787;
		
		//variable to increment thread identity
		int count = 0;
		
		try
		{
			ServerSocket socket1 = new ServerSocket(port);
			System.out.println("Server Initialized . . .");
			//server accepts forever
			while (true)
			{
				Socket connection = socket1.accept();
				//create new runnable
				Runnable runnable = new Server(connection, count);
				count++;
				//fire runnable off in a new thread
				Thread thread = new Thread(runnable);
				//start executing thread block code
				//provided by the run() method
				threadList.add(thread);
				thread.start();
			}
		}
		catch (Exception e) {}
	}
	
	Server(Socket s, int i)
	{
		this.connection = s;
		this.ID = i;
	}

	public void run()
	{
		try
		{
			BufferedReader inFromClient = 
				new BufferedReader(new InputStreamReader(connection.getInputStream())); 
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			
			String inputLine, outputLine;
			
			//symbolizes a new line for print formatting at client side.
			String newline = "~";
			//boolean to determine if they are already logged in
			boolean logged = false;
			
			//server asks the client to enter a command
			outputLine = "Welcome to the Game of Nim, type 'help' without quotes for a list of commands." + newline;
			out.println(outputLine);
		
			String send = "";
			
			while ((inputLine = inFromClient.readLine()) != null)
			{	
				send = "";
				//debugging print statement, server prints client request
				System.out.println("RAW UNPARSED CLIENT REQUEST: " + inputLine);
				
				//call parser to parse the requested command
				String[] response = Parser.GetCommand(inputLine);
				
				//if its a login, and they have never logged in yet
				if (response[0].compareTo("login") == 0 && logged == false)
				{
					
					boolean exists = false;
					//successful login, create new player, add to player list
					for (int i = 0; i < playerList.size(); i++)
					{
						Player temp;
						temp = playerList.get(i);
						//if requesting player is already signed in
						if (temp.getUserName().compareTo(response[1]) == 0)
						{
							exists = true;						
						}
					}
					if (exists)
					{
						send = "That username is taken, please pick another one." + newline;
					}
					else
					{	
						logged = true;
						playerList.add(new Player(response[1], this.connection, this.ID));
						send = "Welcome " + response[1] + ", you are now logged in." + newline;
					}
					//default list of current players in the system upon login
/*
					if (playerList.size() > 1)
					{
						send += "Number of people logged in: " + playerList.size() + newline;			
						send += "Here is who is logged in: " + newline;
						for (int i = 0; i < playerList.size(); i++)
						{
							Player temp;
							temp = playerList.get(i);
							if (temp.getThreadId() != this.ID)
							{
								send += "\t" + temp.getUserName() + " {is in game: " + temp.isBusy() + "}" + newline;
							}
						}			
					}
					else
					{
						send += "You are the only person logged in right now." + newline;
					}
*/
					//server statement for information
					
					sendString(send, this.connection);
				}
				//if they try to log in again
				else if (response[0].compareTo("login") == 0 && logged == true)
				{
					send = "You are already logged in." + newline;
					sendString(send, this.connection);
				}
				//do the rest of these clauses need to be surrounded by an if (logged in?)
				else if (response[0].compareTo("play") == 0 && logged == true)
				{
					int playerAindex = -1, playerBindex = -1;
					//user requested to play a game with person in response[1]
					//try to set up a game.
					Player A = null, B = null;
					String playerA = null, playerB = null;
					//TODO: need a case here for dealing with someone trying to play against themselves
					for (int i = 0; i < playerList.size(); i++)
					{
						Player temp;
						temp = playerList.get(i);
						//if requesting player is already in a game, send error
						if (temp.getThreadId() == this.ID && !temp.isBusy())
						{
							playerAindex = i;
							A = playerList.get(playerAindex);
							playerA = A.getUserName();
						}
						//check if requested opponent is available
						else if (temp.getUserName().compareTo(response[1]) == 0 && !temp.isBusy())
						{
							playerBindex = i;
							B = playerList.get(playerBindex);
							playerB = B.getUserName();
						}
					}
					//if both players are available
					if (playerAindex > -1 && playerBindex > -1 && (!(playerA.compareTo(playerB)==0)) )
					{
						//set players to busy now
						A.setBusy(true);
						B.setBusy(true);
						//create a game with them in it.
						Game game = new Game(A, B);
						String output = game.initialize();
						//add it to global game list
						gameList.add(game);
						
						send = "You are now in a game with " + B.getUserName() + "." + newline;
						//set the output of initialized game
						send += output;
						sendString(send, A.getSocket());
						send = "You are now in a game with " + A.getUserName() + "." + newline;
						send += output;
						sendString(send, B.getSocket());
						//need to also tell them whos turn it is
						Player turn = game.getTurn();
						send = "It is " + turn.getUserName() + "'s turn." + newline;
						sendString(send, A.getSocket());
						sendString(send, B.getSocket());

					}				
					else
					{
						send += "Error: one of you is already in a game, opponent does not exist, or you requested yourself." + newline;
						sendString(send, this.connection);
					}
				}
				else if(response[0].compareTo("unobserve") == 0  && logged == true)
				{
					boolean isValid = true;
					int gameid = 0;
					try
					{
						gameid = Integer.parseInt(response[1]);
					}
					catch (NumberFormatException e)
					{
						isValid = false;
					}
					if (isValid)
					{
						Game temp = null;
						Player temp2 = null;
						for(int i = 0; i < gameList.size(); i++){
							if(gameList.get(i).getID() == gameid){
								temp = gameList.get(i);
								i = gameList.size();
							}
						}
						for(int j = 0; j < playerList.size(); j++){
							if(playerList.get(j).getThreadId() == this.ID){
								temp2 = playerList.get(j);
								j = playerList.size();
							}
						}
						if(temp != null && temp2 != null){
							temp.removeObserver(temp2);
							send += "You have successfully disconnect from the Game : "+temp.getID()+ newline;
						}else{
							send += "No such game exists "+newline;
						}
						sendString(send,this.connection);
					}
					else
					{
						sendString("Invalid game ID requested." + newline, this.connection);
					}
				}
				else if(response[0].compareTo("observe") == 0  && logged == true)
				{
					boolean isValid = true;
					int gameid = 0;
					try
					{
						gameid = Integer.parseInt(response[1]);
					}
					catch (NumberFormatException e)
					{
						isValid = false;
					}
					
					if (isValid)
					{
						Game temp = null;
						Player temp2 = null;
						for(int i = 0; i < gameList.size(); i++){
							if(gameList.get(i).getID() == gameid){
								temp = gameList.get(i);
								i = gameList.size();
							}
						}
						for(int j = 0; j < playerList.size(); j++){
							if(playerList.get(j).getThreadId() == this.ID){
								temp2 = playerList.get(j);
								j = playerList.size();
							}
						}
						if(temp != null && temp2 != null && (temp.getPlayerA().getThreadId() != this.ID || temp.getPlayerB().getThreadId() != this.ID))
						//TODO: add a clause in here to not allow them to be an observer more then once, i.e. check if they are already an observer
						{
							temp.addObserver(temp2);
							send += "You are now an observer of Game : "+temp.getID()+ newline;
						}else{
							send += "You can't observe that game, either it doesn't exist or you are a player in it. "+newline;
						}
						sendString(send,this.connection);
					}
					else
					{
						sendString("Invalid game ID requested." + newline, this.connection);
					}
				}
				//who command
				else if(response[0].compareTo("who") == 0  && logged == true)
				{
					if (playerList.size() > 1)
					{
						send += "Total number of people logged into server: " + playerList.size() + newline;			
						send += "Here is who else is logged in and currently available: " + newline;
						for (int i = 0; i < playerList.size(); i++)
						{
							Player temp;
							temp = playerList.get(i);
							if (temp.getThreadId() != this.ID && temp.isBusy() != true)
							{
								String availability = "available";
								if (temp.isBusy())
									availability = "busy";
								
								send += "\t" + temp.getUserName() + " {status: " + availability + "}" + newline;
							}
						}			
					}
					else
					{
						send += "You are the only person logged in right now." + newline;
					}
					sendString(send, this.connection);
				}
				else if(response[0].compareTo("who2") == 0  && logged == true)
				{
					if (playerList.size() > 1)
					{
						send += "Total number of people logged into server: " + playerList.size() + newline;			
						send += "Here is a complete list of who is logged in: " + newline;
						for (int i = 0; i < playerList.size(); i++)
						{
							Player temp;
							temp = playerList.get(i);
							
							String availability = "available";
							if (temp.isBusy())
								availability = "busy";
							
							send += "\t" + temp.getUserName() + " {status: " + availability + "}" + newline;
							
						}			
					}
					else
					{
						send += "You are the only person logged in right now." + newline;
					}
					sendString(send, this.connection);
				}
				//games
				else if (response[0].compareTo("games") == 0  && logged == true)
				{
					if(gameList.size() > 0){
						send += "Current Games:"+ newline;
						for (int i = 0; i < gameList.size(); i++){
							Game temp;
							temp = gameList.get(i);
							send += temp.getID()+" : "+temp.getPlayerA().getUserName() + " vs " + temp.getPlayerB().getUserName() + newline;
						}
					}else{
						send += "There are currently no games."+ newline;
					}
					sendString(send, this.connection);
					
				}
				//remove
				else if (response[0].compareTo("remove") == 0  && logged == true)
				{
					boolean inGame = false;
					int gameID = 0;
					Game game = null, chosen = null;
					
					Player A = null, B = null;
					//find the game the player is in
					for (int i = 0; i < gameList.size(); i++)
					{
						chosen = gameList.get(i);
						//if the person requesting a remove, is actually in a game
						if ( (this.ID == chosen.getPlayerA().getThreadId()) )
						{
							game = gameList.get(i);
							gameID = game.getID();
							inGame = true;
							A = game.getPlayerA();
							B = game.getPlayerB();
						}
						//pretending like i am player A to make life easier
						else if ( (this.ID == chosen.getPlayerB().getThreadId()) )
						{
							game = gameList.get(i);
							gameID = game.getID();
							inGame = true;
							A = game.getPlayerB();
							B = game.getPlayerA();
						}
					}

					//check if its their turn
					if ( inGame && (game.getTurn().getThreadId() == this.ID) )
					{
						int s = 0, n = 0; 
						boolean validInput = true;
						
						try
						{
							s = Integer.parseInt(response[2]);
							n = Integer.parseInt(response[1]);
						}
						
						catch (NumberFormatException e)
						{
							validInput = false;
						}
						
						
						//check if valid move
						if ( validInput && game.isValidMove(n, s) )
						{
							//valid move, so do it
							send += game.remove(n, s) + newline;
							
							//if the game is not over, keep going
							if (!game.gameOver())
							{
								send += "It is " + game.getTurn().getUserName() + "'s turn." + newline;
															
								sendString(send, A.getSocket());
								sendString(send, B.getSocket());
								for (int i = 0; i < game.getObservers().size(); i++)
								{
									Player temp;
									temp = (Player) game.getObservers().get(i);
									sendString(send, temp.getSocket());
								}
							}
							//game is over after that last move.
							else
							{
								//message all observers to say game is now ending
								for (int i = 0; i < game.getObservers().size(); i++)
								{
									Player temp;
									temp = (Player) game.getObservers().get(i);
									sendString(send, temp.getSocket());
								}			
								//tell playerA and B
								sendString(send, A.getSocket());
								sendString(send, B.getSocket());
								
								//delete game
								for (int i = 0; i < gameList.size(); i++)
								{
									if (gameList.get(i).getID() == gameID)
									{
										gameList.remove(i);
									}
								}
								//change playerB status to not busy anymore
								A.setBusy(false);
								B.setBusy(false);
							}
						}
						else
						{
							send += "Invalid move or input, try again." + newline;
							sendString(send, this.connection);
						}					
					}
					else
					{
						send += "Hold your 'horeses' - You are not in a game, or its not your turn." + newline;
						sendString(send, this.connection);
					}
				}
				//secret command
				else if (response[0].compareTo("crosby") == 0  && logged == true)
				{
					boolean inGame = false;
					int gameID = 0;
					Game game = null, chosen = null;
					Player A = null, B = null;
					//find the game the player is in
					for (int i = 0; i < gameList.size(); i++)
					{
						chosen = gameList.get(i);

						//if the person requesting a remove, is actually in a game
						if ( (this.ID == chosen.getPlayerA().getThreadId()) )
						{
							game = gameList.get(i);
							gameID = game.getID();
							inGame = true;
							A = game.getPlayerA();
							B = game.getPlayerB();
							
						}
						//pretending like i am player A to make life easier
						else if ( (this.ID == chosen.getPlayerB().getThreadId()) )
						{
							game = gameList.get(i);
							gameID = game.getID();
							inGame = true;
							A = game.getPlayerB();
							B = game.getPlayerA();
							
						}
					}	
					//check if its their turn
					if ( inGame && response[1].compareTo("doesnt")==0 &&  response[2].compareTo("quit")==0)
					{
						send += A.getUserName() + " automatically wins because he is better then you." + newline;
						send += "CROSBY. DOESNT. QUIT." + newline;
						
						//message all observers to say game is now ending
						for (int i = 0; i < game.getObservers().size(); i++)
						{
							Player temp;
							temp = (Player) game.getObservers().get(i);
							sendString(send, temp.getSocket());
						}			
						//tell playerA and B
						sendString(send, A.getSocket());
						sendString(send, B.getSocket());
						
						//delete game
						for (int i = 0; i < gameList.size(); i++)
						{
							if (gameList.get(i).getID() == gameID)
							{
								gameList.remove(i);
							}
						}
						//change playerB status to not busy anymore
						A.setBusy(false);
						B.setBusy(false);
					}
					
					else
					{
						send += "You are awesome." + newline;
						sendString(send, this.connection);
					}
				}
				//TODO: pretty sure the cleanup works fine, but something sketchy
				//is happening on disconnect, its not properly disconnecting without
				//the client typing an extra command... might be a client.java issue
				else if (response[0].compareTo("bye") == 0)
				{
					boolean inGame = false, observer = false;
					Game game = null;
					Player A = null, B = null;
					int gameID = 0;
					int observerGameID = 0;
					//find if the player is in a game as a player or observer
					for (int i = 0; i < gameList.size(); i++)
					{
						game = gameList.get(i);
						A = game.getPlayerA();
						B = game.getPlayerB();

						//if the person requesting bye, is actually in a game
						if ( (this.ID == game.getPlayerA().getThreadId()) )
						{
							gameID = game.getID();
							inGame = true;
							A = game.getPlayerA();
							B = game.getPlayerB();
						}
						//pretending like i am player A to make life easier
						else if ( (this.ID == game.getPlayerB().getThreadId()) )
						{
							gameID = game.getID();
							inGame = true;
							A = game.getPlayerB();
							B = game.getPlayerA();
						}
						//observer case
						//if this bye request is from a player who is registered in an observer list
						else if ( (this.ID == ((Player) game.getObservers().get(i)).getThreadId() )) 
						{
							observerGameID = game.getID();
							observer = true;
						} 
					}
					//game + observer cleanup
					if ( inGame && observer )
					{
						//game cleanup
						send += A.getUserName() + " is quitting the game, game over, " + 
						B.getUserName() + " wins!" + newline;
				
						//message all observers to say game is now ending due to leaver
						for (int i = 0; i < game.getObservers().size(); i++)
						{
							Player temp;
							temp = (Player) game.getObservers().get(i);
							sendString(send, temp.getSocket());
						}			
						//tell playerB he won.
						sendString(send, B.getSocket());

						//delete game
						for (int i = 0; i < gameList.size(); i++)
						{
							if (gameList.get(i).getID() == gameID)
							{
								gameList.remove(i);
							}
						}
						//change playerB status to not busy anymore
						B.setBusy(false);
						
						//observer cleanup
						Game games = null;
						for (int j = 0; j < gameList.size(); j++)
						{
							games = gameList.get(j);
							
							for (int i = 0; i < games.getObservers().size(); i++)
							{
								Player temp;
								temp = (Player) game.getObservers().get(i);
								if (temp.getThreadId() == this.ID)
								{
									game.getObservers().remove(i);
								}
							}
						}
						send += "Goodbye sir." + newline;
						sendString(send, this.connection);					
						
					}
					//only game cleanup
					else if ( inGame && !observer)
					{
						send += A.getUserName() + " is quitting the game, game over, " + 
						B.getUserName() + " wins!" + newline;
				
						//message all observers to say game is now ending due to leaver
						for (int i = 0; i < game.getObservers().size(); i++)
						{
							Player temp;
							temp = (Player) game.getObservers().get(i);
							sendString(send, temp.getSocket());
						}			
						//tell playerB he won.
						sendString(send, B.getSocket());
						//say bye to player A
						sendString("Goodbye.", this.connection);
						
						//delete game
						for (int i = 0; i < gameList.size(); i++)
						{
							if (gameList.get(i).getID() == gameID)
							{
								gameList.remove(i);
							}
						}
						//change playerB status to not busy anymore
						B.setBusy(false);
					}
					//only observer cleanup
					else if ( observer && !inGame )
					{
						Game games = null;
						for (int j = 0; j < gameList.size(); j++)
						{
							games = gameList.get(j);
							
							for (int i = 0; i < games.getObservers().size(); i++)
							{
								Player temp;
								temp = (Player) game.getObservers().get(i);
								if (temp.getThreadId() == this.ID)
								{
									game.getObservers().remove(i);
								}
							}
						}
						send += "Goodbye!" + newline;
						sendString(send, this.connection);					
					}
					
					else
					{
						send += "Goodbye!" + newline;
						sendString(send, this.connection);
						
					}
					//remove player from playerlist
					for (int i = 0; i < playerList.size(); i++)
					{
						Player temp;
						temp = playerList.get(i);
						if (temp.getThreadId() == this.ID)
						{
							playerList.remove(i);
						}
					}
					//close the connection
					connection.close();
					connection.close();
				}
				else if (logged == false) //incorrect command given
				{
					send = "You must login to use any command other then help." + newline;
					sendString(send, this.connection);
				}		
				else
				{
					send = " * error message 404 * " + response[0] + newline;
					sendString(send, this.connection);
				}		
			}
		}
		
		catch (Exception e)
		{
			//System.out.println(e);
		}
	}
	
	public void sendString(String msg, Socket socket)
	{
		//System.out.println("Requested send to socket information to: " + socket.toString());
	
		PrintWriter out = null;
		try 
		{
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(msg);
			System.out.println("Message sent to client: " + msg);
		} 
		catch (IOException e) 
		{
			//e.printStackTrace();
			System.out.println("Error sending to socket");
		}	
	}
}