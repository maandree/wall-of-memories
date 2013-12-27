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

import java.awt.*;


/**
 * Perceptual colour model
 */
public class Colour
{
    /**
     * Polynomial interpolation degree for hues
     */
    private static final int DEGREE = 4;
    
    /**
     * Empirical elementary colour luminosity
     */
    private static final double LUM = 0.55;
    
    /**
     * Empirical elementary colour saturation
     */
    private static final double SAT = 0.50;
    
    /**
     * Empirical elementary colours: (red, red–blue, blue, blue–green, green, green–yellow, yellow, yellow–red)
     * at {@link #LUM} luminosity and {@link #SAT} % saturation.
     */
    private static final int[][] elementary = {{205, 101, 108}, {164, 110, 176}, { 36, 149, 190}, {  0, 169, 159},
                                               { 50, 166, 121}, {156, 173,  81}, {204, 173,  71}, {218, 128,  77}};
    
    
    
    /**
     * Constructor
     * 
     * @param  lum  The luminosity of the colour [0, 1]
     * @param  sat  The saturation of the colour [0, 1]
     * @param  hue  The hue of the colour to interpolate: [0, 400[ gon
     */
    public Colour(final double lum, final double sat, final double hue)
    {
	this(lum, sat, hue, 255);
    }
    
    /**
     * Constructor
     * 
     * @param  lum    The luminosity of the colour [0, 1]
     * @param  sat    The saturation of the colour [0, 1]
     * @param  hue    The hue of the colour to interpolate: [0, 400[ gon
     * @param  alpha  Opacity [0, 255]
     */
    public Colour(final double lum, final double sat, final double hue, final int alpha)
    {
	double h = hue;
	while (h >= 400.)  h -= 400.;
	while (h < 0.)     h += 400.;
	
	final int[][] fixed = new int[8][3];
	for (int i = 0; i < 8; i++)
	{
	    final double[] e = toLinear(elementary[i][0], elementary[i][1], elementary[i][2]);
	    for (int j = 0; j < 3; j++)
	    {
		double x = calculatePerception(e[j]);
		
		x = x / LUM * 0.5;
		x = (x - 0.5 * (1. - SAT)) / SAT;
		x = x * sat + 0.5 * (1. - sat);
		x = x / 0.5 * lum;
		
		e[j] = calculateIntensity(x);
	    }
	    
	    fixed[i] = toStandard(e[0], e[1], e[2]);
	}
	
	final int[] srgb = hueToColour(hue, fixed);
	for (int i = 0; i < 3; i++)
	    if (srgb[i] < 0)
		srgb[i] = 0;
	    else if (srgb[i] > 255)
		srgb[i] = 255;
	this.srgb = new Color(srgb[0], srgb[1], srgb[2], alpha);
    }
    
    
    
    /**
     * Standard RGB representation
     */
    public final Color srgb;
    
    
    
    /**
     * Converts sRGB [0, 255] to linear RGB [0, 1]
     * 
     * @param   r  The red   intensity
     * @param   g  The green intensity
     * @param   b  The blue  intensity
     * @return     Linear RGB colours components
     */
    private static double[] toLinear(final int r, final int b, final int g)
    {
        return new double[] {
	            r == 0. ? 0. : Math.pow(r / 255., 2.273943909),
		    g == 0. ? 0. : Math.pow(g / 255., 2.273943909),
		    b == 0. ? 0. : Math.pow(b / 255., 2.273943909)
	        };
    }
    
    /**
     * Converts linear RGB [0, 1] to sRGB [0, 255]
     * 
     * @param   r  The red   intensity
     * @param   g  The green intensity
     * @param   b  The blue  intensity
     * @return     sRGB colours components
     */
    private static int[] toStandard(final double r, final double b, final double g)
    {
	return new int[] {
	            (int)(0.5 + 255. * Math.pow(r, 0.439764585)),
		    (int)(0.5 + 255. * Math.pow(g, 0.439764585)),
		    (int)(0.5 + 255. * Math.pow(b, 0.439764585))
		};
    }
    
