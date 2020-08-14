package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.ArcanePedestalRenderer;
import com.hollingsworth.arsnouveau.client.renderer.EnchantingApparatusRenderer;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.client.renderer.GlyphPressRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler {
    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {
        System.out.println("Rendering model");
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.GLYPH_PRESS_TILE, GlyphPressRenderer::new);
//        ClientRegistry.bindTileEntitySpecialRenderer(GlyphPressTile.class, new GlyphPressRenderer());
//        ClientRegistry.bindTileEntitySpecialRenderer(ArcanePedestalTile.class, new ArcanePedestalRenderer());
//        ClientRegistry.bindTileEntitySpecialRenderer(EnchantingApparatusTile.class, new EnchantingApparatusRenderer());
    }

}
