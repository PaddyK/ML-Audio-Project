import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.util.LinkedList;

import weka.core.Instances;

/**
 * Does all the file related stuff like reading or writing
 * @author Patrick
 *
 */
public class Data {

    public Data () {

    }
    
    /**
     * Gets all paths to the arff files created with opensmile. Only the paths as File object are
     * returned and not the weka instances!
     * @param clap	path to the directory containing the clap samples
     * @param noclap	path to the directory containing the noclap samples
     * @return LinkedList of File objects representing the clap and noclap samples
     */
    public LinkedList<File> retrieveArffFiles(String clap, String noclap) {
        File fclap             = new File(clap);
        File fnoclap           = new File(noclap);
        LinkedList<File> ret   = new LinkedList<File>();
        
        // Retrieve only the files ending with .arff
        File[] tmp = fclap.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".arff");
            }
        });
        // add the retrieved files to the linked list
        for(File f : tmp)
        	ret.add(f);

        tmp = fnoclap.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".arff");
            }
        });
        for(File f : tmp)
        	ret.add(f);
    
        return ret;
    }
    
    /**
     * In order to make use of the in weka implemented stratified 10-fold cross validation, all the
     * data has to be contained in one corpus, i.e. one arff file. With our opensmile script we
     * get for each sample on arff file.
     * This function copies the content of the data section of each arff file into one consolidated
     * @param clap		path to the directory containing the clap samples
     * @param noclap	path to the directory containing the noclap samples
     * @param target	path to the file holding all sample data
     */
    public void consolidateSamples(String clap, String noclap, String target) {
        BufferedReader      reader;
        FileWriter          writer;
        LinkedList<File>	instances;
        String              nl;
        String              line;
        File                instance;
        boolean             firsttime;
        boolean             data;

        nl          = System.getProperty("line.separator");
        instances   = retrieveArffFiles(clap, noclap); // Get references to all arff files of samples
        firsttime   = true;
        writer		= null;
        reader		= null;
        
        try {
            writer  = new FileWriter(target);
        }
        catch(IOException e) {
            System.err.println("Error while trying to create File writer for " + target
            		+ " for arff consolidation");
            e.printStackTrace();
        }

        while((instance = instances.pollFirst()) != null) {
            data = false; // @data section not yet seen
            try {
                reader = new BufferedReader(new FileReader(instance));
                while((line = reader.readLine()) != null) {                    
                	
                	/* The whole content of the first sample arff file is copied to get the
                	 * attributes and class labels and stuff.
                	 * From subsequent sample arff files only the content after the @data relation
                	 * is copied to the consolidated file.
                	 * Empty lines are skipped
                	 */
                    if((data || firsttime) && !line.trim().isEmpty()) {
                    	/* The opensmile output is not well formatted, so the definition of the 
                    	 * class attribute is added in here manually
                    	 */
                        if(firsttime && line.contains("@attribute clap"))
                            line = "@attribute clap {clap,noclap}";
                        
                        /* With our configuration file the name of the different files is added
                         * as a separate attribute. This does not make any sense at all and LibSVM
                         * cannot handle string attributes in the first place, so get rid of this
                         * attribute
                         */
                        if(firsttime && line.contains("@attribute name"))
                            line = "";
                        
                        if(data) {
                            String[] tmp = line.split(",");
                            tmp[0] = "";  // name attribute complication (see above), delete value
                            
                            // Determine if it is a clap or noclap sample and set the attribute value
                            // in the @data section accordingly
                            if(instance.getPath().contains("noclap"))
                                tmp[tmp.length-1] = "noclap";
                            else
                                tmp[tmp.length-1] = "clap";
                            
                            line = "";
                            
                            // Create the content line for the consolidated arff file
                            for(String s : tmp)
                                line += s + ",";
                            // Remove leading and trailing comma
                            line = line.substring(1, line.length()-1);
                        }
                        // Finally, write the content line to the consolidated arff file
                        writer.write(line + nl);
                    }
                    // @data section begins. This statement has to be at the end since else @data
                    // will contained in consolidated arff file for each sample
                    if(!data && line.contains("@data")) { 
                        data = true;
                    }
                }
                // First run over
                firsttime = false;
            }
            catch(IOException e) {
                System.err.println("Error while trying to open " + instance.getPath() + " or write to " + target);
                e.printStackTrace();
            }

        }
        try{
            reader.close();
            writer.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteConsolidatedFile(String target) {
        File ftarget = new File(target);
        ftarget.delete();
    }
    
    /**
     * Reads instances from an arff file
     * @param target	path to the arff file
     * @return	weka.core.Instances
     */
    public Instances getInstances(String target) {
    	Instances ret = null;
        try {
            BufferedReader  reader  = new BufferedReader(new FileReader(target));
            ret     = new Instances(reader);
            reader.close();
            ret.setClassIndex(ret.numAttributes() - 1);
        }
        catch(Exception e) {
            System.err.println("Error on trying to read instances from consolidated file");
            e.printStackTrace();
        }
        return ret;
    }
    
    public void appendToFile(String append, String path) {
    	
    	try{
    	File file = new File(path);
    	FileWriter writer;
	    	if(file.exists()) {
	    		writer = new FileWriter(file, true);
	    	}
	    	else {
	    		writer = new FileWriter(path);
	    	}
	    	
	    	writer.write(append);
	    	writer.close();
    	}
    	catch(FileNotFoundException e) {
    		System.err.println("Error on file " + path);
            e.printStackTrace();
    	}
    	catch(IOException e) {
    		System.err.println("Error on file " + path);
    		e.printStackTrace();
    	}
    }
}
