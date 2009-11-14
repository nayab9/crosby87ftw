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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
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
	
			//listener
			if (this.ID == 0)
			{
				while ((fromServer = inFromServer.readLine()).compareTo("bye") != 0)
				{
					fromServer = fromServer.replace("~", "\n");
					System.out.println("Server - " + fromServer);
				}
				connection.close();
			}
			//sender
			else if (this.ID == 1)
			{
				while ((fromUser = inFromUser.readLine()) != null)
				{
					fromUser = "crosby87 " + fromUser;
					System.out.println("Client - " + fromUser);
					out.println(fromUser);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	
	}
}