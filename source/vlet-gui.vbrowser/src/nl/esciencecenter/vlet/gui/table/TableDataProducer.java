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

package nl.esciencecenter.vlet.gui.table;

import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyNode;
import nl.esciencecenter.vlet.gui.view.ViewNode;


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
    
    public Presentation getPresentation(); 
    
    public String[] getAllHeaderNames();

	public ViewNode getRootViewItem();
    
    //public void insertColumn(String headerName, String argstr);
    //public void removeColumn(String argstr);
    
    
}
