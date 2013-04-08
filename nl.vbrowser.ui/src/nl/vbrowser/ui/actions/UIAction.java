package nl.vbrowser.ui.actions;

import javax.swing.AbstractAction;
//import javax.swing.Action;
import javax.swing.Action;

public abstract class UIAction extends AbstractAction // implements javax.swing.Action
{
    private static final long serialVersionUID = -2196060232819798830L;
    
    public UIAction(String name)
    {
        super(name); 
        //putValue(Action.NAME,name); 
    }
    

    public String getName()
    {
        Object obj=this.getValue(Action.NAME);
        if (obj!=null)
            return obj.toString(); 
        return null;
    }
    
//
//    public Object getValue(String s)
//    {
//        if (s == "Name")
//            return name;
//        else
//            return null;
//    }
//
//    public void putValue(String s, Object obj)
//    {
//    }
//
//    public void setEnabled(boolean flag)
//    {
//    }
//
//    public final boolean isEnabled()
//    {
//        return isEnabled(null);
//    }
//
//    public boolean isEnabled(Object obj)
//    {
//        return true;
//    }
//
//    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
//    {
//    }
//
//    public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
//    {
//    }

    
}
