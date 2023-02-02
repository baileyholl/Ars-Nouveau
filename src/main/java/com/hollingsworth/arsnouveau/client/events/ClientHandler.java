package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.gui.GuiEntityInfoHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiManaHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiSpellHUD;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.entity.*;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.FamiliarBookwyrmRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.FamiliarCarbyRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.FamiliarWhirlisprigRenderer;
import com.hollingsworth.arsnouveau.client.renderer.entity.familiar.GenericFamiliarRenderer;
import com.hollingsworth.arsnouveau.client.renderer.item.ArmorRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.armor.HeavyArmor;
import com.hollingsworth.arsnouveau.common.armor.LightArmor;
import com.hollingsworth.arsnouveau.common.armor.MediumArmor;
import com.hollingsworth.arsnouveau.common.block.tile.MageBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import static com.hollingsworth.arsnouveau.client.events.ClientForgeHandler.localize;

@SuppressWarnings("unchecked")
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
        event.registerBlockEntityRenderer(BlockRegistry.T_LIGHT_TILE, LightRenderer::new);
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
        event.registerBlockEntityRenderer(BlockRegistry.ROTATING_TURRET_TILE.get(), RotatingTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ENCHANTED_SPELL_TURRET_TYPE, ReducerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.TIMER_SPELL_TURRET_TILE, TimerTurretRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCHWOOD_CHEST_TILE, ArchwoodChestRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RUNE_TILE, RuneRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.WHIRLISPRIG_TILE, WhirlisprigFlowerRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.ARCANE_CORE_TILE, ArcaneCoreRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.RELAY_COLLECTOR_TILE, (t) -> new GenericRenderer(t, "source_collector"));
        event.registerBlockEntityRenderer(BlockRegistry.SCRYERS_OCULUS_TILE, (t) -> new ScryerEyeRenderer(t, new ScryersEyeModel()));
        event.registerBlockEntityRenderer(BlockRegistry.ARMOR_TILE, AlterationTableRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MOB_JAR_TILE, MobJarRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MIRROR_WEAVE_TILE, MirrorweaveRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.GHOST_WEAVE_TILE, GhostweaveRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.FALSE_WEAVE_TILE, FalseweaveRenderer::new);
        event.registerBlockEntityRenderer(BlockRegistry.MINI_PEDESTAL_TILE.get(), ArcanePedestalRenderer::new);

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
        event.registerEntityRenderer(ModEntities.LINGER_SPELL.get(),
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENTITY_CASCADING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "cascading_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_BLAZING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "blazing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_FLOURISHING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "flourishing_weald"));
        event.registerEntityRenderer(ModEntities.ENTITY_VEXING_WEALD.get(), (v) -> new WealdWalkerRenderer<>(v, "vexing_weald"));

        event.registerEntityRenderer(ModEntities.AMETHYST_GOLEM.get(), AmethystGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.SCRYER_CAMERA.get(), renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
        event.registerEntityRenderer(ModEntities.ENCHANTED_FALLING_BLOCK.get(), EnchantedFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.ENCHANTED_MAGE_BLOCK.get(), MageBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.GIFT_STARBY.get(), GiftStarbyRenderer::new);
        event.registerEntityRenderer(ModEntities.ANIMATED_BLOCK.get(), AnimBlockRenderer::new);

        event.registerEntityRenderer(ModEntities.WALL_SPELL.get(),
                renderManager -> new RenderBlank(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
    }

    public static NamedGuiOverlay cameraOverlay = new NamedGuiOverlay(new ResourceLocation(ArsNouveau.MODID, "scry_camera"), (gui, pose, partialTick, width, height) -> {
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
                Component exit = localize("ars_nouveau.camera.exit", settings.keyShift.getTranslatedKeyMessage());
                font.drawShadow(pose, lookAround, 10, mc.getWindow().getGuiScaledHeight() - 40, 0xFFFFFF);
                font.drawShadow(pose, exit, 10, mc.getWindow().getGuiScaledHeight() - 30, 0xFFFFFF);
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
            ItemProperties.register(BlockRegistry.POTION_JAR.asItem(), new ResourceLocation(ArsNouveau.MODID, "amount"), (stack, level, entity, seed) -> {
                CompoundTag tag = stack.getTag();
                return tag != null ? (tag.getCompound("BlockEntityTag").getInt("currentFill") / 10000.0F) : 0.0F;
            });
            ItemProperties.register(BlockRegistry.SOURCE_JAR.asItem(), new ResourceLocation(ArsNouveau.MODID, "source"), (stack, level, entity, seed) -> {
                CompoundTag tag = stack.getTag();
                return tag != null ? (tag.getCompound("BlockEntityTag").getInt("source") / 10000.0F) : 0.0F;
            });
        });
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.AddLayers addLayers) {
        GeoArmorRenderer.registerArmorRenderer(LightArmor.class, () -> new ArmorRenderer(new GenericModel<>("light_armor", "items/light_armor").withEmptyAnim()));
        GeoArmorRenderer.registerArmorRenderer(MediumArmor.class, () -> new ArmorRenderer(new GenericModel<>("medium_armor", "items/medium_armor").withEmptyAnim()));
        GeoArmorRenderer.registerArmorRenderer(HeavyArmor.class, () -> new ArmorRenderer(new GenericModel<>("heavy_armor", "items/heavy_armor").withEmptyAnim()));

    }

    @SubscribeEvent
    public static void initBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof PotionJarTile jarTile
                        ? jarTile.getColor()
                        : -1, BlockRegistry.POTION_JAR);

        event.register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof PotionMelderTile melderTile
                        ? melderTile.getColor()
                        : -1, BlockRegistry.POTION_MELDER);

        event.register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof MageBlockTile mageBlockTile
                        ? mageBlockTile.color.getColor() : -1, BlockRegistry.MAGE_BLOCK);
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
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, LibBlockNames.POTION_MELDER_BLOCK)));

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.NOVICE_ROBES);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.NOVICE_BOOTS);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.NOVICE_HOOD);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.NOVICE_LEGGINGS);


        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.APPRENTICE_ROBES);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.APPRENTICE_BOOTS);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.APPRENTICE_HOOD);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.APPRENTICE_LEGGINGS);


        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCHMAGE_ROBES);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCHMAGE_BOOTS);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCHMAGE_HOOD);

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                ItemsRegistry.ARCHMAGE_LEGGINGS);


        event.getItemColors().register((stack, color) -> {
            if (color > 0 || !stack.hasTag()) {
                return -1;
            }
            CompoundTag blockTag = stack.getTag().getCompound("BlockEntityTag");
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
