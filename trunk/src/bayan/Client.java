package bayan;

import java.net.*;
import java.io.*;

public class Client 
{
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
		BufferedReader inFromUser = 
			new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Client Initialized . . .");
		
		try 
		{
			//create socket
			Socket connection = new Socket(host, port);
			
			BufferedReader inFromServer = new BufferedReader
				(new InputStreamReader(connection.getInputStream()));				
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			
			String fromServer, fromUser;

			while ((fromServer = inFromServer.readLine()).compareTo("bye") != 0)//((fromServer = inFromServer.readLine()) != null)
			{
				fromServer = fromServer.replace("~", "\n");
				System.out.println("Server - " + fromServer);
	
				fromUser = inFromUser.readLine();
				if (fromUser != null)
				{				
					fromUser = "crosby87 " + fromUser;
					System.out.println("Client - " + fromUser);
					out.println(fromUser);
				}
				
			} //end client input while
			
			connection.close();
		}
		
		catch (IOException f) 
		{
			System.out.println("IOException: " + f);
		}
		catch (Exception g) 
		{
			System.out.println("Exception: " + g);
		}
	}
}