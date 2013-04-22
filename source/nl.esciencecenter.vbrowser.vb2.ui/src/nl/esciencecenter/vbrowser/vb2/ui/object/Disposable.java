package nl.esciencecenter.vbrowser.vb2.ui.object;

public interface Disposable
{
    /**
     * Expose UI object. 
     * Release UI resource. 
     * Object won't be used after this call. 
     */
    public void dispose();
    
}
