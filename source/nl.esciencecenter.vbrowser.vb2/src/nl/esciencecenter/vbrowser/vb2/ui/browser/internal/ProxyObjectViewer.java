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

package nl.esciencecenter.vbrowser.vb2.ui.browser.internal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.ui.image.ImagePane;
import nl.esciencecenter.ptk.ui.image.ImagePane.ImageWaiter;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.EmbeddedViewer;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * Implementation of an Image Viewer.<br>
 */
public class ProxyObjectViewer extends EmbeddedViewer
{
    // private ImageIcon icon=null;
    JScrollPane scrollPane;
    JPanel mainPanel;

    ViewNode viewNode;
    private JLabel iconLabel; 
    
    // private JLabel imageLabel; // store image in Label Component

    public ProxyObjectViewer(ViewNode node)
    {
        this.viewNode=node; 
    }
    
    @Override
    public String[] getMimeTypes()
    {
        return null; 
    }

    public void initGui()
    {
        {
            this.setSize(800, 600);

            BorderLayout thisLayout = new BorderLayout();
            this.setLayout(thisLayout);
            // this.setLayout(null); // absolute layout

            {
                this.scrollPane = new JScrollPane();
                this.scrollPane.setSize(800, 600);
                this.getContentPanel().add(scrollPane, BorderLayout.CENTER); // addToRootPane(imagePane,BorderLayout.CENTER);
                {
                    this.mainPanel = new JPanel();
                    scrollPane.setViewportView(mainPanel);
                    mainPanel.setLayout(new BorderLayout());
                }
            }

            this.setToolTipText(getName());
            
            {
                this.iconLabel=new JLabel(); 
                mainPanel.add(iconLabel,BorderLayout.CENTER); 
            }
        }

    }

    @Override
    public void initViewer()
    {
        initGui();
    }
    
    @Override
    public void stopViewer()
    {
    }

    @Override
    public void disposeViewer()
    {
    }

    @Override
    public String getName()
    {
        // remove html color codes:
        return "ObjectViewer";
    }

    @Override
    public String getViewerClass()
    {
        return this.getClass().getCanonicalName(); 
    }

    
    @Override
    public void startViewer() 
    {
        try
        {
            update(viewNode);
        }
        catch (Exception e)
        {
            this.notifyException("Failed to update Resource:"+viewNode,e);
        }
    }


    public void update(ViewNode node) throws Exception 
    {
        if (node==null)
        {
            return; 
        }

        notifyBusy(true);

        try
        {
            iconLabel.setIcon(node.getIcon());
            iconLabel.setText(node.getName());
        }
        catch (Exception e)
        {
            throw new VrsException(e);
        }
        finally
        {
            notifyBusy(false);
        }
    }

   
}
