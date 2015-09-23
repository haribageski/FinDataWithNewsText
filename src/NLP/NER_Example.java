package NLP;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import reading_data_from_file.ReadFromFile;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

class NER_Example 
{
	//List <String> Lines;
	
    void relevant_companies(String fileName ) throws IOException {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        props.put("dcoref.score", true);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        
        
        PrintWriter out; //Prints formatted representations of objects to a text-output stream
        out = new PrintWriter("output.txt");
        
		String aText = ReadFromFile.readFileToString(fileName);   
		
		Annotation document = new Annotation(aText);
		pipeline.annotate(document);
	    //pipeline.prettyPrint(document, out);
	    //out.println();
	   
		Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);

		
		
		
		CoreLabel token;
        
        for(Map.Entry<Integer, CorefChain> entry : graph.entrySet()) 
        {
        	String clust = "";
	        CorefChain c = entry.getValue();
	        CorefMention cm = c.getRepresentativeMention();
	        
	        //list of tokens
	        List<CoreLabel> tks = document.get(CoreAnnotations.SentencesAnnotation.class).get(cm.sentNum-1).get(TokensAnnotation.class);
	        
	        CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(cm.sentNum-1);
	        
	        boolean relevant = false;
	        for(int i = cm.startIndex-1; i < cm.endIndex-1; i++)
	        {
	        	clust += tks.get(i).get(TextAnnotation.class) + " ";
	        	token=tks.get(i);
	        	
		        String ne = token.get(NamedEntityTagAnnotation.class);    // this is the NER label of the token
		        
		        //if at least one is relevant token in the sentence
		        if(ne.endsWith("PERSON") || ne.endsWith("ORGANIZATION") || ne.endsWith("LOCATION"))
		        {
		        	System.out.println(" relevant token:");
		        	System.out.println(" this is the NER label " + ne + " and lemma: " + token.lemma() + " and tag " + token.tag() + " of the token " + token.get(TextAnnotation.class));
		        	relevant = true;
		        }
	        }
	        
	        if(relevant==true)
	        {
	        	System.out.println();
	        	
		        System.out.println("New sentence...");
		        clust = clust.trim();
	        	System.out.println( "Representative Mention: " + cm.toString() + ", also appearing as:" );
		        
	        	if(cm.toString().length()<30)	//larger than 30 characters are not really an entity
	        	{
		        	//Lines.add("Representative Mention: " + cm.toString() + ", also appearing as:");
		        	System.out.println("Representative Mention: " + cm.toString() + ", also appearing as:");
		        	
			        //print without saying the number of sentence where it appears
			        
			        for(int i = cm.startIndex-1; i < cm.endIndex-1; i++)
			            clust += tks.get(i).get(TextAnnotation.class) + " ";
			        //clust = clust.trim();
			        System.out.println("representative mention: \"" + clust + "\" ");	
			        //Lines.add(clust);
	        	}
	        
	        	
		        for(CorefMention m : c.getMentionsInTextualOrder())
	            {
	                String clust2 = "";
	                tks = document.get(SentencesAnnotation.class).get(m.sentNum-1).get(TokensAnnotation.class);
	                for(int i = m.startIndex-1; i < m.endIndex-1; i++)
	                    clust2 += tks.get(i).get(TextAnnotation.class) + " ";
	                clust2 = clust2.trim();
	                
	                //don't need the self mention
	                if(clust.equals(clust2) && c.getMentionsInTextualOrder().size()!=1)
	                    continue;
	
	                System.out.println("\t" + clust2);
	            }
	        }
        }
		
		/*
		
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
		for(CoreMap sentence: sentences) 
    	{
			List<CoreLabel> Tokes = sentence.get(TokensAnnotation.class);
    	}
		
		
        for(Map.Entry<Integer, CorefChain> entry : graph.entrySet()) 
        {

        	CorefMention cm = c.getRepresentativeMention();
        	System.out.println( "Representative Mention: " + aText.subSequence(cm.startIndex, cm.endIndex));

        	/*List<CorefMention> cms = c.getCorefMentions();
        	println  "Mentions:  ";
        	cms.each 
        	{ 
        		it ->
        		print aText.subSequence(it.startIndex, it.endIndex) + "|"; 
        	} 
            println "" 
            */
        //}
    //return Lines;
    }
}