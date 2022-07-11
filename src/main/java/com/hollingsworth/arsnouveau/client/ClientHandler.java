package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.client.gui.GuiEntityInfoHUD;
import com.hollingsworth.arsnouveau.client.renderer.entity.*;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.FamiliarBookwyrmRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.FamiliarCarbyRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.FamiliarWhirlisprigRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.GenericFamiliarRenderer;
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
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
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
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
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
        event.registerBlockEntityRenderer(BlockRegistry.SCRYERS_OCULUS_TILE, (t) -> new ScryerEyeRenderer(t, new ScryersEyeModel()));


        event.registerEntityRenderer(ModEntities.SPELL_PROJ.get(),
                renderManager -> new RenderSpell(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_FOLLOW_PROJ.get(),
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.SUMMON_SKELETON.get(), RenderSummonSkeleton::new);

        event.registerEntityRenderer(ModEntities.ENTITY_EVOKER_FANGS_ENTITY_TYPE.get(), EvokerFangsRenderer::new);
        event.registerEntityRenderer(ModEntities.ALLY_VEX.get(), VexRenderer::new);

        event.registerEntityRenderer(ModEntities.STARBUNCLE_TYPE.get(), StarbuncleRenderer::new);
        event.registerEntityRenderer(ModEntities.WHIRLISPRIG_TYPE.get(), WhirlisprigRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_WIXIE_TYPE.get(), (t) -> new TextureVariantRenderer<>(t, new WixieModel<>()));
        event.registerEntityRenderer(ModEntities.WILDEN_STALKER.get(), WildenStalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.WILDEN_GUARDIAN.get(), WildenGuardianRenderer::new);
        event.registerEntityRenderer(ModEntities.WILDEN_HUNTER.get(), WildenHunterRenderer::new);
        event.registerEntityRenderer(ModEntities.SUMMON_WOLF.get(), WolfRenderer::new);
        event.registerEntityRenderer(ModEntities.SUMMON_HORSE.get(), HorseRenderer::new);
        event.registerEntityRenderer(ModEntities.LIGHTNING_ENTITY.get(), LightningBoltRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FLYING_ITEM.get(),
                RenderFlyingItem::new);

        event.registerEntityRenderer(ModEntities.ENTITY_RITUAL.get(),
                renderManager -> new RenderRitualProjectile(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_SPELL_ARROW.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_WIXIE_TYPE.get(), (t) -> new TextureVariantRenderer<>(t, new WixieModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_DUMMY.get(), DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_DRYGMY.get(), (t) -> new TextureVariantRenderer<>(t, new DrygmyModel<>()));
        event.registerEntityRenderer(ModEntities.ORBIT_SPELL.get(), renderManager -> new RenderRitualProjectile(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.WILDEN_BOSS.get(), WildenBossRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_CHIMERA_SPIKE.get(), ChimeraProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get(), FamiliarCarbyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_DRYGMY.get(), (t) -> new GenericFamiliarRenderer<>(t, new DrygmyModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_SYLPH.get(), FamiliarWhirlisprigRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_WIXIE.get(), (t) -> new GenericFamiliarRenderer<>(t, new WixieModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_BOOKWYRM_TYPE.get(), BookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get(), FamiliarBookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_JABBERWOG.get(), (t) -> new GenericFamiliarRenderer<>(t, new DrygmyModel<>())); //avoid REI crash
        event.registerEntityRenderer(ModEntities.LINGER_SPELL.get(),
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_CASCADING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "cascading_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_BLAZING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "blazing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_FLOURISHING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "flourishing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_VEXING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "vexing_weald"));

        event.registerEntityRenderer(ModEntities.AMETHYST_GOLEM.get(), AmethystGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.SCRYER_CAMERA.get(), renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.FALLING_BLOCK.get(), EnchantedFallingBlockRenderer::new);

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
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SCRYERS_OCULUS, RenderType.cutout());

        evt.enqueueWork(() -> {
            ItemProperties.register(ItemsRegistry.ENCHANTERS_SHIELD.get(), new ResourceLocation(ArsNouveau.MODID, "blocking"), (item, resourceLocation, livingEntity, arg4) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == item ? 1.0F : 0.0F;
            });
            ItemProperties.register(ItemsRegistry.DOWSING_ROD.get(), new ResourceLocation(ArsNouveau.MODID, "uses"), new ClampedItemPropertyFunction() {
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

        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT,"Ars Nouveau Tooltip", GuiEntityInfoHUD.OVERLAY);
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
                Component lookAround = localize("ars_nouveau.camera.move", settings.keyUp.getTranslatedKeyMessage(), settings.keyLeft.getTranslatedKeyMessage(), settings.keyDown.getTranslatedKeyMessage(), settings.keyRight.getTranslatedKeyMessage());
                Component exit = localize("ars_nouveau.camera.exit", settings.keyShift.getTranslatedKeyMessage());
                font.drawShadow(pose, lookAround, 10, mc.getWindow().getGuiScaledHeight() - 40, 0xFFFFFF);
                font.drawShadow(pose, exit, 10, mc.getWindow().getGuiScaledHeight() - 30, 0xFFFFFF);
            }
        }
    }

    public static Component localize(String key, Object... params) {
        for (int i = 0; i < params.length; ++i) {
            Object parameter = params[i]; //to avoid ij dataflow warning
            if (parameter instanceof Component component && component.getContents() instanceof TranslatableContents translatableContents)
                params[i] = localize(translatableContents.getKey(), translatableContents.getArgs());
        }
        return Component.translatable(key, params);
    }

}
