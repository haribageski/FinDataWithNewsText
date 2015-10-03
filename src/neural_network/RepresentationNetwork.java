package neural_network;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import news.AllCompaniesSentimentOfNews;
import other_structures.Sym_Date;
import company.Company;

public class RepresentationNetwork extends Matrix_operations
{
	static int Layers = 3;		//two hidden layers
	static int parameters;
	// List <Integer> s_l = new ArrayList<Integer>();
	static int K = 2;  //output units
	static int m;		//size of training set
	static Double lambda = 0.1, EPSILON = 0.12;	//Math.pow(10, -4); possibly set it differently
	
	static Double[][] Xs_training;
	static Double [][] Ys_training;
	
	static Double[][] Theta_1, Theta_2;		//it's 1-indexed ,i.e. Theta1 = Thetas[1]
	static Double[][] a_1, a_2, a_3;
	static Double[][] z_2, z_3;
	static Integer[] s_l = new Integer[5];
	
	static Double[][] h_Theta_all_a4;
	
	static Double[][] delta_errors_2, delta_errors_3;	
	
	static Double[][] Delta_1, Delta_2;

	static Double[][] Ds_1, Ds_2;	// Ds = d/d Theta of J(Theta)
	
	public Double[][] getTheta(int i)
	{
		if(i==1)
			return Theta_1;
		else
			return Theta_2;
	}
	
	public RepresentationNetwork(
				Set<Sym_Date> symDatesForTrainingML, Map<String, Company> companies_match, 
				int training_size, int numOfParameters) throws FileNotFoundException, UnsupportedEncodingException 
	{
		System.out.println("RepresentationNetwork. training_size:" + training_size);
		
		Double [][]Xs = new Double[numOfParameters-6][training_size];	//used to store financial parameters (without senti parameters)
		Double [][] Y = new Double[training_size][K];
		
		int [] trainingSize = new int [1];
		Training_X_Y_matrix.Create_X_Y_finnance_Matrix(symDatesForTrainingML, companies_match, Xs, Y, trainingSize);
		
		//erase empty columns of X
		Double[][]XCorrected = new Double[Xs.length][trainingSize[0]];
		for(int i = 0; i < Xs.length; i++)
			for(int j = 0; j < trainingSize[0]; j++)
			{
				XCorrected[i][j] = Xs[i][j];
			}
		Xs = XCorrected;
		
		System.out.println("Xs size: rows = " + Xs.length + ", columns = " + Xs[0].length); 
		
		training_size = Xs[0].length;
		
		Double [][] YCorrected = new Double[training_size][K];
		for(int i = 0; i < Y.length; i++)
			for(int j = 0; j < K; j++)
			{
				if(Y[i][j] == null )
				{
					continue;
				}
				YCorrected[i][j] = Y[i][j];
			}
		Y = YCorrected;
		System.out.println("Ys size: rows = " + Y.length ); 
        
		
		Double[][] X_senti = new Double[6][trainingSize[0]];
		X_senti = AllCompaniesSentimentOfNews.makeX_FromSentimentData(Training_X_Y_matrix.getSymDates());
		System.out.println("RepresentationNetwork.X_senti size:" + X_senti.length + " x " + X_senti[0].length);
		
		
		// TODO : append is incorrectly defined
		Xs_training = appendToTheBottom(Xs, X_senti);
		
		System.out.println("Xs_training:");
		System.out.println("numof rows:" + Xs_training.length);
		System.out.println("numof cols:" + Xs_training[0].length);
		
		@SuppressWarnings("resource")
		PrintWriter writer = new PrintWriter("training_matrix.txt", "UTF-8");	
		writer.println("printing the first element that will be used for evaluation...");
		writer.println();
			
		//printing the training matrix
		writer.println("printing the training matrix...");
		for(int i=0;i<Xs.length ;i++)
		{
			writer.println();
			for(int j=0;j<Xs[0].length;j++)
				writer.print( Xs[i][j] + " ");
		}
		writer.println();
		
		System.out.println("Xs:" + Xs_training.length + ",num of parameters:"+ numOfParameters+ ",training_size:"+ training_size);
		Xs_training = appendRowOfOnes(Xs_training, numOfParameters, training_size);
		Ys_training = Y;
		
		initializeValues(trainingSize[0],  numOfParameters );		//minus 1 because we use the last column for evaluation
	}
	
	
	
	
	void initializeValues(int training_size, int numOfParameters)
	{
		m = training_size;
		parameters = numOfParameters;
		Double random;
		
		s_l[1]= s_l[2] = parameters;
		s_l[3] = K;
		
		Theta_1 = new Double[s_l[2]][s_l[1] + 1];	
		Theta_2 = new Double[s_l[3]][s_l[2] + 1];	
		
		a_1 = new Double[s_l[1] + 1][m];
		a_2 = new Double[s_l[2] + 1][m];
		a_3 = new Double[s_l[3] ][m];
		
		z_2 = new Double[s_l[2]][m];
		z_3 = new Double[s_l[3]][m];
		
		h_Theta_all_a4 = new Double[s_l[3]][m];
		
		delta_errors_3 = new Double[s_l[3]][m];	
		delta_errors_2 = new Double[s_l[2] + 1][m];	  //first row irrelevant, remove it
		//no delta_1 since there is no error in the given X matrix
		
		Delta_1 = new Double[s_l[2]][s_l[1] + 1];
		Delta_2 = new Double[s_l[3]][s_l[2] + 1];
		
		Ds_1 = new Double[s_l[2]][s_l[1] + 1];	
		Ds_2 = new Double[s_l[3]][s_l[2] + 1];	
		
		for(int l=1; l<Layers ; l++)
		{
			for(int i = 0; i < s_l[l+1]; i++)
				for(int j = 0; j < s_l[l]+1; j++)
				{	
					random = Math.random() * (2 * EPSILON) - EPSILON;
					System.out.println("random:" + random);
					if(l == 1)
					{
						Theta_1[i][j] = random;
						Delta_1[i][j] = 0.0;
					}
					else
					{
						Theta_2[i][j] = random;
						Delta_2[i][j] = 0.0;
					}
				}	
		}
		
	}
	

