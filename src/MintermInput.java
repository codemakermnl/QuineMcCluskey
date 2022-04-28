import java.util.Arrays;

public class MintermInput implements Comparable<MintermInput> //object to hold information about a minterm when using the Quine-McCluskey
	{
	    // Instance Fields
	    private int[] minDec;
	    private String minBits;
	    private boolean checked;

	    // Constructor
	    public MintermInput(int[] minDec, String minBits)    // creates new minterm object
	    {
	        this.minDec = minDec;						  // values that this minterm covers
	        this.minBits = minBits;						  // bit value for this minterm
	        this.checked = false;

	        //--------- Sort the values in ascending order (bubble sort is okay in this case)
	       
	        /*for (int i = 0; i < values.length - 1; i++)
	        {
	            for (int j = i + 1; j < values.length; j++)
	            {
	                if (values[j] < values[i]) 
	                {
	                    int temp = values[j];
	                    values[j] = values[i];
	                    values[i] = temp;
	                }
	            }
	         }
	         */
	        
	        //Edit: Insertion sort of values
	    	for (int j = 1; j < minDec.length; j++) 
	    	{  
	    		int temp = minDec[j];  
	    		int i = j-1;  
	    		while ( (i > -1) && ( minDec [i] > temp ) )
	    		{  
	    			minDec [i+1] = minDec[i];  
	    			i--;  
	    		}  
	    		minDec[i+1] = temp;  
	    	}  
	    }

	    public String toString() //Returns a String representation of the Minterm Bits in form: m(0, 4, 8, 12) = --00
	    {
	        String minBits = "";
	        for (int i = 0; i < this.minDec.length; i++) 
	        {
	            minBits += this.minDec[i];
	            if (i < this.minDec.length - 1)
	            {
	                minBits += ", ";
	            }
	        }
	        
	        return String.format
	        (
	            "m(%s) = %s",
	            minBits, this.minBits
	        );
	    }


	   //Setters
	    
	    public boolean isChecked() //Returns whether or not this Minterm has been checked
	    {
	        return checked;
	    }
	    
	    public void setChecked() //Labels this Minterm as "checked"
	    {
	         checked = true;
	    }

	    // Getters

	    public int[] getDecimals()  //Returns the decimal values in this Minterm.
	    {
	        return minDec;
	    }

	    public String getBin() //Returns the bit values of this Minterm.
	    {
	        return minBits;
	    }
	  
	    // Other Methods
	    
	    public boolean equals(Object data)  //Determines if this Minterm object is equal to another object.
	    {
	        if (! (data instanceof MintermInput))
	        {
	            return false;
	        }
	        MintermInput minterm = (MintermInput) data;

	        return
	        (
	            Arrays.equals(minterm.minDec, this.minDec) &&
	            this.minBits.equals(minterm.minBits)
	        );
	    }
	    
	    public int compareTo(MintermInput minterm) 
	    {
	        // Lengths of values are the same
	        if (this.minDec.length == minterm.minDec.length)
	        {
	            // Compare by each value
	            for (int i = 0; i < this.minDec.length; i++)
	            {
	                if (this.minDec[i] < minterm.minDec[i])
	                {
	                    return -1;
	                }
	                else if (this.minDec[i] > minterm.minDec[i])
	                {
	                    return 0;
	                }
	             }
	            // Minterm is the same; Compare value
	            return this.minBits.compareTo(minterm.minBits);
	        }
	        return this.minDec.length - minterm.minDec.length;
	    }

	    public MintermInput combine(MintermInput minterm)  //Combines 2 Minterms together if they can be combined
	    {
	        if (this.minBits.equals(minterm.minBits))   // Check if the value is the same; If so, do nothing
	        {
	            return null;
	        }
	        
	        if (Arrays.equals(this.minDec, minterm.minDec))    // Check if the values are the same; If so, do nothing
	        {
	            return null;
	        }
	        
	        // Keep track of the difference between the minterms
	        int bitDiff = 0;
	        StringBuilder output = new StringBuilder();

	        // Iterate through the bits in this Minterm's value
	        for (int i = 0; i < this.minBits.length(); i++) 
	        {
	            // Check if the current bit value differs from the minterm's bit value
	            if (this.minBits.charAt(i) != minterm.minBits.charAt(i)) 
	            {
	                bitDiff += 1;
	                output.append("-");
	            }

	            // There is not a difference
	            else
	            {
	                output.append(this.minBits.charAt(i));
	            }
	            
	            // The difference has exceeded 1
	            if (bitDiff > 1)
	            {
	                return null;
	            }
	        }

	        // Combine the values of this Minterm with the values of minterm
	        int[] mergedMinterms = new int[this.minDec.length + minterm.minDec.length];
	        for (int i = 0; i < this.minDec.length; i++)
	        {
	            mergedMinterms[i] = this.minDec[i];
	        }
	        for (int i = 0; i < minterm.minDec.length; i++)
	        {
	        	mergedMinterms[i + this.minDec.length] = minterm.minDec[i];
	        }

	        return new MintermInput(mergedMinterms, output.toString());
	    }
	}