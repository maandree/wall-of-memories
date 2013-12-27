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

import java.awt.*;
import java.util.*;


/**
 * Class for generating and caching a colour representing an activity intensity
 */
public class ActivityColour
{
    /**
     * Constructor
     */
    private ActivityColour(final double hue)
    {
	this.hue = hue;
    }
    
    
    
    /**
     * Colours for activity boxes in normal state
     */
    public static ActivityColour normal = new ActivityColour(85);
    
    /**
     * Colours for activity boxes in normal state but hovered
     */
    public static ActivityColour normal_hover = new ActivityColour(85 + 50);
    
    /**
     * Colours for activity boxes in selected state
     */
    public static ActivityColour selected = new ActivityColour(285);
    
    /**
     * Colours for activity boxes in selected state and hovered
     */
    public static ActivityColour selected_hover = new ActivityColour(285 - 50);
    
    
    
    /**
     * Cache for already calculated colours
     */
    private Color[] cache = new Color[1024];
    
    /**
     * The hue of the colours
     */
    private double hue;
    
    
    
    /**
     * Get the colour representing an activity intensity
     * 
     * @param   activity  Activity intensity [0, 1]
     * @return            The colour
     */
    public Color get(final double activity)
    {
	int descrete = (int)(activity * 1023);
	if (descrete < 0)
	    descrete = 0;
	if (descrete > 1023)
	    descrete = 1023;
	
	if (this.cache[descrete] != null)
	    return this.cache[descrete];
	
	double x = descrete / 1024.;
	double lum = 0.75 - 0.20 * x;
	double sat = 0.025 + 0.475 * x;
	return this.cache[descrete] = (new Colour(lum, sat, this.hue)).srgb;
    }
    
    
    /**
     * Get the colour representing an activity intensity
     * 
     * @param   selected  Whether the representation element is selected
     * @param   hovered   Whether the representation element is hovered
     * @param   activity  Activity intensity [0, 1]
     * @return            The colour
     */
    public static Color get(final boolean selected, final boolean hovered, final double activity)
    {
	if (hovered)
	    if (selected)  return ActivityColour.selected_hover.get(activity);
	    else           return ActivityColour.  normal_hover.get(activity);
	else
	    if (selected)  return ActivityColour.selected.get(activity);
	    else           return ActivityColour.  normal.get(activity);
    }
    
}

