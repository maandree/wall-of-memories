/**
 * libandree — Miscellaneous class library for Java
 * 
 * Copyright © 2007, 2012  Mattias Andrée (maandree@kth.se)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * This layout manager works a <code>BorderManager</code>, but can handle multiple components, not only 5
 *
 * @author  Mattias Andrée <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class DockLayout implements LayoutManager, Serializable
{
    /**
     * Desired by {@link java.io.Serializable}
     */
    private static final long serialVersionUID = 1L;
    
    
    
    //Has default constructor
    
    
    
    /**
     * Docks the component to the top
     */
    public static final String TOP = "TOP";
    
    /**
     * Docks the component to the bottom
     */
    public static final String BOTTOM = "BOTTOM";
    
    /**
     * Docks the component to the left
     */
    public static final String LEFT = "LEFT";
    
    /**
     * Docks the component to the right
     */
    public static final String RIGHT = "RIGHT";
    
    /**
     * Docks the component to the unused space
     */
    public static final String FILL = "FILL";
    
    /**
     * Does not dock the item
     */
    public static final String NONE = "NONE";
    
    
    
    /**
     * Added components
     */
    private final ArrayList<Component> components  = new ArrayList<Component>();

    /**
     * Added components' constraints
     */
    private final ArrayList<String> constraints = new ArrayList<String>();



    /**
     * Lays out the specified container
     *
     * @param  parent  The container to be laid out
     */
    @Override
    public void layoutContainer(Container parent)
    {
        Insets insets = parent.getInsets();
        int left   = insets.left;
        int top    = insets.top;
        int width  = parent.getWidth()  - (insets.left + insets.right);
        int height = parent.getHeight() - (insets.top  + insets.bottom);

        for (int i = 0 ; i < this.components.size(); i++)
        {
            Component c = this.components .get(i);
            String    s = this.constraints.get(i);
            if (c.isVisible() && !(s.equals(NONE)))
            {
                if (s.equals(TOP))
                {
                    c.setBounds(left, top, width, c.getPreferredSize().height);
                    height -= c.getPreferredSize().height;
                    top += c.getPreferredSize().height;
                }
                else if (s.equals(BOTTOM))
                {
                    c.setBounds(left, top + height - c.getPreferredSize().height, width, c.getPreferredSize().height);
                    height -= c.getPreferredSize().height;
                }
                else if (s.equals(LEFT))
                {
                    c.setBounds(left, top, c.getPreferredSize().width, height);
                    width -= c.getPreferredSize().width;
                    left += c.getPreferredSize().width;
                }
                else if (s.equals(RIGHT))
                {
                    c.setBounds(left + width - c.getPreferredSize().width, top, c.getPreferredSize().width, height);
                    width -= c.getPreferredSize().width;
                }
                else if (s.equals(FILL))
                {
                    c.setBounds(left, top, width, height);
                }
                else
                    assert false;
            }
        }

    }


    /**
     * If the layout manager uses a per-component string, adds the component <code>component</code> to the layout,
     * associating it with the string specified by <code>constraint</code>
     *
     * @param  constraint  The constraint
     * @param  component   The component
     */
    public void addLayoutComponent(String constraint, Component component)
    {
        this.components .add(component );
        this.constraints.add(constraint);
    }

    /**
     * Removes the specified component from the layout
     *
     * @param  component  The component to be removed
     */
    public void removeLayoutComponent(Component component)
    {
        int index = this.components.indexOf(component);
        this.components .remove(index);
        this.constraints.remove(index);
    }


    /**
     * Calculates the preferred size dimensions for the specified container, given the components it contains
     *
     * @param   parent  The container to be laid out
     * @return          The preferred size
     */
    public Dimension preferredLayoutSize(Container parent)
    {
    	try
    	{
    	    return parent.getPreferredSize();
    	}
    	catch (final StackOverflowError err)
    	{
    	    return new Dimension(1, 1);
    	}
    }

    /**
     * Calculates the minimum size dimensions for the specified container, given the components it contains
     *
     * @param   parent  The container to be laid out
     * @return          The minimum size
     */
    public Dimension minimumLayoutSize(Container parent)
    {
        return preferredLayoutSize(parent);
    }

}

