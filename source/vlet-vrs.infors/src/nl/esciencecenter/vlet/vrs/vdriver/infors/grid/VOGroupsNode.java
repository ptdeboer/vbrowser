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

package nl.esciencecenter.vlet.vrs.vdriver.infors.grid;

import java.util.ArrayList;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException;
import nl.esciencecenter.vlet.util.bdii.BdiiService;
import nl.esciencecenter.vlet.util.bdii.BdiiUtil;
import nl.esciencecenter.vlet.util.bdii.ServiceInfo;
import nl.esciencecenter.vlet.util.bdii.StorageArea;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;
import nl.esciencecenter.vlet.vrs.vdriver.infors.CompositeServiceInfoNode;
import nl.esciencecenter.vlet.vrs.vdriver.infors.InfoConstants;
import nl.esciencecenter.vlet.vrs.vrms.ResourceFolder;

public class VOGroupsNode extends CompositeServiceInfoNode<VONode>
{
    private StringList cachedVOs;

    public VOGroupsNode(GridNeighbourhood parent, VRSContext vrsContext)
    {
        super(vrsContext, parent.createChildVRL(InfoConstants.VOGROUPS_FOLDER_NAME));
        this.logicalParent = parent; // setLogicalParent(parent);
    }

    @Override
    public String getResourceType()
    {
        return InfoConstants.VOGROUPS_FOLDER_TYPE;
    }

    public String getIconURL()
    {
        return "vogroups-128.png";
    }

    public String getMimeType()
    {
        return null;
    }

    public String[] getAttributeNames()
    {
        StringList list = new StringList(); // super.getAttributeNames());
        // remove mime type:
        // list.remove(VAttributeConstants.ATTR_MIMETYPE);
        list.add(InfoConstants.ATTR_CONFIGURED_VOS);

        return list.toArray();
    }

    public Attribute getAttribute(String name) throws VrsException
    {
        if (name.equals(InfoConstants.ATTR_CONFIGURED_VOS))
        {
            return new Attribute(name, this.getConfiguredVOs().toString(","));
        }

        return super.getAttribute(name);
    }

    public synchronized VONode[] getNodes() throws VrsException
    {
        // Alway update:
        updateVOGroups();

        return this._getNodes();
    }

    protected VONode[] _getNodes() throws VrsException
    {
        if ((childNodes == null) || (childNodes.size() <= 0))
            return null;

        synchronized (childNodes)
        {
            VONode arr[] = new VONode[childNodes.size()];
            // todo cannot convert to T[] array
            return childNodes.toArray(arr);
        }
    }

    private synchronized void updateVOGroups() throws VRLSyntaxException
    {
        // checkVOs:
        StringList newVOs = getConfiguredVOs();

        if ((cachedVOs != null) && cachedVOs.compare(newVOs) == 0)
            return;

        // recreate:
        VONode voGroups[] = new VONode[newVOs.size()];

        for (int i = 0; i < newVOs.size(); i++)
        {
            VONode group = VONode.createVOGroup(this, newVOs.get(i));
            voGroups[i] = group;
        }

        this.setChilds(voGroups);
        this.cachedVOs = newVOs;

    }

    public String[] getResourceTypes()
    {
        return new String[] { InfoConstants.VO_TYPE };
    }

    public synchronized StringList getConfiguredVOs()
    {
        // check context:
        String voStr = this.vrsContext.getStringProperty(VletConfig.PROP_USER_CONFIGURED_VOS);
        StringList voList = new StringList();

        if (voStr != null)
        {
            String[] strs = voStr.split(",");

            if (strs != null)
                voList.add(strs);
        }

        // initialize user default:
        String currentVO = this.vrsContext.getVO();
        if (currentVO != null)
        {
            boolean added = voList.add(currentVO, true);
            // update VO
            if (added)
                this.vrsContext.setUserProperty(VletConfig.PROP_USER_CONFIGURED_VOS, voList.toString(","));
        }

        return voList;
    }

    protected synchronized void setConfiguredVOs(StringList vos)
    {
        this.vrsContext.setUserProperty(VletConfig.PROP_USER_CONFIGURED_VOS, vos.toString(","));
    }

