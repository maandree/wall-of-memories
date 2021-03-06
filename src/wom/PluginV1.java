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

import javax.swing.ImageIcon;


/**
 * Plug-ins must implement this interface or a newer version
 * and have a public nullary (default) constructor.
 * Implemention class should be named {@code Plugin}.
 * 
 * @version  1.0
 * @since    {@link PluginHandler} version 1.0
 */
public interface PluginV1
{
    /**
     * Mini icon size: 8 × 8
     */
    public static final int MINI = 8;
    
    /**
     * Small icon size: 16 × 16
     */
    public static final int SMALL = 16;
    
    /**
     * Medium icon size: 24 × 24
     */
    public static final int MEDIUM = 24;
    
    /**
     * Large icon size: 32 × 32
     */
    public static final int LARGE = 32;
    
    /**
     * Huge icon size: 48 × 48
     */
    public static final int HUGE = 48;
    
    
    
    /**
     * Gets the plug-in's name
     * 
     * @return  The plug-in's name
     */
    public String getName();
    
    /**
     * Gets the plug-in's description
     * 
     * @return  The plug-in's description
     */
    public String getDescription();
    
    /**
     * Gets the version of the plug-in
     * 
     * @return  The version of the plug-in
     */
    public String getVersion();
    
    
    /**
     * Initialises the plug-in
     */
    public void initialise();
    
    /**
     * Terminates the plug-in
     */
    public void terminate();
    
    
    /**
     * Gets the plug-in's icon
     * 
     * @param   dimension  The width and height of the icon
     * @return             The plug-in's icon, {@code null} if vector image is missing
     */
    public ImageIcon getIcon(final int dimension);
    
    /**
     * Gets the plug-in's icon, should the biggest available raster image
     * 
     * @return  The plug-in's icon, {@code null} if raster image is missing
     */
    public ImageIcon getBiggestIcon();
    
    /**
     * Gets an array of all n:s such that a raster icon of size n × n exists,
     * if non-positive number is returned as an element, a vector icon designed for
     * ~n × ~n and larger exists.
     */
    public int[] getIconDimensions();
    
}

