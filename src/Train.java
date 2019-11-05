import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


public class Train 
{
    /*
    implement the function of numpy.random.choice()
    Arrays 'object' and 'probability' are all from index = 0, 'len' is the number of samples we need.
     */
     @SuppressWarnings("unchecked")
    public static ArrayList<Integer> random_choice_sample(int []object, double []probability, int len)
    {
        HashMap<Integer, Float> map = new HashMap<>();
        for (int i=0; i< object.length; i++)
            map.put(object[i], (float)probability[i]);

        // ---
        List<Map.Entry<Integer, Float>> listY =
                new ArrayList<Map.Entry<Integer, Float>>(map.entrySet());
        listY = HeapSort.heapSort(listY, object.length);  // using Lei LI's heapsort

        int k=1, index=0;
        int[] object_des = new int [object.length];
        float[]probability_margin = new float[probability.length];

        Iterator<Map.Entry<Integer, Float>> iter = listY.iterator();
        while (iter.hasNext())
        {
            Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next();
            object_des[index] = entry.getKey();
            probability_margin[index++] = entry.getValue();
        }
        for (int i= probability_margin.length-2; i>=0; i--)
            probability_margin[i] += probability_margin[i+1];

        ArrayList<Integer> sample_list = new ArrayList<>();
        for (int i=0; i<len; i++)
        {
            // generate one random number
            float _random = (float) Math.random();
            boolean isGet = false;
            for (int j= probability_margin.length-1; j>=0; j--)
            {
                if ( _random <probability_margin[j] )
                {
                    sample_list.add(object_des[j]);
                    isGet = true;
                    break;
                }
            }
            if (! isGet)
                sample_list.add(object_des[0]);
        }
        return sample_list;
    }
	
