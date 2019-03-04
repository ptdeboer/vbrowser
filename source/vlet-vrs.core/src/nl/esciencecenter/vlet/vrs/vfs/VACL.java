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

import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

/**
 * Universal Access Control List interface. 
 * <br>
 * The ACL consists of a VAttribute Matrix where each row y
 * in the matrix ACL[y][x] represents an ACL Record for an ACL Entity. 
 * Each value [x] in the ACL Entity record ACL[y][x] represents a 
 * permission type which is either "true" or "false" except for the 
 * first column ACL[y][0] which represent the Entity Name for Entity [y].  
 * <br>
 * And example of a list of Entities for a Unix file system is : 
 * {"user","group","other}.<br>
 * In this example each ACL Entity can have the (UX) permissions attribute: 
 * {"readable","writable","executable"}<br>
 * The full ACL matrix for an Unix like ACL list (&lt;name&gt;=&lt;value&gt;) is:
 * <pre> 
 *   ACL[0][] = { user="user",   readable=true, writable=true,  executable=false }  
 *   ACL[1][] = { group="group", readable=true, writable=false, executable=false } 
 *   ACL[2][] = { other="other", readable=true, writable=false, executable=false }
 * </pre>
 * For an enhanced ACL file system extra entries can be added as follows: 
 * <pre>
 *  ACL[3][] = { user="jan",    readable=true, writable=true,  executable=false }  
 *  ACL[4][] = { user="piet",   readable=true, writable=false, executable=false } 
 * </pre> 
 * To specify a different permission attribute, for example for a Unix directory, 
 * just use a different attribute name:
 * <pre>
 *  ACL[0][] = { user="user",   readable=true, writable=true,  accessible=false }  
 * </pre>
 * 
 * For Unix mode convertor methods, see: {@link VFS#convertFileMode2ACL(int, boolean) }
 * and {@link VFS#convertACL2FileMode(Attribute[][], boolean)} . 
 * 
 * @author P.T. de Boer
 */
public interface VACL  
{
	/** Default User Entity for Unix */ 
    public static final String USER_ENTITY  ="user"; 
    /** Default Group Entity for Unix */ 

    public static final String GROUP_ENTITY ="group"; 
    /** Default "Other" Entity for Unix */ 

    public static final String WORLD_ENTITY ="other";
    
    public static final String PERM_READABLE   ="readable";
    
    public static final String PERM_WRITABLE   ="writable";
    
    /** 'x' for files */ 
    public static final String PERM_EXECUTABLE ="executable";
    
    /** 'x' for directories  */ 
    public static final String PERM_ACCESSIBLE ="accessible";
    
    public static final String SETGID="setGID";  
    public static final String SETUID="setUID";  
    public static final String STICKY="STICKY";  
    
    public static final String PERM_UNIX_SETGID ="unix."+SETGID;
    
    public static final String PERM_UNIX_SETUID  ="unix."+SETUID;
    
    public static final String PERM_UNIX_STICKY  ="unix."+STICKY;
    
    /**
     * Get a modifyable attribute matrix which represents the
     * ACL list.  
     *  
     * @return
     * @throws VrsException
     */
    public Attribute[][] getACL() throws VrsException;
    
    /** 
     * Set the modified ACL list 
     */ 
    
    public void setACL(Attribute[][] acl) throws VrsException; 
    
    /** 
     * Returns list of all possible 'entities' which can
     * have an ACL record entry in the list.
     *  
     * For example for a linux file system this would be "user,group,other". 
     * An entity can also be a username a groupname or a domainname. 
     * The name of the entity is specified as VAttribute name. 
     *  
     * @return VAttribute array of possible entities. 
     */
    public Attribute[] getACLEntities() throws VrsException; 
    
    /**
     * Returns a new 'row' or ACLRecord for the entity. 
     * This entity is taken from one of the entities returned
     * by getACLEntities. 
     * 
     * @param entity       entity to created the new ACL Record for.  
     * @param writeThrough add the new created entry to the ACL list  
     */
    public Attribute[] createACLRecord(Attribute entity, boolean writeThrough) throws VrsException; 
    
    /**
     * Remove the entity from the ACL list, effectively removing
     * all permission settings associated with the entity 
     * (Although this depends on the default permissions of the implementation). 
     * @param entity
     * @return
     * @throws VrsException
     */
    public boolean deleteACLEntity(Attribute entity) throws VrsException; 
    
}
