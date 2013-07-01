/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.nlesc.vlet.gui.proxymodel;

import static nl.nlesc.vlet.vrs.data.VAttributeConstants.ATTR_ICON;
import static nl.nlesc.vlet.vrs.data.VAttributeConstants.ATTR_LOCATION;
import static nl.nlesc.vlet.vrs.data.VAttributeConstants.ATTR_NAME;
import static nl.nlesc.vlet.vrs.data.VAttributeConstants.ATTR_RESOURCE_TYPE;
import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

import nl.nlesc.vlet.gui.UILogger;
import nl.nlesc.vlet.gui.panels.resourcetable.ResourceTableModel;
import nl.nlesc.vlet.gui.proxyvrs.ProxyNode;

public class ProxyNodeTableModel extends ResourceTableModel
{
    private static final long serialVersionUID = 8402972068504033387L;
    private ProxyNode pnode;
    
    public ProxyNodeTableModel(ProxyNode node)
    {
        this.pnode=node; 
        init(); 
    }
    
    public void init()
    {
        // dummy tabel: 
        StringList headers=new StringList();
        
        headers.add(ATTR_ICON);
        headers.add(ATTR_RESOURCE_TYPE);
        headers.add(ATTR_NAME);
        headers.add(ATTR_LOCATION);
        
        this.setHeaders(headers.toArray()); 
        this.setAllHeaders(new StringList(pnode.getAttributeNames()));
        
        fetchData(); 
    }

    private void fetchData()
    {
        this.clearData();
        
        ActionTask task=new ActionTask(null,"Test get ProxyNode data")
        {
            public void doTask()
            {
                ProxyNode nodes[];
                
                try
                {
                    nodes = pnode.getChilds(null);
                }
                catch (VrsException e)
                {
                    handle(e); 
                    return; 
                }
                
                for (ProxyNode node:nodes)
                {
                    AttributeSet set=new AttributeSet();
                    set.put(ATTR_NAME,node.getVRL().getBasename()); 
                    set.put(ATTR_RESOURCE_TYPE,node.getType()); 
                    set.put(new Attribute(ATTR_LOCATION,node.getVRL())); 
                    
                    addRow(node.getVRL().toString(),set);  
                }
                
                for (ProxyNode node:nodes)
                {
                    VRL vrl=node.getVRL(); 
                    
                    String hdrs[] = getHeaders();   
                    Attribute[] attrs;
                    try
                    {
                        attrs = node.getAttributes(hdrs);
                        setValues(vrl.toString(),attrs); 
                    }
                    catch (VrsException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            public void stopTask()
            {
                
            }
        };
        
        task.startTask();
    }

    protected void handle(Throwable e)
    {
        UILogger.logException(this,ClassLogger.ERROR,e,"Exception:%s\n"); 
    }

    public int insertHeader(String headerName, String newName, boolean insertBefore)
    {
        int index=super.insertHeader(headerName, newName, insertBefore); 
        // will update data model, Table View will follow AFTER TableStructureEvent 
        // has been handled. 
        fetchAttribute(newName);
        return index; 
    }

    private void fetchAttribute(String newName)
    {
        ProxyNode nodes[];
        
        StringList attrNames=new StringList(newName); 
        
        try
        {
            // should be cached: 
            nodes = pnode.getChilds(null);
        }
        catch (VrsException e)
        {
            handle(e); 
            return; 
        }
        
        for (ProxyNode node:nodes)
        {
            Attribute[] attrs;
            try
            {
                attrs = node.getAttributes(attrNames.toArray());
                setValues(node.getVRL().toString(),attrs);  
                
            }
            catch (VrsException e)
            {
                handle(e); 
            } 
        }
    }
    
    
    
}
