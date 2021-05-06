package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.GLYPH_PRESS_TILE, PressRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ARCANE_PEDESTAL_TILE, ArcanePedestalRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ENCHANTING_APP_TILE, EnchantingApparatusRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.SCRIBES_TABLE_TILE, ScribeTableRenderer::new);

        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ARCANE_RELAY_TILE, RelayRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.MANA_CONDENSER_TILE, ManaCondenserRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.LIGHT_TILE, LightRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.PORTAL_TILE_TYPE, PortalTileRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ARCANE_RELAY_SPLITTER_TILE, RelaySplitterRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.SPELL_TURRET_TYPE, SpellTurretRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.INTANGIBLE_AIR_TYPE, IntangibleAirRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.VOLCANIC_TILE, VolcanicRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.CRYSTALLIZER_TILE, CrystallizerRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.SUMMONING_CRYSTAL_TILE, SummoningCrystalRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.POTION_MELDER_TYPE, PotionMelderRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.RITUAL_TILE, RitualBrazierRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.SCONCE_TILE, SconceRenderer::new);

        RenderTypeLookup.setRenderLayer(BlockRegistry.MANA_JAR, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.GLYPH_PRESS_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ARCANE_PEDESTAL, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ENCHANTING_APP_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.LIGHT_BLOCK, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.PHANTOM_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.MANA_BLOOM_CROP, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.SCRIBES_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ARCANE_RELAY, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.RUNE_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ARCANE_CORE_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.CRYSTALLIZER_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.SPELL_TURRET, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.VOLCANIC_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.MANA_BERRY_BUSH, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.LAVA_LILY, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.WIXIE_CAULDRON, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.CREATIVE_MANA_JAR, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.VEXING_SAPLING, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.FLOURISHING_SAPLING, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLAZING_SAPLING, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.CASCADING_SAPLING, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.MANA_GEM_BLOCK, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.POTION_JAR, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.POTION_MELDER, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.RITUAL_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.SCONCE_BLOCK, RenderType.cutout());

    }

    @SubscribeEvent
    public static void initColors(final ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, color) -> color > 0 ? -1 :
                (PotionUtils.getPotion(stack) != Potions.EMPTY ? PotionUtils.getColor(stack) : -1),
                ItemsRegistry.POTION_FLASK);

        event.getItemColors().register((stack, color) -> color > 0 ? -1 :
                        (PotionUtils.getPotion(stack) != Potions.EMPTY ? PotionUtils.getColor(stack) : -1),
                ItemsRegistry.POTION_FLASK_EXTEND_TIME);

        event.getItemColors().register((stack, color) -> color > 0 ? -1 :
                        (PotionUtils.getPotion(stack) != Potions.EMPTY ? PotionUtils.getColor(stack) : -1),
                ItemsRegistry.POTION_FLASK_AMPLIFY);

        event.getBlockColors().register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof PotionJarTile
                        ? ((PotionJarTile) reader.getBlockEntity(pos)).getColor()
                        : -1, BlockRegistry.POTION_JAR);
    }

}
