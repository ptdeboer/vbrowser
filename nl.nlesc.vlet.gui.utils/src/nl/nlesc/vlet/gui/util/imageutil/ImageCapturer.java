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

package nl.nlesc.vlet.gui.util.imageutil;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/** Simple Image Caputurer of a JComponent */ 

public class ImageCapturer
{
	private JComponent sourceComponent;

	public ImageCapturer(JComponent source)
	{
		this.sourceComponent=source; 
	}

	public Image captureContents(Dimension size)
	{
		Dimension orgSize = sourceComponent.getSize(); 
		
		BufferedImage orgImage=new BufferedImage(orgSize.width,orgSize.height,BufferedImage.TYPE_INT_ARGB); 
		Graphics2D graphics = orgImage.createGraphics();
		// use print() method ! 
		 
		sourceComponent.printAll(graphics); 
		
		Graphics sourceGraphics = sourceComponent.getGraphics();
		Component[] comps = sourceComponent.getComponents(); 
		for (Component comp:comps)
			comp.paint(graphics); 
		
		return orgImage; 
	}
}
