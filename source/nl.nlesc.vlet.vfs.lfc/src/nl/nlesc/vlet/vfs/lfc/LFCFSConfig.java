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

package nl.nlesc.vlet.vfs.lfc;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.nlesc.vlet.exception.ConfigurationError;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.data.VAttributeConstants;

public class LFCFSConfig
{
    public static final String ATTR_PREFERREDSSES = "listPreferredSEs";

    public static final String ATTR_GENERATED_DIRNAME = "generatedDirname";

    public static final String ATTR_GENERATED_SUBDIR_DATE_SCHEME = "subDirDateScheme";
    
    public static final String ATTR_REPLICA_NAME_CREATION_POLICY= "replicaNamePolicy";
    
    public static final String ATTR_REPLICA_NR_OF_TRIES = "replicaNrOfTries";
 
    public static final String REPLICA_NAME_POLICY_RANDOM="Random"; 
    
    public static final String REPLICA_NAME_POLICY_SIMILAR="Similar"; 
    
    public static final String REPLICA_NAME_POLICIES[] = 
        {
            REPLICA_NAME_POLICY_SIMILAR,// entry[0] is default !
            REPLICA_NAME_POLICY_RANDOM
        }; 
    
    public static final String[] DEFAULT_LIST_PREFERRED_SES=
        {
            "srm.grid.sara.nl",
            "tbn18.nikhef.nl"
        };
    
    // === Default Values ===
    
    public static final String DEFAULT_GENERATED_DIRNAME_VALUE="vletgenerated"; 

    /**
     * See java.text.SimpleDateFormat  
     */
    public static final String DEFAULT_GENERATED_SUBDIR_DATE_SCHEME="yyyy-MM-dd";

    public static final String ATTR_REPLICA_SELECTION_MODE = "replicaSelectionMode";
    
    public static final String ATTR_REPLICA_CREATION_MODE = "replicaCreationMode"; 
    
    public static enum ReplicaSelectionMode
    {
        // use attribute value for string comparison instead of Enum Value !
        PREFERRED("Preferred"),
        PREFERRED_RANDOM("PreferredRandom"),
        ALL_SEQUENTIAL("AllSequential"),
        ALL_RANDOM("AllRandom");
        
        String attrValue; 
        
        private ReplicaSelectionMode(String attrVal)
        {
            this.attrValue=attrVal;
        }
        
        public String getValue()
        {
            return this.attrValue; 
        }
        
        public static ReplicaSelectionMode createFromAttributeValue(String valstr) throws ConfigurationError
        {
            for (ReplicaSelectionMode mode:ReplicaSelectionMode.values())
            {
                if (StringUtil.equals(valstr,mode.getValue()))
                    return mode; 
            }
            
            throw new nl.nlesc.vlet.exception.ConfigurationError("Invalid Replica Selection Mode:"+valstr);
        }
    }
    
    public static enum ReplicaCreationMode
    {
        // use attribute value for string comparison instead of Enum Value !
        PREFERRED("Preferred"),
        PREFERRED_RANDOM("PreferredRandom"),
        //DEFAULT_VO("DefaultVO"),
        VORANDOM("DefaultVORandom");
        
        String attrValue; 
        
        private ReplicaCreationMode(String attrVal)
        {
            this.attrValue=attrVal;
        }
        
        public String getValue()
        {
            return this.attrValue;  
        }
        
        public static ReplicaCreationMode createFromAttributeValue(String valstr) throws ConfigurationError
        {
            for (ReplicaCreationMode mode:ReplicaCreationMode.values())
            {
                if (StringUtil.equals(valstr,mode.getValue()))
                    return mode; 
            }
            
            throw new nl.nlesc.vlet.exception.ConfigurationError("Invalid Replica Creation Mode:"+valstr);  
        }
    }

    // default value; 
    private static int replicaSelectionModeDefault=3;  

    public static final String[] REPLICAS_SELECTIONMODE_VALUES = 
        {
            ReplicaSelectionMode.PREFERRED.getValue(), // first is default  
            ReplicaSelectionMode.PREFERRED_RANDOM.getValue(),   
            ReplicaSelectionMode.ALL_SEQUENTIAL.getValue(),  
            ReplicaSelectionMode.ALL_RANDOM.getValue()
        };

    // default value; 
    private static int replicaCreationModeDefault=2;  
    
    public static final String[] REPLICAS_CREATIONMODE_VALUES = 
        {
            ReplicaCreationMode.PREFERRED.getValue(), // first is default  
            ReplicaCreationMode.PREFERRED_RANDOM.getValue(),  
            //ReplicaCreationMode.DEFAULT_VO.getAttrValue(),
            ReplicaCreationMode.VORANDOM.getValue()
        };

