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

package nl.esciencecenter.ptk.ui.icons;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import nl.esciencecenter.ptk.ui.util.UIResourceLoader;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/** 
 * Simple Icon provider class which searched for icons in 
 * both user and installation directories. 
 * Also checks and searches for mimetype icon 
 */ 
public class IconProvider
{
    // ==========================================================================
    // Class
    // ==========================================================================

    private static JFrame source=null;
    
    private static IconProvider instance=null;

    private static ClassLogger logger;

    static
    {
       logger=ClassLogger.getLogger(IconProvider.class);
       //logger.setLevelToDebug();
    }
    
    public static synchronized IconProvider getDefault()
    {
        if (source==null)
            source=new JFrame(); 
        
        if (instance==null)
            instance=new IconProvider(source,UIResourceLoader.getDefault()); 
            
        return instance; 
    }
    
	// ==========================================================================
	// Instance
	// ==========================================================================

	/** Use image render cache for pre-rendered icons. */ 
	private Hashtable<String,Image> iconHash=new Hashtable<String,Image>();

	/** path prefix for the mimetype icons: <theme>/<size>/<type>  */ 
	private String mime_icons_theme_path="gnome/48x48/mimetypes";

	/** default file icon */ 
	private String file_icon_url="filesystem/file.png"; 

	/** default folder icon */ 
	private String folder_icon_url="filesystem/folder.png";

	/** default home folder icon */
	private String home_icon_url="filesystem/home_folder.png";

    private String brokenimage_url= "generic/brokenimage.png"; 
    
    private String link_icon_url="generic/linkimage.png";
    
	private ImageRenderer iconRenderer;

    private UIResourceLoader resourceLoader;

    private Image brokenImage; 
    
    private Image miniLinkImage;

    // ==========================================================================
    // Constructor/Initializers
    // ==========================================================================

	//private static IconRenderer iconRenderer=new IconRenderer();

    /** IconProvider with optional AWT Image Source and custom resource loader. */ 
    public IconProvider(Component source,UIResourceLoader resourceLoader) 
    {
        this.resourceLoader=resourceLoader;
        this.iconRenderer=new ImageRenderer(source);
        initDefaultImages(); 
    }
    
    /** IconProvider with optional AWT Image Source and custom (url) search path */ 
    public IconProvider(Component source,URL urls[]) 
    {
        this.resourceLoader=new UIResourceLoader(urls); 
        this.iconRenderer=new ImageRenderer(source);
        initDefaultImages(); 
    }

	private void initDefaultImages()
	{  
	    try
        {
            this.brokenImage=resourceLoader.getImage(brokenimage_url);
            this.miniLinkImage=resourceLoader.getImage(link_icon_url);
            this.iconRenderer.setLinkImage(miniLinkImage); 
        }
        catch (IOException e)
        {
            logger.logException(ClassLogger.ERROR,e,"Couldn't initiliaze default icons\n");
        } 
	}
	
    public void setMimeIconPath(String mimeiconpath)
    {
        mime_icons_theme_path=mimeiconpath; 
    }

	public ClassLoader getClassLoader()
	{
	    return Thread.currentThread().getContextClassLoader(); 
	}
	   
    /** Return broken image icon */ 
    public Icon getBrokenIcon()
    {
        return this.createImageIcon(this.brokenImage); 
    }
     
    public Icon getMiniLinkIcon()
    {
        return this.createImageIcon(this.miniLinkImage); 
    }
	
	// ========================
	// Icon Factory Methods. 
	// ========================
	
	/**
     * Create a default icon in the following order: 
     * <ul>
     * <li>I) First checks optional iconURL if it can be resolved. 
     * <li>IIa) Checks mimetype for mime type icon or
     * <li>IIb) Uses default file or folder icon depending on isComposite. 
     * <li>III) Scales to size, optionally adds link icon and performs grey out.
     *</ul>  
     */
    public Icon createDefaultIcon(String iconUrl,
            boolean isComposite,
            boolean isLink,
            String mimetype,
            int size,
            boolean greyOut)
    {
        logger.debugPrintf("createDefaultIcon [%d,%b,%b,%b]\n",size,greyOut,isComposite,isLink); 

        // for plugins, must use classLoader of plugin class ! 
        ClassLoader classLoader=getClassLoader(); 
        
        // custom Icon URL: 
        if (StringUtil.isEmpty(iconUrl)==false) 
        {
            logger.debugPrintf("createDefaultIcon: I)");
            
            Icon icon=createIcon(classLoader,iconUrl,isLink,size,greyOut);
         
            if  (icon!=null)
            {
                logger.debugPrintf("createDefaultIcon: I) not NULL\n");
                return icon;
            }
            logger.debugPrintf("createDefaultIcon: I)  NULL\n");
        }

        // default mimetype for Composite nodes.
        // Set to null to trigger 'default' icons further in this method: 
        // 
        if ( (mimetype!=null) && (isComposite) 
                && (mimetype.compareToIgnoreCase("application/octet-stream")==0) 
           )
        {
            mimetype=null;
        }
        
        // =============================================================================
        // MimeType Icons 
        // =============================================================================

        if ((iconUrl==null) && (mimetype!=null))
        {
            iconUrl=createMimeTypeIconPath(mimetype);
        
            // try again using full (theme) mimetype path: ./<themes path>/iconURL
            Icon icon = createIcon(classLoader,iconUrl,isLink,size,greyOut);
            
            if (icon!=null)
            {
                logger.debugPrintf("createDefaultIcon: using theme mimetype IIb):%s\n",iconUrl);
                return icon;
            }
        }
                
        // =============================================================================
        // Default Resource Icons (File,Folder,...) 
        // =============================================================================

        logger.debugPrintf("createDefaultIcon: III):%s\n",iconUrl);
        
        if (isComposite)
        {
            iconUrl = folder_icon_url;
        }
        else
        {
            iconUrl = file_icon_url;
        }
        
        logger.debugPrintf("createDefaultIcon: IV):%s\n",iconUrl);
        
        Icon icon = createIcon(classLoader,iconUrl,isLink,size,greyOut);

        if (icon!=null)
            return icon;
        
        return createIcon(classLoader,file_icon_url,isLink,size,greyOut);
    }
    	
