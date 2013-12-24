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
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Hour filter component
 */
@SuppressWarnings("serial")
public class HourFilter extends JComponent implements MouseInputListener
{
    /**
     * The width of a box
     */
    private static final int BOX_WIDTH = 12;
    
    /**
     * The height of a box
     */
    private static final int BOX_HEIGHT = 12;
    
    /**
     * The gap between boxes
     */
    private static final int BOX_GAP = 2;
    
    /**
     * The big box gaps
     */
    private static final int BIG_GAP = 14;
    
    /**
     * The margins
     */
    private static final int MARGIN = 8;
    
    
    
    /**
     * Constructor
     */
    public HourFilter()
    {
	this.setBackground(Color.WHITE);
	
	this.addMouseListener(this);
	this.addMouseMotionListener(this);
	
	final int x_ = Math.max(this.selected.length, 1);
	final int x = MARGIN * 2 + BOX_WIDTH  + BIG_GAP + x_ * (BOX_WIDTH  + BOX_GAP) - BOX_GAP;
	final int y = MARGIN * 2 + BOX_HEIGHT + BIG_GAP + 7  * (BOX_HEIGHT + BOX_GAP) - BOX_GAP;
	this.setPreferredSize(new Dimension(x, y));
    }
    
    
    
    /**
     * Mask for selected elements
     */
    private byte[] selected = new byte[24];
    
    /**
     * Mask for fully selected weekdays
     */
    private byte m_selected = 0;
    
