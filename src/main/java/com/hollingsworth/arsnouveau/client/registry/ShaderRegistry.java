package com.hollingsworth.arsnouveau.client.registry;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ShaderRegistry extends RenderType {

    private ShaderRegistry(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static final RenderType SKY_RENDER_TYPE = RenderType.create(ArsNouveau.MODID + "_sky", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(()-> ClientInfo.skyShader))
            .setTextureState(new RenderStateShard.EmptyTextureStateShard(()->{

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
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ArsNouveau.prefix( "sky"), DefaultVertexFormat.POSITION), s -> ClientInfo.skyShader = s);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ArsNouveau.prefix( "rainbow_entity"), DefaultVertexFormat.NEW_ENTITY), s -> ClientInfo.rainbowShader = s);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ArsNouveau.prefix( "blamed_entity"), DefaultVertexFormat.NEW_ENTITY), s -> ClientInfo.blameShader = s);
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

    /**
     * Usable for rendering simple flat textures
     *
     * @param  resLoc texture location
     * @return        render type
     */
    public static RenderType worldEntityIcon(final ResourceLocation resLoc)
    {
        return InnerRenderTypes.WORLD_ENTITY_ICON.apply(resLoc);
    }

    public static final class InnerRenderTypes extends RenderType
    {

        private InnerRenderTypes(final String nameIn,
                                 final VertexFormat formatIn,
                                 final VertexFormat.Mode drawModeIn,
                                 final int bufferSizeIn,
                                 final boolean useDelegateIn,
                                 final boolean needsSortingIn,
                                 final Runnable setupTaskIn,
                                 final Runnable clearTaskIn)
        {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
            throw new IllegalStateException();
        }

        private static final Function<ResourceLocation, RenderType> WORLD_ENTITY_ICON = Util.memoize((p_173202_) -> {
            return create("cafetier_entity_icon",
                    DefaultVertexFormat.POSITION_TEX,
                    VertexFormat.Mode.QUADS,
                    1024,
                    false,
                    true,
                    CompositeState.builder()
                            .setShaderState(POSITION_TEX_SHADER)
                            .setTextureState(new TextureStateShard(p_173202_, false, false))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .createCompositeState(false));
        });
    }
}
