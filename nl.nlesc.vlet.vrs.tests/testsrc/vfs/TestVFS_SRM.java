package vfs;

abstract public class TestVFS_SRM extends TestVFS
{

    /** SRM does not support URI encoding ! */
    boolean getTestEncodedPaths()
    {
        return false; 
    }
    
    /** Not strange chars please */ 
    boolean getTestStrangeCharsInPaths()
    {
        return false;
    }
    
}
