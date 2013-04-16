import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class BagofWords {
	static int accuracy=0;
	public static void main(String args[]) throws IOException {
		Integer[][] wordVectors;
		Integer[][] devVectors;
		wordVectors = createVectors("train.txt","label.txt");
		devVectors = createVectors("dev.txt","label_dev.txt");
		svm_model _model;
		_model = svmTrain(wordVectors);
		//accuracy = new Integer[data.size()];
		Integer accu;
		int acc = 0;
		for(int j=0;j<devVectors.length;j++){	
			accu=evaluate(devVectors[j],_model);
			System.out.println(acc);
			if(accu.equals(devVectors[j][0]))
				acc++;
			System.out.println(acc);
		}	
		double precision = accuracy/wordVectors.length;
		System.out.println("Precision:    "+precision);
		System.out.println("Precision:    "+accuracy);
	}
	
private static svm_model svmTrain(Integer [][] wordVector) {
	    svm_problem prob = new svm_problem();
	    int dataCount = wordVector.length;
	    prob.y = new double[dataCount];
	    prob.l = dataCount;
	    prob.x = new svm_node[dataCount][];     

	    Integer[] features;
	    for (int i = 0; i < dataCount; i++){            
	    	features = wordVector[i];
	        prob.x[i] = new svm_node[features.length-1];
	        for (int j = 1; j < features.length; j++){
	            svm_node node = new svm_node();
	            node.index = j;
	            node.value = features[j];
	            prob.x[i][j-1] = node;
	        }           
	        prob.y[i] = features[0];
	    }               

	    svm_parameter param = new svm_parameter();
	    param.probability = 1;
	    param.gamma = 0.5;
	    param.nu = 0.5;
	    param.C = 1;
	    param.svm_type = svm_parameter.C_SVC;
	    param.kernel_type = svm_parameter.LINEAR;       
	    param.cache_size = 20000;
	    param.eps = 0.001;      

	    svm_model model = svm.svm_train(prob, param);

	    return model;
	}

public static int evaluate(Integer[] wordVectors2, svm_model model) 
{
    svm_node[] nodes = new svm_node[wordVectors2.length-1];
    for (int i = 1; i < wordVectors2.length; i++)
    {
        svm_node node = new svm_node();
        node.index = i;
        node.value = wordVectors2[i];

        nodes[i-1] = node;
    }

    int totalClasses = 3;       
    int[] labels = new int[totalClasses];
    svm.svm_get_labels(model,labels);

    double[] prob_estimates = new double[totalClasses];
    int v = (int) svm.svm_predict_probability(model, nodes, prob_estimates);

    for (int i = 0; i < totalClasses; i++){
        System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");
    }
    System.out.println("(Actual:" + wordVectors2[0] + " Prediction:" + v + ")");            
    if(wordVectors2[0]==v)
    	accuracy++;
    return v;
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
		//int i =0;
		while((line = br.readLine()) != null) {
			data.add(line);
			StringTokenizer st = new StringTokenizer(line);
		     while (st.hasMoreTokens()) {
		    	 String token = st.nextToken();
		         if(!wordMap.containsKey(token)){
		        	 wordMap.put(token,count);
		        	 count +=1;
		         }
		     }
		//i++;
		}
		br.close();
		while ((num = nbr.readLine()) != null) {
            label.add(Integer.parseInt(num));
        }
		//labelArr = label.toArray(new Integer[label.size()]);
		nbr.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(wordMap.size());
	System.out.println(data.size());
	System.out.println(label.size());
	wVectors = new Integer[data.size()][wordMap.size()+1];
	for(int i =0; i<data.size();i++){
		wVectors[i][0] = label.get(i);
		for(int j =1;j<=wordMap.size();j++){
			wVectors[i][j]=0;
		}
	}
	for(int j=0;j<data.size();j++){
		StringTokenizer st = new StringTokenizer(data.get(j));
	     while (st.hasMoreTokens()) {
	    	 String token = st.nextToken();
	    	 int val = wordMap.get(token);
	    	 wVectors[j][val]++;
	     }
	}
	return wVectors;
}
}
