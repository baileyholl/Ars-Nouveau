package com.hollingsworth.nuggets.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Shared client-side data for the Nuggets utility library.
 * Provides tick counter and buffer source for GUI rendering.
 */
public class NuggetClientData {
    public static int ticksInGame = 0;
    public static MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
}
