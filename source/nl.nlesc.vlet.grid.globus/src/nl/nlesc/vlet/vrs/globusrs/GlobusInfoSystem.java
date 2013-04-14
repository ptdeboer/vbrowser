package nl.nlesc.vlet.vrs.globusrs;

import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VResourceSystem;
import nl.nlesc.vlet.vrs.infors.CompositeServiceInfoNode;


public class GlobusInfoSystem extends CompositeServiceInfoNode<VNode> implements VResourceSystem
{

	public GlobusInfoSystem(VRSContext context, VRL vrl) 
	{
		super(context, vrl);
	}

	@Override
	public String getID() 
	{
		return "globusinfo";
	}

	@Override
	public VRL resolve(String path) throws VRISyntaxException
	{
	    return this.getVRL().resolve(path);
    }
	 
	@Override
	public VNode openLocation(VRL vrl) throws VlException 
	{
		GlobusInfoNode node=GlobusInfoNode.createNode(this.getVRSContext(),vrl); 
		return node; 
	}


	@Override
	public void connect() throws VlException 
	{//dummy
	}

	@Override
	public void disconnect() throws VlException 
	{//dummy
	}

	@Override
	public void dispose() 
	{//dummy
	}

	@Override
	public String getResourceType() 
	{
		return "GlobusInfo"; 
	}

	@Override
	public String[] getResourceTypes() 
	{
		return null;
	}

	
}