	public Double[][] makePrediction(Double [][] X, int crossValidatSize)
	{
		System.out.println("Cross validation matrix:");
		chechForNaN(X);
		
		System.out.println("Theta 1 matrix:");
		chechForNaN(Theta_1);
		
		System.out.println("Theta 2 matrix:");
		chechForNaN(Theta_2);
		
		//parameters = 16;
		
		a_1 = new Double[parameters ][crossValidatSize];
		a_1 = transposeMatrix(X);
		a_1 = appendRowOfOnes(a_1 , parameters , crossValidatSize);	//becomes of size params+1 x m

		z_2 = new Double[parameters][crossValidatSize];
		z_2 = matrix_product(Theta_1, a_1 , s_l[2], s_l[1] + 1, crossValidatSize);
		
		System.out.println("X  matrix in all_forward_propagation:");
		chechForNaN(X);
		
		a_2 = new Double[parameters+1][crossValidatSize];
		for(int i = 0; i < s_l[2]; i++)
			for(int j = 0; j < crossValidatSize; j++)
				a_2[i][j] = gFunc (z_2[i][j]);
		a_2 = appendRowOfOnes(a_2 , s_l[2] , crossValidatSize);
		
		System.out.println("a_2 matrix:");
		chechForNaN(a_2);
		
		z_3 = new Double[s_l[3]][crossValidatSize];
		z_3 = matrix_product(Theta_2, a_2, s_l[3] , s_l[2] + 1, crossValidatSize);
		
		System.out.println("z_3 matrix:");
		chechForNaN(z_3);
		
		h_Theta_all_a4 = new Double[s_l[3]][crossValidatSize];
		for(int i = 0; i < s_l[3]; i++)
			for(int j = 0; j < crossValidatSize; j++)
			{
				h_Theta_all_a4[i][j] = gFunc(z_3[i][j]);
				System.out.println("h_Theta_all_a4:" + h_Theta_all_a4[i][j]);
			}
		
		return  transposeMatrix(h_Theta_all_a4);
	}
	
