
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;


public class ClassifySVM {
	static int accuracy=0;
	public static void main(String args[]) throws IOException {
		Double[][] wordVectors;
		Integer[][] devVectors;
		Double[][] testVectors;
		/*
		 * Call createVector for bag of words Model along with readVectors.
		 * readVectors reads a 2d array from file as integers.
		 */
		//createTFIDF.make("data/unique-phrases/7-train.txt","data/label/7-label-train.txt","data/unique-phrases/7-test.txt","data/label/7-label-test.txt","data/7-train-tfidf.txt","data/7-test-tfidf.txt");
		//createVector.make("data/unique-phrases/7-train.txt","data/label/7-label-train.txt","data/unique-phrases/7-test.txt","data/label/7-label-test.txt","data/7-train-bow.txt","data/7-test-bow.txt");
		wordVectors = readvectors("data/bow_train.txt");
		testVectors = readvectors("data/bow_test.txt");
		//System.out.println(wordVectors[0].length);
		//System.out.println(wordVectors);
		//createTFIDF.make("data/unique-phrases/1-train.txt","data/label/1-label-train.txt","data/1-train.txt");
		//createTFIDF.make("data/unique-phrases/1-test.txt","data/label/1-label-test.txt","data/1-test.txt");
		//createVector.make("data/dev.txt","data/label_dev.txt","data/dev_vector.txt");
		//testVectors = readvectors("data/test_vector.txt");
		//createVector.make("data/test.txt","data/label_test.txt","data/test_vector.txt");
		
		Model _model;
		double[] w1 = new double[]{0.35,0.65};
		double[] w2 = new double[]{0.49,0.51};
		double[] w3 = new double[]{0.75,0.25};
		double[] w4 = new double[]{0.68,0.32};
		double[] w5 = new double[]{0.49,0.51};
		double[] w6 = new double[]{0.3,0.7};
		double[] w7 = new double[]{0.47,0.53};
		double[] w8 = new double[]{0.25,0.75};
		double[] w9 = new double[]{0.46,0.54};
		double[] w10 = new double[]{0.75,0.25};
		//int trainingSize = wordVectors
		_model = svmTrain(wordVectors,w2);
		double[] weight_features = _model.getFeatureWeights();
		for(int j =0;j < weight_features.length; j++)
			System.out.println(weight_features[j]);
		System.out.println("*************************************");
		System.out.println(_model.toString());
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
			//System.out.println(testVectors[j][3]);
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
	
private static Model svmTrain(Double[][] wordVectors, double[] classweight) {
	    Problem prob = new Problem();
	    int dataCount = wordVectors.length;
	    prob.y = new double[dataCount];
	    prob.l = dataCount;
	    prob.n = wordVectors[0].length-1;
	    prob.x = new Feature[dataCount][];     
	    
	    Double[] features;
	    for (int i = 0; i < dataCount; i++){            
	    	features = wordVectors[i];
	        prob.x[i] = new Feature[features.length-1];
	        for (int j = 1; j < features.length; j++){
	        	//System.out.println(j + "feature");
	        	//System.out.println(features[j]);
	        	FeatureNode node = new FeatureNode(j, features[j]);
	            prob.x[i][j-1] = node;
	        }           
	        prob.y[i] = features[0].intValue();
	    }
	    
	    double[] weights = classweight;
	    int [] weightLabels = new int[2];
	    weightLabels[0] = 0;
	    weightLabels[1] = 1;
	    SolverType solver = SolverType.L2R_LR; // -s 0
	    double C = 0.59;
	    		//0.59;    // cost of constraints violation
	    double eps = 0.000001; // stopping criteria
	    Parameter parameter = new Parameter(solver, C, eps);
	    parameter.setWeights(weights, weightLabels);
	    //svm_model model = svm.svm_train(prob, param);
	    Model model = Linear.train(prob, parameter);
	    return model;
	}

public static int evaluate(Double[] testVectors, Model model) 
{
    Feature[] nodes = new FeatureNode[testVectors.length-1];
    for (int i = 1; i < testVectors.length; i++)
    {	//System.out.println(testVectors[i]);
        FeatureNode node = new FeatureNode(i, testVectors[i]);
        nodes[i-1] = node;
    }
    int totalClasses = 2;       
//    int[] labels = new int[totalClasses];
//    svm.svm_get_labels(model,labels);
    double[] prob_estimates = new double[totalClasses];
    double v = Linear.predictProbability(model, nodes,prob_estimates);
    double val = Linear.predictValues(model, nodes,prob_estimates);
    System.out.println(val + " This is val");
    System.out.println(v+"*******");
    int result;
    if(v >=0.5)
    	result = 1;
    else
    	result =0;
    System.out.println("(Actual:" + testVectors[0] + " Prediction:" + result + ")");            
    if(testVectors[0]==result)
    	accuracy++;
    return result;
}

public static Double[][] readvectors(String filename){
	Double[][] w_vector = null;
	try{
		 String[] temp;
		  Scanner file=new Scanner (new File(filename));
		  String[] token = file.nextLine().split(" ");
		  System.out.println(filename);
		  w_vector = new Double[Integer.parseInt(token[0])][Integer.parseInt(token[1])];
		  //System.out.println(Integer.parseInt(token[0]));
		  //System.out.println(Integer.parseInt(token[1]));
		   int i =0;
		       while(file.hasNextLine()){
		    	  // System.out.println(i);
		 		       String line= file.nextLine();
		 		        temp = line.split(" ");
	                for (int j = 0; j<temp.length; j++) {
	                	//System.out.println(Double.parseDouble(temp[j]));
	                	//System.out.println(temp[j]);
	                    w_vector[i][j] = Double.parseDouble(temp[j]);
	                }
	                //System.out.println(w_vector[i][0]);	
	                i=i+1;	                             
	            }
		}
		catch (IOException e){
		    System.out.println("IOException");
		}
	return w_vector;
}

}

