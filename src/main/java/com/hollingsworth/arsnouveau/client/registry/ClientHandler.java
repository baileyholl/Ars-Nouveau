package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
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
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.NamedGuiOverlay;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

import org.jetbrains.annotations.Nullable;

import static com.hollingsworth.arsnouveau.client.events.ClientEvents.localize;


@SuppressWarnings("unchecked")
@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
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
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_DEPOSIT_TILE.get(), (t) -> new GenericRenderer(t, "source_deposit"));
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_WARP_TILE.get(), (t) -> new GenericRenderer(t, "source_warp"));
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_RELAY_TILE.get(), (t) -> new GenericRenderer(t, "source_relay"));
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_SPLITTER_TILE.get(), (t) -> new GenericRenderer(t, "source_splitter"));
        event.registerBlockEntityRenderer(BlockRegistry.BASIC_SPELL_TURRET_TILE.get(), BasicTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ROTATING_TURRET_TILE.get(), RotatingTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ENCHANTED_SPELL_TURRET_TYPE.get(), ReducerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.TIMER_SPELL_TURRET_TILE.get(), TimerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCHWOOD_CHEST_TILE.get(), ArchwoodChestRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RUNE_TILE.get(), RuneRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.WHIRLISPRIG_TILE.get(), WhirlisprigFlowerRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_CORE_TILE.get(), ArcaneCoreRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_COLLECTOR_TILE.get(), (t) -> new GenericRenderer(t, "source_collector"));
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

        event.registerEntityRenderer(ModEntities.SPELL_PROJ.get(),
                renderManager -> new RenderSpell(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.SPELL_PROJ_ARC.get(),
                renderManager -> new RenderSpell(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.SPELL_PROJ_HOM.get(),
                renderManager -> new RenderSpell(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));

        event.registerEntityRenderer(ModEntities.ENTITY_FOLLOW_PROJ.get(),
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
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
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_SPELL_ARROW.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_WIXIE_TYPE.get(), (t) -> new TextureVariantRenderer<>(t, new WixieModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_DUMMY.get(), DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_DRYGMY.get(), (t) -> new TextureVariantRenderer<>(t, new DrygmyModel<>()));
        event.registerEntityRenderer(ModEntities.ORBIT_SPELL.get(), renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.WILDEN_BOSS.get(), WildenChimeraRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_CHIMERA_SPIKE.get(), ChimeraProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get(), (t) -> new GenericFamiliarRenderer<>(t, new FamiliarStarbyModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_DRYGMY.get(), (t) -> new GenericFamiliarRenderer<>(t, new DrygmyModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_SYLPH.get(), FamiliarWhirlisprigRenderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_WIXIE.get(), (t) -> new GenericFamiliarRenderer<>(t, new WixieModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_BOOKWYRM_TYPE.get(), BookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.FAMILIAR_AMETHYST_GOLEM.get(), (t) -> new GenericFamiliarRenderer<>(t, new AmethystGolemModel<>()));
        event.registerEntityRenderer(ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get(), FamiliarBookwyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.LINGER_SPELL.get(),
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_CASCADING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "cascading_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_BLAZING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "blazing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_FLOURISHING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "flourishing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_VEXING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "vexing_weald"));

        event.registerEntityRenderer(ModEntities.AMETHYST_GOLEM.get(), AmethystGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.SCRYER_CAMERA.get(), renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENCHANTED_FALLING_BLOCK.get(), EnchantedFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ICE_SHARD.get(), EnchantedFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ENCHANTED_MAGE_BLOCK.get(), MageBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ENCHANTED_HEAD_BLOCK.get(), EnchantedSkullRenderer::new);
        event.registerEntityRenderer(ModEntities.GIFT_STARBY.get(), GiftStarbyRenderer::new);
        event.registerEntityRenderer(ModEntities.ANIMATED_BLOCK.get(), AnimBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ANIMATED_HEAD.get(), AnimSkullRenderer::new);
        event.registerEntityRenderer(ModEntities.CINDER.get(), CinderRenderer::new);
        event.registerEntityRenderer(ModEntities.WALL_SPELL.get(),
                renderManager -> new RenderBlank(renderManager, ArsNouveau.prefix( "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.LILY.get(), LilyRenderer::new);
    }

    public static NamedGuiOverlay cameraOverlay = new NamedGuiOverlay(ArsNouveau.prefix( "scry_camera"), (gui, pose, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        BlockPos pos = mc.cameraEntity.blockPosition();
        if (!CameraUtil.isPlayerMountedOnCamera(mc.player)) {
            return;
        }
        if (!mc.options.renderDebug) {
            BlockEntity var10 = level.getBlockEntity(pos);
            if (var10 instanceof ICameraMountable be) {
                Font font = Minecraft.getInstance().font;
                Options settings = Minecraft.getInstance().options;
                Component lookAround = localize("ars_nouveau.camera.move", settings.keyUp.getTranslatedKeyMessage(), settings.keyLeft.getTranslatedKeyMessage(), settings.keyDown.getTranslatedKeyMessage(), settings.keyRight.getTranslatedKeyMessage());
                Component exit = Component.translatable("ars_nouveau.camera.exit", settings.keyShift.getTranslatedKeyMessage().getString());
                pose.drawString(font, lookAround, 10, mc.getWindow().getGuiScaledHeight() - 40, 0xFFFFFF);
                pose.drawString(font, exit, 10, mc.getWindow().getGuiScaledHeight() - 30, 0xFFFFFF);
            }
        }
    });

    @SubscribeEvent
    public static void registerOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("scry_camera", cameraOverlay.overlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "tooltip", GuiEntityInfoHUD.OVERLAY);
        event.registerAboveAll("mana_hud", GuiManaHUD.OVERLAY);
        event.registerAboveAll("spell_hud", GuiSpellHUD.OVERLAY);

    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {

        evt.enqueueWork(() -> {
            ItemProperties.register(ItemsRegistry.ENCHANTERS_SHIELD.get(), ArsNouveau.prefix( "blocking"), (item, resourceLocation, livingEntity, arg4) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == item ? 1.0F : 0.0F;
            });
            ItemProperties.register(ItemsRegistry.DOWSING_ROD.get(), ArsNouveau.prefix( "uses"), new ClampedItemPropertyFunction() {
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
            ItemProperties.register(BlockRegistry.POTION_JAR.asItem(), ArsNouveau.prefix( "amount"), (stack, level, entity, seed) -> {
                CompoundTag tag = stack.getTag();
                return tag != null ? (tag.getCompound("BlockEntityTag").getInt("currentFill") / 10000.0F) : 0.0F;
            });
            ItemProperties.register(BlockRegistry.SOURCE_JAR.asItem(), ArsNouveau.prefix( "source"), (stack, level, entity, seed) -> {
                CompoundTag tag = stack.getTag();
                return tag != null ? (tag.getCompound("BlockEntityTag").getInt("source") / 10000.0F) : 0.0F;
            });
        });
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

    @SubscribeEvent
    public static void initItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, color) -> color > 0 ? -1 : colorFromFlask(stack),
                ItemsRegistry.POTION_FLASK);

        event.register((stack, color) -> color > 0 ? -1 : colorFromFlask(stack),
                ItemsRegistry.POTION_FLASK_EXTEND_TIME);

        event.register((stack, color) -> color > 0 ? -1 : colorFromFlask(stack),
                ItemsRegistry.POTION_FLASK_AMPLIFY);

        event.register((stack, color) -> color > 0 ? -1 :
                        new ParticleColor(200, 0, 200).getColor(),
                ForgeRegistries.ITEMS.getValue(ArsNouveau.prefix( LibBlockNames.POTION_MELDER_BLOCK)));

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.SORCERER_ROBES);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.SORCERER_BOOTS);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.SORCERER_HOOD);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.SORCERER_LEGGINGS);


        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCANIST_ROBES);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCANIST_BOOTS);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCANIST_HOOD);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCANIST_LEGGINGS);


        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.BATTLEMAGE_ROBES);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.BATTLEMAGE_BOOTS);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.BATTLEMAGE_HOOD);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.BATTLEMAGE_LEGGINGS);

        event.register((stack, color) -> {
            if (color == 1 && stack.getItem() instanceof ICasterTool i) {
                return i.getSpellCaster(stack).getColor().getColor();
            }
            return -1;

        }, ItemsRegistry.SPELL_PARCHMENT);

        event.register((stack, color) -> {
            if (color > 0 || !stack.hasTag()) {
                return -1;
            }
            CompoundTag blockTag = stack.getOrCreateTag().getCompound("BlockEntityTag");
            if (blockTag.contains("potionData")) {
                PotionData data = PotionData.fromTag(blockTag.getCompound("potionData"));
                return PotionUtils.getColor(data.fullEffects());
            }
            return -1;
        }, BlockRegistry.POTION_JAR);

    }

    public static int colorFromArmor(ItemStack stack) {
        IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
        if (!(holder instanceof ArmorPerkHolder armorPerkHolder))
            return DyeColor.PURPLE.getTextColor();
        return DyeColor.byName(armorPerkHolder.getColor(), DyeColor.PURPLE).getTextColor();
    }

    public static int colorFromFlask(ItemStack stack) {
        PotionFlask.FlaskData data = new PotionFlask.FlaskData(stack);
        return data.getPotion().getPotion() == Potions.EMPTY ? -1 : PotionUtils.getColor(data.getPotion().asPotionStack());
    }
}
