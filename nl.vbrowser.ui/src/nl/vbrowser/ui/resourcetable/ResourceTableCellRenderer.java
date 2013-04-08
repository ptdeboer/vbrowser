
package nl.vbrowser.ui.resourcetable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nl.vbrowser.ui.data.Attribute;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.model.ViewNode;

import javax.swing.Icon;

public class ResourceTableCellRenderer extends DefaultTableCellRenderer 
{
    private static final long serialVersionUID = -7461721298242661750L;

    @Override
	public Component getTableCellRendererComponent(JTable table, 
	        Object value,
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column) 
	{
        UIViewModel uiModel=null; 
        
        if (table instanceof ResourceTable) 
        {   
            uiModel=((ResourceTable)table).getUIViewModel();  
        }
        
	    if (value==null)
	    {
	       value="?";
	    }

        // =====================
        // Render Icon 
        // =====================

	    if (value instanceof Icon)
	    {
	    	return renderIcon(this,uiModel,(Icon)value,"",isSelected,hasFocus); 
	    }
	    
	    if (value instanceof Attribute)
	    {
	        Attribute attr=(Attribute)value; 
	        
	        Object attrVal=attr.getValue();
	        
	        // =====================
	        // Render Icon Attribute 
	        // =====================
	        
		    if (attrVal instanceof Icon)
		    {
		    	return renderIcon(this,uiModel,(Icon)attrVal,"",isSelected,hasFocus); 
		    }

		    // ================
		    // Render ViewNode!
		    // ================
		    
		    if (attrVal instanceof ViewNode)
		    {
		    	ViewNode viewNode=(ViewNode)attrVal; 
		    	return renderIcon(this,uiModel,viewNode.getIcon(),viewNode.getName(),isSelected,hasFocus);
		    }

	        // Default to String value... 
	        value=attr.getStringValue(); 
	    }
	    
	    // thiz should be *this* 
	    Component thiz = super.getTableCellRendererComponent(table,value, isSelected, hasFocus, row, column);
	    
	    // update *this* with UI attributes
        if (uiModel!=null)
            if (thiz==this)
                updateUIAttributes(this,uiModel,isSelected,hasFocus);
	    
		return thiz;    
	}

	private static Component renderIcon(DefaultTableCellRenderer target, 
	        UIViewModel uiModel,
	        Icon icon, 
	        String name,
	        boolean isSelected,
	        boolean hasFocus) 
	{
	    target.setIcon(icon); 
	    target.setText(name);
		
		if (uiModel!=null)
		    updateUIAttributes(target,uiModel,isSelected,hasFocus); 
	
		return target; 
	}
	
	private static Component updateUIAttributes(DefaultTableCellRenderer target, 
	        UIViewModel uiModel,
	        boolean isSelected,
	        boolean hasFocus)
    {
	    Color fg=null; 
	    Color bg=null; 
		    
	    if (isSelected) 
	    {
	        fg=uiModel.getSelectedForegroundColor(); 
	        bg=uiModel.getSelectedBackgroundColor(); 
	    }
	    else  
	    {
	        fg=uiModel.getForegroundColor(); 
            bg=uiModel.getBackgroundColor(); 
	    }
    
	    if (fg!=null)
	        target.setForeground(fg);
                    
	    if (bg!=null)
	        target.setBackground(bg); 
		
		return target; 
	}

}
