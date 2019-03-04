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

package test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.esciencecenter.vlet.gui.util.FileSelector;


public class TestFileSelector extends JFrame 
{
    private static final long serialVersionUID = 5029201673262384269L;
    
    static FileSelector dialogVBro = new FileSelector();
    
    public TestFileSelector() {
        super();
        init();
        
    }

    private void init() {
    	
    	this.getContentPane().setLayout(new FlowLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
       
        JButton button = new JButton("Show Save Dialog");
        button.setPreferredSize(new Dimension(400,400));
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                	dialogVBro.setTypeDialog(FileSelector.SAVE_DIALOG);
                	dialogVBro.openDialog(); 
                	if(dialogVBro.currentLocation!=null)
                		JOptionPane.showMessageDialog(null, dialogVBro.currentLocation);
                
                }
            });
        this.getContentPane().add(button);
                
      
        JButton button1 = new JButton("Show Open Dialog");
        button1.setPreferredSize(new Dimension(400,400));
        button1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                	dialogVBro.setTypeDialog(FileSelector.OPEN_DIALOG);
                	dialogVBro.openDialog();   
                	if(dialogVBro.currentLocation!=null)
                		JOptionPane.showMessageDialog(null, dialogVBro.currentLocation);
                	             	
                }
            });
        this.getContentPane().add(button1);
        
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        TestFileSelector frame = new TestFileSelector();
        frame.pack();
        frame.setVisible(true);
    }
}
