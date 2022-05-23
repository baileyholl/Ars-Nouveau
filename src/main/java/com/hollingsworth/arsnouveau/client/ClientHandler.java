package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.client.renderer.entity.*;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_PEDESTAL_TILE, ArcanePedestalRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ENCHANTING_APP_TILE, EnchantingApparatusRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.SCRIBES_TABLE_TILE, ScribesRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.AGRONOMIC_SOURCELINK_TILE, AgronomicRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.LIGHT_TILE, LightRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.PORTAL_TILE_TYPE, PortalTileRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.INTANGIBLE_AIR_TYPE, IntangibleAirRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.VOLCANIC_TILE, VolcanicRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.IMBUEMENT_TILE, ImbuementRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.POTION_MELDER_TYPE, PotionMelderRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RITUAL_TILE, RitualBrazierRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ALCHEMICAL_TILE, AlchemicalRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.VITALIC_TILE, VitalicRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MYCELIAL_TILE, MycelialRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_DEPOSIT_TILE, (t) -> new GenericRenderer(t, "source_deposit"));
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_WARP_TILE, (t) -> new GenericRenderer(t, "source_warp"));
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_RELAY_TILE, (t) -> new GenericRenderer(t, "source_relay"));
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_SPLITTER_TILE, (t) -> new GenericRenderer(t, "source_splitter"));
        event.registerBlockEntityRenderer(BlockRegistry.BASIC_SPELL_TURRET_TILE, BasicTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ENCHANTED_SPELL_TURRET_TYPE, ReducerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.TIMER_SPELL_TURRET_TILE, TimerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCHWOOD_CHEST_TILE, ArchwoodChestRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RUNE_TILE, RuneRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MAGE_BLOCK_TILE, MageBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.WHIRLISPRIG_TILE, WhirlisprigFlowerRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_CORE_TILE, ArcaneCoreRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_COLLECTOR_TILE, (t) -> new GenericRenderer(t, "source_collector"));
        event.registerBlockEntityRenderer(BlockRegistry.SCRYERS_EYE_TILE, (t) -> new ScryerEyeRenderer(t, new ScryersEyeModel()));


        event.registerEntityRenderer( ModEntities.SPELL_PROJ,
                renderManager -> new RenderSpell(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer( ModEntities.ENTITY_FOLLOW_PROJ,
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_EVOKER_FANGS_ENTITY_TYPE, RenderFangs::new);
        event.registerEntityRenderer(ModEntities.ALLY_VEX, RenderAllyVex::new);
        event.registerEntityRenderer(ModEntities.STARBUNCLE_TYPE, StarbuncleRenderer::new);
        event.registerEntityRenderer(ModEntities.WHIRLISPRIG_TYPE, SylphRenderer::new);
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
        event.registerEntityRenderer(ModEntities.ORBIT_SPELL, renderManager -> new RenderRitualProjectile(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.WILDEN_BOSS, WildenBossRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_CHIMERA_SPIKE, ChimeraProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_STARBUNCLE, FamiliarCarbyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_DRYGMY, DrygmyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_SYLPH, SylphRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_WIXIE, WixieRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_BOOKWYRM_TYPE, BookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_BOOKWYRM, BookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_JABBERWOG, BookwyrmRenderer::new); //avoid REI crash
        event.registerEntityRenderer( ModEntities.LINGER_SPELL,
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_CASCADING_WEALD, (v) -> new WealdWalkerRenderer(v, "cascading_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_BLAZING_WEALD, (v) -> new WealdWalkerRenderer(v, "blazing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_FLOURISHING_WEALD, (v) -> new WealdWalkerRenderer(v, "flourishing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_VEXING_WEALD, (v) -> new WealdWalkerRenderer(v, "vexing_weald"));

        event.registerEntityRenderer(ModEntities.AMETHYST_GOLEM, AmethystGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.SCRYER_CAMERA,  renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));


    }
    public static IIngameOverlay cameraOverlay;
    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {
//
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SOURCE_JAR, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ARCANE_PEDESTAL, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ENCHANTING_APP_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LIGHT_BLOCK, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MAGE_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MAGE_BLOOM_CROP, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SCRIBES_BLOCK, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RELAY, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUNE_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ARCANE_CORE_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.IMBUEMENT_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.VOLCANIC_BLOCK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SOURCEBERRY_BUSH, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LAVA_LILY, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.WIXIE_CAULDRON, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CREATIVE_SOURCE_JAR, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.VEXING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.FLOURISHING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BLAZING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CASCADING_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SOURCE_GEM_BLOCK, RenderType.translucent());
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
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ENCHANTED_SPELL_TURRET, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BASIC_SPELL_TURRET, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.TIMER_SPELL_TURRET, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.WHIRLISPRIG_FLOWER, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SCRYERS_CRYSTAL, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SCRYERS_EYE, RenderType.cutout());

        evt.enqueueWork(() -> {
            ItemProperties.register(ItemsRegistry.ENCHANTERS_SHIELD,new ResourceLocation(ArsNouveau.MODID,"blocking"), (item, resourceLocation, livingEntity, arg4) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == item ? 1.0F : 0.0F;
            });
            ItemProperties.register(ItemsRegistry.DOWSING_ROD, new ResourceLocation(ArsNouveau.MODID, "uses"), new ClampedItemPropertyFunction() {
                @Override
                public float unclampedCall(ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
                    return switch (pStack.getDamageValue()) {
                        case 1 -> 0.75f;
                        case 2 -> 0.50f;
                        case 3 -> 0.25f;
                        default -> 1.0f;
                    };
                }
            });
        });

        cameraOverlay = OverlayRegistry.registerOverlayTop("ars_nouveau:camera_overlay", ClientHandler::cameraOverlay);
        OverlayRegistry.enableOverlay(cameraOverlay, false);
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

    public static void cameraOverlay(ForgeIngameGui gui, PoseStack pose, float partialTicks, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        BlockPos pos = mc.cameraEntity.blockPosition();
        if (!mc.options.renderDebug) {
            BlockEntity var10 = level.getBlockEntity(pos);
            if (var10 instanceof ICameraMountable be) {
                Font font = Minecraft.getInstance().font;
                Options settings = Minecraft.getInstance().options;
                TranslatableComponent lookAround = localize("ars_nouveau.camera.move", new Object[]{settings.keyUp.getTranslatedKeyMessage(), settings.keyLeft.getTranslatedKeyMessage(), settings.keyDown.getTranslatedKeyMessage(), settings.keyRight.getTranslatedKeyMessage()});
                TranslatableComponent exit = localize("ars_nouveau.camera.exit", new Object[]{settings.keyShift.getTranslatedKeyMessage()});
                font.drawShadow(pose,lookAround, 10, mc.getWindow().getGuiScaledHeight() - 40 , 0xFFFFFF);
                font.drawShadow(pose,exit, 10, mc.getWindow().getGuiScaledHeight() - 30 , 0xFFFFFF);
            }
        }
    }

    public static TranslatableComponent localize(String key, Object... params) {
        for(int i = 0; i < params.length; ++i) {
            Object var5 = params[i];
            if (var5 instanceof TranslatableComponent) {
                TranslatableComponent component = (TranslatableComponent)var5;
                params[i] = localize(component.getKey(), component.getArgs());
            }
        }

        return new TranslatableComponent(key, params);
    }

}
