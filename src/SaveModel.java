import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveModel
{
    public static void saveModel(String model, String file_name) throws IOException 
	{
	    if (model.equals("Gen model"))
		{
			// ---
			File file = new File(file_name);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
		
			// biasV
			for(int i=1;i<=Data.m;i++)
			{
				bw.write(i+"\t"+Data.g_biasV[i]+"\r\n");
			}
		
			// V
			for(int i=1;i<=Data.m;i++)
			{			
				bw.write(String.valueOf(i));
				for(int k=0;k<Data.d;k++) 
				{
					bw.write("\t" + Data.g_V[i][k]);
				}
				bw.write("\r\n");
			}
        
			// U
			for(int u=1;u<=Data.n;u++)
			{
				bw.write(String.valueOf(u));
				for(int k=0;k<Data.d;k++) 
				{
					bw.write("\t" + Data.g_U[u][k]);
				}
				bw.write("\r\n");
			}
		
			// ---
			bw.flush();
			fw.close();
			bw.close();
		}
		
		else if (model.equals("Dis model"))
		{
			// ---
			File file = new File(file_name);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
		
			// biasV
			for(int i=1;i<=Data.m;i++)
			{
				bw.write(i+"\t"+Data.d_biasV[i]+"\r\n");
			}
		
			// V
			for(int i=1;i<=Data.m;i++)
			{			
				bw.write(String.valueOf(i));
				for(int k=0;k<Data.d;k++) 
				{
					bw.write("\t" + Data.d_V[i][k]);
				}
				bw.write("\r\n");
			}
        
			// U
			for(int u=1;u<=Data.n;u++)
			{
				bw.write(String.valueOf(u));
				for(int k=0;k<Data.d;k++) 
				{
					bw.write("\t" + Data.d_U[u][k]);
				}
				bw.write("\r\n");
			}
		
			// ---
			bw.flush();
			fw.close();
			bw.close();
		}
        
    }
}
