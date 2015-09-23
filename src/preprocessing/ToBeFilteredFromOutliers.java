package preprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ToBeFilteredFromOutliers<S_D,T>  {
	
	HashMap<S_D,T> _orderedMap ;
	
	
	@SuppressWarnings("unchecked")
	public ToBeFilteredFromOutliers(Map<S_D,T> Parameter)
	{
		_orderedMap = new HashMap<S_D,T>(Parameter);
	}
	
	/**
	 * Returns TreeMap with outliers
	 * 
	 * @return TreeMap<S_D,T> 
	 */
	public HashSet<S_D> filterFromOutliers()
	{
		HashSet<S_D> outliers = new HashSet<S_D>();
		S_D[] Keys = (S_D[]) _orderedMap.keySet().toArray();
		//toArray(new Sym_Year[Parameter.size()]);
		T val = (T) _orderedMap.values().toArray()[0];
		
		
		Double lower_upper_outliers = _orderedMap.size()/200.0;
		
		for(int i=0; i<lower_upper_outliers.intValue();  i++)	
		{
			outliers.add((S_D) Keys[i]);
		}
		
		/*
		for(int i=lower_upper_outliers.intValue(); i< _orderedMap.size() - 1 - lower_upper_outliers; i++)	
		{
			outliers.put((S_D) Keys[i],_orderedMap.get(Keys[i]));
		}
		*/
		
		for(int i=(int) (_orderedMap.size() - 1 - lower_upper_outliers); i<_orderedMap.size();  i++)	
		{
			outliers.add((S_D) Keys[i]);
		}
		
		return  outliers;
	}

}
