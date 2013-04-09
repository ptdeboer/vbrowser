package nl.esciencecenter.vbrowser.vb2.ui.data;



/**
 * Interface for MetaData resources
 *  
 * @author Piter T. de Boer 
 */
public interface IAttributes
{
    public String[] getAttributeNames(); 
    
    public Attribute getAttribute(String name); 
    
    public Attribute[] getAttributes(String names[]); 
    
}
