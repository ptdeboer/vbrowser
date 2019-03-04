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

package nl.esciencecenter.vlet.vfs.srm;

import gov.lbl.srm.v22.stubs.ArrayOfTMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.TAccessLatency;
import gov.lbl.srm.v22.stubs.TDirOption;
import gov.lbl.srm.v22.stubs.TFileStorageType;
import gov.lbl.srm.v22.stubs.TFileType;
import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.TOverwriteMode;
import gov.lbl.srm.v22.stubs.TRetentionPolicy;
import gov.lbl.srm.v22.stubs.TRetentionPolicyInfo;
import gov.lbl.srm.v22.stubs.TReturnStatus;
import gov.lbl.srm.v22.stubs.TStatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import nl.esciencecenter.glite.lbl.srm.SRMClient;
import nl.esciencecenter.glite.lbl.srm.SRMClientV1;
import nl.esciencecenter.glite.lbl.srm.SRMClientV2;
import nl.esciencecenter.glite.lbl.srm.SRMException;
import nl.esciencecenter.glite.lbl.srm.status.SRMPutRequest;
import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException;
import nl.esciencecenter.vlet.exception.ResourceCreationFailedException;
import nl.esciencecenter.vlet.exception.ResourceNotFoundException;
import nl.esciencecenter.vlet.exception.ResourceTypeMismatchException;
import nl.esciencecenter.vlet.grid.globus.GlobusUtil;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.util.bdii.BdiiUtil;
import nl.esciencecenter.vlet.util.bdii.ServiceInfo;
import nl.esciencecenter.vlet.util.bdii.StorageArea;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSClient;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskMonitor;
import nl.esciencecenter.vlet.vrs.vfs.FileSystemNode;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VFileActiveTransferable;
import nl.esciencecenter.vlet.vrs.vfs.VFileSystem;
import nl.esciencecenter.vlet.vrs.vrl.VRLUtil;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

/**
 * SRMFileSystem. 
 * 
 * @author Piter T. de Boer, Spiros Koulouzis.
 */
public class SRMFileSystem extends FileSystemNode implements VFileActiveTransferable
{
    static Random fileRandomizer = new Random();

    private static ClassLogger logger;

    static
    {
        ClassLogger srmLogger = ClassLogger.getLogger(SRMClientV2.class);
        SRMClientV2.setLogger(srmLogger);
        // srmLogger.setLevelToDebug();
        logger = ClassLogger.getLogger(SRMFileSystem.class);
        // logger.setLevelToDebug();
    }

