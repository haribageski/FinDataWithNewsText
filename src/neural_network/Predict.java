package neural_network;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import company.Company;
import other_structures.DateModif;
import other_structures.Sym_Date;

public class Predict extends RepresentationNetwork
{
	public Predict(Set<Sym_Date> symDatesForTrainingML,	Map<String, Company> companies_match, 
			int training_size, int param, Double[][] X_senti) throws FileNotFoundException, UnsupportedEncodingException {
		super(symDatesForTrainingML, companies_match, training_size, param, X_senti);
		// TODO Auto-generated constructor stub
	}

	public static Double predict(Double[][] Theta1,Double[][] Theta2,Double[][] Theta3, 
			Double [] X, int Layers, int parameters)
	{	
		int m = X.length;		//number of parameters
		
		int [] p = new int[m];
		for(int i=0;i<m;i++)
			p[i] = 0;
		
		Double [][]a_l = new Double[Layers+1][parameters+1];
		a_l[1] = Matrix_operations.add_ones_as_first(X);
		Double[] Z_2 = new Double[X.length];
		
		Z_2 = Matrix_operations.matrix_product(Theta1, X , parameters, parameters+1);
		for(int i=0; i< parameters; i++)
			a_l[2][i] = g_func (Z_2[i]);
		a_l[2] = add_ones_as_first(a_l[2] );
		
		Double[] Z_3 = new Double[parameters];
		Z_3 = matrix_product(Thetas[2], a_l[2] , parameters, parameters+1);
		for(int i=0; i< parameters; i++)
			a_l[3][i] = g_func (Z_3[i]);
		a_l[3] = add_ones_as_first(a_l[3] );
		
		Double Z_4 = 0.0;
		Z_4 = matrix_product(Thetas[3], a_l[3] , 1, parameters+1)[0];

		h_Theta_all_a4[0][0] = g_func (Z_4);
		return  h_Theta_all_a4[0][0];
	}
}
