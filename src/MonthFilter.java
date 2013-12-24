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
 * Month filter component
 */
@SuppressWarnings("serial")
public class MonthFilter extends JComponent implements MouseInputListener
{
    /**
     * The number of days in each of the months
     */
    private final int[] DAYS = {31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
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
     * The gap between years
     */
    private static final int HUGE_GAP = 32;
    
    /**
     * The margins
     */
    private static final int MARGIN = 8;
    
    /**
     * The width of the component for one year
     */
    private static final int YEAR_WIDTH = BOX_WIDTH + BIG_GAP + 31 * (BOX_WIDTH + BOX_GAP) - BOX_GAP;
    
    /**
     * The height of the component for one year
     */
    private static final int YEAR_HEIGHT = BOX_HEIGHT + BIG_GAP + 12 * (BOX_HEIGHT + BOX_GAP) - BOX_GAP;
    
    
    
    /**
     * Constructor
     */
    public MonthFilter()
    {
	this.setBackground(Color.WHITE);
	
	this.addMouseListener(this);
	this.addMouseMotionListener(this);
	
	final int x = MARGIN * 2 + YEAR_WIDTH + 100/*dummy max. length of text */;
	final int y = MARGIN * 2 + this.m_selected.length * (YEAR_HEIGHT + HUGE_GAP) - HUGE_GAP;
	this.setPreferredSize(new Dimension(x, y));
    }
    
    
    
    /**
     * Mask for selected days, for each year
     */
    private short[][] selected = new short[11][31];
    
    /**
     * Mask for fully selected months, for each year
     */
    private short[] m_selected = new short[11];
    
    /**
     * The first year with activity
     */
    private int first_year = 1990;
    
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
	
	paintYear(g, "All years", MARGIN, 0);
	for (int i = 1; i < this.m_selected.length; i++)
	    paintYear(g, "Year " + (this.first_year + i - 1), MARGIN + i * (YEAR_HEIGHT + HUGE_GAP), i);
    }
    
