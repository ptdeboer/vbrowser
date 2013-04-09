/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: ResourceTreeModel.java,v 1.1 2012/11/18 13:20:35 piter Exp $  
 * $Date: 2012/11/18 13:20:35 $
 */ 
// source: 

package nl.uva.vlet.gui.vb2.oldtree;

import java.util.Hashtable;
import java.util.Map;


import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import nl.piter.ptk.ui.vb2.UIGlobal;
import nl.piter.ptk.ui.vb2.model.UIModel;
import nl.piter.ptk.ui.vb2.model.ViewItem;
import nl.piter.ptk.ui.vb2.proxy.ProxyLocator;

/**
 * @author P.T. de Boer
 */

public class ResourceTreeModel extends DefaultTreeModel 
{
    private static final long serialVersionUID = -8867159295018514115L;

    // === Instance === 
    
    private UIModel uIModel = null;
    
    public ResourceTreeModel(UIModel _viewModel, TreeNode root, boolean asksForAllowedChildren)
    {
        super(root, asksForAllowedChildren);
        this.uIModel = _viewModel;
    }

    public UIModel getViewModel()
    {
        return this.uIModel;
    }

    public ViewItem getRootViewItem()
    {
        ResourceTreeNode treeRoot = (ResourceTreeNode) getRoot();

        return treeRoot.getViewItem();
    }

    public void setChilds(ResourceTreeNode targetNode, ViewItem[] items)
    {
        setChilds(targetNode, items, false);
    }

    public void addChilds(ResourceTreeNode targetNode, ViewItem[] items)
    {
        setChilds(targetNode, items, true);
    }

    protected synchronized void setChilds(ResourceTreeNode targetNode, ViewItem childs[], boolean append)
    {
        // possible background thread: 

        ResourceTreeNode[] rtnodes = null;
        boolean changed = false;

        if ((targetNode.isPopulated() == false) || (append == false))
        {
            clearSubNode(targetNode);
            changed = true;
        }

        if (childs != null)
        {
            rtnodes = new ResourceTreeNode[childs.length];
            // Process the directories
            for (int i = 0; (childs != null) && (i < childs.length); i++)
            {
                ViewItem iconItem = childs[i];
                if (iconItem != null)
                {
                    ProxyLocator childLoc = iconItem.getVRL();
                    ResourceTreeNode rtnode = null;

                    // If child already added:
                    // merge the two subsequent calls to setChilds,
                    // just update the pnode with the same name !

                    if ((rtnode = targetNode.getNode(childLoc)) != null)
                    {
                        rtnode.setViewItem(iconItem);
                        continue; // child already exists;
                    }

                    try
                    {
                        // add node, but do not fire event yet; 
                        rtnodes[i] = addSubNode(targetNode, iconItem);
                        changed = true;
                    }
                    catch (Exception e)
                    {
                        debug("Exception:" + e);
                        e.printStackTrace();
                    }
                }
            }
        }

        targetNode.setPopulated(true);
        
        if (changed)
        {
            // complete subtree has been updated! 
            uiFireStructureChanged(targetNode); 
        }
    }
    
    /** Master method to add a (sub) node */
    private ResourceTreeNode addSubNode(ResourceTreeNode parentNode, ViewItem iconItem)
    {
        ResourceTreeNode rtnode = new ResourceTreeNode(parentNode.getResourceTree(),iconItem);
      
        // it now has at least one node: 
        parentNode.setAllowsChildren(true);
        parentNode.add(rtnode);
        
        return rtnode;
    }

    private void clearSubNode(ResourceTreeNode subNode)
    {
        // remove previous children, might alread been removed
        subNode.removeAllChildren();
    }

    public boolean deleteNode(ResourceTreeNode node)
    {
        return deleteSubNode(node); 
    }
    
    private boolean deleteSubNode(ResourceTreeNode node)
    {
        //debug("deleteSubNode:"+node); 
        
        // Remove from tree
        ResourceTreeNode parent = (ResourceTreeNode) node.getParent();
        
        // if parent==null the node is already deleted 
        if (parent == null)
            return false; 
        
        int childIndex=parent.getIndex(node); 
        if (childIndex<0)
            return false;
        
        parent.remove(childIndex);
        
        fireNodeRemoved(parent,childIndex,node);
        
        return true; 
    }
    
  
    
    // ========================================================================
    // Fire Events: 
    // ========================================================================
    
    public void uiFireStructureChanged(final ResourceTreeNode node)
    {
      // Check UI Thread: 
      if (UIGlobal.isGuiThread() == false)
      {
          Runnable createTask = new Runnable()
          {
              public void run()
              {
                  uiFireStructureChanged(node);
              }
          };

          UIGlobal.swingInvokeLater(createTask);
          return;
      }
      
      // Update Tree. nodeStuctureChanged will post an Event !
      nodeStructureChanged(node);
      ResourceTree resourceTree = node.getResourceTree();      
      // also the tree size has been changed
      resourceTree.notifySizeChange();
      
    }
    
    protected void fireNodeRemoved(ResourceTreeNode parent,int childIndex,ResourceTreeNode node)
    {
        TreeNode[] parentPath = parent.getPath(); 
        
        int indices[]=new int[1];
        indices[0]=childIndex; 
        ResourceTreeNode childs[]=new ResourceTreeNode[1];
        childs[0]=node;
        
        //nodeStructureChanged(parent);
        // does not work ? 
        this.fireTreeNodesRemoved(parent,parentPath,indices,childs);
    }
    
    protected void fireValueChanged(ResourceTreeNode node, ViewItem item)
    {   
        // this.fireTreeNodesChanged(node,node.createTreePath(),, path, childIndices, children);
        
        // fire name change event :
        valueForPathChanged(node.createTreePath(), item);
    }

    private void debug(String msg)
    {
        Global.debugPrintln(this, msg);
    }

}
