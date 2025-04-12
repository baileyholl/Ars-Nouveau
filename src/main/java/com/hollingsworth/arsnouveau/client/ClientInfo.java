package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClientInfo {
    private ClientInfo() {
    }

    public static CompoundTag persistentData = new CompoundTag();
    public static int ticksInGame = 0;
    public static int redOverlayTicks = 0;
    public static float redOverlayMana = 0;
    public static float reservedOverlayMana = 0.15F;

    public static float partialTicks = 0.0f;

    public static float deltaTicks = 0;
    public static float totalTicks = 0;

    public static List<BlockPos> scryingPositions = new ArrayList<>();

    public static List<ColorPos> highlightPositions = new ArrayList<>();
    public static int highlightTicks;

    public static TextureTarget skyRenderTarget;
    public static ShaderInstance skyShader;
    public static ShaderInstance blameShader;
    public static ShaderInstance rainbowShader;

    public static boolean isSupporter = false;

    public static Component[] storageTooltip = new Component[0];
    public static void setTooltip(Component... string) {
        storageTooltip = string;
    }

    public static void highlightPosition(List<ColorPos> colorPos, int ticks){
        highlightPositions = colorPos;
        highlightTicks = ticks;
    }

    private static void calcDelta(){
        float oldTotal = ClientInfo.totalTicks;
        totalTicks = ClientInfo.totalTicks + ClientInfo.partialTicks;
        deltaTicks = totalTicks - oldTotal;
    }

    public static void renderTickStart(float pt) {
        partialTicks = pt;
    }

    public static void renderTickEnd() {
        calcDelta();
    }

    public static void endClientTick(){
        ClientInfo.ticksInGame++;
        partialTicks = 0f;
        if (ClientInfo.redTicks()) {
            ClientInfo.redOverlayTicks--;
        }
        calcDelta();
    }

    public static boolean redTicks() {
        return redOverlayTicks > 0;
    }
}
