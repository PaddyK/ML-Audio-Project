import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Provides a simple interface to interact with data and weka components
 * @author Patrick
 *
 */
public class MlAudioProject {

    public static void main(String[] argv) {
        String ps       = System.getProperty("file.separator");
        String nl       = System.getProperty("line.separator");
        String target   = ".." + ps + "data" + ps + "arff" + ps + "clapping.arff";
        String clap     = ".." + ps + "data" + ps + "arff" + ps + "clap";
        String noclap   = ".." + ps + "data" + ps + "arff" + ps + "noclap";
        
        for(int i = 0; i < argv.length; i+=2) {
        	if(argv[i].equalsIgnoreCase("-target"))
        		target = argv[i + 1];
        	else if(argv[i].equalsIgnoreCase("-clap"))
        		clap = argv[i + 1];
        	else if(argv[i].equalsIgnoreCase("-noclap"))
        		noclap = argv[i + 1];
        }
        
        Training train  = new Training();
        Data data       = new Data(); 
        String input;
        int choice;
        BufferedReader reader;
        
        reader = new BufferedReader(new InputStreamReader(System.in));
        
        do {
        	try {
	            System.out.println("Cmd\tDescription");
	            System.out.println("1\tSet target (arff file containing all sample values) path");
	            System.out.println("2\tSet path to directory containing clap arffs");
	            System.out.println("3\tSet path to directoyr containing noclap arffs");
	            System.out.println("4\tCopy content of clap and noclap arffs into one file (target)." + nl
	                 + "\tPaths to directories containing clap and noclap arffs must specified as well " + nl
                     + "\tas target file." + nl
	                 + "\t current:" + nl
	                 + "\t\tclap: " + clap + nl
	                 + "\t\tnoclap: " + noclap + nl
	                 + "\t\ttarget: " + target);
	            System.out.println("5\tPerform GridSearch for LibSVM with radial kernel" + nl
	                 + "\t Path to target (arff file containing all values) must be specified. " + nl
	                 + "\t current: " + target);
	            System.out.println("6\tTrain model with whole data and serialize it");
	            System.out.println("7\tGrid-Search with multiple info gain");
                System.out.println("8\tGrid-Search with linear kernel");
                System.out.println("9\tGrid-Search with linear kernel and info gain");
                System.out.println("q\tExit");
	            
	            System.out.println("Your choice: ");
	            input = reader.readLine();
	
	            if(input.equals("q")) {
	                System.out.println("Byes");
	                break;
	            } 
	            else {
	               try{
	                   choice = Integer.parseInt(input);
	                   switch(choice) {
	                       case 1: 	System.out.println("New target: ");
	                               	target = reader.readLine();
	                               	break;
	                       case 2: 	System.out.println("New clap dir: ");
	                               	clap = reader.readLine();
	                               	break;
	                       case 3: 	System.out.println("New noclap dir: ");
	                               	noclap = reader.readLine();
	                               	break;
	                       case 4: 	data.consolidateSamples(clap,noclap,target);
	                               	break;
	                       case 5: 	train.gridSearch(data.getInstances(target));
	                               	break;
	                       case 6: 	System.out.println("C-Value: ");
	                       		    String c = "" + Math.pow(2, Double.parseDouble(reader.readLine()));
	                       		    System.out.println("Gamma Value: ");
	                       		    String y = "" + Math.pow(2, Double.parseDouble(reader.readLine()));
	                       		    String[] options = new String[]{"-S","0","-K","2","-D","3","-G","0.0"
	                       		    		,"-R","0.0","-N","0.5","-M","40","-C",c,"-E",y
	                       		    		,"-P","0.1","-seed","1"};
	                       		    train.trainAndSerializeModel(data.getInstances(target)
	                       		    		,"libsvm.model"
	                       		    		,options);
	                       		    break;
                            case 7: train.infoGainSearch(data.getInstances(target));
                                    break;
	                        case 8: train.gridSearchLinear(data.getInstances(target), "results_linear.txt");
                                    break;
                            case 9: train.infoGainSearchLinear(data.getInstances(target));
                                    break;
                       }
	               }
	               catch(NumberFormatException e) {
	                   e.printStackTrace();
	               }
	            }
        	}
        	catch(IOException e) {
        		e.printStackTrace();
        		return;
        	}
        }while(true);
    }
}
