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

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException;
import nl.esciencecenter.vlet.exception.ResourceReadAccessDeniedException;
import nl.esciencecenter.vlet.exception.ResourceToBigException;
import nl.esciencecenter.vlet.gui.dialog.ExceptionForm;


public class testExceptionForm
{

    public static void main(String[] args)
    {
        // tests asynchronous text view 
        
        ExceptionForm.show(new ResourceReadAccessDeniedException("Acces denied"));
     
           
        // tests asynchronous mainText view 
        VrsException e1=new ResourceReadAccessDeniedException(" Test 1");
        VrsException e2=new ResourceAlreadyExistsException(" Test 2");
            
        ExceptionForm.show(e1);
        ExceptionForm.show(e2);

            try 
            {
                int i=0/0; 
            }
            catch (Exception e)
            {
                ExceptionForm.show(new VrsException("Oooops",e));
            }
            
            String str=""; 
            
            for (int i=0;i<100;i++)
                str=str+"A lot of debug information. \n";
            
            ExceptionForm.show(new ResourceToBigException(str)); 
        
    }

}
