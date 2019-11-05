public class Initialization 
{
    public static void initialization(String arg)
    {

        // random initialize the model G
        if (arg.equals("Gen model"))
        {
            Data.g_biasV = new float[Data.m+1];
            Data.g_V = new float[Data.m+1][Data.d];
            Data.g_U = new float[Data.n+1][Data.d];

            // g_avg
            float g_avg = 0;
            for (int i=1; i<Data.m+1; i++)
            {
                g_avg += Data.itemRatingNumTrain[i];
            }
            g_avg = g_avg/Data.n/Data.m;

            // biasV
            for (int i=1; i<Data.m+1; i++)
            {
                Data.g_biasV[i]= (float) Data.itemRatingNumTrain[i] / Data.n - g_avg;
            }

            // V
            for (int i=1; i<Data.m+1; i++)
            {
                for (int f=0; f<Data.d; f++)
                {
                    Data.g_V[i][f] = (float) ( (Math.random()-0.5)*0.01 );
                }
            }

            // U
            for (int u=1; u<Data.n+1; u++)
            {
                for (int f=0; f<Data.d; f++)
                {
                    Data.g_U[u][f] = (float) ( (Math.random()-0.5)*0.01 );
                }
            }
        }

        // random initialize the model D
        else
        {
            Data.d_biasV = new float[Data.m+1];
            Data.d_V = new float[Data.m+1][Data.d];
            Data.d_U = new float[Data.n+1][Data.d];

            // g_avg
            float g_avg = 0;
            for (int i=1; i<Data.m+1; i++)
            {
                g_avg += Data.itemRatingNumTrain[i];
            }
            g_avg = g_avg/Data.n/Data.m;

            // biasV
            for (int i=1; i<Data.m+1; i++)
            {
                Data.d_biasV[i]= (float) Data.itemRatingNumTrain[i] / Data.n - g_avg;
            }

            // V
            for (int i=1; i<Data.m+1; i++)
            {
                for (int f=0; f<Data.d; f++)
                {
                    Data.d_V[i][f] = (float) ( (Math.random()-0.5)*0.01 );
                }
            }

            // U
            for (int u=1; u<Data.n+1; u++)
            {
                for (int f=0; f<Data.d; f++)
                {
                    Data.d_U[u][f] = (float) ( (Math.random()-0.5)*0.01 );
                }
            }
        }

    }
}
