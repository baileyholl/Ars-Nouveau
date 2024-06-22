package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.IParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.Random;

/**
 * Modified class of ElementType: https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/ElementType.java
 */
public class ParticleColor implements IParticleColor, Cloneable {
    public static final ResourceLocation ID = ArsNouveau.prefix( "constant");

    public static final ParticleColor DEFAULT = new ParticleColor(255, 25, 180);
    public static final ParticleColor WHITE = new ParticleColor(255, 255, 255);
    public static final ParticleColor RED = new ParticleColor(255, 50, 50);
    public static final ParticleColor GREEN = new ParticleColor(50, 255, 50);
    public static final ParticleColor BLUE = new ParticleColor(50, 50, 255);
    public static final ParticleColor YELLOW = new ParticleColor(255, 255, 0);
    public static final ParticleColor PURPLE = new ParticleColor(255, 50, 255);
    public static final ParticleColor CYAN = new ParticleColor(50, 255, 255);
    public static final ParticleColor ORANGE = new ParticleColor(255, 128, 0);

    public static final ParticleColor MAGENTA = new ParticleColor(255, 0, 255);
    public static final ParticleColor LIGHT_BLUE = new ParticleColor(173, 216, 230);
    public static final ParticleColor LIME = new ParticleColor(0, 255, 0);
    public static final ParticleColor PINK = new ParticleColor(255, 192, 203);
    public static final ParticleColor GRAY = new ParticleColor(128, 128, 128);
    public static final ParticleColor LIGHT_GRAY = new ParticleColor(211, 211, 211);
    public static final ParticleColor BROWN = new ParticleColor(125, 42, 42);

    public static final ParticleColor BLACK = new ParticleColor(0, 0, 0);

    public static final ParticleColor TO_HIGHLIGHT = RED;
    public static final ParticleColor FROM_HIGHLIGHT = CYAN;
    public static final Random random = new Random();

    private final float r;
    private final float g;
    private float b;
    private final int color;

    public ParticleColor(int r, int g, int b) {
        this.r = Math.max(r, 1) / 255F;
        this.g = Math.max(g, 1) / 255F;
        this.b = Math.max(b, 1) / 255F;
        this.color = (r << 16) | (g << 8) | b;
    }

    public ParticleColor(double red, double green, double blue) {
        this((int) red, (int) green, (int) blue);
    }

    public static ParticleColor makeRandomColor(int r, int g, int b, RandomSource random) {
        return new ParticleColor(random.nextInt(r), random.nextInt(g), random.nextInt(b));
    }

    public ParticleColor(float r, float g, float b) {
        this((int) r, (int) g, (int) b);
    }

    public ParticleColor(CompoundTag compoundTag){
        this(compoundTag.getInt("r"), compoundTag.getInt("g"), compoundTag.getInt("b"));
    }

    public static ParticleColor fromInt(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        return new ParticleColor(r, g, b);
    }

    public static ParticleColor defaultParticleColor() {
        return new ParticleColor(255, 25, 180);
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

    public int getColor() {
        return color;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ID;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        // Wrap and store as int because we don't want to lose precision
        ParticleColor.IntWrapper wrapper = toWrapper();
        tag.putInt("r", wrapper.r);
        tag.putInt("g", wrapper.g);
        tag.putInt("b", wrapper.b);
        tag.putString("type", getRegistryName().toString());
        return tag;
    }

    public String toString() {
        return "" + this.r + "," + this.g + "," + this.b;
    }

    public IntWrapper toWrapper() {
        return new IntWrapper(this);
    }

    /**
     * Generates a new color within the max range of the given color.
     */
    public ParticleColor nextColor(RandomSource random) {
        ParticleColor.IntWrapper wrapper = toWrapper();
        return new ParticleColor(random.nextInt(wrapper.r), random.nextInt(wrapper.g), random.nextInt(wrapper.b));
    }

    @Override
    public ParticleColor transition(int ticks) {
        ParticleColor.IntWrapper wrapper = toWrapper();
        return new ParticleColor(random.nextInt(wrapper.r), random.nextInt(wrapper.g), random.nextInt(wrapper.b));
    }

    // Needed because particles can be created over commands
    public static ParticleColor fromString(String string) {
        if (string == null || string.isEmpty())
            return defaultParticleColor();
        String[] arr = string.split(",");
        return new ParticleColor(Integer.parseInt(arr[0].trim()), Integer.parseInt(arr[1].trim()), Integer.parseInt(arr[2].trim()));
    }

    public double euclideanDistance(ParticleColor color) {
        return Math.sqrt(Math.pow(this.r - color.getRed(), 2) + Math.pow(this.g - color.getGreen(), 2) + Math.pow(this.b - color.getBlue(), 2));
    }

    @Override
    public ParticleColor clone() {
        try {
            ParticleColor clone = (ParticleColor) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Deprecated(forRemoval = true)
    public static class IntWrapper implements Cloneable {
        public int r;
        public int g;
        public int b;

        public IntWrapper(int r, int g, int b) {
            this.r = Math.max(1, r);
            this.g = Math.max(1, g);
            this.b = Math.max(1, b);
        }

        public IntWrapper(ParticleColor color) {
            this.r = Math.max(1, (int) (color.getRed() * 255.0));
            this.g = Math.max(1,(int) (color.getGreen() * 255.0));
            this.b = Math.max(1,(int) (color.getBlue() * 255.0));
        }

        public ParticleColor toParticleColor() {
            return new ParticleColor(r, g, b);
        }

        @Override
        public IntWrapper clone() {
            try {
                IntWrapper clone = (IntWrapper) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
