package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShaderRegistry {

    public static final RenderType SKY_RENDER_TYPE = RenderType.create(ArsNouveau.MODID + "_sky", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(()->ClientInfo.skyShader))
            .setTextureState(new RenderStateShard.EmptyTextureStateShard(()->{
                RenderSystem.setShaderTexture(0, ClientInfo.skyRenderTarget.getColorTextureId());
            }, ()->{}))
            .createCompositeState(false)
    );

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(ArsNouveau.MODID, "sky"), DefaultVertexFormat.POSITION), s -> ClientInfo.skyShader = s);
    }
}
