package bayan;

import java.net.*;
import java.io.*;

public class Client 
{
	public static void main(String[] args) 
	{
		//host provided via command line
		//String host = args[0];
		
		//hardcoded host
		String host = "localhost";
		
		//port provided via command line
		//int port = Integer.parseInt(args[1]);
		
		//hardcoded port
		int port = 8787;
		
		//instream from standard input for commands
		BufferedReader inFromUser = 
			new BufferedReader(new InputStreamReader(System.in));
		
		String request = "";
		String response = "";
		
		System.out.println("Client Initialized . . .");
		
		try 
		{
			//create socket
			Socket connection = new Socket(host, port);
			
			DataOutputStream outToServer = 
				new DataOutputStream(connection.getOutputStream()); 
			BufferedReader inFromServer = new BufferedReader
				(new InputStreamReader(connection.getInputStream()));
			/*
			 * input original command from user
			 * loop until the user says bye
			 */			
			//request = inFromUser.readLine();
			//while (!response.compareTo("crosby87 bye") == 0)
			//{
			
				//temporary automatically generated requests
				request = "crosby87 login bayan";
				
				//send request to server
				outToServer.writeBytes(request + '\n');
				
				/*
				 * response from server can be multiple lines,
				 * to know its the end of the response, look for
				 * a simple blank line
				 */				
				response = inFromServer.readLine();
				//while (!response.compareTo('\n') == 0)
					System.out.println("FROM SERVER: " + response);
				//} end response while
				
				//request = inFromUser.readLine();
			//} end client input while
			
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