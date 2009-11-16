package bayan;

import java.net.*;

import java.io.*;

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
			//listening thread
			if (this.ID == 0)
			{
				while ((fromServer = inFromServer.readLine()).compareTo("1337 DISCONNECT") != 0)
				{
					fromServer = fromServer.replace("~", "\n");
					String response = clientParse(fromServer);
					System.out.println(response);
					//System.out.flush();
				}
				System.out.print(" Closing connection. . .");
				connection.close();
				System.out.print(" COMPLETE.");
			}
			//sending thread
			else if (this.ID == 1)
			{
				boolean quit = false;
				while (((fromUser = inFromUser.readLine()).compareTo("bye") != 0))
				{
	
					if (fromUser.compareTo("help") == 0)
					{
						printHelp();
					}

					else
					{
						fromUser = "crosby87 " + fromUser;
						out.println(fromUser);
					}			
				}
				
				fromUser = "crosby87 " + fromUser;
				out.println(fromUser);			
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
		System.out.println("login NAME 		- 	where NAME is your unique login name.");
		System.out.println("remove N S 		- 	N is the number of items to remove from set S.");
		System.out.println("bye 			- 	disconnect from server.");
		System.out.println("games 			- 	displays current ongoing games listed by their ID numbers.");
		System.out.println("who 			- 	displays all currently logged in players who are available.");
		System.out.println("who2 			- 	displays all currently logged in players.");
		System.out.println("play NAME 		- 	request a game with player NAME.");
		System.out.println("observe X 		- 	allows you to observe game with a game ID of X.");
		System.out.println("unobserve X 		- 	stop observing game with game ID of X");
		/////////////////////////////////secret commands remove these later//////////////////////////////////
		System.out.println("crosby doesnt quit 	- 	win your game instantly.");
		System.out.println("whisper NAME MSG 	- 	private message MSG to person NAME.");
		System.out.println("bcast PSEUDO MSG 	- 	broadcast MSG with name PSEUDO (admin command only).");
		System.out.println("-----------------------------------------------------------------------------");
	}
	
	public String clientParse(String msg)
	{
		String message = "";
		if (msg.indexOf("200 OK") > -1)
		{
			message = msg.replace("200 OK", "");
		}
		else if (msg.indexOf("400 ERROR") > -1)
		{
			message = msg.replace("400 ERROR", "");
		}
		return message;
	}
}