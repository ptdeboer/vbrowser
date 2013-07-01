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

package nl.nlesc.vlet.vfs.srm;

//import eu.geclipse.efs.srm.SRMStore;
import gov.lbl.srm.v22.stubs.ArrayOfAnyURI;
import gov.lbl.srm.v22.stubs.ISRM;
import gov.lbl.srm.v22.stubs.SrmPutDoneRequest;
import gov.lbl.srm.v22.stubs.SrmPutDoneResponse;
import gov.lbl.srm.v22.stubs.TStatusCode;

import java.io.IOException;
import java.io.OutputStream;

import nl.esciencecenter.glite.lbl.srm.SRMException;
import nl.esciencecenter.glite.lbl.srm.status.SRMPutRequest;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.exception.ResourceCreationFailedException;
import nl.nlesc.vlet.exception.ResourceNotFoundException;

/**
 * Code copied from geclipse, but modified to fit the VRS/VFS interface. ---
 * Original text: This is a wrapper class for GSIFTP outgoing transfers
 * handling. Note: you have to fill out token and ArrayOfTSURLs
 */
public class SRMOutputStream extends OutputStream 
{
	private OutputStream stream;
	
	private SRMFileSystem srmClient;
	
	boolean closedOK = false;

	private boolean replaceFileAfterClose;

	private String finalFilepath;

	private String tmpFilepath;

    private SRMPutRequest putRequest;

	/**
	 * Provides additional functionality to transfer streams. SRM needs to
	 * invoke SRM_Put_Done when upload is finished.
	 * 
	 * @param _stream
	 *            output (upload) stream after which Put_Done will be invoked
	 * @param _service
	 *            SRM Web Service endpoint used to invoke Put_Done
	 */
	public SRMOutputStream(SRMPutRequest request,final OutputStream _stream, final SRMFileSystem client)
	{
		super();
		// this.srmStore = store;
		this.stream = _stream;
		this.srmClient = client;
		this.putRequest=request; 
	}
	

	/**
	 * @return the request token
	 */
	public String getToken() 
	{
		return putRequest.getToken();
	}
	
	@Override
	public void close() throws IOException 
	{
//		Global.infoPrintln(this,"Closing srmTransfer to"+tmpFilepath);
//		Global.infoPrintln(this," - renaming to file file:"+finalFilepath); 
	
		// Piter Says: set to false before close !
		this.closedOK = false;
		
		try
		{
			this.stream.close();
		}
		catch (Exception e)
		{
			//Global.warnPrintln(this,"Couldn't close outputstream. Already closed ?:"+e);
		}
		
		// will throw exception is failed: 
		
		try
        {
            this.closedOK = this.srmClient.finalizePutRequest(putRequest,true);
        }
        catch (SRMException ex)
        {
            throw new IOException(ex.getMessage(),ex); 
        } 
		
		if (this.replaceFileAfterClose) 
		{
			try 
			{
				renameTempFile(this.finalFilepath);
			}
			catch (VrsException e) 
			{
				IOException ex = new IOException(e.getMessage());
				ex.initCause(e);
				throw ex;
			}
		}
		
		replaceFileAfterClose = false;
	}

	private void renameTempFile(String finalFilepath) throws VrsException 
	{
		// if previous doesn' t exist it doesn have to be deleted
		try
		{
			srmClient.deleteFile(finalFilepath);
		}
		catch (Exception e)
		{
			// do extra check: 
			if (srmClient.pathExists(finalFilepath)==true)
			{
//				; // todo throw exception ? Move will probably fail 
//				if (e instanceof VlException)
//				{
//					throw ((VlException)e); 
//				}
//				else
				{
					throw new ResourceCreationFailedException(" Failed to delete original file:"+finalFilepath,e); 
				}
			}
			else
			{
				; // is ok: orignal file doesn't exist anwyay  
			}
		}
		
		srmClient.mv(this.tmpFilepath, finalFilepath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(final int arg0) throws IOException {
		this.stream.write(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		this.stream.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(final byte[] arg0, final int arg1, final int arg2)
			throws IOException {
		this.stream.write(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(final byte[] arg0) throws IOException 
	{
		this.stream.write(arg0);
	}

	public void setFinalPathAfterClose(String tmpPath, String newPath) 
	{
		this.replaceFileAfterClose = true;
		this.tmpFilepath = tmpPath;
		this.finalFilepath = newPath;
	}

}