    @SuppressWarnings("unchecked")
    public static void sample4D(int []d_u_train, int []d_i_train)
    {
        int index = 1;
        for (int uid=1; uid<=Data.n; uid++)    
        {
            if (Data.TrainData.containsKey(uid)==false) continue;
            HashSet itemset = Data.TrainData.get(uid); 
            int len = itemset.size();

            double total = 0;
            int[]iid_tmp = new int[Data.m - len];
            double []score_tmp = new double[Data.m - len];

            int pointer = 0;
			
			// calculate the users' preferences to the unobserved items via the generator
            for (int iid=1; iid<=Data.m; iid++)
            {
                if (itemset.contains(iid)) continue;

                // sample from non positive items for user exploiting model G.
                double pred = Data.g_biasV[iid];
                for (int f=0; f<Data.d; f++)
                {
                    pred += Data.g_U[uid][f]*Data.g_V[iid][f];
                }
                pred = Math.exp(pred);
                total += pred;

                iid_tmp[pointer] = iid;
                score_tmp[pointer++] = pred;
            }
            for (int i=0; i<pointer; i++)
                score_tmp[i] /= total;

            ArrayList<Integer> sample_list = random_choice_sample(iid_tmp, score_tmp, len);
            Iterator<Integer>sample_set_iter = sample_list.iterator();
            Iterator<Integer>positive_set_iter = itemset.iterator();
			
			// construct <user, postive_item, negative_item> triples.
            while(sample_set_iter.hasNext())
            {
                int pos = positive_set_iter.next();
                int sample = sample_set_iter.next();
                d_u_train[index] = uid;
                d_i_train[index] = pos;
                d_u_train[++index] = uid;
                d_i_train[index++] = sample;
            }

        }
    }
    @SuppressWarnings("unchecked")
    public static void disTraining(int[] d_u_train, int []d_i_train)throws Exception
    {
        int pointer = 1;
        int offset = Data.batchsize;
        while (true)
        {
            if (pointer + offset >= 2 * Data.num_train +1)
                offset = (2 * Data.num_train +1) - pointer;

            HashMap<Integer, ArrayList<Double>>users_gradient = new HashMap<>();
            HashMap<Integer, ArrayList<Double>>items_gradient = new HashMap<>();
            HashMap<Integer, Double>items_bias_gradient = new HashMap<>();

            for (int index = pointer; index < (pointer + offset); index++)
            {
                int uid = d_u_train[index];
                int iid = d_i_train[index];
                int label = index % 2;

                double pred = Data.d_biasV[iid];
                for (int f=0; f<Data.d; f++)
                    pred += Data.d_U[uid][f]*Data.d_V[iid][f];
                double f2r_ui = 0 ;
                if (pred >= 0)
                    f2r_ui = 1 - label - Math.exp(-pred)/(1+Math.exp(-pred));
                else
                    f2r_ui = -label + Math.exp(pred)/(1+Math.exp(pred));

                // users embeddings
                if (users_gradient.containsKey(uid))
                {
                    ArrayList<Double> u_pre_gra = users_gradient.get(uid);
                    for (int f=0; f<Data.d; f++)
                        u_pre_gra.set(f, u_pre_gra.get(f) + f2r_ui * Data.d_V[iid][f] + Data.d_alpha_u * Data.d_U[uid][f]);
                }
                else
                {
                    ArrayList<Double> u_pre_gra = new ArrayList<>();
                    for (int f=0; f<Data.d; f++)
                        u_pre_gra.add(f2r_ui * Data.d_V[iid][f] + Data.d_alpha_u * Data.d_U[uid][f]);
                    users_gradient.put(uid, u_pre_gra);

                }

                // items embeddings
                if(items_gradient.containsKey(iid))
                {
                    ArrayList<Double> i_pre_gra = items_gradient.get(iid);
                    for (int f=0; f<Data.d; f++)
                        i_pre_gra.set(f, i_pre_gra.get(f) + f2r_ui * Data.d_U[uid][f] + Data.d_alpha_v * Data.d_V[iid][f]);
                }
                else
                {
                    ArrayList<Double> i_pre_gra = new ArrayList<>();
                    for (int f=0; f<Data.d; f++)
                        i_pre_gra.add(f2r_ui * Data.d_U[uid][f] + Data.d_alpha_v * Data.d_V[iid][f]);
                    items_gradient.put(iid, i_pre_gra);

                }

                // items bias
                if(items_bias_gradient.containsKey(iid))
                {
                    items_bias_gradient.put(iid, items_bias_gradient.get(iid) + f2r_ui + Data.d_beta_v * Data.d_biasV[iid]);
                }
                else
                {
                    items_bias_gradient.put(iid, f2r_ui + Data.d_beta_v * Data.d_biasV[iid]);
                }
            }
			
            // update embeddings and bias_v of the discriminator
            Iterator iterator1 = users_gradient.entrySet().iterator();
            while(iterator1.hasNext())
            {
                Map.Entry entry = (Map.Entry)iterator1.next();
                int uid = (Integer) entry.getKey();
                ArrayList<Double> list = (ArrayList<Double>) entry.getValue();
                for(int f=0; f<Data.d; f++)
                    Data.d_U[uid][f] -= Data.d_gamma * list.get(f);
            }
            // update items embeddings
            Iterator iterator2 = items_gradient.entrySet().iterator();
            while(iterator2.hasNext())
            {
                Map.Entry entry = (Map.Entry)iterator2.next();
                int iid = (Integer) entry.getKey();
                //System.out.println("iid = " + iid);
                ArrayList<Double> list = (ArrayList<Double>) entry.getValue();
                for(int f=0; f<Data.d; f++) {
                    Data.d_V[iid][f] -= Data.d_gamma * list.get(f);
                    //System.out.print( list.get(f) + "   ");
                }
                //System.out.println();
            }
            //update items bias
            Iterator iterator3 = items_bias_gradient.entrySet().iterator();
            while(iterator3.hasNext())
            {
                Map.Entry entry = (Map.Entry)iterator3.next();
                int iid = (Integer)entry.getKey();
                Data.d_biasV[iid] -= Data.d_gamma * (double)entry.getValue();
            }
            //System.out.println(Test.test("Dis model"));
            if (offset < Data.batchsize)
                break;
            pointer = pointer + offset;
        }
    }
	
	// sample positive and negative items for the generator through the discriminator.
    @SuppressWarnings("unchecked")
    public static void sample4G(int length, int uid, int [] i_train_pos, int [] i_train_neg)
    {
        int index =1, pointer=0;
        double [] prob_pos = new double[Data.m];
        double [] prob_neg = new double[Data.m];
        double total_pos=0, total_neg=0;
        int []object = new int[Data.m];
		
		// prediction via inner product of model D's parameters.
        for (int iid=1; iid <= Data.m; iid++)
        {
            double pred = Data.d_biasV[iid];
            for (int f=0; f<Data.d; f++)
            {
                pred += Data.d_U[uid][f]*Data.d_V[iid][f];
            }
            prob_pos[pointer] = Math.exp(pred);
            prob_neg[pointer++] = Math.exp(-pred);
            total_pos += Math.exp(pred);
            total_neg += Math.exp(-pred);
        }
        for (int i=0; i< Data.m; i++)
        {
            prob_pos[i] /= total_pos;
            prob_neg[i] /= total_neg;
            object[i] = i+1;
        }
        ArrayList<Integer> sample_pos_list = random_choice_sample(object, prob_pos, length);
        ArrayList<Integer> sample_neg_list = random_choice_sample(object, prob_neg, length);
        Iterator iterator1 = sample_pos_list.iterator();
        Iterator iterator2 = sample_neg_list.iterator();
        while(iterator1.hasNext())
        {
            i_train_pos[index] = (int)iterator1.next();
            i_train_neg[index++] = (int)iterator2.next();
        }
    }
	
