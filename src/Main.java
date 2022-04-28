import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main 
{

    private static final String STOP_CHAR = ".";

    public static void main(String[] args)
    {
        new Main().run();
    }

    public void run() 
    {
    	System.out.println("\r\n"
    			+ " ██████  ██    ██ ██ ███    ██ ███████     ███    ███  ██████  ██████ ██      ██    ██ ███████ ██   ██ ███████ ██    ██ \r\n"
    			+ "██    ██ ██    ██ ██ ████   ██ ██          ████  ████ ██      ██      ██      ██    ██ ██      ██  ██  ██       ██  ██  \r\n"
    			+ "██    ██ ██    ██ ██ ██ ██  ██ █████       ██ ████ ██ ██      ██      ██      ██    ██ ███████ █████   █████     ████   \r\n"
    			+ "██ ▄▄ ██ ██    ██ ██ ██  ██ ██ ██          ██  ██  ██ ██      ██      ██      ██    ██      ██ ██  ██  ██         ██    \r\n"
    			+ " ██████   ██████  ██ ██   ████ ███████     ██      ██  ██████  ██████ ███████  ██████  ███████ ██   ██ ███████    ██    \r\n"
    			+ "    ▀▀                                                                                                                  \r\n"
    			+ "");
    	
    	int choice;
        Scanner scanner = new Scanner(System.in);
        
        do 
        {
        	Scanner in = new Scanner(System.in);
        	System.out.println("=================================================== Start Solving! =================================================== \n");
        	System.out.println("STEP 1: VARIABLE SELECTION");
        	System.out.println("Enter your desired letter variable representations for your expression.");
            System.out.print("> ");
            String varsInput = scanner.nextLine();
            System.out.println("Your selected vars are:");
            varsInput = varsInput.replaceAll("\\s+", "");
            varsInput = varsInput.replaceAll(",", "");

            StringBuilder varsStringBuilder = new StringBuilder();

            for( char c : varsInput.toCharArray() )
            {
                if( Character.isAlphabetic(c) )
                {
                    varsStringBuilder.append(c);
                }
            }

            System.out.println("-> "+varsStringBuilder.toString());
            System.out.println("\n=======================================================================================================================\n");
            String vars = varsStringBuilder.toString();
            
            List<Integer> minDec = new ArrayList<>();
  
            // values
            System.out.println("STEP 2: INPUTTING MINTERMS");
        	System.out.println("Enter the minterms of the expression you want to simplify.");
        	System.out.println("(Press enter for each minterm input and press '.' to stop.)\n");
            while(true)
            {
                System.out.print("> Minterm Input: ");
                String minDecInput = scanner.nextLine();
                if( STOP_CHAR.equalsIgnoreCase(minDecInput.trim()) )
                {
                    break;
                }

                try 
                {
                    int minBits = Integer.parseInt(minDecInput);
                    if( minBits > (int) Math.pow(2, vars.length()) - 1 ) 
                    {
                        throw new Exception();
                    }
                    minDec.add(minBits);
                }
                catch (Exception e) 
                {
                    System.out.println("\tThat is either not a number or an invalid decimal given how many vars you have");
                }
            }
            Collections.sort(minDec);
            
            System.out.println("\nThe minterms of your expression are:");
            System.out.println("->"+minDec);
            System.out.println("\n=======================================================================================================================\n");
            
            List<Integer> minTermValues = new ArrayList<>(minDec);

            String[] variableArray = new String[vars.length()];
            for( int i = 0; i < variableArray.length; i++ ) 
            {
                variableArray[i] = vars.charAt(i) + "";
            }

            String minTermQM = new QuineMcCluskey(variableArray, toIntArray(minTermValues)).getSimplified();
    
            minTermQM = minTermQM.replace(" AND ", "").replace(" OR ", " + ")
                    .replace("NOT", "~").replace("NOT", "~");
            
            /*
            int minTermLiterals = 0;

            for( Character char_ : minTermQM.toCharArray() )
            {
                if( Character.isAlphabetic(char_) ) 
                {
                    minTermLiterals++;
                }
            }
            */

            System.out.println("STEP 3: SIMPLIFYING BOOLEAN EXPRESSION\n");
            System.out.println(
                    new StringBuilder("Given: \n\t")
                    .append("ƒ(") 
                    .append(String.join(", ", variableArray))
                    .append(") = Σm(")
                    .append(minTermValues.stream().map(String::valueOf).collect(Collectors.joining(", ")))
                    .append(")")
                    .append("\n\nFinal Answer: \n")
                    .append("\n\t")
                    .append(minTermQM).toString()
            );
          System.out.println("\n======================================================================================================================\n");
          System.out.print("Do you want to try another integer? (0 if YES / 1 if NO)");
          System.out.print("\n> ");
  		  choice = in.nextInt();
  		  System.out.print("\n\n");
        }
        while(choice==0);
    }

    static int[] toIntArray(List<Integer> list)  
    {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list)
            ret[i++] = e;
        return ret;
    }
}
