import java.util.HashMap;
import java.util.HashSet;

/*
 default value setting
 */
public class Data 
{
    // the number of latent dimensions
    public static int d = 20;
    
    // tradeoff parameters in model D
    public static float d_alpha_u = 0.032f;
    public static float d_alpha_v = 0.032f;
    public static float d_beta_v = 0.032f;

    // tradeoff parameters in model G
    public static float g_alpha_u = 0.001f;
    public static float g_alpha_v = 0.001f;
    public static float g_beta_v = 0.001f;

    // learning rate $\gamma$ in model G and model D
    public static float g_gamma = 0.01f;
    public static float d_gamma = 0.01f;

    // === Input data files
    public static String fnTrainData = "";  // training data file
    public static String fnTestData = "";  // test data file

    // initalized using pre train method?
    public static String g_fnInputModel = "";
    public static String g_fnOutputModel = "";
    public static boolean g_flagInputModel = true;
    public static boolean g_flagOutputModel = false;

    public static String  d_fnInputModel = "";
    public static String d_fnOutputModel = "";
    public static boolean d_flagInputModel = true;
    public static boolean d_flagOutputModel = false;
    
    public static String g_finalModel = "";
    public static String d_finalModel = "";

    //
    public static int n = 0; // number of users
    public static int m = 0; // number of items
    public static int num_train = 0; // number of the total (user, item) pairs in training data
    //
    public static int num_iterations = 0; //scan number over the whole data

    // === Evaluation
    public static int topK = 10; // top k in evaluation
    
    // === training data
    public static HashMap<Integer, HashSet<Integer>> TrainData = new HashMap<Integer, HashSet<Integer>>();


    // === test data
    public static HashMap<Integer, HashSet<Integer>> TestData = new HashMap<Integer, HashSet<Integer>>();

    //Candidate list
    public static HashMap<Integer, HashSet<Integer>> CandidateList = new HashMap<Integer, HashSet<Integer>>();


    // === whole data (items)
    public static HashSet<Integer> ItemSetTrainingData = new HashSet<Integer>();

    public static HashSet<String> UserItemPairsTrain = new HashSet<String>();

    // === some statistics, start from index "1"
    public static int[] userRatingNumTrain;
    public static int[] itemRatingNumTrain;

    // === model parameters to learn, start from index "1"    
    public static float[] g_biasV; // item bias
    public static float[][] g_V;
    public static float[][] g_U;
    
    public static float[] biasV_tmp; // item bias
    public static float[][] V_tmp;
    public static float[][] U_tmp;

    public static float[] d_biasV; // item bias
    public static float[][] d_V;
    public static float[][] d_U;


    public static int batchsize = 64;
    
    public static boolean flagInputCandidateList = false;
    public static boolean flagOutputCandidateList = false;
    public static boolean flagOutputBestRes = false;

    public static String fnOutputBestRes = "";
    public static String fnInputCandidateList = "";
    public static String fnOutputCandidateList = "";

    public static String fnResult = "";
    
    
    public static int d_epoch = 50;
    public static int g_epoch = 50;

}
