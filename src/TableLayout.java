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
 * <p>This layout manager lays out components with an absolute or precental size</p>
 * <p>
 *   The constraint: <br/>
 *       <code>x</code> gives the absolute size x<br/>
 *       <code>x%</code> gives <code>x</code> % of the containers size<br/>
 *       <code>x%+y</code> gives <code>x</code> % of the containers size + <code>y</code><br/>
 *       <code>x%-y</code> gives <code>x</code> % of the containers size - <code>y</code><br/>
 *       <code>?</code> gives the default size<br/>
 *       all other input will be handled as wildcards.
 *       The input segments “*,” and “,*” will be ignored. Empty input and "*,*" is handled as wildcard.
 * </p>
 *
 * @author  Mattias Andrée <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class TableLayout implements LayoutManager, Serializable
{
    /**
     * Desired by {@link java.io.Serializable}
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * Horizontal layout orientation
     */
    public static final boolean HORIZONTAL = false;
    
    /**
     * Vertical layout orientation
     */
    public static final boolean VERTICAL = true;
    
    
    
    /**
     * Constructor
     *
     * @param  orientation  Layout orientation
     */
    public TableLayout(final boolean orientation)
    {
        this.orientation = orientation;
    }
    
    
    
    /**
     * Layout orientation
     */
    private final boolean orientation;

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
    public void layoutContainer(final Container parent)
    {
        Insets insets = parent.getInsets();
        final double left   = insets.left;
        final double top    = insets.top;
        final double width  = parent.getWidth()  - (insets.left + insets.right );
        final double height = parent.getHeight() - (insets.top  + insets.bottom);

        double x  = this.orientation == HORIZONTAL ? left : top;
        double y  = this.orientation == VERTICAL   ? left : top;
        final double hw = this.orientation == VERTICAL ? width : height;
        
        
        final Double[] sizes = getSizes(parent, 0);
        final Double[] positions = getSizes(parent, 1);
        double wildcards = 0.;
        double summa = 0;
        for (int i = 0, n = this.components.size(); i < n; i++)
            if (this.components.get(i).isVisible())
            {
                Double d = sizes[i];
                if (d == null)
                    wildcards++;
                else
                    summa += d.doubleValue();
            }
        
        
        final double overflow = (!this.orientation ? width : height) - summa;
        final double wild = overflow / wildcards;

        for (int i = 0, n = this.components.size(); i < n; i++)
        {
            Component c = this.components.get(i);
            if (c.isVisible())
            {
                double s = (sizes[i] == null) ? wild : sizes[i].doubleValue();
                
                double _x = this.orientation == HORIZONTAL ? x : y;
                double _y = this.orientation == VERTICAL   ? x : y;
                double _w = this.orientation == HORIZONTAL ? s : hw;
                double _h = this.orientation == VERTICAL   ? s : hw;
                
                if (this.orientation == VERTICAL)
                {
                    _x += positions[i].doubleValue();
                    _w -= positions[i].doubleValue();
                }
                else
                {
                    _y += positions[i].doubleValue();
                    _h -= positions[i].doubleValue();
                }
                
                c.setBounds((int)_x, (int)_y, (int)_w, (int)_h);
                
                x += s;
            }
        }

    }
    
    /**
     * Gets the components' sizes
     *
     * @param   parent  The container to be laid out
     * @param   index   Value index
     * @return          Gets the components sizes, <code>null</code> represents wildcard
     */
    private Double[] getSizes(final Container parent, final int index)
    {
        Insets insets = parent.getInsets();
        final double width  = parent.getWidth()  - (insets.left + insets.right );
        final double height = parent.getHeight() - (insets.top  + insets.bottom);

        double w = !this.orientation ? width : height;
        
        final Double[] sizes = new Double[this.components.size()];
        outer:
            for (int i = 0, n = this.components.size(); i < n; i++)
            {
                String s = this.constraints.get(i) + ",0";
                s = s.split(",")[index];
                if (s.equals("?"))
                    if (this.orientation == HORIZONTAL)
                        s = String.valueOf((int)(this.components.get(i).getPreferredSize().getWidth()));
                    else
                        s = String.valueOf((int)(this.components.get(i).getPreferredSize().getHeight()));

                double di = 0, dp = 0, dr = 0;
                double         pd = 1, rd = 1;
                int state = 0;
                double p = 0;
                if (s.length() > 0)
                {
                    for (int j = 0, m = s.length(); j < m; j++)
                    {
                        char c = s.charAt(j);
                        if (('0' <= c) && (c <= '9'))
                            switch (state)
                            {
                                case 4:
                                case 0:
				    di = (di * 10) + (c & 15);
                                    break;
                                case 1:
				    dp = (di * 10) + (c & 15);
                                    pd *= 10;
                                    break;
                                case 2:
				    dr = (di * 10) + (c & 15);
                                    rd *= 10;
                                    break;
                            }
                        else if ((c == '.') && (state < 2))
                            state++;
                        else if ((c == '%') && (state < 3))
                        {
                            p = di + dp / pd + ((dr > 0.) ? (dr / (pd * (rd - 1))) : 0.);
                            di = dp = dr = 0;
                            if (j + 1 < m)
                            {
                                j++;
                                c = s.charAt(j);
                                if      (c == '+')  state = 3;
                                else if (c == '-')  state = 4;
                                else
                                    continue outer;
                            }
                            else
                            {
                                sizes[i] = new Double(p * w / 100.);
                                continue outer;
                            }
                        }
                        else
                            continue outer;
                    }
                    if (!((pd > 1) || (rd > 1)))
                    {
                        if      (state == 3)  sizes[i] = new Double(p * w / 100. + di);
                        else if (state == 4)  sizes[i] = new Double(p * w / 100. - di);
                        else                  sizes[i] = new Double(di);
                    }
                }
            }
        
        return sizes;
    }


    /**
     * If the layout manager uses a per-component string, adds the component <code>component</code> to the layout,
     * associating it with the string specified by <code>constraint</code>
     *
     * @param  constraint  The constraint: <br/>
     *                          <code>x</code> gives the absolute size x<br/>
     *                          <code>x%</code> gives <code>x</code> % of the containers size<br/>
     *                          <code>x%+y</code> gives <code>x</code> % of the containers size + <code>y</code><br/>
     *                          <code>x%-y</code> gives <code>x</code> % of the containers size - <code>y</code><br/>
     *                          <code>?</code> gives the default size<br/>
     *                          all other input will be handled as wildcards.
     *                          The input segments “*,” and “,*” will be ignored. Empty input and "*,*" is handled as wildcard.
     * @param  component   The component
     */
    public void addLayoutComponent(final String constraint, final Component component)
    {
        this.components.add(component);
        this.constraints.add(constraint.replace(" ", ""));
    }

    /**
     * Removes the specified component from the layout
     *
     * @param  component  The component to be removed
     */
    public void removeLayoutComponent(final Component component)
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
    public Dimension preferredLayoutSize(final Container parent)
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
    public Dimension minimumLayoutSize(final Container parent)
    {
        return preferredLayoutSize(parent);
    }

}