	/*public Double getPredictedVal(int index)
	{
		return h_Theta_all_a4[index];
	}*/


	/*
	 * The core function of ML. It is the top level and it calls all the other functions
	 */
	public void Learn()	
	{
		Double prevCostFunctionJ = 100000.0;
		allForwardPropagation();
		do 
		{
			prevCostFunctionJ = costFunctionJ();
			allForwardPropagation();
			all_Backpropagation();
			System.out.println("prevCostFunctionJ:" + prevCostFunctionJ);
			updateTheta();
			System.out.println("costFunctionJ:" + costFunctionJ());
		} while(costFunctionJ() <= prevCostFunctionJ);
		
		System.out.println("Theta 1 matrix:");
		chechForNaN(Theta_1);
		
		System.out.println("Theta 2 matrix:");
		chechForNaN(Theta_2);
		
	}

	void updateTheta()
	{
		for(int l=1; l<Layers -1; l++)
			for(int i=0; i < s_l[l+1]; i++)
				for(int j=0; j < s_l[l]+1; j++)
				{
					if(l == 1)
						Theta_1[i][j] -= lambda * Ds_1[i][j];
					else
						Theta_2[i][j] -= lambda * Ds_2[i][j];
				}
		
		System.out.println("Ds 1  updated matrix:");
		chechForNaN(Ds_1);
		
		System.out.println("Thetas 1  updated matrix:");
		chechForNaN(Theta_1);
	}
	
