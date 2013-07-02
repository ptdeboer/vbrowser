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

package nl.esciencecenter.vlet.vrs.vdriver.infors;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.InternalError;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vrms.LogicalFolderNode;

/** 
 * Non generic super class for Grid Info Nodes 
 */ 
public abstract class CompositeServiceInfoNode<T extends VNode> extends LogicalFolderNode<T>
{
    public CompositeServiceInfoNode(VRSContext context, VRL vrl)
    {
        super(context, vrl);
    }

    public String getMimeType() { return null;} 
    
    public boolean unlinkNode(VNode node) {return false;} 

    public boolean unlinkNodes(VNode nodes[]) {return false;} 
    
    public boolean save() {return false;} 
    
    public VRL getStorageLocation() throws VrsException
    {
        return null; // doesn't have one
    }

    public void setLogicalLocation(VRL newRef) throws VrsException
    {
        throw new InternalError("Can not set logical location of this node:"+this);  
    }    

}
