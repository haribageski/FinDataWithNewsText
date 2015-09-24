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
	static Integer Layers = 4;		//two hidden layers
	static int parameters;
	// List <Integer> s_l = new ArrayList<Integer>();
	Integer K = 1;  //output units
	Integer m;		//size of training set
	static Double lambda = 0.00000001, EPSILON = Math.pow(10, -4);	//possibly set it differently
	
	static Double[][] Xs_training;
	static Double[] Ys_training;
	
	static Double[][][] Thetas;		//it's 1-indexed ,i.e. Theta1 = Thetas[1]
	static Double[][][] a_l;
	static Integer[] s_l = new Integer[5];
	
	static Double[][] h_Theta_all_a4;
	
	static Double[][][] delta_errors;	//first row irrelevant
	
	static Double[][][] Deltas;

	static Double[][][] Ds;	// Ds = d/d Theta of J(Theta)
	
	public Double[][] getTheta(int i)
	{
		return Thetas[i];
	}
	
	public RepresentationNetwork(
				Set<Sym_Date> symDatesForTrainingML, Map<String, Company> companies_match, 
				int training_size, int numOfParameters) throws FileNotFoundException, UnsupportedEncodingException 
	{
		System.out.println("RepresentationNetwork. training_size:" + training_size);
		
		Double [][]Xs = new Double[numOfParameters-6][training_size];	//used to store financial parameters (without senti parameters)
		Double []Y = new Double[training_size];
		
		int [] trainingSize = new int [1];
		Training_X_Y_matrix.Create_X_Y_finnance_Matrix(symDatesForTrainingML, companies_match, Xs, Y, trainingSize);
		
		//erase empty columns of X
		Double[][]XCorrected = new Double[Xs.length][trainingSize[0]];
		for(int i = 0; i < Xs.length; i++)
			for(int j = 0; j < trainingSize[0]; j++)
				XCorrected[i][j] = Xs[i][j];
		Xs = XCorrected;
		System.out.println("Xs size: rows = " + Xs.length + ", columns = " + Xs[0].length); 
		training_size = Xs[0].length;
		/*BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String username = null;
        try {
            username = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } */
        
		
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
		Xs_training = add_ones_as_first(Xs_training, numOfParameters, training_size);
		Ys_training = Y;
		
		initializeValues(trainingSize[0],  numOfParameters );		//minus 1 because we use the last column for evaluation
	}
	
	
	
	
	void initializeValues(int training_size, int numOfParameters)
	{
		m = training_size;
		parameters = numOfParameters;
		Double random;
		
		s_l[1]= s_l[2] = s_l[3] = parameters;
		s_l[4] = 1;
		
		Thetas = new Double[Layers][parameters][parameters+1];	
		a_l = new Double[Layers+1][parameters+1][m];
		
		h_Theta_all_a4 = new Double[1][m];
		
		delta_errors = new Double[Layers+1][parameters+1][m];	//first row irrelevant
		
		Deltas = new Double[Layers][parameters][parameters +1];

		Ds = new Double[Layers][parameters][parameters +1];	
		
		for(int l=1; l<Layers ; l++)
		{
			for(int i=0; i< parameters; i++)
				for(int j=0; j< parameters+1; j++)
				{	
					random = Math.random() * EPSILON - EPSILON;
					Thetas[l][i][j] = random;
					Deltas[l][i][j] = 0.0;
				}	
		}
		
	}
	

	public Double makePrediction(Double [] X)
	{
		for(int i=0; i< X.length; i++)
		{
			System.out.print(X[i] + " ");
		}
		System.out.println();
		parameters = 16;
		//System.out.println("X.length:" + X.length + ", Layers:" + Layers + ", parameters:" + parameters);
		
		Double [][]a_l = new Double[Layers+1][parameters+1];
		a_l[1] = add_ones_as_first(X);
		//System.out.println("a_l[1]:" + a_l[1].length);
		Double[] Z_2 = new Double[parameters];
		Z_2 = matrix_product(Thetas[1], a_l[1] , parameters, parameters+1);
		
		//System.out.println("a_l[2][i]");
		for(int i=0; i< parameters; i++)
		{
			a_l[2][i] = gFunc (Z_2[i]);
			//System.out.println("a_l[2]:" + a_l[2][i] + " ");
		}
		a_l[2] = add_ones_as_first(a_l[2] );
		
		Double[] Z_3 = new Double[parameters];
		Z_3 = matrix_product(Thetas[2], a_l[2] , parameters, parameters+1);
		//System.out.println("a_l[3][i]");
		for(int i=0; i< parameters; i++)
		{
			a_l[3][i] = gFunc (Z_3[i]);
			//System.out.println("a_l[3]:" + a_l[3][i] + " ");
		}
		a_l[3] = add_ones_as_first(a_l[3] );
		
		
		Double Z_4 = 0.0;
		Z_4 = matrix_product(Thetas[3], a_l[3] , 1, parameters+1)[0];
		
		//System.out.println(g_func (Z_4));
		h_Theta_all_a4[0][0] = gFunc (Z_4);
		//System.out.println("h_Theta_all_a4[0][0]:" + h_Theta_all_a4[0][0]);
		return  h_Theta_all_a4[0][0];
	}


	/*
	 * The core function of ML. It is the top level and it calls all the other functions
	 */
	public void Learn()	
	{
		all_forward_propagation();
		do 
		{
			all_forward_propagation();
			all_Backpropagation();
			update_Theta();
			System.out.println(costFunctionJ());
		} while(costFunctionJ()>0);
	}

	void update_Theta()
	{
		for(int l=1; l<Layers -1; l++)
			for(int i=0; i< parameters; i++)
				for(int j=0; j< parameters+1; j++)
					Thetas[l][i][j] -= lambda*Ds[l][i][j];
		//Theta_3
		for(int j=0; j< parameters+1; j++)
				Thetas[Layers -1][0][j] -= lambda*Ds[Layers -1][0][j];
	}
	
	void all_forward_propagation ()
	{
		a_l[1] = Xs_training;
		Double[][] z_2 = new Double[parameters][m];

		z_2 = matrix_product(Thetas[1], Xs_training , parameters, parameters+1, m);
		for(int i=0; i< parameters; i++)
			for(int j=0; j< m; j++)
				a_l[2][i][j] = gFunc (z_2[i][j]);
		a_l[2] = add_ones_as_first(a_l[2] , parameters , m);
		
		Double[][] z_3 = new Double[parameters][m];
		z_3 = matrix_product(Thetas[2], a_l[2] , parameters, parameters+1, m);
		for(int i=0; i< parameters; i++)
			for(int j=0; j< m; j++)
				a_l[3][i][j] = gFunc (z_3[i][j]);
		a_l[3] = add_ones_as_first(a_l[3] , parameters , m);
		
		Double[][] z_4 = new Double[1][m];
		z_4 = matrix_product(Thetas[3], a_l[3] , 1, parameters+1, m);
		for(int j=0; j< m; j++)
			h_Theta_all_a4[0][j] = gFunc (z_4[0][j]);
		a_l[4][0] = h_Theta_all_a4[0];
	}
	
	
	void all_Backpropagation()
	{
		for(int j=0; j<m; j++)
			delta_errors[4][0][j] = h_Theta_all_a4[0][j] - Ys_training[j];
			
		delta_errors[3] = 
					dot_product(  
							matrix_product( transposeMatrix(Thetas[3]),delta_errors[4], parameters+1,1, m) , 
							dot_product(a_l[3], vector_dot_minus(1.0 , a_l[3], parameters+1 ,m) , parameters+1,m)
							, parameters+1	,m			
								);
		
		delta_errors[3] = delete_first_raw(delta_errors[3], parameters+1 , m);
		
		delta_errors[2] = 
				dot_product(  
						matrix_product( transposeMatrix(Thetas[2]),delta_errors[3], parameters+1, parameters, m) , 
						dot_product(a_l[2], vector_dot_minus(1.0 , a_l[2], parameters+1 ,m) , parameters+1,m)
						, parameters+1	,m			
							);
		delta_errors[2] = delete_first_raw(delta_errors[2], parameters+1 , m);
		
		
		for(int l=1; l<Layers -1 ; l++)		//Theta_3 is of different dimension
		{
			Deltas[l] = dot_sum(Deltas[l],
								matrix_product (delta_errors[l+1], transposeMatrix(a_l[l]) , parameters, m, parameters+1)
								, parameters, parameters+1);
		}
		
		//for Theta_3 the adjusting factor
		Deltas[Layers-1] = dot_sum(Deltas[Layers-1],
				matrix_product (delta_errors[Layers], transposeMatrix(a_l[Layers-1]) , 1, m, parameters+1)
				, 1, parameters+1);
		
		for(int l=1; l< Layers - 1; l++)	//Theta_3 is of different dimension
			for(int i=0; i< parameters; i++)
				for(int j=0; j< parameters+1; j++)
				{
					Ds[l][i][j] = Deltas[l][i][j]/m ;
					if(j!=0)
						Ds[l][i][j] +=  lambda * Thetas[l][i][j];
				}
		
		//Theta_3 adjusting factor
			for(int j=0; j< parameters+1; j++)
			{
				Ds[Layers-1][0][j] = Deltas[Layers-1][0][j]/m ;
				if(j!=0)
					Ds[Layers-1][0][j] +=  lambda * Thetas[Layers-1][0][j];
			}
	}
	
	
	
	
	/**
	 * We add second summand multiplied with very small parameter lamda to avoid overfitting.
	 * @return
	 */
	Double costFunctionJ()
	{
		Double Current_Sum = 0.0 , Second_sum = 0.0;
		for(int i=0; i<m ; i++)
		{
			Current_Sum -= (Ys_training[i]* Math.log(h_Theta_all_a4[0][i]) + 
					(1-Ys_training[i]) * Math.log(1-h_Theta_all_a4[0][i]));
			Current_Sum /=m;
		}
		for(int l=1; l<= Layers-1 ; l++)
			for(int i=0; i< s_l[l] ; i++)
				for(int j=0; j< s_l[l+1]; j++ )
				{
					Second_sum+= Math.pow(Thetas[l][j][i] , 2);
				}
		
		Current_Sum += lambda*Second_sum / (2*m);
		return Current_Sum;
	}
	
	
	
	
	static Double gFunc(Double z)
	{
		return 1/(1+Math.pow(Math.E , -z));
	}
	
	/**
	 * 
	 * @param matrix1
	 * @param matrix2
	 * 
	 * Append the second matrix beneath the first. The matrices are of dimension : dim(matrix1) = p x q and 
	 * dim(matrix2) = p x r. The resulting matrix will be of dimension p x (q+r)
	 * 
	 * @return
	 */
	public static Double[][] appendToTheBottom(Double[][] matrix1, Double[][] matrix2) 
	{
		//System.out.println("RepresentationNetwork.append first matrix len 1st dim: " + matrix1.length + ",len 2nd dim: " + matrix1[0].length );
		//System.out.println("RepresentationNetwork.append second matrix len 1st dim: " + matrix2.length + ",len 2nd dim: " + matrix2[0].length );
		
		Double[][] result = new Double[matrix1.length + matrix2.length][matrix1[0].length];
		
		for(int i = 0; i < matrix1.length; i++)
			for(int j = 0; j < matrix1[0].length; j++)
				result[i][j] = matrix1[i][j];
		
		for(int i = 0; i < matrix2.length; i++)
			for(int j = 0; j < matrix2[0].length; j++)
				result[i + matrix1.length][j] = matrix2[i][j];
        
        return result;
    }
	
	
	
	public static Double[][] appendToTheRight(Double[][] matrix1, Double[][] matrix2) 
	{
		//System.out.println("RepresentationNetwork.append first matrix len 1st dim: " + matrix1.length + ",len 2nd dim: " + matrix1[0].length );
		//System.out.println("RepresentationNetwork.append second matrix len 1st dim: " + matrix2.length + ",len 2nd dim: " + matrix2[0].length );
		
		Double[][] result = new Double[matrix1.length][matrix1[0].length + matrix2[0].length];
		
		for(int i = 0; i < matrix1.length; i++)
			for(int j = 0; j < matrix1[0].length; j++)
				result[i][j] = matrix1[i][j];
		
		for(int i = 0; i < matrix2.length; i++)
			for(int j = 0; j < matrix2[0].length; j++)
				result[i][j + matrix1[0].length] = matrix2[i][j];
        
        return result;
    }

}