    @SuppressWarnings("unchecked")
    public static void genTraining(int length, int uid, int []i_train_pos, int []i_train_neg)
    {
        double [] user_gradient = new double[Data.d];
        for (int f=0; f<Data.d; f++)
            user_gradient[f] = 0.0;
        HashMap<Integer, ArrayList<Double>>items_gradient = new HashMap<>();
        HashMap<Integer, Double>items_bias_gradient = new HashMap<>();
        for (int index =1; index <= length; index++)
        {
            int i_pos = i_train_pos[index];
            int i_neg = i_train_neg[index];
            double pred_pos = Data.g_biasV[i_pos];
            double pred_neg = Data.g_biasV[i_neg];
            for (int f=0; f<Data.d; f++)
            {
                pred_pos += Data.g_U[uid][f]*Data.g_V[i_pos][f];
                pred_neg += Data.g_U[uid][f]*Data.g_V[i_neg][f];
            }
            double L2X = 1/(1 + Math.exp(pred_neg - pred_pos)) -1 ;
            //user gradient
            for (int f=0; f< Data.d; f++)
                user_gradient[f] += (1 / (double)length) * ( L2X * (Data.g_V[i_pos][f] - Data.g_V[i_neg][f]) + Data.g_alpha_u * Data.g_U[uid][f] );
            //pos item gradient
            if(items_gradient.containsKey(i_pos))
            {
                ArrayList list  = items_gradient.get(i_pos);
                for (int f=0; f<Data.d; f++)
                    list.set(f, (double)list.get(f) + (1 / (double)length) * ( L2X * Data.g_U[uid][f] + Data.g_alpha_v * Data.g_V[i_pos][f]));
                items_gradient.put(i_pos, list);
            }
            else
            {
                ArrayList<Double> list  = new ArrayList<>();
                for (int f=0; f<Data.d; f++)
                    list.add( (1 / (double)length) * (L2X * Data.g_U[uid][f] + Data.g_alpha_v * Data.g_V[i_pos][f]));
                items_gradient.put(i_pos, list);
            }

            //neg item gradients
            if(items_gradient.containsKey(i_neg))
            {
                ArrayList list  = items_gradient.get(i_neg);
                for (int f=0; f<Data.d; f++)
                    list.set(f, (double)list.get(f) + (1 / (double)length) * (- L2X * Data.g_U[uid][f] + Data.g_alpha_v * Data.g_V[i_neg][f]));
                items_gradient.put(i_neg, list);
            }
            else
            {
                ArrayList<Double> list  = new ArrayList<>();
                for (int f=0; f<Data.d; f++)
                    list.add( (1 / (double)length) * (- L2X * Data.g_U[uid][f] + Data.g_alpha_v * Data.g_V[i_neg][f]));
                items_gradient.put(i_neg, list);
            }
            //bias gradient
            if(items_bias_gradient.containsKey(i_pos))
                items_bias_gradient.put(i_pos,items_bias_gradient.get(i_pos)+ (1 / (double)length) * (L2X + Data.g_beta_v * Data.g_biasV[i_pos]));
            else
                items_bias_gradient.put(i_pos, (1 / (double)length) * (L2X + Data.g_beta_v * Data.g_biasV[i_pos]));

            if(items_bias_gradient.containsKey(i_neg))
                items_bias_gradient.put(i_neg, items_bias_gradient.get(i_neg)+ (1 / (double)length) * (-L2X + Data.g_beta_v * Data.g_biasV[i_neg]));
            else
                items_bias_gradient.put(i_neg, (1 / (double)length) * (-L2X + Data.g_beta_v * Data.g_biasV[i_neg]));
        }
        // update for user
        for (int f=0; f< Data.d; f++)
        {
            Data.g_U[uid][f] -= Data.g_gamma  * user_gradient[f];
        }
		
        //pos items and neg items
        Iterator iter1 = items_gradient.entrySet().iterator();
        while(iter1.hasNext()) {
            Map.Entry entry = (Map.Entry) iter1.next();
            int item = (Integer) entry.getKey();
            ArrayList list_pos = (ArrayList<Double>) entry.getValue();
            for (int f = 0; f < Data.d; f++)
            {
                Data.g_V[item][f] -= Data.g_gamma * (double) list_pos.get(f);
            }
        }
        //pos items and neg items bias
        Iterator iter2 = items_bias_gradient.entrySet().iterator();
        while(iter2.hasNext())
        {
            Map.Entry entry = (Map.Entry)iter2.next();
            int item = (Integer)entry.getKey();
            Data.g_biasV[item] -= Data.g_gamma * (double)entry.getValue();
        }
    }
    @SuppressWarnings("unchecked")
    public static void train() throws Exception
    {
        float best_res = 0;        
        int best_epoch = 0;
        Data.biasV_tmp = new float[Data.m+1];        
        Data.V_tmp = new float[Data.m+1][Data.d];
        Data.U_tmp = new float[Data.n+1][Data.d];
		
		System.out.println("Model Epoch\t Precision@3\t Precision@5\t Precision@10\t NDCG@3\t NDCG@5\t NDCG@10");
        for (int epoch=0; epoch < Data.num_iterations; epoch++)
        {
            // Discriminative training
            int []d_u_train = new int[2 * Data.num_train +1];  //from index=1
            int []d_i_train = new int[2 * Data.num_train +1];
            for (int d_epoch = 0; d_epoch < Data.d_epoch; d_epoch++)
            {
				// Sample through the generator every k iteration, and we set k=5
                if(d_epoch % 5 == 0)
                {
                    sample4D(d_u_train, d_i_train);
                }
                disTraining(d_u_train, d_i_train);
            }
            
            String resultStr = Test.test("Dis model");
			System.out.print("Dis "+ String.valueOf(epoch+1) + '\t' +resultStr);

            //Generative training
            for (int g_epoch = 0; g_epoch < Data.g_epoch; g_epoch++)
            {
                for (int uid=1; uid < Data.m; uid++)
                {
                    if (Data.TrainData.containsKey(uid) == false) continue;
					
                    int len = Data.TrainData.get(uid).size();
                    int []i_train_pos = new int[len + 1];     // from index = 1.
                    int []i_train_neg = new int[len + 1]; 
                    sample4G(len, uid, i_train_pos, i_train_neg);  //samples provided by the discriminator.
                    genTraining(len, uid, i_train_pos, i_train_neg);
                }
            }
            String res_ret = Test.test("Gen model");
			System.out.print("Gen "+ String.valueOf(epoch+1) + '\t' +resultStr);
            
            res_ret = res_ret.substring(res_ret.indexOf('\t')+1);
            float prec5 = Float.parseFloat(res_ret.substring(0, res_ret.indexOf('\t')));
            if(prec5 > best_res)
            {
                best_res = prec5;
                best_epoch = epoch;
                if (Data.g_flagOutputModel==true)
                {
                    for (int iid_tmp =1; iid_tmp <= Data.m; iid_tmp++)
                    {
                        Data.biasV_tmp[iid_tmp] = Data.g_biasV[iid_tmp];
                        for (int f_tmp = 0; f_tmp < Data.d; f_tmp++)
                            Data.V_tmp[iid_tmp][f_tmp] = Data.g_V[iid_tmp][f_tmp];
                    }
                    for (int uid_tmp =1; uid_tmp <= Data.n; uid_tmp++)
                    {
                        for (int f_tmp = 0; f_tmp < Data.d; f_tmp++)
                            Data.U_tmp[uid_tmp][f_tmp] = Data.g_U[uid_tmp][f_tmp];
                    }
                }
            }
        }
        
		// save the models
        if (Data.flagOutputBestRes == true)
        {
            for (int iid_tmp =1; iid_tmp <= Data.m; iid_tmp++)
            {
                Data.g_biasV[iid_tmp] = Data.biasV_tmp[iid_tmp] ;
                for (int f_tmp = 0; f_tmp < Data.d; f_tmp++)
                    Data.g_V[iid_tmp][f_tmp] = Data.V_tmp[iid_tmp][f_tmp];
            }
            for (int uid_tmp =1; uid_tmp <= Data.n; uid_tmp++)
            {
                for (int f_tmp = 0; f_tmp < Data.d; f_tmp++)
                    Data.g_U[uid_tmp][f_tmp] = Data.U_tmp[uid_tmp][f_tmp];
            }
            SaveModel.saveModel("Gen model", Data.g_fnOutputModel);
            
            BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Data.fnOutputBestRes, true), "UTF-8"));
            bw2.write("g_alpha_u=" + Float.toString(Data.g_alpha_u) + "\t d_alpha_u=" + Float.toString(Data.d_alpha_u) + ":\n" );
            bw2.write("best_epoch=" + Integer.toString(best_epoch) +"   "+"best_res=" + Float.toString(best_res) +"\n");
            bw2.write("\n");
            bw2.flush();
            bw2.close();
        }
    }
    
}
