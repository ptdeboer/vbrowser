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

//package icons;
//
//import java.awt.BorderLayout;
//import java.net.URL;
//
//import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.border.BevelBorder;
//
//import nl.uva.vlet.gui.icons.AnimatedIcon;
//import nl.uva.vlet.gui.image.AnimatedImage;
//import nl.uva.vlet.gui.icons.IconAnimator;
//import nl.uva.vlet.gui.image.ImageUtil;
//
//public class TestAnimatedImageIcon
//{
//
//    public static void main(String args[])
//    {
//        try
//        {
//            AnimatedImage image=null;
//            ImageIO.read(new URL("file:/home/ptdeboer/gif/test.gif"));
//            
//            image=ImageUtil.loadGifImage(new URL("file:/home/ptdeboer/gif/test.gif"));  
//            
//            // wrape ImageIcon around AnimatedImage 
//            ImageIcon icon =new ImageIcon(image); 
//            
//            //animicon = new AnimatedIcon("file:/home/ptdeboer/gif/test.gif");
//            
//            //animicon.setImageObserver(animicon.new AnimationObserver());
//            
//            JFrame frame=new JFrame();
//            JPanel panel=new JPanel(); 
//            frame.setLayout(new BorderLayout());
//            frame.add(panel,BorderLayout.CENTER);
//            panel.setLayout(new BorderLayout());
//            panel.setBorder(new BevelBorder(BevelBorder.LOWERED)); 
//            JLabel label=new JLabel("test icon"); 
//            label.setIcon(icon); 
//            label.setBorder(new BevelBorder(BevelBorder.LOWERED));
//            
//            // IconAnimator.getDefault().register(label,animicon); 
//            
//            panel.add(label,BorderLayout.CENTER);
//            frame.pack();
//            frame.setVisible(true); 
//            
//            
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        } 
//        
//    }
//}
