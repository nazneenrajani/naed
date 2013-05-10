import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


public class createCoocurrence {
	public static void make(String text_file,String test_file, String semFile, String output_file,String output_test) throws IOException{
		Map<String,Integer> wordMap = new LinkedHashMap<String,Integer>();
		Map<String,Double[]> semMap = new HashMap<String,Double[]>();
		//Map<String,Integer> phraseMap = new HashMap<String,Integer>();
		Double[] wVectors,iwVectors;
		BufferedReader br,pbr,ubr;
		int count = 0;
		//Integer[] accuracy;
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<String> data_test = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(text_file));
			String line;
			String ph,ur;
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String aps = "([A-Za-z]+)(')([a-z])";
			//int i =0;
			/*try {
				File file = new File("clean_test.txt");text
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				*/
			while((line = br.readLine()) != null) {
				String smallLine = line.toLowerCase();
				smallLine.replaceAll(reg,"");
				smallLine.replaceAll(aps,"$1");
				data.add(smallLine);
				StringTokenizer st = new StringTokenizer(smallLine);
				     while (st.hasMoreTokens()) {
				    	 String token = st.nextToken();
				    	 if (token.matches(reg) || token.matches(apos)|| token.matches("\\d+"))
				    		 continue;
				    	 	//bw.write(token);
				    	 	//bw.write(" ");
				    	 //System.out.println(token);
				         if(!wordMap.containsKey(token)){
				        	 wordMap.put(token,count);
				        	 count +=1;
				         }
				     }
				     //bw.write("\n");
				}
			//bw.close();
			//}
			/*catch (IOException e) {
				e.printStackTrace();
				}
			//i++;	*/
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		try {
			ubr = new BufferedReader(new FileReader(test_file));
			String line;
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String aps = "([A-Za-z]+)(')([a-z])";
			while((line = ubr.readLine()) != null) {
				String smallLine = line.toLowerCase();
				smallLine.replaceAll(reg,"");
				smallLine.replaceAll(aps,"$1");
				data_test.add(smallLine);
				}
			ubr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		try {
			pbr = new BufferedReader(new FileReader(semFile));
			String line;
			while((line = pbr.readLine()) != null) {
				String[] temp = line.split(" ");
				Double[] sim = new Double[temp.length];
				for(int i =0;i<temp.length;i++)
					sim[i] = Double.parseDouble(temp[i]);
				Set<String> keys = wordMap.keySet();
				Iterator<String> iter = keys.iterator();
				 String key = (String) iter.next();
				  semMap.put(key, sim);				
				     //bw.write("\n");
				}
			pbr.close();
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
			Double value = 0.0;
	    	int tokenCount = 0;
		     while (st.hasMoreTokens()) {
		    	 String token = st.nextToken().toLowerCase();
		    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
		    		 continue;
		    	 if(!semMap.containsKey(token))
	    			 continue;
		    	 Double[] val = semMap.get(token);
		    	 System.out.println(val[0]);
	    		 System.out.println(val[1]);
	    		 System.out.println("***************");
		    	 StringTokenizer st1 = new StringTokenizer(data.get(j));
		    	 while (st1.hasMoreTokens()) {
		    		 String token1 = st1.nextToken().toLowerCase();
		    		 if(!semMap.containsKey(token1))
		    			 continue;
		    		 if(token1 == token)
		    			 continue;
		    		 Double[] temp  = semMap.get(token1);
		    		 System.out.println(temp[0]);
		    		 System.out.println(temp[1]);
		    		 tokenCount ++;
		    		 value = value + val[0]*temp[0] + val[1]*temp[1];
		    	 }
		     }
		     value = value/tokenCount;
		     if (Double.isNaN(value))
		    	 value = -1.0;
	    	 wVectors[j] = value;
		}
		PrintStream ps;
		File file = new File(output_file);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		try {
				//bw.write(wVectors.length+" "+wVectors[0].length);
				//bw.write("\n");
				for(int i=0;i<wVectors.length;i++){
					bw.write(wVectors[i].toString());
					bw.write("\n");
	}
		bw.close();
	}
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
		for(int j=0;j<data_test.size();j++){
			StringTokenizer st = new StringTokenizer(data_test.get(j));
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			Double value = 0.0;
	    	int tokenCount = 0;
		     while (st.hasMoreTokens()) {
		    	 String token = st.nextToken().toLowerCase();
		    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
		    		 continue;
		    	 if(!semMap.containsKey(token))
	    			 continue;
		    	 Double[] val = semMap.get(token);
		    	 System.out.println(val[0]);
	    		 System.out.println(val[1]);
	    		 System.out.println("***************");
		    	 StringTokenizer st1 = new StringTokenizer(data_test.get(j));
		    	 while (st1.hasMoreTokens()) {
		    		 String token1 = st1.nextToken().toLowerCase();
		    		 if(!semMap.containsKey(token1))
		    			 continue;
		    		 if(token1 == token)
		    			 continue;
		    		 Double[] temp  = semMap.get(token1);
		    		 System.out.println(temp[0]);
		    		 System.out.println(temp[1]);
		    		 tokenCount ++;
		    		 value = value + val[0]*temp[0] + val[1]*temp[1];
		    	 }
		     }
		     value = value/tokenCount;
		     if (Double.isNaN(value))
		    	 value = -1.0;
	    	 iwVectors[j] = value;
		}
		File ifile = new File(output_test);
		FileWriter ifw = new FileWriter(ifile.getAbsoluteFile());
		BufferedWriter ibw = new BufferedWriter(ifw);
		try {
				//bw.write(wVectors.length+" "+wVectors[0].length);
				//bw.write("\n");
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
