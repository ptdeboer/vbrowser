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

package nl.nlesc.glite.lfc;

public final class Assert
{
    private Assert()
    {
    }

    public static boolean isLegal(boolean expression)
    {
        return isLegal(expression, "");
    }

    public static boolean isLegal(boolean expression, String message)
    {
        if(!expression)
            throw new IllegalArgumentException(message);
        else
            return expression;
    }

    public static void isNotNull(Object object)
    {
        isNotNull(object, "");
    }

    public static void isNotNull(Object object, String message)
    {
        if(object == null)
            throw new AssertionFailedException("null argument:" + message);
        else
            return;
    }

    public static boolean isTrue(boolean expression)
    {
        return isTrue(expression, "");
    }

    public static boolean isTrue(boolean expression, String message)
    {
        if(!expression)
            throw new AssertionFailedException("assertion failed: " + message);
        else
            return expression;
    }
}

