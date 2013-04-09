package nl.esciencecenter.vbrowser.vb2.ui.actionmenu;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;

public class Action 
{
	// ===
	// default actions, no arguments.
	// ===
	
//    public final static Action DELETE=new Action("Delete",ActionMethod.DO_DELETE);
//	
//	public final static Action REFRESH=new Action("Refresh",ActionMethod.DO_REFRESH);
//	
//	public final static Action PROPERTIES=new Action("Properties",ActionMethod.DO_PROPERTIES);
//
//	public final static Action SELECTION_ACTION=new Action("SelectionAction",ActionMethod.DO_SELECTION_ACTION);
//
//	public final static Action DEFAULT_ACTION=new Action("DefaultAction",ActionMethod.DO_DEFAULT_ACTION);

	// === instance === // 
	private ActionMethod actionMethod; 

	private StringList arguments=null;

    private Object source; 
	
	public Action(Object source,ActionMethod actionMethod)
	{
	    this.source=source; 
	    this.actionMethod=actionMethod; 
	}

	public ActionMethod getActionMethod()
	{
		return this.actionMethod;
	}
	
	public String getActionMethodString()
	{
		return this.actionMethod.getMethodName(); 
	}

	public String toString()
	{
		String str=actionMethod.toString();
		//optional arguments; 
		if ((this.arguments!=null) && (this.arguments.size()>0))
				str=str+":"+this.arguments.toString(","); 
		return str;
	}
	
	public static Action createFrom(ViewNode viewNode, String cmdStr) 
	{
		String strs[]=cmdStr.split(":"); 
		
		String methodStr=null;
		String argsStr=null;
		
		if (strs.length>0)
			methodStr=strs[0];
		
		if (strs.length>1)
			argsStr=strs[1]; 
		
		Action action=new Action(viewNode,ActionMethod.createFrom(methodStr)); 
		action.parseArgs(argsStr); 
		return action; 
	}

	protected void parseArgs(String argsStr) 
	{	
		if ((argsStr==null) || argsStr.equals(""))
			return; 
		
		this.arguments=StringList.createFrom(argsStr,","); 
	}

	public StringList getArgs()
	{
		return this.arguments; 
	}
	
    public Object getActionSource()
    {
        return source;
    }

    // === 
    // Factory method 
    // ===
    
    public static Action createSelectionAction(ViewNode node)
    {
        Action action=new Action(node,ActionMethod.SELECTION_ACTION);
        return action; 
    }
    
    public static Action createDefaultAction(ViewNode node)
    {
        Action action=new Action(node,ActionMethod.DEFAULT_ACTION); 
        return action; 
    }

    public static Action createGlobalAction(ActionMethod meth)
    {
        Action action=new Action(null,meth);  
        return action; 
    }
    
}
