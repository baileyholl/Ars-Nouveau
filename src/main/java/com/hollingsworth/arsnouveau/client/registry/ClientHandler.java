package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import com.hollingsworth.arsnouveau.client.gui.GuiEntityInfoHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiManaHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiSpellHUD;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.entity.*;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.*;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.tile.MageBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import com.hollingsworth.arsnouveau.common.entity.*;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.*;
// ItemProperties and ClampedItemPropertyFunction removed in MC 1.21.11 — replaced by JSON item model properties
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.hollingsworth.arsnouveau.client.events.ClientEvents.localize;


@SuppressWarnings("unchecked")
@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ClientHandler {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_PEDESTAL_TILE.get(), ArcanePedestalRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ENCHANTING_APP_TILE.get(), EnchantingApparatusRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.SCRIBES_TABLE_TILE.get(), ScribesRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.PORTAL_TILE_TYPE.get(), PortalTileRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.SKYWEAVE_TILE.get(), SkyBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.INTANGIBLE_AIR_TYPE.get(), IntangibleAirRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.IMBUEMENT_TILE.get(), ImbuementRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.POTION_MELDER_TYPE.get(), PotionMelderRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_DEPOSIT_TILE.get(), (t) -> new GenericTileRenderer<>(t, "source_deposit"));
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_WARP_TILE.get(), (t) -> new GenericTileRenderer<>(t, "source_warp"));
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_RELAY_TILE.get(), (t) -> new GenericTileRenderer<>(t, "source_relay"));
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_SPLITTER_TILE.get(), (t) -> new GenericTileRenderer<>(t, "source_splitter"));
        event.registerBlockEntityRenderer(BlockRegistry.BASIC_SPELL_TURRET_TILE.get(), BasicTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ROTATING_TURRET_TILE.get(), RotatingTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ENCHANTED_SPELL_TURRET_TYPE.get(), ReducerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.TIMER_SPELL_TURRET_TILE.get(), TimerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCHWOOD_CHEST_TILE.get(), ArchwoodChestRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RUNE_TILE.get(), RuneRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.WHIRLISPRIG_TILE.get(), WhirlisprigFlowerRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_CORE_TILE.get(), ArcaneCoreRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_COLLECTOR_TILE.get(), (t) -> new GenericTileRenderer<>(t, "source_collector"));
        event.registerBlockEntityRenderer(BlockRegistry.SCRYERS_OCULUS_TILE.get(), (t) -> new ScryerOculusRenderer(t, new ScryersEyeModel()));
        event.registerBlockEntityRenderer(BlockRegistry.ARMOR_TILE.get(), AlterationTableRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MOB_JAR_TILE.get(), MobJarRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MIRROR_WEAVE_TILE.get(), MirrorweaveRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.GHOST_WEAVE_TILE.get(), GhostweaveRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.FALSE_WEAVE_TILE.get(), FalseweaveRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.TEMPORARY_TILE.get(), MirrorweaveRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.CRAFTING_LECTERN_TILE.get(), LecternRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ITEM_DETECTOR_TILE.get(), ItemDetectorRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.REPOSITORY_TILE.get(), RepositoryRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.REDSTONE_RELAY_TILE.get(), RedstoneRelayRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.PLANARIUM_TILE.get(), PlanariumRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.REPOSITORY_CONTROLLER_TILE.get(), (t) -> new GenericTileRenderer<>(t, new RepoControllerModel()));
        event.registerBlockEntityRenderer(BlockRegistry.DECOR_BLOSSOM_TILE.get(), BlossomRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.PLANARIUM_PROJECTOR_TILE.get(), PlanariumProjectorRenderer::new);
