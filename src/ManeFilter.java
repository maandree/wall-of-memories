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
 * Mane filter component
 */
@SuppressWarnings("serial")
public class ManeFilter extends JPanel
{
    /**
     * The size of the margin
     */
    private final int MARGIN = 8;
    
    /**
     * The gap between rows
     */
    private final int GAP = 8;
    
    
    
    /**
     * Constructor
     */
    public ManeFilter()
    {
	this.setBackground(Color.WHITE);
	this.setLayout(new DockLayout());
	
	this.add(new PadPane(MARGIN, MARGIN), DockLayout.TOP);
	this.add(new PadPane(MARGIN, MARGIN), DockLayout.RIGHT);
	this.add(new PadPane(MARGIN, MARGIN), DockLayout.BOTTOM);
	this.add(new PadPane(MARGIN, MARGIN), DockLayout.LEFT);
	
	final JComponent[][] comps =
	        {
		    { new JLabel("Clear filters: "),
		      new JButton("Clear"),
		    },
		    { new PadPane(GAP, GAP),
		    },
		    { new JLabel("Active filters: "),
		      new JTextField("years and months and days and hours"),
		      new JButton("Apply"),
		    },
		};
	
	comps[2][1].setMinimumSize(comps[2][1].getPreferredSize());
	comps[2][1].setPreferredSize(new Dimension(0, comps[2][1].getPreferredSize().height));
	
	int total_width = 0;
	int total_height = 2 * MARGIN;
	
	int width = 1;
	for (final JComponent[] layer : comps)
	    width = Math.max(width, layer[0].getPreferredSize().width);
	
	for (final JComponent[] layer : comps)
	{
	    final JPanel panel = new JPanel(new DockLayout());
	    int total_width_ = 0;
	    Object side = DockLayout.LEFT;
	    JComponent fill = null;
	    
	    layer[0].setPreferredSize(new Dimension(width, layer[0].getPreferredSize().height));
	    for (int i = 0, n = layer.length - 1; i <= n; i++)
	    {
		layer[i].setOpaque(false);
		if (layer[i].getPreferredSize().width > 0)
		    panel.add(layer[i], side);
		else
		{
		    fill = layer[i];
		    side = DockLayout.RIGHT;
		}
		total_width_ += Math.max(layer[i].getMinimumSize().width, layer[i].getPreferredSize().width);
	    }
	    if (fill != null)
		panel.add(fill, DockLayout.FILL);
	    
	    int height = 1;
	    for (final JComponent component : layer)
		height = Math.max(height, component.getPreferredSize().height);
	    
	    panel.setOpaque(false);
	    panel.setPreferredSize(new Dimension(0, height));
	    this.add(panel, DockLayout.TOP);
	    
	    total_height += height;
	    total_width = Math.max(total_width, total_width_);
	}
	
	this.setPreferredSize(new Dimension(total_width + 2 * MARGIN, total_height));
    }
    
}

