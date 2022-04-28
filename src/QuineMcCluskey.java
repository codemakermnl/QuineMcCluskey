import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;

public class QuineMcCluskey //class to handle processing the Quine-McCluskey Algorithm
{
    // Instance Fields
    private String[] vars;
    private int[] minDec;
    private String logicalExp;

    // Constructor

    public QuineMcCluskey(String[] vars, int[] minDec) //new QM object to process the Quine-McCluskey Algorithm
    {
        this.vars = vars;
        this.minDec = minDec;
        this.logicalExp = getSimplified(false);
    }

    // Helper Methods

    /**
     * Returns the binary representation of the specified value.
     *
     * @param value The int value to turn into a binary digit.
     * @returns String
     */
    private String getBin(int minBits) 
    {
        // Convert the value into a binary number
        // Then add extra 0's to the beginning to match how many vars there are
        String output = Integer.toBinaryString(minBits);
        for (int i = output.length(); i < this.vars.length; i++)
        {
        	output = "0" + output;
        }
        return output;
    }

    // Grouping Methods

    /**
     * Creates the initial grouping for the bits from the values
     * given to the Quine-McCluskey Algorithm.
     *
     * @returns Minterm[][]
     */
    
    private MintermInput[][] initialGroup() 
    {
        // Keep track of groups by an array of linked lists
        LinkedList<MintermInput>[] groups = new LinkedList[this.vars.length + 1];
        
        for (int i = 0; i < groups.length; i++)
        {
            groups[i] = new LinkedList<MintermInput>();
        }

        // Iterate through values
        for (int minBits: this.minDec) 
        {
            // Count number of 1's in value's bit equivalent
            int count = 0;
            String bits = getBin(minBits);
            for (int i = 0; i < bits.length(); i++)
            {
                if (bits.charAt(i) == '1')
                {
                    count += 1;
                }
            }
            // Add count to proper group
            groups[count].add(new MintermInput(new int[] {minBits}, bits));
        }

        // Turn the groups into 2-dimensional array
        MintermInput[][] groupsArray = new MintermInput[groups.length][];
        
        for (int i = 0; i < groups.length; i++) 
        {
            groupsArray[i] = new MintermInput[groups[i].size()];
            for (int j = 0; j < groups[i].size(); j++)
            {
                groupsArray[i][j] = groups[i].get(j);
            }
        }
        return groupsArray;
    }

