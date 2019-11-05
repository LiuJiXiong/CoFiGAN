import java.awt.geom.FlatteningPathIterator;
import java.io.*;
import java.util.Arrays;

 
public class ReadConfigurations
{
    public static void readConfigurations(String[]args) throws IOException 
    {
        // read the configurations
        for (int k=0; k < args.length; k++)
        {
            if (args[k].equals("-d")) Data.d = Integer.parseInt(args[++k]);

            else if (args[k].equals("-g_alpha_u")) Data.g_alpha_u = Float.parseFloat(args[++k]);
            else if (args[k].equals("-g_alpha_v")) Data.g_alpha_v = Float.parseFloat(args[++k]);
            else if (args[k].equals("-g_beta_v")) Data.g_beta_v = Float.parseFloat(args[++k]);
            else if (args[k].equals("-g_gamma")) Data.g_gamma = Float.parseFloat(args[++k]);

            else if (args[k].equals("-d_alpha_u")) Data.d_alpha_u = Float.parseFloat(args[++k]);
            else if (args[k].equals("-d_alpha_v")) Data.d_alpha_v = Float.parseFloat(args[++k]);
            else if (args[k].equals("-d_beta_v")) Data.d_beta_v = Float.parseFloat(args[++k]);
            else if (args[k].equals("-d_gamma")) Data.d_gamma = Float.parseFloat(args[++k]);

            else if (args[k].equals("-fnTrainData")) Data.fnTrainData = args[++k];
            else if (args[k].equals("-fnTestData")) Data.fnTestData = args[++k];
            
            else if (args[k].equals("-d_epoch")) Data.d_epoch = Integer.parseInt(args[++k]);
            else if (args[k].equals("-g_epoch")) Data.g_epoch = Integer.parseInt(args[++k]);


            // pre train of model G and D
            else if (args[k].equals("-g_fnOutputModel")) 
            {
                Data.g_flagOutputModel=true;
                Data.g_fnOutputModel=args[++k];
            }
            else if (args[k].equals("-d_fnOutputModel")) 
            {
                Data.d_flagOutputModel=true;
                Data.d_fnOutputModel=args[++k];
            }
            
            else if (args[k].equals("-g_finalModel")) 
            {
                Data.g_finalModel=args[++k];
            }
            else if (args[k].equals("-d_finalModel")) 
            {
                Data.d_finalModel=args[++k];
            }
            
            else if (args[k].equals("-g_fnInputModel")) 
            {
                Data.g_flagInputModel=true;
                Data.g_fnInputModel=args[++k];
            }
            else if (args[k].equals("-d_fnInputModel")) 
            {
                Data.d_flagInputModel=true;
                Data.d_fnInputModel=args[++k];
            }
            else if (args[k].equals("-fnOutputBestRes")) 
            {
                Data.flagOutputBestRes=true;
                Data.fnOutputBestRes=args[++k];
            }

            else if (args[k].equals("-n")) Data.n = Integer.parseInt(args[++k]);
            else if (args[k].equals("-m")) Data.m = Integer.parseInt(args[++k]);            
            else if (args[k].equals("-num_iterations")) Data.num_iterations = Integer.parseInt(args[++k]);
            else if (args[k].equals("-topK")) Data.topK = Integer.parseInt(args[++k]);
            else if (args[k].equals("-batchsize")) Data.batchsize = Integer.parseInt(args[++k]);
            else if (args[k].equals("-fnResult")) Data.fnResult = args[++k];

        }
        
        // print the configurations
        System.out.println(Arrays.toString(args));
    }
}
