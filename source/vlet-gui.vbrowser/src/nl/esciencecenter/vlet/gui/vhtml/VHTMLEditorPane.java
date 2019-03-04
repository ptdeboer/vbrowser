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

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;


/**
 * Default HTML viewer. 
 * Uses Java internal 3.2 HTML parser. 
 * Only used for backup purposes as the default HTML viewer is the Lobo viewer, 
 * However that is a GPL plugin this might be not always be available. 
 */
public class VHTMLEditorPane extends JEditorPane
{
    private static final long serialVersionUID = 667938516906888816L;
    
    
    public VHTMLEditorPane()
    {
        
    }
    
    // The code is copied from the original JEditorPane as is it buggy
    // and still doesn't work correctly 
    //@Override
    public void scrollToReference2(String reference)
    {
        Document d = getDocument();

        if (d instanceof HTMLDocument)
        {
            HTMLDocument doc = (HTMLDocument) d;
            HTMLDocument.Iterator iter = doc.getIterator(HTML.Tag.A);
            for (; iter.isValid(); iter.next())
            {
                AttributeSet a = iter.getAttributes();
                String nm = (String) a.getAttribute(HTML.Attribute.NAME);
                //UIGlobal.debugPrintln(this, ">>> Checking name:" + nm);

                if ((nm != null) && (nm.compareTo(reference) == 0))
                {

                   // Global.debugPrintln(this, ">>> Found Match:" + nm);

                    // found a matching reference in the document.
                    try
                    {
                        Rectangle r = modelToView(iter.getStartOffset());
                        if (r != null)
                        {
                            // the view is visible, scroll it to the
                            // center of the current visible area.
                            Rectangle vis = getVisibleRect();
                            // r.y -= (vis.height / 2);
                            r.height = vis.height;
                            
                            //Global.debugPrintln(this, ">>> New ScrollRect=:" + r);
                            
                            scrollRectToVisible(r);

                            break;
                        }
                    }
                    catch (BadLocationException ble)
                    {
                        UIManager.getLookAndFeel().provideErrorFeedback(this);
                    }
                }
            }
        }
    }

    public void showPopup(MouseEvent reason, String targetHref)
    {
        JPopupMenu popup = new JPopupMenu();
        popup.add("Open");
        popup.add("Open in VBrowser");
        popup.add("Copy Location");
        popup.add("ref:"+targetHref);
        
        popup.show(reason.getComponent(),reason.getX(),reason.getY());
    }

}
