package com.hollingsworth.craftedmagic.client.renderer.entity;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ModRenderers {
    public static void register() {
        System.out.println("Rendering entity");
//        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSpell.class,
//                manager -> new RenderProjectile(manager, 0.5f,
//                        new ResourceLocation(ExampleMod.MODID,
//                                "textures/entity/eyeray.png")));

        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSpell.class,
                RenderSpell::new);

    }
}
