package NLP;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;


public class Senior_Thesis_UI {

	private JFrame frame;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextArea textArea;
	private JButton btnEvaluate;
	
	
	String file_path;
	List<String> lines = null;
	private JButton btnEvaluate_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Senior_Thesis_UI window = new Senior_Thesis_UI();
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
	public Senior_Thesis_UI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 656, 424);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField_2 = new JTextField();
		textField_2.setBounds(476, 350, 154, 23);
		frame.getContentPane().add(textField_2);
		textField_2.setColumns(10);
		
		JButton btnChooseACompany = new JButton("Choose a company");
		btnChooseACompany.addActionListener(new ActionListener() {
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

					out_text = List_to_String(lines, 60);
					
					textArea.setText(out_text);
						
				}
				
			}
		});
		
		
		btnChooseACompany.setBounds(60, 350, 123, 23);
		frame.getContentPane().add(btnChooseACompany);
		
		textArea = new JTextArea();
		textArea.setBounds(10, 10, 437, 331);
		frame.getContentPane().add(textArea);
		
		
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(476, 22, 133, 290);
		frame.getContentPane().add(textArea_1);
		
		
		
		
		
		NER_Example NER = new NER_Example();
		List<String> Relevant_entities = null;
		
		btnEvaluate_1 = new JButton("Evaluate");
		btnEvaluate_1.addActionListener(new ActionListener() {
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
		btnEvaluate_1.setBounds(227, 350, 89, 23);
		frame.getContentPane().add(btnEvaluate_1);
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
		
		for(int i=0; i<lines.size(); i++)
		{
			String small_line = "";
			int k = 0;
			for(int j=0;j<lines.get(i).length(); j++)
			{
				small_line +=lines.get(i).toCharArray()[j];
				k++;
				if(k==line_max_size_in_textbox)
				{
					out_text += small_line + " \n";
					k=0;
					small_line = "";
				}
			}
		}	
		return out_text;
	}
}
