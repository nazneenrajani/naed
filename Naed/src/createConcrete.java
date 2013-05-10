import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;



public class createConcrete {
	public static void make(String text_file, String test_file, String output_train, String output_test, String concrete) throws IOException{
		Map<String,Integer> wordMap = new HashMap<String,Integer>();
		Double[] wVectors,iwVectors;
		String[] phrase =  new String[]{"at", "the" ,"end" ,"of" ,"the" ,"day"};
		BufferedReader br,nbr,ibr,inbr;
		//Integer[] accuracy;
		List<Integer> label = new ArrayList<Integer>();
		ArrayList<String> data = new ArrayList<String>();
		List<Integer> test_label = new ArrayList<Integer>();
		ArrayList<String> data_test = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(concrete));
			String line;
			while((line = br.readLine()) != null) {
				String smallLine = line.toLowerCase();
				String[] temp = smallLine.split(" ");
				System.out.println(temp[0]);
				System.out.println(temp[1]);
				 if(!wordMap.containsKey(temp[1])){
				        	 wordMap.put(temp[1],Integer.parseInt(temp[0]));
				         }
				}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		Double phraseValue = 0.0;
		int phCount = 0;
		for(int k = 0; k< phrase.length; k++){
			if(wordMap.containsKey(phrase[k])){
				phraseValue += wordMap.get(phrase[k]);
				phCount+=1;
			}
		}
		phraseValue= phraseValue/phCount;
		try {
			br = new BufferedReader(new FileReader(text_file));
			String line;
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String aps = "([A-Za-z]+)(')([a-z])";
			while((line = br.readLine()) != null) {
				String smallLine = line.toLowerCase();
				smallLine.replaceAll(reg,"");
				smallLine.replaceAll(aps,"$1");
				data.add(smallLine);
				}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		try {
			ibr = new BufferedReader(new FileReader(test_file));
			String line;
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String aps = "([A-Za-z]+)(')([a-z])";
			while((line = ibr.readLine()) != null) {
				String smallLine = line.toLowerCase();
				smallLine.replaceAll(reg,"");
				smallLine.replaceAll(aps,"$1");
				data_test.add(smallLine);
				}
			ibr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		
		wVectors = new Double[data.size()];
			for(int i =0;i<data.size();i++){
				wVectors[i]=0.0;
		}
			iwVectors = new Double[data_test.size()];
			for(int i =0;i<data_test.size();i++){
				iwVectors[i]=0.0;
		}
			for(int j=0;j<data.size();j++){
				StringTokenizer st = new StringTokenizer(data.get(j));
				String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
				String apos = "(')([a-z])";
				int value = 0;
		    	int tokenCount = 1;
			     while (st.hasMoreTokens()) {
			    	 String token = st.nextToken().toLowerCase();
			    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
			    		 continue;
			    	 if(!wordMap.containsKey(token))
		    			 continue;
			    	 value = value + wordMap.get(token);
			    	 tokenCount = tokenCount+1;
			    	 System.out.println(value);
		    		 System.out.println("***************");		    
			     }
		    	 wVectors[j] += (value*1.0/tokenCount);
		    			 //phraseValue ;
		    			 //+ (value*1.0/tokenCount);
			}
			
			for(int j=0;j<data_test.size();j++){
				StringTokenizer st = new StringTokenizer(data_test.get(j));
				String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
				String apos = "(')([a-z])";
				int value = 0;
		    	int tokenCount = 1;
			     while (st.hasMoreTokens()) {
			    	 String token = st.nextToken().toLowerCase();
			    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
			    		 continue;
			    	 if(!wordMap.containsKey(token))
		    			 continue;
			    	 value = value + wordMap.get(token);
			    	 tokenCount = tokenCount+1;
			    	 System.out.println(value);
		    		 System.out.println("***************");		    
			     }
		    	 iwVectors[j] += (value*1.0/tokenCount);
		    			 //phraseValue;
		    			 //+ (value*1.0/tokenCount);
			}
		File file = new File(output_train);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		try {
				for(int i=0;i<wVectors.length;i++){
					bw.write(wVectors[i].toString());
			bw.write("\n");
	}
		bw.close();
	}
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
		File ifile = new File(output_test);
		FileWriter ifw = new FileWriter(ifile.getAbsoluteFile());
		BufferedWriter ibw = new BufferedWriter(ifw);
		try {
				for(int i=0;i<iwVectors.length;i++){
					ibw.write(iwVectors[i].toString());
			ibw.write("\n");
	}
		ibw.close();
	}
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	}
}
