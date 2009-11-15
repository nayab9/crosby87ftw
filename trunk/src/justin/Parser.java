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
		String[] result = null;
		String[] tokens = s.split(" ");
		for(int i = 0; i < tokens.length; i ++){
			tokens[i] = tokens[i].toLowerCase();
		}
		if(tokens.length > 1){
			if(tokens[0].toLowerCase().compareTo("crosby87") == 0){
		        //check for commands with no arguments
				if(tokens.length == 2){
					result = NoArgument(tokens);
				//check for one argument
				}else if(tokens.length == 3){
					result = OneArgument(tokens);
				//check for two arguments
				}else if(tokens.length == 4){
					result = TwoArgument(tokens);
				}else if(tokens.length > 4){
					result = whisperArg(tokens);
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
	    //for(int i = 0; i < result.length; i++){
	    //	System.out.print(" "+result[i]);
	    //}
	    System.out.println();
		return result;
	}
	
	//used for parsing the login,play,observe, and unobserve command
	public static String[] OneArgument(String[] tokens){
		String[] result;
		if(tokens[1].compareTo("login") == 0 || tokens[1].compareTo("play") == 0 || tokens[1].compareTo("observe") == 0 || tokens[1].compareTo("unobserve") == 0){
			result = new String[2];
			result[0] = tokens[1];
			result[1] = tokens[2];
		}else{
			result = new String[1];
			result[0] = "Error incorrect command";
		}
		return result;
	}
	//used for parsing the bye,games, and who commands
	public static String[] NoArgument(String[] tokens){
		String[] result;
		if(tokens[1].compareTo("bye") == 0 || tokens[1].compareTo("games") == 0 || tokens[1].compareTo("who") == 0 || tokens[1].compareTo("who2") == 0){
			result = new String[1];
			result[0] = tokens[1];
		}else{
			result = new String[1];
			result[0] = "Error incorrect command";
		}
		return result;
	}
	//used for parsing the remove command
	public static String[] TwoArgument(String[] tokens){
		String[] result;
		if( tokens[1].compareTo("remove") == 0 || tokens[1].compareTo("crosby") == 0 || tokens[1].compareTo("whisper") == 0 || tokens[1].compareTo("bcast") == 0){
			result = new String[3];
			result[0] = tokens[1];
			result[1] = tokens[2];
			result[2] = tokens[3];
		}else{
			result = new String[1];
			result[0] = "Error incorrect command";
		}
		return result;
	}
	
	public static String[] whisperArg(String[] tokens){
		String[] result;
		if( tokens[1].compareTo("whisper") == 0 || tokens[1].compareTo("bcast") == 0){
			result = new String[tokens.length-1];
			result[0] = tokens[1];
			result[1] = tokens[2];
			for (int i = 2; i < result.length; i++)
			{
				result[i] = tokens[i+1];
			}
			
		}else{
			result = new String[1];
			result[0] = "Error incorrect command";
		}
		return result;
	}		
		

}