    protected synchronized VONode addVO(String vo)
    {
        if (vo == null)
            return null;

        VONode voGroup = findVOGroup(vo);

        if (voGroup != null)
            return voGroup;

        // check context:
        String voStr = this.vrsContext.getStringProperty(VletConfig.PROP_USER_CONFIGURED_VOS);
        StringList voList = new StringList();

        if (voStr != null)
        {
            String[] strs = voStr.split(",");

            if (strs != null)
                voList.add(strs);
        }

        boolean added = voList.add(vo, true);
        // update VO
        if (added)
            this.vrsContext.setUserProperty(VletConfig.PROP_USER_CONFIGURED_VOS, voList.toString(","));
        
        try
        {
            this.updateVOGroups();
        }
        catch (Exception e)
        {
            
        }
        
        return findVOGroup(vo);
    }

    public synchronized VONode findVOGroup(String vo)
    {
        for (VONode vogroup : this.childNodes)
        {
            if (StringUtil.equalsIgnoreCase(vogroup.getName(), vo))
                return vogroup;
        }

        return null;
    }

    public synchronized ResourceFolder createSEFolderForVO(VRL logicalParent, String vo) throws VrsException
    {
        infoPrintf("createSEFolderForVO(): For vo:%s\n", vo);
        
        BdiiService bdii = getBdiiService();
        ArrayList<StorageArea> sas = bdii.getSRMv22SAsforVO(vo);

        infoPrintf(" - got # %d Storage Areas for vo:%s\n", sas.size(), vo);

        VRL logVrl = logicalParent.appendPath("StorageElements (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);

        resF.setName(logVrl.getBasename());

        for (StorageArea sa : sas)
        {
            infoPrintf(" - checking storage area: %s\n", sa);

            ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Populating StorageElements folder for VO:"
                    + vo, -1);

            VRL seLoc = sa.getVOStorageLocation();
            // resourceFolder will update logical location

            if (seLoc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action aborted:%s\n", monitor.getCurrentSubTaskName());
                    break;
                }

                AttributeSet set;
                
                ServiceInfo srm = sa.getSRMV22Service();

                // get/check service attributes. 
                //set = bdii.getServiceAttributes(srm); 
                set=srm.getInfoAttributes();

                infoPrintf(" - - number of info attributes: %d\n",set.size());

                // attr = new VAttribute("serviceType", srm.getServiceType());
                // set.put(attr);

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext,
                        logVrl.appendPath(sa.getHostname()), 
                        seLoc,
                        false);
                
                seNode.setName(sa.getHostname() + ":" + sa.getStoragePath());
                                // Not Editable !
                seNode.setEditable(false);
                seNode.setInfoAttributes(set);
                resF.addNode(seNode);

            }
        }

        resF.setIconURL("servercluster.png");
        resF.setEditable(false);
        resF.setPresentation(getSRMPresentation());

        return resF;
    }

    protected Presentation getSRMPresentation()
    {
        return getDefaultPresentation();
    }

    protected Presentation getDefaultPresentation()
    {
        // First Get Defaults!:
        Presentation pres = Presentation.getPresentationFor(VRS.SRM_SCHEME, null, VRS.RESOURCE_INFO_TYPE,true);
        // Mess around:
        StringList names = new StringList( VAttributeConstants.ATTR_ICON, 
                VAttributeConstants.ATTR_RESOURCE_TYPE,
                VAttributeConstants.ATTR_HOSTNAME, 
                VAttributeConstants.ATTR_PORT, 
                VAttributeConstants.ATTR_PATH );

        pres.setPreferredContentAttributeNames(names);
        return pres;
    }

    public synchronized ResourceFolder createLFCFolderForVO(VRL logicalParent, String vo) throws VrsException
    {
        BdiiService bdii = getBdiiService();
        ArrayList<ServiceInfo> lfcs = bdii.getLFCsforVO(vo);

        VRL logVrl = logicalParent.appendPath("Logical File Catalogs (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);
        resF.setName(logVrl.getBasename());

        if (lfcs == null)
        {
            resF.setEditable(false);
            return resF;
        }

        infoPrintf("Got #%s Storage LFC servers for vo: %s\n", lfcs.size(), vo);
        for (ServiceInfo lfc : lfcs)
        {
            ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Populating StorageElements folder for VO:"
                    + vo, -1);

            VRL lfcLoc = new VRL(lfc.toVRL().replacePath("/grid"));
            // resourceFolder will update logical location

            if (lfcLoc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action Aborted:%s\n", monitor.getCurrentSubTaskName());
                    break;
                }
                
                AttributeSet set = new AttributeSet();
                set = lfc.getInfoAttributes();
                // attr = new VAttribute("serviceType", lfc.getServiceType());
                // set.put(attr);
                // attr = new VAttribute("serviceVersion", "?");
                // set.put(attr);

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext, logVrl.appendPath(lfcLoc.getHostname()),
                        lfcLoc, false);
                seNode.setName("LFC " + lfcLoc.getHostname());
                seNode.setInfoAttributes(set);

                resF.addNode(seNode);
            }
        }

        resF.setEditable(false);
        resF.setIconURL("servercluster.png");
        resF.setPresentation(getDefaultPresentation());
        return resF;
    }

    public ResourceFolder createWMSFolderForVO(VRL logicalParent, String vo) throws VrsException
    {
        BdiiService bdii = getBdiiService();
        ArrayList<ServiceInfo> wmss = bdii.getWMSServiceInfos(vo);

        VRL logVrl = logicalParent.appendPath("WMS Services (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);
        resF.setName(logVrl.getBasename());

        if (wmss == null)
        {
            resF.setEditable(false);
            return resF;
        }

        infoPrintf("Got #%d WMS services for vo:%s\n", wmss.size(), vo);
        for (ServiceInfo wms : wmss)
        {
            ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Populating WMS folder for VO:" + vo, -1);

            VRL loc = wms.toVRL();
            // resourceFolder will update logical location

            if (loc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action Aborted:%s\n", monitor.getCurrentSubTaskName());
                    break;
                }
                
                AttributeSet set = new AttributeSet();

                // attr = new VAttribute("serviceType", wms.getServiceType());
                // set.put(attr);
                // attr = new VAttribute("serviceVersion", "?");
                // set.put(attr);
                set = wms.getInfoAttributes();

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext, logVrl.appendPath(loc.getHostname()), loc,
                        false);
                seNode.setInfoAttributes(set);
                seNode.setName("WMS " + loc.getHostname());
                resF.addNode(seNode);
            }
        }

        resF.setEditable(false);
        resF.setIconURL("servercluster.png");
        resF.setPresentation(getDefaultPresentation());

        return resF;
    }

    protected BdiiService getBdiiService() throws VrsException
    {
        return BdiiUtil.getBdiiService(this.vrsContext); 
    }
    
    public ResourceFolder createLBFolderForVO(VRL logicalParent, String vo) throws VrsException
    {
        BdiiService bdii = getBdiiService();  
        ArrayList<ServiceInfo> lbss = bdii.getLBServiceInfosForVO(vo);

        VRL logVrl = logicalParent.appendPath("LB Services (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);
        resF.setName(logVrl.getBasename());

        if (lbss == null)
        {
            resF.setEditable(false);
            return resF;
        }

        infoPrintf("Got #%d WMS services for vo:%s\n", lbss.size(), vo);
        for (ServiceInfo lbs : lbss)
        {
            ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Populating WMS folder for VO:" + vo, -1);

            VRL loc = lbs.toVRL();
            // resourceFolder will update logical location

            if (loc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action Aborted:%s\n", monitor.getCurrentSubTaskName());
                    break;
                }
                
                AttributeSet set = new AttributeSet();

                // attr = new VAttribute("serviceType", wms.getServiceType());
                // set.put(attr);
                // attr = new VAttribute("serviceVersion", "?");
                // set.put(attr);
                set = lbs.getInfoAttributes();

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext, logVrl.appendPath(loc.getHostname()), loc,
                        false);
                seNode.setInfoAttributes(set);
                seNode.setName("LB " + loc.getHostname());
                resF.addNode(seNode);
            }
        }

        resF.setEditable(false);
        resF.setIconURL("servercluster.png");
        resF.setPresentation(getDefaultPresentation());

        return resF;
    }

    private void infoPrintf(String msg, Object... args)
    {
        VletConfig.getRootLogger().infoPrintf(this+msg, args);
    }

    @Override
    public synchronized VNode createNode(String type, String name, boolean force)
            throws ResourceTypeNotSupportedException
    {
        if ((type == null) || (type.compareTo(InfoConstants.VO_TYPE) != 0))
            throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException("Can not create resource type:" + type);

        return this.addVO(name);
    }

    public synchronized boolean deleteVOGroup(VONode groupNode)
    {
        return _delete(groupNode);
    }

    private boolean _delete(VNode node)
    {
        synchronized (childNodes)
        {
            this.delSubNode(node);

            // Update VOs:
            StringList newVos = new StringList();

            // new configured vo list:
            for (VONode vogroup : childNodes)
                newVos.add(vogroup.getName());

            this.setConfiguredVOs(newVos);

            return true;
        }
    }

    @Override
    public synchronized boolean delNode(VNode node)
    {
        return _delete(node);
    }

}
