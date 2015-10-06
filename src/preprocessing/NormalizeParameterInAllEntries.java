package preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import daily_financial_parameters.BasicDailyFinData;
import writing_data_to_file.WriteToFile;
import yearly_financial_parameters.BasicYearlyFinData;

public class NormalizeParameterInAllEntries<S_D,T> {
	
	Double _globalMaxOfParameter = 0.0;
	Double _globalMinOfParameter = 0.0;
	SortedMap <S_D, T> _allValuesOfParameterNonnormalized = new TreeMap<S_D,T>();
	SortedMap <S_D, T> _allValuesOfParameterNormalized = new TreeMap<S_D,T>();
	//List<Double> Normalized_list = new ArrayList<Double>();
	
	//one of the next two classes will be used
	List<BasicDailyFinData> Daily_fin_fundam;
	List<BasicYearlyFinData> Fin_fundam;
	
	
	public T getNonNormalizedData( S_D symDate)
	{
		return _allValuesOfParameterNonnormalized.get(symDate);
	}
	
	public T getNormalizedData( S_D symDate)
	{
		return _allValuesOfParameterNonnormalized.get(symDate);
	}
	
	public Double get_min()
	{
		return _globalMinOfParameter;
	}
	
	public Double get_max()
	{
		return _globalMaxOfParameter;
	}
	
	
	/*
	public void make_double_list_from_string_list(List<String> Rough_list)
	{
		Integer list_size = Rough_list.size(); 
	
		for(int i=0;i< list_size; i++)
		{
			String text_field = Rough_list.get(i);
			if(text_field.equals("NaN") || text_field.equals(""))
			{
				Converted_rough_list.add(Double.NaN);
				continue;
			}
			
			Double field = Double.parseDouble(text_field);
			Converted_rough_list.add(field);
		}
	}*/
	
	
	@SuppressWarnings("unchecked")
	public	NormalizeParameterInAllEntries(Map <S_D, T> Parameter)
	{	
		_allValuesOfParameterNormalized.clear();
		
		//checking how all values are sorted	
		_allValuesOfParameterNonnormalized = (SortedMap<S_D, T>) Parameter;
		
		
		List <T> orderedValsOfParam = new ArrayList<T>(_allValuesOfParameterNonnormalized.values());		//TO DO!!! , otherwise nonesence in the normalization!!!!!!!!
		Collection<S_D> Keys = _allValuesOfParameterNonnormalized.keySet();
		
		T value = (T) _allValuesOfParameterNonnormalized.values().toArray()[0];
		
		if (value.getClass().toString().equals(new BasicDailyFinData("",0.0).getClass().toString()))		//daily data
		{
			Collections.sort((List<BasicDailyFinData>) orderedValsOfParam, (c1,c2) -> (c1).compare(c2));
			/*for(int i = 0; i<orderedValsOfParam.size(); i++)
				System.out.println("orderedValsOfParam val:" + ((Basic_daily_fin_data)orderedValsOfParam.toArray()[i]).get_val());
			*/
			_globalMaxOfParameter = 
					((BasicDailyFinData) orderedValsOfParam.toArray()[orderedValsOfParam.size()-1]).getVal();
			_globalMinOfParameter = 
					((BasicDailyFinData)orderedValsOfParam.toArray()[0]).getVal();
			
			for(S_D key : Keys)
			{
				BasicDailyFinData D = (BasicDailyFinData) _allValuesOfParameterNonnormalized.get(key);
				//System.out.println("Daily parameter to be normalized:" + D.get_val());
				if(D.getVal() == null || D.getVal().isNaN() || D.getVal().isInfinite() )
				{
					System.out.println("D.getVal() => continue");
					continue;
				}
				else
				{
					D.setVal((D.getVal() - _globalMinOfParameter) / (_globalMaxOfParameter - _globalMinOfParameter));
				}
				//System.out.println("Daily parameter normalized:" + D.getVal());
				_allValuesOfParameterNormalized.put(key, (T) D);
			}
		}
		
		else	//yearly data
		if(value.getClass().equals(new BasicYearlyFinData(null,null).getClass()))
		{
			
			Collections.sort((List<BasicYearlyFinData>) orderedValsOfParam, (c1,c2) -> (c1).compare(c2));	
			/*for(int i = 0; i<orderedValsOfParam.size(); i++)
				System.out.println("orderedValsOfParam val:" + ((basicYearlyFinData)orderedValsOfParam.toArray()[i]).getVal());
			*/
			_globalMaxOfParameter = 
					((BasicYearlyFinData) orderedValsOfParam.toArray()[orderedValsOfParam.size()-1]).getVal();
			_globalMinOfParameter = 
					((BasicYearlyFinData)orderedValsOfParam.toArray()[0]).getVal();

			
			for(S_D key : Keys)
			{
				BasicYearlyFinData D = (BasicYearlyFinData) _allValuesOfParameterNonnormalized.get(key);
				//System.out.println("Yearly parameter to be normalized:" + D.getVal());
				
				if(D.getVal() == null || D.getVal().isNaN() || D.getVal().isInfinite() )
				{
					System.out.println("D.getVal() => continue");
					continue;
				}
				else
				{
					D.setVal((D.getVal() - _globalMinOfParameter) / (_globalMaxOfParameter - _globalMinOfParameter));
				}
				//System.out.println("Yearly parameter normalized:" + D.getVal());
				_allValuesOfParameterNormalized.put(key, (T) D);
			}
		}
		System.out.println("global_max_of_parameter:" + _globalMaxOfParameter);
		System.out.println("global_min_of_parameter:" + _globalMinOfParameter);
	}
	
	
	public Map<S_D,T> getValuesOfParameterNonnormalized()
	{
		return _allValuesOfParameterNonnormalized;
	}
	
	public Map<S_D,T> getValuesOfParameterNormalized()
	{
		return _allValuesOfParameterNormalized;
	}
}
