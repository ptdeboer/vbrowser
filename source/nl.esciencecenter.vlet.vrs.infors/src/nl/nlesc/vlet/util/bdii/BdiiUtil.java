package nl.nlesc.vlet.util.bdii;

import java.util.HashMap;
import java.util.Map;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.vrs.VRSContext;

public class BdiiUtil
{
    private static Map<String,BdiiService> services=new HashMap<String, BdiiService>(); 
    
    /**
     * Creates or return already created BDII Service for this context. 
     * @retun Always create a BdiiService Object. 
     * @throws VrsException
     */
    public static BdiiService getBdiiService(VRSContext vrsContext) throws VrsException
    {
        // auto update !
        java.net.URI uri = vrsContext.getConfigManager().getBdiiServiceURI();
        
        synchronized (services)
        {
            BdiiService bdiiService = services.get(uri.toString()); 
            
            if (bdiiService == null)
            {
                bdiiService = BdiiService.createService(vrsContext);
                services.put(uri.toString(),bdiiService); 
            }

            return bdiiService;
        }
    }
}
