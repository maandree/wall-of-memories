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
import java.io.*;
import java.util.*;


/**
 * Application settings
 */
public class Settings
{
    /**
     * Configuration table
     */
    public static final HashMap<String, String> configurations = new HashMap<String, String>();
    
    
    
    /**
     * Load all settings when first used
     */
    static
    {
	final String[] files = new String[6];
	final String $XDG_CONFIG_HOME = System.getenv("XDG_CONFIG_HOME");
	final String $HOME = System.getenv("HOME");
	final String HOME = System.getProperty("user.home");
	if (($XDG_CONFIG_HOME != null) && $XDG_CONFIG_HOME.length() > 0)
	    files[0] = $XDG_CONFIG_HOME + "/wall-of-memories/womrc";
	if (($HOME != null) && $HOME.length() > 0)
	    files[1] = $HOME + "/.config/wall-of-memories/womrc";
	if (($HOME != null) && $HOME.length() > 0)
	    files[2] = $HOME + "/.womrc";
	if ((HOME != null) && HOME.length() > 0)
	    files[3] = HOME + "/.config/wall-of-memories/womrc";
	if ((HOME != null) && HOME.length() > 0)
	    files[4] = HOME + "/.womrc";
	files[5] = "/etc/womrc";
	
	for (final String file : files)
	    if ((file != null) && (new File(file)).exists())
	    {
		InputStream is = null;
		try
		{
		    is = new FileInputStream(new File(file));
		    byte[] data = new byte[8192];
		    int ptr = 0;
		    for (;;)
		    {
			if (ptr == data.length)
			    System.arraycopy(data, 0, data = new byte[data.length << 1], 0, ptr);
			final int n = is.read(data, ptr, data.length - ptr);
			if (n <= 0)
			    break;
			ptr += n;
		    }
		    char[] chars = (new String(data, 0, ptr, "UTF-8") + "\n").toCharArray();
		    boolean comment = false;
		    boolean escape = false;
		    char quote = 0;
		    char[] buf = new char[ptr];
		    String key = null;
		    int ptr_ = ptr = 0;
		    for (final char c : chars)
		    {
			if (escape)
			{
			    buf[ptr_ = ptr++] = c;
			    escape = false;
			}
			else if (c == quote)
			    quote = 0;
			else if (quote == '\'')
			    if (c == '\'')
				buf[ptr_ = ptr++] = quote = 0;
			    else
				buf[ptr_ = ptr++] = c;
			else if (c == '\n')
			    if (key == null)
			    {
				configurations.put((new String(buf, 0, ptr_)).replace("\0", ""), "yes");
				ptr_ = ptr = 0;
			    }
			    else
			    {
				configurations.put(key, (new String(buf, 0, ptr_)).replace("\0", ""));
				ptr_ = ptr = 0;
				key = null;
			    }
			else if (comment == false)
			    if (c == '\\')
				escape = true;
			    else if (quote == '\"')
				buf[ptr_ = ptr++] = c;
			    else if ((c == '\'') || (c == '\"'))
				quote = c;
			    else if ((c == '#') || (c == ';'))
				comment = true;
			    else if (c == ' ')
			    {
				if ((ptr != 0) && (buf[ptr - 1] != ' '))
				{
				    ptr_ = ptr;
				    buf[ptr++] = ' ';
				}
			    }
			    else if ((key == null) && (c == '='))
			    {
				key = (new String(buf, 0, ptr_)).replace("\0", "");
				ptr_ = ptr = 0;
			    }
			    else
				buf[ptr_ = ptr++] = c;
		    }
		}
		catch (final Throwable err)
		{
		    err.printStackTrace(System.err);
		}
		finally
		{
		    if (is != null)
			try
			{
			    is.close();
			}
			catch (final Throwable ignore)
			{
			    /* ignore */
			}
		}
		break;
	    }
    }
    
    
}

