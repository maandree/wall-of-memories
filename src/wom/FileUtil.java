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
import java.util.*;


/**
 * File utilities
 */
public class FileUtil
{
    /**
     * Copy a file
     * 
     * @param  source       The file to copy
     * @param  destination  The output file
     * 
     * @throws  IOException  On file I/O error
     */
    public static void copy(final String source, final String destination) throws IOException
    {
	InputStream is = null;
	OutputStream os = null;
	try
	{
	    is = new FileInputStream (new File(source));
	    os = new FileOutputStream(new File(destination));
	    byte[] data = new byte[8192];
	    
	    for (;;)
	    {
		final int n = is.read(data, 0, data.length);
		if (n <= 0)
		    break;
		os.write(data, 0, n);
	    }
	    
	    os.flush();
	}
	finally
	{   if (is != null)
		try
		{   is.close();
		}
		catch (final Throwable ignore)
		{   /* ignore */
		}
	    if (os != null)
		try
		{   os.close();
		}
		catch (final Throwable ignore)
		{   /* ignore */
	}	}
    }
    
    
    /**
     * Read a file as text
     * 
     * @param   file  The file
     * @return        The text in the file
     * 
     * @throws  IOException  On file reading error
     */
    public static String readFile(final String file) throws IOException
    {
	final String text;
	InputStream is = null;
	try
	{
	    is = new BufferedInputStream(new FileInputStream(new File(file)));
	    final Vector<byte[]> bufs = new Vector<byte[]>();
	    int size = 0;
	    
	    for (int av; (av = is.available()) > 0;)
	    {
		byte[] buf = new byte[av];
		av = is.read(buf, 0, av);
		if (av < buf.length)
		{
		    final byte[] nbuf = new byte[av];
		    System.arraycopy(buf, 0, nbuf, 0, av);
		    buf = nbuf;
		}
		size += av;
		bufs.add(buf);
	    }
	    
	    final byte[] full = new byte[size];
	    int ptr = 0;
	    for (final byte[] buf : bufs)
	    {
		System.arraycopy(buf, 0, full, ptr, buf.length);
		ptr += buf.length;
	    }
	    
	    text = new String(full, "UTF-8");
	}
	finally
	{   if (is != null)
		try
		{   is.close();
		}
		catch (final Throwable ignore)
		{   /* ignore */
	}       }
	return text;
    }
    
}

