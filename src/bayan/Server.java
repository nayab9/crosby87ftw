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
			outputLine = "Enter a command: ";
			out.println(outputLine);
		
			String send = "";
			
			while ((inputLine = inFromClient.readLine()) != null)
			{	
				send = "";
				//debugging print statement, server prints client request
				System.out.println("Client request: " + inputLine);
				
				//call parser to parse the requested command
				String[] response = Parser.GetCommand(inputLine);
				
				//if its a login, and they have never logged in yet
				if (response[0].compareTo("login") == 0 && logged == false)
				{
					logged = true;
					//successful login, create new player, add to player list
					playerList.add(new Player(response[1], this.connection, this.ID));
					
					//build up the string to output
					//can make a class to put this code in to clean it up if needed
					//basically prints that they have logged in, then the number of people
					//currently logged into the system as well as the list of their names
					send = "You requested login." + newline;
					if (playerList.size() > 1)
					{
						send += "Number of people logged in (including you): " + playerList.size() + newline;			
						send += "Here is who is logged in (including you): " + newline;
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
					//server statement for information
					System.out.println("login successful.");
				}
				//if they try to log in again
				else if (response[0].compareTo("login") == 0 && logged == true)
				{
					send = "You are already logged in." + newline;
				}
				
				else if (response[0].compareTo("play") == 0)
				{
					int playerAindex = -1, playerBindex = -1;
					//user requested to play a game with person in response[1]
					//try to set up a game.
					Player A = null, B = null;
					//need a case here for dealing with someone trying to play against themselves
					for (int i = 0; i < playerList.size(); i++)
					{
						Player temp;
						temp = playerList.get(i);
						//if requesting player is already in a game, send error
						if (temp.getThreadId() == this.ID && !temp.isBusy())
						{
							playerAindex = i;
							A = playerList.get(playerAindex);
						}
						//check if requested challenger is available
						else if (temp.getUserName().compareTo(response[1]) == 0 && !temp.isBusy())
						{
							playerBindex = i;
							B = playerList.get(playerBindex);
						}
					}
					//if both players are available
					if (playerAindex > -1 && playerBindex > -1)
					{
						//set players to busy now
						A.setBusy(true);
						B.setBusy(true);
						//create a game with them in it.
						Game game = new Game(A, B);
						String output = game.initialize();
						//add it to global game list
						gameList.add(game);
						//set the output of initialized game
						send += output + newline;
						
						//need to also tell them whos turn it is
						Player turn = game.getTurn();
						send += "It is " + turn.getUserName() + "'s turn." + newline;
						
						sendString(send);
						
						System.out.println("PlayerA socket information to send to: " + A.getSocket().toString());
						System.out.println("PlayerB socket information to send to: " + B.getSocket().toString());
						//special output to the socket of the other player
					}				
					else
					{
						send += "One of you is already in a game or opponent does not exist." + newline;
					}
				}				
				
				else if (response[0].compareTo("remove") == 0)
				{
					
					//send = "You requested remove.";
				}
				else if (response[0].compareTo("bye") == 0)
				{
					//remove player from playerlist, games, etc
					//send message to an opponent if they are in a game with one
					//informing them they won by forfeit
					//that opponent needs to be removed from that game.
					//that game needs to be deleted
					send = "bye";
					out.println(send);
					connection.close();
				}
				else //incorrect command given
				{
					send = " * error message 404 * " + response[0] + newline;
				}
				
				//out.println(send);
				
			}
		}
		
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void sendString(String msg)
	{
		PrintWriter out = null;
		try 
		{
			out = new PrintWriter(connection.getOutputStream(), true);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		out.println(msg);
	}
}