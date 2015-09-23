package NLP;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import company.Company;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;

import reading_data_from_file.ReadUniqueSymFromFile;
import scr.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JLabel;

import neural_network.RepresentationNetwork;
import neural_network.Training_X_Y_matrix;
import news.CompanyNewsSentiment;
import news.SingleGoogleNews;
import other_structures.DateModif;

public class Tabs {

	private JFrame frame;
	private JTextField textField;
	
	String file_path;
	List<String> lines = null;
	String [] K;
	Main Finance_Class = new Main();
	
	static Map<String, Company> Companies_match = new TreeMap<String, Company>();
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tabs window = new Tabs();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Tabs() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 832, 490);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("781px:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("417px:grow"),}));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, "2, 2, fill, fill");
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Online News", null, panel, null);
		panel.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 67, 529, 288);
		panel.add(textArea);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(569, 11, 201, 344);
		panel.add(textArea_1);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(476, 384, 275, 23);
		panel.add(textField);
		
		
		
		
		NER_Example NER = new NER_Example();
		List<String> Relevant_entities = null;
		JButton button_1 = new JButton("Choose a company");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("D:\\my documents\\Senior_Project_datasets\\My_news\\"));
				fc.setDialogTitle("Choose an online news to process");
				int returnVal = fc.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					file_path = fc.getSelectedFile().getAbsolutePath();
					String out_text = "";
					
					try {
						lines = read_file(file_path);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					out_text = List_to_String(lines, 80);
					
					textArea.setText(out_text);
						
				}
			}
		});
		button_1.setBounds(27, 384, 158, 23);
		panel.add(button_1);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Company Evaluation", null, panel_1, null);
		panel_1.setLayout(null);
		
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setBounds(63, 11, 184, 396);
		panel_1.add(textArea_2);
		
		JTextArea textArea_3 = new JTextArea();
		textArea_3.setBounds(10, 23, 529, 33);
		panel.add(textArea_3);
		
		JTextArea textArea_4 = new JTextArea();
		textArea_4.setBounds(320, 158, 303, 71);
		panel_1.add(textArea_4);
		
		
		JButton btnSeeListOf = new JButton("See list of symbols \r\nfor companies to \r\ndo financial analysis\r\n");
		btnSeeListOf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					
				//file_path = "D:\\my documents\\Senior_Project_datasets\\unique_company_symbols";
					
					
					try {
						Finance_Class.main(null);
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Companies_match = Finance_Class.returnCompaniesMap();
					System.out.println("Companies_match size:" + Companies_match.size());
					
					List<String> Sym = new ArrayList<String>(Companies_match.keySet());
					
					String out_text = "";
					for(int i=0;i<Sym.size();i++)
						out_text += Sym.get(i) + " \n";
					
					textArea_2.setText(out_text);
					System.out.println("out_text size:" + out_text.length());
				
				}
			
		});
		btnSeeListOf.setToolTipText("Show list of symbols \r\nfor companies to \r\nperform financial \r\nanalysis");
		btnSeeListOf.setBounds(283, 77, 370, 47);
		panel_1.add(btnSeeListOf);
		
		textField_1 = new JTextField();
		textField_1.setBounds(283, 311, 164, 38);
		panel_1.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnEnterACompany = new JButton("Enter a company to see a predicted price and sentiment");
		btnEnterACompany.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String Company_to_Eval = textField_1.getText();
				String Date = textField_2.getText();
				
				DateModif Date_m = new DateModif();
				try {
					Date_m = new DateModif(Date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<String> Train_sym = new ArrayList<String>();
				Train_sym.add(Company_to_Eval);
				
				List<DateModif> Date_current_news = new ArrayList<DateModif>();
				Date_current_news.add(Date_m);
				
				System.out.println(Finance_Class.get_num_of_parameters());
				Double [][] X_train_fin = new Double[Finance_Class.get_num_of_parameters()-6][1];
				
				int [] trainingSize = new int[1];
				try {
					Training_X_Y_matrix.Create_X_Y_finnance_Matrix(Train_sym, Date_current_news, Companies_match, X_train_fin, null, trainingSize);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				
				String Titl = textArea_3.getText();
				String Descript = textArea.getText();
				SingleGoogleNews News = new SingleGoogleNews(Company_to_Eval, Date_m, Date_m.get_year_in_date(),Titl ,Descript , "");	//for now source will be unknown (null value) 
				
				String output_to_textbox = "";
				
				Company Comp = Companies_match.get(Company_to_Eval);
				try {
					if(News.isRelevant(Comp.get_Company_SUE().GET_all_company_SUE_years(),  
							Comp.get_Fin_fundamentals().getAllCompanyFinFundamYears(), Comp.get_Company_Qoutes().Get_all_company_quotes_years(),						
							Comp.get_Company_Qoutes().get_quotes_map(), Comp.get_Company_Dividend().get_avg_dividends().keySet()))
					{
						output_to_textbox += "Relevant news \n";
						
						CompanyNewsSentiment Company = new CompanyNewsSentiment(Company_to_Eval, Finance_Class.getPipe());
						Double[] Title_Senti = new Double [3];
						Title_Senti= Company.evaluateSentiOfText(Titl);
						Double [] Descript_Senti = new Double [3];
						Descript_Senti= Company.evaluateSentiOfText(Descript);
						
						Double [] All_senti = new Double [6];
						for(int i=0;i<3;i++)
							All_senti[i] = Title_Senti[i];
						for(int i=0;i<3;i++)
							All_senti[i+3] = Descript_Senti[i];
						
						Double [] Ar = new Double [X_train_fin.length];
						for(int i=0;i<X_train_fin.length;i++)
						{
							Ar[i] = X_train_fin[i][0];
							System.out.println("Ar[i]:"  + Ar[i]);
						}
						for(int i=0;i<All_senti.length;i++)
						{
							System.out.println("All_senti[i]:"  + All_senti[i]);
						}
						Double value = RepresentationNetwork.makePrediction(append(Ar,All_senti));
						output_to_textbox += "Expected price for the next day is" + output_to_textbox.toString();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				
			}
		});
		btnEnterACompany.setBounds(346, 254, 263, 38);
		panel_1.add(btnEnterACompany);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(492, 311, 164, 38);
		panel_1.add(textField_2);
		
		JLabel lblCompanySymbol = new JLabel("Company symbol");
		lblCompanySymbol.setBounds(306, 360, 127, 26);
		panel_1.add(lblCompanySymbol);
		
		JLabel lblDateOfThe = new JLabel("Date of the news you have inserted");
		lblDateOfThe.setBounds(492, 360, 188, 26);
		panel_1.add(lblDateOfThe);
		
		
		
		
		
		
		
		
		
		
		
		
		JButton btnProcess = new JButton("Process");
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					NER.relevant_companies(file_path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String out_text = List_to_String(lines, 60);
				
				textArea_1.setText(out_text);
			}
		});
		btnProcess.setBounds(195, 384, 89, 23);
		panel.add(btnProcess);
		
		JButton btnValidate = new JButton("Validate");
		btnValidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					ReadUniqueSymFromFile Company_Sym = 
							new ReadUniqueSymFromFile("D:\\my documents\\Senior_Project_datasets\\unique_company_symbols.txt");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnValidate.setBounds(310, 384, 99, 23);
		panel.add(btnValidate);
		
		
	}
	
	
	List<String> read_file(String file_path) throws IOException	//maybe useful for deleting unnecessary files
	{
		
		List<String> Symbols = new ArrayList<String>();
		  
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(new java.io.File(file_path))));
				
		String line;
			
		while((line = read.readLine()) != null)
		{
			if(line.toString()!="")
				Symbols.add(line.toString());
		}
		read.close();
		return Symbols;
	}
	
	String List_to_String(List<String> lines, int line_max_size_in_textbox)
	{
		String out_text = "";
		if(!lines.equals(null))
		for(int i=0; i<lines.size(); i++)
		{
			String small_line = "", line = "";
			line = lines.get(i);
			String [] Words = line.split("\\s+");
			
			int k = 0;
			for(int j=0;j<Words.length; j++)
			{
				if(Words.length>0)
				{
					k+=Words[j].length()+1;
					
					if(k>line_max_size_in_textbox)
					{
						out_text += small_line + " \n";
						k=Words[j].length();
						small_line = Words[j] + " ";
						
					}
					else
					{
						small_line += Words[j] + " ";
					}
				}
				else
				{
					System.out.println("one word");
					out_text +=Words[0] + "\n";
				}
			}
		}	
		return out_text;
	}
	
	public static Double[] append(Double[] doubles, Double[] doubles2) 
	{
		Double[] result = new Double[doubles.length + doubles2.length];
        System.arraycopy(doubles, 0, result, 0, doubles.length);
        System.arraycopy(doubles2, 0, result, doubles.length, doubles2.length);
        return result;
    }
}



