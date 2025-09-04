package com.hollingsworth.arsnouveau.client.gui;

public class HSLColor {
    private int red;
    private int green;
    private int blue;
    private double hue;
    private double saturation;
    private double lightness;
    private double opacity;
    private String hex;

    public Color toColor() {
        return new Color(toInt());
    }

    public static HSLColor rgb(int red, int green, int blue) {
        return rgb(red, green, blue, (double) 1.0F);
    }

    public static HSLColor rgb(int red, int green, int blue, double opacity) {
        HSLColor color = new HSLColor();
        color.red = red;
        color.green = green;
        color.blue = blue;
        color.opacity = opacity;
        double r = (double) red / (double) 255.0F;
        double g = (double) green / (double) 255.0F;
        double b = (double) blue / (double) 255.0F;
        double max = Math.max(Math.max(r, g), b);
        double min = Math.min(Math.min(r, g), b);
        double delta = max - min;
        color.lightness = (max + min) / (double) 2.0F;
        color.saturation = delta == (double) 0.0F ? (double) 0.0F : delta / ((double) 1.0F - Math.abs((double) 2.0F * color.lightness - (double) 1.0F));
        if (delta == (double) 0.0F) {
            color.hue = 0.0F;
        } else if (max == r) {
            color.hue = (double) 60.0F * ((g - b) / delta + (double) 0.0F);
        } else if (max == g) {
            color.hue = (double) 60.0F * ((b - r) / delta + (double) 2.0F);
        } else if (max == b) {
            color.hue = (double) 60.0F * ((r - g) / delta + (double) 4.0F);
        }

        color.hue = color.hue < (double) 0.0F ? color.hue + (double) 360.0F : (Math.min(color.hue, 360.0F));
        color.hex = String.format("#%02x%02x%02x", red, green, blue);
        return color;
    }

    public static HSLColor hsl(double hue, double saturation, double lightness) {
        return hsl(hue, saturation, lightness, (double) 1.0F);
    }

    public static HSLColor hsl(double hue, double saturation, double lightness, double opacity) {
        double _c = ((double) 1.0F - Math.abs((double) 2.0F * lightness - (double) 1.0F)) * saturation;
        double _h = hue / (double) 60.0F;
        double _x = _c * ((double) 1.0F - Math.abs(_h % (double) 2.0F - (double) 1.0F));
        double[] _rgb = new double[]{(double) 0.0F, (double) 0.0F, (double) 0.0F};
        if (_h >= (double) 0.0F && _h < (double) 1.0F) {
            _rgb = new double[]{_c, _x, (double) 0.0F};
        } else if (_h >= (double) 1.0F && _h < (double) 2.0F) {
            _rgb = new double[]{_x, _c, (double) 0.0F};
        } else if (_h >= (double) 2.0F && _h < (double) 3.0F) {
            _rgb = new double[]{(double) 0.0F, _c, _x};
        } else if (_h >= (double) 3.0F && _h < (double) 4.0F) {
            _rgb = new double[]{(double) 0.0F, _x, _c};
        } else if (_h >= (double) 4.0F && _h < (double) 5.0F) {
            _rgb = new double[]{_x, (double) 0.0F, _c};
        } else if (_h >= (double) 5.0F && _h < (double) 6.0F) {
            _rgb = new double[]{_c, (double) 0.0F, _x};
        }

        double _m = lightness - _c / (double) 2.0F;
        int red = (int) ((_rgb[0] + _m) * (double) 255.0F);
        int green = (int) ((_rgb[1] + _m) * (double) 255.0F);
        int blue = (int) ((_rgb[2] + _m) * (double) 255.0F);
        HSLColor color = rgb(red, green, blue);
        color.opacity = opacity;
        color.hue = hue;
        color.saturation = saturation;
        color.lightness = lightness;
        return color;
    }

    public int toInt() {
        return (((int) (this.opacity * (double) 255.0F) & 255) << 24) + ((this.red & 255) << 16) + ((this.green & 255) << 8) + (this.blue & 255);
    }

    public double getHue() {
        return hue;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getLightness() {
        return lightness;
    }

    public double getOpacity() {
        return opacity;
    }

}
