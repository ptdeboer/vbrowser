package nl.vbrowser.ui.iconspanel;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.TransferHandler;

import nl.nlesc.ptk.net.VRI;
import nl.nlesc.ptk.ui.fonts.FontInfo;
import nl.vbrowser.ui.dnd.DnDUtil;
import nl.vbrowser.ui.dnd.ViewNodeDropTarget;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.model.ViewContainerEventAdapter;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeComponent;
import nl.vbrowser.ui.model.ViewNodeContainer;

;

/** 
 * Rewrite 
 * @author piter
 */
public class IconItem extends JLabel implements ViewNodeComponent, FocusListener
{
    private static final long serialVersionUID = 4212206705193994742L;
    private FontInfo fontInfo;
    private int max_icon_width; 
    private ViewNode viewNode;
    private boolean selected;
    private UIViewModel uiModel;


    public IconItem(ViewNodeContainer parent, UIViewModel uiModel,ViewNode item) 
    {
        init(parent,uiModel,item); 
    }

    private void init(ViewNodeContainer parent,UIViewModel uiModel,ViewNode node) 
    {
        this.viewNode=node; 
        this.uiModel=uiModel; 
        
        initModel(uiModel); 

        this.setIcon(viewNode.getIcon());
        updateLabelText(viewNode.getName(),false); 

        boolean visible=true; 

        // Label Font + Text 
        {
            // move to UIModel ? 
            fontInfo=FontInfo.getFontInfo(FontInfo.FONT_ICON_LABEL);
            setForeground(this.fontInfo.getForeground());
            this.setFont(fontInfo.createFont());
        }

        // Label placement:
        if (uiModel.getIconLabelPlacement()==UIViewModel.UIDirection.VERTICAL)
        {
            if (visible)
                this.setIconTextGap(8);
            else
                this.setIconTextGap(4);

            this.setVerticalAlignment(JLabel.TOP);
            this.setHorizontalAlignment(JLabel.CENTER);

            this.setVerticalTextPosition(JLabel.BOTTOM);
            this.setHorizontalTextPosition(JLabel.CENTER);
        }
        else
        { 
            this.setIconTextGap(4); 
            this.setVerticalAlignment(JLabel.CENTER);
            this.setHorizontalAlignment(JLabel.LEFT);

            this.setVerticalTextPosition(JLabel.CENTER);
            this.setHorizontalTextPosition(JLabel.RIGHT);
        }
        // === Listeners === 
        this.setFocusable(true);
        // handle own focus events. 
        this.addFocusListener(this);  
        // this.addMouseListener(handler); 
    }
    
    public void setViewComponentEventAdapter(ViewContainerEventAdapter handler)
    {
        // IconItem handles focus event, VCEAdaptor handles mouse events (?)
        // this.addFocusListener(handler); 
        this.addMouseListener(handler); 
    }


    private void initModel(UIViewModel uiModel) 
    {
        this.max_icon_width=uiModel.getMaxIconLabelWidth(); 
    }

    protected void initDND(TransferHandler transferHandler, DragGestureListener dragListener)
    {
        // One For All: Transfer Handler: 
        //icon.setTransferHandler(VTransferHandler.getDefault());
        
        // reuse draglistener from iconsPanel:
        DragSource dragSource=DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, dragListener );
       
        // Specify DROP target: 
        this.setDropTarget(new ViewNodeDropTarget(this));
        // Have to set Keymapping to my component 
        nl.vbrowser.ui.actions.KeyMappings.addCopyPasteKeymappings(this); 
        
        this.setTransferHandler(transferHandler);   
    }
    
    public void updateLabelText(String text,boolean hasFocus)
    {
        if (text==null)
            text=""; 

        String htmlText= "<html>";

        if (hasFocus)
            htmlText+="<u>";
        
        // from model ? 
        if (max_icon_width<0) 
            this.max_icon_width=180; 
        
        // Calculate Font Size 
        Font font = getFont(); 
        FontMetrics fmetric = getFontMetrics(font);
        
        // get 'widest'character
        int charWidth=fmetric.charWidth('w'); //leniance  
        //System.err.println("charwidth="+charWidth);

        //int lines=0; 
        int len=text.length();  
        //int width=0; 
        int i=0; 

        while(i<len) 
        {
            // html expantion
            int compensate=0; 

            String linestr=""; 
            // arg must tweak label size: 
            while ((i<len) && (fmetric.stringWidth(linestr)<(max_icon_width-2*charWidth+compensate))) 
            {
                switch (text.charAt(i))
                {
                    // filter out special HTML characters: 
                    // only a small set needs to be filtered:
                    case '/':
                    case '!':
                    case '@':
                    case '#': 
                    case '$': 
                    case '%': 
                    case '^':
                    case '&':
                    case '*':
                    case '<':
                    case '>':
                        // use numerical ASCII value: 
                        String str="&#"+(int)text.charAt(i)+";"; 
                        linestr+=str; 
                        // compensate for HTML expansion (minus character,plus pixel) 
                        compensate+=(str.length()-1)*charWidth;  
                        break; 
                    default: 
                        linestr+=text.charAt(i);
                        break;
                }

                i++; 
            }

            htmlText+=linestr;
            // hard line ?
            if (i<len) 
                htmlText+="<br>";
        }

        if (hasFocus)
            htmlText+="</u>"; 

        htmlText+="</html>"; 

        setText(htmlText);
        
    }

    public ViewNode getViewNode() 
    {
        return this.viewNode; 
    }

    public boolean hasLocator(VRI locator)
    {
        return this.viewNode.getVRI().equals(locator); 
    }

    public void updateFocus(boolean hasFocus)
    {
        updateLabelText(viewNode.getName(),hasFocus); 
        repaint();
    }

    @Override
    public UIViewModel getUIViewModel()
    {
        return this.uiModel; 
    }

    public boolean hasViewNode(ViewNode node)
    {
        return this.viewNode.equals(node); 
    }
    
    public boolean requestFocus(boolean value)
    {
        // forward to focus subsystem ! 
        return this.requestFocusInWindow(); 
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        this.updateFocus(true); 
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        this.updateFocus(false);
    }

    @Override
    public ViewNodeContainer getViewContainer()
    {
        // parent MUST be IconPanel;
        Container parent = this.getParent();
        return (IconsPanel)parent; 
    }
    
    public void setSelected(boolean val)
    {
        this.selected=val;
        
        if (val)
        {
            setIcon(viewNode.getSelectedIcon());
            //textLabel.setOpaque(true);
            //prev_label_color=textLabel.getForeground();
            setForeground(uiModel.getFontHighlightColor()); 
        }
        else
        {
            setIcon(viewNode.getIcon());
            //textLabel.setOpaque(false);
            setForeground(uiModel.getFontColor()); 
        }
        
        this.repaint();
    }
    
    public boolean isSelected()
    {
    	return this.selected; 
    }

}