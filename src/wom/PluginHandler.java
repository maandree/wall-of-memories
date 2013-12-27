/**
 * wall-of-memories — A photo management program
 * 
 * Copyright © 2013  Mattias Andrée (maandree@member.fsf.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wom;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;


/**
 * This class is used to enable plug-in support
 * 
 * @version  1.0
 */
public class PluginHandler
{
    /**
     * The name of the plug-in classes
     */
    private static final String PLUGIN_CLASS_NAME = "Plugin";
    
    
    
    /**
     * The plugin list file
     */
    public static String pluginFile;
    
    /**
     * The plug-ins instanciated
     */
    private static Vector<PluginV1> pluginInstances = new Vector<PluginV1>();
    
    /**
     * The plug-in files, list
     */
    private static Vector<String> pluginFiles = new Vector<String>();
    
    /**
     * The plug-in files, set
     */
    private static HashSet<String> pluginHash = new HashSet<String>();
    
    /**
     * The active plug-ins
     */
    private static HashSet<PluginV1> activePlugins = new HashSet<PluginV1>();
    
    /**
     * Map from plug-in file to last modification date
     */
    private static HashMap<String, Long> pluginMDates = new HashMap<String, Long>();
    
    
    
    /**
     * Type initialiser
     */
    static
    {
	pluginFile = "/dev/null";
	
	String HOME             = System.getProperty("user.home", null);
	String $HOME            = System.getenv("HOME");
	String $XDG_CONFIG_HOME = System.getenv("XDG_CONFIG_HOME");
	
	if ((HOME             != null) && (HOME            .length() == 0))  HOME             = null;
	if (($HOME            != null) && ($HOME           .length() == 0))  $HOME            = null;
	if (($XDG_CONFIG_HOME != null) && ($XDG_CONFIG_HOME.length() == 0))  $XDG_CONFIG_HOME = null;
	
	final Vector<String> filenames = new Vector<String>();
	
	if ($XDG_CONFIG_HOME != null)  filenames.add("$XDG_CONFIG_HOME/%1/plugins".replace("$XDG_CONFIG_HOME", $XDG_CONFIG_HOME));
	if ($HOME            != null)  filenames.add("$HOME/.config/%1/plugins"   .replace("$HOME",            $HOME));
	if ($HOME            != null)  filenames.add("$HOME/.%2.plugins"          .replace("$HOME",            $HOME));
	if (HOME             != null)  filenames.add("~/.config/%1/plugins"       .replace("~",                HOME));
	if (HOME             != null)  filenames.add("~/.%2.plugins"              .replace("~",                HOME));
	filenames.add("/etc/%2.plugins");
	
	boolean have = false;
	File file_;
	for (String filename : filenames)
	    if ((file_ = new File(filename)).exists() && (file_.isDirectory() == false))
	    {	have = true;
		break;
	    }
	try
	{   if (have == false)
	    {   if ((file_ = new File("/etc/skel/.config/wall-of-memories/plugins")).exists() && (file_.isDirectory() == false))
		    FileUtil.copy("/etc/skel/.config/wall-of-memories/plugins", HOME + "/.config/wall-of-memories/plugins");
		else if ((file_ = new File("/etc/skel/.wom.plugins")).exists() && (file_.isDirectory() == false))
		    FileUtil.copy("/etc/skel/.wom.plugins", HOME + "/.wom.plugins");
	}   }
	catch (final Throwable err)
	{   err.printStackTrace(System.err);
	}
	
	for (String filename : filenames)
	{   final File file = new File(filename = filename.replace("%1", "wall-of-memories").replace("%2", "wom"));
	    if (file.exists() && (file.isDirectory() == false))
	    {   pluginFile = filename;
		break;
	}   }
    }
    
    
    
