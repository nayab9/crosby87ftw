package justin;
public class Parser {
	public static int justin = 1;
	
	public static void main (String[] args){
		GetCommand("Crosby87 justin justin");
		GetCommand("Crosby justin justin");
		GetCommand("crosby87 Login Justin");
		GetCommand("crosby87 login");
		GetCommand("Crosby87 remove 1 2");
		GetCommand("Crosby87 bye");
		GetCommand("Crosby87");
		GetCommand("Crosby87 games");
		GetCommand("Crosby87 who");
		GetCommand("Crosby87 play");
		GetCommand("Crosby87 play justin");
		GetCommand("Crosby87 observe");
		GetCommand("Crosby87 observe justin");
		GetCommand("Crosby87 unobserve");
		GetCommand("Crosby87 unobserve justin");
	}
	//Purpose:parse the input and return the command the user wants to output.
	//Input -> Takes a string (our protocol) and parses it into tokens
	//output -> will out put a string with the command name or and error message
	//This is a simple parser. e.g.
	public static String[] GetCommand(String s){
		String[] result = new String[5];
		String[] tokens = s.split(" ");
		if(tokens.length > 1){
			if(tokens[0].toLowerCase().compareTo("crosby87") == 0){
				//check for login command
				if(tokens[1].toLowerCase().compareTo("login") == 0 && tokens.length == 3){
					result = new String[2];
					result[0] = tokens[1].toLowerCase();
					result[1] = tokens[2].toLowerCase();
				//check for play command
				}else if(tokens[1].toLowerCase().compareTo("play") == 0 && tokens.length == 3){
					result = new String[2];
					result[0] = tokens[1].toLowerCase();
					result[1] = tokens[2].toLowerCase();
				//check for observe
				}else if(tokens[1].toLowerCase().compareTo("observe") == 0 && tokens.length == 3){
					result = new String[2];
					result[0] = tokens[1].toLowerCase();
					result[1] = tokens[2].toLowerCase();
					//check for observe
				}else if(tokens[1].toLowerCase().compareTo("unobserve") == 0 && tokens.length == 3){
					result = new String[2];
					result[0] = tokens[1].toLowerCase();
					result[1] = tokens[2].toLowerCase();
			    //check for remove command
				}else if(tokens[1].toLowerCase().compareTo("remove") == 0 && tokens.length == 4){
					result = new String[3];
					result[0] = tokens[1].toLowerCase();
					result[1] = tokens[2].toLowerCase();
					result[2] = tokens[3].toLowerCase();
				//check for bye command
				}else if(tokens[1].toLowerCase().compareTo("bye") == 0 && tokens.length == 2){
					result = new String[1];
					result[0] = tokens[1].toLowerCase();
				//check for games command, no arguments
				}else if(tokens[1].toLowerCase().compareTo("games") == 0 && tokens.length == 2){
					result = new String[1];
					result[0] = tokens[1].toLowerCase();
				//check for who command,no arguments
				}else if(tokens[1].toLowerCase().compareTo("who") == 0 && tokens.length == 2){
					result = new String[1];
					result[0] = tokens[1].toLowerCase();
				}else{
					result = new String[1];
					result[0] = "Error incorrect command!";
				}
			}else{
				result = new String[1];
				result[0] = "Error Incorrect Protocol!";
			}
		}else{
			result = new String[1];
			result[0] = "Error not enough arguments!";
		}
	    for(int i = 0; i < result.length; i++){
	    	System.out.print(" "+result[i]);
	    }
	    System.out.println();
		return result;
	}

}
