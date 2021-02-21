package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRenderers {
    @SubscribeEvent
    public static void register(final FMLClientSetupEvent event) {

        RenderingRegistry.registerEntityRenderingHandler( ModEntities.SPELL_PROJ,
                renderManager -> new RenderSpell(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        RenderingRegistry.registerEntityRenderingHandler( ModEntities.ENTITY_FOLLOW_PROJ,
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_EVOKER_FANGS_ENTITY_TYPE, RenderFangs::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ALLY_VEX, RenderAllyVex::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_WHELP_TYPE, WhelpRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_CARBUNCLE_TYPE, CarbuncleRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_SYLPH_TYPE, SylphRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_WIXIE_TYPE, WixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.WILDEN_STALKER,renderManager -> new GenericRenderer(renderManager, new WildenStalkerModel()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.WILDEN_HUNTER, WildenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.SUMMON_WOLF, WolfRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.SUMMON_HORSE, HorseRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_FLYING_ITEM,
                RenderFlyingItem::new);

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_RITUAL,
                renderManager -> new RenderRitualProjectile(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_SPELL_ARROW, TippedArrowRenderer::new);


    }
}
