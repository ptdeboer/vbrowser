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

package nl.nlesc.vlet.gui.proxynode;

import java.util.List;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.proxyvrs.ProxyNode;
import nl.nlesc.vlet.gui.view.ViewModel;
import nl.nlesc.vlet.gui.view.ViewNode;

public class ViewNodeFactory
{

    public static ViewNode[] createFrom(ViewModel model,ProxyNode[] nodes)
    {
        return createFrom(model,nodes,ViewNode.DEFAULT_ICON_SIZE); 
    }
    
    public static ViewNode[] createFrom(ViewModel model,ProxyNode[] nodes, int size)
    {
        if (nodes==null)
            return null;
        
        ViewNode items[]=new ViewNode[nodes.length]; 
        
        for (int i=0;i<nodes.length;i++)
        {
            items[i]=createViewNode(nodes[i],size); 
        }
        
        return items; 
    }
    
    static public ViewNode[] createFrom(List<ProxyNode> nodes,int size)
    {
        ViewNode items[]=new ViewNode[nodes.size()]; 
        
        for (int i=0;i<nodes.size();i++)
        {
            items[i]=createViewNode(nodes.get(i),size); 
        }
        
        return items; 
    }
    
    static public ViewNode createViewNode(ProxyNode pnode) 
    {
        ViewNode viewNode=new ViewNode(pnode.getVRL(),pnode.getType(),pnode.isComposite());
        // use setters 
        viewNode.setName(pnode.getName());
        viewNode.setComposite(pnode.isComposite());
        viewNode.setBusy(pnode.isBusy());
        viewNode.setResourceLink(pnode.isResourceLink());
        viewNode.setMimeType(pnode.getMimeType());
        //this.aliasVrl=pnode.getAliasVRL();  
        
        //Experimental: Prefetch Target Link!
        if (viewNode.isResourceLink())
        {
            try
            {
                viewNode.setTargetVrl(pnode.getTargetVRL());
            }
            catch (VlException e)
            {
               // Global.warnPrintln(this,"Couldn't prefetch LinkTarget:"+pnode);
              //  Global.warnPrintln(this,"Exception="+e); 
                // error: keep as null; 
            }
        }
        
        initIcons(viewNode,pnode,ViewNode.DEFAULT_ICON_SIZE);
        
        return viewNode; 
    }
    
    static public ViewNode createViewNode(ProxyNode pnode,int defaultIconSize)
    {
        ViewNode viewNode=createViewNode(pnode);
        initIcons(viewNode, pnode,defaultIconSize);
        return viewNode;
    }
    
    static private void initIcons(ViewNode viewNode,ProxyNode pnode,int iconSize)
    {
        viewNode.setIcon(ViewNode.DEFAULT_ICON,pnode.getDefaultIcon(iconSize,false));
        viewNode.setIcon(ViewNode.SELECTED_ICON,pnode.getDefaultIcon(iconSize,true));
        viewNode.setIconSize(iconSize);
    }
    
   
    
}
