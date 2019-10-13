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

//package test.wms;
//
//import nl.nlesc.ptk.global.Global;
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.gui.vbrowser.VBrowserInit;
//import nl.uva.vlet.gui.viewers.grid.jobmonitor.JobMonitor;
//import nl.uva.vlet.vjs.wms.WMSFactory;
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrs.VRS;
//
//public class TestJobMonitor
//{
//    public static void main(String args[])
//    {
//        try
//        {
//            Global.init();
//            VRS.getRegistry().addVRSDriverClass(WMSFactory.class);
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        
//        try
//        {
//            // init platform!
//            VBrowserInit.initPlatform(); 
//
//            JobMonitor jobMonitor = new JobMonitor(true);
//            jobMonitor.startAsStandAloneApplication(new VRL("file:"+Global.getUserHome()+"/myjobs.jids"));
//        }
//        catch (VlException e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void StartVBrowser(String args[])
//    {
//        nl.uva.vlet.gui.StartVBrowser.main(args);
//    }
//
//}
