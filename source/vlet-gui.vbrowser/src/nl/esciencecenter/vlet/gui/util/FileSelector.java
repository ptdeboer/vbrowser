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

package nl.esciencecenter.vlet.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.tree.ResourceTree;

/**
 * Class that extend the JDIalog class. It can open a save or open dialog. User
 * has to set the type before to open the dialog.
 * 
 * @author kboulebiar
 */
public class FileSelector extends javax.swing.JDialog
{
    /** save dialog or open dialog */
    public final static int SAVE_DIALOG = 1;

    public final static int OPEN_DIALOG = 2;

    private int typeDialog;

    private FileSelectorController controller;

    // panel North
    JTextField locationTextField;

    // panel center
    JPanel horizontalSplitPane;

    JScrollPane treeResourceScrollPane;

    JTabbedPane resourceTabbedPane;

    ResourceTree resourceTree;

    // Panel SOUTH
    JButton bpvalid;

    JButton bpCancel;

    JButton bpRefresh;

    public String currentLocation = null;

    /**
     * Constructor for modal dialogs
     * 
     * @param uiPlatform
     */
    public FileSelector()
    {
        super();
        // Create Controller before initGUI (is needed by ResourceTree)
        controller = new FileSelectorController(this);

        initGUI();
        initDialogParameter();
    }

    /**
     * Initialize general value about this Dialog
     */
    public void initDialogParameter()
    {
        setModal(true);
        setAlwaysOnTop(true);

        try
        {

            setRoot(null);
            currentLocation = "";
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "init Error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Init components and organize the layout
     */
    private void initGUI()
    {
        try
        {
            if (typeDialog == SAVE_DIALOG)
                this.setTitle("Save Dialog");
            else
                this.setTitle("Open File Dialog");

            this.setPreferredSize(new Dimension(570, 450));

            locationTextField = new JTextField();
            locationTextField.setText(currentLocation);
            if (typeDialog == SAVE_DIALOG)
                locationTextField.setEditable(true);
            else
                locationTextField.setEditable(false);

            // set Preferred Width for the GTK/Window LAF!
            locationTextField.setPreferredSize(new java.awt.Dimension(450, 28));

            Panel panNorth = new Panel();
            panNorth.add(new JLabel("Selected file:"));
            panNorth.add(locationTextField);

            resourceTree = new ResourceTree(this.controller);
            resourceTree.setPreferredSize(new java.awt.Dimension(2000, 2000));

            treeResourceScrollPane = new JScrollPane();
            treeResourceScrollPane.setPreferredSize(new java.awt.Dimension(300, 300));
            treeResourceScrollPane.setViewportView(resourceTree);

            resourceTabbedPane = new JTabbedPane();
            resourceTabbedPane.setPreferredSize(new java.awt.Dimension(300, 300));
            resourceTabbedPane.setAutoscrolls(true);
            resourceTabbedPane.setDoubleBuffered(true);
            resourceTabbedPane.setFocusCycleRoot(true);
            resourceTabbedPane.addTab("Resource", null, treeResourceScrollPane, null);

            JPanel panSouth = new JPanel();

            bpvalid = new JButton("Valid");
            bpCancel = new JButton("Cancel");
            bpRefresh = new JButton("Refresh");

            panSouth.add(bpvalid);
            panSouth.add(bpCancel);
            panSouth.add(bpRefresh);

            this.getContentPane().setLayout(new BorderLayout());
            this.getContentPane().add(panNorth, BorderLayout.NORTH);
            this.getContentPane().add(resourceTabbedPane, BorderLayout.CENTER);
            this.getContentPane().add(panSouth, BorderLayout.SOUTH);

            addAction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addAction()
    {
        bpvalid.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                validAction();
                closeDialog();
            }
        });

        bpCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                currentLocation = null;
                closeDialog();
            }
        });

        bpRefresh.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // KAMEL:I have to call a refresh method
                try
                {
                    System.out.println("REFRESH");
                    controller.refresh();
                }
                catch (VrsException e1)
                {
                    e1.printStackTrace();
                }

            }
        });
    }

    public void validAction()
    {
        currentLocation = locationTextField.getText();
    }

    public void setRoot(VRL vrl)
    {
        // redirect to controller:
        controller.setRoot(vrl);
    }

    public void openDialog()
    {
        pack();
        setVisible(true);
    }

    public void closeDialog()
    {
        setVisible(false);
    }

    public void disposeDialog()
    {
        // this.resourceTree.dispose();
        this.dispose();
    }

    public int getTypeDialog()
    {
        return typeDialog;
    }

    public void setTypeDialog(int typeDialog)
    {
        this.typeDialog = typeDialog;
        if (typeDialog == SAVE_DIALOG)
        {
            this.setTitle("Save Dialog");
            locationTextField.setEditable(true);
            bpvalid.setText("Save");
        }
        else
        {
            this.setTitle("Open File Dialog");
            locationTextField.setEditable(false);
            bpvalid.setText("Open");
        }
    }

    public String getCurrentLocation()
    {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation)
    {
        this.currentLocation = currentLocation;
    }
}
