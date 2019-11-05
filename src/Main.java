import java.io.*;
import java.util.*;

public class Main
{
    public static void main(String [] args) throws Exception
    {
        // 1. read configurations
        Long start_time = System.currentTimeMillis();
        ReadConfigurations.readConfigurations(args);
        System.out.println("Finish reading configurations.");
		
        // 2. read training data and test data
        ReadData.readData();
        System.out.println("Finish reading data.");

        // 3. read the model parameters or apply initialization
        if (Data.g_flagInputModel == true){
            ReadModel.readModel("Gen model");
        }
        else{
            Initialization.initialization("Gen model");
        }
		
        if (Data.d_flagInputModel == true){
            //System.out.println("Model D is initialized by pre train method. ");
            ReadModel.readModel("Dis model");
        }
        else{
            //System.out.println("Model D is random initialized. ");
            Initialization.initialization("Dis model");
        }

        // 4. training
        Train.train();
        //System.out.println("Finish Training.");

        Long end_time = System.currentTimeMillis();
		System.out.println("\nFinal res:");
		System.out.print("Gen: " + Test.test("Gen model"));
		System.out.print("Dis: " + Test.test("Dis model"));
        System.out.println("Time spend (total) = " + (end_time-start_time)/1000F +"s");

        /*
        // 5. save model parameters
        if (Data.flagOutputModel==true)
        {
            SaveModel.saveModel();
        }

        // 6. read candidate list
        if(Data.flagInputCandidateList)
        {
            ReadCandidateList.readCandidateList();
        }

        // 7. test and output candidate list (if required)
        Test.test();
        */

    }
}
