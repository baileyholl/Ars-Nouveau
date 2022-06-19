package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.LightBlock;
import com.hollingsworth.arsnouveau.common.block.*;
import com.hollingsworth.arsnouveau.common.block.tile.*;
import com.hollingsworth.arsnouveau.common.items.FluidBlockItem;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Objects;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.defaultItemProperties;
@Mod(ArsNouveau.MODID)
public class BlockRegistry {

    static final String BlockRegistryKey = "minecraft:block";
    static final String BlockEntityRegistryKey = "minecraft:block_entity_type";

    //@ObjectHolder( value = LibBlockNames.ARCANE_ROAD, registryName = BlockRegistryKey) public static TickableModBlock ARCANE_ROAD;

    public static BlockBehaviour.Properties LOG_PROP = BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD);

    public static BlockBehaviour.Properties SAP_PROP = BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS);

    @ObjectHolder(value = LibBlockNames.MAGE_BLOCK, registryName = BlockRegistryKey)
    public static MageBlock MAGE_BLOCK;
    @ObjectHolder(value = LibBlockNames.MAGE_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<MageBlockTile> MAGE_BLOCK_TILE;

    @ObjectHolder(value = LibBlockNames.LIGHT_BLOCK, registryName = BlockRegistryKey)
    public static LightBlock LIGHT_BLOCK;
    @ObjectHolder(value = LibBlockNames.LIGHT_BLOCK, registryName = BlockEntityRegistryKey)
    public static BlockEntityType<LightTile> LIGHT_TILE;
    @ObjectHolder( value = LibBlockNames.AGRONOMIC_SOURCELINK, registryName = BlockEntityRegistryKey) public static BlockEntityType<AgronomicSourcelinkTile> AGRONOMIC_SOURCELINK_TILE;
    @ObjectHolder( value = LibBlockNames.AGRONOMIC_SOURCELINK, registryName = BlockRegistryKey) public static AgronomicSourcelinkBlock AGRONOMIC_SOURCELINK;

    @ObjectHolder( value = LibBlockNames.ENCHANTING_APPARATUS, registryName = BlockEntityRegistryKey) public static BlockEntityType<EnchantingApparatusTile> ENCHANTING_APP_TILE;
    @ObjectHolder( value = LibBlockNames.ENCHANTING_APPARATUS, registryName = BlockRegistryKey) public static EnchantingApparatusBlock ENCHANTING_APP_BLOCK;

    @ObjectHolder( value = LibBlockNames.ARCANE_PEDESTAL, registryName = BlockEntityRegistryKey) public static BlockEntityType<ArcanePedestalTile> ARCANE_PEDESTAL_TILE;
    @ObjectHolder( value = LibBlockNames.ARCANE_PEDESTAL, registryName = BlockRegistryKey) public static ArcanePedestal ARCANE_PEDESTAL;
    @ObjectHolder( value = LibBlockNames.SOURCE_JAR, registryName = BlockRegistryKey) public static SourceJar SOURCE_JAR;
    @ObjectHolder( value = LibBlockNames.SOURCE_JAR, registryName = BlockEntityRegistryKey) public static BlockEntityType<SourceJarTile> SOURCE_JAR_TILE;
    @ObjectHolder( value = LibBlockNames.RELAY, registryName = BlockEntityRegistryKey) public static BlockEntityType<RelayTile> ARCANE_RELAY_TILE;

    @ObjectHolder( value = LibBlockNames.MAGE_BLOOM, registryName = BlockRegistryKey) public static MageBloomCrop MAGE_BLOOM_CROP;
    @ObjectHolder( value = LibBlockNames.ARCANE_BRICKS, registryName = BlockRegistryKey) public static ModBlock ARCANE_BRICKS;
    @ObjectHolder( value = LibBlockNames.SCRIBES_BLOCK, registryName = BlockRegistryKey) public static ScribesBlock SCRIBES_BLOCK;
    @ObjectHolder( value = LibBlockNames.SCRIBES_BLOCK, registryName = BlockEntityRegistryKey) public static BlockEntityType<ScribesTile> SCRIBES_TABLE_TILE;
    @ObjectHolder( value = LibBlockNames.RELAY, registryName = BlockRegistryKey) public static Relay RELAY;
    @ObjectHolder( value = LibBlockNames.RUNE, registryName = BlockEntityRegistryKey) public static BlockEntityType<RuneTile> RUNE_TILE;
    @ObjectHolder( value = LibBlockNames.RUNE, registryName = BlockRegistryKey) public static RuneBlock RUNE_BLOCK;
    @ObjectHolder( value = LibBlockNames.PORTAL, registryName = BlockRegistryKey) public static PortalBlock PORTAL_BLOCK;
    @ObjectHolder( value = LibBlockNames.PORTAL, registryName = BlockEntityRegistryKey) public static BlockEntityType<PortalTile> PORTAL_TILE_TYPE;
    @ObjectHolder( value = LibBlockNames.IMBUEMENT_CHAMBER, registryName = BlockRegistryKey) public static ImbuementBlock IMBUEMENT_BLOCK;
    @ObjectHolder( value = LibBlockNames.IMBUEMENT_CHAMBER, registryName = BlockEntityRegistryKey) public static BlockEntityType<ImbuementTile> IMBUEMENT_TILE;
    @ObjectHolder( value = LibBlockNames.RELAY_SPLITTER, registryName = BlockRegistryKey) public static RelaySplitter RELAY_SPLITTER;
    @ObjectHolder( value = LibBlockNames.RELAY_SPLITTER, registryName = BlockEntityRegistryKey) public static BlockEntityType<RelaySplitterTile> RELAY_SPLITTER_TILE;
    @ObjectHolder( value = LibBlockNames.ARCANE_CORE, registryName = BlockRegistryKey) public static ArcaneCore ARCANE_CORE_BLOCK;
    @ObjectHolder( value = LibBlockNames.ARCANE_CORE, registryName = BlockEntityRegistryKey) public static BlockEntityType<ArcaneCoreTile> ARCANE_CORE_TILE;
    @ObjectHolder( value = LibBlockNames.AB_ALTERNATE, registryName = BlockRegistryKey) public static ModBlock AB_ALTERNATE;
    @ObjectHolder( value = LibBlockNames.AB_BASKET, registryName = BlockRegistryKey) public static ModBlock AB_BASKET;
    @ObjectHolder( value = LibBlockNames.AB_HERRING, registryName = BlockRegistryKey) public static ModBlock AB_HERRING;
    @ObjectHolder( value = LibBlockNames.AB_MOSAIC, registryName = BlockRegistryKey) public static ModBlock AB_MOSAIC;
    @ObjectHolder( value = LibBlockNames.ARCANE_STONE, registryName = BlockRegistryKey) public static ModBlock ARCANE_STONE;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH_SLAB, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH_SLAB;
    @ObjectHolder( value = LibBlockNames.AB_CLOVER, registryName = BlockRegistryKey) public static ModBlock AB_CLOVER;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH_BASKET, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH_BASKET;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH_CLOVER, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH_CLOVER;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH_HERRING, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH_HERRING;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH_MOSAIC, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH_MOSAIC;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH_ALTERNATING, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH_ALTERNATING;
    @ObjectHolder( value = LibBlockNames.AB_SMOOTH_ASHLAR, registryName = BlockRegistryKey) public static ModBlock AB_SMOOTH_ASHLAR;
    @ObjectHolder( value = LibBlockNames.ENCHANTED_SPELL_TURRET, registryName = BlockRegistryKey) public static EnchantedSpellTurret ENCHANTED_SPELL_TURRET;
    @ObjectHolder( value = LibBlockNames.ENCHANTED_SPELL_TURRET, registryName = BlockEntityRegistryKey) public static BlockEntityType<EnchantedTurretTile> ENCHANTED_SPELL_TURRET_TYPE;
    @ObjectHolder( value = LibBlockNames.REDSTONE_AIR, registryName = BlockRegistryKey) public static RedstoneAir REDSTONE_AIR;
    @ObjectHolder( value = LibBlockNames.INTANGIBLE_AIR, registryName = BlockRegistryKey) public static IntangibleAirBlock INTANGIBLE_AIR;
    @ObjectHolder( value = LibBlockNames.INTANGIBLE_AIR, registryName = BlockEntityRegistryKey) public static  BlockEntityType<IntangibleAirTile> INTANGIBLE_AIR_TYPE;

    @ObjectHolder( value = LibBlockNames.VOLCANIC_SOURCELINK, registryName = BlockRegistryKey) public static VolcanicSourcelinkBlock VOLCANIC_BLOCK;
    @ObjectHolder( value = LibBlockNames.VOLCANIC_SOURCELINK, registryName = BlockEntityRegistryKey) public static  BlockEntityType<VolcanicSourcelinkTile> VOLCANIC_TILE;
    @ObjectHolder( value = LibBlockNames.LAVA_LILY, registryName = BlockRegistryKey) public static LavaLily LAVA_LILY;
    @ObjectHolder( value = LibBlockNames.SOURCEBERRY_BUSH, registryName = BlockRegistryKey) public static SourceBerryBush SOURCEBERRY_BUSH;

    @ObjectHolder( value = LibBlockNames.WIXIE_CAULDRON, registryName = BlockRegistryKey) public static WixieCauldron WIXIE_CAULDRON;
    @ObjectHolder( value = LibBlockNames.WIXIE_CAULDRON, registryName = BlockEntityRegistryKey) public static BlockEntityType<WixieCauldronTile> WIXIE_CAULDRON_TYPE;


    @ObjectHolder( value = LibBlockNames.CREATIVE_SOURCE_JAR, registryName = BlockRegistryKey) public static CreativeSourceJar CREATIVE_SOURCE_JAR;
    @ObjectHolder( value = LibBlockNames.CREATIVE_SOURCE_JAR, registryName = BlockEntityRegistryKey) public static BlockEntityType<CreativeSourceJarTile> CREATIVE_SOURCE_JAR_TILE;

    @ObjectHolder( value = LibBlockNames.CASCADING_LOG, registryName = BlockRegistryKey) public static StrippableLog CASCADING_LOG;
    @ObjectHolder( value = LibBlockNames.CASCADING_LEAVES, registryName = BlockRegistryKey) public static MagicLeaves CASCADING_LEAVE;
    @ObjectHolder( value = LibBlockNames.CASCADING_SAPLING, registryName = BlockRegistryKey) public static SaplingBlock CASCADING_SAPLING;
    @ObjectHolder( value = LibBlockNames.CASCADING_WOOD, registryName = BlockRegistryKey) public static StrippableLog CASCADING_WOOD;

    @ObjectHolder( value = LibBlockNames.BLAZING_LOG, registryName = BlockRegistryKey) public static StrippableLog BLAZING_LOG;
    @ObjectHolder( value = LibBlockNames.BLAZING_LEAVES, registryName = BlockRegistryKey) public static MagicLeaves BLAZING_LEAVES;
    @ObjectHolder( value = LibBlockNames.BLAZING_SAPLING, registryName = BlockRegistryKey) public static SaplingBlock BLAZING_SAPLING;
    @ObjectHolder( value = LibBlockNames.BLAZING_WOOD, registryName = BlockRegistryKey) public static StrippableLog BLAZING_WOOD;

    @ObjectHolder( value = LibBlockNames.VEXING_LOG, registryName = BlockRegistryKey) public static StrippableLog VEXING_LOG;
    @ObjectHolder( value = LibBlockNames.VEXING_LEAVES, registryName = BlockRegistryKey) public static MagicLeaves VEXING_LEAVES;
    @ObjectHolder( value = LibBlockNames.VEXING_SAPLING, registryName = BlockRegistryKey) public static SaplingBlock VEXING_SAPLING;
    @ObjectHolder( value = LibBlockNames.VEXING_WOOD, registryName = BlockRegistryKey) public static StrippableLog VEXING_WOOD;

    @ObjectHolder( value = LibBlockNames.FLOURISHING_LOG, registryName = BlockRegistryKey) public static StrippableLog FLOURISHING_LOG;
    @ObjectHolder( value = LibBlockNames.FLOURISHING_LEAVES, registryName = BlockRegistryKey) public static MagicLeaves FLOURISHING_LEAVES;
    @ObjectHolder( value = LibBlockNames.FLOURISHING_SAPLING, registryName = BlockRegistryKey) public static SaplingBlock FLOURISHING_SAPLING;
    @ObjectHolder( value = LibBlockNames.FLOURISHING_WOOD, registryName = BlockRegistryKey) public static StrippableLog FLOURISHING_WOOD;
    @ObjectHolder( value = LibBlockNames.ARCHWOOD_PLANK, registryName = BlockRegistryKey) public static ModBlock ARCHWOOD_PLANK;

    @ObjectHolder( value = LibBlockNames.RITUAL_BRAZIER, registryName = BlockRegistryKey) public static RitualBrazierBlock RITUAL_BLOCK;
    @ObjectHolder( value = LibBlockNames.RITUAL_BRAZIER, registryName = BlockEntityRegistryKey) public static BlockEntityType<RitualBrazierTile> RITUAL_TILE;

    @ObjectHolder( value = LibBlockNames.ARCHWOOD_BUTTON, registryName = BlockRegistryKey) public static WoodButtonBlock ARCHWOOD_BUTTON;
    @ObjectHolder( value = LibBlockNames.ARCHWOOD_STAIRS, registryName = BlockRegistryKey) public static StairBlock ARCHWOOD_STAIRS;
    @ObjectHolder( value = LibBlockNames.ARCHWOOD_SLABS, registryName = BlockRegistryKey) public static SlabBlock ARCHWOOD_SLABS;
   // @ObjectHolder( value = LibBlockNames.ARCHWOOD_SIGN, registryName = BlockRegistryKey) public static WallSignBlock ARCHWOOD_SIGN;
    @ObjectHolder( value = LibBlockNames.ARCHWOOD_FENCE_GATE, registryName = BlockRegistryKey) public static FenceGateBlock ARCHWOOD_FENCE_GATE;
    @ObjectHolder(value = LibBlockNames.ARCHWOOD_TRAPDOOR, registryName = BlockRegistryKey)
    public static TrapDoorBlock ARCHWOOD_TRAPDOOR;
    @ObjectHolder(value = LibBlockNames.ARCHWOOD_PRESSURE_PLATE, registryName = BlockRegistryKey)
    public static PressurePlateBlock ARCHWOOD_PPlate;
    @ObjectHolder(value = LibBlockNames.ARCHWOOD_FENCE, registryName = BlockRegistryKey)
    public static FenceBlock ARCHWOOD_FENCE;
    @ObjectHolder( value = LibBlockNames.ARCHWOOD_DOOR, registryName = BlockRegistryKey) public static DoorBlock ARCHWOOD_DOOR;

    @ObjectHolder( value = LibBlockNames.STRIPPED_AWLOG_BLUE, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWLOG_BLUE;
    @ObjectHolder( value = LibBlockNames.STRIPPED_AWWOOD_BLUE, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWWOOD_BLUE;
    @ObjectHolder( value = LibBlockNames.STRIPPED_AWLOG_GREEN, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWLOG_GREEN;
    @ObjectHolder( value = LibBlockNames.STRIPPED_AWWOOD_GREEN, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWWOOD_GREEN;
    @ObjectHolder( value = LibBlockNames.STRIPPED_AWLOG_RED, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWLOG_RED;
    @ObjectHolder( value = LibBlockNames.STRIPPED_AWWOOD_RED, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWWOOD_RED;
    @ObjectHolder( value = LibBlockNames.STRIPPED_AWLOG_PURPLE, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWLOG_PURPLE;
    @ObjectHolder( value = LibBlockNames.STRIPPED_AWWOOD_PURPLE, registryName = BlockRegistryKey) public static RotatedPillarBlock STRIPPED_AWWOOD_PURPLE;
    @ObjectHolder( value = LibBlockNames.SOURCE_GEM_BLOCK, registryName = BlockRegistryKey) public static ModBlock SOURCE_GEM_BLOCK;

    @ObjectHolder( value = LibBlockNames.POTION_JAR_BLOCK, registryName = BlockRegistryKey) public static PotionJar POTION_JAR;
    @ObjectHolder( value = LibBlockNames.POTION_JAR_BLOCK, registryName = BlockEntityRegistryKey) public static BlockEntityType<PotionJarTile> POTION_JAR_TYPE;
    @ObjectHolder( value = LibBlockNames.POTION_MELDER_BLOCK, registryName = BlockRegistryKey) public static PotionMelder POTION_MELDER;
    @ObjectHolder( value = LibBlockNames.POTION_MELDER_BLOCK, registryName = BlockEntityRegistryKey) public static BlockEntityType<PotionMelderTile> POTION_MELDER_TYPE;

    @ObjectHolder( value = LibBlockNames.SCONCE, registryName = BlockRegistryKey) public static SconceBlock SCONCE_BLOCK;
    @ObjectHolder( value = LibBlockNames.SCONCE, registryName = BlockEntityRegistryKey) public static BlockEntityType<SconceTile> SCONCE_TILE;

    @ObjectHolder( value = LibBlockNames.DRYGMY_STONE, registryName = BlockRegistryKey) public static DrygmyStone DRYGMY_BLOCK;
    @ObjectHolder( value = LibBlockNames.DRYGMY_STONE, registryName = BlockEntityRegistryKey) public static BlockEntityType<DrygmyTile> DRYGMY_TILE;

    @ObjectHolder( value = LibBlockNames.AS_GOLD_ALT, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_ALT;
    @ObjectHolder( value = LibBlockNames.AS_GOLD_ASHLAR, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_ASHLAR;
    @ObjectHolder( value = LibBlockNames.AS_GOLD_BASKET, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_BASKET;
    @ObjectHolder( value = LibBlockNames.AS_GOLD_CLOVER, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_CLOVER;
    @ObjectHolder( value = LibBlockNames.AS_GOLD_HERRING, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_HERRING;
    @ObjectHolder( value = LibBlockNames.AS_GOLD_MOSAIC, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_MOSAIC;
    @ObjectHolder( value = LibBlockNames.AS_GOLD_SLAB, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_SLAB;
    @ObjectHolder( value = LibBlockNames.AS_GOLD_STONE, registryName = BlockRegistryKey) public static ModBlock AS_GOLD_STONE;

    @ObjectHolder( value = LibBlockNames.ALCHEMICAL_SOURCELINK, registryName = BlockRegistryKey) public static AlchemicalSourcelinkBlock ALCHEMICAL_BLOCK;
    @ObjectHolder( value = LibBlockNames.ALCHEMICAL_SOURCELINK, registryName = BlockEntityRegistryKey) public static BlockEntityType<AlchemicalSourcelinkTile> ALCHEMICAL_TILE;

    @ObjectHolder( value = LibBlockNames.VITALIC_SOURCELINK, registryName = BlockRegistryKey) public static VitalicSourcelinkBlock VITALIC_BLOCK;
    @ObjectHolder( value = LibBlockNames.VITALIC_SOURCELINK, registryName = BlockEntityRegistryKey) public static BlockEntityType<VitalicSourcelinkTile> VITALIC_TILE;

    @ObjectHolder( value = LibBlockNames.MYCELIAL_SOURCELINK, registryName = BlockRegistryKey) public static MycelialSourcelinkBlock MYCELIAL_BLOCK;
    @ObjectHolder( value = LibBlockNames.MYCELIAL_SOURCELINK, registryName = BlockEntityRegistryKey) public static BlockEntityType<MycelialSourcelinkTile> MYCELIAL_TILE;

    @ObjectHolder( value = LibBlockNames.RELAY_DEPOSIT, registryName = BlockRegistryKey) public static RelayDepositBlock RELAY_DEPOSIT;
    @ObjectHolder( value = LibBlockNames.RELAY_DEPOSIT, registryName = BlockEntityRegistryKey) public static BlockEntityType<RelayDepositTile> RELAY_DEPOSIT_TILE;

    @ObjectHolder( value = LibBlockNames.RELAY_WARP, registryName = BlockRegistryKey) public static RelayWarpBlock RELAY_WARP;
    @ObjectHolder( value = LibBlockNames.RELAY_WARP, registryName = BlockEntityRegistryKey) public static BlockEntityType<RelayWarpTile> RELAY_WARP_TILE;


    @ObjectHolder( value = LibBlockNames.BOOKWYRM_LECTERN, registryName = BlockRegistryKey) public static BookwyrmLectern BOOKWYRM_LECTERN;
    @ObjectHolder( value = LibBlockNames.BOOKWYRM_LECTERN, registryName = BlockEntityRegistryKey) public static BlockEntityType<BookwyrmLecternTile> BOOKWYRM_LECTERN_TILE;

    @ObjectHolder( value = LibBlockNames.BASIC_SPELL_TURRET, registryName = BlockRegistryKey) public static BasicSpellTurret BASIC_SPELL_TURRET;
    @ObjectHolder( value = LibBlockNames.BASIC_SPELL_TURRET, registryName = BlockEntityRegistryKey) public static BlockEntityType<BasicSpellTurretTile> BASIC_SPELL_TURRET_TILE;

    @ObjectHolder( value = LibBlockNames.TIMER_SPELL_TURRET, registryName = BlockRegistryKey) public static TimerSpellTurret TIMER_SPELL_TURRET;
    @ObjectHolder( value = LibBlockNames.TIMER_SPELL_TURRET, registryName = BlockEntityRegistryKey) public static BlockEntityType<TimerSpellTurretTile> TIMER_SPELL_TURRET_TILE;
    @ObjectHolder( value = LibBlockNames.ARCHWOOD_CHEST, registryName = BlockRegistryKey) public static  ArchwoodChest ARCHWOOD_CHEST;
    @ObjectHolder( value = LibBlockNames.ARCHWOOD_CHEST, registryName = BlockEntityRegistryKey) public static  BlockEntityType<ArchwoodChestTile> ARCHWOOD_CHEST_TILE;
    @ObjectHolder( value = LibBlockNames.SPELL_PRISM, registryName = BlockRegistryKey) public static  SpellPrismBlock SPELL_PRISM;
    @ObjectHolder( value = LibBlockNames.WHIRLISPRIG_BLOCK, registryName = BlockEntityRegistryKey) public static BlockEntityType<WhirlisprigTile> WHIRLISPRIG_TILE;
    @ObjectHolder( value = LibBlockNames.WHIRLISPRIG_BLOCK, registryName = BlockRegistryKey) public static WhirlisprigFlower WHIRLISPRIG_FLOWER;
    @ObjectHolder( value = LibBlockNames.RELAY_COLLECTOR, registryName = BlockRegistryKey) public static RelayCollectorBlock RELAY_COLLECTOR;
    @ObjectHolder( value = LibBlockNames.RELAY_COLLECTOR, registryName = BlockEntityRegistryKey) public static BlockEntityType<RelayCollectorTile> RELAY_COLLECTOR_TILE;

    @ObjectHolder( value = LibBlockNames.RED_SBED, registryName = BlockRegistryKey) public static SummonBed RED_SBED;
    @ObjectHolder( value = LibBlockNames.BLUE_SBED, registryName = BlockRegistryKey) public static SummonBed BLUE_SBED;
    @ObjectHolder( value = LibBlockNames.GREEN_SBED, registryName = BlockRegistryKey) public static SummonBed GREEN_SBED;
    @ObjectHolder( value = LibBlockNames.ORANGE_SBED, registryName = BlockRegistryKey) public static SummonBed ORANGE_SBED;
    @ObjectHolder( value = LibBlockNames.YELLOW_SBED, registryName = BlockRegistryKey) public static SummonBed YELLOW_SBED;
    @ObjectHolder( value = LibBlockNames.PURPLE_SBED, registryName = BlockRegistryKey) public static SummonBed PURPLE_SBED;

    @ObjectHolder(value = LibBlockNames.STATE_PROVIDER, registryName = "minecraft:worldgen/block_state_provider_type")
    public static BlockStateProviderType<?> stateProviderType;

    @ObjectHolder( value = LibBlockNames.SCRYERS_OCULUS, registryName = BlockRegistryKey) public static ScryersOculus SCRYERS_OCULUS;
    @ObjectHolder( value = LibBlockNames.SCRYERS_OCULUS, registryName = BlockEntityRegistryKey) public static BlockEntityType<ScryersOculusTile> SCRYERS_OCULUS_TILE;

    @ObjectHolder( value = LibBlockNames.SCRYERS_CRYSTAL, registryName = BlockRegistryKey) public static ScryerCrystal SCRYERS_CRYSTAL;
    @ObjectHolder( value = LibBlockNames.SCRYERS_CRYSTAL, registryName = BlockEntityRegistryKey) public static BlockEntityType<ScryerCrystalTile> SCRYER_CRYSTAL_TILE;

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void BlocksRegistry(final RegisterEvent event) {

        }

        public static void onBlocksRegistry(final IForgeRegistry<Block> registry) {

            //blocks
            registry.register(LibBlockNames.MAGE_BLOCK, new MageBlock());
            registry.register(LibBlockNames.LIGHT_BLOCK, new LightBlock());
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
            registry.register(LibBlockNames.CASCADING_LEAVES, createLeavesBlock());
            registry.register(LibBlockNames.BLAZING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_RED));
            registry.register(LibBlockNames.BLAZING_LEAVES, createLeavesBlock());
            registry.register(LibBlockNames.FLOURISHING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_GREEN));
            registry.register(LibBlockNames.FLOURISHING_LEAVES, createLeavesBlock());
            registry.register(LibBlockNames.VEXING_LOG, new StrippableLog(LOG_PROP, () -> BlockRegistry.STRIPPED_AWLOG_PURPLE));
            registry.register(LibBlockNames.VEXING_LEAVES, createLeavesBlock());

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

            registry.register(LibBlockNames.ARCANE_BRICKS, new ModBlock());

            registry.register(LibBlockNames.AB_SMOOTH_BASKET, new ModBlock());
            registry.register(LibBlockNames.AB_SMOOTH_CLOVER, new ModBlock());
            registry.register(LibBlockNames.AB_SMOOTH_HERRING, new ModBlock());
            registry.register(LibBlockNames.AB_SMOOTH_MOSAIC, new ModBlock());
            registry.register(LibBlockNames.AB_SMOOTH_ALTERNATING, new ModBlock());
            registry.register(LibBlockNames.AB_SMOOTH_ASHLAR, new ModBlock());

            registry.register(LibBlockNames.AB_ALTERNATE, new ModBlock());
            registry.register(LibBlockNames.ARCANE_STONE, new ModBlock());
            registry.register(LibBlockNames.AB_BASKET, new ModBlock());
            registry.register(LibBlockNames.AB_HERRING, new ModBlock());
            registry.register(LibBlockNames.AB_MOSAIC, new ModBlock());

            registry.register(LibBlockNames.AB_SMOOTH, new ModBlock());
            registry.register(LibBlockNames.AB_SMOOTH_SLAB, new ModBlock());
            registry.register(LibBlockNames.AB_CLOVER, new ModBlock());

            registry.register(LibBlockNames.AS_GOLD_ALT, new ModBlock());
            registry.register(LibBlockNames.AS_GOLD_ASHLAR, new ModBlock());
            registry.register(LibBlockNames.AS_GOLD_BASKET, new ModBlock());
            registry.register(LibBlockNames.AS_GOLD_CLOVER, new ModBlock());
            registry.register(LibBlockNames.AS_GOLD_HERRING, new ModBlock());
            registry.register(LibBlockNames.AS_GOLD_MOSAIC, new ModBlock());
            registry.register(LibBlockNames.AS_GOLD_SLAB, new ModBlock());
            registry.register(LibBlockNames.AS_GOLD_STONE, new ModBlock());

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

        }
        static Block.Properties woodProp = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD);
        public static MagicLeaves createLeavesBlock() {
            return new MagicLeaves(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(
                    BlockRegistry::allowsSpawnOnLeaves).isSuffocating(BlockRegistry::isntSolid).isViewBlocking(BlockRegistry::isntSolid));
        }


        @SuppressWarnings("ConstantConditions")
        public static void onTileEntityRegistry(IForgeRegistry<BlockEntityType<?>> registry) {

            registry.register(LibBlockNames.MAGE_BLOCK, BlockEntityType.Builder.of(MageBlockTile::new, BlockRegistry.MAGE_BLOCK).build(null));
            registry.register(LibBlockNames.AGRONOMIC_SOURCELINK, BlockEntityType.Builder.of(AgronomicSourcelinkTile::new, BlockRegistry.AGRONOMIC_SOURCELINK).build(null));
            registry.register(LibBlockNames.SOURCE_JAR, BlockEntityType.Builder.of(SourceJarTile::new, BlockRegistry.SOURCE_JAR).build(null));
            registry.register(LibBlockNames.LIGHT_BLOCK, BlockEntityType.Builder.of(LightTile::new, BlockRegistry.LIGHT_BLOCK).build(null));
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
            registry.register(LibBlockNames.ARCANE_PEDESTAL, new BlockItem(BlockRegistry.ARCANE_PEDESTAL, defaultItemProperties()));
            registry.register(LibBlockNames.ARCANE_BRICKS, new BlockItem(BlockRegistry.ARCANE_BRICKS, defaultItemProperties()));
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
            //registry.register(LibBlockNames.RUNE, new BlockItem(BlockRegistry.RUNE_BLOCK, new Item.Properties()));
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
            registry.register(LibBlockNames.AB_ALTERNATE,getDefaultBlockItem(BlockRegistry.AB_ALTERNATE));
            registry.register(LibBlockNames.AB_BASKET, getDefaultBlockItem(BlockRegistry.AB_BASKET));
            registry.register(LibBlockNames.AB_HERRING, getDefaultBlockItem(BlockRegistry.AB_HERRING));
            registry.register(LibBlockNames.AB_MOSAIC, getDefaultBlockItem(BlockRegistry.AB_MOSAIC));
            registry.register(LibBlockNames.ARCANE_STONE, getDefaultBlockItem(BlockRegistry.ARCANE_STONE));
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
//
            registry.register(LibBlockNames.AB_SMOOTH_SLAB, getDefaultBlockItem(BlockRegistry.AB_SMOOTH_SLAB));
            registry.register(LibBlockNames.AB_SMOOTH, getDefaultBlockItem(BlockRegistry.AB_SMOOTH));
            registry.register(LibBlockNames.AB_CLOVER, getDefaultBlockItem(BlockRegistry.AB_CLOVER));
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
            //ComposterBlock.COMPOSTABLES.put(BlockRegistry.MAGE_BLOOM_CROP.asItem(), 0.3f);

            registry.register(LibBlockNames.POTION_JAR_BLOCK, getDefaultBlockItem(BlockRegistry.POTION_JAR));
            registry.register(LibBlockNames.POTION_MELDER_BLOCK, new RendererBlockItem(BlockRegistry.POTION_MELDER, defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return PotionMelderRenderer::getISTER;
                }
            });

            registry.register(LibBlockNames.AB_SMOOTH_BASKET, getDefaultBlockItem(BlockRegistry.AB_SMOOTH_BASKET));
            registry.register(LibBlockNames.AB_SMOOTH_CLOVER, getDefaultBlockItem(BlockRegistry.AB_SMOOTH_CLOVER));
            registry.register(LibBlockNames.AB_SMOOTH_HERRING, getDefaultBlockItem(BlockRegistry.AB_SMOOTH_HERRING));
            registry.register(LibBlockNames.AB_SMOOTH_MOSAIC, getDefaultBlockItem(BlockRegistry.AB_SMOOTH_MOSAIC));
            registry.register(LibBlockNames.AB_SMOOTH_ALTERNATING, getDefaultBlockItem(BlockRegistry.AB_SMOOTH_ALTERNATING));
            registry.register(LibBlockNames.AB_SMOOTH_ASHLAR, getDefaultBlockItem(BlockRegistry.AB_SMOOTH_ASHLAR));
            registry.register(LibBlockNames.SCONCE, getDefaultBlockItem(BlockRegistry.SCONCE_BLOCK));
            registry.register(LibBlockNames.DRYGMY_STONE, getDefaultBlockItem(BlockRegistry.DRYGMY_BLOCK));

            registry.register(LibBlockNames.AS_GOLD_ALT, getDefaultBlockItem(BlockRegistry.AS_GOLD_ALT));
            registry.register(LibBlockNames.AS_GOLD_ASHLAR, getDefaultBlockItem(BlockRegistry.AS_GOLD_ASHLAR));
            registry.register(LibBlockNames.AS_GOLD_BASKET, getDefaultBlockItem(BlockRegistry.AS_GOLD_BASKET));
            registry.register(LibBlockNames.AS_GOLD_CLOVER, getDefaultBlockItem(BlockRegistry.AS_GOLD_CLOVER));
            registry.register(LibBlockNames.AS_GOLD_HERRING, getDefaultBlockItem(BlockRegistry.AS_GOLD_HERRING));
            registry.register(LibBlockNames.AS_GOLD_MOSAIC, getDefaultBlockItem(BlockRegistry.AS_GOLD_MOSAIC));
            registry.register(LibBlockNames.AS_GOLD_SLAB, getDefaultBlockItem(BlockRegistry.AS_GOLD_SLAB));
            registry.register(LibBlockNames.AS_GOLD_STONE, getDefaultBlockItem(BlockRegistry.AS_GOLD_STONE));
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
        }

        public static ModBlockItem getDefaultBlockItem(Block block){
            return new ModBlockItem(block, defaultItemProperties());
        }

        @SubscribeEvent
        public static void registerBlockProvider(final RegisterEvent event) {
            if(!event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_STATE_PROVIDER_TYPES)) return;

            IForgeRegistry<BlockStateProviderType<?>> registry = Objects.requireNonNull(event.getForgeRegistry());
            registry.register(new ResourceLocation(ArsNouveau.MODID, LibBlockNames.STATE_PROVIDER), new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC));
        }
    }

    private static Boolean allowsSpawnOnLeaves(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

}
