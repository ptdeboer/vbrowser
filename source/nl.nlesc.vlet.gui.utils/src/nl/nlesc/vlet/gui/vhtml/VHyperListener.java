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

package nl.nlesc.vlet.gui.vhtml;

import java.awt.Container;
import java.net.URL;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.HyperLinkListener;
import nl.nlesc.vlet.gui.viewers.IMimeViewer;
import nl.nlesc.vlet.gui.viewers.ViewerEvent;
import nl.nlesc.vlet.vrl.VRL;

/**
 * Handles link events from the htmlPane.
 * Forwards event to MasterBrowser if present, or else the (Parent)Viewer
 * the htmlPane is embedded in.  
 * 
 * @author P.T. de Boer
 */
public class VHyperListener implements HyperlinkListener
{
    /** Associated Viewer */   
    private HyperLinkListener destination=null; // (Optional) master browser to inform the events 
    private IMimeViewer viewerSource=null;       // (Optional) Viewer which is the source of the events 

    /**
     * Creates a new HyperLinkListenener. 
     * The events come from the IViewer and are reported back to 
     * the MasterBrowser. This could be a parent browser: VBrowser
     * or the IViewer itself, in case of an standalone HTML window. 
     * 
     * @param viewer    The Viewer which is the source of the events
     * @param browser   The MasterBrowser which receives the events
     */
    public VHyperListener(IMimeViewer viewer,HyperLinkListener browser)
    {
        this.destination=browser; 
        this.viewerSource=viewer; 
    }

    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            // get HTML Pane : 
            VHTMLEditorPane pane = (VHTMLEditorPane) e.getSource();
            
            if (e instanceof HTMLFrameHyperlinkEvent)
            {
                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument doc = (HTMLDocument) pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            }
            else
            {
                try
                {
                    URL url=e.getURL();

                    VRL loc=null;
                        
                    
                    if (url!=null) 
                    {
                        // I) url: 
                        Debug("uri="+url); 
                        loc=new VRL(url); 
                    }
                    else
                    {
                        // resolve (relative) link 
                         
                       String uriStr=e.getDescription(); 
                    
                       Debug("uriStr="+uriStr);
                       
                       HTMLDocument doc = (HTMLDocument) pane.getDocument();
                       
                       loc=resolve(doc.getBase(),uriStr); 
                    }
                    Debug(">>> New VRL="+loc);
                     
                    /*
                     * Inform the MasterBrowser of the event 
                     */
                    if (destination!=null)
                    {
                        Container comp = pane.getParent();
                        
                        // Let MasterBroswer handle the link event.
                        destination.notifyHyperLinkEvent(ViewerEvent.createHyperLinkEvent(viewerSource,loc)); 
                    }
                    else
                       Debug("*** Warning: No MasterBrowser for viewer:"+viewerSource); 
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
    }

    private void Debug(String string)
    {
        
    }

    private VRL resolve(URL base, String uriStr) throws Exception
    {
        VRL loc = new VRL(base).resolve(uriStr);
        return loc;
    }

  
    
}