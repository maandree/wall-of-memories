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


/**
 * Class for generating and caching a colour representing an activity intensity
 */
public class ActivityColour
{
    /**
     * Get the colour representing an activity intensity
     * 
     * @param   activity  Activity intensity [0, 1]
     * @return            The colour
     */
    public static Color get(final double activity)
    {
	int descrete = (int)(activity * 1023);
	if (descrete < 0)
	    descrete = 0;
	if (descrete > 1023)
	    descrete = 1023;
	
	if (cache[descrete] != null)
	    return cache[descrete];
	
	double x = descrete / 1024.;
	double lum = 0.75 - 0.20 * x;
	double sat = 0.5 * x;
	return cache[descrete] = (new Colour(lum, sat, 85)).srgb;
    }
    
    
    
    /**
     * Cache for already calculated colours
     */
    private static Color[] cache = new Color[1024];
    
}

