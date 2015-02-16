import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MlAudioProject {

    public static void main(String[] argv) {
        String ps       = System.getProperty("file.separator");
        String nl       = System.getProperty("line.separator");
        String target   = ".." + ps + "data" + ps + "arff" + "clapping.arff";
        String clap     = ".." + ps + "data" + ps + "arff" +"clap";
        String noclap   = ".." + ps + "data" + ps + "arff" +"noclap";
        Training train  = new Training();
        Data data       = new Data(); 
        String input;
        int choice;
        BufferedReader reader;
        
        reader = new BufferedReader(new InputStreamReader(System.in));
        
        do {
        	try {
	            System.out.println("Command\tDescription");
	            System.out.println("1\t\tSet target (arff file containing all sample values) path");
	            System.out.println("2\t\tSet path to directory containing clap arffs");
	            System.out.println("3\t\tSet path to directoyr containing noclap arffs");
	            System.out.println("4\t\tCopy content of clap and noclap arffs into one file (target)." + nl
	                 + " Paths to directories containing clap and noclap arffs must specified as well as target file." + nl
	                 + " current:" + nl
	                 + "\tclap: " + clap + nl
	                 + "\tnoclap: " + noclap + nl
	                 + "\ttarget: " + target);
	            System.out.println("5\t\tPerform GridSearch for LibSVM with radial kernel" + nl
	                 + " Path to target (arff file containing all values) must be specified. " + nl
	                 + " current: " + target);
	            System.out.println("q\tExit");
	            
	            System.out.print("Your choice: ");
	            input = reader.readLine();
	
	            if(input.equals("q")) {
	                System.out.println("Byes");
	                break;
	            } 
	            else {
	               try{
	                   choice = Integer.parseInt(input);
	                   switch(choice) {
	                       case 1: System.out.println("New target: ");
	                               target = reader.readLine();
	                               break;
	                       case 2: System.out.println("New clap dir: ");
	                               clap = reader.readLine();
	                               break;
	                       case 3: System.out.println("New noclap dir: ");
	                               noclap = reader.readLine();
	                               break;
	                       case 4: data.consolidateSamples(clap,noclap,target);
	                               break;
	                       case 5: train.gridSearch(data.getInstances(target));
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
