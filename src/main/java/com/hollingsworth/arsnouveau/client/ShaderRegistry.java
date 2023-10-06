package com.hollingsworth.arsnouveau.client;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.shader.FixedMultiTextureStateShard;
import com.hollingsworth.arsnouveau.client.shader.Texture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShaderRegistry extends RenderType {

    private ShaderRegistry(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static final RenderType SKY_RENDER_TYPE = RenderType.create(ArsNouveau.MODID + "_sky", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> ClientInfo.skyShader))
            .setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {
                RenderSystem.setShaderTexture(0, ClientInfo.skyRenderTarget.getColorTextureId());
            }, () -> {
            }))
            .createCompositeState(false)
    );

    private static final TriFunction<ResourceLocation, ResourceLocation, Boolean, RenderType> RAINBOW_ENTITY_RENDER_TYPE = memoize((location, mask, outline) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> ClientInfo.rainbowShader))
                .setTextureState(new FixedMultiTextureStateShard(List.of(new Texture(location), new Texture(mask))))
                .setTransparencyState(NO_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(outline);
        return create(ArsNouveau.MODID + "_rainbow_entity", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
    });

    private static final BiFunction<ResourceLocation, Boolean, RenderType> BLAMED_TYPE = Util.memoize((p_173233_, p_173234_) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> ClientInfo.blameShader))
                .setTextureState(new RenderStateShard.TextureStateShard(p_173233_, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(p_173234_);
        return create(ArsNouveau.MODID + "_blame", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
    });

    public static RenderType rainbowEntity(ResourceLocation location, ResourceLocation mask, boolean outline) {
        return RAINBOW_ENTITY_RENDER_TYPE.apply(location, mask, outline);
    }

    public static RenderType blamed(ResourceLocation location, boolean outline) {
        return BLAMED_TYPE.apply(location, outline);
    }

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ArsNouveau.MODID, "sky"), DefaultVertexFormat.POSITION), s -> ClientInfo.skyShader = s);
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ArsNouveau.MODID, "rainbow_entity"), DefaultVertexFormat.NEW_ENTITY), s -> ClientInfo.rainbowShader = s);
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ArsNouveau.MODID, "blamed_entity"), DefaultVertexFormat.NEW_ENTITY), s -> ClientInfo.blameShader = s);
    }

    private static <T, U, V, R> TriFunction<T, U, V, R> memoize(final TriFunction<T, U, V, R> pMemoBiFunction) {
        return new TriFunction<>() {
            private final Map<Triple<T, U, V>, R> cache = Maps.newHashMap();

            public R apply(T t, U u, V v) {
                return this.cache.computeIfAbsent(Triple.of(t, u, v), (triple) -> pMemoBiFunction.apply(triple.getLeft(), triple.getMiddle(), triple.getRight()));
            }

            public String toString() {
                return "memoize/3[function=" + pMemoBiFunction + ", size=" + this.cache.size() + "]";
            }
        };
    }
}
