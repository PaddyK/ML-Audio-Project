import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
/**
 * Does all the weka related stuff
 * @author Patrick
 *
 */
public class Training {

    public Training() {
    
    }
    public void gridSearch(Instances data) {
        gridSearch(data, "results.txt");
    }


    /**
     * Perform a grid search on svm with linear kernel on paramters c \in {2^-5,..,2^15} and
     * \gamm \in {2^-15,..,2^3}. This range of values is proposed by the LibSVM user guide.
     * Unfortunately LibSVM outputs a lot of information itself. It is not possible to change this
     * through the weka wrapper, the LibSVM itself would need to be changed.
     * @param data	weka.core.Instances for cross validation
     */
    public void gridSearchLinear(Instances data, String resultfile) {
        Random      rand;
        Instances   randData;
        Evaluation  eval;
        int         folds;
        String[]    options;
        LibSVM      svm;
        String		results;
        String		inc;
        Data		writer;
        
        writer	= new Data();
        eval	= null;
        folds 	= 10;
        rand    = new Random(42);
        // Default options as from weka gui. Option -C and -E are the for c-value and gamma
        options = new String[]{"-S","0","-K","0","-D","3","-G","0.0","-R","0.0","-N","0.5","-M"
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
                inc = randData.classAttribute().value(0) + "\t"
                		+ c + "\t"
                		+ y + "\t" 
                		+ eval.precision(0) + "\t" 
                		+ eval.recall(0) + "\t"
                        +eval.truePositiveRate(0) + "\t" 
                		+ eval.fMeasure(0) + "\t"
                        + eval.correct() + "\t"
                        + eval.incorrect() + "\t"
                        + eval.pctCorrect() 
                		+ System.getProperty("line.separator");

                if(y==-15)
                    inc +="Attribute\tC-Value\tGamma\tPrecision\tRecall\tTPR\tF1-Score\tcorrect\tincorrect\tCoverage" + System.getProperty("line.separator");
                
                inc += randData.classAttribute().value(1) + "\t" 
                		+ c +"\t"
                		+ y +"\t"
                		+ eval.precision(1) + "\t" 
                		+ eval.recall(1) + "\t" 
                		+ eval.fMeasure(1) + "\t" 
                        + eval.truePositiveRate(1) + "\t"
                		+ System.getProperty("line.separator");
                writer.appendToFile(inc, resultfile);
                results += inc;
                results += eval.toSummaryString(c + "-" + y, false) + System.getProperty("line.separator");
                try{
                    results += eval.toMatrixString(c + "-" + y) + System.getProperty("line.separator");
                }
                catch(Exception e) {
                e.printStackTrace();
                }
                results += "=======================================================" + System.getProperty("line.separator") + System.getProperty("line.separator");
            }
        }
        System.out.println(results);
        writer.appendToFile(results, "results2.txt");
    }


    /**
     * Perform a grid search on svm with rbf kernel on paramters c \in {2^-5,..,2^15} and
     * \gamm \in {2^-15,..,2^3}. This range of values is proposed by the LibSVM user guide.
     * Unfortunately LibSVM outputs a lot of information itself. It is not possible to change this
     * through the weka wrapper, the LibSVM itself would need to be changed.
     * @param data	weka.core.Instances for cross validation
     */
    public void gridSearch(Instances data, String resultfile) {
        Random      rand;
        Instances   randData;
        Evaluation  eval;
        int         folds;
        String[]    options;
        LibSVM      svm;
        String		results;
        String		inc;
        Data		writer;
        
        writer	= new Data();
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
                inc = randData.classAttribute().value(0) + "\t"
                		+ c + "\t"
                		+ y + "\t" 
                		+ eval.precision(0) + "\t" 
                		+ eval.recall(0) + "\t" 
                		+ eval.fMeasure(0) + "\t"
                        + eval.correct() + "\t"
                        + eval.incorrect() + "\t"
                        + eval.pctCorrect() 
                		+ System.getProperty("line.separator");

                if(y==-15)
                    inc +="Attribute\tC-Value\tGamma\tPrecision\tRecall\tF1-Score\tcorrect\tincorrect\tCoverage" + System.getProperty("line.separator");
                
                inc += randData.classAttribute().value(1) + "\t" 
                		+ c +"\t"
                		+ y +"\t"
                		+ eval.precision(1) + "\t" 
                		+ eval.recall(1) + "\t" 
                		+ eval.fMeasure(1) + "\t" 
                		+ System.getProperty("line.separator");
                writer.appendToFile(inc, resultfile);
                results += inc;
                results += eval.toSummaryString(c + "-" + y, false) + System.getProperty("line.separator");
                try{
                    results += eval.toMatrixString(c + "-" + y) + System.getProperty("line.separator");
                }
                catch(Exception e) {
                e.printStackTrace();
                }
            }
        }
        System.out.println(results);
        writer.appendToFile(results, "results2.txt");
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
    
    private double getMean(double[] values) {
        double mean = 0;
        for(double d : values)
            mean += d;
        mean = mean / values.length;
        return mean;
    }

    private double getStandardDeviation(double[] values) {
        return getStandardDeviation(values, getMean(values));
    }

    private double getStandardDeviation(double[] values, double mean) {
        double sd = 0;
        for(double d : values)
            sd += (d - mean) * (d - mean);
        sd = sd / (values.length - 1);
        return sd;
    }
