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

package nl.esciencecenter.vlet.vrs.tools;

import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.*;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.ResourceNotFoundException;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSClient;
import nl.esciencecenter.vlet.vrs.vfs.VChecksum;

/**
 * URI 'stat' command. Parse command line arguments and perform 'stat' like command on URI. 
 * is called from the uristat script.
 * 
 * @since VLET 1.2
 */
public class URIStat
{
    static int verbose = 0;
    static String sourceVrl = null;
    static boolean quoted = true;
    static boolean allAttributes = true;
    static boolean checksum = false;
    static String checksumType = "MD5";

    /** List of attribute to display */
    static StringList attributeList = null;
    static String proxyFile = null;

    static String commonFileAttrNames[] =
            { 
              ATTR_PATH, ATTR_RESOURCE_TYPE, ATTR_FILE_SIZE, ATTR_PERMISSIONSTRING, ATTR_UNIX_FILE_MODE, ATTR_CREATION_TIME,
              ATTR_MODIFICATION_TIME, ATTR_UNIX_USERID, ATTR_UNIX_GROUPID, ATTR_ISSYMBOLIC_LINK, ATTR_SYMBOLICLINKTARGET 
            };

    public static void Usage()
    {
        System.err.println("Usage: [-v] [-filestat] [-attrs=<attributelist>] [-D<prop>=<value] <sourceURI> ");
        System.err.println("Arguments:\n" + "  <sourceURI>              : source URI \n"
                + "  [ -proxy <proxyfile> ]   : Optional proxy file \n"
                + "  -v [-v]                  : be verbose (use twice for more)\n"
                + "  -file                    : print out common file attributes\n"
                + "  -all                     : print out all resource attributes (default)\n"
                + "  -[no]quotes              : put value between single 'quotes' (or not)\n"
                + "  -checksum <TYPE>         : report checksum (if supported)\n"
                + "  [-attrs=<attributelist>] : list of comma separated VRS Attribute names\n"
                + "  [-D<prop>=<value]        : specify java properties to the JVM\n");
    }

    public static void error(String str)
    {
        System.err.println(str);
    }

    public static void exit(int stat)
    {
        System.exit(stat);
    }

    public static void main(String args[])
    {
        args = VletConfig.parseArguments(args);

        attributeList = new StringList();

        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            String arg2 = null;

            if ((i + 1) < args.length)
                arg2 = args[i + 1];

            // double arguments
            if (i + 1 < args.length)
            {
                if (args[i].equalsIgnoreCase("-checksum"))
                {
                    checksum = true;
                    allAttributes = false;
                    checksumType = args[i + 1];
                    attributeList.add(ATTR_CHECKSUM);
                    attributeList.add(ATTR_CHECKSUM_TYPE);
                    attributeList.add(ATTR_CHECKSUM_TYPES);
                    // SHIFT
                    i++;
                    continue;
                }
            }

            if (arg.equalsIgnoreCase("-nochecksum"))
            {
                checksum = false;
                attributeList.remove(ATTR_CHECKSUM);
            }
            else if (arg.equalsIgnoreCase("-v"))
            {
                verbose++;
                int v = verbose - 2;

                // set Global verbose 2 levels back to VFSCopy verbose
                // (Verbose>2 triggers Global Verbose)
                if (v <= 0)
                    v = 0;

            }
            else if (arg.equalsIgnoreCase("-debug"))
            {
                getLogger().setLevelToDebug();
            }
            else if (arg.equalsIgnoreCase("-info"))
            {
                getLogger().setLevelToInfo();
            }
            else if (arg.equalsIgnoreCase("-warn"))
            {
                getLogger().setLevelToWarn();
            }
            else if ((arg.equalsIgnoreCase("-filestat")) || (arg.equalsIgnoreCase("-file")))
            {
                allAttributes = false;
                attributeList.merge(commonFileAttrNames);
            }
            else if (arg.equalsIgnoreCase("-all"))
            {
                allAttributes = true;
            }
            else if (arg.equalsIgnoreCase("-help"))
            {
                Usage();
                exit(0);
            }
            else if (arg.equalsIgnoreCase("-h"))
            {
                Usage();
                exit(0);
            }
            else if (arg.equalsIgnoreCase("-quotes"))
            {
                quoted = true;
            }
            else if (arg.equalsIgnoreCase("-noquotes"))
            {
                quoted = false;
            }
            // OLD style parameter
            else if (arg.startsWith("-proxy="))
            {
                proxyFile = arg.substring("-proxy=".length());
            }
            else if (arg.compareTo("-proxy") == 0)
            {
                proxyFile = arg2;
                i++; // SHIFT!
            }
            else if (arg.startsWith("-attrs="))
            {
                allAttributes = false;
                String strs[] = arg.split("=");
                if ((strs != null) && (strs.length == 2))
                {
                    attributeList.merge(StringList.createFrom(strs[1], ","));
                }
            }
            else
            {
                if (sourceVrl == null)
                    sourceVrl = arg; // first argument
                else
                {
                    // extra argument
                    error("*** Error: Invalid argument:" + arg);
                    Usage();
                    exit(1);
                }
            } // if(args[i])
        } // for

