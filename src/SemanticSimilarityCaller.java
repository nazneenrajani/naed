import java.io.IOException;


public class SemanticSimilarityCaller {
	public static void main(String args[]) throws IOException{
		//createCoocurrence.make("data/unique-phrases/1-train.txt", "data/unique-phrases/1-test.txt","data/sem1.txt","data/1-train-semantic.txt","data/1-test-semantic.txt");
		createVector.make("data/clean_train.txt", "data/label.txt","data/clean_test.txt","data/label_test.txt","data/train_Bow.txt","data/test_Bow.txt");
		//SkipBigram.make("data/unique-phrases/1-train.txt", "data/unique-phrases/1-test.txt","data/1-train-bi.txt","data/1-test-bi.txt");
		//createTFIDF.make("data/all_train.txt", "data/allabels_train.txt","data/all_test.txt","data/allabels_test.txt","data/tfidf_train.txt","data/tfidf_test.txt");

	}
}
