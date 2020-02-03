package com.hollingsworth.craftedmagic.client.renderer.entity;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
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
        System.out.println("Rendering entity");
//        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSpell.class,
//                manager -> new RenderProjectile(manager, 0.5f,
//                        new ResourceLocation(ExampleMod.MODID,
//                                "textures/entity/spell_proj.png")));

        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSpell.class,
                renderManager -> new RenderSpell(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));

    }
}
