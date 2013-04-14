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

package nl.esciencecenter.vbrowser.vb2.ui.browser;

import nl.esciencecenter.vbrowser.vb2.ui.actionmenu.Action;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeEvent;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeEventNotifier;

/** 
 * Delegated Action Handler class for the Proxy Browser. 
 * Encapsulates Copy, Paste, Create, Delete, Drag & Drop. 
 * 
 * @author Piter T. de Boer 
 *
 */
public class ProxyActionHandler
{
    private ProxyBrowser proxyBrowser;

    public ProxyActionHandler(ProxyBrowser proxyBrowser)
    {
        this.proxyBrowser=proxyBrowser; 
    }

    public void handlePaste(Action action,ViewNode node)
    {
        System.err.printf("*** Paste On:%s\n",node); 
    }

    public void handleCopy(Action action,ViewNode node)
    {
        System.err.printf("*** Copy On:%s\n",node);
    }

    public void handleCopySelection(Action action,ViewNode node)
    {
        System.err.printf("*** Copy Selection:%s\n",node);
    }

    public void handleDeleteSelection(Action action,ViewNode node)
    {
        System.err.printf("*** Delete Selection: %s\n",node);
    }

    public void handleCreate(Action action, ViewNode node)
    {
        System.err.printf("*** Create: %s::%s\n",node,action.getActionMethodString());
        
        ProxyNodeEventNotifier.getInstance().scheduleEvent(
                    ProxyNodeEvent.createChildAddedEvent(
                            node.getVRI(),
                            node.getVRI().appendPath(
                                    "/node" + ProxyNode.newID())));
    }

    public void handleDelete(Action action, ViewNode node)
    {
        System.err.printf("*** Delete On:%s\n",node); 
        
        ProxyNodeEventNotifier.getInstance().scheduleEvent(
                ProxyNodeEvent.createChildDeletedEvent(null,
                        node.getVRI()));
    }

}
