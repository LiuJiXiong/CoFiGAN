import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class ReadCandidateList
{
    public static void readCandidateList() throws IOException 
	{
    	BufferedReader brInputCandidateList = new BufferedReader(new FileReader(Data.fnInputCandidateList)); 
    	String line = null;
    	while ((line = brInputCandidateList.readLine())!=null)
    	{
    		String[] terms = line.split("\\s+|,|;");
    		int userID = Integer.parseInt(terms[0]);
    		int itemID = Integer.parseInt(terms[1]);
    		
			if(Data.CandidateList.containsKey(userID))
	    	{
	    		HashSet<Integer> itemSet = Data.CandidateList.get(userID);
	    		itemSet.add(itemID);
	    		Data.CandidateList.put(userID, itemSet);
	    	}
	    	else
	    	{
	    		HashSet<Integer> itemSet = new HashSet<Integer>();
	    		itemSet.add(itemID);
	    		Data.CandidateList.put(userID, itemSet);
	    	}
    	}
    	brInputCandidateList.close();
    }    
}