    public static AttributeSet createDefaultServerAttributes(VRSContext context, AttributeSet uriAttrs)
        {
           AttributeSet set=new AttributeSet(); 
           Attribute attr=null;
           
           
           //Spiros non read variable 
//           ConfigManager confMan=context.getConfigManager();
           
           // ===
           // initialize with hardcoded defaults
           // === 
           
           set.put(attr=new Attribute(VAttributeConstants.ATTR_HOSTNAME,"LFCHOST")); 
           attr.setEditable(true);
           
           set.put(attr=new Attribute(VAttributeConstants.ATTR_PORT,5010)); 
           attr.setEditable(true); 
          
           //use configuration property
           String val=context.getStringProperty("lfc."+ATTR_PREFERREDSSES);
           attr=new Attribute(ATTR_PREFERREDSSES,val);
            
           attr.setEditable(true); 
           set.put(attr); 
    
           //String modes[]={"AlwaysFirst,MatchPreferredFirst,MatchPreferredInOrder,Random,Parralel"}; 
           String modes[]=REPLICAS_SELECTIONMODE_VALUES; 
           attr=new Attribute(ATTR_REPLICA_SELECTION_MODE,modes,replicaSelectionModeDefault);  
           attr.setEditable(true); 
           set.put(attr);
           
           modes=REPLICAS_CREATIONMODE_VALUES; 
           attr=new Attribute(ATTR_REPLICA_CREATION_MODE,modes,replicaCreationModeDefault);  
           attr.setEditable(true); 
           set.put(attr);
           
           set.put(attr=new Attribute(LFCFSConfig.ATTR_REPLICA_NR_OF_TRIES,5)); 
           attr.setEditable(true);
           
           attr=new Attribute(ATTR_GENERATED_SUBDIR_DATE_SCHEME,
                                       DEFAULT_GENERATED_SUBDIR_DATE_SCHEME);
           attr.setEditable(false); // not editable for now ! 
           set.put(attr); 
    
           attr=new Attribute(ATTR_GENERATED_DIRNAME,DEFAULT_GENERATED_DIRNAME_VALUE);
           attr.setEditable(false); // not editable for now ! 
           set.put(attr); 
                
           modes=LFCFSConfig.REPLICA_NAME_POLICIES;  
           attr=new Attribute(ATTR_REPLICA_NAME_CREATION_POLICY,modes,0);  
           attr.setEditable(true); 
           set.put(attr);

    //
    //       attr=new VAttribute("replicaSelectionNrOfTries",3);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       attr=new VAttribute("replicaSelectionTimeOut",5);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       String modes[]={"PreferredInOrder,PreferredRandom,VOAllowedFirst,VOAllowedInOrder,VOAllowedRandom"};
    //       attr=new VAttribute("replicaCreationMode",modes,0);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       attr=new VAttribute("defaultNrOfReplicas",3);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       attr=new VAttribute("replicaAutoUseSRMV22URI",true); 
    //       attr.setEditable(true); 
    //       set.put(attr); 
    
           // update default from Context and optional URI Attribute
           // Overriding hard coded defaults ! 
           for (String key:set.keySet()) 
           {
               Attribute orgAttr=set.get(key); 
    
               // check global and context properties: 
               Object obj=context.getProperty("lfc."+key); 
               
               if (obj!=null) 
               {
                   orgAttr.setObjectValue(obj.toString()); // store as String.  
                   //Global.infoPrintln(LFCFSConfig.class,"Using context property:"+key+"="+obj); 
               }
               
               // get attribute from optional URI attribute set:  
               Attribute uriAttr=null; 
               if (uriAttrs!=null)
               {
                      uriAttr=uriAttrs.get("lfc."+key);
               }
               
               // get attribute from context: 
               if (uriAttr!=null)
               {
                   // Global.infoPrintln(LFCFSConfig.class,"Using URI attribute:"+key+"="+obj); 
                   orgAttr.setObjectValue(uriAttr.getValue());
               }
           }
           // return updated set: 
           return set; 
        }

    public static void updateURIAttributes(ServerInfo lfcInfo, AttributeSet uriAttrs)
    {
        if ((uriAttrs==null) || (lfcInfo==null))
            return; 
        
     // Overriding hard coded defaults ! 
        for (String key:lfcInfo.getAttributeNames()) 
        {
            Attribute orgAttr=lfcInfo.getAttribute(key); 
            
            // get attribute from optional URI attribute set:  
            Attribute uriAttr=uriAttrs.get("lfc."+key);
            
            // get attribute from context: 
            if (uriAttr!=null)
            {
                //Global.infoPrintln(LFCFSConfig.class,"Using URI attribute:"+key+"="+uriAttr); 
                orgAttr.setObjectValue(uriAttr.getValue());
            }
        }
    }

    public static ReplicaSelectionMode string2ReplicaSelectionMode(String value) throws ConfigurationError
    {
        return ReplicaSelectionMode.createFromAttributeValue(value); 
    }
    
    public static ReplicaCreationMode string2ReplicaCreationMode(String value) throws ConfigurationError
    {
        return ReplicaCreationMode.createFromAttributeValue(value); 
    }

    /** convenience method to check whether property string is on of the "Preferred" modes */ 
    public static boolean isPreferredMode(String valstr)
    {
        if (valstr==null)
            return false; 
        
        try
        {
            ReplicaSelectionMode mode = string2ReplicaSelectionMode(valstr);
            switch(mode)
            {
                case PREFERRED:
                case PREFERRED_RANDOM:
                    return true;
                default: 
                    return false;  
            }
        }
        catch (ConfigurationError e)
        {
            // e.printStackTrace();
        }
        
        try
        {
            ReplicaCreationMode mode = string2ReplicaCreationMode(valstr);
            switch(mode)
            {
                case PREFERRED:
                case PREFERRED_RANDOM:
                    return true;
                default: 
                    return false;  
            }
        }
        catch (ConfigurationError e)
        {
            // e.printStackTrace();
        } 
     
        return false; 
        
    }

}
