package nl.esciencecenter.vbrowser.vb2.ui.proxy;

import java.util.ArrayList;
import java.util.List;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

public class VirtualRootNode extends ProxyNode
{
    protected String name="Root"; 
    
    protected List<ProxyNode> childs=new ArrayList<ProxyNode>();
    
    protected VirtualRootNode(ProxyFactory factory, VRL proxyLocation)
    {
        super(factory, proxyLocation);
    }

    @Override
    protected String doGetName() throws ProxyException
    {
        return name; 
    }

    @Override
    protected String doGetResourceType() throws ProxyException
    {
        return "Root"; 
    }

    @Override
    protected String doGetResourceStatus() throws ProxyException
    {
        return null;
    }

    @Override
    protected String doGetMimeType() throws ProxyException
    {
        return null;
    }

    @Override
    protected boolean doGetIsComposite() throws ProxyException
    {
        return true; 
    }

    @Override
    protected ProxyNode[] doGetChilds(int offset, int range, LongHolder numChildsLeft) throws ProxyException
    {
        return this.subrange(childs.toArray(new ProxyNode[0]),offset,range); 
    }

    @Override
    protected ProxyNode doGetParent() throws ProxyException
    {
        return this;
    }

    @Override
    protected String[] doGetChildTypes() throws ProxyException
    {
        String types[]=new String[childs.size()]; 
        
        for (int i=0;i<types.length;i++)
        {
            types[i]=childs.get(i).getResourceType();
        }
        return types;
    }

    @Override
    protected String[] doGetAttributeNames() throws ProxyException
    {
        return null;
    }

    @Override
    protected Attribute[] doGetAttributes(String[] names) throws ProxyException
    {
        return null;
    }

    @Override
    protected UIPresentation doGetPresentation()
    {
        return null;
    }

    public void addChild(ProxyNode node)
    {
        if (childs==null)
            childs=new ArrayList<ProxyNode>(); 
        
       this.childs.add(node); 
    }

    public void setChilds(List<ProxyNode> nodes)
    {
       this.childs=new ArrayList<ProxyNode>(nodes);
    }

    public boolean hasChild(VRL locator)
    {
        return (this.getChild(locator)!=null); 
    }

    public ProxyNode getChild(VRL locator)
    {
        for (ProxyNode node:this.childs)
        {
            if (node.hasLocator(locator))
                return node; 
        }
        
        return null; 
    }
    

}