    /**
     * Calculates the linear intensity from the linear perception
     * 
     * @param   perception  The linear perception
     * @return              The linear intensity
     */
    private static double calculateIntensity(final double perception)
    {
	return Math.pow(perception,  2.);
    }
    
    /**
     * Calculates the linear perception from the linear intensity
     * 
     * @param   intensity  The linear intensity
     * @return             The linear perception
     */
    private static double calculatePerception(final double intensity)
    {
	return Math.pow(intensity, 1. / 2.);
    }
    
    /**
     * Interpolates colour with a hue, using fixed colours evenly distributed in hue starting at 0
     * 
     * @param   hue    The hue of the colour to interpolate: [0, 400[ gon
     * @param   fixed  Fixed colours: {0 gon, 50 gon, 100 gon, ... 350 gon} × {red, green, blue}
     * @return         sRGB colour components: {red, green, blue}
     */
    private static int[] hueToColour(final double hue, final int[][] fixed)
    {
        double[][] f = new double[8][];
        double[] frk = new double[8];
        double[] fgk = new double[8];
	double[] fbk = new double[8];
	double[] fi;
	int[] fxi;
        for (int i = 0; i < 8; i++)
	{
	    frk[i] = (fi = f[i] = toLinear((fxi = fixed[i])[0], fxi[1], fxi[2]))[0];
	    fgk[i] = fi[1];
	    fbk[i] = fi[2];
	}
	
        int $hue = (int)(hue);
	$hue = ($hue % 400) + 400;
        if ($hue < 450)
            $hue += 400;
	int midH = ($hue / 50) & 7;
        int lowH = (midH - 1) & 7;
	
        int[] hs = new int[2];
        hs[0] = lowH & 7;
	hs[1] = (lowH + 1) & 7;
	
        double[][] xs = new double[DEGREE][DEGREE];
        double[] rk = new double[DEGREE];
	double[] gk = new double[DEGREE];
        double[] bk = new double[DEGREE];
        for (int y = 0; y < DEGREE; y++)
        {
	    int ym = (lowH + y) & 7;
	    rk[y] = frk[ym];
	    gk[y] = fgk[ym];
	    bk[y] = fbk[ym];
	    double c = 1, m = 400 + (lowH + y) * 50;
	    for (int x = 0; x < DEGREE; x++)
		xs[y][x] = c *= m;
	}
	rk = eliminate(xs, rk);
        gk = eliminate(xs, gk);
        bk = eliminate(xs, bk);
	
        double h = 1, r = 0, g = 0, b = 0;
	
        for (int i = 0; i < DEGREE; i++)
        {
	    r += (h *= $hue) * rk[i];
	    g +=  h          * gk[i];
	    b +=  h          * bk[i];
	}
	
        return toStandard(r, g, b);
    }
    
    /**
     * Gaussian elimination
     * 
     * @param   x  Square matrix
     * @param   y  Matrix augment
     * @return     Coefficients
     */
    private static double[] eliminate(final double[][] x, final double[] y)
    {
        int n = x.length;
        final double[] r = new double[n];
        final double[][] b = new double[n][n];
	
        System.arraycopy(y, 0, r, 0, n);
        for (int i = 0; i < n; i++)
            System.arraycopy(x[i], 0, b[i], 0, n);
	
        for (int k = 0, m = n - 1; k < m; k++)
            for (int i = k + 1; i < n; i++)
	    {
		double mul = b[i][k] / b[k][k];
		for (int j = k + 1; j < n; j++)
		    b[i][j] -= b[k][j] * mul;
		r[i] -= r[k] * mul;
	    }
	
        for (int k = n - 1; k > 0; k--)
            for (int i = 0; i < k; i++)
                r[i] -= r[k] * b[i][k] / b[k][k];
	
        for (int k = 0; k < n; k++)
            r[k] /= b[k][k];
	
        return r;
    }
    
}

