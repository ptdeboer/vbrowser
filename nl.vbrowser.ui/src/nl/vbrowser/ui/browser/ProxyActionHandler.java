package nl.vbrowser.ui.browser;

import nl.vbrowser.ui.actionmenu.Action;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.proxy.ProxyNode;
import nl.vbrowser.ui.proxy.ProxyNodeEvent;
import nl.vbrowser.ui.proxy.ProxyNodeEventNotifier;

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
