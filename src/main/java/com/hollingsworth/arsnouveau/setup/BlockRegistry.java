package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.LightBlock;
import com.hollingsworth.arsnouveau.common.block.*;
import com.hollingsworth.arsnouveau.common.block.tile.*;
import com.hollingsworth.arsnouveau.common.items.FluidBlockItem;
import com.hollingsworth.arsnouveau.common.items.MobJarItem;
import com.hollingsworth.arsnouveau.common.items.ModBlockItem;
import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.common.world.WorldgenRegistry;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTree;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.ITEMS;
import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.defaultItemProperties;

public class BlockRegistry {

    //TODO Switch to these for 1.20
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsNouveau.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArsNouveau.MODID);

    static final String BlockRegistryKey = "minecraft:block";
    static final String BlockEntityRegistryKey = "minecraft:block_entity_type";
    static final String prepend = ArsNouveau.MODID + ":";

    public static BlockBehaviour.Properties LOG_PROP = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD);
    public static BlockBehaviour.Properties SAP_PROP = BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY);

    public static RegistryWrapper<MageBlock> MAGE_BLOCK = registerBlockAndItem(LibBlockNames.MAGE_BLOCK, MageBlock::new);
    public static RegistryWrapper<BlockEntityType<MageBlockTile>> MAGE_BLOCK_TILE = registerTile(LibBlockNames.MAGE_BLOCK, MageBlockTile::new, MAGE_BLOCK);
    public static RegistryWrapper<LightBlock> LIGHT_BLOCK = registerBlockAndItem(LibBlockNames.LIGHT_BLOCK, LightBlock::new);
    public static RegistryWrapper<BlockEntityType<LightTile>> LIGHT_TILE = registerTile(LibBlockNames.LIGHT_BLOCK, LightTile::new, LIGHT_BLOCK);

    public static RegistryWrapper<TempLightBlock> T_LIGHT_BLOCK = registerBlockAndItem(LibBlockNames.T_LIGHT_BLOCK, TempLightBlock::new);
    public static RegistryWrapper<BlockEntityType<TempLightTile>> T_LIGHT_TILE = registerTile(LibBlockNames.T_LIGHT_BLOCK, TempLightTile::new, T_LIGHT_BLOCK);
    public static RegistryWrapper<AgronomicSourcelinkBlock> AGRONOMIC_SOURCELINK = registerBlockAndItem(LibBlockNames.AGRONOMIC_SOURCELINK, AgronomicSourcelinkBlock::new, (reg) ->  new RendererBlockItem(reg.get(), defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return AgronomicRenderer::getISTER;
        }
    });
    public static RegistryWrapper<BlockEntityType<AgronomicSourcelinkTile>> AGRONOMIC_SOURCELINK_TILE = registerTile(LibBlockNames.AGRONOMIC_SOURCELINK, AgronomicSourcelinkTile::new, AGRONOMIC_SOURCELINK);


    public static RegistryWrapper<EnchantingApparatusBlock> ENCHANTING_APP_BLOCK = registerBlockAndItem(LibBlockNames.ENCHANTING_APPARATUS, EnchantingApparatusBlock::new, (reg) -> new RendererBlockItem(reg.get(), defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("enchanting_apparatus");
        }
    });

    public static RegistryWrapper<BlockEntityType<EnchantingApparatusTile>> ENCHANTING_APP_TILE = registerTile(LibBlockNames.ENCHANTING_APPARATUS, EnchantingApparatusTile::new, ENCHANTING_APP_BLOCK);

    public static RegistryWrapper<SourceJar> SOURCE_JAR = registerBlockAndItem(LibBlockNames.SOURCE_JAR, SourceJar::new);
    public static RegistryWrapper<BlockEntityType<SourceJarTile>> SOURCE_JAR_TILE = registerTile(LibBlockNames.SOURCE_JAR, SourceJarTile::new, SOURCE_JAR);

    public static RegistryWrapper<Relay> RELAY = registerBlockAndItem(LibBlockNames.RELAY, Relay::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("source_relay");
        }
    });
    public static RegistryWrapper<BlockEntityType<RelayTile>> ARCANE_RELAY_TILE = registerTile(LibBlockNames.RELAY, RelayTile::new, RELAY);
    public static RegistryWrapper<MageBloomCrop> MAGE_BLOOM_CROP = registerBlockAndItem(LibBlockNames.MAGE_BLOOM, MageBloomCrop::new);
    public static RegistryWrapper<ScribesBlock> SCRIBES_BLOCK = registerBlockAndItem(LibBlockNames.SCRIBES_BLOCK, ScribesBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return ScribesRenderer::getISTER;
        }
    });
    public static RegistryWrapper<BlockEntityType<ScribesTile>> SCRIBES_TABLE_TILE = registerTile(LibBlockNames.SCRIBES_BLOCK, ScribesTile::new, SCRIBES_BLOCK);
    public static RegistryWrapper<RuneBlock> RUNE_BLOCK = registerBlockAndItem(LibBlockNames.RUNE, RuneBlock::new);
    public static RegistryWrapper<BlockEntityType<RuneTile>> RUNE_TILE = registerTile(LibBlockNames.RUNE, RuneTile::new, RUNE_BLOCK);
    public static RegistryWrapper<PortalBlock> PORTAL_BLOCK = registerBlockAndItem(LibBlockNames.PORTAL, PortalBlock::new);
    public static RegistryWrapper<BlockEntityType<PortalTile>> PORTAL_TILE_TYPE = registerTile(LibBlockNames.PORTAL, PortalTile::new, PORTAL_BLOCK);


    public static RegistryWrapper<ImbuementBlock> IMBUEMENT_BLOCK = registerBlockAndItem(LibBlockNames.IMBUEMENT_CHAMBER, ImbuementBlock::new, (reg) -> new RendererBlockItem(BlockRegistry.IMBUEMENT_BLOCK, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("imbuement_chamber");
        }
    });
    public static RegistryWrapper<BlockEntityType<ImbuementTile>> IMBUEMENT_TILE = registerTile(LibBlockNames.IMBUEMENT_CHAMBER, ImbuementTile::new, IMBUEMENT_BLOCK);
    public static RegistryWrapper<RelaySplitter> RELAY_SPLITTER = registerBlockAndItem(LibBlockNames.RELAY_SPLITTER, RelaySplitter::new, (reg) -> new RendererBlockItem(BlockRegistry.RELAY_SPLITTER, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("source_splitter");
        }
    });
    public static RegistryWrapper<BlockEntityType<RelaySplitterTile>> RELAY_SPLITTER_TILE = registerTile(LibBlockNames.RELAY_SPLITTER, RelaySplitterTile::new, RELAY_SPLITTER);
    public static RegistryWrapper<ArcaneCore> ARCANE_CORE_BLOCK = registerBlockAndItem(LibBlockNames.ARCANE_CORE, ArcaneCore::new, (reg) -> new RendererBlockItem(BlockRegistry.ARCANE_CORE_BLOCK, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return ArcaneCoreRenderer::getISTER;
        }
    });
    public static RegistryWrapper<BlockEntityType<ArcaneCoreTile>> ARCANE_CORE_TILE = registerTile(LibBlockNames.ARCANE_CORE, ArcaneCoreTile::new, ARCANE_CORE_BLOCK);
    public static RegistryWrapper<EnchantedSpellTurret> ENCHANTED_SPELL_TURRET = registerBlockAndItem(LibBlockNames.ENCHANTED_SPELL_TURRET, EnchantedSpellTurret::new, (reg) -> new RendererBlockItem(BlockRegistry.ENCHANTED_SPELL_TURRET, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return ReducerTurretRenderer::getISTER;
        }
    });
    public static RegistryWrapper<BlockEntityType<EnchantedTurretTile>> ENCHANTED_SPELL_TURRET_TYPE = registerTile(LibBlockNames.ENCHANTED_SPELL_TURRET, EnchantedTurretTile::new, ENCHANTED_SPELL_TURRET);
    public static RegistryWrapper<RedstoneAir> REDSTONE_AIR = registerBlock(LibBlockNames.REDSTONE_AIR, RedstoneAir::new);
    public static RegistryWrapper<IntangibleAirBlock> INTANGIBLE_AIR = registerBlock(LibBlockNames.INTANGIBLE_AIR, IntangibleAirBlock::new);
    public static RegistryWrapper<BlockEntityType<IntangibleAirTile>> INTANGIBLE_AIR_TYPE = registerTile(LibBlockNames.INTANGIBLE_AIR, IntangibleAirTile::new, INTANGIBLE_AIR);
    public static RegistryWrapper<VolcanicSourcelinkBlock> VOLCANIC_BLOCK = registerBlockAndItem(LibBlockNames.VOLCANIC_SOURCELINK, VolcanicSourcelinkBlock::new, (reg) -> new RendererBlockItem(BlockRegistry.VOLCANIC_BLOCK, defaultItemProperties().fireResistant()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return VolcanicRenderer::getISTER;
        }
    });
    public static RegistryWrapper<BlockEntityType<VolcanicSourcelinkTile>> VOLCANIC_TILE = registerTile(LibBlockNames.VOLCANIC_SOURCELINK, VolcanicSourcelinkTile::new, VOLCANIC_BLOCK);

    public static RegistryWrapper<LavaLily> LAVA_LILY = registerBlockAndItem(LibBlockNames.LAVA_LILY, LavaLily::new, (reg) ->new FluidBlockItem(reg.get(), defaultItemProperties().fireResistant()));

    public static RegistryWrapper<SourceBerryBush> SOURCEBERRY_BUSH = registerBlockAndItem(LibBlockNames.SOURCEBERRY_BUSH, () -> new SourceBerryBush(BlockBehaviour.Properties.of().randomTicks().noCollission().sound(SoundType.SWEET_BERRY_BUSH)), (reg) -> new BlockItem(reg.get(), defaultItemProperties().food(ItemsRegistry.SOURCE_BERRY_FOOD)));
    public static RegistryWrapper<WixieCauldron> WIXIE_CAULDRON = registerBlockAndItem(LibBlockNames.WIXIE_CAULDRON, WixieCauldron::new);
    public static RegistryWrapper<BlockEntityType<WixieCauldronTile>> WIXIE_CAULDRON_TYPE = registerTile(LibBlockNames.WIXIE_CAULDRON, WixieCauldronTile::new, WIXIE_CAULDRON);
    public static RegistryWrapper<CreativeSourceJar> CREATIVE_SOURCE_JAR = registerBlockAndItem(LibBlockNames.CREATIVE_SOURCE_JAR, CreativeSourceJar::new);
    public static RegistryWrapper<BlockEntityType<CreativeSourceJarTile>> CREATIVE_SOURCE_JAR_TILE = registerTile(LibBlockNames.CREATIVE_SOURCE_JAR, CreativeSourceJarTile::new, CREATIVE_SOURCE_JAR);
    public static RegistryWrapper<StrippableLog> CASCADING_LOG = registerBlockAndItem(LibBlockNames.CASCADING_LOG, () ->  new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_BLUE));
    public static RegistryWrapper<MagicLeaves> CASCADING_LEAVE = registerBlockAndItem(LibBlockNames.CASCADING_LEAVES, () -> createLeavesBlock(MapColor.COLOR_BLUE));
    public static RegistryWrapper<SaplingBlock> CASCADING_SAPLING = registerBlockAndItem(LibBlockNames.CASCADING_SAPLING, () -> new SaplingBlock(new MagicTree(WorldgenRegistry.CONFIGURED_CASCADING_TREE), SAP_PROP));
    public static RegistryWrapper<StrippableLog> CASCADING_WOOD = registerBlockAndItem(LibBlockNames.CASCADING_WOOD, () -> new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_BLUE));
    public static RegistryWrapper<StrippableLog> BLAZING_LOG = registerBlockAndItem(LibBlockNames.BLAZING_LOG, () -> new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_RED));
    public static RegistryWrapper<MagicLeaves> BLAZING_LEAVES = registerBlockAndItem(LibBlockNames.BLAZING_LEAVES, () -> createLeavesBlock(MapColor.COLOR_RED));
    @ObjectHolder(value = prepend + LibBlockNames.BLAZING_SAPLING, registryName = BlockRegistryKey)
    public static SaplingBlock BLAZING_SAPLING;
    @ObjectHolder(value = prepend + LibBlockNames.BLAZING_WOOD, registryName = BlockRegistryKey)
    public static StrippableLog BLAZING_WOOD;
    @ObjectHolder(value = prepend + LibBlockNames.VEXING_LOG, registryName = BlockRegistryKey)
    public static StrippableLog VEXING_LOG;
    @ObjectHolder(value = prepend + LibBlockNames.VEXING_LEAVES, registryName = BlockRegistryKey)
    public static MagicLeaves VEXING_LEAVES;
    @ObjectHolder(value = prepend + LibBlockNames.VEXING_SAPLING, registryName = BlockRegistryKey)
    public static SaplingBlock VEXING_SAPLING;
    @ObjectHolder(value = prepend + LibBlockNames.VEXING_WOOD, registryName = BlockRegistryKey)
    public static StrippableLog VEXING_WOOD;
    @ObjectHolder(value = prepend + LibBlockNames.FLOURISHING_LOG, registryName = BlockRegistryKey)
    public static StrippableLog FLOURISHING_LOG;
    @ObjectHolder(value = prepend + LibBlockNames.FLOURISHING_LEAVES, registryName = BlockRegistryKey)
    public static MagicLeaves FLOURISHING_LEAVES;
    @ObjectHolder(value = prepend + LibBlockNames.FLOURISHING_SAPLING, registryName = BlockRegistryKey)
    public static SaplingBlock FLOURISHING_SAPLING;
    @ObjectHolder(value = prepend + LibBlockNames.FLOURISHING_WOOD, registryName = BlockRegistryKey)
    public static StrippableLog FLOURISHING_WOOD;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_PLANK, registryName = BlockRegistryKey)
    public static ModBlock ARCHWOOD_PLANK;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_BUTTON, registryName = BlockRegistryKey)
    public static ButtonBlock ARCHWOOD_BUTTON;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_STAIRS, registryName = BlockRegistryKey)
    public static StairBlock ARCHWOOD_STAIRS;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_SLABS, registryName = BlockRegistryKey)
    public static SlabBlock ARCHWOOD_SLABS;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_FENCE_GATE, registryName = BlockRegistryKey)
    public static FenceGateBlock ARCHWOOD_FENCE_GATE;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_TRAPDOOR, registryName = BlockRegistryKey)
    public static TrapDoorBlock ARCHWOOD_TRAPDOOR;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_PRESSURE_PLATE, registryName = BlockRegistryKey)
    public static PressurePlateBlock ARCHWOOD_PPlate;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_FENCE, registryName = BlockRegistryKey)
    public static FenceBlock ARCHWOOD_FENCE;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_DOOR, registryName = BlockRegistryKey)
    public static DoorBlock ARCHWOOD_DOOR;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWLOG_BLUE, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWLOG_BLUE;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWWOOD_BLUE, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWWOOD_BLUE;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWLOG_GREEN, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWLOG_GREEN;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWWOOD_GREEN, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWWOOD_GREEN;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWLOG_RED, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWLOG_RED;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWWOOD_RED, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWWOOD_RED;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWLOG_PURPLE, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWLOG_PURPLE;
    @ObjectHolder(value = prepend + LibBlockNames.STRIPPED_AWWOOD_PURPLE, registryName = BlockRegistryKey)
    public static RotatedPillarBlock STRIPPED_AWWOOD_PURPLE;
    @ObjectHolder(value = prepend + LibBlockNames.SOURCE_GEM_BLOCK, registryName = BlockRegistryKey)
    public static ModBlock SOURCE_GEM_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.POTION_JAR_BLOCK, registryName = BlockRegistryKey)
    public static PotionJar POTION_JAR;
    @ObjectHolder(value = prepend + LibBlockNames.POTION_JAR_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<PotionJarTile> POTION_JAR_TYPE;
    @ObjectHolder(value = prepend + LibBlockNames.POTION_MELDER_BLOCK, registryName = BlockRegistryKey)
    public static PotionMelder POTION_MELDER;
    @ObjectHolder(value = prepend + LibBlockNames.POTION_MELDER_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<PotionMelderTile> POTION_MELDER_TYPE;
    @ObjectHolder(value = prepend + LibBlockNames.SCONCE, registryName = BlockRegistryKey)
    public static SconceBlock SCONCE_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.SCONCE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<SconceTile> SCONCE_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.DRYGMY_STONE, registryName = BlockRegistryKey)
    public static DrygmyStone DRYGMY_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.DRYGMY_STONE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<DrygmyTile> DRYGMY_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.ALCHEMICAL_SOURCELINK, registryName = BlockRegistryKey)
    public static AlchemicalSourcelinkBlock ALCHEMICAL_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.ALCHEMICAL_SOURCELINK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<AlchemicalSourcelinkTile> ALCHEMICAL_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.VITALIC_SOURCELINK, registryName = BlockRegistryKey)
    public static VitalicSourcelinkBlock VITALIC_BLOCK;

    @ObjectHolder(value = prepend + LibBlockNames.VITALIC_SOURCELINK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<VitalicSourcelinkTile> VITALIC_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.MYCELIAL_SOURCELINK, registryName = BlockRegistryKey)
    public static MycelialSourcelinkBlock MYCELIAL_BLOCK;

    @ObjectHolder(value = prepend + LibBlockNames.MYCELIAL_SOURCELINK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<MycelialSourcelinkTile> MYCELIAL_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.RELAY_DEPOSIT, registryName = BlockRegistryKey)
    public static RelayDepositBlock RELAY_DEPOSIT;

    @ObjectHolder(value = prepend + LibBlockNames.RELAY_DEPOSIT, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RelayDepositTile> RELAY_DEPOSIT_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.RELAY_WARP, registryName = BlockRegistryKey)
    public static RelayWarpBlock RELAY_WARP;

    @ObjectHolder(value = prepend + LibBlockNames.RELAY_WARP, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RelayWarpTile> RELAY_WARP_TILE;



    @ObjectHolder(value = prepend + LibBlockNames.BASIC_SPELL_TURRET, registryName = BlockRegistryKey)
    public static BasicSpellTurret BASIC_SPELL_TURRET;

    @ObjectHolder(value = prepend + LibBlockNames.BASIC_SPELL_TURRET, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<BasicSpellTurretTile> BASIC_SPELL_TURRET_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.TIMER_SPELL_TURRET, registryName = BlockRegistryKey)
    public static TimerSpellTurret TIMER_SPELL_TURRET;

    @ObjectHolder(value = prepend + LibBlockNames.TIMER_SPELL_TURRET, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<TimerSpellTurretTile> TIMER_SPELL_TURRET_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_CHEST, registryName = BlockRegistryKey)
    public static ArchwoodChest ARCHWOOD_CHEST;

    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_CHEST, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<ArchwoodChestTile> ARCHWOOD_CHEST_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.SPELL_PRISM, registryName = BlockRegistryKey)
    public static SpellPrismBlock SPELL_PRISM;

    @ObjectHolder(value = prepend + LibBlockNames.WHIRLISPRIG_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<WhirlisprigTile> WHIRLISPRIG_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.WHIRLISPRIG_BLOCK, registryName = BlockRegistryKey)
    public static WhirlisprigFlower WHIRLISPRIG_FLOWER;

    @ObjectHolder(value = prepend + LibBlockNames.RELAY_COLLECTOR, registryName = BlockRegistryKey)
    public static RelayCollectorBlock RELAY_COLLECTOR;

    @ObjectHolder(value = prepend + LibBlockNames.RELAY_COLLECTOR, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RelayCollectorTile> RELAY_COLLECTOR_TILE;

    public static RegistryWrapper<SummonBed> RED_SBED = registerBlockAndItem(LibBlockNames.RED_SBED, SummonBed::new);
    public static RegistryWrapper<SummonBed> BLUE_SBED = registerBlockAndItem(LibBlockNames.BLUE_SBED, SummonBed::new);
    public static RegistryWrapper<SummonBed> GREEN_SBED = registerBlockAndItem(LibBlockNames.GREEN_SBED, SummonBed::new);
    public static RegistryWrapper<SummonBed> ORANGE_SBED = registerBlockAndItem(LibBlockNames.ORANGE_SBED, SummonBed::new);
    public static RegistryWrapper<SummonBed> YELLOW_SBED = registerBlockAndItem(LibBlockNames.YELLOW_SBED, SummonBed::new);
    public static RegistryWrapper<SummonBed> PURPLE_SBED = registerBlockAndItem(LibBlockNames.PURPLE_SBED, SummonBed::new);

    @ObjectHolder(value = prepend + LibBlockNames.STATE_PROVIDER, registryName = "minecraft:worldgen/block_state_provider_type")
    public static BlockStateProviderType<?> stateProviderType;

    @ObjectHolder(value = prepend + LibBlockNames.SCRYERS_OCULUS, registryName = BlockRegistryKey)
    public static ScryersOculus SCRYERS_OCULUS;

    @ObjectHolder(value = prepend + LibBlockNames.SCRYERS_OCULUS, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<ScryersOculusTile> SCRYERS_OCULUS_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.SCRYERS_CRYSTAL, registryName = BlockRegistryKey)
    public static ScryerCrystal SCRYERS_CRYSTAL;

    @ObjectHolder(value = prepend + LibBlockNames.SCRYERS_CRYSTAL, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<ScryerCrystalTile> SCRYER_CRYSTAL_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.MENDOSTEEN_POD, registryName = BlockRegistryKey)
    public static ArchfruitPod MENDOSTEEN_POD;

    @ObjectHolder(value = prepend + LibBlockNames.BASTION_POD, registryName = BlockRegistryKey)
    public static ArchfruitPod BASTION_POD;

    @ObjectHolder(value = prepend + LibBlockNames.FROSTAYA_POD, registryName = BlockRegistryKey)
    public static ArchfruitPod FROSTAYA_POD;

    @ObjectHolder(value = prepend + LibBlockNames.BOMBEGRANATE_POD, registryName = BlockRegistryKey)
    public static ArchfruitPod BOMBEGRANTE_POD;

    @ObjectHolder(value = prepend + LibBlockNames.POTION_DIFFUSER, registryName = BlockRegistryKey)
    public static PotionDiffuserBlock POTION_DIFFUSER;

    @ObjectHolder(value = prepend + LibBlockNames.POTION_DIFFUSER, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<PotionDiffuserTile> POTION_DIFFUSER_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.ALTERATION_TABLE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<AlterationTile> ARMOR_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.ALTERATION_TABLE, registryName = BlockRegistryKey)
    public static AlterationTable ALTERATION_TABLE;

    @ObjectHolder(value = prepend + LibBlockNames.MOB_JAR, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<MobJarTile> MOB_JAR_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.MOB_JAR, registryName = BlockRegistryKey)
    public static MobJar MOB_JAR;

    @ObjectHolder(value = prepend + LibBlockNames.VOID_PRISM, registryName = BlockRegistryKey)
    public static VoidPrism VOID_PRISM;


    @ObjectHolder(value = prepend + LibBlockNames.REPOSITORY, registryName = BlockRegistryKey)
    public static RepositoryBlock REPOSITORY;

    @ObjectHolder(value = prepend + LibBlockNames.REPOSITORY, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RepositoryTile> REPOSITORY_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.FALSE_WEAVE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<FalseWeaveTile> FALSE_WEAVE_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.FALSE_WEAVE, registryName = BlockRegistryKey)
    public static FalseWeave FALSE_WEAVE;

    @ObjectHolder(value = prepend + LibBlockNames.MIRROR_WEAVE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<MirrorWeaveTile> MIRROR_WEAVE_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.MIRROR_WEAVE, registryName = BlockRegistryKey)
    public static MirrorWeave MIRROR_WEAVE;

    @ObjectHolder(value = prepend + LibBlockNames.GHOST_WEAVE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<GhostWeaveTile> GHOST_WEAVE_TILE;

    @ObjectHolder(value = prepend + LibBlockNames.GHOST_WEAVE, registryName = BlockRegistryKey)
    public static GhostWeave GHOST_WEAVE;

    @ObjectHolder(value = prepend + LibBlockNames.MAGEBLOOM_BLOCK, registryName = BlockRegistryKey)
    public static ModBlock MAGEBLOOM_BLOCK;

    public static void onBlocksRegistry(final IForgeRegistry<Block> registry) {
        registry.register(LibBlockNames.SPELL_PRISM, new SpellPrismBlock());


        registry.register(LibBlockNames.RELAY_DEPOSIT, new RelayDepositBlock());
        registry.register(LibBlockNames.RELAY_WARP, new RelayWarpBlock());
        registry.register(LibBlockNames.RELAY_COLLECTOR, new RelayCollectorBlock());
        registry.register(LibBlockNames.BASIC_SPELL_TURRET, new BasicSpellTurret());
        registry.register(LibBlockNames.TIMER_SPELL_TURRET, new TimerSpellTurret());


        registry.register(LibBlockNames.SCRYERS_OCULUS, new ScryersOculus());
        registry.register(LibBlockNames.SCRYERS_CRYSTAL, new ScryerCrystal());


        registry.register(LibBlockNames.BLAZING_SAPLING, new SaplingBlock(new MagicTree(WorldgenRegistry.CONFIGURED_BLAZING_TREE), SAP_PROP));
        registry.register(LibBlockNames.VEXING_SAPLING, new SaplingBlock(new MagicTree(WorldgenRegistry.CONFIGURED_VEXING_TREE), SAP_PROP));
        registry.register(LibBlockNames.FLOURISHING_SAPLING, new SaplingBlock(new MagicTree(WorldgenRegistry.CONFIGURED_FLOURISHING_TREE), SAP_PROP));

        registry.register(LibBlockNames.FLOURISHING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_GREEN));
        registry.register(LibBlockNames.FLOURISHING_LEAVES, createLeavesBlock(MapColor.COLOR_LIGHT_GREEN));
        registry.register(LibBlockNames.VEXING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_PURPLE));
        registry.register(LibBlockNames.VEXING_LEAVES, createLeavesBlock(MapColor.COLOR_PURPLE));

        registry.register(LibBlockNames.VEXING_WOOD, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_PURPLE));
        registry.register(LibBlockNames.FLOURISHING_WOOD, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_GREEN));
        registry.register(LibBlockNames.BLAZING_WOOD, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_RED));
        registry.register(LibBlockNames.ARCHWOOD_PLANK, new ModBlock(LOG_PROP));
        registry.register(LibBlockNames.ARCHWOOD_BUTTON, new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), BlockSetType.OAK, 30, true));
        registry.register(LibBlockNames.ARCHWOOD_STAIRS, new StairBlock(() -> ARCHWOOD_PLANK.defaultBlockState(), woodProp));
        registry.register(LibBlockNames.ARCHWOOD_SLABS, new SlabBlock(woodProp));
        registry.register(LibBlockNames.ARCHWOOD_FENCE_GATE, new FenceGateBlock(woodProp, WoodType.OAK));
        registry.register(LibBlockNames.ARCHWOOD_FENCE, new FenceBlock(woodProp));
        registry.register(LibBlockNames.ARCHWOOD_DOOR, new DoorBlock(woodProp, BlockSetType.OAK));
        registry.register(LibBlockNames.ARCHWOOD_PRESSURE_PLATE, new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, woodProp, BlockSetType.OAK));
        registry.register(LibBlockNames.ARCHWOOD_TRAPDOOR, new TrapDoorBlock(woodProp, BlockSetType.OAK));
        registry.register(LibBlockNames.ARCHWOOD_CHEST, new ArchwoodChest());

        registry.register(LibBlockNames.STRIPPED_AWLOG_BLUE, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_BLUE, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.STRIPPED_AWLOG_GREEN, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_GREEN, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.STRIPPED_AWLOG_RED, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_RED, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.STRIPPED_AWLOG_PURPLE, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_PURPLE, new RotatedPillarBlock(LOG_PROP));
        registry.register(LibBlockNames.SOURCE_GEM_BLOCK, new ModBlock(ModBlock.defaultProperties().noOcclusion().lightLevel(s -> 6)));
        registry.register(LibBlockNames.POTION_JAR_BLOCK, new PotionJar(ModBlock.defaultProperties().noOcclusion()));
        registry.register(LibBlockNames.POTION_MELDER_BLOCK, new PotionMelder(ModBlock.defaultProperties().noOcclusion()));


        registry.register(LibBlockNames.ALCHEMICAL_SOURCELINK, new AlchemicalSourcelinkBlock());
        registry.register(LibBlockNames.VITALIC_SOURCELINK, new VitalicSourcelinkBlock());
        registry.register(LibBlockNames.MYCELIAL_SOURCELINK, new MycelialSourcelinkBlock());


        registry.register(LibBlockNames.WHIRLISPRIG_BLOCK, new WhirlisprigFlower());
        registry.register(LibBlockNames.SCONCE, new SconceBlock());
        registry.register(LibBlockNames.DRYGMY_STONE, new DrygmyStone());


        registry.register(LibBlockNames.MENDOSTEEN_POD, new ArchfruitPod(() -> FLOURISHING_LOG));
        registry.register(LibBlockNames.BASTION_POD, new ArchfruitPod(() -> VEXING_LOG));
        registry.register(LibBlockNames.FROSTAYA_POD, new ArchfruitPod(() -> CASCADING_LOG.get()));
        registry.register(LibBlockNames.BOMBEGRANATE_POD, new ArchfruitPod(() -> BLAZING_LOG.get()));
        registry.register(LibBlockNames.POTION_DIFFUSER, new PotionDiffuserBlock());
        for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
            if (LibBlockNames.DIRECTIONAL_SOURCESTONE.contains(s)) {
                registry.register(s, new DirectionalModBlock());
            } else {
                registry.register(s, new ModBlock());
            }
        }
        for(String s : LibBlockNames.DECORATIVE_SLABS){
            registry.register(s, new SlabBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).sound(SoundType.STONE)));
        }
        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
            registry.register(s + "_stairs", new StairBlock(() -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s)).defaultBlockState(), BlockBehaviour.Properties.of().strength(1.5F, 6.0F).sound(SoundType.STONE)));
        }

        registry.register(LibBlockNames.ALTERATION_TABLE, new AlterationTable());
        registry.register(LibBlockNames.MOB_JAR, new MobJar());
        registry.register(LibBlockNames.VOID_PRISM, new VoidPrism());

        registry.register(LibBlockNames.REPOSITORY, new RepositoryBlock());
        registry.register(LibBlockNames.MIRROR_WEAVE, new MirrorWeave(Block.Properties.of().strength(0.1F).sound(SoundType.WOOL).noOcclusion()));
        registry.register(LibBlockNames.GHOST_WEAVE, new GhostWeave(Block.Properties.of().strength(0.1F).sound(SoundType.WOOL).noOcclusion()));
        registry.register(LibBlockNames.FALSE_WEAVE, new FalseWeave(Block.Properties.of().strength(0.1F).sound(SoundType.WOOL).noOcclusion().noCollission()));
        registry.register(LibBlockNames.MAGEBLOOM_BLOCK, new ModBlock(BlockBehaviour.Properties.of().strength(0.1F).sound(SoundType.WOOL)));

        registry.register(LibBlockNames.Pot(LibBlockNames.MAGE_BLOOM), createPottedBlock(() -> MAGE_BLOOM_CROP.get()));
        registry.register(LibBlockNames.Pot(LibBlockNames.BLAZING_SAPLING), createPottedBlock(() -> BLAZING_SAPLING));
        registry.register(LibBlockNames.Pot(LibBlockNames.CASCADING_SAPLING), createPottedBlock(() -> CASCADING_SAPLING.get()));
        registry.register(LibBlockNames.Pot(LibBlockNames.FLOURISHING_SAPLING), createPottedBlock(() -> FLOURISHING_SAPLING));
        registry.register(LibBlockNames.Pot(LibBlockNames.VEXING_SAPLING), createPottedBlock(() -> VEXING_SAPLING));

    }

    public static MagicLeaves createLeavesBlock(MapColor color) {
        return new MagicLeaves(BlockBehaviour.Properties.of().mapColor(color).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(
                BlockRegistry::allowsSpawnOnLeaves).isSuffocating(BlockRegistry::isntSolid).isViewBlocking(BlockRegistry::isntSolid).pushReaction(PushReaction.DESTROY).ignitedByLava());
    }

    @SuppressWarnings("ConstantConditions")
    public static void onTileEntityRegistry(IForgeRegistry<BlockEntityType<?>> registry) {
        registry.register(LibBlockNames.POTION_JAR_BLOCK, BlockEntityType.Builder.of(PotionJarTile::new, BlockRegistry.POTION_JAR).build(null));
        registry.register(LibBlockNames.POTION_MELDER_BLOCK, BlockEntityType.Builder.of(PotionMelderTile::new, BlockRegistry.POTION_MELDER).build(null));
        registry.register(LibBlockNames.SCONCE, BlockEntityType.Builder.of(SconceTile::new, BlockRegistry.SCONCE_BLOCK).build(null));
        registry.register(LibBlockNames.DRYGMY_STONE, BlockEntityType.Builder.of(DrygmyTile::new, BlockRegistry.DRYGMY_BLOCK).build(null));
        registry.register(LibBlockNames.ALCHEMICAL_SOURCELINK, BlockEntityType.Builder.of(AlchemicalSourcelinkTile::new, BlockRegistry.ALCHEMICAL_BLOCK).build(null));
        registry.register(LibBlockNames.VITALIC_SOURCELINK, BlockEntityType.Builder.of(VitalicSourcelinkTile::new, BlockRegistry.VITALIC_BLOCK).build(null));
        registry.register(LibBlockNames.MYCELIAL_SOURCELINK, BlockEntityType.Builder.of(MycelialSourcelinkTile::new, BlockRegistry.MYCELIAL_BLOCK).build(null));
        registry.register(LibBlockNames.RELAY_DEPOSIT, BlockEntityType.Builder.of(RelayDepositTile::new, BlockRegistry.RELAY_DEPOSIT).build(null));
        registry.register(LibBlockNames.RELAY_WARP, BlockEntityType.Builder.of(RelayWarpTile::new, BlockRegistry.RELAY_WARP).build(null));
        registry.register(LibBlockNames.BASIC_SPELL_TURRET, BlockEntityType.Builder.of(BasicSpellTurretTile::new, BlockRegistry.BASIC_SPELL_TURRET).build(null));
        registry.register(LibBlockNames.TIMER_SPELL_TURRET, BlockEntityType.Builder.of(TimerSpellTurretTile::new, BlockRegistry.TIMER_SPELL_TURRET).build(null));
        registry.register(LibBlockNames.ARCHWOOD_CHEST, BlockEntityType.Builder.of(ArchwoodChestTile::new, BlockRegistry.ARCHWOOD_CHEST).build(null));
        registry.register(LibBlockNames.WHIRLISPRIG_BLOCK, BlockEntityType.Builder.of(WhirlisprigTile::new, BlockRegistry.WHIRLISPRIG_FLOWER).build(null));
        registry.register(LibBlockNames.RELAY_COLLECTOR, BlockEntityType.Builder.of(RelayCollectorTile::new, BlockRegistry.RELAY_COLLECTOR).build(null));
        registry.register(LibBlockNames.SCRYERS_OCULUS, BlockEntityType.Builder.of(ScryersOculusTile::new, BlockRegistry.SCRYERS_OCULUS).build(null));
        registry.register(LibBlockNames.SCRYERS_CRYSTAL, BlockEntityType.Builder.of(ScryerCrystalTile::new, BlockRegistry.SCRYERS_CRYSTAL).build(null));
        registry.register(LibBlockNames.POTION_DIFFUSER, BlockEntityType.Builder.of(PotionDiffuserTile::new, BlockRegistry.POTION_DIFFUSER).build(null));
        registry.register(LibBlockNames.ALTERATION_TABLE, BlockEntityType.Builder.of(AlterationTile::new, BlockRegistry.ALTERATION_TABLE).build(null));
        registry.register(LibBlockNames.MOB_JAR, BlockEntityType.Builder.of(MobJarTile::new, BlockRegistry.MOB_JAR).build(null));
        registry.register(LibBlockNames.REPOSITORY, BlockEntityType.Builder.of(RepositoryTile::new, BlockRegistry.REPOSITORY).build(null));
        registry.register(LibBlockNames.FALSE_WEAVE, BlockEntityType.Builder.of(FalseWeaveTile::new, BlockRegistry.FALSE_WEAVE).build(null));
        registry.register(LibBlockNames.MIRROR_WEAVE, BlockEntityType.Builder.of(MirrorWeaveTile::new, BlockRegistry.MIRROR_WEAVE).build(null));
        registry.register(LibBlockNames.GHOST_WEAVE, BlockEntityType.Builder.of(GhostWeaveTile::new, BlockRegistry.GHOST_WEAVE).build(null));

    }

    public static void onBlockItemsRegistry(IForgeRegistry<Item> registry) {
        registry.register(LibBlockNames.RELAY_WARP, new RendererBlockItem(BlockRegistry.RELAY_WARP, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("source_warp");
            }
        });
        registry.register(LibBlockNames.RELAY_DEPOSIT, new RendererBlockItem(BlockRegistry.RELAY_DEPOSIT, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("source_deposit");
            }
        });
        registry.register(LibBlockNames.VEXING_LEAVES, getDefaultBlockItem(BlockRegistry.VEXING_LEAVES));
        registry.register(LibBlockNames.VEXING_LOG, getDefaultBlockItem(BlockRegistry.VEXING_LOG));
        registry.register(LibBlockNames.VEXING_SAPLING, getDefaultBlockItem(BlockRegistry.VEXING_SAPLING));
        registry.register(LibBlockNames.VEXING_WOOD, getDefaultBlockItem(BlockRegistry.VEXING_WOOD));
        registry.register(LibBlockNames.FLOURISHING_LEAVES, getDefaultBlockItem(BlockRegistry.FLOURISHING_LEAVES));
        registry.register(LibBlockNames.FLOURISHING_LOG, getDefaultBlockItem(BlockRegistry.FLOURISHING_LOG));
        registry.register(LibBlockNames.FLOURISHING_SAPLING, getDefaultBlockItem(BlockRegistry.FLOURISHING_SAPLING));
        registry.register(LibBlockNames.FLOURISHING_WOOD, getDefaultBlockItem(BlockRegistry.FLOURISHING_WOOD));
        registry.register(LibBlockNames.BLAZING_SAPLING, getDefaultBlockItem(BlockRegistry.BLAZING_SAPLING));
        registry.register(LibBlockNames.BLAZING_WOOD, getDefaultBlockItem(BlockRegistry.BLAZING_WOOD));
        registry.register(LibBlockNames.ARCHWOOD_PLANK, getDefaultBlockItem(BlockRegistry.ARCHWOOD_PLANK));

        registry.register(LibBlockNames.ARCHWOOD_BUTTON, getDefaultBlockItem(BlockRegistry.ARCHWOOD_BUTTON));
        registry.register(LibBlockNames.ARCHWOOD_STAIRS, getDefaultBlockItem(BlockRegistry.ARCHWOOD_STAIRS));
        registry.register(LibBlockNames.ARCHWOOD_SLABS, getDefaultBlockItem(BlockRegistry.ARCHWOOD_SLABS));
        registry.register(LibBlockNames.ARCHWOOD_FENCE_GATE, getDefaultBlockItem(BlockRegistry.ARCHWOOD_FENCE_GATE));
        registry.register(LibBlockNames.ARCHWOOD_TRAPDOOR, getDefaultBlockItem(BlockRegistry.ARCHWOOD_TRAPDOOR));
        registry.register(LibBlockNames.ARCHWOOD_PRESSURE_PLATE, getDefaultBlockItem(BlockRegistry.ARCHWOOD_PPlate));
        registry.register(LibBlockNames.ARCHWOOD_FENCE, getDefaultBlockItem(BlockRegistry.ARCHWOOD_FENCE));
        registry.register(LibBlockNames.ARCHWOOD_DOOR, getDefaultBlockItem(BlockRegistry.ARCHWOOD_DOOR));

        registry.register(LibBlockNames.STRIPPED_AWLOG_BLUE, getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_BLUE));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_BLUE, getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_BLUE));
        registry.register(LibBlockNames.STRIPPED_AWLOG_GREEN, getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_GREEN));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_GREEN, getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_GREEN));
        registry.register(LibBlockNames.STRIPPED_AWLOG_RED, getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_RED));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_RED, getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_RED));
        registry.register(LibBlockNames.STRIPPED_AWLOG_PURPLE, getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_PURPLE));
        registry.register(LibBlockNames.STRIPPED_AWWOOD_PURPLE, getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_PURPLE));

        registry.register(LibBlockNames.SOURCE_GEM_BLOCK, getDefaultBlockItem(BlockRegistry.SOURCE_GEM_BLOCK));

        registry.register(LibBlockNames.POTION_JAR_BLOCK, getDefaultBlockItem(BlockRegistry.POTION_JAR));
        registry.register(LibBlockNames.POTION_MELDER_BLOCK, getDefaultBlockItem(BlockRegistry.POTION_MELDER));

        registry.register(LibBlockNames.SCONCE, getDefaultBlockItem(BlockRegistry.SCONCE_BLOCK));
        registry.register(LibBlockNames.DRYGMY_STONE, getDefaultBlockItem(BlockRegistry.DRYGMY_BLOCK));
        registry.register(LibBlockNames.ALCHEMICAL_SOURCELINK, new RendererBlockItem(BlockRegistry.ALCHEMICAL_BLOCK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return AlchemicalRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.VITALIC_SOURCELINK, new RendererBlockItem(BlockRegistry.VITALIC_BLOCK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return VitalicRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.MYCELIAL_SOURCELINK, new RendererBlockItem(BlockRegistry.MYCELIAL_BLOCK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return MycelialRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.TIMER_SPELL_TURRET, new RendererBlockItem(BlockRegistry.TIMER_SPELL_TURRET, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return TimerTurretRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.BASIC_SPELL_TURRET, new RendererBlockItem(BlockRegistry.BASIC_SPELL_TURRET, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return BasicTurretRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.ARCHWOOD_CHEST, new ArchwoodChest.Item(BlockRegistry.ARCHWOOD_CHEST, defaultItemProperties()));
        registry.register(LibBlockNames.SPELL_PRISM, getDefaultBlockItem(BlockRegistry.SPELL_PRISM));
        registry.register(LibBlockNames.WHIRLISPRIG_BLOCK, new RendererBlockItem(BlockRegistry.WHIRLISPRIG_FLOWER, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return WhirlisprigFlowerRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.RELAY_COLLECTOR, new RendererBlockItem(BlockRegistry.RELAY_COLLECTOR, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("source_collector");
            }
        });
        registry.register(LibBlockNames.SCRYERS_CRYSTAL, getDefaultBlockItem(BlockRegistry.SCRYERS_CRYSTAL));
        registry.register(LibBlockNames.SCRYERS_OCULUS, new RendererBlockItem(BlockRegistry.SCRYERS_OCULUS, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ScryerOculusRenderer::getISTER;
            }
        }.withTooltip(Component.translatable("ars_nouveau.tooltip.scryers_oculus").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE))));

        registry.register(LibBlockNames.POTION_DIFFUSER, getDefaultBlockItem(BlockRegistry.POTION_DIFFUSER));
        registry.register(LibBlockNames.MENDOSTEEN_POD, new ItemNameBlockItem(BlockRegistry.MENDOSTEEN_POD, defaultItemProperties().food(ItemsRegistry.MENDOSTEEN_FOOD)));
        registry.register(LibBlockNames.BASTION_POD, new ItemNameBlockItem(BlockRegistry.BASTION_POD, defaultItemProperties().food(ItemsRegistry.BASTION_FOOD)));
        registry.register(LibBlockNames.BOMBEGRANATE_POD, new ItemNameBlockItem(BlockRegistry.BOMBEGRANTE_POD, defaultItemProperties().food(ItemsRegistry.BLASTING_FOOD)));
        registry.register(LibBlockNames.FROSTAYA_POD, new ItemNameBlockItem(BlockRegistry.FROSTAYA_POD, defaultItemProperties().food(ItemsRegistry.FROSTAYA_FOOD)));

        for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
            registry.register(s, getDefaultBlockItem(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s))));
        }

        for (String s : LibBlockNames.DECORATIVE_STAIRS) {
            registry.register(s, getDefaultBlockItem(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s))));
        }

        for (String s : LibBlockNames.DECORATIVE_SLABS) {
            registry.register(s, getDefaultBlockItem(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s))));
        }
        registry.register(LibBlockNames.ALTERATION_TABLE, new RendererBlockItem(BlockRegistry.ALTERATION_TABLE, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return AlterationTableRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.MOB_JAR, new MobJarItem(BlockRegistry.MOB_JAR, defaultItemProperties()));
        registry.register(LibBlockNames.VOID_PRISM, getDefaultBlockItem(BlockRegistry.VOID_PRISM));

        registry.register(LibBlockNames.REPOSITORY, new RendererBlockItem(BlockRegistry.REPOSITORY, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return RepositoryRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.GHOST_WEAVE, getDefaultBlockItem(BlockRegistry.GHOST_WEAVE));
        registry.register(LibBlockNames.FALSE_WEAVE, getDefaultBlockItem(BlockRegistry.FALSE_WEAVE));
        registry.register(LibBlockNames.MIRROR_WEAVE, getDefaultBlockItem(BlockRegistry.MIRROR_WEAVE));
        registry.register(LibBlockNames.MAGEBLOOM_BLOCK, getDefaultBlockItem(BlockRegistry.MAGEBLOOM_BLOCK));

    }

    public static ModBlockItem getDefaultBlockItem(Block block) {
        return new ModBlockItem(block, defaultItemProperties());
    }

    public static void registerBlockProvider(IForgeRegistry<BlockStateProviderType<?>> registry) {
        registry.register(new ResourceLocation(ArsNouveau.MODID, LibBlockNames.STATE_PROVIDER), new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC));
    }

    private static Boolean allowsSpawnOnLeaves(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

    static Block.Properties woodProp = BlockBehaviour.Properties.of().strength(2.0F, 3.0F).ignitedByLava().mapColor(MapColor.WOOD).sound(SoundType.WOOD);


    public static Block getBlock(String s) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s));
    }

    //Somebody need to start it
    public static final RegistryWrapper<Block> ROTATING_TURRET;
    public static final RegistryObject<BlockEntityType<RotatingTurretTile>> ROTATING_TURRET_TILE;
    public static final RegistryWrapper<ArcanePlatform> ARCANE_PLATFORM;
    public static final RegistryWrapper<MagelightTorch> MAGELIGHT_TORCH;
    public static final RegistryWrapper<BrazierRelay> BRAZIER_RELAY;

    public static final RegistryWrapper<CraftingLecternBlock> CRAFTING_LECTERN;
    public static RegistryObject<BlockEntityType<ArcanePedestalTile>> ARCANE_PEDESTAL_TILE;
    public static RegistryObject<BlockEntityType<MagelightTorchTile>> MAGELIGHT_TORCH_TILE;

    public static RegistryWrapper<ArcanePedestal> ARCANE_PEDESTAL;

    public static RegistryWrapper<RitualBrazierBlock> RITUAL_BLOCK;
    public static RegistryWrapper<SkyWeave> SKY_WEAVE;
    public static RegistryWrapper<TemporaryBlock> TEMPORARY_BLOCK;
    public static RegistryWrapper<ItemDetector> ITEM_DETECTOR;

    public static RegistryObject<BlockEntityType<RitualBrazierTile>> RITUAL_TILE;
    public static RegistryObject<BlockEntityType<BrazierRelayTile>> BRAZIER_RELAY_TILE;
    public static RegistryObject<BlockEntityType<SkyBlockTile>> SKYWEAVE_TILE;
    public static RegistryObject<BlockEntityType<TemporaryTile>> TEMPORARY_TILE;
    public static RegistryObject<BlockEntityType<CraftingLecternTile>> CRAFTING_LECTERN_TILE;
    public static RegistryObject<BlockEntityType<ItemDetectorTile>> ITEM_DETECTOR_TILE;
    static {
        ROTATING_TURRET = registerBlock(LibBlockNames.ROTATING_SPELL_TURRET, RotatingSpellTurret::new);
        ARCANE_PLATFORM = registerBlock(LibBlockNames.MINI_PEDESTAL, ArcanePlatform::new);
        MAGELIGHT_TORCH = registerBlock(LibBlockNames.MAGELIGHT_TORCH, MagelightTorch::new);
        ARCANE_PEDESTAL = registerBlock(LibBlockNames.ARCANE_PEDESTAL, ArcanePedestal::new);
        BRAZIER_RELAY = registerBlock(LibBlockNames.BRAZIER_RELAY, BrazierRelay::new);
        RITUAL_BLOCK = registerBlock(LibBlockNames.RITUAL_BRAZIER, RitualBrazierBlock::new);
        SKY_WEAVE = registerBlock(LibBlockNames.SKY_WEAVE, () -> new SkyWeave(Block.Properties.of().strength(0.1F).sound(SoundType.WOOL).noOcclusion()));
        TEMPORARY_BLOCK = registerBlock(LibBlockNames.TEMPORARY_BLOCK, () -> new TemporaryBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).sound(SoundType.STONE)));
        CRAFTING_LECTERN = registerBlock(LibBlockNames.STORAGE_LECTERN, CraftingLecternBlock::new);
        ITEM_DETECTOR = registerBlock(LibBlockNames.ITEM_DETECTOR, ItemDetector::new);
        ITEMS.register(LibBlockNames.ROTATING_SPELL_TURRET, () -> new RendererBlockItem(ROTATING_TURRET.get(), defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return BasicTurretRenderer::getISTER;
            }
        }.withTooltip(Component.translatable("ars_nouveau.turret.tooltip")));

        ITEMS.register(LibBlockNames.ARCANE_PEDESTAL, () -> getDefaultBlockItem(ARCANE_PEDESTAL.get()));

        ITEMS.register(LibBlockNames.MINI_PEDESTAL, () -> new ModBlockItem(BlockRegistry.ARCANE_PLATFORM.get(), defaultItemProperties()).withTooltip(Component.translatable("ars_nouveau.arcane_platform.tooltip")));
        ITEMS.register(LibBlockNames.MAGELIGHT_TORCH, () -> getDefaultBlockItem(BlockRegistry.MAGELIGHT_TORCH.get()));
        ITEMS.register(LibBlockNames.BRAZIER_RELAY, () -> getDefaultBlockItem(BlockRegistry.BRAZIER_RELAY.get()));
        ITEMS.register(LibBlockNames.RITUAL_BRAZIER, () -> getDefaultBlockItem(BlockRegistry.RITUAL_BLOCK.get()));
        ITEMS.register(LibBlockNames.SKY_WEAVE, () -> getDefaultBlockItem(BlockRegistry.SKY_WEAVE.get()));
        ITEMS.register(LibBlockNames.TEMPORARY_BLOCK, () -> new ModBlockItem(BlockRegistry.TEMPORARY_BLOCK.get(), new Item.Properties()));
        ITEMS.register(LibBlockNames.STORAGE_LECTERN, () -> new RendererBlockItem((BlockRegistry.CRAFTING_LECTERN.get()), defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return LecternRenderer::getISTER;
            }
        });
        ITEMS.register(LibBlockNames.ITEM_DETECTOR, () -> getDefaultBlockItem(BlockRegistry.ITEM_DETECTOR.get()));

        ROTATING_TURRET_TILE = BLOCK_ENTITIES.register(LibBlockNames.ROTATING_SPELL_TURRET, () -> BlockEntityType.Builder.of(RotatingTurretTile::new, ROTATING_TURRET.get()).build(null));
        ARCANE_PEDESTAL_TILE = BLOCK_ENTITIES.register(LibBlockNames.ARCANE_PEDESTAL, () -> BlockEntityType.Builder.of(ArcanePedestalTile::new, ARCANE_PEDESTAL.get(), ARCANE_PLATFORM.get()).build(null));
        MAGELIGHT_TORCH_TILE = BLOCK_ENTITIES.register(LibBlockNames.MAGELIGHT_TORCH, () -> BlockEntityType.Builder.of(MagelightTorchTile::new, MAGELIGHT_TORCH.get()).build(null));
        BRAZIER_RELAY_TILE = BLOCK_ENTITIES.register(LibBlockNames.BRAZIER_RELAY, () -> BlockEntityType.Builder.of(BrazierRelayTile::new, BRAZIER_RELAY.get()).build(null));
        RITUAL_TILE = BLOCK_ENTITIES.register(LibBlockNames.RITUAL_BRAZIER, () -> BlockEntityType.Builder.of(RitualBrazierTile::new, RITUAL_BLOCK.get()).build(null));
        SKYWEAVE_TILE = BLOCK_ENTITIES.register(LibBlockNames.SKY_WEAVE, () -> BlockEntityType.Builder.of(SkyBlockTile::new, SKY_WEAVE.get()).build(null));
        TEMPORARY_TILE = BLOCK_ENTITIES.register(LibBlockNames.TEMPORARY_BLOCK, () -> BlockEntityType.Builder.of(TemporaryTile::new, TEMPORARY_BLOCK.get()).build(null));
        CRAFTING_LECTERN_TILE = BLOCK_ENTITIES.register(LibBlockNames.STORAGE_LECTERN, () -> BlockEntityType.Builder.of(CraftingLecternTile::new, CRAFTING_LECTERN.get()).build(null));
        ITEM_DETECTOR_TILE = BLOCK_ENTITIES.register(LibBlockNames.ITEM_DETECTOR, () -> BlockEntityType.Builder.of(ItemDetectorTile::new, ITEM_DETECTOR.get()).build(null));
    }

    public static RegistryWrapper registerBlock(String name, Supplier<Block> blockSupp) {
        return new RegistryWrapper<>(BLOCKS.register(name, blockSupp));
    }

    public static RegistryWrapper registerBlockAndItem(String name, Supplier<Block> blockSupp) {
        RegistryWrapper<Block> blockReg = new RegistryWrapper<>(BLOCKS.register(name, blockSupp));
        ITEMS.register(name, () -> getDefaultBlockItem(blockReg.get()));
        return blockReg;
    }

    public static RegistryWrapper registerBlockAndItem(String name, Supplier<Block> blockSupp, Function<RegistryWrapper<Block>, Item> blockItemFunc) {
        RegistryWrapper<Block> blockReg = new RegistryWrapper<>(BLOCKS.register(name, blockSupp));
        ITEMS.register(name, () -> blockItemFunc.apply(blockReg));
        return blockReg;
    }

    public static final Map<Supplier<ResourceLocation>, FlowerPotBlock> flowerPots = new HashMap<>();

    public static FlowerPotBlock createPottedBlock(Supplier<? extends Block> block) {
        FlowerPotBlock pot = new FlowerPotBlock(() -> (FlowerPotBlock)Blocks.FLOWER_POT, block, BlockBehaviour.Properties.of().instabreak().noOcclusion());
        flowerPots.put(() -> ForgeRegistries.BLOCKS.getKey(block.get()), pot);
        return pot;
    }

    public static BlockEntityType<?> buildTile(BlockEntityType.BlockEntitySupplier<?> supplier, Block... blocks) {
        return BlockEntityType.Builder.of(supplier, blocks).build(null);
    }

    public static RegistryWrapper registerTile(String regName, BlockEntityType.BlockEntitySupplier<?> tile, RegistryWrapper<? extends Block> block){
        return new RegistryWrapper<>(BLOCK_ENTITIES.register(regName, () -> buildTile(tile, block.get())));
    }
}
