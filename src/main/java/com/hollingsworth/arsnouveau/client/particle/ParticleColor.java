package com.hollingsworth.arsnouveau.client.particle;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Modified class of ElementType: https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/ElementType.java
 */
public class ParticleColor {

    private final float r;
    private final float g;
    private final float b;
    private final int color;

    public ParticleColor(int r, int g, int b) {
        this.r = r / 255F;
        this.g = g / 255F;
        this.b = b / 255F;
        this.color = (r << 16) | (g << 8) | b;
    }

    public static ParticleColor makeRandomColor(int r, int g, int b, Random random){
        return new ParticleColor(random.nextInt(r), random.nextInt(g), random.nextInt(b));
    }

    public ParticleColor(float r, float g, float b){
        this((int)r,(int) g,(int) b);
    }


    public static ParticleColor fromInt(int color){
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return new ParticleColor(r,g,b);
    }

    public float getRed(){return r;}

    public float getGreen() {
        return g;
    }

    public float getBlue() {
        return b;
    }

    public int getColor() {
        return color;
    }

    public String serialize(){
        return "" + this.r + "," + this.g +","+this.b;
    }

    public IntWrapper toWrapper(){
        return new IntWrapper(this);
    }

    public static ParticleColor deserialize(String string){
        String[] arr = string.split(",");
        return new ParticleColor(Integer.parseInt(arr[0].trim()), Integer.parseInt(arr[1].trim()), Integer.parseInt(arr[2].trim()));
    }

    public static class IntWrapper{
        public int r;
        public int g;
        public int b;

        public IntWrapper(int r, int g, int b){
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public IntWrapper(ParticleColor color){
            this.r = (int) (color.getRed() * 255.0);
            this.g = (int) (color.getGreen() * 255.0);
            this.b = (int) (color.getBlue() * 255.0);
        }

        public ParticleColor toParticleColor(){
            return new ParticleColor(r,g,b);
        }

        public String serialize(){
            return "" + this.r + "," + this.g +","+this.b;
        }

        public void makeVisible(){
            if(r + g + b < 20){
                b += 10;
                g += 10;
                r += 10;
            }
        }

        public static @Nonnull ParticleColor.IntWrapper deserialize(String string){
            ParticleColor.IntWrapper color = ParticleUtil.defaultParticleColorWrapper();
            try{
                String[] arr = string.split(",");
                color = new ParticleColor.IntWrapper(Integer.parseInt(arr[0].trim()), Integer.parseInt(arr[1].trim()), Integer.parseInt(arr[2].trim()));
                return color;
            }catch (Exception e){
                e.printStackTrace();
            }
            return color;
        }
    }
}
