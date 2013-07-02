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

package tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import nl.esciencecenter.vlet.gui.vhtml.VHTMLKit;


public class TestVHTMLPopup
{
    private static String content = new StringBuffer()
            .append("<html><body>\n")
            .append("<a href=\"popup:It doest't have to be a valid URL\">\n")
            .append("Right click to see popup menu.</a><br>\n")
            .append("<a href=\"http://www.experts-exchange.com/jsp/qShow.jsp?qid=20143329\">\n")
            .append("You still can handle normal URLs.</a><br>\n")
            .append("And you can have mouse-insensitive text\n")
            .append("</body></html>\n")
            .toString();
        
    public static void main(String[] args) throws IOException
    {
            System.out.println(content);
            System.setErr(new PrintStream(new FileOutputStream("err.log")));
            JTextPane textPane = new JTextPane();
            textPane.setEditorKit(new VHTMLKit());
            textPane.setText(content);
            textPane.addHyperlinkListener(new VHTMLKit.LinkListener());
            textPane.setEditable(false);
            JFrame frame = new JFrame("HTML Popup Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(textPane);
            frame.pack();
            frame.setSize(300,300);
            frame.setVisible(true);
    }
        
    
    
}
