package justin;
public class Parser {
	public static int justin = 1;
	
	public static void main (String[] args){
		GetCommand("Crosby87 justin justin");
		GetCommand("Crosby justin justin");
		GetCommand("crosby87 Login Justin");
		GetCommand("Crosby87 remove 1 2");
		GetCommand("Crosby87 bye");
	}
	//Purpose:parse the input and return the command the user wants to output.
	//Input -> Takes a string (our protocol) and parses it into tokens
	//output -> will out put a string with the command name or and error message
	//This is a simple parser. e.g.
	public static String[] GetCommand(String s){
		String[] result = new String[5];
		String[] tokens = s.split(" ");
		if(tokens[0].toLowerCase().compareTo("crosby87") >= 0){
			if(tokens[1].toLowerCase().compareTo("login") >= 0 && tokens.length == 3){
				result = new String[2];
				result[0] = tokens[1].toLowerCase();
				result[1] = tokens[2].toLowerCase();
			}else if(tokens[1].toLowerCase().compareTo("remove") >= 0 && tokens.length == 4){
				result = new String[3];
				result[0] = tokens[1].toLowerCase();
				result[1] = tokens[2].toLowerCase();
				result[2] = tokens[3].toLowerCase();
			}else if(tokens[1].toLowerCase().compareTo("bye") >= 0 && tokens.length == 2){
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
	    for(int i = 0; i < result.length; i++){
	    	//System.out.print(" "+result[i]);
	    }
	    System.out.println();
		return result;
	}

}
