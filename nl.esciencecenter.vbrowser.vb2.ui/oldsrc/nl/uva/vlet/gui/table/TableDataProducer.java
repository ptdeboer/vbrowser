/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: TableDataProducer.java,v 1.1 2012/11/18 13:20:35 piter Exp $  
 * $Date: 2012/11/18 13:20:35 $
 */ 
// source: 

package nl.uva.vlet.gui.table;

import nl.uva.vlet.gui.proxymodel.ViewItem;
import nl.uva.vlet.gui.proxynode.ProxyNode;
import nl.uva.vlet.presentation.Presentation;

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

	public ViewItem getRootViewItem();
    
    //public void insertColumn(String headerName, String argstr);
    //public void removeColumn(String argstr);
    
    
}