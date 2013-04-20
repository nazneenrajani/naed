import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class Tagger {
	
	public static void tagText(MaxentTagger tagger, String filename) {
		String path = "../data/pos/";
		String outputfile = path + "pos_" + filename;
		
		System.out.println("Tagging " + filename + " ...");
		
		try {
 
			String line;
 
			BufferedReader in = new BufferedReader(new FileReader(path + filename));
			File file = new File(outputfile);
			BufferedWriter out= new BufferedWriter(new FileWriter(file));
 
			while ((line = in.readLine()) != null) {
				if (line.length() > 0) {
					if (line.charAt(0) == '@' || line.charAt(0) == '$') {
							out.write(line + "\n");
						}
					else {
						String tagged = tagger.tagString(line);
						String[] tokens = tagged.split(" ");
						for (String t : tokens) {
							out.write(t + "\n");
						} 
					}
				}
				else {
					out.write("\n");
				}
			}
 
			in.close();
			out.close();
		    } 
		catch (IOException e) {
			e.printStackTrace();
		  } 
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		MaxentTagger tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
		
		String[] files = new String[6];
		files[0] = "dev.txt";
		files[1] = "train.txt";
		files[2] = "test.txt";
		files[3] = "lex_dev.txt";
		files[4] = "lex_train.txt";
		files[5] = "lex_test.txt";
		
		for (String file : files ) {
			tagText(tagger, file);
		}
		
		System.out.println("Done.");
	
	}
}
