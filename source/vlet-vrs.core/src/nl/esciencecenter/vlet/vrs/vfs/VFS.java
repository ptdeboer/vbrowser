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

package nl.esciencecenter.vlet.vrs.vfs;

import nl.esciencecenter.ptk.data.BooleanHolder;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.VResourceSystem;


/**
 * The Virtual File System Global Class.
 * <p>
 * Holds the VFS Global methods and constants.
 * 
 * @author P.T. de Boer
 */
public class VFS extends VRS
{
    // ========================================================================
    // Class Fields
    // ========================================================================

    /** A Default Filesystem supports Directories and Files */
    static String defaultChildTypes[] =
        { 
            DIR_TYPE, 
            FILE_TYPE 
        };

    // ========================================================================
    // Class Methods
    // ========================================================================

    /**
     * Translate Unix style file mode to VRS ACL list.
     * 
     * @see VACL
     */
    public static Attribute[][] convertFileMode2ACL(int mode, boolean isDir)
    {
        // Global.debugPrintf(VFS.class,"converting file mode: %o\n",mode);

        int numEntries = 3; // user, group,other
        int numAttrs = 5; // name, readable,writeable, exe/passable, <misc>
        Attribute acl[][] = new Attribute[numEntries][];
        String execName = null;

        String setUID[] = { "-", VACL.SETUID };
        String setGID[] = { "-", VACL.SETGID };
        String setSticky[] = { "-", VACL.STICKY };

        if (isDir)
            execName = VACL.PERM_ACCESSIBLE;
        else
            execName = VACL.PERM_EXECUTABLE;

        for (int i = 0; i < numEntries; i++)
            acl[i] = new Attribute[numAttrs];

        // USER_ENTITY is both used for name as for value
        acl[0][0] = new Attribute(VACL.USER_ENTITY, VACL.USER_ENTITY);

        acl[0][1] = new Attribute(VACL.PERM_READABLE, (mode & 00400) > 0);
        acl[0][2] = new Attribute(VACL.PERM_WRITABLE, (mode & 00200) > 0);
        acl[0][3] = new Attribute(execName, (mode & 00100) > 0);
        acl[0][4] = new Attribute("Misc", setUID, ((mode & 04000) > 0) ? 1 : 0);

        acl[1][0] = new Attribute(VACL.USER_ENTITY, VACL.GROUP_ENTITY);
        acl[1][1] = new Attribute(VACL.PERM_READABLE, (mode & 00040) > 0);
        acl[1][2] = new Attribute(VACL.PERM_WRITABLE, (mode & 00020) > 0);
        acl[1][3] = new Attribute(execName, (mode & 0010) > 0);
        acl[1][4] = new Attribute("Misc", setGID, ((mode & 02000) > 0) ? 1 : 0);

        acl[2][0] = new Attribute(VACL.USER_ENTITY, VACL.WORLD_ENTITY);
        acl[2][1] = new Attribute(VACL.PERM_READABLE, (mode & 00004) > 0);
        acl[2][2] = new Attribute(VACL.PERM_WRITABLE, (mode & 00002) > 0);
        acl[2][3] = new Attribute(execName, (mode & 001) > 0);
        acl[2][4] = new Attribute("Misc", setSticky, ((mode & 01000) > 0) ? 1 : 0);

        // enable editing:
        for (int i = 0; i < numEntries; i++)
            for (int j = 1; j < 4; j++)
                // skip misc for now
                acl[i][j].setEditable(true);

        return acl;
    }