	public void allForwardPropagation ()
	{
		a_1 = Xs_training;
		a_1 = appendRowOfOnes(a_1 , parameters , m);	//becomes of size params+1 x m

		z_2 = matrix_product(Theta_1, a_1 , s_l[2], s_l[1] + 1, m);
		
		System.out.println("Xs_training  matrix in all_forward_propagation:");
		chechForNaN(Xs_training);
		
		for(int i=0; i< s_l[2]; i++)
			for(int j=0; j< m; j++)
				a_2[i][j] = gFunc (z_2[i][j]);
		a_2 = appendRowOfOnes(a_2 , s_l[2] , m);
		
		System.out.println("a_2 matrix:");
		chechForNaN(a_2);
		
		z_3 = matrix_product(Theta_2, a_2, s_l[3] , s_l[2] + 1, m);
		
		System.out.println("z_3 matrix:");
		chechForNaN(z_3);
		
		for(int i = 0; i < s_l[3]; i++)
			for(int j = 0; j < m; j++)
				h_Theta_all_a4[i][j] = gFunc(z_3[i][j]);
		
		a_3 = h_Theta_all_a4;
	}
	
	
	void all_Backpropagation()
	{
		System.out.println("h_Theta_all_a4 matrix:");
		chechForNaN(h_Theta_all_a4);
		
		System.out.println("all_backwards().Y matrix:");
		for(int i = 0; i < Ys_training.length; i++)
		{
			if(Ys_training[i] == null)
				System.out.println("index:" + 0 + "x" + i + " is null");
			/*else
				if(Ys_training[i].isNaN())
					System.out.println("index:" + 0 + "x" + i + " is NaN");
					*/
		}
		
		
		for(int i = 0; i < m; i++)
			for(int j = 0; j < K; j++)
			delta_errors_3[j][i] = h_Theta_all_a4[j][i] - Double.parseDouble(Ys_training[i][j].toString());
		
		delta_errors_2 = 
					dot_product(  
							matrix_product( transposeMatrix(Theta_2),delta_errors_3, s_l[2] + 1 , s_l[3], m) , 
							dot_product(a_2, vector_dot_minus(1.0 , a_2, s_l[2] + 1 , m) , s_l[2] + 1 , m)
							, s_l[2] + 1 ,m	);
		
		delta_errors_2 = delete_first_raw(delta_errors_2, s_l[2] + 1 , m);
		
		
		for(int l = 1; l < Layers - 1 ; l++)		//Theta_3 is of different dimension
		{
			if(l == 1)
				Delta_1 = dot_sum(Delta_1,
						matrix_product (delta_errors_2, transposeMatrix(a_1) , s_l[2], m, s_l[1] + 1)
						, s_l[2], s_l[1] + 1);
			else
				Delta_2 = dot_sum(Delta_2,
						matrix_product (delta_errors_3, transposeMatrix(a_2) , s_l[3], m, s_l[2] + 1)
						, s_l[3], s_l[2] + 1);
		}
		
		
		Double[][][] approximation = gradApprox();
		for(int l=1; l< Layers - 1; l++)	//Theta_3 is of different dimension
			for(int i=0; i < s_l[l+1]; i++)
				for(int j=0; j < s_l[l]+1; j++)
				{
					if(l == 1)
					{
						Ds_1[i][j] = Delta_1[i][j]/m ;
						if(j!=0)
							Ds_1[i][j] +=  lambda * Theta_1[i][j];
						
						System.out.println("Ds with index:" + l + "x" + i + "x" + j + " = " + Ds_1[i][j] + 
								", and the approximation is:" + approximation[l][i][j]);	
					}
					else
					{
						Ds_2[i][j] = Delta_2[i][j]/m ;
						if(j!=0)
							Ds_2[i][j] +=  lambda * Theta_1[i][j];
						
						System.out.println("Ds with index:" + l + "x" + i + "x" + j + " = " + Ds_2[i][j] + 
								", and the approximation is:" + approximation[l][i][j]);	
					}
								
				}
	}
	
	

	/**
	 * We add second summand multiplied with very small parameter lamda to avoid overfitting.
	 * @return
	 */
	public Double costFunctionJ()
	{
		Double Current_Sum = 0.0 , Second_sum = 0.0;
		for(int i = 0; i < m ; i++)
			for(int j = 0; j < K; j++)
			{
				//Current_Sum += Math.pow(Ys_training[i][j] - h_Theta_all_a4[j][i] , 2);
				Current_Sum -= (Ys_training[i][j]* Math.log(h_Theta_all_a4[j][i]) + (1-Ys_training[i][j]) * Math.log(1-h_Theta_all_a4[j][i]));
				//System.out.println("index:" + i + ",predicted val:" + h_Theta_all_a4[0][i] + ", and true value:" + Ys_training[i]);
			}
		Current_Sum /= m;
		for(int l = 1; l <= Layers-1 ; l++)
			for(int i = 0; i < s_l[l] ; i++)
				for(int j = 0; j < s_l[l+1]; j++ )
				{
					if(l == 1)
						Second_sum += Math.pow(Theta_1[j][i] , 2);
					else
						Second_sum += Math.pow(Theta_2[j][i] , 2);
				}
		
		Current_Sum += lambda * Second_sum / (2 * m);
		return Current_Sum;
	}
	
	
	
	public Double[][][] gradApprox()
	{
		Double[][][] gradApproximation = new Double [Layers][parameters][parameters+1];	
		for(int l = 1; l <= Layers-1 ; l++)
		{
			System.out.println("gradApproximation" + l);
			for(int i = 0; i < s_l[l] ; i++)
			{
				for(int j = 0; j < s_l[l+1]; j++ )
				{
					gradApproximation[l][i][j] = (costFunctionModified(l,i,j, true).doubleValue() - costFunctionModified(l,i,j, false).doubleValue() ) / (2 * EPSILON); 
					System.out.print(gradApproximation[l][i][j] + " ");
				}
				System.out.println();
			}
		}
		return gradApproximation;
	}
	
