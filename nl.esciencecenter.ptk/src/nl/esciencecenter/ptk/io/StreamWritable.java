/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.io;

import java.io.OutputStream;

public interface StreamWritable 
{
	OutputStream createOutputStream() throws Exception; 
}