    /**
     * The index of the hovered element, -1 if none
     */
    private int hover = -1;
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(final Graphics g)
    {
	g.setColor(this.getBackground());
	g.fillRect(0, 0, this.getWidth(), this.getHeight());
	
	final Rectangle rect = g.getClipBounds();
	
	int x1 = (rect.x - MARGIN - BOX_WIDTH  - BIG_GAP) / (BOX_WIDTH  + BOX_GAP);
	int y1 = (rect.y - MARGIN - BOX_HEIGHT - BIG_GAP) / (BOX_HEIGHT + BOX_GAP);
	
	int x2 = rect.x + rect.width  - MARGIN - BOX_WIDTH;
	int y2 = rect.y + rect.height - MARGIN - BOX_HEIGHT;
	x2 = (x2 + BOX_WIDTH  + BOX_GAP - 1) / BOX_WIDTH  + BOX_GAP;
	y2 = (y2 + BOX_HEIGHT + BOX_GAP - 1) / BOX_HEIGHT + BOX_GAP;
	
	final int weeks = this.selected.length;
	if (x1 < 0)      x1 = 0;
	if (y1 < 0)      y1 = 0;
	if (x2 > weeks)  x2 = weeks;
	if (y2 > 7)      y2 = 7;
	
	boolean hovered = this.hover == 0;
	boolean selected = (this.m_selected & (1 << 7) - 1) == (1 << 7) - 1;
	
	g.setColor(ActivityColour.get(selected, hovered, 0.0));
	g.fillRect(MARGIN, MARGIN, BOX_WIDTH, BOX_HEIGHT);
	
	for (int y = y1; y < y2; y++)
	{
	    final int Y = y * (BOX_HEIGHT + BOX_GAP) + BOX_HEIGHT + BIG_GAP + MARGIN;
	    
	    hovered  = this.hover == y + 1;
	    hovered |= this.hover == 0;
	    selected = (this.m_selected & (1 << y)) != 0;
	    
	    g.setColor(ActivityColour.get(selected, hovered, 0.0));
	    g.fillRect(MARGIN, Y, BOX_WIDTH, BOX_HEIGHT);
	}
	
	for (int x = x1; x < x2; x++)
	{
	    final int X = x * (BOX_WIDTH + BOX_GAP) + BOX_WIDTH + BIG_GAP + MARGIN;
	    
	    hovered  = this.hover == (x + 1) * 8;
	    hovered |= this.hover == 0;
	    selected = (this.selected[x] & (1 << 7) - 1) == (1 << 7) - 1;
	    
	    g.setColor(ActivityColour.get(selected, hovered, 0.0));
	    g.fillRect(X, MARGIN, BOX_WIDTH, BOX_HEIGHT);
	    
	    for (int y = y1; y < y2; y++)
	    {
		final int Y = y * (BOX_HEIGHT + BOX_GAP) + BOX_HEIGHT + BIG_GAP + MARGIN;
		
		hovered  = this.hover == (x + 1) * 8 + y + 1;
		hovered |= this.hover == (x + 1) * 8;
		hovered |= this.hover == y + 1;
		hovered |= this.hover == 0;
		selected = (this.selected[x] & 1 << y) == 1 << y;
		
		g.setColor(ActivityColour.get(selected, hovered, 0.0));
		g.fillRect(X, Y, BOX_WIDTH, BOX_HEIGHT);
	    }
	}
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void mouseClicked(final MouseEvent e)
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseEntered(final MouseEvent e)
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseExited(final MouseEvent e)
    {
	if (this.hover >= 0)
	{
	    this.hover = -1;
	    this.repaint();
	}
    }
    
    /**
     * {@inheritDoc}
     */
    public void mousePressed(final MouseEvent e)
    {
	final int hover = calculateHover(e.getPoint());
	if (hover >= 0)
	{
	    final int x = (hover / 8) - 1;
	    final int y = (hover % 8) - 1;
	    if (x < 0)
	    {
		final boolean selected;
		if (y < 0)
		{
		    selected = (this.m_selected & (1 << 7) - 1) == (1 << 7) - 1;
		    this.m_selected = (byte)(selected ? 0 : (1 << 7) - 1);
		}
		else
		{
		    selected = (this.m_selected & 1 << y) != 0;
		    this.m_selected ^= 1 << y;
		}
		for (int i = 0, n = this.selected.length; i < n; i++)
		    if (y < 0)
			if (selected)
			    this.selected[i] = 0;
			else
			    this.selected[i] |= (1 << 7) - 1;
		    else
			if (selected)
			    this.selected[i] &= ~(1 << y);
			else
			    this.selected[i] |= 1 << y;
		this.repaint();
	    }
	    else if (x < this.selected.length)
	    {
		int m1 = -1, m2 = -2;
		if (y < 0)
		    if (this.selected[x] == (1 << 7) - 1)
			this.selected[x] = this.m_selected = 0;
		    else
		    {
			this.selected[x] = (1 << 7) - 1;
			m1 = 0;
			m2 = 6;
		    }
		else
		    this.selected[x] ^= 1 << (m1 = m2 = y);
		if (m2 >= m1)
		    for (int m = 1 << m1; m <= 1 << m2; m <<= 1)
		    {
			int count = 0, n = this.selected.length;
			for (int hour = 0; hour < n; hour++)
			    if ((this.selected[hour] & m) == m)
				count++;
			this.m_selected = (byte)((this.m_selected & ~m) | (count == n ? m : 0));
		    }
		this.repaint();
	    }
	}
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseReleased(final MouseEvent e)
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseDragged(final MouseEvent e)
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseMoved(final MouseEvent e)
    {
	final int hover = calculateHover(e.getPoint());
	if (hover != this.hover)
	{
	    this.hover = hover;
	    this.repaint();
	}
    }
    
    /**
     * Get the index of the hovered element
     * 
     * @param   point  The position of the rat
     * @return         The index of the hovered element, -1 if none
     */
    private static int calculateHover(final Point point)
    {
	int hover = -1;
	
	int x = point.x - MARGIN - BOX_WIDTH  - BIG_GAP;
	int y = point.y - MARGIN - BOX_HEIGHT - BIG_GAP;
	
	if (x < 0)
	{
	    x = point.x - MARGIN;
	    if ((x < 0) || (x >= BOX_WIDTH))
		x = -1;
	    else
		x = 0;
	}
	else if (x % (BOX_WIDTH + BOX_GAP) >= BOX_WIDTH)
	    x = -1;
	else
	    x = x / (BOX_WIDTH + BOX_GAP) + 1;
	
	if (y < 0)
	{
	    y = point.y - MARGIN;
	    if ((y < 0) || (y >= BOX_HEIGHT))
		y = -1;
	    else
		y = 0;
	}
	else if (y % (BOX_HEIGHT + BOX_GAP) >= BOX_WIDTH)
	    y = -1;
	else
	    y = y / (BOX_HEIGHT + BOX_GAP) + 1;
	
	if ((x >= 0) && (y >= 0) && (y < 8))
	    hover = x * 8 + y;
	
	return hover;
    }
    
}