    /**
     * Creates a power set of all valid prime implicants that covers the rest of an expression.
     * This is used after the essential prime implicants have been found.
     *
     * @param values An array of int's that the power set must cover.
     * @param primeImplicants An array of Minterms for the prime implicants to check for.
     * @returns Minterm[]
     */
    private MintermInput[] powerSet(int[] minDec, MintermInput[] primeImplicants) 
    {
        if (primeImplicants.length == 0)
        {
            return new MintermInput[] {};
        }
        // Get the power set of all the prime implicants
        LinkedList<LinkedList<MintermInput>> powerset = new LinkedList<>();

        // Iterate through the decimal values from 1 to 2 ** size - 1
        for (int i = 1; i < (int)(Math.pow(2, primeImplicants.length)); i++) 
        {
            LinkedList<MintermInput> currentset = new LinkedList<>();
            
            // Get the binary value of the decimal value
            String binValue = Integer.toBinaryString(i);
            for (int j = binValue.length(); j < primeImplicants.length; j++)
            {
                binValue = "0" + binValue;
            }
            
            // Find which indexes have 1 in the binValue string
            for (int index = 0; index < binValue.length(); index++)
            {
                if (binValue.charAt(index) == '1')
                {
                    currentset.add(primeImplicants[index]);
                }
            powerset.add(currentset);
            }
        }

        // Remove all subsets that do not cover the rest of the implicants
        LinkedList<Integer> minDecLeftToList = new LinkedList<>();
        for (int i = 0; i < minDec.length; i++)
        {
        	minDecLeftToList.add(minDec[i]);
        }
        LinkedList<LinkedList<MintermInput>> newPowerset = new LinkedList<>();
        for (LinkedList<MintermInput> subset: powerset) 
        {
            // Get all the values the set covers
            LinkedList<Integer> tempValues = new LinkedList<>();
            for (MintermInput implicant: subset)
            {
                for (int minBits: implicant.getDecimals())
                {
                    if (!tempValues.contains(minBits) && minDecLeftToList.contains(minBits))
                    {
                    	 tempValues.add(minBits);
                    }
                       
                }
            }
            
            // Turn the LinkedList into an array
            int[] tempValuesArray = new int[tempValues.size()];
            for (int i = 0; i < tempValues.size(); i++)
            {
                tempValuesArray[i] = tempValues.get(i);
            }
            
            // Sort tempValuesArray
            for (int i = 0; i < tempValuesArray.length; i++)
            {
                for (int j = i; j < tempValuesArray.length; j++)
                {
                    if (tempValuesArray[j] < tempValuesArray[i]) 
                    {
                        int temp = tempValuesArray[i];
                        tempValuesArray[i] = tempValuesArray[j];
                        tempValuesArray[j] = temp;
                    }
                }
            }

            // Check if this subset covers the rest of the values
            if (Arrays.equals(tempValuesArray, minDec))
            {
                newPowerset.add(subset);
            }
        }
        powerset = newPowerset;

        // Find the minimum amount of implicants that can cover the expression
        LinkedList<MintermInput> minSet = powerset.get(0);
        for (LinkedList<MintermInput> subset: powerset)
        {
            if (subset.size() < minSet.size())
            {
                minSet = subset;
            }
        }
        
        // Turn the minSet into an array
        MintermInput[] minSetArray = new MintermInput[minSet.size()];
        for (int i = 0; i < minSet.size(); i++)
        {
            minSetArray[i] = minSet.get(i);
        }
        
        return minSetArray;
    }
//------------------------END OF MAX PART------------------------
    // Compare Methods

    /**
     * Returns an array of all the prime implicants for the expression.
     *
     * @returns Minterm[]
     */
    private MintermInput[] getPrimeImplicants() 
    {
        return getPrimeImplicants(initialGroup());
    }

    /**
     * Returns an array of all the prime implicants for the expression.
     *
     * @param groups A 2-dimensional array of minterms separated into groups
     * @returns Minterm[]
     */
    private MintermInput[] getPrimeImplicants(MintermInput[][] groups)
    {
        // If there is only 1 group, return all minterms in it
        if (groups.length == 1)
        {
            return groups[0];
        }
        // Try comparing the rest
        else 
        {
            // Only run this if groups.length - 1 is greater than 0
            if (groups.length - 1 <= 0)
            {
                return new MintermInput[] {};
            }
                
            LinkedList<MintermInput> unchecked = new LinkedList<MintermInput>();
            
            int[] comparisons = new int[(groups.length - 1 > 0)? groups.length - 1: 0];
            for (int i = 0; i < comparisons.length; i++)
            {
                comparisons[i] = i;
            }
            LinkedList<MintermInput>[] newGroups = new LinkedList[comparisons.length];
            for (int i = 0; i < newGroups.length; i++)
            {
                newGroups[i] = new LinkedList<MintermInput>();
            }
            for (int compare: comparisons) 
            {
            	MintermInput[] group1 = groups[compare];
            	MintermInput[] group2 = groups[compare + 1];

                // Compare every term in group1 with every term in group2
                for (MintermInput term1: group1)
                {
                    for (MintermInput term2: group2)
                    {
                        // Try combining it
                    	MintermInput term3 = term1.combine(term2);

                        // Only add it to the new group if term3 is not null
                        //  term3 will only be null if term1 and term2 could not
                        //  be combined
                        if (term3 != null) 
                        {
                            term1.setChecked();
                            term2.setChecked();
                            if (! newGroups[compare].contains(term3))
                            {
                                newGroups[compare].add(term3);
                            }
                        }
                     }
                 }
            }

            // Turn the newGroups into a 2-dimensional array
            MintermInput[][] newGroupsArray = new MintermInput[newGroups.length][];
            for (int i = 0; i < newGroups.length; i++) 
            {
                newGroupsArray[i] = new MintermInput[newGroups[i].size()];
                for (int j = 0; j < newGroups[i].size(); j++)
                {
                    newGroupsArray[i][j] = newGroups[i].get(j);
                }
            }

            // Add unchecked minterms
            for (MintermInput[] group: groups)
            {
                for (MintermInput term: group)
                {
                    if (!term.isChecked() && !unchecked.contains(term))
                    {
                        unchecked.add(term);
                    }
                }
             }
            
            // Add recursive call
            for (MintermInput term: getPrimeImplicants(newGroupsArray))
            {
                if (!term.isChecked() && !unchecked.contains(term))
                {
                    unchecked.add(term);
                }
            }
            
            // Turn the unchecked into an array
            MintermInput[] uncheckedArray = new MintermInput[unchecked.size()];
            for (int i = 0; i < unchecked.size(); i++)
            {
                uncheckedArray[i] = unchecked.get(i);
            }
            return uncheckedArray;
        }
    }

