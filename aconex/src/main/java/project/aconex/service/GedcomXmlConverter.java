package project.aconex.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GedcomXmlConverter {

    private static final Logger LOG = Logger.getLogger("GedcomXmlConverter.java");

    /*
     * Parses gedcom input file line by line and constructs list of string.
     */
    public static List<String> readGedcomFile(String pathToGedcomFile)
    {
        List<String> inputLines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(pathToGedcomFile)));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.length() > 0) {
                    inputLines.add(line);
                }
            }
            reader.close();
        }
        catch(IOException exception) {
            LOG.severe("Unable to read input file " + exception.getMessage());
        }
  
        return inputLines;
    }

    /*
     * Writes flattened gedcom xml string to a file.
     */
    public static void writeGedcomXml(String outputFileName, String flattenedGedcomXml)
    {
        File file = new File(outputFileName);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            out.write(flattenedGedcomXml);
            out.close();
        }
        catch(IOException exception) {
            LOG.severe("Error has occurred while creating xml " + exception.getMessage());
        }
    }
}
