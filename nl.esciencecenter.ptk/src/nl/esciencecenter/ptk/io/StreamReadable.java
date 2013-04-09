/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.io;

import java.io.InputStream;

public interface StreamReadable 
{
	InputStream createInputStream() throws Exception; 
}
