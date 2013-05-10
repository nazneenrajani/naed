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


public class SkipBigram {
	public static void make(String text_file, String test_file,  String output_train, String output_test) throws IOException{
		Map<String,Integer> wordMap = new HashMap<String,Integer>();
		Integer[][] wVectors,iwVectors;
		BufferedReader br,nbr,ibr,inbr;
		int count = 0;
		//Integer[] accuracy;
		List<Integer> label = new ArrayList<Integer>();
		ArrayList<String> data = new ArrayList<String>();
		List<Integer> test_label = new ArrayList<Integer>();
		ArrayList<String> data_test = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(text_file));
			String line;
			String num;
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String punct = "[,.:'\"]+";
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
				/*
				smallLine.replaceAll(reg,"");
				smallLine.replaceAll(aps,"$1");
				smallLine.replaceAll("([a-z]+)[?:!.,;]*", "$1");*/
				smallLine.replaceAll("\\W", "");
				data.add(smallLine);
				String token1 = "";
				String token2 = "";
				String bigram;
				StringTokenizer st = new StringTokenizer(smallLine);
				//smallLine.replaceAll(reg,""); smallLine.replaceAll(apos,""); smallLine.replaceAll("\\d+","") ;smallLine.replaceAll(".",""); smallLine.replaceAll(",",""); smallLine.replaceAll("!","");
				//smallLine.replaceAll("\\d+","");
				//smallLine.replaceAll("\\(" ,"");
				//smallLine.replaceAll("\\)","");
				if (st.hasMoreTokens())
				{token1 = st.nextToken();
				}
			while(st.hasMoreTokens()) {
				token2 = st.nextToken();				
					bigram = token1 + " " + token2;
					if(!wordMap.containsKey(bigram)){
						wordMap.put(bigram,count);
						count = count+1;
					}
					token1 = token2; // step forward
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
		for (String name: wordMap.keySet()){

            String key =name.toString();
            String value = wordMap.get(name).toString();  
            System.out.println(key + " " + value);  


}
		try {
			ibr = new BufferedReader(new FileReader(test_file));
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
				smallLine.replaceAll("\\W", "");
				/*smallLine.replaceAll(reg,"");
				smallLine.replaceAll(aps,"$1");
				smallLine.replaceAll("([a-z]+)[?:!.,;]*", "$1");
				*/
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
			for(int j =0;j<=wordMap.size();j++){
				wVectors[i][j]=0;
			}
		}
		for(int j=0;j<data.size();j++){
			String line = data.get(j);
			StringTokenizer st = new StringTokenizer(line);
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String token1 = "";
			String token2 = "";
			String bigram;
			//line.replaceAll("([a-z]+)[?:!.,;]*", "$1");
			//line.replaceAll(reg,""); line.replaceAll(apos,""); line.replaceAll("\\d+","") ;line.replaceAll(".",""); line.replaceAll(",",""); line.replaceAll("!","");
			//line.replaceAll("\\d+","");
			//line.replaceAll("\\(" ,"");
			//line.replaceAll("\\)","");
			if (st.hasMoreTokens())
			{token1 = st.nextToken();
			}
		while(st.hasMoreTokens()) {			
			token2 = st.nextToken();				
				bigram = token1 + " " + token2;
				System.out.println(bigram);
				token1 = token2;
				if(!wordMap.containsKey(bigram))
					continue;
				int val = wordMap.get(bigram);
				wVectors[j][val]++;
				}   
		}
		iwVectors = new Integer[data_test.size()][wordMap.size()+1];
		for(int i =0; i<data_test.size();i++){
			for(int j =0;j<=wordMap.size();j++){
				iwVectors[i][j]=0;
			}
		}
		for(int j=0;j<data_test.size();j++){
			String line = data_test.get(j);
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
			String token1 = "";
			String token2 = "";
			//line.replaceAll("([a-z]+)[?:!.,;]*", "$1");
			//line.replaceAll(reg,""); line.replaceAll(apos,""); line.replaceAll("\\d+","") ;line.replaceAll(".",""); line.replaceAll(",",""); line.replaceAll("!","");
			//line.replaceAll("\\d+","");
			//line.replaceAll("\\(" ,"");
			//line.replaceAll("\\)","");
			StringTokenizer st = new StringTokenizer(line);
			String bigram;
			if (st.hasMoreTokens())
			{token1 = st.nextToken();
			}
		while(st.hasMoreTokens()) {
			//token1 = st.nextToken();
			token2 = st.nextToken();				
				bigram = token1 + " " + token2;
				System.out.println(bigram);
				token1 = token2;
				if(!wordMap.containsKey(bigram))
					continue;
				int val = wordMap.get(bigram);
				wVectors[j][val]++;
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
