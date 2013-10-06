package nl.esciencecenter.vbrowser.vb2.ui.viewer.events;

import nl.esciencecenter.ptk.events.IEvent;
import nl.esciencecenter.vbrowser.vb2.ui.viewerplugin.ViewerPanel;

public class ViewerEvent implements IEvent<ViewerPanel>
{
    protected ViewerPanel source; 
    
    public ViewerEvent(ViewerPanel source)
    {
        this.source=source;
    }
    
    public ViewerPanel getSource()
    {
        return this.source;
    }

}
