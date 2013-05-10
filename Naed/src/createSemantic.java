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


public class createSemantic {
	public static void make(String text_file, String output_file) throws IOException{
		Map<String,Integer> wordMap = new LinkedHashMap<String,Integer>();
		//Map<String,Integer> phraseMap = new HashMap<String,Integer>();
		Integer[][] wVectors;
		BufferedReader br,pbr,ubr;
		int count = 0;
		//Integer[] accuracy;
		ArrayList<String> data = new ArrayList<String>();
		List<String> phrase = new ArrayList<String>();
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
		wVectors = new Integer[wordMap.size()][2];
			for(int i =0;i<wordMap.size();i++){
				for(int j = 0; j< 2; j++){
				wVectors[i][j]=0;
			}
		}
		for(int j=0;j<data.size();j++){
			String[] temp = data.get(j).split("\\$\\$");
			System.out.println(temp.length);
			System.out.println(temp[0]);
			StringTokenizer st1 = new StringTokenizer(temp[0]);
			StringTokenizer st2 = new StringTokenizer(temp[1]);
			String reg = "(\\d+)?[\\.;,?!()'\":-@_$%`&]+";
			String apos = "(')([a-z])";
		     while (st1.hasMoreTokens()) {
		    	 String token = st1.nextToken().toLowerCase();
		    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
		    		 continue;
		    	 int val = wordMap.get(token);
		    	 wVectors[val][0]++;
		     }
		     while (st2.hasMoreTokens()) {
		    	 String token = st2.nextToken().toLowerCase();
		    	 if (token.matches(reg) || token.matches(apos) || token.matches("\\d+"))
		    		 continue;
		    	 int val = wordMap.get(token);
		    	 wVectors[val][1]++;
		     }
		}
		PrintStream ps;
		File file = new File(output_file);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		try {
				//bw.write(wVectors.length+" "+wVectors[0].length);
				//bw.write("\n");
			Set<String> keys = wordMap.keySet();
			       
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
	}
}
