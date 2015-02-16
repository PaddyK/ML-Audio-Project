import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;

/**
 * Does all the weka related stuff
 * @author Patrick
 *
 */
public class Training {

    public Training() {
    
    }
    /**
     * Perform a grid search on svm with rbf kernel on paramters c \in {2^-5,..,2^15} and
     * \gamm \in {2^-15,..,2^3}. This range of values is proposed by the LibSVM user guide.
     * Unfortunately LibSVM outputs a lot of information itself. It is not possible to change this
     * through the weka wrapper, the LibSVM itself would need to be changed.
     * @param data	weka.core.Instances for cross validation
     */
    public void gridSearch(Instances data) {
        Random      rand;
        Instances   randData;
        Evaluation  eval;
        int         folds;
        String[]    options;
        LibSVM      svm;
        String		results;
        
        eval	= null;
        folds 	= 10;
        rand    = new Random(42);
        // Default options as from weka gui. Option -C and -E are the for c-value and gamma
        options = new String[]{"-S","0","-K","2","-D","3","-G","0.0","-R","0.0","-N","0.5","-M"
        		,"40","-C","1","-E","0.001","-P","0.1","-seed","1"};
        // Header for a summary of results
        results = "Attribute\tC-Value\tGamma\tPrecision\tRecall\tF1-Score" + System.getProperty("line.separator");
        
        // Perform the grid search
        for(int c = -5; c < 16; c++) {
            for(int y = -15; y < 4; y++) {
            	// Create a randomized copy of the data
                randData = new Instances(data);
                randData.randomize(rand);
                
                // Set the values for c-value and gamma
                options[15] = "" + Math.pow(2,c);
                options[17] = "" + Math.pow(2,y);
                
                // Create a new LibSVM classifier
                // For more information on creating a classifier see http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
                // For more information (albeit not much) see http://weka.wikispaces.com/LibSVM
                svm = new LibSVM();
                try {
                	svm.setOptions(options);
                } catch (Exception e) {
                	e.printStackTrace();
                	return;
                }
                
                try {
                	// execute a stratified cross validation. For more information see
                	// http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
                	eval = new Evaluation(randData);
                	eval.crossValidateModel(svm, randData, folds, new Random(1));
                }
                catch(Exception e) {
                	e.printStackTrace();
                	return;
                }
                
                // Print some statistics to compare different settings
                results += randData.classAttribute().value(0) + "\t"
                		+ c + "\t"
                		+ y + "\t" 
                		+ eval.precision(0) + "\t" 
                		+ eval.recall(0) + "\t" 
                		+ ((eval.precision(0) * eval.recall(0)) / (eval.precision(0) + eval.recall(0)))
                		+ System.getProperty("line.separator");

                results += randData.classAttribute().value(1) + "\t" 
                		+ c +"\t"
                		+ y +"\t"
                		+ eval.precision(1) + "\t" 
                		+ eval.recall(1) + "\t" 
                		+ ((eval.precision(1) * eval.recall(1)) / (eval.precision(1) + eval.recall(1)))
                		+ System.getProperty("line.separator");
             }
        }
        System.out.println(results);
    }
    
    /**
     * Train a LibSVM model and export it to a .model file
     * @param data			weka.core.Instances to train model
     * @param destination	destination of model
     * @param options		Array of Strings containing options for svm model
     */
    public void trainAndSerializeModel(Instances data, String destination, String[] options) {
        LibSVM svm = new LibSVM();        
        data.randomize(new Random(42));

        try {
        	// Train the model
        	svm.setOptions(options);
			svm.buildClassifier(data);
		} catch (Exception e) {
			System.err.println("Error while training svm model for serialization");
			e.printStackTrace();
			return;
		}
        ObjectOutputStream oos;
		try {
			// Create the .model file
			// For more information on this topic see http://weka.wikispaces.com/Serialization
			oos = new ObjectOutputStream(new FileOutputStream(destination));		
	        oos.writeObject(svm);
	        oos.flush();
	        oos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error while serializing model - file not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error while serializing model");
			e.printStackTrace();
		}
    }
}

