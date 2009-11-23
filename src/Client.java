
import java.net.*;
import java.io.*;

public class Client implements Runnable
{
	private Socket connection;
	private int ID;

	public static void main(String[] args)
	{
		//host provided via command line
		String host = args[0];

		//create socket
		Socket connection = null;
		try
		{
			// port number provided by command line
			int port = Integer.parseInt(args[1]);
			connection = new Socket(host, port);
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("Port number must be an integer.");
			System.exit(0);
		}
		catch (IOException e)
		{
			System.out.println("Unable to connect to "+host+" via port "+args[1]+".");
			System.exit(0);
		}

		System.out.println("Client Initialized . . .");

		Runnable runnable1 = new Client(connection, 0);
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
				System.out.println(" COMPLETE.");
			}
			//sending thread
			else if (this.ID == 1)
			{
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
		System.out.println("                       LIST OF DEFAULT COMMANDS                              ");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("help			- brings up this menu obviously.");
		System.out.println("login NAME		- where NAME is your unique login name.");
		System.out.println("remove N S		- N is the number of items to remove from set S.");
		System.out.println("bye			- disconnect from server.");
		System.out.println("games			- displays current ongoing games listed by their ID numbers.");
		System.out.println("who			- displays all currently logged in players who are available.");
		System.out.println("play NAME		- request a game with player NAME.");
		System.out.println("observe X		- allows you to observe game with a game ID of X.");
		System.out.println("unobserve X		- stop observing game with game ID of X");
		/////////////////////////////////secret commands remove these later//////////////////////////////////
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                         LIST OF BONUS COMMANDS                              ");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("who2			- displays all currently logged in players.");
		System.out.println("whoami			- displays detailed information about yourself");
		System.out.println("login NAME PASS		- where NAME is your unique login name and PASS is password.");
		System.out.println("crosby doesnt quit	- win your game instantly (admin only).");
		System.out.println("whisper NAME MSG	- private message MSG to person NAME.");
		System.out.println("bcast PSEUDO MSG	- broadcast MSG with name PSEUDO (admin only).");
		System.out.println("-----------------------------------------------------------------------------");
	}

	public String clientParse(String msg)
	{
		String message = "";
		if (msg.indexOf("CROSBY87 200 OK") > -1)
		{
			message = msg.replace("CROSBY87 200 OK", "");
		}
		else if (msg.indexOf("CROSBY87 400 ERROR") > -1)
		{
			message = msg.replace("CROSBY87 400 ERROR", "");
		}
		return message;
	}
}