    /**
     *  Update location with SRM V2.2 port, if known 
     */
    public static VRL resolveToV22SRM(VRSContext context, VRL loc)
    {
        try
        {
            ServiceInfo se = BdiiUtil.getBdiiService(context).getSRMv22ServiceForHost(loc.getHostname());
            if (se != null)
            {
                loc = VRLUtil.replacePort(loc, se.getPort());
                return loc;
            }
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.WARN, e, "Couldn't resolve SRM V2.2 location:%s\n", loc);
        }
        // return default
        return loc;
    }

    public static boolean isGFTP(VRL location)
    {
        String scheme = VRLUtil.resolveScheme(location.getScheme());

        if (StringUtil.equalsIgnoreCase(scheme, VRS.GFTP_SCHEME))
            return true;

        return false;
    }

    public static boolean isSRM(VRL location)
    {
        String scheme = VRLUtil.resolveScheme(location.getScheme());

        if (StringUtil.equalsIgnoreCase(scheme, VRS.SRM_SCHEME))
            return true;

        return false;
    }

    public static void clearClass()
    {
        // ;
    }

    // =======================================================================
    //
    // =======================================================================

    private SRMClientV2 srmClient = null;

    long connectionTimeout;

    private VFSClient vfsClient;

    private SRMClientV1 srmClientV1;

    // Spiros: Dead variable
    // private String srmVersionInfo;

    public SRMFileSystem(VRSContext context, ServerInfo info, VRL vrl) throws VrsException
    {
        super(context, info);
        connect(false);
    }

    public SRMFileSystem(VRSContext context, ServerInfo info, VRL vrl, boolean connect) throws VrsException
    {
        super(context, info);
        // get socket timeout:
        this.connectionTimeout = context.getConfigManager().getSocketTimeOut();
        if (connect)
            connect(false);
    }

    @Override
    public VFSNode openLocation(VRL loc) throws VrsException
    {
        connect(false);
        VFSNode node = getPath(loc);

        return node;
    }

    public void connect() throws VrsException
    {
        connect(false);
    }

    // private static Object globalSRMConnectMutex=new Object();

    public void connect(boolean closeFirst) throws VrsException
    {
        logger.infoPrintf("Connecting (closeFirst=%s):%s\n", closeFirst, this);

        if (srmClient != null)
            if (closeFirst)
                disconnect();
            else
                return; // already connected
        GridProxy prox = this.vrsContext.getGridProxy();

        int port = getPort();
        if (port <= 0)
            port = VFS.getSchemeDefaultPort(VFS.SRM_SCHEME);
        String host = getHostname();

        if (prox.isValid() == false)
            throw new nl.esciencecenter.vlet.exception.AuthenticationException("Invalid grid proxy");
        try
        {
            // check SRM V2 Client:
            srmClient = new SRMClientV2(host, port, false);

            // Update Socket Connection time out.
            srmClient.setConnectionTimeout(this.vrsContext.getConfigManager().getSocketTimeOut());

            // Update Reqeust timeout (post socket setup)
            int defVal = vrsContext.getConfigManager().getServerRequestTimeOut();
            // optional server info value:
            int srmReqTimeOut = this.getServerInfo().getIntProperty(SRMFSFactory.ATTR_SRM_REQUEST_TIMEOUT, defVal);
            if (srmReqTimeOut <= 0)
                srmReqTimeOut = defVal;

            srmClient.setSRMRequestTimeout(srmReqTimeOut);

            // Must Update used Globus Credential !
            srmClient.setGlobusCredential(GlobusUtil.getGlobusCredential(prox));
            // NOW connect!
            srmClient.connect();

            // SrmPingResponse response = srmClient.srmPing();
            // this.srmVersionInfo = response.getVersionInfo();

        }
        catch (Exception e)
        {
            throw convertException("Could not connect to:" + host + ":" + port, e);
        }
    }

    public void disconnect()
    {
        logger.infoPrintf("disconnecting:%s\n", this);

        if (this.srmClient != null)
        {
            try
            {
                this.srmClient.disconnect();
            }
            catch (SRMException e)
            {
                logger.warnPrintf("Error when closing connection:%s\n", e);
            }
        }

        this.srmClient = null;
    }

    public boolean isConnected()
    {
        if (srmClient != null)
            return true;

        return false;
    }

    private String getDefaultVOHome() throws VrsException
    {
        String vo = this.getVRSContext().getVO();
        // get storage area path suitable for writing by this VO.
        if (StringUtil.isEmpty(vo))
            throw new nl.esciencecenter.vlet.exception.AuthenticationException(
                    "No VO specified in current context. Please enable VO authentication. ");

        String voPath = getSAPathFor(this.getServerVRL(), vo);

        return voPath;
    }

    private String getSAPathFor(VRL saVRL, String vo) throws VrsException
    {
        ArrayList<StorageArea> sa = BdiiUtil.getBdiiService(getVRSContext()).getVOStorageAreas(vo, saVRL.getHostname(),
                false);

        if ((sa != null) && (sa.size() > 0))
            return sa.get(0).getStoragePath();

        return null;
    }

    private boolean getTrySRMV1()
    {
        return false;
    }

    // =======================================================================
    // VRS/VFS Methods
    // =======================================================================

    // custom VFSClient:
    public VFSClient getVFSClient()
    {
        if (this.vfsClient == null)
            vfsClient = new VFSClient(this.getVRSContext());

        return vfsClient;
    }

    @Override
    public VFile createFile(VRL pathVRL, boolean force) throws VrsException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("createFile:" + this, -1);

        // create empty file
        try
        {
            String fullpath = resolvePathString(pathVRL.getPath());
            // file may NOT exist yet:
            SRMOutputStream outps = createNewOutputStream(monitor, fullpath, force);

            byte bytes[] = new byte[0];
            outps.write(bytes);
            outps.close();

            // refetch or check
            // return new SRMFile(srmClient, new SrmDetails);

            return this.getFile(pathVRL);

        }
        catch (IOException e)
        {
            throw new NestedIOException("Couldn't create file:" + pathVRL, e);
        }

    }

    // @Override
    public VDir createDir(VRL vrl, boolean force) throws VrsException
    {
        return createDir(vrl.getPath(), force);
    }

    public VDir createDir(String name, boolean force) throws VrsException
    {
        debug("createDir: " + name + " " + force);

        String fullpath = resolvePathString(name);

        if (mkdir(fullpath, force))
        {
            return (VDir) getPath(fullpath);
        }

        throw new nl.esciencecenter.vlet.exception.ResourceCreationFailedException("Could not create " + fullpath);
    }

    @Override
    public VDir newDir(VRL dirVrl) throws VrsException
    {
        return new SRMDir(this, dirVrl);
    }

    @Override
    public VFile newFile(VRL fileVrl) throws VrsException
    {
        return new SRMFile(this, fileVrl.getPath());
    }

    public VFSNode getPath(VRL pathVrl) throws VrsException
    {
        logger.debugPrintf("SRMFileSystem:getPath():%s\n", pathVrl);

        String path = pathVrl.getPath();
        // normalize and absolute path
        path = this.resolvePathString(path);

        TMetaDataPathDetail detail;

        if (path.startsWith("/~"))
        {
            // returnes absolute path
            String voPath = getDefaultVOHome();
            if (voPath != null)
                path = path.replaceFirst("/~", voPath);
            else
                path = path.replaceFirst("/~", "/");
        }

        detail = queryPath(path);

        if (detail == null)
        {
            throw new nl.esciencecenter.vlet.exception.VrsResourceException("Query failed. Result is NULL for:" + path);
        }

        // New SRM at RuG return NULL Type !
        if ((detail.getType() != null) && (detail.getType() == (TFileType.DIRECTORY)))
        {
            DirQuery dirQ = DirQuery.parseDirQuery(pathVrl);
            logger.debugPrintf("DirQuery=%s\n", dirQ);
            return new SRMDir(this, detail, dirQ);
        }
        else
        {
            // default to file: (blind mode!)
            return new SRMFile(this, detail);
        }
    }

    public VRL createPathVRL(String path, DirQuery dirQuery)
    {
        VRL pathVrl = new VRL(SRMFSFactory.SRM_SCHEME, getHostname(), getPort(), path);

        if (dirQuery != null)
        {
            String qstr = dirQuery.toString();

            logger.debugPrintf(">>> OLD VRL=%s\n", pathVrl);
            pathVrl = VRLUtil.replaceQuery(pathVrl, qstr);
            logger.debugPrintf(">> NEW VRL=%a\n", pathVrl);
        }

        return pathVrl;
    }

    public TMetaDataPathDetail queryPath(String path) throws VrsException
    {
        ArrayList<TMetaDataPathDetail> details = queryPaths(new String[]
        { path });

        if ((details == null) || (details.size() <= 0))
            return null; // empty/non existant

        return details.get(0);
    }

    /** Master method to query a set of paths */
    public ArrayList<TMetaDataPathDetail> queryPaths(String paths[]) throws VrsException
    {
        try
        {
            // VRS needs FULL details: Always query for it:
            return this.srmClient.queryPaths(paths, true);
        }
        catch (SRMException e)
        {
            // if NOT any other Exception then assume resource not found !

            // if (e.getErrorType() == SRMException.ErrorType.GENERAL_ERROR)
            // throw new ResourceNotFoundException("Couldn't query path(s):"
            // + flatten(paths), e);

            // Warning!!! If you use equals(obj) then it is dangerous to compare
            // completely different objects. Safer to use this way
            if (e.hasReturnStatusCode(TStatusCode.SRM_FAILURE))
            {
                throw new ResourceNotFoundException("Couldn't query path(s):" + flatten(paths), e);
            }

            throw convertException("Couldn't query path(s):\n" + flatten(paths), e);
        }
        catch (MalformedURIException e)
        {
            throw new VRLSyntaxException("Invalid URI(s):" + flatten(paths), e);
        }
    }

    // List Single Path
    public VFSNode[] list(String path) throws VrsException
    {
        return listPaths(new String[]
        { path }, true, -1, -1);
    }

    // List Single Path
    public VFSNode[] list(String path, int offset, int count) throws VrsException
    {
        return listPaths(new String[]
        { path }, true, offset, count);
    }

    /**
     * list Paths. Master Method
     * 
     * Level=1 => directory itself, level=2 => including contents and specify
     * fullDetails for file details!
     * 
     * Method merges different paths.
     */
    public VFSNode[] listPaths(String[] paths, boolean fullDetails, int offset, int count) throws VrsException
    {
        debug("listPaths:" + flatten(paths));

        VFSNode nodeArr[] = null;

        ArrayList<TMetaDataPathDetail> details;
        try
        {
            details = srmClient.listPaths(paths, fullDetails, offset, count);
        }
        catch (SRMException e)
        {
            throw convertException("Couldn't query path(s):\n" + flatten(paths), e);
        }
        catch (MalformedURIException e)
        {
            throw new VRLSyntaxException(e);
        }

        // ===
        // Flatten Details !
        // Merge all results into single VFSNode array
        // ===

        Vector<VFSNode> nodes = new Vector<VFSNode>();

        for (TMetaDataPathDetail detail : details)
        {
            if (detail == null)
                continue; // has happened...

            ArrayOfTMetaDataPathDetail pathDetail = detail.getArrayOfSubPaths();
            TMetaDataPathDetail[] topLevel = null;
            TMetaDataPathDetail[] allPathDetails = null;

            if (pathDetail != null)
            {
                topLevel = pathDetail.getPathDetailArray();
                allPathDetails = getAllSubLevels(topLevel);

                // empty dir:
                if (allPathDetails == null)
                    continue; // return null;

                for (int i = 0; i < allPathDetails.length; i++)
                {
                    VFSNode newNode;

                    debug("Path: " + allPathDetails[i].getPath());
                    TFileType type = allPathDetails[i].getType();

                    debug("Node Type: " + ((type != null) ? type : "<NULL TYPE>"));

                    // New SRM at RuG Returns NULL Type !
                    if ((type != null) && (isDirectory(type)))
                    {
                        // dir
                        newNode = new SRMDir(this, allPathDetails[i], null);
                    }
                    else if ((type != null) && (isFile(type)))
                    {
                        // file
                        newNode = new SRMFile(this, allPathDetails[i]);
                    }
                    else
                    {
                        warn("Warning: Couldn't determine type of (assuming file!):" + format(allPathDetails[i]));
                        // Default to FILE ! (For blind mode).
                        newNode = new SRMFile(this, allPathDetails[i]);
                    }
                    nodes.add(newNode);
                }

                // Dir could be empty
                if ((allPathDetails == null) || (pathDetail == null))
                {
                    // nodeArr = new VFSNode[1];
                    // nodeArr[0] = new SRMDir(this, detail);
                    debug("Path: " + detail.getPath());
                    debug("Node Type: " + detail.getType().getValue());
                }

            }
        }

        if (nodes.size() <= 0)
            return null;

        nodeArr = new VFSNode[nodes.size()];
        nodeArr = nodes.toArray(nodeArr);

        return nodeArr;
    }

    protected boolean isDirectory(TFileType type)
    {
        return StringUtil.equalsIgnoreCase(type.getValue(), TFileType.DIRECTORY.getValue());
    }

    protected boolean isFile(TFileType type)
    {
        return StringUtil.equalsIgnoreCase(type.getValue(), TFileType.FILE.getValue());
    }

    private String format(TMetaDataPathDetail detail)
    {
        String str = " path                = " + detail.getPath() + "\n retention type    = " + detail.getType()
                + "\n file storage type = " + detail.getFileStorageType() + "\n size              = "
                + detail.getSize() + "\n owner permission  = " + detail.getOwnerPermission()
                + "\n group permission  = " + detail.getGroupPermission() + "\n other permission  = "
                + detail.getOtherPermission() + "\n status            = " + detail.getStatus().getExplanation()
                + "\n file locality     = " + detail.getFileLocality() + "\n retention policy  = "
                + detail.getRetentionPolicyInfo() + "\n retention policy  = " + detail.getCreatedAtTime();

        return str;
    }

    private TMetaDataPathDetail[] getAllSubLevels(TMetaDataPathDetail[] subLevel)
    {
        // empty directory!
        if (subLevel == null)
            return null;

        ArrayList<TMetaDataPathDetail> allSubLevels = new ArrayList<TMetaDataPathDetail>();

        List list = null;
        TMetaDataPathDetail[] arrayOfPathDetail;
        for (int i = 0; i < subLevel.length; i++)
        {
            if (!allSubLevels.contains(subLevel[i]))
            {
                allSubLevels.add(subLevel[i]);
            }

            if (subLevel[i].getArrayOfSubPaths() != null)
            {
                TMetaDataPathDetail[] subLevels = getAllSubLevels(subLevel[i].getArrayOfSubPaths().getPathDetailArray());

                if (subLevels != null)
                    list = Arrays.asList(subLevels);
            }

            if (list != null && !allSubLevels.containsAll(list))
            {
                allSubLevels.addAll(list);
            }
        }

        arrayOfPathDetail = new TMetaDataPathDetail[allSubLevels.size()];
        arrayOfPathDetail = allSubLevels.toArray(arrayOfPathDetail);
        return arrayOfPathDetail;
    }

    @Override
    public VFile activeTransferFrom(ITaskMonitor monitor, VFile targetFile, VRL remoteSourceLocation)
            throws VrsException
    {
        if (!(targetFile instanceof SRMFile))
            throw new ResourceTypeMismatchException("activeTransferFrom(): TargetFile must be a SRMFile:"+targetFile); 
        
        return doActiveTransfer(monitor, remoteSourceLocation, (SRMFile) targetFile);
    }

    @Override
    public VFile activeTransferTo(ITaskMonitor monitor, VFile sourceFile, VRL remoteTargetLocation) throws VrsException
    {
        if (!(sourceFile instanceof SRMFile))
            throw new ResourceTypeMismatchException("activeTransferTo(): SourceFile must be a SRMFile:"+sourceFile); 

        return doTransfer(monitor, (SRMFile) sourceFile, remoteTargetLocation);
    }

    @Override
    public ActiveTransferType canTransferFrom(VFile sourceFile, VRL remoteTargetLocation, StringHolder explanation)
            throws VrsException
    {
        return checkTransferLocation(remoteTargetLocation, explanation, false);
    }

    @Override
    public ActiveTransferType canTransferTo(VFile targetFile, VRL remoteSourceLocation, StringHolder explanation)
            throws VrsException
    {
        return checkTransferLocation(remoteSourceLocation, explanation, true);
    }

    public ActiveTransferType checkTransferLocation(VRL remoteLocation, StringHolder explanation, boolean isTarget)
    {
        // All SRM And GFTP locations can be both target and source when doing 3rd party transfers
        // (ignore whether location is target or source) but one of the should be a SRM File. 
        if (isGFTP(remoteLocation))
        {
            explanation.value = "SRM can handle GFTP locations.";
            return ActiveTransferType.ACTIVE_3RDPARTY;
        }

        if (isSRM(remoteLocation))
        {
            ActiveTransferType val = ActiveTransferType.NONE;

            //
            // Allow SRM <-> SRM copying on SAME server !
            // Currently SRM <-> SRM between difference servers don't work!
            //

            if ((isTarget) && VRLUtil.hasSameServer(this.getServerVRL(), remoteLocation))
            {
                // transfer from this to remote Location on same server !
                val = ActiveTransferType.ACTIVE_3RDPARTY;
            }
            else if ((isTarget == false) && VRLUtil.hasSameServer(this.getServerVRL(), remoteLocation))
            {
                // transfer from remoteLocation to same server (this)
                val = ActiveTransferType.ACTIVE_3RDPARTY;
            }
            else
            {
                val = ActiveTransferType.ACTIVE_3RDPARTY; // getAllowSRM2SRMThirdPartyTransfer();
            }

            if (val == ActiveTransferType.ACTIVE_3RDPARTY)
                explanation.value = "SRM to SRM transfer supported";
            else
                explanation.value = "SRM to SRM transfer currently not supported";

            return val;
        }

        explanation.value = "Unkown scheme. Cannot handle:" + remoteLocation.getScheme();

        return ActiveTransferType.NONE;
    }

  
    /** 
     * Actual transfer from SourceLocation to SRMFile. 
     * Source can be both a GridFTP file or a SRM file. 
     */
    protected VFile doActiveTransfer(ITaskMonitor monitor, VRL sourceLocation, SRMFile targetFile) throws VrsException
    {

        VFSClient vfs = getVFSClient();

        // ===
        // GFTP Upload to SRM !
        // ===

        if (isGFTP(sourceLocation))
        {
            monitor.logPrintf("Uploading GFTP file to SRM using Third Party copy from:\n - " + sourceLocation + "\n");

            VFile transportFile = this.doTransportTransfer(monitor, sourceLocation, targetFile);
            // Must Return SRM File here !
            return targetFile;
        }

        // ===
        // SRM (GFTP) -> to NEW SRM, must get new Transport URL !
        // ===

        if (isSRM(sourceLocation))
        {
            // SRM Source must exists!
            VFile srmSource = vfs.getFile(sourceLocation);

            if (srmSource instanceof SRMFile)
            {
                // Spiros: variable 'transportFile' not used
                // VFile transportFile =
                doTransportTransfer(monitor, ((SRMFile) srmSource).getTransportVRL(), targetFile);
                // Must Return SRM File !
                return targetFile;
            }
            else
            {
                // srm location should result in SRM File:
                throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException(
                        "SRM source location is not an SRM file:" + sourceLocation);
            }
        }

        // no methods left:
        throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException("Cannot handle third party transfer from:"
                + sourceLocation);

    }

    public void thirdPartyCopy(VRL[] source, VRL[] destination) throws VrsException
    {

        if (source.length != destination.length)
        {
            throw new VrsException("Source and destination VRL arrays must have the same length. Source length="
                    + source.length + ", Desination length=" + destination.length);
        }
        org.apache.axis.types.URI[] arrayOfSourceSURLs = new org.apache.axis.types.URI[source.length];
        org.apache.axis.types.URI[] arrayOfTargetSURLs = new org.apache.axis.types.URI[destination.length];

        TDirOption[] dirOptions = new TDirOption[source.length];

        try
        {
            for (int i = 0; i < arrayOfSourceSURLs.length; i++)
            {
                arrayOfSourceSURLs[i] = new org.apache.axis.types.URI(source[i].toString());
                arrayOfTargetSURLs[i] = new org.apache.axis.types.URI(destination[i].toString());

                dirOptions[i] = new TDirOption();
                if (existsDir(source[i]))
                {
                    dirOptions[i].setIsSourceADirectory(true);
                    dirOptions[i].setAllLevelRecursive(true);
                    dirOptions[i].setNumOfLevels(3);
                }
                else
                {
                    dirOptions[i].setIsSourceADirectory(false);
                    dirOptions[i].setAllLevelRecursive(false);
                    dirOptions[i].setNumOfLevels(1);
                }
            }

            TRetentionPolicyInfo targetFileRetentionPolicyInfo = new TRetentionPolicyInfo();
            targetFileRetentionPolicyInfo.setAccessLatency(TAccessLatency.ONLINE);
            targetFileRetentionPolicyInfo.setRetentionPolicy(TRetentionPolicy.OUTPUT);
            this.srmClient.srmCp(arrayOfSourceSURLs, arrayOfTargetSURLs, dirOptions, TOverwriteMode.ALWAYS,
                    targetFileRetentionPolicyInfo, TFileStorageType.PERMANENT);
        }
        catch (MalformedURIException e)
        {
            throw new VRLSyntaxException(e);
        }
        catch (SRMException e)
        {
            throw convertException("Failed to do third party SRM copy", e);
        }

    }

    protected VFile doTransfer(ITaskMonitor monitor, SRMFile sourceFile, VRL targetLocation) throws VrsException
    {
        // Source here is always a (Transport) GridFTP file. 
        
        VRL transportUrl = sourceFile.getTransportVRL();
        VFSClient vfs = getVFSClient();

        // ===
        // SRM (GFTP) -> GFTP, use default GFTP third party transfer:
        // ===

        if ((isGFTP(transportUrl)) && (isGFTP(targetLocation)))
        {
            // SRM sourceFile to GridFTP target location = transport file.  
            monitor.logPrintf("Delegating 3rd party transfer to GFTP destination:\n - " + targetLocation + "\n");

            // Check GridFTP FileSytem:
            VFile sourceTransportFile = vfs.newFile(transportUrl); 
            VFileSystem sourceVFS=sourceTransportFile.getFileSystem();
            if (sourceVFS instanceof VFileActiveTransferable)
            {
                debug("Invoking gftp->gftp transfer to:" + targetLocation);
                return ((VFileActiveTransferable) sourceVFS).activeTransferTo(monitor, sourceTransportFile,targetLocation);
            }

            // srm location should result in SRM File:
            throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException(
                    "GridFtp FileSysem is not a ThirdParty Transferable FileSystem:" + sourceVFS);

        }

        // SRM (GFTP) -> SRM, must get new Transport URL !

        if ((isGFTP(transportUrl)) && (isSRM(targetLocation)))
        {
            // New Location !
            VFile srmTarget = vfs.newFile(targetLocation);

            if (srmTarget instanceof SRMFile)
            {
                VFile transportFile = doTransportTransfer(monitor, transportUrl, (SRMFile) srmTarget);
                // return SRM Target !
                return srmTarget;
            }
            else
            {
                // srm location should result in SRM File:
                throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException("SRM Location is not an SRM file:"
                        + targetLocation);
            }
        }

        // no methods left:

        throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException("Cannot handle 3rd party transfer to:"
                + targetLocation);

    }

    /**
     * Do actual transport Copy from GridFTP source file to SRM TargetFile.  
     * Warning: Returns Transport file (TURL).
     * @return the Transported File which is a GridFTP File. 
     */
    protected VFile doTransportTransfer(ITaskMonitor monitor, VRL transportSourceVRL, SRMFile targetFile)
            throws VrsException
    {
        VFSClient vfs = getVFSClient();

        // Resolve source (Must be GFTP ! ) or compatible with SRM TargetFile
        // Transport VRL !
        VFile sourceTransportFile = vfs.newFile(transportSourceVRL);
        VFileSystem sourceTransportVFS=sourceTransportFile.getFileSystem();  
        
        if ((sourceTransportVFS instanceof VFileActiveTransferable) == false)
        {
            throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException(
                    "Transport FileSystem doesn't support active 3rd party transfers:" + sourceTransportVFS);
        }
        
        // 
        // Use REMOTE SRM FileSystem Object !
        SRMFileSystem targetFS = targetFile.getFileSystem();

        SRMPutRequest putRequest = targetFS.createPutRequest(monitor, targetFile.getPath(), true);
        VRL targetTransferVRL = new VRL(putRequest.getTurl(0).toString());

        // putRequest must have
        monitor.logPrintf("SRM: Initiating third party (gftp->gftp) transfer: \n" + " - from:" + sourceTransportFile
                + "\n" + " - to  :" + targetTransferVRL + "\n");
        {
            debug("Invoking gftp->gftp transfer to:" + targetTransferVRL);

            VFile resultFile = null;
            Exception transferEx = null;

            try
            {
                resultFile = ((VFileActiveTransferable) sourceTransportVFS).activeTransferTo(monitor,
                        sourceTransportFile,targetTransferVRL);
                debug("After activePartyTransfer, resulting (transport) file=" + resultFile);
            }
            catch (Exception e)
            {
                monitor.logPrintf("SRM: *** Error: 3rd party transfer failed. ***\n - Exception = %s\n", e);
                debug("*** TransferException:" + e);
                transferEx = e;
            }

            boolean succeeded = (transferEx == null);
            // ====
            // Always finalize !
            // ====

            try
            {
                if (succeeded)
                {
                    monitor.logPrintf("SRM: Finalizing file:\n - " + targetFile + "\n");
                    debug("finalizing SRMFile :" + targetFile);
                    debug("finalizing SURL[0] :" + putRequest.getSURLs().getUrlArray()[0]);
                }
                else
                {
                    monitor.logPrintf("SRM: Finalizing *failed* transfer:\n - " + targetFile + "\n");
                    debug("Cleanup: finalizing ***FAILED*** SRMFile :" + targetFile);
                    debug("Cleanup: finalizing ***FAILED*** SURL[0] :" + putRequest.getSURLs().getUrlArray()[0]);
                }
                targetFS.finalizePutRequest(putRequest, (transferEx == null));
            }
            catch (SRMException e)
            {
                if (transferEx != null)
                {
                    // Finalize failed after invalid
                    monitor.setException(transferEx);
                    throw convertException("SRM 3rd party copy failed. Finalizing PutRequest failed as well.",
                            transferEx);
                }
                else
                {
                    monitor.setException(e);
                    throw convertException("SRM finalizing PutRequest after successful transfer failed!", e);
                }
            }

            // ===
            // error handling
            // remove bogus file if transfer failed;
            // ===

            if (transferEx != null)
            {
                VFile bogusFile = targetFS.newFile(targetTransferVRL);

                if (bogusFile.exists())
                {
                    monitor.logPrintf("SRM: Cleaning up target file:\n - " + bogusFile + "\n");
                    try
                    {
                        bogusFile.delete();
                    }
                    catch (Exception e)
                    {
                        logger.warnPrintf("Deletion of bogus file failed:%s\n", bogusFile);
                    }
                    // continue:
                }

                throw new ResourceCreationFailedException("Couldn't write to remote resource:" + targetTransferVRL,
                        transferEx);
            }

            return resultFile;
        }

        // throw new
        // nl.uva.vlet.exception.ResourceTypeNotSupportedException("Cannot
        // handle 3rd party transfer to:"+targetTransferVRL);
    }

    public boolean pathExists(String path) throws VrsException
    {
        try
        {
            String aPath = queryPath(path).getPath();
            if (aPath == null)
                return false;

            return true;
        }
        catch (VrsException ex)
        {
            // filter resource not found exceptions
            if (ex instanceof ResourceNotFoundException)
                return false;

            debug("pathExists ex: " + ex.getMessage());
            String exstr = "" + ex.getMessage();
            if (exstr.contains("path does not exist for one or more files specified"))
            {
                debug("pathExists returning false ");
                return false;
            }

            throw ex;
        }
    }

    public long createModTime(TMetaDataPathDetail srmDetails)
    {
        if (srmDetails == null)
            return -1;

        Calendar cal = srmDetails.getLastModificationTime();

        if (cal != null)
            return cal.getTimeInMillis();

        return -1;
    }

    public int getUnixMode(TMetaDataPathDetail srmDetails)
    {
        // Might be permission denied!
        if (srmDetails == null)
            return 0;

        // Internal Error: Full Details missing!
        if (srmDetails.getOwnerPermission() == null)
        {
            warn("Warning: Owner permissions missing from TMetaDataPathDetails!");
            return 0;
        }

        String userStr = srmDetails.getOwnerPermission().getMode().getValue();
        int userInt = parsePermissionsString(userStr);

        String groupStr = srmDetails.getGroupPermission().getMode().getValue();
        int groupInt = parsePermissionsString(groupStr);

        String otherStr = srmDetails.getOtherPermission().getValue();
        int otherInt = parsePermissionsString(otherStr);

        return userInt * 64 + groupInt * 8 + otherInt;
    }

    public int parsePermissionsString(String permissions)
    {
        // make it case insensitive
        permissions = permissions.toUpperCase();
        int mode = 0x00;

        if (permissions.length() >= 1)
        {
            if (permissions.contains("R"))
                mode |= 0x04;

            if (permissions.contains("W"))
                mode |= 0x02;

            if (permissions.contains("X"))
                mode |= 0x01;
        }

        return mode;
    }

    public InputStream createInputStream(ITaskMonitor monitor, String path) throws VrsException
    {
        VRL tsvrl = getTransportVRL(monitor, path);
        return getVFSClient().openInputStream(tsvrl);
    }

    public VRL getTransportVRL(ITaskMonitor monitor, String path) throws VrsException
    {
        // use new bulk mode:
        VRL vrls[] = getTransportVRLs(monitor, new String[]
        { path });
        if ((vrls == null) || (vrls.length <= 0))
            return null;
        return vrls[0];
    }

    public VRL[] getTransportVRLs(ITaskMonitor monitor, String[] filePaths) throws VrsException
    {
        VrsException orgEx;

        URI[] surls;

        try
        {
            surls = this.srmClient.createURIArray(filePaths);
        }
        catch (SRMException e)
        {
            throw convertException("Failed to get transport URIs:\n" + flatten(filePaths), e);
        }

        try
        {
            // URI[] uris = surls.getUrlArray();
            URI[] turls = this.srmClient.getTransportURIs(surls);
            return createTransportVRLs(turls);
        }
        catch (Exception e)
        {
            orgEx = convertException("Failed to get transport URIs:\n" + flatten(filePaths), e);
        }

        // ==============================================
        // V1 compatibility ONLY for getTransportVRLs:
        // So SRMV1 replica can be read from.
        // ==============================================

        logger.warnPrintf("getTransportVRLs(): Exception while trying SRMClientV2:%s\n", orgEx);

        // ====
        // check for SRMClient v1 for hostname here!
        // ====
        if (getTrySRMV1() == false)
            throw orgEx;

        try
        {
            // URI[] uris = surls.getUrlArray();
            URI[] turls = getSRMV1Client().getTransportURIs(surls);
            return createTransportVRLs(turls);
        }
        catch (Exception e)
        {
            logger.warnPrintf("getTransportVRLs(): Exception while trying SRMClientV1():%s\n", e);
            logger.logException(ClassLogger.DEBUG, e, "--- SRMVlient V1 Exception stack trace ---\n");

            // throw SRM V2 exceptions:
            throw orgEx;
        }
    }

    private VRL[] createTransportVRLs(URI[] turls) throws VRLSyntaxException
    {
        VRL vrls[] = new VRL[turls.length];

        for (int i = 0; i < turls.length; i++)
            vrls[i] = new VRL(turls[i].toString());

        return vrls;
    }

    public boolean mkdir(String fullpath, boolean force) throws VrsException
    {
        try
        {
            return this.srmClient.mkdir(fullpath);
        }
        catch (SRMException e)
        {
            // exception handling:

            if (e.hasReturnStatusCode(TStatusCode.SRM_DUPLICATION_ERROR)
                    || e.hasReturnStatusCode(TStatusCode.SRM_INVALID_PATH))
            {
                if (!force)
                {
                    throw new nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException("Path already exists:" + fullpath,
                            e);
                }
                // fore==true check directory and keep it
                TMetaDataPathDetail details = this.queryPath(fullpath);
                if (isDirectory(details.getType()))
                {
                    return true;
                }
                // SRM_INVALID_PATH can be almost anything. So start
                // investigating
                if (e.getReturnStatusExplanation().contains("exists as a file"))
                {
                    throw new nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException(
                            "Path already exists but is a file:" + fullpath, e);
                }
                if (isFile(details.getType()))
                {
                    throw new nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException(
                            "Path already exists but is a file:" + fullpath, e);
                }
            }

            // exception handling:
            // if (e.getStatusCode() == TStatusCode.SRM_INVALID_PATH)
            // {
            // // inspect
            // if (e.getMessage().contains("exists as file"))
            // throw new nl.uva.vlet.exception.ResourceAlreadyExistsException(
            // "Path already exists as file:" + fullpath, e);
            //
            // if (force == false)
            // throw new nl.uva.vlet.exception.ResourceAlreadyExistsException(
            // "Path already exists:" + fullpath, e);
            //
            // // fore==true check directory and keep it
            // TMetaDataPathDetail details = this.queryPath(fullpath);
            //
            // if (isDirectory(details.getType()))
            // {
            // return true;
            // }
            //
            // throw new nl.uva.vlet.exception.ResourceAlreadyExistsException(
            // "Path already exists but is a file:" + fullpath, e);
            // }

            // default:
            throw convertException("Couldn't create new directory:" + fullpath, e);
        }
    }

    public boolean rmdir(String path, boolean recurse) throws VrsException
    {
        try
        {
            return this.srmClient.rmdir(path, recurse);
        }
        catch (SRMException e)
        {
            throw convertException("Failed to remove:" + createPathVRL(path, null), e);
        }
    }

    public boolean deleteFile(String path) throws VrsException
    {
        try
        {
            return this.srmClient.rm(path);
        }
        catch (SRMException e)
        {
            throw convertException("Failed to remove:" + createPathVRL(path, null), e);
        }
        catch (MalformedURIException e)
        {
            throw new VRLSyntaxException(e);
        }
    }

    public SRMPutRequest createPutRequest(ITaskMonitor monitor, String path, boolean overwrite) throws VrsException
    {

        URI surl;
        try
        {
            surl = this.srmClient.createPathSurlURI(path);

            URI[] surls = new URI[]
            { surl };

            // determin TOverwriteMode
            TOverwriteMode mode = null;
            if (overwrite)
            {
                mode = TOverwriteMode.ALWAYS;
            }
            else
            {
                mode = TOverwriteMode.NEVER;
            }
            // there is also a 3rd: TOverwriteMode.WHEN_FILES_ARE_DIFFERENT. Not
            // sure in which use case it might be useful
            return this.srmClient.srmCreatePutRequest(surls, mode);
        }
        catch (SRMException e)
        {
            VRL vrl = createPathVRL(path, null);

            // Fix for castor. Make sure that the correct exception is created
            if (e.hasReturnStatusCode(TStatusCode.SRM_DUPLICATION_ERROR) == false)
            {
                if (existsPath(vrl))
                {
                    throw convertException("Failed to create putrequest to:" + vrl, new SRMException(
                            "Path already exists.", new TReturnStatus(TStatusCode.SRM_DUPLICATION_ERROR,
                                    "File or Dir already exists"), null));
                }
            }

            throw convertException("Failed to create putrequest to:" + vrl, e);
        }
    }

    protected boolean finalizePutRequest(SRMPutRequest putRequest, boolean succeeded) throws SRMException
    {
        return this.srmClient.finalizePutRequest(putRequest, succeeded);
    }

    public SRMOutputStream createNewOutputStream(ITaskMonitor monitor, String path, boolean overwrite)
            throws VrsException
    {
        SRMOutputStream result = null;

        try
        {
            result = createNewOutputStreamImpl(monitor, path, overwrite);
        }
        catch (VrsException ex)
        {
            if ((ex instanceof ResourceAlreadyExistsException) && (overwrite) && getPath(path).isFile())
            {
                debug("File " + path + " exits will delete it");
                deleteFile(path);
                result = createNewOutputStreamImpl(monitor, path, overwrite);
            }
            else
            {
                throw ex;
            }
        }
        return result;
    }

    /**
     * Register NEW file path and create an outputstream to write to. The
     * specified path may not exist yet on the remote SRM Server.
     * 
     * @param minitor
     * 
     * @param path
     * @return
     * @throws VrsException
     *             , ResourceAlreadyExistsException if path already exists
     */
    public SRMOutputStream createNewOutputStreamImpl(ITaskMonitor monitor, String path, boolean overwrite)
            throws VrsException
    {
        debug(" - openOutputStream():" + path);

        SRMPutRequest putreq = createPutRequest(monitor, path, overwrite);

        SRMOutputStream result = null;

        result = createSrmOutputStream(putreq);

        return result;

    }

    /** Create SRMOutputStream from (GFTP) transfer VRL */
    private SRMOutputStream createSrmOutputStream(SRMPutRequest putreq) throws VrsException
    {

        debug("getToken: " + putreq.getToken());

        for (int i = 0; i < putreq.getSURLs().getUrlArray().length; i++)
        {
            debug("createSrmOutputStream():SURLs: " + putreq.getSURLs().getUrlArray()[i]);
        }

        for (int i = 0; i < putreq.getTURLs().getUrlArray().length; i++)
        {
            debug("createSrmOutputStream():TURLs: " + putreq.getTURLs().getUrlArray()[i]);
        }

        VRL turlVrl = new VRL(putreq.getTurl(0).toString());
        // use VRS:
        debug("createSrmOutputStream():uri=" + turlVrl);

        try
        {
            OutputStream outps = new VRSClient(this.vrsContext).openOutputStream(turlVrl);
            return new SRMOutputStream(putreq, outps, this);
        }
        catch (Exception e)
        {

            try
            {
                // must close Put Request !
                this.finalizePutRequest(putreq, false);
            }
            catch (SRMException e1)
            {
                logger.logException(ClassLogger.WARN, e1,
                        "Got exception while trying to finalize PutRequest after exception!\n");
            }

            throw convertException("Failed to created outputstream to Transport URL:" + turlVrl, e);
        }

    }

    public VRL mv(String path, String newPath) throws VrsException
    {

        try
        {
            boolean success = srmClient.mv(path, newPath);
            if (success)
            {
                return createPathVRL(newPath, null);
            }
        }
        catch (SRMException e)
        {
            throw convertException("Faild to move " + path + " to " + newPath, e);
        }
        return null;
    }

    // =====================================================
    //
    //
    // To Be Implemented;
    //
    //
    // =====================================================

    public void setUnixMode(VRL location, TMetaDataPathDetail srmDetails, int mode) throws VrsException
    {
        try
        {
            String srmPermitionString = VFS.modeToString(mode, false, false).toUpperCase();
            // srmPermitionString = srmPermitionString.replace('-', '_');

            this.srmClient.setPermissions(srmClient.createPathSurlURI(location.getPath()), srmDetails,
                    srmPermitionString);
        }
        catch (SRMException e)
        {
            convertException("Couldn't set permissions for: " + location, e);
        }
    }

    public VrsException convertException(String message, Exception cause)
    {
        logger.debugPrintf(message + " ex: %s\n", cause);

        // to be extended
        if (cause instanceof SRMException)
        {
            SRMException ex = (SRMException) cause;

            // switch (ex.getErrorType())
            // {
            // case CONNECTION_ERROR:
            // return new nl.uva.vlet.exception.VlConnectionException(message,
            // cause);
            // case AUTHENTICATION_ERROR:
            // return new nl.uva.vlet.exception.VlAuthenticationException(
            // message, cause);
            // case PATH_NOT_FOUND:
            // return new ResourceNotFoundException(message, cause);
            // case PERMISSION_DENIED:
            // return new nl.uva.vlet.exception.ResourceAccessDeniedException(
            // message, cause);
            // case PATH_ALREADY_EXISTS:
            // return new
            // nl.uva.vlet.exception.ResourceAlreadyExistsException(message,
            // cause);
            // default:
            // return new VlException("SRMException", message + "\nErrorcode="
            // + ex.getErrorType() + "\n" + ex.getMessage(), ex);
            // }

            if (ex.hasReturnStatusCode(TStatusCode.SRM_DUPLICATION_ERROR))
            {
                return new ResourceAlreadyExistsException(message, cause);
            }
            return new NestedIOException(message + "\n" + ex.getMessage(), ex);
        }

        String errorstr = cause.getMessage();

        if (errorstr == null)
            errorstr = "";

        if ((errorstr.contains("Connection refused")) || (errorstr.contains("No route to host")))

        {
            return new nl.esciencecenter.vlet.exception.ConnectionException(message + "\n"
                    + "Connection error. Server might be down or not reachable:" + this.getHostname() + "\n"
                    + "Reason:" + cause.getMessage(), cause);
        }

        // Check Standard Globus Exceptions:
        VrsException globusEx = GlobusUtil.checkException(message, cause);

        if (globusEx != null)
            return globusEx;

        // default:
        return new NestedIOException("SRMException:" + message, cause);
    }

    // ============================================================ //
    // =========================== MISC =========================== //
    // ============================================================ //

    private void debug(String msg)
    {
        logger.debugPrintf("%s\n", msg);
    }

    // private void error(String msg)
    // {
    // logger.errorPrintf("%s\n",msg);
    // }

    private void warn(String msg)
    {
        logger.warnPrintf("%s\n", msg);
    }

    public String flatten(String strs[])
    {
        if ((strs == null) || (strs.length <= 0))
            return "";

        // use StringList
        return new StringList(strs).toString("\"", "\n");
    }

    public String getBackendType() throws VrsException
    {
        try
        {
            return this.srmClient.getBackendType();
        }
        catch (SRMException e)
        {
            throw convertException("Couldn't get backend type.", e);
        }
    }

    public String getBackendVersion() throws VrsException
    {
        try
        {
            return this.srmClient.getBackendVersion();
        }
        catch (SRMException e)
        {
            throw convertException("Couldn't get backend type.", e);
        }
    }

    protected SRMClientV1 getSRMV1Client() throws VrsException
    {
        try
        {
            if (this.srmClientV1 == null)
            {
                this.srmClientV1 = SRMClient.createSRMClientV1(getHostname(), getPort(), true);

                // Update Connection time out.
                srmClientV1.setConnectionTimeout(this.vrsContext.getConfigManager().getSocketTimeOut());
                srmClientV1.setSRMRequestTimeout(this.vrsContext.getConfigManager().getServerRequestTimeOut());

            }
        }
        catch (Exception e)
        {
            throw convertException("Couldn't create SRMV1 Client for:" + getHostname(), e);
        }

        return srmClientV1;
    }

    public SRMClientV2 getSRMClient()
    {
        return this.srmClient;
    }

    public static ClassLogger getLogger()
    {
        return logger;
    }
}