/*
    public void muSigmaStandardization(Instances data) {
        double mu;
        double sd;
        
        for(int i = 0; i < data.numInstances() - 1; i++) {
            mu = getMean(data.attributeToDoubleArray(i));
            sd = getStandardDeviation(data.attributeToDoubleArray(i), mu);
            
            for(int j = 0; j < data.numInstances(); j++) {
                data.get(j).setValue((data.get(j).value(i) - mu) / sd, i);
            }
        }
    }
*/
    public void infoGainSearch(Instances data) {
        double[] gainValues = new double[]{0.04,0.051,0.052,0.054,0.06,0.09,0.1,0.17,0.18,0.3,0.7};
        AttributeSelection      filter;
        InfoGainAttributeEval   eval;
        Ranker                  ranker;
        Instances               topn = null;
   
        for(double d : gainValues) {
            filter  = new AttributeSelection();
            eval    = new InfoGainAttributeEval();
            ranker  = new Ranker();

            eval.setBinarizeNumericAttributes(false);
            eval.setMissingMerge(false);

            ranker.setGenerateRanking(true);
            ranker.setThreshold(d);

            filter.setEvaluator(eval);
            filter.setSearch(ranker);

            try {
                filter.setInputFormat(data);
                topn = Filter.useFilter(data, filter);
            }
            catch(OutOfMemoryError e) { e.printStackTrace(); }
            catch(Exception e) { e.printStackTrace(); }
           String ival = "" + d;
            ival = ival.replace(".","_"); 
            gridSearch(topn, "results-gain-" + ival + ".txt");
        }
        
    }            



    public void infoGainSearchLinear(Instances data) {
        //double[] gainValues = new double[]{0.04,0.051,0.052,0.054,0.06,0.09,0.1,0.17,0.18,0.3,0.7};
        double[] gainValues = new double[]{0.3,0.7};
        AttributeSelection      filter;
        InfoGainAttributeEval   eval;
        Ranker                  ranker;
        Instances               topn = null;
   
        for(double d : gainValues) {
            filter  = new AttributeSelection();
            eval    = new InfoGainAttributeEval();
            ranker  = new Ranker();

            eval.setBinarizeNumericAttributes(false);
            eval.setMissingMerge(false);

            ranker.setGenerateRanking(true);
            ranker.setThreshold(d);

            filter.setEvaluator(eval);
            filter.setSearch(ranker);

            try {
                filter.setInputFormat(data);
                topn = Filter.useFilter(data, filter);
            }
            catch(OutOfMemoryError e) { e.printStackTrace(); }
            catch(Exception e) { e.printStackTrace(); }
           String ival = "" + d;
            ival = ival.replace(".","_"); 
            gridSearch(topn, "results-gain-linear" + ival + ".txt");
        }
        
    }            
    
    public void preprocess(Instances data) {
        data.deleteAttributeAt(0);  // Delete Time Frame
    }                       
}