	/**
	 * Returns Icon or broken image icon.
	 * Creates ImageIcon directly from URL, works with animated GIFs as
	 * the icon is not changed
	 */ 
	public Icon createIconOrBroken(ClassLoader optClassLoader,String url)
	{
		logger.debugPrintf("createIconOrDefault:%s\n",url); 
		// Find image and create icon. No Rendering!
		
		URL resolvedUrl=resourceLoader.resolveUrl(optClassLoader,url); 
		
		Icon icon=null;
		if (resolvedUrl!=null)
		    icon=new ImageIcon(resolvedUrl); 

		if (icon!=null)
			return icon;

		logger.debugPrintf("Returning broken icon for:%s\n",url);
		return getBrokenIcon(); 
	}
	
	// ========================
	// Icon Render methods 
    // ========================
    

    /**
     * Resolve Icon URL and create icon.   
     * @see IconProvider#createIcon(ClassLoader, String, boolean, Dimension, boolean) 
     */
    public Icon createIcon(String iconURL)
    {
        return this.createIcon(null,iconURL,false,null,
                false); 
    }
    
    /**
     * Resolve Icon URL and creates icon. Renders it to the specified size.    
     * Caches result. 
     * @see IconProvider#createIcon(ClassLoader, String, boolean, Dimension, boolean) 
     */
    public Icon createIcon(String iconurl, int size)
    {
        return this.createIcon(null,iconurl,false,new Dimension(size,size),false); 
    }
    
    /** Find icon or return null */ 
    public Icon createIcon(ClassLoader extraLoader, String iconURL)
    {
        return this.createIcon(extraLoader,iconURL,false,null,false); 
    }

    /**
     * Render a scaled icon and cache it. 
     * Warning: Method does NOT yet work with animated Icons ! 
     */
	public Icon createIcon(ClassLoader optClassLoader,
	        String iconURL,
			boolean showAsLink,
			int size,
			boolean greyOut)
	{
		return createIcon(optClassLoader,
		        iconURL,
		        showAsLink,
		        new Dimension(size,size),
		        greyOut);
	}


    /**
     * Create icon specified bu the iconURL string. 
     * Optionally scale icon, add a mini linkicon or perform
     * a grey out. 
     * The resulted icon image is cached for reuse, but the returned Icon object is always
     * a new Icon Object. 
     * Warning: Method does NOT yet work with animated Icons !
     */
	public Icon createIcon(ClassLoader optClassLoader,
			String iconURL,
			boolean showAsLink,
			Dimension prefSize,
			boolean greyOut)
	{
		logger.debugPrintf("createIcon(): %s{%b,%s,%b}\n",iconURL,showAsLink,prefSize,greyOut);

		if (iconURL==null)
			return null;

		Image image=getImageFromHash(iconURL,showAsLink,prefSize,greyOut);

		if (image!=null)
		{
			logger.debugPrintf("Returning icon created from hashed image: %s{%b,%s,%b}\n",iconURL,showAsLink,prefSize,greyOut);
			return createImageIcon(image);  
		}

		image=findImage(optClassLoader,iconURL);

		// get default broken icon ?
		if (image==null)
		{
			logger.debugPrintf("createIcon: null icon for:%s\n",iconURL); 
			return null; 
		}

		image=iconRenderer.renderIconImage(image,showAsLink,prefSize,greyOut);
		
		if (image!=null)
		{
			putImageToHash(image,iconURL,showAsLink,prefSize,greyOut);
			return createImageIcon(image);  
		}
		else
		{
			logger.debugPrintf("createIcon: *** Error: renderIcon failed for non null icon:%s\n",iconURL); 
		}
		
		return null; 
	}
	