        if (sourceVrl == null)
        {
            // not enough arguments
            Usage();
            exit(1);
        }

        try
        {
            VRSClient vfs = new VRSClient(); // My Client

            if (proxyFile != null)
            {
                GridProxy proxy = GridProxy.loadFrom(vfs.getVRSContext(), proxyFile);
                vfs.getVRSContext().setGridProxy(proxy);
            }

            VNode node = vfs.openLocation(sourceVrl);

            stat(node, attributeList);

            // // Check Source:
            // // ===========
            // if (sourceNode.exists()==false)
            // {
            // Error("*** Error: Could not locate source:"+sourceNode);
            // exit(1);
            // }

            Message(2, "--- parameters --- ");
            Message(2, "source  =" + sourceVrl);

        }// try
        catch (ResourceNotFoundException e)
        {
            error("*** Resource not found:" + sourceVrl);
            exit(1);
        }
        catch (Exception e1)
        {
            error("*** Exception=" + e1);
            error("*** Exception message=" + e1.getMessage());
            // message is included in above message
            // Error("Exception Message:" + e1.getMessage());

            // print out full stack trace in WARN mode:
            getLogger().logException(ClassLogger.WARN, e1, "Stat Failed for%s\n", sourceVrl);
            exit(2);
        }

        exit(0); // ok (?)
    }

    private static ClassLogger getLogger()
    {
        return ClassLogger.getLogger(URIStat.class);
    }

    public static void Message(int level, String str)
    {
        if (level <= verbose)
            System.out.println(str);
        else
            getLogger().infoPrintf("URIStat", "%s\n", str);
    }

    private static void stat(VNode node, StringList attributeList) throws Exception
    {
        Message(1, "Statting node:" + node);

        // Add File Attributes:

        AttributeSet attrs = null;

        if ((allAttributes == true) || (attributeList == null))
        {
            attributeList = new StringList(node.getAttributeNames());
        }

        Message(2, "Statting Attributes:" + attributeList.toString(","));

        attrs = node.getAttributeSet(attributeList.toArray());

        if (checksum)
        {
            if (node instanceof VChecksum)
            {
                try
                {
                    String checkStr = ((VChecksum) node).getChecksum(checksumType);
                    attrs.set(new Attribute(ATTR_CHECKSUM, checkStr));
                    attrs.set(new Attribute(ATTR_CHECKSUM_TYPE, checksumType));
                }
                catch (Exception e)
                {
                    error("*** Error fetching checksum type:" + checksumType);
                    error("*** Exception message=" + e.getMessage());
                }

            }
        }
        // find maximum
        int maxWidth = 0;
        for (String name : attributeList)
            if (name.length() > maxWidth)
                maxWidth = name.length();

        for (String name : attributeList)
        {
            Attribute attr = attrs.get(name);
            String value = "";
            if (attr != null)
                value = attr.getStringValue();

            if (quoted)
                System.out.printf(" %" + maxWidth + "s='%s'\n", name, value);
            else
                System.out.printf(" %" + maxWidth + "s=%s\n", name, value);
        }
    }

}
