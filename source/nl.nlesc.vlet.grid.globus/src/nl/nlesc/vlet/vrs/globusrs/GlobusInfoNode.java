package nl.nlesc.vlet.vrs.globusrs;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRSContext;

public class GlobusInfoNode extends VNode
{
	public static GlobusInfoNode createNode(VRSContext context,VRL vrl)
	{
		GlobusInfoNode node=new GlobusInfoNode(context,vrl);
		return node; 
	}	
	 
    public GlobusInfoNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
    }

	@Override
	public String getResourceType() 
	{
		return "GlobusInfo"; 
	}
	
	public String getName()
	{
		return "GlobusInfo";
	}
	
	//
	//public String getIconURL(int prefSize)
    //{
	//	return ;
    //}
    //

}

