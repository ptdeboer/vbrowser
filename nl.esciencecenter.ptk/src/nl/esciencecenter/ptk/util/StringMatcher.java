/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringMatcher
{
    // === Static === 
        
    public static StringMatcher createREMatcher(String regularExpr,boolean matchCase)
    {
        return new StringMatcher(regularExpr,matchCase); 
    }
    
    public static StringMatcher createWildcardMatcher(String wildCardExpr,boolean matchCase)
    {
        return new StringMatcher(Wildcard2Regex.wildcardToRegex(wildCardExpr),matchCase); 
    }

    // === Instancee === 

    protected Pattern pattern=null; 
    
    protected int flags=0;

    public StringMatcher(String pattern)
    {
        this.flags=0;
        this.pattern=Pattern.compile(pattern, flags);
    }
    
    public StringMatcher(String pattern, boolean matchCase)
    {
        this.flags=0;
        if (matchCase==false)
            this.flags|=Pattern.CASE_INSENSITIVE; 
        this.pattern=Pattern.compile(pattern, flags);
    }
    
    public boolean matches(String value, boolean completeMatch)
    {
        Matcher m=pattern.matcher(value);  
        boolean prefixMatch=m.matches();
        //m.toMatchResult()
        return prefixMatch; 
    }


}
