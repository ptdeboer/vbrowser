/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.icons;

import java.awt.Component;
import java.awt.Image;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;



/**
 * Icon Animator calling the icons 'repaint' method
 *
 */
public class IconAnimator implements Runnable
{
    public class IconInfo
    {
        long lastUpdateTime=0; 
        long newUpdateTime=0; 
        AnimatedIcon icon=null;  
        Component comp; 
        
        public IconInfo(Component comp, AnimatedIcon icon)
        {
            this.icon=icon;
            this.comp=comp; 
        }
       
        public AnimatedIcon getIcon()
        {
            return icon; 
        }
    
        /** JComponent who contains the icon */ 
        public Component getComponent()
        {
            return comp; 
        }

        /** Whether icon needs to be updated */ 
        public boolean needsUpdate(long currentTimeMillis)
        {
            if (currentTimeMillis>=newUpdateTime)
                return true;
            else
                return false; 
        }
    }
    
    private static IconAnimator instance=null;  
    
    public static IconAnimator getDefault()
    {
        if (instance==null)
        {
            instance=new IconAnimator();
            instance.start();
        }
        
        return instance;  
    }
    
    // ================================ //
    //  
    // ================================ // 
    
    private List<IconInfo> icons=new Vector<IconInfo>(); //synchronized vector 
    private Thread thread;  
    private boolean mustStop=false; 
    
    public IconAnimator()
    {
        
    }
    
    public synchronized void start()
    {
        if ((this.thread!=null) && (this.thread.isAlive()))
        {
            // already running and will not stop: 
            if (this.mustStop==false)
            {
                //debugPrintf("start(): Already running!\n");
                return;
            }
        }

        //debugPrintf("start(): Starting!\n");

        this.thread=new Thread(this);
        this.thread.start(); 
    }
    
    public synchronized void stop()
    {
        this.mustStop=true; 
    }
    
    public void register(AnimatedIcon icon)
    {
        register(null,icon);
    }

    public void unregister(AnimatedIcon icon)
    {
        synchronized(this.icons)
        {
            IconInfo iconState; 
            
            if ( (iconState=find(icon)) !=null)
            {
                this.icons.remove(iconState); 
                icon.setIconAnimator(null);
            }
        }
    }

    protected IconInfo find(AnimatedIcon icon)
    {
        for (IconInfo state:icons)
        {
            if (state.icon.equals(icon))
                return state; 
        }
        
        return null;   
    }
    
    @Override
    public void run()
    {
        while(mustStop==false)
        {
            if (this.icons.size()<=0)
            {
                //debugPrintf("run(): break: No more icons to animate! \n");
                break; 
            }
            
            int waitTime=updateAll();
            try
            {
                //debugPrintf("sleep:%d\n",waitTime);
                Thread.sleep(waitTime);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            } 
            
        }
        
        //debugPrintf("run(): stopped! \n");
    }
    
    /**
     * Update All icons which need to be updated. 
     * Returns minimum wait time for the next updated. 
     * This is the minimum all of icon wait times. 
     * 
     * @return
     */
    protected int updateAll()
    {
        // thread safe (?) iterator 
        Iterator<IconInfo> iterator = this.icons.iterator(); 
        
        int nextWaitTime=1000;  
        int waitTime=0; 
        
        while(mustStop==false && iterator.hasNext())
        {
            IconInfo next = iterator.next();
            if (next.icon.hasStopped()==false)
            {
                if (next.needsUpdate(System.currentTimeMillis()))
                {
                    waitTime=update(next);
                    if (waitTime<nextWaitTime)
                        nextWaitTime=waitTime;
                }
            }
        }
 
        return nextWaitTime; 
    }
    
    
    /**
     * Updated the icon state. 
     * Doesn't check whether the icon needs to be updated.
     * Return the wait time needed for the next update.  
     * @param state IconInfo state information.
     */
    protected int update(IconInfo state)
    {
        AnimatedIcon icon=state.getIcon();
        Component comp=state.getComponent();

        JComponent jcomp=null;
        
        if (comp instanceof JComponent)
            jcomp=(JComponent)comp; 
        
        // Next icon and update JComponent. 
        // This code assumes the first frame already has been paint since, 
        // this method paintes the *next* image! 
        
        icon.next();
        if (jcomp!=null)
        {
            // swing method this is what a JLabel effectively does!  
            jcomp.repaint();
            //debugPrintf("Calling repaint for component:%s\n",jcomp.getName());
        }
        else if (comp!=null)
        {
            // awt direct paint ? (NOT TESTED -> doesn't work with JLabel ) 
            Image image=icon.getCurrentImage(); 
            comp.imageUpdate(image, Component.FRAMEBITS, 0,0,0,0);    
        }
        else
        {
            //errorPrintf("Error: NULL (J)Component for Icon/IconInfo:%s/%s\n"+icon,state); 
        }
        
        // display time of current icon frame;  
        int waitTime=icon.getCurrentWaitTime(); 
        
        // update time and set new time: 
        state.lastUpdateTime=System.currentTimeMillis(); 
        state.newUpdateTime=state.lastUpdateTime+waitTime;
        
        //debugPrintf(" - current time/wait time=  %d/%d\n",state.lastUpdateTime,waitTime);
        
        return waitTime; // time for next update: 
    }

    public void register(Component c, AnimatedIcon icon)
    {
        //debugPrintf("register():%s\n",icon);
        synchronized(this.icons)
        {
            this.icons.add(new IconInfo(c,icon));
            icon.setIconAnimator(this); 
        }
        
        // start if not started: 
        start(); 
    }

    

    public void dispose()
    {
        this.mustStop=true;
        
        if (thread!=null)
        {
            this.thread.interrupt(); 
            this.thread=null; 
        }
        
        if (icons!=null)
        {
            for (IconInfo icon:this.icons)
                icon.icon.setIconAnimator(null);
            icons.clear(); 
        }
                 
    }
    
}
