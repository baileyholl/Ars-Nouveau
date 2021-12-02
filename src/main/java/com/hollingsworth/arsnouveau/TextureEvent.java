package com.hollingsworth.arsnouveau;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class TextureEvent {

    @SubscribeEvent
    public static void textEvent(TextureStitchEvent.Pre event){
        if(event.getMap().location().toString().equals("minecraft:textures/atlas/chest.png")) {
            ResourceLocation resNormal = new ResourceLocation(ArsNouveau.MODID,"entity/archwood_chest");
            ResourceLocation resLeft = new ResourceLocation(ArsNouveau.MODID,"entity/archwood_chest_left");
            ResourceLocation resRight = new ResourceLocation(ArsNouveau.MODID,"entity/archwood_chest_right");
            event.addSprite(resNormal);
            event.addSprite(resLeft);
            event.addSprite(resRight);
        }
    }
}
