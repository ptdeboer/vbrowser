package nl.esciencecenter.vbrowser.vb2.ui.widgets;

import javax.swing.Icon;
import javax.swing.JLabel;

import nl.esciencecenter.ptk.ui.icons.AnimatedIcon;
import nl.esciencecenter.ptk.ui.icons.IconAnimator;

/**
 * JLabel subclass with some convenience methods which supports AnimatedIcon as the Icon.
 * This way animated gifs, etc can be stopped, restarted or the animation speed can be changed. 
 *
 */
public class JAnimatedLabel extends JLabel
{
    private static final long serialVersionUID = -1199581037837264677L;
    
    public JAnimatedLabel(AnimatedIcon icon)
    {
        super(icon); // will call setIcon() ! 
    }
    
    public void setIcon(Icon icon)
    {
        super.setIcon(icon); 
    }
    
    /** Explicitly set AnimatedIcon */ 
    public void setAnimatedIcon(AnimatedIcon icon) 
    {
        setIcon(icon); 
    }
    
    public void start()
    {
        if (isAnimated())
            getAnimatedIcon().start(); 
    }
    
    public void stop()
    {
        if (isAnimated())
            getAnimatedIcon().stop(); 
    }
    
    public void reset()
    {
        if (isAnimated())
            getAnimatedIcon().reset(); 
    }
    
    public void setAnimationSpeed(double speed)
    {
        if (isAnimated())
            getAnimatedIcon().setAnimationSpeed(speed);  
    }
    
    public AnimatedIcon getAnimatedIcon()
    {
       Icon icon=this.getIcon(); 
       if (icon instanceof AnimatedIcon)
           return (AnimatedIcon)icon;
       
       return null;  
    }
    
    /** Returns true is the Icon is of AnimatedIcon class */ 
    public boolean isAnimated()
    {
        return (getAnimatedIcon()!=null); 
    }
    
    public void dispose()
    {
        AnimatedIcon icon=this.getAnimatedIcon();
 
        // unregister and dispose; 
        if (icon!=null)
        {
            IconAnimator.getDefault().unregister(icon);
            icon.dispose();
        }
    } 
    
}
