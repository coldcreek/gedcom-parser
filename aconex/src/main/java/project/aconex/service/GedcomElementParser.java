package project.aconex.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import project.aconex.model.GedcomElement;

/**
 *  GEDCOM Parser
 */

/**
 * @author Gayathri Muralidharan
 */
public class GedcomElementParser {

    private static final Logger LOG = Logger.getLogger("SampleParser.java");

    /**
     * @param args
     *        Absolute path to gedcom input file
     */
    public static void main(String[] args)
    {
        // Read input file
        if(args == null || args.length == 0 || args.length > 1)
        {
            LOG.severe("Please specify absolute path to a single gedcom input file");
            return;
        }

        String inputFileName = args[0];
        List<String> inputLines = GedcomXmlConverter.readGedcomFile(inputFileName);

        if(inputLines == null || inputLines.isEmpty())
        {
            throw new IllegalArgumentException("Unable to read input file/Input file is empty");
        }

        // Construct object hierarchy
        List<GedcomElement> elements = parseGedcomInput(inputLines);

        if(elements == null)
        {
            throw new IllegalArgumentException("Unable to parse given gedcom file :" + inputFileName);
        }

        // Convert object hierarchy to xml string
        String flattenedGedcomXml = convertGedcomElementsToXmlString(elements);

        if(flattenedGedcomXml == null)
        {
            throw new IllegalArgumentException("Unable to flatten gedcom elements to xml string");
        }

        // Remove .txt or .* from input file name and append .xml to it, to arrive
        // at output file name.
        String outputFileName = inputFileName.replace(
            inputFileName.substring(inputFileName.lastIndexOf(".")), ".xml");

        LOG.info("Gedcom output file :" + outputFileName);

        // Write xml string to a file
        GedcomXmlConverter.writeGedcomXml(outputFileName, flattenedGedcomXml);

    }

    /*
     * Helper method that constructs gedcom object hierarchy from the given input
     * string list.
     */
    private static List<GedcomElement> parseGedcomInput(List<String> inputList)
    {
        List<GedcomElement> elements = new ArrayList<>();

        Integer prevLevel = null;
        GedcomElement currentParent = null;

        for(String input:inputList)
        {
            String[] parts   = input.split("\\s+", 3);
            Integer  level   = Integer.valueOf(parts[0]);
            String   tagOrId = parts.length > 1 ? parts[1] : null;
            String   data    = parts.length > 2 ? parts[2] : null;

            // Back-traverse and identify correct parent.
            //i.e This is a new subtree.
            if(prevLevel != null && level < prevLevel && currentParent != null)
            {
                while(currentParent != null &&
                      currentParent.getParentElement() != null &&
                      currentParent.getLevel() >= level)
                {
                    currentParent = currentParent.getParentElement();
                }

                prevLevel = currentParent.getLevel();
            }

            if(level == 0) {
                // Root element, if level is 0
                GedcomElement element = new GedcomElement(level,
                                                          tagOrId,
                                                          data,
                                                          null,
                                                          new ArrayList<GedcomElement>());

                elements.add(element);
                currentParent = element;
                prevLevel = level;
            }
            else if(level > prevLevel && currentParent != null)
            {
                // Child element, if current level > previous level
                GedcomElement childElement = new GedcomElement(level,
                                                               tagOrId,
                                                               data,
                                                               currentParent,
                                                               new ArrayList<GedcomElement>());

                currentParent.getChildElements().add(childElement);
                currentParent = childElement;
                prevLevel = level;
            }
            else if(level == prevLevel && currentParent != null)
            {
                // Sibling element, if current level == previous level
                GedcomElement siblingElement = new GedcomElement(level,
                                                                 tagOrId,
                                                                 data,
                                                                 currentParent.getParentElement(),
                                                                 new ArrayList<GedcomElement>());
                currentParent.getParentElement().getChildElements().add(siblingElement);
                currentParent = siblingElement;
            }
        }

        return elements;
    }

    private static String getXmlHeader()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }

    private static String escapeXml(String input)
    {
        if(input == null || input.length() == 0) {
            return input;
        }

        return input.replace("&", "&amp;").
                     replace("<", "&lt;").
                     replace(">", "&gt;").
                     replace("\"", "&quot;").
                     replace("\'", "&apos;");
    }

    /*
     * Helper method that recursively flattens gedcom object hierarchy into xml string, handling root
     * and sibling id/data elements appropriately.
     */
    private static String flattenGedcomElements(List<GedcomElement> elements)
    {
        StringBuilder flattenedGedcomElement = new StringBuilder();

        if(elements == null || elements.isEmpty())
        {
            return flattenedGedcomElement.toString();
        }

        for(GedcomElement element:elements) {
            String tagOrId = element.getTagOrId();

            String data    = null;
            if(element.getData() != null) {
                data = escapeXml(element.getData());
            }

            if(tagOrId.startsWith("@") &&
               tagOrId.endsWith("@"))
            {
                if(data != null) {
                    flattenedGedcomElement.append("<" + data.toLowerCase() + " id=\"" + tagOrId + "\">\n");
                }

                if(element.getChildElements() != null &&
                   !element.getChildElements().isEmpty())
                {
                    for(GedcomElement childElement:element.getChildElements()) {
                        flattenedGedcomElement.append(flattenGedcomElements(Arrays.asList(childElement)));
                    }
                }

                if(data != null) {
                    flattenedGedcomElement.append("</" + data.toLowerCase() +">\n");
                }
            }
            else
            {
                String tag = element.getTagOrId().toLowerCase();
                if(element.getChildElements() != null &&
                   !element.getChildElements().isEmpty())
                {
                    if(data != null) {
                        flattenedGedcomElement.append("<" + tag + " value=\"" + data+ "\">\n");
                    }
                    else {
                        flattenedGedcomElement.append("<" + tag + ">\n");
                    }
                    for(GedcomElement childElement:element.getChildElements()) {
                        flattenedGedcomElement.append(flattenGedcomElements(Arrays.asList(childElement)));
                    }

                    flattenedGedcomElement.append("</" + tag + ">\n");
                }
                else {
                    if(data != null) {
                        flattenedGedcomElement.append("<" + tag + ">" + data + "</" + tag + ">\n");
                    }
                    else {
                        flattenedGedcomElement.append("<" + tag + "/>\n");
                    }
                }
            }
        }
        return flattenedGedcomElement.toString();
    }

    private static String convertGedcomElementsToXmlString(List<GedcomElement> elements)
    {
        StringBuilder flattenedGedcomElements = new StringBuilder();

        flattenedGedcomElements.append(getXmlHeader());
        flattenedGedcomElements.append("<gedcom>\n");
        flattenedGedcomElements.append(flattenGedcomElements(elements));
        flattenedGedcomElements.append("\n</gedcom>");

        LOG.info("Xml output of given gedcom input = " + flattenedGedcomElements.toString());
        return flattenedGedcomElements.toString();

    }
}
