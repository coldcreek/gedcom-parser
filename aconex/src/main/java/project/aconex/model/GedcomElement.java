package project.aconex.model;

import java.util.List;

/**
 * 
 */

/**
 * @author muralidh
 */
public class GedcomElement
{
    /*
     * Represents the current depth in the tree
     */
    private Integer level;

    /*
     * Either a tag that identifies the type of data in that node,
     * or it is a unique identifier. 
     */
    private String  tagOrId;

    /*
     * Type of the subtree that is identified.
     */
    private String  data;

    /*
     * Contains list of child gedcom elements
     */
    private List<GedcomElement> childElements;

    /*
     * Contains link to parent gedcom element.
     * Will be null for root element
     */
    private GedcomElement parentElement;

    public GedcomElement(Integer level,
                         String tagOrId,
                         String data,
                         GedcomElement parentElement,
                         List<GedcomElement> childElements)
    {
        this.level         = level;
        this.tagOrId       = tagOrId;
        this.data          = data;
        this.parentElement = parentElement;
        this.childElements = childElements;
    }

    public Integer getLevel()
    {
        return level;
    }

    public void setLevel(Integer level)
    {
        this.level = level;
    }

    public String getTagOrId()
    {
        return tagOrId;
    }

    public void setTagOrId(String tagOrId)
    {
        this.tagOrId = tagOrId;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public List<GedcomElement> getChildElements()
    {
        return childElements;
    }

    public void setChildElements(List<GedcomElement> childElements)
    {
        this.childElements = childElements;
    }

    public GedcomElement getParentElement()
    {
        return parentElement;
    }

    public void setParentElement(GedcomElement parentElement)
    {
        this.parentElement = parentElement;
    }

}