    /**
     * Translate VFS ACL list to Unix file mode.
     * 
     * @see VACL
     */
    public static int convertACL2FileMode(Attribute[][] acl, boolean isDir)
    {
        int nrEntities = 3;
        int nrAttrs = 5;
        int attrOffset = 1;

        int rang = 0400;
        int mode = 0;

        if ((acl == null) || (acl[0] == null) || (acl[0].length < 1))
            return -1;

        for (int i = 0; i < nrEntities; i++)
        {
            if (i >= acl.length)
            {
                errorPrintf("Error converting ACL to unix file mode: acl.length<3. length=%d\n", acl.length);
                return -1;
            }

            String attrName = acl[i][0].getName();

            if (attrName.compareTo(VACL.USER_ENTITY) != 0)
            {
                errorPrintf("Error converting ACL to unix file mode: first attribute MUST be user type:%s\n", attrName);
                return -1;
            }

            String entity = acl[i][0].getStringValue();

            if (entity.compareTo(VACL.USER_ENTITY) == 0)
                rang = 0100;
            else if (entity.compareTo(VACL.GROUP_ENTITY) == 0)
                rang = 0010;
            else if (entity.compareTo(VACL.WORLD_ENTITY) == 0)
                rang = 0001;
            else
            {
                errorPrintf("Error converting ACL to unix file mode: can't recognise user type:%s\n", entity);
                return -1;
            }

            for (int j = attrOffset; j < nrAttrs; j++)
            {
                if (j >= acl[i].length)
                {
                    errorPrintf("Error converting ACL to unix file mode: acl[" + i
                            + "] attribute length<4. length=%d\n", acl[0].length);
                    return -1;
                }

                Attribute attr = acl[i][j];

                String perm = attr.getName();
                int val = 0;
                boolean misc = false;

                if (perm.compareTo(VACL.PERM_READABLE) == 0)
                    val = 4;
                else if (perm.compareTo(VACL.PERM_WRITABLE) == 0)
                    val = 2;
                else if (perm.compareTo(VACL.PERM_ACCESSIBLE) == 0)
                    val = 1; // for directories
                else if (perm.compareTo(VACL.PERM_EXECUTABLE) == 0)
                    val = 1; // for files
                else if (perm.compareTo("Misc") == 0)
                {
                    misc = true;
                    String str = attr.getStringValue();

                    if (StringUtil.equals(str, VACL.SETUID))
                        mode = mode | 04000;
                    else if (StringUtil.equals(str, VACL.SETGID))
                        mode = mode | 02000;
                    else if (StringUtil.equals(str, VACL.STICKY))
                        mode = mode | 01000;
                    else
                        warnPrintf("Warning converting ACL to unix file mode: can't recognise miscellaneous type:%s\n",
                                str);
                }
                else
                {
                    warnPrintf("Warning converting ACL to unix file mode: can't recognise permission type:%s\n", perm);
                    // continue;
                }

                if ((misc == false) && (attr.getBooleanValue()))
                    mode = mode | (val * rang);
            }
        }

        return mode;
    }

    private static void warnPrintf(String format, Object... args)
    {
        ClassLogger.getLogger(VFS.class).warnPrintf(format, args);
    }

    private static void errorPrintf(String format, Object... args)
    {
        ClassLogger.getLogger(VFS.class).errorPrintf(format, args);
    }

    public static int parseUnixPermissions(String permStr, BooleanHolder isDir, BooleanHolder isLink)
    {
        if (isDir == null)
            isDir = new BooleanHolder();

        if (isLink == null)
            isLink = new BooleanHolder();

        if (permStr == null)
            return -1;

        isDir.value = false;

        isLink.value = false;

        if (permStr.startsWith("d"))
            isDir.value = true;
        else if (permStr.startsWith("l"))
            isLink.value = true;
        else if (permStr.startsWith("-"))
        {
            isDir.value = false;
            isLink.value = false;
        }
        else
        {
            // Global.debugPrintf("VFS","Could not parse Permissions String:%s\n",permStr);
            return -1;
        }

        if (permStr.length() < 10)
        {
            // Global.debugPrintf("VFS","Permissions string to short:%s\n",permStr);
            return -1;
        }

        int mode = 0;

        // NOT permStr=permStr.toLowerCase();

        mode = ((permStr.charAt(1) == 'r') ? 0400 : 0) + ((permStr.charAt(2) == 'w') ? 0200 : 0)
                + ((permStr.charAt(3) == 'x') ? 0100 : 0)
                + ((permStr.charAt(4) == 'r') ? 0040 : 0)
                + ((permStr.charAt(5) == 'w') ? 0020 : 0)
                + ((permStr.charAt(6) == 'x') ? 0010 : 0)
                + ((permStr.charAt(7) == 'r') ? 0004 : 0)
                + ((permStr.charAt(8) == 'w') ? 0002 : 0)
                + ((permStr.charAt(9) == 'x') ? 0001 : 0)
                // S[UG]ID and Sticky bits
                // lower = 'x' is set : "--s--s--t"
                + ((permStr.charAt(3) == 's') ? 04100 : 0) + ((permStr.charAt(6) == 's') ? 02010 : 0)
                + ((permStr.charAt(9) == 't') ? 01001 : 0)
                // upper = 'x' is unset: "--S--S--T"
                + ((permStr.charAt(3) == 'S') ? 04000 : 0) + ((permStr.charAt(6) == 'S') ? 02000 : 0)
                + ((permStr.charAt(9) == 'T') ? 01000 : 0);

        // Global.debugPrintf(VFS.class,"Permstring:'%s'(dir=%b) = %o\n",permStr,isDir,mode);

        return mode;
    }