//        event.registerBlockEntityRenderer(BlockRegistry.SCRYER_PLANARIUM_TILE.get(), ScryerPlanariumRenderer::new);

        event.registerEntityRenderer(ModEntities.SPELL_PROJ.get(), StyledSpellRender::new);
        event.registerEntityRenderer(ModEntities.SPELL_PROJ_ARC.get(),
                renderManager -> new RenderSpell(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.SPELL_PROJ_HOM.get(),
                renderManager -> new RenderSpell(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));

        event.registerEntityRenderer(ModEntities.ENTITY_FOLLOW_PROJ.get(),
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.SUMMON_SKELETON.get(), RenderSummonSkeleton::new);

        event.registerEntityRenderer(ModEntities.ENTITY_EVOKER_FANGS_ENTITY_TYPE.get(), EvokerFangsRenderer::new);
        event.registerEntityRenderer(ModEntities.ALLY_VEX.get(), VexRenderer::new);

        event.registerEntityRenderer(ModEntities.STARBUNCLE_TYPE.get(), StarbuncleRenderer::new);
        // 1.21.11: GeoEntityRenderer requires explicit type R extends EntityRenderState & GeoRenderState.
        // getTextureLocation(T) no longer exists in EntityRenderer — dynamic texture broken until
        // entity texture variant is stored in a custom GeoRenderState (TODO).
        event.registerEntityRenderer(ModEntities.WHIRLISPRIG_TYPE.get(), (t) -> new GeoEntityRenderer<Whirlisprig, ArsEntityRenderState>(t, new WhirlisprigModel<>()) {
            @Override
            public ArsEntityRenderState createRenderState(Whirlisprig animatable, Void context) {
                return new ArsEntityRenderState();
            }
        });
        event.registerEntityRenderer(ModEntities.ENTITY_WIXIE_TYPE.get(), (t) -> new GeoEntityRenderer<EntityWixie, ArsEntityRenderState>(t, new WixieModel<>()) {
            @Override
            public ArsEntityRenderState createRenderState(EntityWixie animatable, Void context) {
                return new ArsEntityRenderState();
            }
        });
        event.registerEntityRenderer(ModEntities.WILDEN_STALKER.get(), (t) -> new GeoEntityRenderer<WildenStalker, ArsEntityRenderState>(t, new WildenStalkerModel()));
        event.registerEntityRenderer(ModEntities.WILDEN_GUARDIAN.get(), (t) -> new GeoEntityRenderer<WildenGuardian, ArsEntityRenderState>(t, new WildenGuardianModel()));
        event.registerEntityRenderer(ModEntities.WILDEN_HUNTER.get(), (t) -> new GeoEntityRenderer<WildenHunter, ArsEntityRenderState>(t, new WildenHunterModel()));
        event.registerEntityRenderer(ModEntities.SUMMON_WOLF.get(), WolfRenderer::new);
        event.registerEntityRenderer(ModEntities.SUMMON_HORSE.get(), HorseRenderer::new);
        event.registerEntityRenderer(ModEntities.LIGHTNING_ENTITY.get(), LightningBoltRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FLYING_ITEM.get(),
                RenderFlyingItem::new);

        event.registerEntityRenderer(ModEntities.ENTITY_RITUAL.get(),
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_SPELL_ARROW.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_WIXIE_TYPE.get(), (t) -> new GeoEntityRenderer<EntityWixie, ArsEntityRenderState>(t, new WixieModel<>()) {
            @Override
            public ArsEntityRenderState createRenderState(EntityWixie animatable, Void context) {
                return new ArsEntityRenderState();
            }
        });
        event.registerEntityRenderer(ModEntities.ENTITY_DUMMY.get(), DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_DRYGMY.get(), (t) -> new GeoEntityRenderer<EntityDrygmy, ArsEntityRenderState>(t, new DrygmyModel<>()) {
            @Override
            public ArsEntityRenderState createRenderState(EntityDrygmy animatable, Void context) {
                return new ArsEntityRenderState();
            }

            @Override
            public void addRenderData(EntityDrygmy entity, Void context, ArsEntityRenderState state, float partialTick) {
                super.addRenderData(entity, context, state, partialTick);
                state.addGeckolibData(com.hollingsworth.arsnouveau.client.renderer.ANDataTickets.DRYGMY_COLOR, entity.getColor());
            }
        });
        event.registerEntityRenderer(ModEntities.ORBIT_SPELL.get(), renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.WILDEN_BOSS.get(), rendermanager -> new GeoEntityRenderer<WildenChimera, ArsEntityRenderState>(rendermanager, new WildenChimeraModel()));
        event.registerEntityRenderer(ModEntities.ENTITY_CHIMERA_SPIKE.get(), ChimeraProjectileRenderer::new);
        // TODO: special shader render types (rainbow/blame) for named starbuncles need GeckoLib 5.4.2 migration
        // getRenderType now takes (GeoRenderState/LivingEntityRenderState, Identifier) — entity name
        // available via renderState.nameTag; shader stubs use entityCutoutNoCull anyway
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get(), (t) -> new GenericFamiliarRenderer<>(t, new FamiliarStarbyModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_DRYGMY.get(), (t) -> new GenericFamiliarRenderer<>(t, new DrygmyModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_SYLPH.get(), FamiliarWhirlisprigRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_WIXIE.get(), (t) -> new GenericFamiliarRenderer<>(t, new WixieModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_BOOKWYRM_TYPE.get(), BookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.FAMILIAR_AMETHYST_GOLEM.get(), AmethystGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get(), FamiliarBookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.LINGER_SPELL.get(),
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_CASCADING_WEALD.get(), (v) -> new GeoEntityRenderer<WealdWalker, ArsEntityRenderState>(v, new WealdWalkerModel<>("cascading_weald")));
        event.registerEntityRenderer(ModEntities.ENTITY_BLAZING_WEALD.get(), (v) -> new GeoEntityRenderer<WealdWalker, ArsEntityRenderState>(v, new WealdWalkerModel<>("blazing_weald")));
        event.registerEntityRenderer(ModEntities.ENTITY_FLOURISHING_WEALD.get(), (v) -> new GeoEntityRenderer<WealdWalker, ArsEntityRenderState>(v, new WealdWalkerModel<>("flourishing_weald")));
        event.registerEntityRenderer(ModEntities.ENTITY_VEXING_WEALD.get(), (v) -> new GeoEntityRenderer<WealdWalker, ArsEntityRenderState>(v, new WealdWalkerModel<>("vexing_weald")));

        event.registerEntityRenderer(ModEntities.AMETHYST_GOLEM.get(), AmethystGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.SCRYER_CAMERA.get(), renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENCHANTED_FALLING_BLOCK.get(), EnchantedFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ICE_SHARD.get(), EnchantedFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ENCHANTED_MAGE_BLOCK.get(), MageBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ENCHANTED_HEAD_BLOCK.get(), EnchantedSkullRenderer::new);
        event.registerEntityRenderer(ModEntities.GIFT_STARBY.get(), (renderer) -> new GeoEntityRenderer<GiftStarbuncle, ArsEntityRenderState>(renderer, new GiftStarbyModel()));
        event.registerEntityRenderer(ModEntities.ANIMATED_BLOCK.get(), AnimBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ANIMATED_HEAD.get(), AnimSkullRenderer::new);
        event.registerEntityRenderer(ModEntities.CINDER.get(), CinderRenderer::new);
        event.registerEntityRenderer(ModEntities.WALL_SPELL.get(),
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix("textures/entity/spell_proj.png")));

        event.registerEntityRenderer(ModEntities.LILY.get(), (v) -> new GeoEntityRenderer<Lily, ArsEntityRenderState>(v, new LilyModel()));
        event.registerEntityRenderer(ModEntities.NOOK.get(), (v) -> new GeoEntityRenderer<Nook, ArsEntityRenderState>(v, new NookModel()));
        event.registerEntityRenderer(ModEntities.ALAKARKINOS_TYPE.get(), (v) -> new GeoEntityRenderer<Alakarkinos, ArsEntityRenderState>(v, new AlakarkinosModel()));
        event.registerEntityRenderer(ModEntities.BUBBLE.get(), BubbleRenderer::new);
        event.registerEntityRenderer(ModEntities.ENCHANTED_HOOK.get(), FishingHookRenderer::new);

        event.registerEntityRenderer(ModEntities.ARCHWOOD_BOAT.get(), context -> new ArchwoodBoatRenderer(context, false));
    }

    public static GuiLayer cameraOverlay = (gui, tracker) -> {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        // 1.21.11: cameraEntity became private; use getCameraEntity()
        BlockPos pos = mc.getCameraEntity().blockPosition();
        if (!CameraUtil.isPlayerMountedOnCamera(mc.player) || mc.options.hideGui) {
            return;
        }
        if (!mc.options.reducedDebugInfo().get()) {
            BlockEntity var10 = level.getBlockEntity(pos);
            if (var10 instanceof ICameraMountable be) {
                Font font = Minecraft.getInstance().font;
                Options settings = Minecraft.getInstance().options;
                Component lookAround = localize("ars_nouveau.camera.move", settings.keyUp.getTranslatedKeyMessage(), settings.keyLeft.getTranslatedKeyMessage(), settings.keyDown.getTranslatedKeyMessage(), settings.keyRight.getTranslatedKeyMessage());
                Component exit = Component.translatable("ars_nouveau.camera.exit", settings.keyShift.getTranslatedKeyMessage().getString());
                gui.drawString(font, lookAround, 10, mc.getWindow().getGuiScaledHeight() - 40, 0xFFFFFFFF);
                gui.drawString(font, exit, 10, mc.getWindow().getGuiScaledHeight() - 30, 0xFFFFFFFF);
            }
        }
    };

    @SubscribeEvent
    public static void registerMenu(final RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.STORAGE.get(), CraftingTerminalScreen::new);
    }

    @SubscribeEvent
    public static void registerOverlays(final RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, ArsNouveau.prefix("scry_camera"), cameraOverlay);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, ArsNouveau.prefix("tooltip"), GuiEntityInfoHUD.OVERLAY);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, ArsNouveau.prefix("mana_hud"), GuiManaHUD.OVERLAY);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, ArsNouveau.prefix("spell_hud"), GuiSpellHUD.OVERLAY);

    }


    // RegisterDimensionSpecialEffectsEvent was removed in NeoForge 21.11 - stubbed until replaced
    // TODO: re-implement jar dimension sky effects using new rendering API
    // @SubscribeEvent
    // public static void registerDimSpecialEffects(final RegisterDimensionSpecialEffectsEvent evt) {
    //     evt.register(ArsNouveau.prefix("jar"), new JarDimensionEffects());
    // }


    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {
        // TODO: MC 1.21.11 removed ItemProperties.register() — item model predicates (blocking,
        // uses, amount, source) must now be defined via JSON range_dispatch/conditional item models.
        // Migrate item model JSON files for: enchanters_shield, dowsing_rod, potion_jar, source_jar.
    }

    @SubscribeEvent
    public static void initBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof PotionJarTile jarTile
                        ? jarTile.getColor()
                        : -1, BlockRegistry.POTION_JAR.get());

        event.register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof PotionMelderTile melderTile
                        ? melderTile.getColor()
                        : -1, BlockRegistry.POTION_MELDER.get());

        event.register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof MageBlockTile mageBlockTile
                        ? mageBlockTile.color.getColor() : -1, BlockRegistry.MAGE_BLOCK.get());
    }

    // TODO: MC 1.21.11 — RegisterColorHandlersEvent.Item is gone; item tints are now data-driven
    // via ItemTintSource JSON. Re-implement as ItemTintSource codecs + item model JSON tint entries.
    // @SubscribeEvent
    // public static void initItemColors(final RegisterColorHandlersEvent.Item event) { ... }

    public static int colorFromArmor(ItemStack stack) {
        DyeColor color = stack.getOrDefault(DataComponents.BASE_COLOR, DyeColor.PURPLE);
        return ARGB.opaque(color.getTextColor());
    }

    public static int colorFromFlask(ItemStack stack) {
        PotionContents contents = PotionUtil.getContents(stack);
        var provider = PotionProviderRegistry.from(stack);
        if (provider != null) {
            return provider.usesRemaining(stack) <= 0 ? 0 : provider.getPotionData(stack).getColor();
        }
        return contents == PotionContents.EMPTY ? -1 : contents.getColor();
    }
}
