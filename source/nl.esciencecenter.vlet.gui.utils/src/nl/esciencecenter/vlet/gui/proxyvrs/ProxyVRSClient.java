/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.gui.proxyvrs;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.error.InitializationError;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.events.ResourceEvent;
import nl.esciencecenter.vlet.vrs.vrms.ConfigManager;

/**
 * ProxyVRSClient. 
 * Client to the ProxyVRS. 
 * Under construction. All direct access to the VRS/VFS should be wrapped. 
 */
public class ProxyVRSClient 
{
	private static ProxyVRSClient instance=null; 

	// one instance for now: 
	public static synchronized ProxyVRSClient getInstance()
	{
		if (instance==null)
			instance=new ProxyVRSClient(UIGlobal.getVRSContext());
		
		return instance; 
	}
	
	// ========================================================================
	//
	// ========================================================================

	private VRSContext context;
	private ProxyResourceEventNotifier proxyEventNotifier=null;
	private ProxyNodeFactory _proxyNodeFactory=null;  
	
	protected ProxyVRSClient(VRSContext vrsContext)
	{
		this.context=vrsContext; 
		init();
	}

	protected void init()
	{
		proxyEventNotifier=new ProxyResourceEventNotifier(context); 
	}
	
	public void dispose() 
	{
		proxyEventNotifier.dispose(); 
		
		if (_proxyNodeFactory!=null)
			_proxyNodeFactory.reset(); 
	}

    // =======================================================================
    // ProxyNode Factory interface 
    // =======================================================================
    
    /** Set default ProxyNode Factory. Currently only one per instance supported */ 
    public void setProxyNodeFactory(ProxyNodeFactory factory)
    {
    	_proxyNodeFactory=factory;  
    }
    
    /** Get default ProxyNode Factory. Currently only one per instance supported */ 
    public ProxyNodeFactory getProxyNodeFactory()
    {
    	if (_proxyNodeFactory==null)
    		throw new InitializationError("Initiliziation error. Proxy Node Factory not set. Use ProxyTNode.init() first !!!"); 
    	
    	return _proxyNodeFactory; 
    }
    
    // ========================================================================
    // VRS 
    // ========================================================================
    
    public VRL getVirtualRootLocation() throws VrsException
    {
        return context.getVirtualRootLocation();
    }
    
	// ========================================================================
	// ProxyResourceEvent interface
	// ========================================================================

	public ProxyResourceEventNotifier getProxyResourceEventNotifier()
	{
		return proxyEventNotifier; 
	}

	public void addResourceEventListener(ProxyResourceEventListener listener) 
	{
		proxyEventNotifier.addResourceEventListener(listener);		
	}
	
	public void removeResourceEventListener(ProxyResourceEventListener listener) 
	{
		proxyEventNotifier.removeResourceEventListener(listener);		
	}
	
	public void fireEvent(ResourceEvent event)
    {
		proxyEventNotifier.fireEvent(event); 
    }

    public ConfigManager getConfigManager()
    {
        return context.getConfigManager();
    }
}
