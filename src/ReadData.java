import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class ReadData
{
    public static void readData() throws IOException 
	{
        // --- some statistics
        Data.userRatingNumTrain = new int[Data.n+1]; // start from index "1"
        Data.itemRatingNumTrain = new int[Data.m+1]; // start from index "1"

        // ----------------------------------------------------
        BufferedReader br = new BufferedReader(new FileReader(Data.fnTrainData));
        String line = null;
        while ((line = br.readLine())!=null)
        {
            String[] terms = line.split("\\s+|,|;");
			
			int userID = 0;
			int itemID = 0;
	
			userID = Integer.parseInt(terms[0]);
			itemID = Integer.parseInt(terms[1]);
	
			// --- add to the whole item set
            Data.ItemSetTrainingData.add(itemID);

            // --- statistics, used to calculate the performance on different user groups
            Data.userRatingNumTrain[userID] += 1;
            Data.itemRatingNumTrain[itemID] += 1;

            // ---
            Data.num_train += 1; // the number of total user-item pairs

            // TrainData: user->items
            if(Data.TrainData.containsKey(userID))
            {
                HashSet<Integer> itemSet = Data.TrainData.get(userID);
                itemSet.add(itemID);
                Data.TrainData.put(userID, itemSet);
            }
            else
            {
                HashSet<Integer> itemSet = new HashSet<Integer>();
                itemSet.add(itemID);
                Data.TrainData.put(userID, itemSet);
            }
        }
        br.close();
        // ----------------------------------------------------

        // ----------------------------------------------------
        if (Data.fnTestData.length()>0)
        {
            br = new BufferedReader(new FileReader(Data.fnTestData));
            line = null;
            while ((line = br.readLine())!=null)
            {
                String[] terms = line.split("\\s+|,|;");
				
				int userID = 0;
				int itemID = 0;
	
				userID = Integer.parseInt(terms[0]);
				itemID = Integer.parseInt(terms[1]);
	            
                if(Data.TestData.containsKey(userID))
                {
                    HashSet<Integer> itemSet = Data.TestData.get(userID);
                    itemSet.add(itemID);
                    Data.TestData.put(userID, itemSet);
                }
                else
                {
                    HashSet<Integer> itemSet = new HashSet<Integer>();
                    itemSet.add(itemID);
                    Data.TestData.put(userID, itemSet);
                }
            }
            br.close();
        }
        // ----------------------------------------------------
    }    
}
