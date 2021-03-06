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


/**
 * Mane class of the program
 */
public class Program
{
    /**
     * Mane function of the program
     * 
     * @param  args  Command line, excluding the zeroth argument
     */
    public static void main(final String... args)
    {
	PluginHandler.restartPlugins();
	
	final ManeFrame frame = new ManeFrame();
	frame.setSize(new Dimension(1733, 1300));
	frame.setVisible(true);
    }
    
}

