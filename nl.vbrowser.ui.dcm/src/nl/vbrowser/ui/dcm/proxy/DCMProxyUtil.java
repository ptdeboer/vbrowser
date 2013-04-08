package nl.vbrowser.ui.dcm.proxy;

import java.io.IOException;

import nl.nlesc.medim.dicom.DicomUtil;
import nl.nlesc.medim.dicom.DicomWrapper;
import nl.nlesc.ptk.io.FSNode;

public class DCMProxyUtil
{

    public static String[] getDicomTagNames(FSNode fsNode, boolean sort) throws IOException
    {
        DicomWrapper wrap;
        wrap = getDicom(fsNode);
        return wrap.getTagNames(sort); 
        
    }

    public static DicomWrapper getDicom(FSNode fsNode) throws IOException
    {
        return new DicomWrapper(DicomUtil.readDicom(fsNode.getPath()));
    }

}
