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

package nl.esciencecenter.vlet.gui.viewers.grid.jobmonitor;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.ui.widgets.NavigationBar;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.panels.resourcetable.ResourceTable;
import nl.esciencecenter.vlet.gui.viewers.ViewerPlugin;

/** 
 * Simple Job Status viewer. 
 * 
 * Uses VJS and VJob interface. Doesn't know anything from implemenation. 
 * 
 * @author Piter T. de Boer. 
 */
public class JobMonitor extends ViewerPlugin
{
    private static final long serialVersionUID = -7576499812836308887L;

    public static String ACTION_REFRESH_ALL = "refresh";
    public static String ACTION_RELOAD = "reload";
    public static String ACTION_START = "start";
    public static String ACTION_STOP = "stop";
    public static String ACTION_OPEN_JOB = "openJob";

    /** .vljids File Type */
    public static String mimetypes[] =
    {
        // typos:
        "application/vlemed/jobids", "application/vlemed-jobids",
        // new non vlemed:
        "application/glite-jobids", 
    };

    private JMenuBar menuBar;
    private JScrollPane jobTableSP;
    private JMenu helpMenu;
    private JMenuItem stopMi;
    private JMenuItem startMi;
    private JMenuItem refeshMi;
    private JMenu viewMenu;
    private JPanel jobMonitorPanel;
    private JPanel toolTopPanel;
    private JPanel menuBarPanel;
    private JMenu mainJobMenu;
    private JPanel topPanel;
    private NavigationBar locationToolbar;
    private JobMonitorController controller;
    private ResourceTable jobTable;
    //TableMouseListener mouseListener;
    
    public JobMonitor()
    {
        // needed for viewer registry.
        init(false); // bean object. Do not initialize;
    }
    
    /**
     * This method should return an instance of this class which does NOT
     * initialize it's GUI elements. This method is ONLY required by Jigloo if
     * the superclass of this class is abstract or non-public. It is not needed
     * in any other situation.
     */
    public static Object getGUIBuilderInstance()
    {
        return new JobMonitor(Boolean.FALSE);
    }

    /**
     * This constructor is used by the getGUIBuilderInstance method to provide
     * an instance of this class which has not had it's GUI elements initialized
     * (ie, initGUI is not called in this constructor).
     */
    public JobMonitor(Boolean initGui)
    {
        super();
        init(initGui);
    }
    
    public void init(boolean initGui)
    {
        if (initGui)
        {
            this.controller = new JobMonitorController(this);
            initGui();
            initListeners();
            controller.initUpdater(getJobStatusDataModel()); 
        }
    }
    
    protected JobStatusDataModel getJobStatusDataModel()
    {
    	return (JobStatusDataModel)this.jobTable.getModel(); 
    }
    
    protected void initListeners()
    {
        //this.mouseListener=new TableMouseListener(this.jobTable); 
        //jobTable.addMouseListener(mouseListener);
        // Add listeners *after* Table+Model has been created ! 
        jobTable.getHeaderModel().addListDataListener(controller.getHeaderModelListener()); 
        jobTable.setPopupMenu(new JobMonitorMenu(this.controller));
        // install mouselistener in background ScrollPanel!
        jobTableSP.addMouseListener(jobTable.getMouseListener()); 
    }
    
    @Override
    public String[] getMimeTypes()
    {
        return mimetypes;
    }

    @Override
    public void disposeViewer()
    {
        if (this.controller != null)
            this.controller.dispose();

        this.controller = null;
        this.jobTable = null;
    }

    @Override
    public String getName()
    {
        return "JobMonitor";
    }

    @Override
    public void initViewer()
    {
        init(true); 
    }

    @Override
    public void stopViewer()
    {
        this.controller.stopViewer();
    }

    @Override
    public void updateLocation(VRL loc) throws VrsException
    {
        this.controller.updateLocation(loc);
    }

    public boolean isTool()
    {
    	return false;
    }
    
    public boolean getAlwaysStartStandalone()
    {
        return true;
    }
    
