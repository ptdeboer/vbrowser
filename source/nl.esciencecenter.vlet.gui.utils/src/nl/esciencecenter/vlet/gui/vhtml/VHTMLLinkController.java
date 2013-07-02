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

package nl.esciencecenter.vlet.gui.vhtml;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import nl.esciencecenter.vlet.gui.GuiSettings;

public class VHTMLLinkController extends HTMLEditorKit.LinkController
{
    private static final long serialVersionUID = 251294012651731298L;
    
    private JEditorPane htmlPane;

    VHTMLLinkController(JEditorPane c)
    {
        this.htmlPane=c; 
    }
    
    
    public void mousePressed(final MouseEvent event)
    {
        if (checkPopup(event))
            return; 
            
        super.mousePressed(event);
    }

    public void mouseReleased(MouseEvent event)
    {
        super.mousePressed(event);
    }

    public void mouseClicked(MouseEvent event)
    { 
        // only at mouse press, not click: 
        //if (checkPopup(event))
        //    return; 
        
        super.mouseClicked(event);
    }

    private boolean isPopupClick(MouseEvent event)
    {
        return GuiSettings.isPopupTrigger(event); 
    }


    public void mouseMoved(MouseEvent event)
    {
//        if(isHTMLPane(event.getComponent()) && isPopupHref(getHref(event)))
//        {
//            event.consume();
//            return;
//        }
        super.mouseMoved(event);
    }

    boolean checkPopup(final MouseEvent event)
    {
        if (isHTMLPopup(event)==false)
            return false; 
     
        final String href = getHref(event);
//        if(isPopupHref(href))
//        {
//            event.consume();
//            showPopup(event,href);
//        }
//        
        event.consume();
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                showPopup(event,href);
            }
        });
        
        return true; 
    }

    boolean isHTMLPopup(MouseEvent event)
    {
        return isPopupClick(event) && isHTMLPane(event.getComponent());
    }

    boolean isHTMLPane(Component component)
    {
        if ((component instanceof JEditorPane)==false)
            return false;
        
        return (  ((JEditorPane)component).getDocument() instanceof HTMLDocument); 
    }

    public String getHref(MouseEvent event)
    {
        JEditorPane editor = (JEditorPane)event.getSource();
        HTMLDocument doc = (HTMLDocument)editor.getDocument();

        Point pt = new Point(event.getX(), event.getY());
        javax.swing.text.Element e = doc.getCharacterElement(editor.viewToModel(pt));
        AttributeSet a = e.getAttributes();
        AttributeSet anchor = (AttributeSet) a.getAttribute(HTML.Tag.A);

        if (anchor==null)
            return null; 
        
        Object hrefVal = anchor.getAttribute(HTML.Attribute.HREF);
        
        return (String)hrefVal; 
    }

//    boolean isPopupHref(String href)
//    {
//        return true; 
//        // return href != null && href.startsWith("popup:");
//    }

    void showPopup(MouseEvent reason, String targetHref)
    {
        if (htmlPane instanceof VHTMLEditorPane)
        {
            ((VHTMLEditorPane)this.htmlPane).showPopup(reason,targetHref);
        }
        else
        {
//            JPopupMenu popup = new JPopupMenu();
//            popup.add("Item 1");
//            popup.add("Item 2");
//            popup.show(reason.getComponent(),reason.getX(),reason.getY());
        }
    }

}
