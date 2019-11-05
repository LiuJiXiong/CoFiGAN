import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Test 
{
    public static float getRating(String model, int u, int i)
    {
        float pred = 0;
        // Test the performance of model G
        if (model.equals("Gen model"))
        {
            pred = Data.g_biasV[i];
            for (int f=0; f<Data.d; f++)
            {
                pred += Data.g_U[u][f]*Data.g_V[i][f];
            }
        }
        //Test the performance of model D
        else if(model.equals("Dis model"))
        {
            pred = Data.d_biasV[i];
            for (int f=0; f<Data.d; f++)
            {
                pred += Data.d_U[u][f]*Data.d_V[i][f];
            }
        }
        return pred;
    }
    public static String test(String model) throws IOException
	{
        // ==========================================================
        BufferedWriter bwOutputCandidateList = null;
        if(Data.flagOutputCandidateList)
        {
            bwOutputCandidateList = new BufferedWriter(new FileWriter(Data.fnOutputCandidateList));
        }
        // TestData: user->items
        // ==========================================================
        float[] PrecisionSum = new float[Data.topK+1];
        float[] RecallSum = new float[Data.topK+1];
        float[] F1Sum = new float[Data.topK+1];
        float[] NDCGSum = new float[Data.topK+1];
        float[] OneCallSum = new float[Data.topK+1];

        // --- calculate the best DCG, which can be used later
        float[] DCGbest = new float[Data.topK+1];
        for (int k=1; k<=Data.topK; k++)
        {
            DCGbest[k] = DCGbest[k-1];
            DCGbest[k] += 1/Math.log(k+1);
        }

        // ---
        int UserNum_TestData = 0;
       
        for(int u=1; u<=Data.n; u++)
        {
            // --- warm-start users
            if ( !Data.TrainData.containsKey(u) || !Data.TestData.containsKey(u))
            {
                continue;
            }
            
            // ---
            UserNum_TestData ++;

            // ---            
            HashSet<Integer> ItemSet_u_TrainData = new HashSet<Integer>();
            if (Data.TrainData.containsKey(u))
            {
                ItemSet_u_TrainData = Data.TrainData.get(u);
            }
            
            // ---
            HashSet<Integer> ItemSet_u_TestData = Data.TestData.get(u);

            // --- the number of preferred items of user $u$ in the test Data
            int ItemNum_u_TestData = ItemSet_u_TestData.size();

            // ===========================================================
            // --- prediction
            HashMap<Integer, Float> item2Prediction = new HashMap<Integer, Float>();
            item2Prediction.clear();

            if(Data.flagInputCandidateList)
            {
            	HashSet<Integer> ItemSet_u_TopKItem = Data.CandidateList.get(u);
            	for( int i : ItemSet_u_TopKItem )
            	{
            		// --- warm-start items
	                if ( !Data.ItemSetTrainingData.contains(i) || ItemSet_u_TrainData.contains(i) )
	                {
                        continue;
                    }
	                item2Prediction.put(i, getRating(model, u, i));
            	}
            }
            else
            {
	            for(int i=1; i<=Data.m; i++)
	            {
	                // --- warm-start items
	                if ( !Data.ItemSetTrainingData.contains(i) || ItemSet_u_TrainData.contains(i) )
	                {
	                    continue;
	                }
	                
                    // --- prediction via inner product
                    item2Prediction.put(i, getRating(model, u ,i));
	            }
            }
			
            // ---
            List<Map.Entry<Integer, Float>> listY =
                    new ArrayList<Map.Entry<Integer, Float>>(item2Prediction.entrySet());
            
			// --- sort
            listY = HeapSort.heapSort(listY, Data.topK);  // using Lei LI's heapsort
           
            // ===========================================================
            // === Evaluation: TopK Result
            // --- Extract the topK recommended items
            int k=1;
            int[] TopKResult = new int [Data.topK+1];
            Iterator<Map.Entry<Integer, Float>> iter = listY.iterator();
            while (iter.hasNext())
            {
                if(k>Data.topK)
                {
                    break;
                }
                
                Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next();
                int itemID = entry.getKey();      
                TopKResult[k] = itemID;
                k++;
                
                // ---
                if(Data.flagOutputCandidateList)
                {                
                	float preRating = entry.getValue();                	
	                String tmp = ",";
	        		String line = Integer.toString(u);
	    			line += tmp + Integer.toString(itemID) + tmp + Float.toString(preRating);
	        		bwOutputCandidateList.write(line);
	        		bwOutputCandidateList.newLine();
                }
            }
            // --- TopK evaluation
            int HitSum = 0;
            float[] DCG = new float[Data.topK+1];
            float[] DCGbest2 = new float[Data.topK+1];
            for(k=1; k<=Data.topK; k++)
            {
                // ---
                DCG[k] = DCG[k-1];
                int itemID = TopKResult[k];
                if ( ItemSet_u_TestData.contains(itemID) )
                {
                    HitSum += 1;
                    DCG[k] += 1 / Math.log(k+1);
                }
                // --- precision, recall, F1, 1-call
                float prec = (float) HitSum / k;
                float rec = (float) HitSum / ItemNum_u_TestData;
                float F1 = 0;
                if (prec+rec>0)
                    F1 = 2 * prec*rec / (prec+rec);
                PrecisionSum[k] += prec;
                RecallSum[k] += rec;
                F1Sum[k] += F1;
                // --- in case the the number relevant items is smaller than k
                if (ItemSet_u_TestData.size()>=k)
                    DCGbest2[k] = DCGbest[k];
                else
                    DCGbest2[k] = DCGbest2[k-1];
                NDCGSum[k] += DCG[k]/DCGbest2[k];
                // ---
                OneCallSum[k] += HitSum>0 ? 1:0;
            }
            // ===========================================================

        }
        
        // --- flush and close
        if(Data.flagOutputCandidateList)
        {
        	bwOutputCandidateList.flush();
        	bwOutputCandidateList.close();
        }
        
        // =========================================================
        // --- Number of users in the training Data
        //System.out.println( "Number of users in the training Data: " + Integer.toString(Data.TrainData.keySet().size()) );
        // --- Number of users in the test Data
        //System.out.println( "Number of users in the test Data: " + Integer.toString(Data.TestData.keySet().size()) );
        // --- Number of warm-start users in the test Data
        //System.out.println( "Number of warm-start users in the test Data: " + Integer.toString(UserNum_TestData) );
		
		String result_str = Float.toString(PrecisionSum[3]/UserNum_TestData) + "\t" + Float.toString(PrecisionSum[5]/UserNum_TestData) + "\t" + Float.toString(PrecisionSum[10]/UserNum_TestData) + "\t" ;
		result_str += Float.toString(NDCGSum[3]/UserNum_TestData) + "\t" + Float.toString(NDCGSum[5]/UserNum_TestData) + "\t" + Float.toString(NDCGSum[10]/UserNum_TestData) +"\n"; 
		return result_str;

        /*
		// --- precision@k
        for(int k=1; k<=Data.topK; k++)
        {
            float prec = PrecisionSum[k]/UserNum_TestData;
            System.out.println("Prec@"+Integer.toString(k)+":"+Float.toString(prec));
        }
        // --- recall@k
        for(int k=1; k<=Data.topK; k++)
        {
            float rec = RecallSum[k]/UserNum_TestData;
            System.out.println("Rec@"+Integer.toString(k)+":"+Float.toString(rec));
        }
        // --- F1@k
        for(int k=1; k<=Data.topK; k++)
        {
            float F1 = F1Sum[k]/UserNum_TestData;
            System.out.println("F1@"+Integer.toString(k)+":"+Float.toString(F1));
        }
        // --- NDCG@k
        for(int k=1; k<=Data.topK; k++)
        {
            float NDCG = NDCGSum[k]/UserNum_TestData;
            System.out.println("NDCG@"+Integer.toString(k)+":"+Float.toString(NDCG));
        }
        // --- 1-call@k
        for(int k=1; k<=Data.topK; k++)
        {
            float OneCall = OneCallSum[k]/UserNum_TestData;
            System.out.println("1-call@"+Integer.toString(k)+":"+Float.toString(OneCall));
        }*/
    }
    // =============================================================
}




