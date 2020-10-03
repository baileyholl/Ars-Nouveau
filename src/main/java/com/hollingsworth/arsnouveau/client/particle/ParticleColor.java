package com.hollingsworth.arsnouveau.client.particle;

/**
 * Modified class of ElementType: https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/ElementType.java
 */
public class ParticleColor {

    private final float r;
    private final float g;
    private final float b;
    private final int color;
    private final float alpha;
    public ParticleColor(int r, int g, int b) {
        this.r = r / 255F;
        this.g = g / 255F;
        this.b = b / 255F;
        this.color = (r << 16) | (g << 8) | b;
        alpha = 1.0f;
    }

    public ParticleColor(int r, int g, int b, float a){
        this.r = r / 255F;
        this.g = g / 255F;
        this.b = b / 255F;
        this.color = (r << 16) | (g << 8) | b;
        this.alpha = a;
    }

    public float getRed() {
        return r;
    }

    public float getGreen() {
        return g;
    }

    public float getBlue() {
        return b;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getColor() {
        return color;
    }

    public String serialize(){
        return "" + this.r + "," + this.g +","+this.b + "," + this.alpha;
    }

    public static ParticleColor deserialize(String string){
        String[] arr = string.split(",");
        return new ParticleColor(Integer.parseInt(arr[0].trim()), Integer.parseInt(arr[1].trim()), Integer.parseInt(arr[2].trim()), Float.parseFloat(arr[3].trim()));
    }
}