	/**
	 * 
	 * @param index - it tells us that we want to add or substract EPSILON to Theta_index
	 * @param plus_minus - if true we add EPSILON to Theta_index, otherwise we substract
	 * @return cost function for all Thetas same but the index-th 
	 */
	public Double costFunctionModified(int index1, int index2, int index3, boolean plus_minus)
	{
		Double Current_Sum = 0.0 , Second_sum = 0.0;
		for(int i = 0; i < m ; i++)
			for(int j = 0; j < K ; j++)
		{
			Current_Sum -= (Double.parseDouble(Ys_training[i][j].toString())* Math.log(h_Theta_all_a4[j][i]) + (1-Double.parseDouble(Ys_training[i][j].toString())) * Math.log(1-h_Theta_all_a4[j][i]));
			//System.out.println("index:" + i + ",predicted val:" + h_Theta_all_a4[0][i] + ", and true value:" + Ys_training[i]);
		}
		Current_Sum /= m;
		for(int l = 1; l <= Layers-1 ; l++)
			for(int i = 0; i < s_l[l] ; i++)
				for(int j = 0; j < s_l[l+1]; j++ )
				{
					if(l == index1 && i == index2 && j == index3)
					{
						if(plus_minus == true)
						{
							if(l == 1)
								Second_sum += Math.pow(Theta_1[j][i] + EPSILON , 2);
							else
								Second_sum += Math.pow(Theta_2[j][i] + EPSILON , 2);
						}
							
						else
						{
							if(l == 1)
								Second_sum += Math.pow(Theta_1[j][i] - EPSILON , 2);
							else
								Second_sum += Math.pow(Theta_1[j][i] - EPSILON , 2);
						}
					}
					else
					{
						if(l == 1)
							Second_sum += Math.pow(Theta_1[j][i], 2);
						else
							Second_sum += Math.pow(Theta_2[j][i], 2);
					}
				}
		
		Current_Sum += lambda * Second_sum / (2 * m);
		return Current_Sum;
	}
	
		
	public Double costFunctionForCrossValidat(Double[][] Y, int crossValidatSize)
	{
		Double Current_Sum = 0.0 , Second_sum = 0.0;
		System.out.println("h_Theta_all_a4 size: 1x" + h_Theta_all_a4[0].length + ", crossValidatSize size" + crossValidatSize);
		for(int i = 0; i < crossValidatSize ; i++)
			for(int j = 0; j < K; j++)
			{
				if(h_Theta_all_a4[j][i] == null)
					System.out.println("h_Theta_all_a4 is null");
				if(Y[i][j] == null)
					System.out.println("Y is null");
				Current_Sum  -=(Double.parseDouble(Y[i][j].toString())* Math.log(h_Theta_all_a4[j][i]) + (1- Double.parseDouble(Y[i][j].toString())) * Math.log(1-h_Theta_all_a4[j][i]));
				//System.out.println("index:" + i + ",predicted val:" + h_Theta_all_a4[0][i] + ", and true value:" + Ys_training[i]);
			}
		Current_Sum /= crossValidatSize;
		
		/*
		for(int l = 1; l <= Layers-1 ; l++)
			for(int i = 0; i < s_l[l] ; i++)
				for(int j = 0; j < s_l[l+1]; j++ )
				
				{
					if(l == 1)
						Second_sum += Math.pow(Theta_1[j][i] , 2);
					else
						Second_sum += Math.pow(Theta_2[j][i] , 2);
				}
		Current_Sum += lambda * Second_sum / (2 * crossValidatSize);
		*/
		return Current_Sum;
	}
	
	
	static Double gFunc(Double z)
	{
		return 1/(1+Math.pow(Math.E , -z));
	}
	
}