    // Solving Methods

    /**
     * Solves for the expression returning the minimal amount of prime implicants needed
     * to cover the expression.
     *
     * @returns Minterm[]
     */
    private MintermInput[] solve() 
    {

        // Get the prime implicants
    	MintermInput[] primeImplicants = getPrimeImplicants();

        // Keep track of values with only 1 implicant
        //  These are the essential prime implicants
        LinkedList<MintermInput> essentialPrimeImplicants = new LinkedList<MintermInput>();
        boolean[] minDecChecked = new boolean[this.minDec.length];
        for (int i = 0; i < this.minDec.length; i++)
        {
            minDecChecked[i] = false;
        }
        
        for (int i = 0; i < minDec.length; i++) 
        {
            int minBits = minDec[i];

            // Count how many times the current minterm value is used
            int checks = 0;
            MintermInput last = null;
            for (MintermInput minterm: primeImplicants)
            {
                boolean found = false;
                for (int j = 0; j < minterm.getDecimals().length; j++)
                {
                    if (minBits == minterm.getDecimals()[j]) 
                    {
                        found = true;
                        break;
                    }
                }
                if (found) 
                {
                    checks += 1;
                    last = minterm;
                }
            }
            
            // If there is only 1 use, this is an essential prime implicant
            if (checks == 1 && !essentialPrimeImplicants.contains(last)) 
            {
                for (int lv = 0; lv < last.getDecimals().length; lv++)
                {
                    for (int v = 0; v < minDec.length; v++)
                    {
                        if (last.getDecimals()[lv] == minDec[v])
                        {
                            minDecChecked[v] = true;
                            break;
                        }
                    }        
                 }
                essentialPrimeImplicants.add(last);
             }
        }

        // Turn the essentialPrimeImplicants into an array
        MintermInput[] essentialPrimeImplicantsArray = new MintermInput[essentialPrimeImplicants.size()];
        for (int i = 0; i < essentialPrimeImplicants.size(); i++) 
        {
            essentialPrimeImplicantsArray[i] = essentialPrimeImplicants.get(i);
        }

        // Check if all values were used
        boolean found = false;
        for (int i = 0; i < minDecChecked.length; i++)
        {
            if (minDecChecked[i]) 
            {
                found = true;
                break;
            }
        }
        // If all values were used, return the essential prime implicants
        if (!found) 
        {
            return essentialPrimeImplicantsArray;
        }
        
        // Keep track of prime implicants that cover as many values as possible
        LinkedList<MintermInput> newPrimeImplicants = new LinkedList<>();
        for (int i = 0; i < primeImplicants.length; i++)
        {
            if (!essentialPrimeImplicants.contains(primeImplicants[i]))
            {
                newPrimeImplicants.add(primeImplicants[i]);
            }
        }
        
        // Turn the new prime implicants into an array
        primeImplicants = new MintermInput[newPrimeImplicants.size()];
        for (int i = 0; i < newPrimeImplicants.size(); i++) 
        {
            primeImplicants[i] = newPrimeImplicants.get(i);
        }
        
        // Check if there is only 1 implicant left (very rare but just in case)
        if (primeImplicants.length == 1) 
        {
        	MintermInput[] finalResult = new MintermInput
        	[
                essentialPrimeImplicantsArray.length + primeImplicants.length
            ];
            for (int i = 0; i < essentialPrimeImplicantsArray.length; i++)
            {
                finalResult[i] = essentialPrimeImplicantsArray[i];
            }
            for (int i = 0; i < primeImplicants.length; i++)
            {
                finalResult[essentialPrimeImplicantsArray.length + i] = primeImplicants[i];
            }
            return finalResult;
        }

        // Create a power set from the remaining prime implicants and check which
        //  combination of prime implicants gets the simplest form
        LinkedList<Integer> minDecLeftToList = new LinkedList<>();
        for (int i = 0; i < minDecChecked.length; i++)
        {
            if (!minDecChecked[i])
            {
            	minDecLeftToList.add(minDec[i]);
            }
        }

        // Turn the values left into an array
        int[] minDecLeft = new int[minDecLeftToList.size()];
        for (int i = 0; i < minDecLeftToList.size(); i++) 
        {
            minDecLeft[i] = minDecLeftToList.get(i);
        }
        
        // Get the power set
        MintermInput[] powerset = powerSet(minDecLeft, primeImplicants);
        
        // Get the final result
        MintermInput[] finalResult = new MintermInput
        [
            essentialPrimeImplicantsArray.length + powerset.length
        ];

        for (int i = 0; i < essentialPrimeImplicantsArray.length; i++)
        {
            finalResult[i] = essentialPrimeImplicantsArray[i];
        }
        for (int i = 0; i < powerset.length; i++)
        {
            finalResult[essentialPrimeImplicantsArray.length + i] = powerset[i];
        }
        return finalResult;
    }

