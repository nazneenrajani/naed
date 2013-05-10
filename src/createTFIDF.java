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



public class createTFIDF {
	public static void make(String text_file, String label_file,String test_file, String label_test, String output_train, String output_test) throws IOException{
		Map<String,Integer> wordMap = new HashMap<String,Integer>();
		ArrayList<HashMap<String, Integer>> tfidfMap = new ArrayList<HashMap<String,Integer>>();
		ArrayList<HashMap<String, Integer>> tfidfMap_test = new ArrayList<HashMap<String,Integer>>();
		Float[][] wVectors,iwVectors;
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
				}
			ibr.close();
			while ((num = inbr.readLine()) != null) {
	            test_label.add(Integer.parseInt(num));
	        }
			//labelArr = label.toArray(new Integer[label.size()]);
			inbr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		wVectors = new Float[data.size()][2];
		for(int i =0; i<data.size();i++){
			wVectors[i][0] = (float) label.get(i);
				wVectors[i][1]=(float) 0;
		}
		for(int i =0; i<data.size();i++){
			HashMap<String, Integer> tempMap = new HashMap<String,Integer>();
			String[] temp = data.get(i).toLowerCase().split(" ");
			for (int j = 0; j<temp.length; j++) {
				if(tempMap.containsKey(temp[j]))
					tempMap.put(temp[j], tempMap.get(temp[j]) + 1);
				else
					tempMap.put(temp[j], 1);
			}
			tfidfMap.add(tempMap);
		}
		     for(int k =0;k < tfidfMap.size(); k++) {
		    	 HashMap<String, Integer> tempMap = tfidfMap.get(k);
		    	 Set<String> keys = tempMap.keySet();
		    	 int total = 0;
		    	 Float tfidfvalue = (float) 0;
		    	 for (Integer value : tempMap.values()) {
		    	     total = total + value; 
		    	 }
				   for (Iterator<String> i = keys.iterator(); i.hasNext();) 
				   {
				       String key = (String) i.next();
				       if(!wordMap.containsKey(key))
				    	   continue;
				       int val = wordMap.get(key);
				       int numDocs = 0;
				       for(int p =0;p < tfidfMap.size(); p++) {
				    	   HashMap<String, Integer> tMap = tfidfMap.get(p);
				    	   if(tMap.containsKey(key))
				    		   numDocs++;
				       }
				       System.out.println(tempMap.get(key)+ " "+total+ " "+tfidfMap.size()+" "+ numDocs);
				       TFIDF tfidf = new TFIDF(tempMap.get(key), total, tfidfMap.size(), numDocs);
				       tfidfvalue = tfidfvalue + tfidf.getValue();
				   }
				   wVectors[k][1] = tfidfvalue/total;
		     }
		     
		     iwVectors = new Float[data_test.size()][2];
				for(int i =0; i<data_test.size();i++){
					iwVectors[i][0] = (float) test_label.get(i);
						iwVectors[i][1]=(float) 0;
				}
				for(int i =0; i<data_test.size();i++){
					HashMap<String, Integer> tempMap = new HashMap<String,Integer>();
					String[] temp = data_test.get(i).toLowerCase().split(" ");
					for (int j = 0; j<temp.length; j++) {
						if(tempMap.containsKey(temp[j]))
							tempMap.put(temp[j], tempMap.get(temp[j]) + 1);
						else
							tempMap.put(temp[j], 1);
					}
					tfidfMap_test.add(tempMap);
				}
				     for(int k =0;k < tfidfMap_test.size(); k++) {
				    	 HashMap<String, Integer> tempMap = tfidfMap_test.get(k);
				    	 Set<String> keys = tempMap.keySet();
				    	 int total = 0;
				    	 Float tfidfvalue = (float) 0;
				    	 for (Integer value : tempMap.values()) {
				    	     total = total + value; 
				    	 }
						   for (Iterator<String> i = keys.iterator(); i.hasNext();) 
						   {
						       String key = (String) i.next();
						       if(!wordMap.containsKey(key))
						    	   continue;
						       int val = wordMap.get(key);
						       int numDocs = 0;
						       for(int p =0;p < tfidfMap_test.size(); p++) {
						    	   HashMap<String, Integer> tMap = tfidfMap_test.get(p);
						    	   if(tMap.containsKey(key))
						    		   numDocs++;
						       }
						       System.out.println(tempMap.get(key)+ " "+total+ " "+tfidfMap_test.size()+" "+ numDocs);
						       TFIDF tfidf = new TFIDF(tempMap.get(key), total, tfidfMap_test.size(), numDocs);
						       tfidfvalue = tfidfvalue + tfidf.getValue();
						   }
						   iwVectors[k][1] = tfidfvalue/total;				    	 
				     }
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
