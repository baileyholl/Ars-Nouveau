package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.entity.*;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(BlockRegistry.GLYPH_PRESS_TILE, PressRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_PEDESTAL_TILE, ArcanePedestalRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ENCHANTING_APP_TILE, EnchantingApparatusRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.SCRIBES_TABLE_TILE, ScribesRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.AGRONOMIC_SOURCELINK_TILE, AgronomicRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.LIGHT_TILE, LightRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.PORTAL_TILE_TYPE, PortalTileRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.INTANGIBLE_AIR_TYPE, IntangibleAirRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.VOLCANIC_TILE, VolcanicRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.CRYSTALLIZER_TILE, CrystallizerRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.POTION_MELDER_TYPE, PotionMelderRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RITUAL_TILE, RitualBrazierRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ALCHEMICAL_TILE, AlchemicalRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.VITALIC_TILE, VitalicRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MYCELIAL_TILE, MycelialRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_DEPOSIT_TILE, (t) -> new GenericRenderer(t, "source_deposit"));
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_WARP_TILE, (t) -> new GenericRenderer(t, "source_warp"));
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_RELAY_TILE, (t) -> new GenericRenderer(t, "source_relay"));
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_RELAY_SPLITTER_TILE, (t) -> new GenericRenderer(t, "source_splitter"));
        event.registerBlockEntityRenderer(BlockRegistry.BASIC_SPELL_TURRET_TILE, BasicTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.SPELL_TURRET_TYPE, ReducerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.TIMER_SPELL_TURRET_TILE, TimerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCHWOOD_CHEST_TILE, ArchwoodChestRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RUNE_TILE, RuneRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.PHANTOM_TILE, MageBlockRenderer::new);

        event.registerEntityRenderer( ModEntities.SPELL_PROJ,
                renderManager -> new RenderSpell(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer( ModEntities.ENTITY_FOLLOW_PROJ,
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_EVOKER_FANGS_ENTITY_TYPE, RenderFangs::new);
        event.registerEntityRenderer(ModEntities.ALLY_VEX, RenderAllyVex::new);
        event.registerEntityRenderer(ModEntities.ENTITY_CARBUNCLE_TYPE, CarbuncleRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_SYLPH_TYPE, SylphRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_WIXIE_TYPE, WixieRenderer::new);
        event.registerEntityRenderer(ModEntities.WILDEN_STALKER,renderManager -> new com.hollingsworth.arsnouveau.client.renderer.entity.GenericRenderer(renderManager, new WildenStalkerModel()));
        event.registerEntityRenderer(ModEntities.WILDEN_GUARDIAN, WildenGuardianRenderer::new);
        event.registerEntityRenderer(ModEntities.WILDEN_HUNTER, WildenRenderer::new);
        event.registerEntityRenderer(ModEntities.SUMMON_WOLF, WolfRenderer::new);
        event.registerEntityRenderer(ModEntities.SUMMON_HORSE, HorseRenderer::new);
        event.registerEntityRenderer(ModEntities.LIGHTNING_ENTITY, LightningBoltRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FLYING_ITEM,
                RenderFlyingItem::new);

        event.registerEntityRenderer(ModEntities.ENTITY_RITUAL,
                renderManager -> new RenderRitualProjectile(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_SPELL_ARROW, TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_WIXIE_TYPE, WixieRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_DUMMY, DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_DRYGMY, DrygmyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_WARD,  renderManager -> new RenderRitualProjectile(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.WILDEN_BOSS, WildenBossRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_CHIMERA_SPIKE, ChimeraProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_CARBUNCLE, FamiliarCarbyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_DRYGMY, DrygmyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_SYLPH, SylphRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_WIXIE, WixieRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_BOOKWYRM_TYPE, BookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_BOOKWYRM, BookwyrmRenderer::new);
        event.registerEntityRenderer( ModEntities.LINGER_SPELL,
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_CASCADING_WEALD, (v) -> new WealdWalkerRenderer(v, "cascading_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_BLAZING_WEALD, (v) -> new WealdWalkerRenderer(v, "blazing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_FLOURISHING_WEALD, (v) -> new WealdWalkerRenderer(v, "flourishing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_VEXING_WEALD, (v) -> new WealdWalkerRenderer(v, "vexing_weald"));


    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {
//
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MANA_JAR, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.GLYPH_PRESS_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ARCANE_PEDESTAL, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ENCHANTING_APP_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LIGHT_BLOCK, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.PHANTOM_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MAGE_BLOOM_CROP, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SCRIBES_BLOCK, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ARCANE_RELAY, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUNE_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ARCANE_CORE_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CRYSTALLIZER_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.VOLCANIC_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MANA_BERRY_BUSH, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LAVA_LILY, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.WIXIE_CAULDRON, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CREATIVE_MANA_JAR, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.VEXING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.FLOURISHING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BLAZING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CASCADING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MANA_GEM_BLOCK, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.POTION_JAR, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.POTION_MELDER, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RITUAL_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SCONCE_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.DRYGMY_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ALCHEMICAL_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.VITALIC_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MYCELIAL_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RELAY_WARP, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RELAY_DEPOSIT, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BOOKWYRM_LECTERN, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SPELL_TURRET, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BASIC_SPELL_TURRET, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.TIMER_SPELL_TURRET, RenderType.cutout());
        evt.enqueueWork(() -> {
            ItemProperties.register(ItemsRegistry.ENCHANTERS_SHIELD,new ResourceLocation(ArsNouveau.MODID,"blocking"), (p_239421_0_, p_239421_1_, p_239421_2_, arg4) -> {
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