    /**
     * Gets the count of plug-ins
     * 
     * @return  The count of plug-ins
     */
    public static int getPluginCount()
    {
	synchronized (PluginHandler.class)
	{   return pluginInstances.size();
	}
    }
    
    
    /**
     * Gets a plug-in by its index
     * 
     * @param   index  The index of the plug-in
     * @return         The plug-in
     */
    public static PluginV1 getPlugin(final int index)
    {
	synchronized (PluginHandler.class)
	{   return pluginInstances.get(index);
	}
    }
    
    
    /**
     * Gets whether a plug-in is activated
     *
     * @param   plugin  The index of the plug-in
     * @return          Whether the plug-in is activated
     */
    public static boolean isActive(final int plugin)
    {
	synchronized (PluginHandler.class)
	{   return activePlugins.contains(pluginInstances.get(plugin));
	}
    }
    
    
    /**
     * Sets whether a plug-in is activated
     *
     * @param  plugin  The index of the plug-in
     * @param  active  Whether the plug-in should be active
     */
    public static void setActive(final int plugin, final boolean active)
    {
	synchronized (PluginHandler.class)
	{   try
	    {   if (activePlugins.contains(pluginInstances.get(plugin)) ^ active)
		    if (active)  {  activePlugins.add   (pluginInstances.get(plugin));  pluginInstances.get(plugin).initialise();  }
		    else         {  activePlugins.remove(pluginInstances.get(plugin));  pluginInstances.get(plugin).terminate();   }
	    }
	    catch (final Throwable err)
	    {   System.err.println("Problem with plug-in " + (active ? "activation" : "deactivation") + ": " + err.toString());
	}   }
    }
    
    
    /**
     * Updates a plug-in, for this to work, the plug-in cannot be in used and the may but be no external references to it.
     * 
     * @param  plugin  The index of the plug-in
     */
    public static void updatePlugin(final int plugin)
    {
	synchronized (PluginHandler.class)
	{   try
	    {   {   final PluginV1 _plugin = pluginInstances.get(plugin);
		    if (activePlugins.contains(_plugin))
			return;
		}
		pluginInstances.set(plugin, null);
		System.gc();
		pluginInstances.set(plugin, getPluginInstance(pluginFiles.get(plugin)));
	    }
	    catch (final Throwable err)
	    {   System.err.println("Problem with plug-in updating: " + err.toString());
	}   }
    }
    
    
    /**
     * Gets the plug-in as an instance
     *
     * @param   name       The file name of the plug-in
     * @return             The plug-in as an instance
     * @throws  Exception  If the plug-in can't be loaded
     */
    private static PluginV1 getPluginInstance(final String path) throws Exception
    {
	final URL url = (new File(path)).toURI().toURL();
	
	URLClassLoader sysloader = (URLClassLoader)(ClassLoader.getSystemClassLoader());
	Class<URLClassLoader> sysclass = URLClassLoader.class;
	
	Method method = sysclass.getDeclaredMethod("addURL", URL.class);
	method.setAccessible(true);
	method.invoke(sysloader, url);
	
	String name = path.substring(path.lastIndexOf('/') + 1);
	name = name.substring(0, name.length() - 3) + PLUGIN_CLASS_NAME;
	synchronized (PluginHandler.class)
	{   try (URLClassLoader classLoader = new URLClassLoader(new URL[]{url}))
	    {
		@SuppressWarnings("unchecked")
		Class<PluginV1> klass = (Class<PluginV1>)(classLoader.loadClass(name));
		return klass.newInstance();
	}   }
    }
    
    
    /**
     * Stops all unlisted plugins, starts all newly listed plugins and restarts all updated plugins
     */
    public static void restartPlugins()
    {
	synchronized (PluginHandler.class)
	{   try
	    {
		final Vector<String> newFiles = new Vector<String>();
		final HashSet<String> gotHash = new HashSet<String>();
		
		for (final String line : FileUtil.readFile(pluginFile).replace('\f', '\n').replace('\t', ' ').split("\n"))
		{   if ((line.length() == 0) || line.replace(" ", "").startsWith("#") || line.replace(" ", "").startsWith(";"))
			continue;
		    if (pluginHash.contains(line) == false)
			newFiles.add(line);
		    gotHash.add(line);
		}
		
		String file;
		for (int i = 0, n = pluginFiles.size(); i < n; i++)
		    if (gotHash.contains(file = pluginFiles.get(i)) == false)
		    {
			if (activePlugins.contains(pluginInstances.get(i)))
			    setActive(i, false);
			pluginHash.remove(file);
			pluginMDates.remove(file);
			pluginFiles.remove(i);
			pluginInstances.remove(i);
			n--;
			i--;
		    }
		    else if (isActive(i))
		    {
			long newMDate = (new File(file)).lastModified();
			long oldMDate = pluginMDates.get(file).longValue();
			if (newMDate != oldMDate)
			{
			    pluginMDates.put(file, Long.valueOf(newMDate));
			    updatePlugin(i);
			}
		    }
		
		int i = pluginFiles.size();
		for (final String newFile : newFiles)
		{
		    pluginHash.add(newFile);
		    pluginFiles.add(newFile);
		    pluginMDates.put(newFile, Long.valueOf((new File(newFile)).lastModified()));
		    pluginInstances.add(getPluginInstance(newFile));
		    setActive(i, true);
		    i++;
		}
	    }
	    catch (final Throwable err)
	    {   System.err.println("Problem with initial plug-in activation: " + err.toString());
	}   }
    }
    
}

