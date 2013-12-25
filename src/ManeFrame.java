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
 * Mane frame of the program
 */
@SuppressWarnings("serial")
public class ManeFrame extends JFrame
{
    /**
     * Constructor
     */
    public ManeFrame()
    {
	super("Wall of Memories");
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	this.pack();
	this.setLayout(new BorderLayout());
	
	final JScrollPane left_scroll = new JScrollPane(new JPanel(),
							JScrollPane.  VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	final JScrollPane top_scroll  = new JScrollPane(new JPanel(),
							JScrollPane.  VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	final Object[][] bottom_tab_contents =
	        {
		    {"Years",      new YearFilter()},
		    {"Months",     new MonthFilter()},
		    {"Days",       new DayFilter()},
		    {"Hours",      new HourFilter()},
		    {"Categories", new CategoryFilter()},
		    {"People",     new PersonFilter()},
		    {"Filter",     new ManeFilter()},
		};
	
	final JTabbedPane bottom_tabs = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
	for (final Object[] bottom_tab_content : bottom_tab_contents)
	    bottom_tabs.add((String)(bottom_tab_content[0]),
			    new JScrollPane((JComponent)(bottom_tab_content[1]),
					    JScrollPane.  VERTICAL_SCROLLBAR_AS_NEEDED,
					    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	
	left_scroll.setBorder(null);
	top_scroll.setBorder(null);
	
	final JSplitPane vsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	vsplit.setTopComponent(top_scroll);
	vsplit.setBottomComponent(bottom_tabs);
	vsplit.setBorder(null);
	vsplit.setDividerLocation(0.8);
	vsplit.setResizeWeight(0.8);
	
	final JSplitPane hsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	hsplit.setLeftComponent(left_scroll);
	hsplit.setRightComponent(vsplit);
	hsplit.setBorder(null);
	hsplit.setDividerLocation(0.2);
	hsplit.setResizeWeight(0.2);
	this.add(hsplit);
    }
    
}

