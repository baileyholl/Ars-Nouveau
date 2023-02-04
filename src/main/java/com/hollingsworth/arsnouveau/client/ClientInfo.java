package com.hollingsworth.arsnouveau.client;

import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class ClientInfo {
    private ClientInfo() {
    }

    public static CompoundTag persistentData = new CompoundTag();
    public static int ticksInGame = 0;
    public static float partialTicks = 0.0f;
    public static List<BlockPos> scryingPositions = new ArrayList<>();

    public static TextureTarget skyRenderTarget;
    public static ShaderInstance skyShader;

}
