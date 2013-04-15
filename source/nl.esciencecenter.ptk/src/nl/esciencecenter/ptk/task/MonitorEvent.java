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

package nl.esciencecenter.ptk.task;

public class MonitorEvent
{
    public static enum MonitorEventType
    {
       StatusChanged,ChildMonitorAdded,ChildMonitorRemoved;
    };
    
    public ITaskMonitor source=null;
    public MonitorEventType type=null; 
    public ITaskMonitor childMonitor=null;
    
    public MonitorEvent(ITaskMonitor source)
    {
        this.source=source; 
    }
    
    public MonitorEvent(ITaskMonitor source,MonitorEventType eventType)
    {
        this.source=source;
        this.type=eventType;
    }
    
    public void setChild(ITaskMonitor child)
    {
        this.childMonitor=child; 
    }
    
    public static MonitorEvent createChildAddedEvent(ITaskMonitor source,ITaskMonitor child)
    {
        MonitorEvent event=new MonitorEvent(source,MonitorEventType.ChildMonitorAdded);
        event.setChild(child);
        return event; 
    }
    
    public static MonitorEvent createChildRemovedEvent(ITaskMonitor source,ITaskMonitor child)
    {
        MonitorEvent event=new MonitorEvent(source,MonitorEventType.ChildMonitorRemoved);
        event.setChild(child);
        return event; 
    }
    
    public static MonitorEvent createStatusEvent(ITaskMonitor source)
    {
        return new MonitorEvent(source,MonitorEventType.StatusChanged); 
    }
    
    
}
