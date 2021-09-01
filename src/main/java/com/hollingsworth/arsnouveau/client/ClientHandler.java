package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
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
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.SCRIBES_TABLE_TILE, ScribesRenderer::new);


        ClientRegistry.bindTileEntityRenderer(BlockRegistry.AGRONOMIC_SOURCELINK_TILE, AgronomicRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.LIGHT_TILE, LightRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.PORTAL_TILE_TYPE, PortalTileRenderer::new);


        ClientRegistry.bindTileEntityRenderer(BlockRegistry.INTANGIBLE_AIR_TYPE, IntangibleAirRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.VOLCANIC_TILE, VolcanicRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.CRYSTALLIZER_TILE, CrystallizerRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.SUMMONING_CRYSTAL_TILE, SummoningCrystalRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.POTION_MELDER_TYPE, PotionMelderRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.RITUAL_TILE, RitualBrazierRenderer::new);

        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ALCHEMICAL_TILE, AlchemicalRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.VITALIC_TILE, VitalicRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.MYCELIAL_TILE, MycelialRenderer::new);

        ClientRegistry.bindTileEntityRenderer(BlockRegistry.RELAY_DEPOSIT_TILE, (t) -> new GenericRenderer(t, "source_deposit"));
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.RELAY_WARP_TILE, (t) -> new GenericRenderer(t, "source_warp"));
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ARCANE_RELAY_TILE, (t) -> new GenericRenderer(t, "source_relay"));
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ARCANE_RELAY_SPLITTER_TILE, (t) -> new GenericRenderer(t, "source_splitter"));
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.BASIC_SPELL_TURRET_TILE, BasicTurretRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.SPELL_TURRET_TYPE, ReducerTurretRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.TIMER_SPELL_TURRET_TILE, TimerTurretRenderer::new);

        RenderTypeLookup.setRenderLayer(BlockRegistry.MANA_JAR, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.GLYPH_PRESS_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ARCANE_PEDESTAL, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ENCHANTING_APP_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.LIGHT_BLOCK, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.PHANTOM_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.MAGE_BLOOM_CROP, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.SCRIBES_BLOCK, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ARCANE_RELAY, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.RUNE_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ARCANE_CORE_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.CRYSTALLIZER_BLOCK, RenderType.cutout());
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
        RenderTypeLookup.setRenderLayer(BlockRegistry.POTION_JAR, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.POTION_MELDER, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.RITUAL_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.SCONCE_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.DRYGMY_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.ALCHEMICAL_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.VITALIC_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.MYCELIAL_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.RELAY_WARP, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.RELAY_DEPOSIT, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.BOOKWYRM_LECTERN, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.SPELL_TURRET, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.BASIC_SPELL_TURRET, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.TIMER_SPELL_TURRET, RenderType.cutout());
        evt.enqueueWork(() -> {
            ItemModelsProperties.register(ItemsRegistry.ENCHANTERS_SHIELD,new ResourceLocation(ArsNouveau.MODID,"blocking"), (p_239421_0_, p_239421_1_, p_239421_2_) -> {
                return p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == p_239421_0_ ? 1.0F : 0.0F;
            });
        });
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
