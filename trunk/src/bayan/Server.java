package bayan;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable
{
	private Socket connection;
	private String request, response;
	private int ID;
	
	public static void main(String[] args)
	{
		//port provided via command line
		//int port = Integer.parseInt(args[0]);
		
		//hardcoded port
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
			DataOutputStream outToClient = 
				new DataOutputStream(connection.getOutputStream()); 
			
			// read a line from the input stream
			request = inFromClient.readLine();
			
			//while (!response.compareTo("crosby87 bye") == 0)
			//{
			
				System.out.println("Client requested this: " + request);
			      
				//ANY PROCESSING DONE IN HERE USE KEYWORD "synchronized"!
			
				response = "Hi Client, You asked for a login! I am thread #" + this.ID + '\n';
			      
				outToClient.writeBytes(response);
				
				//request = inFromClient.readLine();
			//}
			
			connection.close();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}