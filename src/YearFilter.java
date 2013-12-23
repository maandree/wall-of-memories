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
import java.awt.*;


/**
 * Year filter component
 */
@SuppressWarnings("serial")
public class YearFilter extends JComponent
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
     * The big box gap
     */
    private static final int BIG_GAP = 16;
    
    /**
     * The margins
     */
    private static final int MARGIN = 8;
    
    
    
    /**
     * Constructor
     */
    public YearFilter()
    {
	this.setBackground(Color.WHITE);
	
	final int x = MARGIN * 2 + Math.max(this.years, 1) * (BOX_WIDTH + BOX_GAP) - BOX_GAP;
	final int y = MARGIN * 2 + BOX_HEIGHT + BIG_GAP + 12 * (BOX_HEIGHT + BOX_GAP) - BOX_GAP;
	this.setPreferredSize(new Dimension(x, y));
    }
    
    
    
    /**
     * The number of years
     */
    private int years = 50;
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(final Graphics g)
    {
	g.setColor(this.getBackground());
	g.fillRect(0, 0, this.getWidth(), this.getHeight());
	
	final Rectangle rect = g.getClipBounds();
	
	int x1 = (rect.x - MARGIN) / (BOX_WIDTH  + BOX_GAP);
	int y1 = (rect.y - MARGIN) / (BOX_HEIGHT + BOX_GAP);
	
	int x2 = rect.x + rect.width  - MARGIN - BOX_WIDTH;
	int y2 = rect.y + rect.height - MARGIN - BOX_HEIGHT;
	x2 = (x2 + BOX_WIDTH  + BOX_GAP - 1) / BOX_WIDTH  + BOX_GAP;
	y2 = (y2 + BOX_HEIGHT + BOX_GAP - 1) / BOX_HEIGHT + BOX_GAP;
	
	if (x1 < 0)           x1 = 0;
	if (y1 < 0)           y1 = 0;
	if (x2 > this.years)  x2 = this.years;
	if (y2 > 12)          y2 = 12;
	
	g.setColor(ActivityColour.normal.get(0.5));
	for (int x = x1; x < x2; x++)
	{
	    final int X = x * (BOX_WIDTH + BOX_GAP) + MARGIN;
	    g.fillRect(X, MARGIN, BOX_WIDTH, BOX_HEIGHT);
	    
	    for (int y = y1; y < y2; y++)
	    {
		final int Y = y * (BOX_HEIGHT + BOX_GAP) + BOX_HEIGHT + BIG_GAP + MARGIN;
		g.fillRect(X, Y, BOX_WIDTH, BOX_HEIGHT);
	    }
	}
	
	super.paint(g);
    }
    
    
}

