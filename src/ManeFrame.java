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
	
	final JScrollPane years_scroll  = new JScrollPane(new YearFilter(),
							  JScrollPane.  VERTICAL_SCROLLBAR_AS_NEEDED,
							  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	final JScrollPane months_scroll = new JScrollPane(new MonthFilter(),
							  JScrollPane.  VERTICAL_SCROLLBAR_AS_NEEDED,
							  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	final JScrollPane days_scroll   = new JScrollPane(new DayFilter(),
							  JScrollPane.  VERTICAL_SCROLLBAR_AS_NEEDED,
							  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	final JScrollPane hours_scroll  = new JScrollPane(new HourFilter(),
							  JScrollPane.  VERTICAL_SCROLLBAR_AS_NEEDED,
							  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	final JScrollPane filter_scroll = new JScrollPane(new ManeFilter(),
							  JScrollPane.  VERTICAL_SCROLLBAR_AS_NEEDED,
							  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	final JTabbedPane bottom_tabs = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
	bottom_tabs.add("Years",   years_scroll);
	bottom_tabs.add("Months", months_scroll);
	bottom_tabs.add("Days",     days_scroll);
	bottom_tabs.add("Hours",   hours_scroll);
	bottom_tabs.add("Filter", filter_scroll);
	
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

