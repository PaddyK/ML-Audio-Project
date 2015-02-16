import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.util.LinkedList;

import weka.core.Instances;

public class Data {

    public Data () {

    }

    public LinkedList<File> retrieveArffFiles(String clap, String noclap) {
        File fclap                  = new File(clap);
        File fnoclap                = new File(noclap);
        LinkedList<File> ret   = new LinkedList<File>();
        
        File[] tmp = fclap.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".arff");
            }
        });
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

    public void consolidateSamples(String clap, String noclap, String target) {
        BufferedReader          reader;
        FileWriter          	writer;
        LinkedList<File>	    instances;
        String                  nl;
        String                  line;
        File                    instance;
        boolean                 firsttime;
        boolean                 data;

        nl          = System.getProperty("line.separator");
        instances   = retrieveArffFiles(clap, noclap);
        firsttime   = true;
        writer		= null;
        reader		= null;
        
        try {
            writer  = new FileWriter(target);
        }
        catch(IOException e) {
            System.err.println("Error while trying to create File writer for " + target + " for arff consolidation");
            e.printStackTrace();
        }

        while((instance = instances.pollFirst()) != null) {
            data = false;
            try {
                reader = new BufferedReader(new FileReader(instance));
                while((line = reader.readLine()) != null) {
                    

                    if((data || firsttime) && !line.trim().isEmpty()) {
                        if(firsttime && line.contains("@attribute clap"))
                            line = "@attribute clap {clap,noclap}";
                        if(data) {
                            String[] tmp = line.split(",");

                            if(instance.getPath().contains("noclap"))
                                tmp[tmp.length-1] = "noclap";
                            else
                                tmp[tmp.length-1] = "clap";
                            
                            line = "";
                            
                            for(String s : tmp)
                                line += s + ",";

                            line = line.substring(0, line.length()-1);
                        }

                        writer.write(line + nl);
                    }
                    if(!data && line.contains("@data")) { 
                        data = true;
                    }
                }
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
}
