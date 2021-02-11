package DLibX.util;

import java.awt.Color;

/**
 * Class to help convert between colourspaces.
 * All calculations are device dependant.
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class ColorHelper {

    /**
     * Converts RGB to a Color
     *
     * @param   r Red component [0-1]
     * @param   g Green component [0-1]
     * @param   b Blue component [0-1]
     * @return  The Color representation
     */

    public static Color rgb(double r, double g, double b) {
        return new Color((int)(r*255), (int)(g*255), (int)(b*255));
    }

    /**
     * Converts RGBa to a Color
     *
     * @param   r Red component [0-1]
     * @param   g Green component [0-1]
     * @param   b Blue component [0-1]
     * @param   a Alpha component [0-1]
     * @return  The Color representation
     */

    public static Color rgb(double r, double g, double b, double a) {
        return new Color((int)(r*255), (int)(g*255), (int)(b*255), (int)(a*255));
    }

    /**
     * Converts HSL to a Color
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   l Lightness component [0-1]
     * @return  The Color representation
     */

    public static Color hsl(double h, double s, double l) {
        final double[] rgb = hslToRgb(h, s, l);
        return rgb(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Converts HSLa to a Color
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   l Lightness component [0-1]
     * @param   a Alpha component [0-1]
     * @return  The Color representation
     */

    public static Color hsl(double h, double s, double l, double a) {
        final double[] rgb = hslToRgb(h, s, l);
        return rgb(rgb[0], rgb[1], rgb[2], a);
    }

    /**
     * Converts HSV to a Color
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   v Value component [0-1]
     * @return  The Color representation
     */

    public static Color hsv(double h, double s, double v) {
        final double[] rgb = hsvToRgb(h, s, v);
        return rgb(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Converts HSVa to a Color
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   v Value component [0-1]
     * @param   a Alpha component [0-1]
     * @return  The Color representation
     */

    public static Color hsv(double h, double s, double v, double a) {
        final double[] rgb = hsvToRgb(h, s, v);
        return rgb(rgb[0], rgb[1], rgb[2], a);
    }

    /**
     * Calculates percieved luminsosity of RGB colour
     *
     * @param   r Red component [0-1]
     * @param   g Green component [0-1]
     * @param   b Blue component [0-1]
     * @return  The calculated luminosity
     */

    public static double lum(double r, double g, double b) {
        return Math.sqrt(0.299*r*r + 0.587*g*g + 0.114*b*b);
    }

    /**
     * Converts RGB to HSL colourspace
     *
     * @param   r Red component [0-1]
     * @param   g Green component [0-1]
     * @param   b Blue component [0-1]
     * @return  The HSL representation {h [0-1], s [0-1], l [0-1]}
     */

    public static double[] rgbToHsl(double r, double g, double b) {
        final double[] hsv = rgbToHsv(r, g, b);
        return hsvToHsl(hsv[0], hsv[1], hsv[2]);
    }

    /**
     * Converts RGB to HSV colourspace
     *
     * @param   r Red component [0-1]
     * @param   g Green component [0-1]
     * @param   b Blue component [0-1]
     * @return  The HSL representation {h [0-1], s [0-1], v [0-1]}
     */

    public static double[] rgbToHsv(double r, double g, double b) {
        final double max = Math.max(r, Math.max(g, b));
        final double min = Math.min(r, Math.min(g, b));
        double h, s, v;
        h = s = v = max;

        final double d = max - min;
        s = (max == 0)? 0: d/max;

        if (max == min) {
            h = 0;
        } else {
            if (max == r) h = (g-b)/d + ((g < b)? 6:0);
            if (max == g) h = (b-r)/d + 2;
            if (max == b) h = (r-g)/d + 4;
            h /= 6;
        }

        return new double[]{h, s, v};
    }

    /**
     * Converts HSL to RGB colourspace
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   l Lightness component [0-1]
     * @return  The RGB representation {r [0-1], g [0-1], b [0-1]}
     */

    public static double[] hslToRgb(double h, double s, double l) {
        final double[] hsv = hslToHsv(h, s, l);
        return hsvToRgb(hsv[0], hsv[1], hsv[2]);
    }

    /**
     * Converts HSL to HSV colourspace
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   l Lightness component [0-1]
     * @return  The HSV representation {h [0-1], s [0-1], v [0-1]}
     */

    public static double[] hslToHsv(double h, double s, double l) {
        l *= 2;
        s *= (l <= 1)? l: 2-l;

        final double v = (l+s)/2;
        final double ss = (2*s)/(l+s);

        return new double[] {h, ss, v};
    }

    /**
     * Converts HSV to RGB colourspace
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   v Lightness component [0-1]
     * @return  The RGB representation {r [0-1], g [0-1], b [0-1]}
     */

    public static double[] hsvToRgb(double h, double s, double v){
        double r, g, b;

        final int i = (int)Math.floor(h*6);
        final double f = h*6 - i;
        final double p = v*(1 - s);
        final double q = v*(1 - f*s);
        final double t = v*(1 - (1-f)*s);

        switch(i % 6){
            case 0 : r = v; g = t; b = p; break;
            case 1 : r = q; g = v; b = p; break;
            case 2 : r = p; g = v; b = t; break;
            case 3 : r = p; g = q; b = v; break;
            case 4 : r = t; g = p; b = v; break;
            default: r = v; g = p; b = q;
        }

        return new double[]{r, g, b};
    }

    /**
     * Converts HSV to RGB colourspace
     *
     * @param   h Hue component [0-1]
     * @param   s Saturation component [0-1]
     * @param   v Lightness component [0-1]
     * @return  The HSL representation {h [0-1], s [0-1], l [0-1]}
     */

    public static double[] hsvToHsl(double h, double s, double v) {
        double ss = s*v;
        double ll = (2-s)*v;

        ss /= (ll <= 1)? ll: 2-ll;
        ll /= 2;

        return new double[] {h, ss, ll};
    }
}