    protected void initGui()
    {
        // JPanel has aldready a layout
        BorderLayout thisLayout = new BorderLayout();
        this.setLayout(thisLayout);
        this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
        this.setPreferredSize(new java.awt.Dimension(900, 300));

        {
            topPanel = new JPanel();
            this.add(topPanel, BorderLayout.NORTH);
            BorderLayout topPanelLayout = new BorderLayout();
            topPanel.setLayout(topPanelLayout);
            topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            topPanel.setPreferredSize(new java.awt.Dimension(896, 61));
            {
                {
                    menuBarPanel = new JPanel();
                    BoxLayout menuBarPanelLayout = new BoxLayout(menuBarPanel, javax.swing.BoxLayout.X_AXIS);
                    menuBarPanel.setLayout(menuBarPanelLayout);
                    topPanel.add(menuBarPanel, BorderLayout.NORTH);
                    menuBar = new JMenuBar();
                    menuBarPanel.add(menuBar);
                    {
                        mainJobMenu = new JMenu();
                        menuBar.add(mainJobMenu);
                        mainJobMenu.setText("JobMonitor");
                        {
                            startMi = new JMenuItem();
                            mainJobMenu.add(startMi);
                            startMi.setText("Start");
                            startMi.setActionCommand(ACTION_START);
                            startMi.addActionListener(controller);
                        }
                        {
                            stopMi = new JMenuItem();
                            mainJobMenu.add(stopMi);
                            stopMi.setText("Stop");
                            stopMi.setActionCommand(ACTION_STOP);
                            stopMi.addActionListener(controller);
                        }
                    }
                    {
                        viewMenu = new JMenu();
                        menuBar.add(viewMenu);
                        viewMenu.setText("View");
                        {
                            refeshMi = new JMenuItem();
                            viewMenu.add(refeshMi);
                            refeshMi.setText("Refresh");
                            refeshMi.setActionCommand(ACTION_REFRESH_ALL);
                            refeshMi.addActionListener(this.controller);
                        }
                        {
                            JMenuItem item = new JMenuItem();
                            viewMenu.add(item);
                            item.setText("Reload");
                            item.setActionCommand(ACTION_RELOAD);
                            item.addActionListener(this.controller);
                        }

                    }
                    {
                        helpMenu = new JMenu();
                        menuBar.add(helpMenu);
                        helpMenu.setText("Help");
                    }

                }
                {
                    toolTopPanel = new JPanel();
                    BoxLayout toolTopPanelLayout = new BoxLayout(toolTopPanel, javax.swing.BoxLayout.X_AXIS);
                    toolTopPanel.setLayout(toolTopPanelLayout);
                    topPanel.add(toolTopPanel, BorderLayout.CENTER);
                    {
                        locationToolbar = new NavigationBar(NavigationBar.LOCATION_ONLY);
                        toolTopPanel.add(locationToolbar);
                        locationToolbar.addTextFieldListener(controller);
                        // no up down back, etc.
                        locationToolbar.setEnableNagivationButtons(false);
                        locationToolbar.addNavigationButtonsListener(controller);
                    }
                }
            }
        }
        {
            jobMonitorPanel = new JPanel();
            BorderLayout jobMonitorPanelLayout = new BorderLayout();
            jobMonitorPanel.setLayout(jobMonitorPanelLayout);
            this.add(jobMonitorPanel, BorderLayout.CENTER);
            {
                jobMonitorPanel.add(getJobTableSP(), BorderLayout.CENTER);
            }
        }
    }

    public ResourceTable getJobTable()
    {
        if (jobTable == null)
        {
            // empty model:
            JobStatusDataModel model = new JobStatusDataModel(controller);
            UIPresentation pres = model.getPresentation();
            jobTable = new ResourceTable(model, pres);

            // jobTable.setPresentation(model.getPresentation());
            // jobTable.setPopupMenu(new ReplicaPopupMenu(this.controller));
            // Presentation pres = replicaTable.getPresentation();
        }

        return this.jobTable;
    }

    private JScrollPane getJobTableSP()
    {
        if (jobTableSP == null)
        {
            jobTableSP = new JScrollPane();
            jobTableSP.setViewportView(this.getJobTable());
        }
        return jobTableSP;
    }

    public void updateLocationBar(VRL loc)
    {
        this.locationToolbar.setLocationText("" + loc,false);

    }
    
    // Table/Model 
    
    protected ResourceTable getResourceTable()
    {
        return this.jobTable; 
    }
    
    protected JobStatusDataModel getJobMonitorDataModel()
    {
        return (JobStatusDataModel) getResourceTable().getModel();
    }

	public NavigationBar getNavigationBar()
	{
		return this.locationToolbar;
	}
	
	
    
}
