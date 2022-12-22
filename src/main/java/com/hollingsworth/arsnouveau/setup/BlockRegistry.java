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
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.world.WorldEvent;
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
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.defaultItemProperties;

public class BlockRegistry {

    //TODO Switch to these
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsNouveau.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArsNouveau.MODID);

    static final String BlockRegistryKey = "minecraft:block";
    static final String BlockEntityRegistryKey = "minecraft:block_entity_type";
    static final String prepend = ArsNouveau.MODID + ":";

    public static BlockBehaviour.Properties LOG_PROP = BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD);

    public static BlockBehaviour.Properties SAP_PROP = BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS);

    @ObjectHolder(value = prepend + LibBlockNames.MAGE_BLOCK, registryName = BlockRegistryKey)
    public static MageBlock MAGE_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.MAGE_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<MageBlockTile> MAGE_BLOCK_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.LIGHT_BLOCK, registryName = BlockRegistryKey)
    public static LightBlock LIGHT_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.LIGHT_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<LightTile> LIGHT_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.T_LIGHT_BLOCK, registryName = BlockRegistryKey)
    public static LightBlock T_LIGHT_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.T_LIGHT_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<LightTile> T_LIGHT_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.AGRONOMIC_SOURCELINK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<AgronomicSourcelinkTile> AGRONOMIC_SOURCELINK_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.AGRONOMIC_SOURCELINK, registryName = BlockRegistryKey)
    public static AgronomicSourcelinkBlock AGRONOMIC_SOURCELINK;
    @ObjectHolder(value = prepend + LibBlockNames.ENCHANTING_APPARATUS, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<EnchantingApparatusTile> ENCHANTING_APP_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.ENCHANTING_APPARATUS, registryName = BlockRegistryKey)
    public static EnchantingApparatusBlock ENCHANTING_APP_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.ARCANE_PEDESTAL, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<ArcanePedestalTile> ARCANE_PEDESTAL_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.ARCANE_PEDESTAL, registryName = BlockRegistryKey)
    public static ArcanePedestal ARCANE_PEDESTAL;
    @ObjectHolder(value = prepend + LibBlockNames.SOURCE_JAR, registryName = BlockRegistryKey)
    public static SourceJar SOURCE_JAR;
    @ObjectHolder(value = prepend + LibBlockNames.SOURCE_JAR, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<SourceJarTile> SOURCE_JAR_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.RELAY, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RelayTile> ARCANE_RELAY_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.MAGE_BLOOM, registryName = BlockRegistryKey)
    public static MageBloomCrop MAGE_BLOOM_CROP;
    @ObjectHolder(value = prepend + LibBlockNames.SCRIBES_BLOCK, registryName = BlockRegistryKey)
    public static ScribesBlock SCRIBES_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.SCRIBES_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<ScribesTile> SCRIBES_TABLE_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.RELAY, registryName = BlockRegistryKey)
    public static Relay RELAY;
    @ObjectHolder(value = prepend + LibBlockNames.RUNE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RuneTile> RUNE_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.RUNE, registryName = BlockRegistryKey)
    public static RuneBlock RUNE_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.PORTAL, registryName = BlockRegistryKey)
    public static PortalBlock PORTAL_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.PORTAL, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<PortalTile> PORTAL_TILE_TYPE;
    @ObjectHolder(value = prepend + LibBlockNames.IMBUEMENT_CHAMBER, registryName = BlockRegistryKey)
    public static ImbuementBlock IMBUEMENT_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.IMBUEMENT_CHAMBER, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<ImbuementTile> IMBUEMENT_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.RELAY_SPLITTER, registryName = BlockRegistryKey)
    public static RelaySplitter RELAY_SPLITTER;
    @ObjectHolder(value = prepend + LibBlockNames.RELAY_SPLITTER, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RelaySplitterTile> RELAY_SPLITTER_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.ARCANE_CORE, registryName = BlockRegistryKey)
    public static ArcaneCore ARCANE_CORE_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.ARCANE_CORE, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<ArcaneCoreTile> ARCANE_CORE_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.ENCHANTED_SPELL_TURRET, registryName = BlockRegistryKey)
    public static EnchantedSpellTurret ENCHANTED_SPELL_TURRET;
    @ObjectHolder(value = prepend + LibBlockNames.ENCHANTED_SPELL_TURRET, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<EnchantedTurretTile> ENCHANTED_SPELL_TURRET_TYPE;
    @ObjectHolder(value = prepend + LibBlockNames.REDSTONE_AIR, registryName = BlockRegistryKey)
    public static RedstoneAir REDSTONE_AIR;
    @ObjectHolder(value = prepend + LibBlockNames.INTANGIBLE_AIR, registryName = BlockRegistryKey)
    public static IntangibleAirBlock INTANGIBLE_AIR;
    @ObjectHolder(value = prepend + LibBlockNames.INTANGIBLE_AIR, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<IntangibleAirTile> INTANGIBLE_AIR_TYPE;
    @ObjectHolder(value = prepend + LibBlockNames.VOLCANIC_SOURCELINK, registryName = BlockRegistryKey)
    public static VolcanicSourcelinkBlock VOLCANIC_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.VOLCANIC_SOURCELINK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<VolcanicSourcelinkTile> VOLCANIC_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.LAVA_LILY, registryName = BlockRegistryKey)
    public static LavaLily LAVA_LILY;
    @ObjectHolder(value = prepend + LibBlockNames.SOURCEBERRY_BUSH, registryName = BlockRegistryKey)
    public static SourceBerryBush SOURCEBERRY_BUSH;
    @ObjectHolder(value = prepend + LibBlockNames.WIXIE_CAULDRON, registryName = BlockRegistryKey)
    public static WixieCauldron WIXIE_CAULDRON;
    @ObjectHolder(value = prepend + LibBlockNames.WIXIE_CAULDRON, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<WixieCauldronTile> WIXIE_CAULDRON_TYPE;
    @ObjectHolder(value = prepend + LibBlockNames.CREATIVE_SOURCE_JAR, registryName = BlockRegistryKey)
    public static CreativeSourceJar CREATIVE_SOURCE_JAR;
    @ObjectHolder(value = prepend + LibBlockNames.CREATIVE_SOURCE_JAR, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<CreativeSourceJarTile> CREATIVE_SOURCE_JAR_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.CASCADING_LOG, registryName = BlockRegistryKey)
    public static StrippableLog CASCADING_LOG;
    @ObjectHolder(value = prepend + LibBlockNames.CASCADING_LEAVES, registryName = BlockRegistryKey)
    public static MagicLeaves CASCADING_LEAVE;
    @ObjectHolder(value = prepend + LibBlockNames.CASCADING_SAPLING, registryName = BlockRegistryKey)
    public static SaplingBlock CASCADING_SAPLING;
    @ObjectHolder(value = prepend + LibBlockNames.CASCADING_WOOD, registryName = BlockRegistryKey)
    public static StrippableLog CASCADING_WOOD;
    @ObjectHolder(value = prepend + LibBlockNames.BLAZING_LOG, registryName = BlockRegistryKey)
    public static StrippableLog BLAZING_LOG;
    @ObjectHolder(value = prepend + LibBlockNames.BLAZING_LEAVES, registryName = BlockRegistryKey)
    public static MagicLeaves BLAZING_LEAVES;
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
    @ObjectHolder(value = prepend + LibBlockNames.RITUAL_BRAZIER, registryName = BlockRegistryKey)
    public static RitualBrazierBlock RITUAL_BLOCK;
    @ObjectHolder(value = prepend + LibBlockNames.RITUAL_BRAZIER, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<RitualBrazierTile> RITUAL_TILE;
    @ObjectHolder(value = prepend + LibBlockNames.ARCHWOOD_BUTTON, registryName = BlockRegistryKey)
    public static WoodButtonBlock ARCHWOOD_BUTTON;
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

    @ObjectHolder(value = prepend + LibBlockNames.BOOKWYRM_LECTERN, registryName = BlockRegistryKey)
    public static BookwyrmLectern BOOKWYRM_LECTERN;

    @ObjectHolder(value = prepend + LibBlockNames.BOOKWYRM_LECTERN, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<BookwyrmLecternTile> BOOKWYRM_LECTERN_TILE;

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

    @ObjectHolder(value = prepend + LibBlockNames.RED_SBED, registryName = BlockRegistryKey)
    public static SummonBed RED_SBED;

    @ObjectHolder(value = prepend + LibBlockNames.BLUE_SBED, registryName = BlockRegistryKey)
    public static SummonBed BLUE_SBED;

    @ObjectHolder(value = prepend + LibBlockNames.GREEN_SBED, registryName = BlockRegistryKey)
    public static SummonBed GREEN_SBED;

    @ObjectHolder(value = prepend + LibBlockNames.ORANGE_SBED, registryName = BlockRegistryKey)
    public static SummonBed ORANGE_SBED;

    @ObjectHolder(value = prepend + LibBlockNames.YELLOW_SBED, registryName = BlockRegistryKey)
    public static SummonBed YELLOW_SBED;

    @ObjectHolder(value = prepend + LibBlockNames.PURPLE_SBED, registryName = BlockRegistryKey)
    public static SummonBed PURPLE_SBED;

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

        //blocks
        registry.register(LibBlockNames.MAGE_BLOCK, new MageBlock());
        registry.register(LibBlockNames.LIGHT_BLOCK, new LightBlock());
        registry.register(LibBlockNames.T_LIGHT_BLOCK, new TempLightBlock());
        registry.register(LibBlockNames.SOURCE_JAR, new SourceJar());
        registry.register(LibBlockNames.CREATIVE_SOURCE_JAR, new CreativeSourceJar());
        registry.register(LibBlockNames.SCRIBES_BLOCK, new ScribesBlock());
        registry.register(LibBlockNames.MAGE_BLOOM, new MageBloomCrop());
        registry.register(LibBlockNames.IMBUEMENT_CHAMBER, new ImbuementBlock());
        registry.register(LibBlockNames.ENCHANTING_APPARATUS, new EnchantingApparatusBlock());
        registry.register(LibBlockNames.ARCANE_CORE, new ArcaneCore());
        registry.register(LibBlockNames.ARCANE_PEDESTAL, new ArcanePedestal());
        registry.register(LibBlockNames.RITUAL_BRAZIER, new RitualBrazierBlock());
        registry.register(LibBlockNames.RUNE, new RuneBlock());
        registry.register(LibBlockNames.PORTAL, new PortalBlock());
        registry.register(LibBlockNames.SPELL_PRISM, new SpellPrismBlock());

        //Relay and turrets
        registry.register(LibBlockNames.RELAY, new Relay());
        registry.register(LibBlockNames.RELAY_SPLITTER, new RelaySplitter());
        registry.register(LibBlockNames.RELAY_DEPOSIT, new RelayDepositBlock());
        registry.register(LibBlockNames.RELAY_WARP, new RelayWarpBlock());
        registry.register(LibBlockNames.RELAY_COLLECTOR, new RelayCollectorBlock());
        registry.register(LibBlockNames.BASIC_SPELL_TURRET, new BasicSpellTurret());
        registry.register(LibBlockNames.TIMER_SPELL_TURRET, new TimerSpellTurret());
        registry.register(LibBlockNames.ENCHANTED_SPELL_TURRET, new EnchantedSpellTurret());

        //Misc
        registry.register(LibBlockNames.SCRYERS_OCULUS, new ScryersOculus());
        registry.register(LibBlockNames.SCRYERS_CRYSTAL, new ScryerCrystal());
        registry.register(LibBlockNames.REDSTONE_AIR, new RedstoneAir());
        registry.register(LibBlockNames.INTANGIBLE_AIR, new IntangibleAirBlock());

        //Trees & co
        registry.register(LibBlockNames.LAVA_LILY, new LavaLily());
        registry.register(LibBlockNames.SOURCEBERRY_BUSH, new SourceBerryBush(BlockBehaviour.Properties.of(Material.PLANT).randomTicks().noCollission().sound(SoundType.SWEET_BERRY_BUSH)));
        registry.register(LibBlockNames.CASCADING_SAPLING, new SaplingBlock(new MagicTree(() -> WorldEvent.CASCADING_TREE), SAP_PROP));
        registry.register(LibBlockNames.BLAZING_SAPLING, new SaplingBlock(new MagicTree(() -> WorldEvent.BLAZING_TREE), SAP_PROP));
        registry.register(LibBlockNames.VEXING_SAPLING, new SaplingBlock(new MagicTree(() -> WorldEvent.VEXING_TREE), SAP_PROP));
        registry.register(LibBlockNames.FLOURISHING_SAPLING, new SaplingBlock(new MagicTree(() -> WorldEvent.FLOURISHING_TREE), SAP_PROP));


        registry.register(LibBlockNames.CASCADING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_BLUE));
        registry.register(LibBlockNames.CASCADING_LEAVES, createLeavesBlock(MaterialColor.COLOR_BLUE));
        registry.register(LibBlockNames.BLAZING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_RED));
        registry.register(LibBlockNames.BLAZING_LEAVES, createLeavesBlock(MaterialColor.COLOR_RED));
        registry.register(LibBlockNames.FLOURISHING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_GREEN));
        registry.register(LibBlockNames.FLOURISHING_LEAVES, createLeavesBlock(MaterialColor.COLOR_LIGHT_GREEN));
        registry.register(LibBlockNames.VEXING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_PURPLE));
        registry.register(LibBlockNames.VEXING_LEAVES, createLeavesBlock(MaterialColor.COLOR_PURPLE));

        registry.register(LibBlockNames.VEXING_WOOD, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_PURPLE));
        registry.register(LibBlockNames.CASCADING_WOOD, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_BLUE));
        registry.register(LibBlockNames.FLOURISHING_WOOD, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_GREEN));
        registry.register(LibBlockNames.BLAZING_WOOD, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWWOOD_RED));
        registry.register(LibBlockNames.ARCHWOOD_PLANK, new ModBlock(LOG_PROP));
        registry.register(LibBlockNames.ARCHWOOD_BUTTON, new WoodButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
        registry.register(LibBlockNames.ARCHWOOD_STAIRS, new StairBlock(() -> ARCHWOOD_PLANK.defaultBlockState(), woodProp));
        registry.register(LibBlockNames.ARCHWOOD_SLABS, new SlabBlock(woodProp));
        registry.register(LibBlockNames.ARCHWOOD_FENCE_GATE, new FenceGateBlock(woodProp));
        registry.register(LibBlockNames.ARCHWOOD_FENCE, new FenceBlock(woodProp));
        registry.register(LibBlockNames.ARCHWOOD_DOOR, new DoorBlock(woodProp));
        registry.register(LibBlockNames.ARCHWOOD_PRESSURE_PLATE, new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, woodProp));
        registry.register(LibBlockNames.ARCHWOOD_TRAPDOOR, new TrapDoorBlock(woodProp));
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

        //Sourcelinks
        registry.register(LibBlockNames.ALCHEMICAL_SOURCELINK, new AlchemicalSourcelinkBlock());
        registry.register(LibBlockNames.AGRONOMIC_SOURCELINK, new AgronomicSourcelinkBlock());
        registry.register(LibBlockNames.VITALIC_SOURCELINK, new VitalicSourcelinkBlock());
        registry.register(LibBlockNames.MYCELIAL_SOURCELINK, new MycelialSourcelinkBlock());
        registry.register(LibBlockNames.VOLCANIC_SOURCELINK, new VolcanicSourcelinkBlock());

        //SummonBlocks
        registry.register(LibBlockNames.BOOKWYRM_LECTERN, new BookwyrmLectern(ModBlock.defaultProperties().noOcclusion()));
        registry.register(LibBlockNames.WIXIE_CAULDRON, new WixieCauldron());
        registry.register(LibBlockNames.WHIRLISPRIG_BLOCK, new WhirlisprigFlower());
        registry.register(LibBlockNames.SCONCE, new SconceBlock());
        registry.register(LibBlockNames.DRYGMY_STONE, new DrygmyStone());

        //Beds
        registry.register(LibBlockNames.RED_SBED, new SummonBed());
        registry.register(LibBlockNames.BLUE_SBED, new SummonBed());
        registry.register(LibBlockNames.GREEN_SBED, new SummonBed());
        registry.register(LibBlockNames.ORANGE_SBED, new SummonBed());
        registry.register(LibBlockNames.YELLOW_SBED, new SummonBed());
        registry.register(LibBlockNames.PURPLE_SBED, new SummonBed());

        registry.register(LibBlockNames.MENDOSTEEN_POD, new ArchfruitPod(() -> FLOURISHING_LOG));
        registry.register(LibBlockNames.BASTION_POD, new ArchfruitPod(() -> VEXING_LOG));
        registry.register(LibBlockNames.FROSTAYA_POD, new ArchfruitPod(() -> CASCADING_LOG));
        registry.register(LibBlockNames.BOMBEGRANATE_POD, new ArchfruitPod(() -> BLAZING_LOG));
        registry.register(LibBlockNames.POTION_DIFFUSER, new PotionDiffuserBlock());
        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
            if (LibBlockNames.DIRECTIONAL_SOURCESTONE.contains(s)) {
                registry.register(s, new DirectionalModBlock());
            } else {
                registry.register(s, new ModBlock());
            }
        }
        registry.register(LibBlockNames.ALTERATION_TABLE, new AlterationTable());
        registry.register(LibBlockNames.MOB_JAR, new MobJar());
        registry.register(LibBlockNames.VOID_PRISM, new VoidPrism());
        registry.register(LibBlockNames.MIRROR_WEAVE, new MirrorWeave(Block.Properties.of(Material.CLOTH_DECORATION).strength(0.1F).sound(SoundType.WOOL).noOcclusion()));
        registry.register(LibBlockNames.GHOST_WEAVE, new GhostWeave(Block.Properties.of(Material.CLOTH_DECORATION).strength(0.1F).sound(SoundType.WOOL).noOcclusion()));
        registry.register(LibBlockNames.FALSE_WEAVE, new FalseWeave(Block.Properties.of(Material.CLOTH_DECORATION).strength(0.1F).sound(SoundType.WOOL).noOcclusion().noCollission()));
        registry.register(LibBlockNames.MAGEBLOOM_BLOCK, new ModBlock(BlockBehaviour.Properties.of(Material.CLOTH_DECORATION, MaterialColor.COLOR_PINK).strength(0.1F).sound(SoundType.WOOL)));
    }

    public static MagicLeaves createLeavesBlock(MaterialColor color) {
        return new MagicLeaves(BlockBehaviour.Properties.of(Material.LEAVES).color(color).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(
                BlockRegistry::allowsSpawnOnLeaves).isSuffocating(BlockRegistry::isntSolid).isViewBlocking(BlockRegistry::isntSolid));
    }

    @SuppressWarnings("ConstantConditions")
    public static void onTileEntityRegistry(IForgeRegistry<BlockEntityType<?>> registry) {

        registry.register(LibBlockNames.MAGE_BLOCK, BlockEntityType.Builder.of(MageBlockTile::new, BlockRegistry.MAGE_BLOCK).build(null));
        registry.register(LibBlockNames.AGRONOMIC_SOURCELINK, BlockEntityType.Builder.of(AgronomicSourcelinkTile::new, BlockRegistry.AGRONOMIC_SOURCELINK).build(null));
        registry.register(LibBlockNames.SOURCE_JAR, BlockEntityType.Builder.of(SourceJarTile::new, BlockRegistry.SOURCE_JAR).build(null));
        registry.register(LibBlockNames.LIGHT_BLOCK, BlockEntityType.Builder.of(LightTile::new, BlockRegistry.LIGHT_BLOCK).build(null));
        registry.register(LibBlockNames.T_LIGHT_BLOCK, BlockEntityType.Builder.of(TempLightTile::new, BlockRegistry.T_LIGHT_BLOCK).build(null));
        registry.register(LibBlockNames.ENCHANTING_APPARATUS, BlockEntityType.Builder.of(EnchantingApparatusTile::new, BlockRegistry.ENCHANTING_APP_BLOCK).build(null));
        registry.register(LibBlockNames.ARCANE_PEDESTAL, BlockEntityType.Builder.of(ArcanePedestalTile::new, BlockRegistry.ARCANE_PEDESTAL).build(null));
        registry.register(LibBlockNames.SCRIBES_BLOCK, BlockEntityType.Builder.of(ScribesTile::new, BlockRegistry.SCRIBES_BLOCK).build(null));
        registry.register(LibBlockNames.RELAY, BlockEntityType.Builder.of(RelayTile::new, BlockRegistry.RELAY).build(null));
        registry.register(LibBlockNames.RUNE, BlockEntityType.Builder.of(RuneTile::new, BlockRegistry.RUNE_BLOCK).build(null));
        registry.register(LibBlockNames.PORTAL, BlockEntityType.Builder.of(PortalTile::new, BlockRegistry.PORTAL_BLOCK).build(null));
        registry.register(LibBlockNames.RELAY_SPLITTER, BlockEntityType.Builder.of(RelaySplitterTile::new, BlockRegistry.RELAY_SPLITTER).build(null));
        registry.register(LibBlockNames.ARCANE_CORE, BlockEntityType.Builder.of(ArcaneCoreTile::new, BlockRegistry.ARCANE_CORE_BLOCK).build(null));
        registry.register(LibBlockNames.IMBUEMENT_CHAMBER, BlockEntityType.Builder.of(ImbuementTile::new, BlockRegistry.IMBUEMENT_BLOCK).build(null));
        registry.register(LibBlockNames.ENCHANTED_SPELL_TURRET, BlockEntityType.Builder.of(EnchantedTurretTile::new, BlockRegistry.ENCHANTED_SPELL_TURRET).build(null));
        registry.register(LibBlockNames.INTANGIBLE_AIR, BlockEntityType.Builder.of(IntangibleAirTile::new, BlockRegistry.INTANGIBLE_AIR).build(null));
        registry.register(LibBlockNames.VOLCANIC_SOURCELINK, BlockEntityType.Builder.of(VolcanicSourcelinkTile::new, BlockRegistry.VOLCANIC_BLOCK).build(null));
        registry.register(LibBlockNames.WIXIE_CAULDRON, BlockEntityType.Builder.of(WixieCauldronTile::new, BlockRegistry.WIXIE_CAULDRON).build(null));
        registry.register(LibBlockNames.CREATIVE_SOURCE_JAR, BlockEntityType.Builder.of(CreativeSourceJarTile::new, BlockRegistry.CREATIVE_SOURCE_JAR).build(null));
        registry.register(LibBlockNames.RITUAL_BRAZIER, BlockEntityType.Builder.of(RitualBrazierTile::new, BlockRegistry.RITUAL_BLOCK).build(null));
        registry.register(LibBlockNames.POTION_JAR_BLOCK, BlockEntityType.Builder.of(PotionJarTile::new, BlockRegistry.POTION_JAR).build(null));
        registry.register(LibBlockNames.POTION_MELDER_BLOCK, BlockEntityType.Builder.of(PotionMelderTile::new, BlockRegistry.POTION_MELDER).build(null));
        registry.register(LibBlockNames.SCONCE, BlockEntityType.Builder.of(SconceTile::new, BlockRegistry.SCONCE_BLOCK).build(null));
        registry.register(LibBlockNames.DRYGMY_STONE, BlockEntityType.Builder.of(DrygmyTile::new, BlockRegistry.DRYGMY_BLOCK).build(null));
        registry.register(LibBlockNames.ALCHEMICAL_SOURCELINK, BlockEntityType.Builder.of(AlchemicalSourcelinkTile::new, BlockRegistry.ALCHEMICAL_BLOCK).build(null));
        registry.register(LibBlockNames.VITALIC_SOURCELINK, BlockEntityType.Builder.of(VitalicSourcelinkTile::new, BlockRegistry.VITALIC_BLOCK).build(null));
        registry.register(LibBlockNames.MYCELIAL_SOURCELINK, BlockEntityType.Builder.of(MycelialSourcelinkTile::new, BlockRegistry.MYCELIAL_BLOCK).build(null));
        registry.register(LibBlockNames.RELAY_DEPOSIT, BlockEntityType.Builder.of(RelayDepositTile::new, BlockRegistry.RELAY_DEPOSIT).build(null));
        registry.register(LibBlockNames.RELAY_WARP, BlockEntityType.Builder.of(RelayWarpTile::new, BlockRegistry.RELAY_WARP).build(null));
        registry.register(LibBlockNames.BOOKWYRM_LECTERN, BlockEntityType.Builder.of(BookwyrmLecternTile::new, BlockRegistry.BOOKWYRM_LECTERN).build(null));
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
        registry.register(LibBlockNames.FALSE_WEAVE, BlockEntityType.Builder.of(FalseWeaveTile::new, BlockRegistry.FALSE_WEAVE).build(null));
        registry.register(LibBlockNames.MIRROR_WEAVE, BlockEntityType.Builder.of(MirrorWeaveTile::new, BlockRegistry.MIRROR_WEAVE).build(null));
        registry.register(LibBlockNames.GHOST_WEAVE, BlockEntityType.Builder.of(GhostWeaveTile::new, BlockRegistry.GHOST_WEAVE).build(null));

    }

    public static void onBlockItemsRegistry(IForgeRegistry<Item> registry) {

        registry.register(LibItemNames.SOURCE_BERRY, new BlockItem(BlockRegistry.SOURCEBERRY_BUSH, defaultItemProperties().food(ItemsRegistry.SOURCE_BERRY_FOOD)));
        registry.register(LibBlockNames.MAGE_BLOCK, new BlockItem(BlockRegistry.MAGE_BLOCK, defaultItemProperties()));
        registry.register(LibBlockNames.LIGHT_BLOCK, new BlockItem(BlockRegistry.LIGHT_BLOCK, new Item.Properties()));
        registry.register(LibBlockNames.AGRONOMIC_SOURCELINK, new RendererBlockItem(BlockRegistry.AGRONOMIC_SOURCELINK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return AgronomicRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.SOURCE_JAR, new BlockItem(BlockRegistry.SOURCE_JAR, defaultItemProperties()));
        registry.register(LibBlockNames.MAGE_BLOOM, new BlockItem(BlockRegistry.MAGE_BLOOM_CROP, defaultItemProperties()));
        registry.register(LibBlockNames.ENCHANTING_APPARATUS, new RendererBlockItem(BlockRegistry.ENCHANTING_APP_BLOCK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("enchanting_apparatus");
            }
        });
        registry.register(LibBlockNames.ARCANE_PEDESTAL, new RendererBlockItem(BlockRegistry.ARCANE_PEDESTAL, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ArcanePedestalRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.SCRIBES_BLOCK, new RendererBlockItem(BlockRegistry.SCRIBES_BLOCK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ScribesRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.RELAY, new RendererBlockItem(BlockRegistry.RELAY, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("source_relay");
            }
        });
        registry.register(LibBlockNames.PORTAL, new BlockItem(BlockRegistry.PORTAL_BLOCK, new Item.Properties()));
        registry.register(LibBlockNames.RELAY_SPLITTER, new RendererBlockItem(BlockRegistry.RELAY_SPLITTER, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("source_splitter");
            }
        });
        registry.register(LibBlockNames.IMBUEMENT_CHAMBER, new RendererBlockItem(BlockRegistry.IMBUEMENT_BLOCK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("imbuement_chamber");
            }
        });
        registry.register(LibBlockNames.ARCANE_CORE, new RendererBlockItem(BlockRegistry.ARCANE_CORE_BLOCK, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ArcaneCoreRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.VOLCANIC_SOURCELINK, new RendererBlockItem(BlockRegistry.VOLCANIC_BLOCK, defaultItemProperties().fireResistant()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return VolcanicRenderer::getISTER;
            }
        });
        registry.register(LibBlockNames.LAVA_LILY, new FluidBlockItem(BlockRegistry.LAVA_LILY, defaultItemProperties().fireResistant()));
        registry.register(LibBlockNames.WIXIE_CAULDRON, new BlockItem(BlockRegistry.WIXIE_CAULDRON, defaultItemProperties()));
        registry.register(LibBlockNames.CREATIVE_SOURCE_JAR, new BlockItem(BlockRegistry.CREATIVE_SOURCE_JAR, defaultItemProperties()));
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

        registry.register(LibBlockNames.CASCADING_LEAVES, getDefaultBlockItem(BlockRegistry.CASCADING_LEAVE));
        registry.register(LibBlockNames.CASCADING_LOG, getDefaultBlockItem(BlockRegistry.CASCADING_LOG));
        registry.register(LibBlockNames.CASCADING_SAPLING, getDefaultBlockItem(BlockRegistry.CASCADING_SAPLING));
        registry.register(LibBlockNames.CASCADING_WOOD, getDefaultBlockItem(BlockRegistry.CASCADING_WOOD));
        registry.register(LibBlockNames.VEXING_LEAVES, getDefaultBlockItem(BlockRegistry.VEXING_LEAVES));
        registry.register(LibBlockNames.VEXING_LOG, getDefaultBlockItem(BlockRegistry.VEXING_LOG));
        registry.register(LibBlockNames.VEXING_SAPLING, getDefaultBlockItem(BlockRegistry.VEXING_SAPLING));
        registry.register(LibBlockNames.VEXING_WOOD, getDefaultBlockItem(BlockRegistry.VEXING_WOOD));
        registry.register(LibBlockNames.FLOURISHING_LEAVES, getDefaultBlockItem(BlockRegistry.FLOURISHING_LEAVES));
        registry.register(LibBlockNames.FLOURISHING_LOG, getDefaultBlockItem(BlockRegistry.FLOURISHING_LOG));
        registry.register(LibBlockNames.FLOURISHING_SAPLING, getDefaultBlockItem(BlockRegistry.FLOURISHING_SAPLING));
        registry.register(LibBlockNames.FLOURISHING_WOOD, getDefaultBlockItem(BlockRegistry.FLOURISHING_WOOD));
        registry.register(LibBlockNames.BLAZING_LEAVES, getDefaultBlockItem(BlockRegistry.BLAZING_LEAVES));
        registry.register(LibBlockNames.BLAZING_LOG, getDefaultBlockItem(BlockRegistry.BLAZING_LOG));
        registry.register(LibBlockNames.BLAZING_SAPLING, getDefaultBlockItem(BlockRegistry.BLAZING_SAPLING));
        registry.register(LibBlockNames.BLAZING_WOOD, getDefaultBlockItem(BlockRegistry.BLAZING_WOOD));
        registry.register(LibBlockNames.ARCHWOOD_PLANK, getDefaultBlockItem(BlockRegistry.ARCHWOOD_PLANK));
        registry.register(LibBlockNames.RITUAL_BRAZIER, new RendererBlockItem(BlockRegistry.RITUAL_BLOCK,
                defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return RitualBrazierRenderer::getISTER;
            }
        });

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
        registry.register(LibBlockNames.BOOKWYRM_LECTERN, getDefaultBlockItem(BlockRegistry.BOOKWYRM_LECTERN));
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
        registry.register(LibBlockNames.ENCHANTED_SPELL_TURRET, new RendererBlockItem(BlockRegistry.ENCHANTED_SPELL_TURRET, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ReducerTurretRenderer::getISTER;
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
        registry.register(LibBlockNames.RED_SBED, getDefaultBlockItem(BlockRegistry.RED_SBED));
        registry.register(LibBlockNames.BLUE_SBED, getDefaultBlockItem(BlockRegistry.BLUE_SBED));
        registry.register(LibBlockNames.GREEN_SBED, getDefaultBlockItem(BlockRegistry.GREEN_SBED));
        registry.register(LibBlockNames.YELLOW_SBED, getDefaultBlockItem(BlockRegistry.YELLOW_SBED));
        registry.register(LibBlockNames.PURPLE_SBED, getDefaultBlockItem(BlockRegistry.PURPLE_SBED));
        registry.register(LibBlockNames.ORANGE_SBED, getDefaultBlockItem(BlockRegistry.ORANGE_SBED));
        registry.register(LibBlockNames.SCRYERS_CRYSTAL, getDefaultBlockItem(BlockRegistry.SCRYERS_CRYSTAL));
        registry.register(LibBlockNames.SCRYERS_OCULUS, new RendererBlockItem(BlockRegistry.SCRYERS_OCULUS, defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ScryerEyeRenderer::getISTER;
            }
        }.withTooltip(Component.translatable("ars_nouveau.tooltip.scryers_oculus").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE))));

        registry.register(LibBlockNames.POTION_DIFFUSER, getDefaultBlockItem(BlockRegistry.POTION_DIFFUSER));
        registry.register(LibBlockNames.MENDOSTEEN_POD, new ItemNameBlockItem(BlockRegistry.MENDOSTEEN_POD, defaultItemProperties().food(ItemsRegistry.MENDOSTEEN_FOOD)));
        registry.register(LibBlockNames.BASTION_POD, new ItemNameBlockItem(BlockRegistry.BASTION_POD, defaultItemProperties().food(ItemsRegistry.BASTION_FOOD)));
        registry.register(LibBlockNames.BOMBEGRANATE_POD, new ItemNameBlockItem(BlockRegistry.BOMBEGRANTE_POD, defaultItemProperties().food(ItemsRegistry.BLASTING_FOOD)));
        registry.register(LibBlockNames.FROSTAYA_POD, new ItemNameBlockItem(BlockRegistry.FROSTAYA_POD, defaultItemProperties().food(ItemsRegistry.FROSTAYA_FOOD)));

        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
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

    static Block.Properties woodProp = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD);


    public static Block getBlock(String s) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s));
    }
}
