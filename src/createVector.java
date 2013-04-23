import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class createVector {
	public static void make(String text_file, String label_file, String output_file) throws IOException{
		Map<String,Integer> wordMap = new HashMap<String,Integer>();
		Integer[][] wVectors;
		BufferedReader br,nbr;
		int count = 1;
		//Integer[] accuracy;
		List<Integer> label = new ArrayList<Integer>();
		ArrayList<String> data = new ArrayList<String>();
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
		PrintStream ps;
		try {
		ps = new PrintStream(new FileOutputStream(output_file));
		ps.println(wVectors.length+" "+wVectors[0].length);
		for(int i=0;i<wVectors.length;i++){
			for(int j=0;j<wVectors[0].length;j++){
				ps.print(wVectors[i][j]);
				ps.print(" ");
			}
			ps.println();
		}
		ps.close();
	}
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	}
}
