import java.io.FileNotFoundException;
import java.io.IOException;


public class Solver 
{

	public static void main(String[] args) throws FileNotFoundException, IOException 
	{
		double accuracy = 0.0;
		double[] allAccuracies = new double[10];
		double sumAcc = 0.0;
		for(int i = 0; i < 10; i++)
		{
			allAccuracies[i] = Classificator.solve(accuracy);
			sumAcc += allAccuracies[i];
		}
		double avgAcc = sumAcc / 10.0;
		System.out.println("The average accuracy of the algorithm is: " + avgAcc);
	}

}
