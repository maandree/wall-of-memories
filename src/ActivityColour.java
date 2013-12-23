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
    public static ActivityColour normal_hover = new ActivityColour(115);
    
    /**
     * Colours for activity boxes in selected state
     */
    public static ActivityColour selected = new ActivityColour(185);
    
    /**
     * Colours for activity boxes in selected state and hovered
     */
    public static ActivityColour selected_hover = new ActivityColour(155);
    
    
    
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
	double sat = 0.1 + 0.4 * x;
	return this.cache[descrete] = (new Colour(lum, sat, this.hue)).srgb;
    }
    
}

