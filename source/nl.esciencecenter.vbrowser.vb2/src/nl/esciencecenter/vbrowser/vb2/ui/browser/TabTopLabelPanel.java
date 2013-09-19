package nl.esciencecenter.vbrowser.vb2.ui.browser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class TabTopLabelPanel extends JPanel
{
    private static final long serialVersionUID = 5566174001482465094L;

    public static enum TabButtonType {Delete,Add}; 

    private final TabContentPanel pane;

    private final JTabbedPane parentPane;

    private TabButton addButton;

    private TabButton delButton;

    public TabTopLabelPanel(final JTabbedPane parentPane, final TabContentPanel pane)
    {
        // unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.parentPane = parentPane;
        this.pane = pane;

        if ( (parentPane==null) || (pane == null))
        {
            throw new NullPointerException("Tab pane or Parent TabbedPane is null");
        }

        setOpaque(false);

        // make JLabel read titles from JTabbedPane
        JLabel label = new JLabel()
        {
            public String getText()
            {
                return pane.getName();
            }
        };

        add(label);
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // tab button

        {
            delButton = new TabButton(TabButtonType.Delete);
            add(delButton);
            delButton.setActionCommand(TabNavigationBar.CLOSE_TAB_ACTION); 
        }
        {
            addButton = new TabButton(TabButtonType.Add);
            add(addButton);
            addButton.setActionCommand(TabNavigationBar.NEW_TAB_ACTION); 
        }
        
        // add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
    
    public void setActionListener(ActionListener listener)
    {
        this.delButton.addActionListener(listener); 
        this.addButton.addActionListener(listener); 
       
    }
    public void setEnableAddButton(boolean value)
    {
        addButton.setVisible(value);
    }
    
    private class TabButton extends JButton // implements ActionListener
    {
        TabButtonType type; 
        
        public TabButton(TabButtonType buttonType)
        {
            int size = 17;
            this.type=buttonType; 
            
            setPreferredSize(new Dimension(size, size));
            switch (type)
            {
                case Delete:
                    setToolTipText("Close this tab");
                    break;
                case Add: 
                    setToolTipText("Copy tab");
                    break; 
                default: 
                    setToolTipText("?");
                    break; 
            }
            
            // Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            // Make it transparent
            setContentAreaFilled(false);
            // No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            // Making nice rollover effect
            // we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            // Close the proper tab by clicking the button
            // addActionListener(this);
        }

//        public void actionPerformed(ActionEvent e)
//        {
//            if (this.type==TabButtonType.Delete)
//            {
//                parentPane.remove(pane);
//            }
//            else if (this.type==TabButtonType.Add)
//            {
//                
//            }
//        }

        public void updateUI()
        {
            
        }

        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D) g.create();
            // shift the image for pressed buttons
            if (getModel().isPressed())
            {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);

            
            if (getModel().isRollover())
            {
                g2.setColor(Color.MAGENTA);
            }
            
            int delta = 5;
            if (type==TabButtonType.Delete)
            {
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            }
            else
            {
                int w=getWidth(); 
                int h=getHeight();
                g2.drawLine(w/2, delta,w/2,h-delta-1);
                g2.drawLine(delta,h/2,w-delta-1,h/2); 
            }
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter()
    {
        public void mouseEntered(MouseEvent e)
        {
            Component component = e.getComponent();
            if (component instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e)
        {
            Component component = e.getComponent();
            if (component instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}
