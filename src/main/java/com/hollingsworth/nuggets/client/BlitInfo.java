package com.hollingsworth.nuggets.client;

import net.minecraft.resources.Identifier;

public record BlitInfo(Identifier location, int u, int v, int width, int height, int xOffset, int yOffset) {
    public BlitInfo(Identifier location, int width, int height){
        this(location, 0, 0, width, height, 0, 0);
    }

    public BlitInfo(Identifier location, int u, int v, int width, int height){
        this(location, u, v, width, height, 0, 0);
    }

    /**
     * Offsets used internally for drawing, useful if the texture does not exactly match the locational draw.
     */
    public BlitInfo xOffset(int xOffset){
        return new BlitInfo(location, u, v, width, height, xOffset, yOffset);
    }

    public BlitInfo yOffset(int yOffset){
        return new BlitInfo(location, u, v, width, height, xOffset, yOffset);
    }
}
