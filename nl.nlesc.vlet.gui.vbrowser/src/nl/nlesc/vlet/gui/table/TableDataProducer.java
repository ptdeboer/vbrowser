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

package nl.nlesc.vlet.gui.table;

import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentation;
import nl.nlesc.vlet.gui.proxyvrs.ProxyNode;
import nl.nlesc.vlet.gui.view.ViewNode;


/**
 * Shared Table interface for ACL Lists and NodeTable to
 * share same VRSTableModel 
 * 
 * @author P.T. de Boer
 */ 
public interface TableDataProducer 
{
    //public void produceColumnData(String name) throws VlException; 
    //public void produceRowData(int rownr) throws VlException;
    
    public void backgroundCreateTable();
    
    public void storeTable();
    
    public void setNode(ProxyNode node);
    
    public ProxyNode getNode(); 
   
    public void dispose();
    
    public UIPresentation getPresentation(); 
    
    public String[] getAllHeaderNames();

	public ViewNode getRootViewItem();
    
    //public void insertColumn(String headerName, String argstr);
    //public void removeColumn(String argstr);
    
    
}
