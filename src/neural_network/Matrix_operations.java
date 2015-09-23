package neural_network;

public class Matrix_operations 
{
	
	public Double[][] vector_dot_minus (Double d, Double [][] M1,  int index_1 ,  int index_2)
	{
		Double[][] R = new Double[index_1][index_2];
		for(int i=0; i<index_1 ; i++)
			for(int j=0; j<index_2 ; j++)
				R[i][j] = d - M1[i][j];
		return R;
	}
	
	public Double[][] matrix_product (Double [][] M1, Double [][] M2, Integer index_1 , Integer index_2, Integer index_3)
	{
		Double[][] R = new Double[index_1][index_3];
		for(int i=0; i<index_1 ; i++)
			for(int j=0; j<index_3; j++)
			{
				Double Sum = 0.0;
				for(int k=0; k< index_2; k++)
				{
					/*System.out.println("i:" + i + "k:" + k + "j:" + j);
					System.out.println("M1[i][k]:" + M1[i][k] + ",M2[k][j]:" + M2[k][j]); 
							
					Double d = null;
					System.out.println(
							M2[k][j] == null || (M2[k][j] != null && M2[k][j].equals(null))
							|| (M2[k][j] != null && M2[k][j].equals(d))); 
					if(M2[k][j] != null)
						System.out.println(M2[k][j].toString());
					System.out.println("M2[k][j]:" + M2[k][j]);				*/	
					Sum += M1[i][k] * M2[k][j];
				}
				R[i][j] = Sum;
			}
		return R;
	}
	
	public static Double[] matrix_product (Double [][] M1, Double [] M2, int index_1 , int index_2)
	{
		Double[] R = new Double[index_1];
		for(int i=0; i<index_1 ; i++)
		{
			Double Sum = 0.0;
			for(int j=0; j<index_2; j++)
			{
				Sum += M1[i][j]*M2[j];
			}
			R[i] = Sum;
		}
		return R;
	}
	
	public Double[][] transposeMatrix(Double[][] theta2)
	{
		Double[][] T_matrix = new Double[theta2[0].length][theta2.length];
        for (int i = 0; i < theta2.length; i++)
            for (int j = 0; j < theta2[0].length; j++)
            	T_matrix[j][i] = theta2[i][j];
        return T_matrix;
    }
	
	public Double[][] dot_product (Double [][] M1, Double [][] M2, int index_1 , int index_2)
	{
		Double [][] R = new Double [index_1][index_2];
		for(int i=0; i<index_1; i++)
			for(int j=0; j<index_2; j++)
				R[i][j] = M1[i][j] * M2[i][j];
		return R;
	}
	
	public Double[][] dot_sum (Double [][] M1, Double [][] M2, int index_1 , int index_2)
	{
		Double [][] R = new Double [index_1][index_2];
		for(int i=0; i<index_1; i++)
			for(int j=0; j<index_2; j++)
				R[i][j] = M1[i][j] + M2[i][j];
		return R;
	}
	
	static Double[][] add_ones_as_first(Double[][] D, int index1, int index2)
	{
		Double[][] A = new Double[index1+1][index2];
		for(int i =1; i<=index1; i++)
			for(int j=0; j< index2; j++)
				A[i][j] = D[i-1][j];
		for(int j=0; j< index2; j++)
			A[0][j]=1.0;
		return A;
	}
	
	static Double[] add_ones_as_first(Double[] D)
	{
		Double[] A = new Double[D.length+1];
		for(int i =1; i<=D.length; i++)
			A[i] = D[i-1];

		A[0]=1.0;
		return A;
	}
	
	static Double[][] delete_first_raw (Double[][] D, int index1, int index2)
	{
		Double[][] A = new Double[index1-1][index2];
		for(int i =1; i< index1; i++)
			for(int j=0; j< index2; j++)
				A[i-1][j] = D[i][j];
		return A;
	}
}
