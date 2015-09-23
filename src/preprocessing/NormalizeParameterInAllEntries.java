package preprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import daily_financial_parameters.Basic_daily_fin_data;
import yearly_financial_parameters.basicYearlyFinData;

public class NormalizeParameterInAllEntries<S_D,T> {
	
	Double _globalMaxOfParameter = 0.0;
	Double _globalMinOfParameter = 0.0;
	SortedMap <S_D, T> _allValuesOfParameterNonnormalized = new TreeMap<S_D,T>();
	SortedMap <S_D, T> _allValuesOfParameterNormalized = new TreeMap<S_D,T>();
	//List<Double> Normalized_list = new ArrayList<Double>();
	
	//one of the next two classes will be used
	List<Basic_daily_fin_data> Daily_fin_fundam;
	List<basicYearlyFinData> Fin_fundam;
	
	
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
		
		if (value.getClass().toString().equals(new Basic_daily_fin_data("",0.0).getClass().toString()))		//daily data
		{
			Collections.sort((List<Basic_daily_fin_data>) orderedValsOfParam, (c1,c2) -> (c1).compare(c2));
			/*for(int i = 0; i<orderedValsOfParam.size(); i++)
				System.out.println("orderedValsOfParam val:" + ((Basic_daily_fin_data)orderedValsOfParam.toArray()[i]).get_val());
			*/
			_globalMaxOfParameter = 
					((Basic_daily_fin_data) orderedValsOfParam.toArray()[orderedValsOfParam.size()-1]).get_val();
			_globalMinOfParameter = 
					((Basic_daily_fin_data)orderedValsOfParam.toArray()[0]).get_val();
			
			for(S_D key : Keys)
			{
				Basic_daily_fin_data D = (Basic_daily_fin_data) _allValuesOfParameterNonnormalized.get(key);
				//System.out.println("Daily parameter to be normalized:" + D.get_val());
				if(D.get_val().isNaN())
				{
					//D.set_val(Double.NaN);
					continue;
				}
				else
				{
					D.set_val((D.get_val() - _globalMinOfParameter) / (_globalMaxOfParameter - _globalMinOfParameter));
				}
				//System.out.println("Daily parameter normalized:" + D.get_val());
				_allValuesOfParameterNormalized.put(key, (T) D);
			}
		}
		
		else	//yearly data
		if(value.getClass().equals(new basicYearlyFinData(null,null).getClass()))
		{
			Collections.sort((List<basicYearlyFinData>) orderedValsOfParam, (c1,c2) -> (c1).compare(c2));	
			/*for(int i = 0; i<orderedValsOfParam.size(); i++)
				System.out.println("orderedValsOfParam val:" + ((basicYearlyFinData)orderedValsOfParam.toArray()[i]).getVal());
			*/
			_globalMaxOfParameter = 
					((basicYearlyFinData) orderedValsOfParam.toArray()[orderedValsOfParam.size()-1]).getVal();
			_globalMinOfParameter = 
					((basicYearlyFinData)orderedValsOfParam.toArray()[0]).getVal();

			for(S_D key : Keys)
			{
				basicYearlyFinData D = (basicYearlyFinData) _allValuesOfParameterNonnormalized.get(key);
				//System.out.println("Yearly parameter to be normalized:" + D.getVal());
				
				if(D.getVal().isNaN())
				{
					//D.Set_val(Double.NaN);
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
	
	/*public void write_normalized_files() throws IOException
	{
		final Charset ENCODING = StandardCharsets.UTF_8;
		Path path = Paths.get( input_folder_path + "Normalized\\ " + file_name);
		Files.write(path, Lines_to_output, ENCODING);
	}*/
	
	/*public List<Double> get_Normalized_list()
	{
		return Normalized_list;
	}*/

}
