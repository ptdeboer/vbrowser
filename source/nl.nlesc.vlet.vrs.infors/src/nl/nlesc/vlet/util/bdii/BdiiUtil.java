package nl.nlesc.vlet.util.bdii;

import java.util.HashMap;
import java.util.Map;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrs.VRSContext;

public class BdiiUtil
{
    private static Map<String,BdiiService> services=new HashMap<String, BdiiService>(); 
    
    /**
     * Creates or return already created BDII Service for this context. 
     * @retun Always create a BdiiService Object. 
     * @throws VlException
     */
    public static BdiiService getBdiiService(VRSContext vrsContext) throws VlException
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
