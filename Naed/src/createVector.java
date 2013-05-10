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

import edu.stanford.nlp.trees.Tree;


public class createVector {
	public static void make(String text_file, String label_file,String test_file, String label_test, String output_train, String output_test) throws IOException{
		Map<String,Integer> wordMap = new HashMap<String,Integer>();
		Integer[][] wVectors,iwVectors;
		BufferedReader br,nbr,ibr,inbr;
		int count = 1;
		//Integer[] accuracy;
		List<Integer> label = new ArrayList<Integer>();
		ArrayList<String> data = new ArrayList<String>();
		List<Integer> test_label = new ArrayList<Integer>();
		ArrayList<String> data_test = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(text_file));
			nbr = new BufferedReader(new FileReader(label_file));
			String line;
			String num;
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String aps = "([A-Za-z]+)(')([a-z])";
			//int i =0;
			/*try {
				File file = new File("clean_test.txt");
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
			while ((num = nbr.readLine()) != null) {
	            label.add(Integer.parseInt(num));
	        }
			//labelArr = label.toArray(new Integer[label.size()]);
			nbr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		try {
			ibr = new BufferedReader(new FileReader(test_file));
			inbr = new BufferedReader(new FileReader(label_test));
			String line;
			String num;
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String aps = "([A-Za-z]+)(')([a-z])";
			//int i =0;
			/*try {
				File file = new File("clean_test.txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				*/
			while((line = ibr.readLine()) != null) {
				String smallLine = line.toLowerCase();
				smallLine.replaceAll(reg,"");
				smallLine.replaceAll(aps,"$1");
				data_test.add(smallLine);
				     //bw.write("\n");
				}
			//bw.close();
			//}
			/*catch (IOException e) {
				e.printStackTrace();
				}
			//i++;	*/
			ibr.close();
			while ((num = inbr.readLine()) != null) {
	            test_label.add(Integer.parseInt(num));
	        }
			//labelArr = label.toArray(new Integer[label.size()]);
			inbr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/*
		Set keys = wordMap.keySet();

		   for (Iterator i = keys.iterator(); i.hasNext();) 
		   {
		       String key = (String) i.next();
		       //String value = (String) map.get(key);
		       System.out.println(key);;
		   }
		   */
		wVectors = new Integer[data.size()][wordMap.size()+1];
		for(int i =0; i<data.size();i++){
			wVectors[i][0] = label.get(i);
			for(int j =1;j<=wordMap.size();j++){
				wVectors[i][j]=0;
			}
		}
		for(int j=0;j<data.size();j++){
			StringTokenizer st = new StringTokenizer(data.get(j));
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
		     while (st.hasMoreTokens()) {
		    	 String token = st.nextToken().toLowerCase();
		    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
		    		 continue;
		    	 int val = wordMap.get(token);
		    	 wVectors[j][val]++;
		     }
		}
		iwVectors = new Integer[data_test.size()][wordMap.size()+1];
		for(int i =0; i<data_test.size();i++){
			iwVectors[i][0] = test_label.get(i);
			for(int j =1;j<=wordMap.size();j++){
				iwVectors[i][j]=0;
			}
		}
		for(int j=0;j<data_test.size();j++){
			StringTokenizer st = new StringTokenizer(data_test.get(j));
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
		     while (st.hasMoreTokens()) {
		    	 String token = st.nextToken().toLowerCase();
		    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
		    		 continue;
		    	 if(!wordMap.containsKey(token))
			    	 	continue;
		    	 int val = wordMap.get(token);
		    	 iwVectors[j][val]++;
		     }
		}
		PrintStream ps;
		File file = new File(output_train);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		try {
				bw.write(wVectors.length+" "+wVectors[0].length);
				bw.write("\n");
				for(int i=0;i<wVectors.length;i++){
					for(int j=0;j<wVectors[0].length;j++){
					bw.write(wVectors[i][j].toString());
					bw.write(" ");
				}
			bw.write("\n");
	}
		/*
		ps = new PrintStream(new FileOutputStream(output_file));
		ps.println(wVectors.length+" "+wVectors[0].length);
		for(int i=0;i<wVectors.length;i++){
			for(int j=0;j<wVectors[0].length;j++){
				ps.print(wVectors[i][j]);
				ps.print(" ");
			}
			ps.println();
		}
		ps.close();*/
		bw.close();
	}
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
		
		File ifile = new File(output_test);
		FileWriter ifw = new FileWriter(ifile.getAbsoluteFile());
		BufferedWriter ibw = new BufferedWriter(ifw);
		try {
				ibw.write(iwVectors.length+" "+iwVectors[0].length);
				ibw.write("\n");
				for(int i=0;i<iwVectors.length;i++){
					for(int j=0;j<iwVectors[0].length;j++){
					ibw.write(iwVectors[i][j].toString());
					ibw.write(" ");
				}
			ibw.write("\n");
	}
		
		ibw.close();
	}
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	}
}