    /**
     * Returns the expression in a readable form.
     */
    public String getSimplified()
    {
        return logicalExp;
    }

    /**
     * Returns the expression in a readable form.
     */
    private String getSimplified(boolean saveVariable) 
    {
        // Get the prime implicants and vars
    	MintermInput[] primeImplicants = solve();

        // Check if there are no prime implicants; Always False
        if (primeImplicants.length == 0)
        {
            return "0";
        }
        
        // Check if there is only 1 prime implicant
        else if (primeImplicants.length == 1)
        {
            // Now check if there are just as many hyphens (-) as there are vars
            int hyphens = 0;
            for (int i = 0; i < primeImplicants[0].getBin().length(); i++)
            {
                if (primeImplicants[0].getBin().charAt(i) == '-')
                {
                    hyphens += 1;
                }
            }
            
            if (hyphens == vars.length)
            {
                return "1";
            }
         }

        String output = "";

        // Iterate through the prime implicants
        for (int i = 0; i < primeImplicants.length; i++) 
        {
        	MintermInput implicant = primeImplicants[i];

            // Determine if parentheses should be added to each minterm's expression
            int hyphens = 0;
            boolean addParenthesis = false;
            for (int j = 0; j < implicant.getBin().length(); j++)
            {
                if (implicant.getBin().charAt(j) == '-')
                {
                    hyphens += 1;
                }
            }
            if (hyphens < this.vars.length - 1)
            {
                addParenthesis = true;
            }
            
            // Add parenthesis if necessary
            if (addParenthesis)
            {
            	output += "(";
            }

            // Iterate through all bits in the implicants value
            for (int j = 0; j < implicant.getBin().length(); j++) 
            {
                String character = String.valueOf(implicant.getBin().charAt(j));
                if (character.equals("0"))
                {
                	output += "NOT";
                }
                if (!character.equals("-"))
                {
                	output += vars[j];
                }       
                // Make sure there are no more hyphens
                hyphens = 0;
                for (int k = j + 1; k < implicant.getBin().length(); k++)
                {
                    if (implicant.getBin().charAt(k) == '-')
                    {
                        hyphens += 1;
                    }
                }
                
                if ((hyphens < implicant.getBin().length() - j - 1) && !character.equals("-"))
                	{
                	output += " AND ";
                	}
            }

            // Add parenthesis if necessary
            if (addParenthesis)
            {
            	output += ")";
            }

            // Combine minterm expressions with an OR statement
            if (i < primeImplicants.length - 1)
            {
            	output += " OR ";
            }
        }
        return output;
    }
}