/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.gui.vhtml;


import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

public class VHTMLKit extends HTMLEditorKit
{
    private static final long serialVersionUID = 4777118976470906864L;

    public static class LinkListener implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent e) 
        {
            JEditorPane pane = (JEditorPane) e.getSource();
            try 
            {
                pane.setPage(e.getURL());
            }
            catch (Throwable t) 
            {
                t.printStackTrace();
            }
        }
    }
   
    // ========================================================================
    // ========================================================================
    
    private VHTMLLinkController linkController;
    
    public void install(JEditorPane c) 
    {
        super.install(c);
        replaceLinkController(c);
    }
    
    public void deinstall(JEditorPane c)
    {
        c.removeMouseListener(linkController);
        c.removeMouseMotionListener(linkController);
    }

    void replaceLinkController(JEditorPane c)
    {
        {
            EventListener[] listeners = c.getListeners(MouseListener.class);
            for( int i=0; i<listeners.length; ++i)
            {
                if(listeners[i] instanceof HTMLEditorKit.LinkController)
                {
                    c.removeMouseListener((MouseListener)listeners[i]);
                }
            }
        }
        
        {
            EventListener[] listeners = c.getListeners(MouseMotionListener.class);
            for( int i=0; i<listeners.length; ++i)
            {
                if(listeners[i] instanceof HTMLEditorKit.LinkController)
                {
                    c.removeMouseMotionListener((MouseMotionListener)listeners[i]);
                }
            }
        }
        
        linkController = new VHTMLLinkController(c);
        c.addMouseListener(linkController);
        c.addMouseMotionListener(linkController);
    }

    LinkController createLinkController()
    {
        return new LinkController();
    }
    
}
