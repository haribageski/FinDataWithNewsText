package neural_network;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
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
	static Double lambda = 0.01, EPSILON = 0.012;//Math.pow(10, -4);	// possibly set it differently (	0.12 //)
	
	static Double[][] Xs_training;
	static Double [][] Ys_training;
	
	static Double[][] _XsCrossValidation;
	static Double [][] _YsCrossValidation;
	static int _crossValidatSize;
	
	static Double[][] Theta_1, Theta_2, Theta_3;		//it's 1-indexed ,i.e. Theta1 = Thetas[1]
	static Double[][] a_1, a_2, a_3, a_4;
	static Double[][] z_2, z_3, z_4;
	static Integer[] s_l = new Integer[5];
	
	static Double[][] h_Theta_all_a4;
	
	static Double[][] delta_errors_2, delta_errors_3, delta_errors_4;	
	
	static Double[][] Delta_1, Delta_2, Delta_3;

	static Double[][] Ds_1, Ds_2, Ds_3;	// Ds = d/d Theta of J(Theta)
	
	public Double[][] getTheta(int i)
	{
		if(i==1)
			return Theta_1;
		else
			return Theta_2;
	}
	
	public RepresentationNetwork(
				Set<Sym_Date> symDatesForTrainingML, Map<String, Company> companies_match, 
				int training_size, int numOfParameters, 
				Double [][] XsCrossValidat, Double [][] YCrossValidat) throws FileNotFoundException, UnsupportedEncodingException 
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
		
		_XsCrossValidation = XsCrossValidat;
		_YsCrossValidation = YCrossValidat;
		_crossValidatSize = _XsCrossValidation.length;
		
		initializeValues(trainingSize[0],  numOfParameters );		//minus 1 because we use the last column for evaluation
	}
	
	
	
	
	void initializeValues(int training_size, int numOfParameters)
	{
		m = training_size;
		parameters = numOfParameters;
		Double random;
		
		s_l[1] = parameters;
		s_l[2] = 2*parameters;
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
	

	public Double[][] makePrediction()
	{
		chechForNaN(_XsCrossValidation, "Cross validation matrix:");
		
		chechForNaN(Theta_1, "Theta 1 matrix:");
		
		chechForNaN(Theta_2, "Theta 2 matrix:");
		
		//parameters = 16;
		
		Double [][] a_1Predict = new Double[parameters ][_crossValidatSize];
		a_1Predict = transposeMatrix(_XsCrossValidation);
		a_1Predict = appendRowOfOnes(a_1Predict , parameters , _crossValidatSize);	//becomes of size params+1 x m

		Double [][] z_2Predict = new Double[s_l[2]][_crossValidatSize];
		z_2Predict = matrix_product(Theta_1, a_1Predict );	// s_l[2] x s_l[1] + 1 x crossValidatSize
		
		chechForNaN(_XsCrossValidation, "_XsCrossValidation  matrix in all_forward_propagation:");
		
		Double [][] a_2Predict = new Double[s_l[2]][_crossValidatSize];
		for(int i = 0; i < s_l[2]; i++)
			for(int j = 0; j < _crossValidatSize; j++)
				a_2Predict[i][j] = sigmoid (z_2Predict[i][j]);
		a_2Predict = appendRowOfOnes(a_2Predict , s_l[2] , _crossValidatSize);
		
		chechForNaN(a_2Predict, "a_2Predict matrix:");
		
		Double [][] z_3Predict = new Double[s_l[3]][_crossValidatSize];
		z_3Predict = matrix_product(Theta_2, a_2Predict);		//s_l[3] x s_l[2] + 1 x crossValidatSize
		
		chechForNaN(z_3Predict, "z_3Predict matrix:");
		
		h_Theta_all_a4 = new Double[s_l[3]][_crossValidatSize];
		for(int i = 0; i < s_l[3]; i++)
			for(int j = 0; j < _crossValidatSize; j++)
			{
				h_Theta_all_a4[i][j] = sigmoid(z_3Predict[i][j]);
				//System.out.println("h_Theta_all_a4Predict:" + h_Theta_all_a4[i][j]);
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
		int iteration = 1;
		Double prevCostJ = 100000.0;
		Double currentCostJ = prevCostJ;
		Double prevCostCrossVal = 100000.0;
		Double currentCostCrossVal = prevCostCrossVal;
		allForwardPropagation();
		
		do 
		{
			System.out.println("iteration " + iteration + " in neural net learning");
			iteration++;
			System.out.println("prevCostJ:" + prevCostJ + ", current cost is:" + currentCostJ);
			prevCostJ = currentCostJ;
			all_Backpropagation();
			prevCostCrossVal = currentCostCrossVal;
			updateTheta();
			makePrediction();
			currentCostCrossVal = costFunctionForCrossValidat();
			System.out.println("Cost in CrossValidat set: " + costFunctionForCrossValidat());
			allForwardPropagation();
			currentCostJ = costFunctionJ();
			
		} while(currentCostJ < prevCostJ || currentCostCrossVal < prevCostCrossVal);
		
		chechForNaN(Theta_1, "Theta 1 matrix:");
		
		chechForNaN(Theta_2, "Theta 2 matrix:");
		
	}

	void updateTheta()
	{
		for(int l = 1; l < Layers -1; l++)
			for(int i = 0; i < s_l[l+1]; i++)
				for(int j = 0; j < s_l[l]+1; j++)
				{
					if(l == 1)
						Theta_1[i][j] -= EPSILON * Ds_1[i][j];
					else
						Theta_2[i][j] -= EPSILON * Ds_2[i][j];
				}
		
		chechForNaN(Ds_1, "Ds 1  updated matrix:");
		
		chechForNaN(Theta_1, "Thetas 1  updated matrix:");
	}
	
	public void allForwardPropagation ()
	{
		
		chechForNaN(Xs_training, "Xs_training  matrix in all_forward_propagation");
		chechForInfinity(Xs_training, "Xs_training  matrix in all_forward_propagation");
		
		a_1 = Xs_training;
		a_1 = appendRowOfOnes(a_1 , parameters , m);	//becomes of size params+1 x m
		
		z_2 = matrix_product(Theta_1, a_1 );		// s_l[2] x s_l[1] + 1 x m
		//z_2 = matrix_product(a_1' , Theta_1);		// m x s_l[1] + 1 x s_l[2]
		chechForNaN(z_2, "z_2");
		chechForInfinity(z_2, "z_2");
		
		chechForNaN(Ys_training, "Y  matrix in all_forward_propagation:");
		chechForInfinity(Ys_training, "Y  matrix in all_forward_propagation:");
		
		a_2 = sigmoid(z_2);
		a_2 = appendRowOfOnes(a_2 , s_l[2] , m);
		
		chechForNaN(a_2, "a_2 matrix:");
		chechForInfinity(a_2, "a_2 matrix:");
		
		z_3 = matrix_product(Theta_2, a_2);		//s_l[3] x s_l[2] + 1 x m
		
		chechForNaN(z_3, "z_3 matrix:");
		chechForInfinity(z_2, "z_3 matrix:");
		
		h_Theta_all_a4 = new Double[s_l[3]][m];
		h_Theta_all_a4 = sigmoid(z_3);
		
		a_3 = h_Theta_all_a4;
		chechForNaN(a_3, "a_3 matrix:");
		chechForInfinity(a_3, "a_3 matrix:");
	}
	
	
	void all_Backpropagation()
	{
		System.out.println();
		chechForNaN(h_Theta_all_a4, "all_Backpropagation: h_Theta_all_a4 matrix:");
		chechForInfinity(h_Theta_all_a4, "all_Backpropagation: h_Theta_all_a4 matrix:");
		
		System.out.println();
		chechForNaN(Ys_training, "all_Backpropagation: all_backwards().Y matrix:");
		chechForInfinity(Ys_training, "all_Backpropagation: all_backwards().Y matrix:");
		
		for(int i = 0; i < m; i++)
			for(int j = 0; j < K; j++)
			{
				delta_errors_3[j][i] = h_Theta_all_a4[j][i] - Ys_training[i][j];
				if(delta_errors_3[j][i].isNaN())
					System.out.println("all_Backpropagation: delta_errors_3[i][j]:" + j + "x" + i + " = " + delta_errors_3[j][i] );
			}
		
		delta_errors_2 = 
					dot_product(  
							matrix_product( transposeMatrix(Theta_2),delta_errors_3) , // s_l[2] + 1 x s_l[3] x m
							dot_product(a_2, vector_dot_minus(1.0 , a_2, s_l[2] + 1 , m) , s_l[2] + 1 , m)
							, s_l[2] + 1 ,m	);
		
		delta_errors_2 = delete_first_raw(delta_errors_2, s_l[2] + 1 , m);
		chechForNaN(delta_errors_2, "all_Backpropagation: delta_errors_2:" );
		chechForInfinity(delta_errors_2, "all_Backpropagation: delta_errors_2:" );

		chechForNaN(a_1, "all_Backpropagation: a_1:" );
		chechForInfinity(a_1, "all_Backpropagation: a_1:" );

		chechForNaN(a_2, "all_Backpropagation: a_2:" );
		chechForInfinity(a_2, "all_Backpropagation: a_2:" );
		
		for(int l = 1; l < Layers - 1 ; l++)		//Theta_3 is of different dimension
		{
			if(l == 1)
			{
				chechForNaN(Delta_1, "Delta_1:" );
				chechForInfinity(Delta_1, "Delta_1:" );
				
				Delta_1 = dot_sum(Delta_1,
						matrix_product (delta_errors_2, transposeMatrix(a_1) )		//s_l[2] x m x s_l[1] + 1
						, s_l[2], s_l[1] + 1);
			}
						
			else
				Delta_2 = dot_sum(Delta_2,
						matrix_product (delta_errors_3, transposeMatrix(a_2))		//s_l[3] x m x s_l[2] + 1
						, s_l[3], s_l[2] + 1);
		}
		chechForNaN(Delta_1, "all_Backpropagation: Delta_1:");
		chechForNaN(Delta_2, "all_Backpropagation: Delta_2:");
		
		//Double[][][] approximation = gradApprox();
		for(int l=1; l< Layers - 1; l++)	//Theta_3 is of different dimension
			for(int i=0; i < s_l[l+1]; i++)
				for(int j=0; j < s_l[l]+1; j++)
				{
					if(l == 1)
					{
						if(Delta_1[i][j].isNaN())
							System.out.println("all_Backpropagation: Delta_1 with index:" + l + "x" + i + "x" + j + " = " + Delta_1[i][j] );
						Ds_1[i][j] = Delta_1[i][j]/m ;
						if(j!=0)
							Ds_1[i][j] +=  lambda/m * Theta_1[i][j];
						/*if(Ds_1[i][j].isNaN())
							System.out.println("all_Backpropagation: Ds with index:" + l + "x" + i + "x" + j + " = " + Ds_1[i][j] + 
								", and the approximation is:" + approximation[l][i][j]);	*/
					}
					else
					{
						if(Delta_2[i][j].isNaN())
							System.out.println("all_Backpropagation: Delta_2 with index:" + l + "x" + i + "x" + j + " = " + Delta_2[i][j] );
						Ds_2[i][j] = Delta_2[i][j]/m ;
						if(j!=0)
							Ds_2[i][j] +=  lambda/m * Theta_1[i][j];
						
						/*if(Ds_2[i][j].isNaN())
							System.out.println("all_Backpropagation: Ds with index:" + l + "x" + i + "x" + j + " = " + Ds_2[i][j] + 
								", and the approximation is:" + approximation[l][i][j]);	*/
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
				//if((Ys_training[i][j] == 0 && h_Theta_all_a4[j][i] >= 0.5) || (Ys_training[i][j] == 1 && h_Theta_all_a4[j][i] < 0.5 ))
				//	Current_Sum ++;
				Current_Sum -= Ys_training[i][j] * Math.log(h_Theta_all_a4[j][i]) + (1-Ys_training[i][j]) * Math.log(1-h_Theta_all_a4[j][i]);
				//System.out.println("index:" + i + ",predicted val:" + h_Theta_all_a4[0][i] + ", and true value:" + Ys_training[i]);
			}
		Current_Sum /= m;
		for(int l = 1; l <= Layers-1 ; l++)
			for(int i = 0; i < s_l[l+1]; i++ )
				for(int j = 1; j < s_l[l]+1 ; j++)
				{
					if(l == 1)
						Second_sum += Math.pow(Theta_1[i][j] , 2);
					else
						Second_sum += Math.pow(Theta_2[i][j] , 2);
				}
		
		Current_Sum += lambda * Second_sum / (2 * m);
		return Current_Sum;
	}
	
	
	
	/*public Double[][][] gradApprox()
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
	*/
	
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
			if((Ys_training[i][j] == 0 && h_Theta_all_a4[j][i] >= 0.5) || (Ys_training[i][j] == 1 && h_Theta_all_a4[j][i] < 0.5 ))
				Current_Sum ++;
			//Current_Sum -= (Double.parseDouble(Ys_training[i][j].toString())* Math.log(h_Theta_all_a4[j][i]) + (1-Double.parseDouble(Ys_training[i][j].toString())) * Math.log(1-h_Theta_all_a4[j][i]));
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
	
		
	public Double costFunctionForCrossValidat()
	{
		Double Current_Sum = 0.0;
		System.out.println("h_Theta_all_a4 size: 1x" + h_Theta_all_a4[0].length + ", crossValidatSize size" + _crossValidatSize);
		for(int i = 0; i < _crossValidatSize ; i++)
			for(int j = 0; j < K; j++)
			{
				if(h_Theta_all_a4[j][i] == null)
					System.out.println("h_Theta_all is null");
				if(_YsCrossValidation[i][j] == null)
					System.out.println("Y is null");
				if((_YsCrossValidation[i][j] == 0 && h_Theta_all_a4[j][i] >= 0.5) || (_YsCrossValidation[i][j] == 1 && h_Theta_all_a4[j][i] < 0.5 ))
					Current_Sum ++;
					//Current_Sum  -=(Double.parseDouble(_YsCrossValidation[i][j].toString())* Math.log(h_Theta_all_a4[j][i]) + 
					//	(1- Double.parseDouble(_YsCrossValidation[i][j].toString())) * Math.log(1-h_Theta_all_a4[j][i]));
				//System.out.println("index:" + i + ",predicted val:" + h_Theta_all_a4[0][i] + ", and true value:" + Ys_training[i]);
				//Current_Sum += Math.pow(_YsCrossValidation[i][j] - h_Theta_all_a4[j][i] , 2);
			}
		Current_Sum /= _crossValidatSize;
		
		return Current_Sum;
	}
	
	
	static Double sigmoid(Double z)
	{
		return 1/(1+Math.pow(Math.E , -z));
	}
	
	static Double[][] sigmoid(Double [][] M)
	{
		Double SigmoidM[][] = new Double[M.length][M[0].length];
		for(int i = 0; i < M.length; i++)
			for(int j = 0; j < M[0].length; j++)
			{
				SigmoidM[i][j] = sigmoid (M[i][j]);
			}
		return SigmoidM;
	}
	
}
