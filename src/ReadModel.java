import java.io.*;


public class ReadModel 
{
    public static void readModel(String arg) throws IOException
	{
    	// initialize the model G exploiting pretraining model G
        if (arg.equals("Gen model"))
        {
            Data.g_biasV = new float[Data.m+1];
            Data.g_V = new float[Data.m+1][Data.d];
            Data.g_U = new float[Data.n+1][Data.d];

            // ---
            File file = new File(Data.g_fnInputModel);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = null;

            // biasV
            for(int i=1;i<=Data.m;i++)
            {
                line = br.readLine();
                String[]field = line.split("\t");
                if(i==Integer.parseInt(field[0]))
                {
                    Data.g_biasV[i] = Float.parseFloat(field[1]);
                }
            }

            // V
            for(int i=1;i<=Data.m;i++)
            {
                line = br.readLine();
                String[] field = line.split("\t");
                if (i == Integer.parseInt(field[0]))
                {
                    for (int k = 0; k < Data.d; k++)
                    {
                        Data.g_V[i][k] = Float.parseFloat(field[k + 1]);
                    }
                }
            }

            // U
            for(int u=1;u<=Data.n;u++)
            {
                line = br.readLine();
                String[] field = line.split("\t");
                if (u == Integer.parseInt(field[0]))
                {
                    for (int k = 0; k < Data.d; k++)
                    {
                        Data.g_U[u][k] = Float.parseFloat(field[k + 1]);
                    }
                }
            }

            // ---
            fr.close();
            br.close();

        }

        // initialize the model D exploiting pretraining model D
        else if(arg.equals("Dis model"))
        {
            Data.d_biasV = new float[Data.m+1];
            Data.d_V = new float[Data.m+1][Data.d];
            Data.d_U = new float[Data.n+1][Data.d];

            // ---
            File file = new File(Data.d_fnInputModel);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = null;

            // biasV
            for(int i=1;i<=Data.m;i++)
            {
                line = br.readLine();
                String[]field = line.split("\t");
                if(i==Integer.parseInt(field[0]))
                {
                    Data.d_biasV[i] = Float.parseFloat(field[1]);
                }
            }

            // V
            for(int i=1;i<=Data.m;i++)
            {
                line = br.readLine();
                String[] field = line.split("\t");
                if (i == Integer.parseInt(field[0]))
                {
                    for (int k = 0; k < Data.d; k++)
                    {
                        Data.d_V[i][k] = Float.parseFloat(field[k + 1]);
                    }
                }
            }

            // U
            for(int u=1;u<=Data.n;u++)
            {
                line = br.readLine();
                String[] field = line.split("\t");
                if (u == Integer.parseInt(field[0]))
                {
                    for (int k = 0; k < Data.d; k++)
                    {
                        Data.d_U[u][k] = Float.parseFloat(field[k + 1]);
                    }
                }
            }

            // ---
            fr.close();
            br.close();
        }

    }

}