    /** Create Icon from Image, result is NOT cached */ 
	public Icon createImageIcon(Image image)
	{
	    // NULL Pointer save:
	    if (image==null)
	        return null; 
	    
	    return new ImageIcon(image); 
	}
	
	/**
	 * Try to find a specified image, but do no throw an (IOL)Exception if it can't be found. 
	 * Method will return 'null' if the case an image can't be found. 
	 */
    public Image findImage(ClassLoader extraLoader,String iconURL)
    {
		URL resolvedurl=resourceLoader.resolveUrl(extraLoader,iconURL); 

		// return url 
		if (resolvedurl!=null)
		{
			logger.debugPrintf("findIcon:found Icon:%s\n",resolvedurl);

			// Basic checks whether the icon is a valid icon ?  
			try
			{
    			Image image=loadImage(resolvedurl,true);
    			
    			if (image!=null)
    			{
    				logger.debugPrintf("findIcon:returning non null icon:%s\n",resolvedurl);
    				return image; 
    			}
 			}
			catch (IOException e)
			{
			    logger.logException(ClassLogger.DEBUG,e,"Exception when loading image:%s\n",iconURL);
			}
		
		}

		logger.warnPrintf("Couldn't find (icon) image:%s\n",iconURL);
		
		return null; 
	}
	
	public String createMimeTypeIconPath(String mimetype)
	{
		// tranform mimetype "<type>/<subtype>" into filename
		// "<type>-<subtype>"

	    // replace "/" with "-": 
		String iconpath=mimetype.replace("/","-"); 
		
		iconpath=this.mime_icons_theme_path+"/"+iconpath+".png"; // .gif ?
		
		// make absolute URL ! 
		if (this.mime_icons_theme_path.startsWith("/"))
		    iconpath="file:"+iconpath; 
		
		return iconpath;
	}
    
	/** Loads (icon) image or throw IOException if it can't be found */ 
    public Image loadImage(URL url) throws IOException
    {
        return loadImage(url,true); 
    }
	
	/** 
	 * Load imageIcon, optionally uses cache.
	 * Use this method only for relative small icons and NOT for big images
	 * as the image are put into the cache for reuse. 
	 *  
	 * @throws IOException 
	 */
	public Image loadImage(URL url, boolean useCache) throws IOException
    {
	    // get/create "raw" icon from cache;  
	    Image image=null;
	    
	    if (useCache)
	    {
	        image=this.getImageFromHash("raw-"+url.toString(),false,null,false); 
	        if (image!=null)
	            return image;
	    }
	    
	    logger.infoPrintf("loading new icon image:%s\n",url); 
        String urlStr=url.toString().toLowerCase();
        // Direct .ico support: do not use resource loader to resolve icons
        if (urlStr.endsWith(".ico"))
        {
            image=resourceLoader.getIcoImage(url);
        }
        else
        {
            // Uses java 1.5 ImageIO! 
            image=resourceLoader.getImage(url); 
        }
        
        if (image!=null)
        {
            if (useCache)
                this.putImageToHash(image,"raw-"+url.toString(),false,null,false);
            
            return image;
        }
        else
        {
            throw new IOException("ImageIO Returned NULL image for url:"+url);
        }
    }

	private void putImageToHash(Image image,String iconURL, boolean showAsLink, Dimension size, boolean greyOut)
	{
		if (image==null)
			return; 

		synchronized(this.iconHash)
        {
    		String id=createHashID(iconURL,showAsLink,size,greyOut); 
    		this.iconHash.put(id,image);
        }
	}

	private String createHashID(String iconURL, boolean showAsLink,Dimension size, boolean greyOut)
	{
		String sizeStr="-";
		if (size!=null)
			sizeStr=size.height+"-"+size.width;
		
		return iconURL+"-"+showAsLink+"-"+sizeStr+"-"+greyOut; 
	}

	private Image getImageFromHash(String iconURL, boolean showAsLink, Dimension size, boolean greyOut)
	{
	    synchronized(this.iconHash)
	    {
    		String id=createHashID(iconURL,showAsLink,size,greyOut);
    		Image image=this.iconHash.get(id);
    		logger.debugPrintf("> getIconFromHash:%s for '%s'\n",((image!=null)?"HIT":"MISS"),id);
    		return image; 
	    }
	}

	/** Clear Icon Cache */ 
	public void clearCache()
	{
		this.iconHash.clear(); 
	}

    public Icon getFileIcon(int size)
    {
        return this.createIcon(this.file_icon_url,size); 
    }
    
    public Icon getFolderIcon(int size)
    {
        return this.createIcon(this.folder_icon_url,size); 
    }
    
    public Icon getHomeFolderIcon(int size)
    {
        return this.createIcon(this.home_icon_url,size); 
    }
   
 
}
