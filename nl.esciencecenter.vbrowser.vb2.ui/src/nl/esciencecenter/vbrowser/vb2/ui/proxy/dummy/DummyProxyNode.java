package nl.esciencecenter.vbrowser.vb2.ui.proxy.dummy;

import java.util.ArrayList;
import java.util.List;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.data.Attribute;
import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;


public class DummyProxyNode extends ProxyNode
{
    static private UIPresentation dummyPresentation;

    static private StringList attrNames=null; 
    
    static
    {
        attrNames=new StringList(new String[]{"attr1","attr2","attr3","attr4"});
        
        dummyPresentation=UIPresentation.createDefault();
        for (int i=0;i<attrNames.size();i++)
            dummyPresentation.setAttributePreferredWidths(attrNames.get(i),new int[]{42,42+i*42,42+4*42}); 
        dummyPresentation.setChildAttributeNames(attrNames.toArray()); 
        
    }

    // --- 
    
    private DummyProxyNode parent;
    private List<DummyProxyNode> childs;
	private boolean isComposite=true; 
	private String mimetype="text/plain";
    protected DummyProxyNode createChild(String childname)
        
    {
        return new DummyProxyNode(this,getVRI().appendPath(childname)); 
    }
    
    public DummyProxyNode(VRI locator)
    {
        super(locator); 
        this.parent=null; 
    }
    
    protected DummyProxyNode(DummyProxyNode parent,VRI locator)
    {
        super(locator);
        this.parent=parent; 
        init(); 
    }

    private void init()
    {
    	;
    }
    
    @Override
    public boolean isBusy()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "DummyProxy:"+this.getID(); 
    }

    @Override
    public boolean hasChildren()
    {   
        return true; 
    }

    @Override
    public boolean isComposite()
    {
        return isComposite; 
    }

    @Override
    protected ProxyNode doGetParent() throws ProxyException
    {
        return DummyProxyFactory.getDefault().doOpenLocation(this.locator.getParent());
    }
    
    @Override
    public String getMimeType()
    {
        return this.mimetype; 
    }

    @Override
    public ProxyNode[] doGetChilds(int offset, int range,LongHolder numChildsLeft)
    {
        if (childs==null)
        {	
        	
        	childs=new ArrayList<DummyProxyNode>(); 
            childs.add(createChild("child-"+id+".1")); 
            childs.add(createChild("child-"+id+".2")); 
            childs.add(createChild("child-"+id+".3")); 
            
            DummyProxyNode node = createChild("leaf-"+id+".4");
            node.isComposite=false; 
            node.mimetype="text/html"; 
            childs.add(node); 
            
            node = createChild("leaf-"+id+".5");
            node.isComposite=false; 
            node.mimetype="text/rtf"; 
            childs.add(node); 
        }
        
        ProxyNode _arr[]=new ProxyNode[childs.size()]; 
        _arr=childs.toArray(_arr); 
        
        return subrange(_arr,offset,range);  
    }

    public static DummyProxyNode getRoot() throws ProxyException
    {
        try
        {
            return new DummyProxyNode(new VRI("proxy:/"));
        }
        catch (VRISyntaxException e)
        {
            throw new ProxyException("VRISyntaxException",e); 
        } 
    }

	@Override
	public ProxyFactory getProxyFactory()
	{
		return DummyProxyFactory.getDefault(); 
	}

	@Override
	protected String doGetMimeType() throws ProxyException 
	{
		return this.mimetype; 
	}

	@Override
	protected boolean doGetIsComposite() throws ProxyException
	{
		return this.isComposite; 
	}

    @Override
    protected String doGetName() 
    {
        return this.getVRI().getBasename(); 
    }

    @Override
    protected String doGetResourceType() 
    {
        return "DummyType"; 
    }

    @Override
    protected String doGetResourceStatus() 
    {
        return "NOP";
    }

    @Override
    protected String[] doGetChildTypes() 
    {
        return new String[]{"DummyType"}; 
    }

    @Override
    protected String[] doGetAttributeNames() throws ProxyException
    {
        return attrNames.toArray();
    }

    @Override
    protected Attribute[] doGetAttributes(String[] names) throws ProxyException
    {
        Attribute attrs[]=new Attribute[names.length];
        
        for (int i=0;i<names.length;i++)
            attrs[i]=new Attribute(names[i],"Value:"+names[i]); 
        
        return attrs;
        
    }

    @Override
    protected UIPresentation doGetPresentation()
    {
        return dummyPresentation; 
    }

}
