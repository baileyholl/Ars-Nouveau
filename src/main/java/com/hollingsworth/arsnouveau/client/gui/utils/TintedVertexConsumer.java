package com.hollingsworth.arsnouveau.client.gui.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;

public final class TintedVertexConsumer implements VertexConsumer {
    private final VertexConsumer wrapped;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public TintedVertexConsumer(VertexConsumer wrapped, float red, float green, float blue, float alpha) {
        this.wrapped = wrapped;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return wrapped.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return wrapped.color(
                (int) (red * this.red),
                (int) (green * this.green),
                (int) (blue * this.blue),
                (int) (alpha * this.alpha)
        );
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return wrapped.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return wrapped.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return wrapped.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return wrapped.normal(x, y, z);
    }

    @Override
    public void endVertex() {
        wrapped.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        wrapped.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor() {
        wrapped.unsetDefaultColor();
    }

}