    /**
     * Print a year of the component
     * 
     * @param  g         Drawing component from {@link #paint(Graphics)}
     * @param  title     The text to print on the left side of the component
     * @param  y_offset  The year components position on the vertical axi
     * @param  year      Zero for all years, otherwise {@code year_to_print - this.first_year + 1}
     */
    private void paintYear(final Graphics g, final String title, final int y_offset, final int year)
    {
	final Rectangle rect = g.getClipBounds();
	final int rect_y = rect.y - y_offset;
	
	int x1 = (rect.x - MARGIN - BOX_WIDTH  - BIG_GAP) / (BOX_WIDTH  + BOX_GAP);
	int y1 = (rect_y          - BOX_HEIGHT - BIG_GAP) / (BOX_HEIGHT + BOX_GAP);
	
	int x2 = rect.x + rect.width  - MARGIN - BOX_WIDTH;
	int y2 = rect_y + rect.height          - BOX_HEIGHT;
	x2 = (x2 + BOX_WIDTH  + BOX_GAP - 1) / BOX_WIDTH  + BOX_GAP;
	y2 = (y2 + BOX_HEIGHT + BOX_GAP - 1) / BOX_HEIGHT + BOX_GAP;
	
	if (x1 < 0)   x1 = 0;
	if (y1 < 0)   y1 = 0;
	if (x2 > 31)  x2 = 31;
	if (y2 > 12)  y2 = 12;
	
	if (y2 <= 0)
	    return;
	
	leapYearFix(year);
	
	boolean hovered = (this.hover == 0) || (this.hover == year << 9);
	boolean selected = (this.m_selected[year] & (1 << 12) - 1) == (1 << 12) - 1;
	
	g.setColor(ActivityColour.get(selected, hovered, 1.0));
	g.fillRect(MARGIN, y_offset, BOX_WIDTH, BOX_HEIGHT);
	
	for (int y = y1; y < y2; y++)
	{
	    final int Y = y * (BOX_HEIGHT + BOX_GAP) + BOX_HEIGHT + BIG_GAP;
	    
	    hovered  = this.hover ==  y + 1;
	    hovered |= this.hover == (y + 1 | year << 9);
	    hovered |= this.hover == 0;
	    hovered |= this.hover == year << 9;
	    selected = (this.m_selected[year] & (1 << y)) != 0;
	    
	    g.setColor(ActivityColour.get(selected, hovered, 1.0));
	    g.fillRect(MARGIN, Y + y_offset, BOX_WIDTH, BOX_HEIGHT);
	}
	
	for (int x = x1; x < x2; x++)
	{
	    final int X = x * (BOX_WIDTH + BOX_GAP) + BOX_WIDTH + BIG_GAP + MARGIN;
	    
	    hovered  = this.hover ==  (x + 1) * 13;
	    hovered |= this.hover == ((x + 1) * 13 | year << 9);
	    hovered |= this.hover == 0;
	    hovered |= this.hover == year << 9;
	    selected = (this.selected[year][x] & (1 << 12) - 1) == (1 << 12) - 1;
	    
	    g.setColor(ActivityColour.get(selected, hovered, 1.0));
	    g.fillRect(X, y_offset, BOX_WIDTH, BOX_HEIGHT);
	    
	    for (int y = y1; y < y2; y++)
	    {
		if (x >= DAYS[y])
		    continue;
		
		final int Y = y * (BOX_HEIGHT + BOX_GAP) + BOX_HEIGHT + BIG_GAP;
		
		hovered  = this.hover ==  (x + 1) * 13 + y + 1;
		hovered |= this.hover == ((x + 1) * 13 + y + 1 | year << 9);
		hovered |= this.hover ==  (x + 1) * 13;
		hovered |= this.hover == ((x + 1) * 13 | year << 9);
		hovered |= this.hover ==  y + 1;
		hovered |= this.hover == (y + 1 | year << 9);
		hovered |= this.hover == 0;
		hovered |= this.hover == year << 9;
		selected = (this.selected[year][x] & (1 << y)) != 0;
		
		g.setColor(ActivityColour.get(selected, hovered, 1.0));
		g.fillRect(X, Y + y_offset, BOX_WIDTH, BOX_HEIGHT);
	    }
	}
	
	g.setColor(Color.BLACK);
	g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	final int x = MARGIN + YEAR_WIDTH + BIG_GAP;
	final int y = Math.max(g.getFontMetrics().getHeight() - MARGIN, BOX_HEIGHT);
	g.drawString(title, x, y + y_offset);
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
	int hover = calculateHover(e.getPoint());
	if (hover >= 0)
	{
	    final int z = hover >> 9;
	    final int z1 = z != 0 ? z     : 0;
	    final int z2 = z != 0 ? z + 1 : this.m_selected.length;
	    hover &= 511;
	    final int x = (hover / 13) - 1;
	    final int y = (hover % 13) - 1;
	    if (x < 0)
	    {
		final boolean selected;
		if (y < 0)
		{
		    selected = (this.m_selected[z] & (1 << 12) - 1) == (1 << 12) - 1;
		    for (int i = z1; i < z2; i++)
			this.m_selected[i] = (short)(selected ? 0 : (1 << 12) - 1);
		}
		else
		{
		    selected = (this.m_selected[z] & 1 << y) != 0;
		    for (int i = z1; i < z2; i++)
			this.m_selected[i] ^= 1 << y;
		}
		for (int j = z1; j < z2; j++)
		    for (int i = 0, n = this.selected[j].length; i < n; i++)
			if (y < 0)
			    if (selected)
				this.selected[j][i] = 0;
			    else
				this.selected[j][i] |= (1 << 12) - 1;
			else
			    if (selected)
				this.selected[j][i] &= ~(1 << y);
			    else
				this.selected[j][i] |= 1 << y;
		this.repaint();
	    }
	    else if (x < this.selected[z].length)
	    {
		int m1 = -1, m2 = -2;
		if (y < 0)
		    if (this.selected[z][x] == (1 << 12) - 1)
			for (int i = z1; i < z2; i++)
			    this.selected[i][x] = this.m_selected[i] = 0;
		    else
		    {
			for (int i = z1; i < z2; i++)
			    this.selected[i][x] = (1 << 12) - 1;
			m1 = 0;
			m2 = 11;
		    }
		else if ((this.selected[z][x] & (1 << (m1 = m2 = y))) != 0)
		    for (int i = z1; i < z2; i++)
			this.selected[i][x] &= ~(1 << y);
		else
		    for (int i = z1; i < z2; i++)
			this.selected[i][x] |= 1 << y;
		if (m2 >= m1)
		{
		    for (int i = z1; i < z2; i++)
		    {
			leapYearFix(i);
			for (int m = 0; m < 12; m++)
			    for (int d = DAYS[m]; d < this.selected[i].length; d++)
				this.selected[i][d] |= 1 << m;
		    }
		    for (int i = z1; i < z2; i++)
			for (int m = 1 << m1; m <= 1 << m2; m <<= 1)
			{
			    int count = 0, n = this.selected[i].length;
			    for (int day = 0; day < n; day++)
				if ((this.selected[i][day] & m) == m)
				    count++;
			    this.m_selected[i] = (short)((this.m_selected[i] & ~m) | (count == n ? m : 0));
			}
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
    private int calculateHover(final Point point)
    {
	int hover = -1;
	
	int point_y = point.y - MARGIN;
	if (point_y < 0)
	    return hover;
	int z = point_y / (YEAR_HEIGHT + HUGE_GAP);
	point_y %= YEAR_HEIGHT + HUGE_GAP;
	
	if (z >= this.m_selected.length)
	    return hover;
	
	int x = point.x - MARGIN - BOX_WIDTH  - BIG_GAP;
	int y = point_y          - BOX_HEIGHT - BIG_GAP;
	
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
	    y = point_y;
	    if ((y < 0) || (y >= BOX_HEIGHT))
		y = -1;
	    else
		y = 0;
	}
	else if (y % (BOX_HEIGHT + BOX_GAP) >= BOX_WIDTH)
	    y = -1;
	else
	    y = y / (BOX_HEIGHT + BOX_GAP) + 1;
	
	if ((0 <= x) && (x <= 31) && (0 <= y) && (y <= 12))
	    hover = (z << 9) | (x * 13 + y);
	
	return hover;
    }
    
    
    /**
     * Modify {@link #DAYS} to accout for whether it is a leap year or not
     * 
     * @param  year  The index of {@link #selected} or {@link #m_selected} of interest
     */
    private void leapYearFix(final int year)
    {
	if (year == 0)
	{
	    DAYS[1] = 28;
	    for (int y_ = 1; y_ < this.m_selected.length; y_++)
	    {
		int y = this.first_year + y_ - 1;
		if (((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0))
		{
		    DAYS[1] = 29;
		    break;
		}
	    }
	    return;
	}
	
	int y = this.first_year + year - 1;
	if (((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0))
	    DAYS[1] = 29;
	else
	    DAYS[1] = 28;
    }
    
}

