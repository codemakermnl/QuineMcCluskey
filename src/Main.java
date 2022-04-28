import java.util.*;
import java.util.stream.Collectors;


public class Main
{

    private static final String STOP_CHAR = ".";

    private boolean isRunning = true;

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
        Scanner scanner = new Scanner(System.in);
        do
        {
            doRun(scanner);
        }
        while( isRunning );
    }

    private void doRun(Scanner scanner) {
        Scanner in = new Scanner(System.in);
        System.out.println("=================================================== Start Solving! =================================================== \n");
        System.out.println("STEP 1: VARIABLE SELECTION");
        System.out.println("Enter your desired letter variable representations for your expression.");
        System.out.print("> ");
        String variablesInput = scanner.nextLine();
        System.out.println("Your selected vars are:");
        variablesInput = variablesInput.replaceAll("\\s+", "");
        variablesInput = variablesInput.replaceAll(",", "");

        StringBuilder varsStringBuilder = new StringBuilder();

        for( char c : variablesInput.toCharArray() )
        {
            if( Character.isAlphabetic(c) )
            {
                varsStringBuilder.append(c);
            }
        }

        System.out.println("-> "+varsStringBuilder.toString());
        System.out.println("\n=======================================================================================================================\n");
        String variables = varsStringBuilder.toString();

        List<Integer> minTermsList = getMinTerms( variables.length() );

        // values
        System.out.println("STEP 2: INPUTTING MINTERMS");
        System.out.println("Enter the minterms of the expression you want to simplify.");
        System.out.println("(Press enter for each minterm input and press '.' to stop.)\n");


        System.out.println("\nThe minterms of your expression are:");
        System.out.println("->"+minTermsList);
        System.out.println("\n=======================================================================================================================\n");

        List<Integer> minTermValues = new ArrayList<>(minTermsList);

        String[] variableArray = new String[variables.length()];
        for( int i = 0; i < variableArray.length; i++ )
        {
            variableArray[i] = variables.charAt(i) + "";
        }

        String minTermQM = new QuineMcCluskey(variableArray, toIntArray(minTermValues)).getSimplified();

        minTermQM = minTermQM.replace(" AND ", "").replace(" OR ", " + ")
                .replace("NOT", "~").replace("NOT", "~");

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
        int choice = in.nextInt();

        if( 1 == choice ) {
            isRunning = false;
        }

        System.out.print("\n\n");
    }


    static int[] toIntArray(List<Integer> list)
    {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list)
            ret[i++] = e;
        return ret;
    }

    private List<Integer> getMinTerms(int variablesLength) {
        List<Integer> minTermsList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            System.out.print("> Minterm Input: ");
            String minDecInput = scanner.nextLine();
            if( STOP_CHAR.equalsIgnoreCase(minDecInput.trim()) ){
                break;
            }

            try
            {
                int minBit = Integer.parseInt(minDecInput);
                if( minBit > (int) Math.pow(2, variablesLength) - 1 )
                {
                    throw new Exception();
                }

                if( minTermsList.contains( minBit ) ) {
                    throw new DuplicateBitsException("\tMin bit entered (" + minBit + ") already exists as a minterm. Enter another one.");
                }

                minTermsList.add(minBit);
            }
            catch( DuplicateBitsException duplicateBitsException ) {
                System.out.println(duplicateBitsException.getMessage());
            }
            catch (Exception e)
            {
                System.out.println("\tThat is either not a number or an invalid decimal given how many vars you have");
            }
        }
        Collections.sort(minTermsList);

        return minTermsList;
    }
}