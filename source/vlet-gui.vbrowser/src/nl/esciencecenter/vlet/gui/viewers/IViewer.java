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

package nl.esciencecenter.vlet.gui.viewers;
///*
// * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
// * 
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache Licence at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * See: http://www.vl-e.nl/ 
// * See: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// * $Id: IViewer.java,v 1.1 2013/01/22 15:42:15 piter Exp $  
// * $Date: 2013/01/22 15:42:15 $
// */
//// source: 
//
//package nl.uva.vlet.gui.viewers;
//
//import nl.uva.vlet.Global;
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.gui.MasterBrowser;
//import nl.uva.vlet.vrl.VRL;
//
///**
// * Deprecated Class. Since the 'I' in the name suggest and interface this class
// * has been renamed. Use ViewerPanel for the Superclass of VBrowser Viewers.
// * Also most of the Viewer 'management' code has been moved to ViewerManager
// * class so separate actual viewer methodology and viewer life cycle management.
// * <p>
// * This class still contains some legacy code mostly for testing purposes.
// * Update your viewer class to ViewePanel.
// * 
// * @deprecated Use new ViewerPanel class
// */
//public abstract class IViewer extends ViewerPlugin
//{
//
//    /**
//     * Legacy method, is only used for stand alone testing as Viewer may not
//     * call their own start methods.
//     * 
//     * @see #startStandAlone(VRL);
//     * 
//     * @deprecated LEgacy code only used for testing standalone. Use
//     *             ViewerManager class
//     */
//    public void startForLocation(MasterBrowser bc, final VRL location) throws VlException
//    {
//        Global.errorPrintln(this,
//                "startForLocation(): Warning: using deprecated method. Use startStandAlone for testing !");
//
//        startAsStandAloneApplication(location);
//    }
//
//    /**
//     * Deprecated method: SignalDispose has moved to viewer manager. Implement
//     * override disposeViewer().
//     * 
//     * @deprecated use disposeViewer() to dispose a viewer
//     */
//    public void signalDispose()
//    {
//
//    }
//
//}