    // ===
    // Static Misc. methods
    // ===

    public static String modeToString(int mode, boolean isdir)
    {
        return modeToString(mode, isdir, false);
    }

    public static String modeToString(int mode, boolean isDir, boolean isLink)
    {
        char bits[] = new char[10];

        bits[0] = (isDir == true) ? 'd' : '-';
        // link 'l' overrides dir 'd':
        bits[0] = (isLink == true) ? 'l' : bits[0];

        bits[1] = ((mode & 00400) > 0) ? 'r' : '-';
        bits[2] = ((mode & 00200) > 0) ? 'w' : '-';
        bits[3] = ((mode & 00100) > 0) ? 'x' : '-';
        bits[4] = ((mode & 00040) > 0) ? 'r' : '-';
        bits[5] = ((mode & 00020) > 0) ? 'w' : '-';
        bits[6] = ((mode & 00010) > 0) ? 'x' : '-';
        bits[7] = ((mode & 00004) > 0) ? 'r' : '-';
        bits[8] = ((mode & 00002) > 0) ? 'w' : '-';
        bits[9] = ((mode & 00001) > 0) ? 'x' : '-';
        // S[GU]ID and sTicky bits (override 'x'):
        bits[3] = ((mode & 04100) == 04100) ? 's' : bits[3];
        bits[6] = ((mode & 02010) == 02010) ? 's' : bits[6];
        bits[9] = ((mode & 01001) == 01001) ? 't' : bits[9];

        bits[3] = ((mode & 04100) == 04000) ? 'S' : bits[3];
        bits[6] = ((mode & 02010) == 02000) ? 'S' : bits[6];
        bits[9] = ((mode & 01001) == 01000) ? 'T' : bits[9];

        return new String(bits);
    }

    // ===
    // VFS Helper methods, interface might changed:
    // ===

    // helper methods:
    public static int getOptimalReadBufferSizeFor(VNode node) throws VrsException
    {
        // todo: Stream optimization !
        return VRS.DEFAULT_STREAM_READ_CHUNK_SIZE;
    }

    // helper methods
    public static int getOptimalWriteBufferSizeFor(VNode node) throws VrsException
    {
        // todo: Stream optimization !
        return VRS.DEFAULT_STREAM_WRITE_CHUNK_SIZE;
    }

    /**
     * Static method to open a new FileSystem given the context and the location
     */
    public static VFileSystem openFileSystem(VRSContext context, VRL location) throws VrsException
    {
        VResourceSystem rs = openResourceSystem(context, location);

        if (rs == null)
            throw new nl.esciencecenter.vlet.exception.VrsServiceTypeMismatchException("Could find filesystem implementation for:"
                    + location);

        if ((rs instanceof VFileSystem) == false)
            throw new nl.esciencecenter.vlet.exception.VrsServiceTypeMismatchException("Remote Resource is not a File System:"
                    + location);

        return (VFileSystem) rs;
    }

    /** Create new VFSClient to access the Virtual File System */
    public static VFSClient createVFSClient(VRSContext context)
    {
        return new VFSClient(context);
    }

    /**
     * VFile factory method: Create new VFile Object using the specified
     * context. The file might not exist on the (remote) FileSystem yet. Use
     * VFile.exists() or VFile.create() to check or create the file.
     * 
     * @see VFileSystem#newFile(VRL)
     * @param context
     *            VRSContext
     * @param fileVRL
     *            New or existing File Location
     * @throws VrsException
     *             If the remote filesystem couldn't be contacted or another
     *             error occurred.
     */
    public static VFile newVFile(VRSContext context, VRL fileVRL) throws VrsException
    {
        VFileSystem vfs = openFileSystem(context, fileVRL);
        return vfs.newFile(fileVRL);
    }

    /**
     * VDir factory method: Create new VDir Object using the specified context.
     * The directory might not exist on the (remote) FileSystem. For this to
     * work the VFileSystem must support the creation of new VDir objects. Use
     * VDir.exists() or VDir.create() to check or create the directory.
     * 
     * @see VFileSystem#newDir(VRL)
     * @param context
     *            VRSContext
     * @param dirVRL
     *            New or existing Directory Location
     * @throws VrsException
     *             If the remote filesystem couldn't be contacted or another
     *             error occurred.
     */
    public static VDir newVDir(VRSContext context, VRL dirVRL) throws VrsException
    {
        VFileSystem vfs = openFileSystem(context, dirVRL);
        return vfs.newDir(dirVRL);
    }

}
