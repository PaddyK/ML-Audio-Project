import java.util.Random;

import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;

public class Training {

    public Training() {
    
    }

    public void gridSearch(Instances data) {
        Random      rand;
        Instances   randData;
        Evaluation  eval;
        int         folds;
        String[]    options;
        LibSVM      svm;
        
        eval	= null;
        folds 	= 10;
        rand    = new Random(42);
        options = new String[]{"-S","0","-K","2","-D","3","-G","0.0","-R","0.0","-N","0.5","-M","40","-C","1","-E","0.001","-P","0.1","-seed","1"};

        System.out.println("Attribute\tC-Value\tGamma\tPrecision\tRecall\tF1-Score");
        
        for(int c = -5; c < 16; c++) {
            for(int y = -15; y < 4; y++) {
                randData = new Instances(data);
                randData.randomize(rand);
                
                options[15] = "" + Math.pow(2,c);
                options[17] = "" + Math.pow(2,y);

                svm = new LibSVM();
                try {
                	svm.setOptions(options);
                } catch (Exception e) {
                	e.printStackTrace();
                	return;
                }
                
                try {
                	eval = new Evaluation(randData);
                	eval.crossValidateModel(svm, randData, folds, new Random(1));
                }
                catch(Exception e) {
                	e.printStackTrace();
                	return;
                }
                
                System.out.println(randData.classAttribute().value(0) + "\t"
                		+ c + "\t"
                		+ y + "\t" 
                		+ eval.precision(0) + "\t" 
                		+ eval.recall(0) + "\t" 
                		+ ((eval.precision(0) * eval.recall(0)) / (eval.precision(0) + eval.recall(0))));

                System.out.println(randData.classAttribute().value(1) + "\t" 
                		+ c +"\t"
                		+ y +"\t"
                		+ eval.precision(1) + "\t" 
                		+ eval.recall(1) + "\t" 
                		+ ((eval.precision(1) * eval.recall(1)) / (eval.precision(1) + eval.recall(1))));
             }
        } 
    }

}

