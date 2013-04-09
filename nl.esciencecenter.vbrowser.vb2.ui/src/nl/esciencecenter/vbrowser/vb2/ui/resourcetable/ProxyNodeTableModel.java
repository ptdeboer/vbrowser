
package nl.esciencecenter.vbrowser.vb2.ui.resourcetable;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.data.Attribute;
import nl.esciencecenter.vbrowser.vb2.ui.data.AttributeSet;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.tasks.UITask;

/** 
 * Direct ResourceTableModel implementation for ProxyNode resources. 
 * Could use/implement ExtendedDataSource.  
 */
public class ProxyNodeTableModel extends ResourceTableModel 
{
    private static final long serialVersionUID = 8402972068504033387L;
    
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(ProxyNodeTableModel.class); 
        logger.setLevelToDebug();
    }
    // === === //
    
    private ProxyNode pnode;

    private UIViewModel uiModel;
    
    public ProxyNodeTableModel(UIViewModel uiModel,ProxyNode node)
    {
        this.pnode=node; 
        this.uiModel=uiModel; 
        init(); 
    }
    
    public ProxyNodeTableModel(ProxyNode node)
    {
        this.pnode=node; 
        this.uiModel=UIViewModel.createTableModel(); 
        init(); 
    }
    
    public void init()
    {
        if (pnode==null)
        {
            logger.debugPrintf("init(): >>> NULL ProxyNode <<<\n");
            this.setHeaders(new String[0]); 
            return; 
        }
        
        // dummy tabel: 
        StringList headers=new StringList();
        
        Presentation pres = pnode.getPresentation(); 
        StringList allHeaders=new StringList(); 
        
        try
        {
            allHeaders.add(pnode.getAttributeNames());
        }
        catch(Exception e)
        {
            handle(e,"Couldn't get resource attribute names\n");
        }

        // set default attributes if there is no presentation or headers
        // defined in the presentation  
        if ((pres==null) || (headers.size()<=0)) 
        {
            headers.addAll(allHeaders); 
        }
        else
        {
            String[] names = pres.getChildAttributeNames();
            for (String name:names)
                headers.add(name);
        }
        
        logger.debugPrintf("    headers = %s\n",headers.toString()); 
        logger.debugPrintf("all headers = %s\n",allHeaders.toString()); 
        
        this.setHeaders(headers.toArray()); 
        this.setAllHeaders(allHeaders); 
        
        fetchData(); 
    }

    private void fetchData()
    {
        // allowed at init time! 
        if (pnode==null)
        {
            logger.debugPrintf("fetchData(): NULL pnode\n"); 
            return; 
        }
        
        this.clearData();
        
        UITask task=new UITask(null,"Test get ProxyNode data")
        {
            boolean mustStop=false; 
            
            public void doTask()
            {
                try
                {
                    ProxyNode nodes[];
                    
                    try
                    {
                        nodes = pnode.getChilds(); 
                        logger.debugPrintf("fetchData(): #%d nodes\n",(nodes!=null)?nodes.length:"0"); 
                    }
                    catch (Exception e)
                    {
                        handle(e,"Couldn't fetch childs\n"); 
                        return; 
                    }
                    
                    for (ProxyNode node:nodes)
                    {
                        if (mustStop==true)
                            return; 
                        
                        AttributeSet set=new AttributeSet();
                        
                        //prefill: 
                        
                        set.put("name",node.getVRI().getBasename()); 
                        set.put("type",node.getResourceType()); 
                        set.put("location",node.getVRI()); 
                        
                        addRow(node.getVRI().toString(),set);  
                    }
                    
                    for (ProxyNode node:nodes)
                    {
                        if (mustStop==true)
                            return; 
                     
                        VRI vrl=node.getVRI(); 
                        
                        String hdrs[] = getHeaders();   
                        Attribute[] attrs;
                        try
                        {
                            attrs = node.getAttributes(hdrs);
                            setValues(vrl.toString(),attrs); 
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch(Throwable t)
                {
                    handle(t,"Failed to fetch table data\n");
                }
            }

            public void stopTask()
            {
                
            }
        };
        
        task.startTask();
    }

    protected void handle(Throwable t,String format,Object... args)
    {
        logger.logException(ClassLogger.ERROR,t,format,args); 
        t.printStackTrace();
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
        StringList attrNames=new StringList(newName); 
        fetchAttributes(attrNames); 
    }
    
    private void fetchAttributes(final StringList attrNames)
    {
        if (pnode==null)
            return; 
        
        ProxyNode nodes[];
        
        try
        {
            // TODO: ViewFilter! 
            nodes = pnode.getChilds();
        }
        catch (Exception e)
        {
            handle(e,"Couldn't fetch childs\n"); 
            return; 
        }
        
        for (ProxyNode node:nodes)
        {
            Attribute[] attrs;
            try
            {
                attrs = node.getAttributes(attrNames.toArray());
                setValues(node.getVRI().toString(),attrs);  
            }
            catch (Exception e)
            {
                handle(e,"Couldn't get Attributes\n"); 
            } 
        }
    }
    
    
    
}
