import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Classificator 
{
	
	public static List<Double> countCondProbability(Voter[] v)
	{
		List<Double> condProbabilities = new ArrayList<Double>();
		double prob1 = 0.0;
		double prob2 = 0.0;
		double prob3 = 0.0;
		double prob4 = 0.0;
		for(int i = 1; i < 17; i++)
		{
			double countNo = 0.0;
			double countRepublicanN = 0.0;
			double countRepublicanY = 0.0;
			for(int j = 0; j < 435; j++)
			{
				if(v[j] == null)
				{
					continue;
				}
				if(v[j].attr[i].equals("n"))
				{
					countNo++;
					if(v[j].attr[0].equals("republican"))
					{
						countRepublicanN++;
					}
				}
				if(v[j].attr[i].equals("y"))
				{
					if(v[j].attr[0].equals("republican"))
					{
						countRepublicanY++;
					}
				}
			}
			prob1 = countRepublicanN / countNo;
			prob2 = countRepublicanY / (348.0 - countNo);
			prob3 = (countNo - countRepublicanN) / countNo;
			prob4 = (348.0 - countNo - countRepublicanY) / (348.0 - countNo);
			condProbabilities.add(prob1);
			condProbabilities.add(prob2);
			condProbabilities.add(prob3);
			condProbabilities.add(prob4);
		}
		
		return condProbabilities;
	}
	
	public static List<Double> countProbability(Voter[] v)
	{
		List<Double> probabilities = new ArrayList<Double>();
		for(int i = 0; i < 17; i++)
		{
			double countYes = 0.0;
			double countRepublican = 0.0;
			double prob = 0.0;
			for(int j = 0; j < 435; j++)
			{
				if(v[j] == null)
				{
					continue;
				}
				if(i == 0)
				{
					if(v[j].attr[i].equals("republican"))
					{
						countRepublican++;
					}
				}
				else if(v[j].attr[i].equals("y"))
				{
					countYes++;
				}
			}
			if(i == 0)
			{
				prob = countRepublican / 348.0;
				probabilities.add(prob);
				probabilities.add(1.0 - prob);
			}
			else
			{
				prob = countYes / 348.0;
				probabilities.add(prob);
				probabilities.add(1.0 - prob);
			}
		}
		return probabilities;
	}
	
	public static String[] mostCommonAnswer(Voter[] v, int col, String[] answer)
	{
		int countYes = 0;
		int countNo = 0;
		for(int i = 0; i < 435; i++)
		{
			if(v[i].attr[col].equals("y"))
			{
				countYes++;
			}
			else if(v[i].attr[col].equals("n"))
			{
				countNo++;
			}
		}
		//System.out.println(countYes + " " + countNo);
		if(countYes > countNo)
		{
			answer[col] = "y";
		}
		else
		{
			answer[col] = "n";
		}
		return answer;
	}
	
	public static double solve(double accuracy) throws FileNotFoundException, IOException
	{
		File file = new File("F:/Java/workspace/ClassificatorBase/src/RepDem.txt");
		Voter[] voters = new Voter[435];
		Voter[] forTesting = new Voter[435];
		Voter[] forTraining = new Voter[435];
		int index = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) 
		{
		    String line;
		    while ((line = br.readLine()) != null) 
		    {
		       String[] data = line.split(",");
		       Voter v = new Voter(data);
		       Voter toTest = new Voter(data);
		       Voter v2 = new Voter(data);
		       voters[index] = v;
		       forTesting[index] = toTest;
		       forTraining[index] = v2;
		       index++;
		    }
		}
		
		String[] answers = new String[17];
		
		for(int i = 1; i < 17; i++)
		{
			mostCommonAnswer(voters, i, answers);
		}
		
		for(int i = 0; i < 435; i++)
		{
			for(int j = 1; j < 17; j++)
			{
				if(voters[i].attr[j].equals("?"))
				{
					voters[i].attr[j] = answers[j];
					forTesting[i].attr[j] = answers[j];
					forTraining[i].attr[j] = answers[j];
				}
			}
		}
		Random r = new Random();
		Voter[] testing = new Voter[87]; //20 % for testing
		List<Integer> indexes = new ArrayList<Integer>();
		
		for(int i = 0; i < 87; i++)
		{
			index = r.nextInt(435);
			while(indexes.contains(index))
			{
				index = r.nextInt(435);
			}
			indexes.add(index);
			testing[i] = forTesting[index];
			forTraining[index] = null;
		}
		
		List<Double> probs = new ArrayList<Double>();
		probs = countProbability(forTraining);
		/*for(int i = 0; i < probs.size(); i++)
		{
			System.out.print(probs.get(i) + " ");
		}
		System.out.println();*/
		List<Double> condProbs = new ArrayList<Double>();
		condProbs = countCondProbability(forTraining);
		/*for(int i = 0; i < condProbs.size(); i++)
		{
			System.out.println(condProbs.get(i) + " ");
		}*/
		
		double probRep = 0.0;
		double probDem = 0.0;
		int[] flags = new int[16];
		double correctGuesses = 0.0;
		String correctAns = "";
		
		for(int i = 0; i < 87; i++)
		{
			probRep = 0.0;
			for(int j = 1; j < 17; j++)
			{
				if(testing[i].attr[j].equals("y"))
				{
					flags[j - 1] = 1;
				}
				else
				{
					flags[j - 1] = 0;
				}
			}
			probRep = condProbs.get((4 * 0) + flags[0]);
			probDem = condProbs.get(((4 * 0) + 2) + flags[0]);
			
			for(int j = 2; j < 17; j++)
			{
				probRep = probRep * condProbs.get((4 * (j - 1)) + flags[j - 1]);
				probDem = probDem * condProbs.get((4 * (j - 1) + 2) + flags[j - 1]);
			}
			probRep = probRep * probs.get(0);
			probDem = probDem * probs.get(1);
			//System.out.println(probRep + " " + probDem);
			correctAns = voters[indexes.get(i)].getParty();
			if(probRep >= probDem)
			{
				testing[i].setParty("republican");
			}
			else
			{
				testing[i].setParty("democrat");
			}
			//System.out.println("voters: " + voters[indexes.get(i)].getParty());
			if(testing[i].attr[0].equals(correctAns))
			{
				correctGuesses++;
			}
		}
		accuracy = (correctGuesses / 87.0) * 100.0;
		System.out.println("Percentage of correct guesses: " + (correctGuesses / 87.0) * 100.0);
		return accuracy;
	}
}
