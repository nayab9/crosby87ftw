package bayan;

import java.net.*;

import java.io.*;
import java.util.*;

public class Client implements Runnable
{
	private Socket connection;
	private int ID;
	
	public static void main(String[] args) 
	{
		//host provided via command line
		//String host = args[0];
		
		//hard coded host for now
		String host = "localhost";
		
		//port provided via command line
		//int port = Integer.parseInt(args[1]);
		
		//hard coded port for now
		int port = 8787;
		int count = 0;
		//instream from standard input for commands

		
		//create socket
		Socket connection = null;
		try 
		{
			connection = new Socket(host, port);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		System.out.println("Client Initialized . . .");
	
		Runnable runnable1 = new Client(connection, 0);
		//count++;
		Thread thread1 = new Thread(runnable1);
		thread1.start();
		
		Runnable runnable2 = new Client(connection, 1);
		Thread thread2 = new Thread(runnable2);
		thread2.start();
	}
	
	Client(Socket s, int i)
	{
		this.connection = s;
		this.ID = i;
	}
	
	public void run()
	{
		try
		{		
			BufferedReader inFromUser = 
				new BufferedReader(new InputStreamReader(System.in));	
			BufferedReader inFromServer = new BufferedReader
				(new InputStreamReader(connection.getInputStream()));
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			
			String fromServer, fromUser;
	
			//TODO: something sketchy might be happening with "bye" for dc
			//listener
			if (this.ID == 0)
			{
				boolean quit = false;
				while ((fromServer = inFromServer.readLine()) != null && quit == false)
				{
					fromServer = fromServer.replace("~", "\n");
					System.out.println(fromServer);
					if (fromServer.indexOf("Goodbye!") > -1)
					{
						quit = true;	
					}
				}
				connection.close();
				
			}
			//sender
			else if (this.ID == 1)
			{
				boolean quit = false;
				while ((fromUser = inFromUser.readLine()) != null && quit == false)
				{
					if (fromUser.compareTo("bye") == 0)
					{
						quit = true;
						fromUser = "crosby87 " + fromUser;
						out.println(fromUser);
					
					}
					else if (fromUser.compareTo("help") == 0)
					{
						printHelp();
					}
					else
					{
						fromUser = "crosby87 " + fromUser;
						out.println(fromUser);
					}
				}
				connection.close();
				System.out.println("No longer taking commands, you are disconnected.");
				
			}
		}
		catch (Exception e)
		{
			//System.out.println(e);
		}
	
	}
	
	public void printHelp()
	{
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                              LIST OF COMMANDS                               ");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("help 			- 	brings up this menu obviously.");
		System.out.println("login NAME 		- 	where NAME is your login name.");
		System.out.println("remove N S 		- 	N is the number of items to remove from set S.");
		System.out.println("bye 			- 	disconnect from server.");
		System.out.println("games 			- 	list of all current ongoing games by their ID numbers.");
		System.out.println("who 			- 	list of all currently logged in players who are available.");
		System.out.println("who2 			- 	list of all currently logged in players.");
		System.out.println("play NAME 		- 	request a game to be created with player NAME.");
		System.out.println("observe X 		- 	allows you to observe game with ID of X.");
		System.out.println("unobserve X 		- 	stop observing game with ID of X");
		/////////////////////////////////secret command////////////////////////////////////////
		System.out.println("crosby doesnt quit 		- 	win your game instantly.");
		System.out.println("-----------------------------------------------------------------------------");
	}
}