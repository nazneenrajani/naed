
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;


public class BagofWordsLib {
	static int accuracy=0;
	public static void main(String args[]) throws IOException {
		Integer[][] wordVectors;
		Integer[][] devVectors;
		Integer[][] testVectors;
		wordVectors = createVectors("train.txt","label.txt");
		//devVectors = createVectors("dev.txt","label_dev.txt");
		testVectors = createVectors("test.txt","label_test.txt");
		Model _model;
		//int trainingSize = wordVectors
		_model = svmTrain(wordVectors);
		//accuracy = new Integer[data.size()];
		int accu;
		int ftp = 0;
		int ltp = 0;
		int ftn = 0;
		int ltn =0;
		int ffp = 0;
		int lfp = 0;
		int ffn = 0;
		int lfn =0;
		int literal_count=0;
		int fig_count=0;
		for(int j=0;j<testVectors.length;j++){	
			//System.out.println(testVectors[j][0]);
			if(testVectors[j][0] == 0)
				literal_count++;
			else if(testVectors[j][0] == 1)
				fig_count++;
			accu=evaluate(testVectors[j],_model);
			//System.out.println(accu);
			if(accu == testVectors[j][0]){
				if(accu == 0){
					ltp++;
					ftn++;
				}
				else if(accu ==1){
					ltn++;
					ftp++;
				}
			}
			else{
				if(accu == 0){
					lfp++;
					ffn++;
				}
				else if(accu ==1){
					lfn++;
					ffp++;
				}
			}
		}
		double l_accu = ltp*1.0/literal_count;
		double f_accu = ftp*1.0/fig_count;
		double accur = l_accu*0.4+f_accu*0.6;
		System.out.println("Accuracy_Metric:    "+accur);
		double f_accuracy = (accuracy*1.0)/testVectors.length;
		System.out.println("Accuracy:    "+f_accuracy);
		double lprecision = (ltp*1.0)/(ltp+lfp);
		System.out.println("Literal Precision:    "+lprecision);
		double lrecall = (ltp*1.0)/(ltp+lfn);
		System.out.println("Literal Recall:    "+lrecall);
		double lf1 = 2*lprecision*lrecall/(lprecision+lrecall);
		System.out.println("Literal F1:    "+lf1);
		double fprecision = (ftp*1.0)/(ftp+ffp);
		System.out.println("Figurative Precision:    "+fprecision);
		double frecall = (ftp*1.0)/(ffn+ftp);
		System.out.println("Figurative Recall:    "+frecall);
		double ff1 = 2*fprecision*frecall/(fprecision+frecall);
		System.out.println("Figurative F1:    "+ff1);
		//System.out.println("Precision:    "+accuracy);
	}
	
private static Model svmTrain(Integer [][] wordVector) {
	    Problem prob = new Problem();
	    int dataCount = wordVector.length;
	    prob.y = new double[dataCount];
	    prob.l = dataCount;
	    prob.n = wordVector[0].length-1;
	    prob.x = new Feature[dataCount][];     
	    
	    Integer[] features;
	    for (int i = 0; i < dataCount; i++){            
	    	features = wordVector[i];
	        prob.x[i] = new Feature[features.length-1];
	        for (int j = 1; j < features.length; j++){
	            FeatureNode node = new FeatureNode(j, features[j]);
	            prob.x[i][j-1] = node;
	        }           
	        prob.y[i] = features[0];
	    }
	    double [] weights = new double[2];
	    weights[0] = 0.6;
	    weights[1] = 0.4;
	    int [] weightLabels = new int[2];
	    weightLabels[0] = 0;
	    weightLabels[1] = 1;
	    SolverType solver = SolverType.L2R_LR; // -s 0
	    double C = 1;    // cost of constraints violation
	    double eps = 0.0001; // stopping criteria
	    Parameter parameter = new Parameter(solver, C, eps);
	    parameter.setWeights(weights, weightLabels);
	    //svm_model model = svm.svm_train(prob, param);
	    Model model = Linear.train(prob, parameter);
	    return model;
	}

public static int evaluate(Integer[] wordVectors2, Model model) 
{
    Feature[] nodes = new FeatureNode[wordVectors2.length-1];
    for (int i = 1; i < wordVectors2.length; i++)
    {
        FeatureNode node = new FeatureNode(i, wordVectors2[i]);
        nodes[i-1] = node;
    }
    int totalClasses = 3;       
//    int[] labels = new int[totalClasses];
//    svm.svm_get_labels(model,labels);
    double[] prob_estimates = new double[totalClasses];
    double v = Linear.predictProbability(model, nodes,prob_estimates);
    //System.out.println(v+"*******");
    int result;
    if(v >=0.5)
    	result = 1;
    else
    	result =0;
    System.out.println("(Actual:" + wordVectors2[0] + " Prediction:" + result + ")");            
    if(wordVectors2[0]==result)
    	accuracy++;
    return result;
}

public static Integer[][] createVectors(String text_file, String label_file) throws IOException{
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
			    	 System.out.println(token);
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
	return wVectors;
}
}

