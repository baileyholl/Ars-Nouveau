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
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

@ObjectHolder(ArsNouveau.MODID)
public class BlockRegistry {

    public static BlockBehaviour.Properties LOG_PROP = BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD);

    public static BlockBehaviour.Properties SAP_PROP = BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS);

    @ObjectHolder(LibBlockNames.MAGE_BLOCK) public static MageBlock MAGE_BLOCK;
    @ObjectHolder(LibBlockNames.MAGE_BLOCK) public static BlockEntityType<MageBlockTile> MAGE_BLOCK_TILE;

    @ObjectHolder(LibBlockNames.LIGHT_BLOCK) public static LightBlock LIGHT_BLOCK;
    @ObjectHolder(LibBlockNames.LIGHT_BLOCK) public static BlockEntityType<LightTile> LIGHT_TILE;
    @ObjectHolder(LibBlockNames.AGRONOMIC_SOURCELINK) public static BlockEntityType<AgronomicSourcelinkTile> AGRONOMIC_SOURCELINK_TILE;
    @ObjectHolder(LibBlockNames.AGRONOMIC_SOURCELINK) public static AgronomicSourcelinkBlock AGRONOMIC_SOURCELINK;

    @ObjectHolder(LibBlockNames.ENCHANTING_APPARATUS) public static BlockEntityType<EnchantingApparatusTile> ENCHANTING_APP_TILE;
    @ObjectHolder(LibBlockNames.ENCHANTING_APPARATUS) public static EnchantingApparatusBlock ENCHANTING_APP_BLOCK;

    @ObjectHolder(LibBlockNames.ARCANE_PEDESTAL) public static BlockEntityType<ArcanePedestalTile> ARCANE_PEDESTAL_TILE;
    @ObjectHolder(LibBlockNames.ARCANE_PEDESTAL) public static ArcanePedestal ARCANE_PEDESTAL;
    @ObjectHolder(LibBlockNames.SOURCE_JAR) public static SourceJar SOURCE_JAR;
    @ObjectHolder(LibBlockNames.SOURCE_JAR) public static BlockEntityType<SourceJarTile> SOURCE_JAR_TILE;
    @ObjectHolder(LibBlockNames.RELAY) public static BlockEntityType<RelayTile> ARCANE_RELAY_TILE;

    @ObjectHolder(LibBlockNames.MAGE_BLOOM) public static MageBloomCrop MAGE_BLOOM_CROP;
    @ObjectHolder(LibBlockNames.ARCANE_BRICKS) public static ModBlock ARCANE_BRICKS;
    @ObjectHolder(LibBlockNames.SCRIBES_BLOCK) public static ScribesBlock SCRIBES_BLOCK;
    @ObjectHolder(LibBlockNames.SUMMONING_CRYSTAL) public static SummoningCrystal SUMMONING_CRYSTAL;
    @ObjectHolder(LibBlockNames.SUMMONING_CRYSTAL) public static BlockEntityType<SummoningCrystalTile> SUMMONING_CRYSTAL_TILE;
    @ObjectHolder(LibBlockNames.SCRIBES_BLOCK) public static BlockEntityType<ScribesTile> SCRIBES_TABLE_TILE;
    @ObjectHolder(LibBlockNames.ARCANE_ROAD) public static TickableModBlock ARCANE_ROAD;
    @ObjectHolder(LibBlockNames.RELAY) public static Relay RELAY;
    @ObjectHolder(LibBlockNames.RUNE) public static BlockEntityType<RuneTile> RUNE_TILE;
    @ObjectHolder(LibBlockNames.RUNE) public static RuneBlock RUNE_BLOCK;
    @ObjectHolder(LibBlockNames.PORTAL) public static PortalBlock PORTAL_BLOCK;
    @ObjectHolder(LibBlockNames.PORTAL) public static BlockEntityType<PortalTile> PORTAL_TILE_TYPE;
    @ObjectHolder(LibBlockNames.IMBUEMENT_CHAMBER) public static ImbuementBlock IMBUEMENT_BLOCK;
    @ObjectHolder(LibBlockNames.IMBUEMENT_CHAMBER) public static BlockEntityType<ImbuementTile> IMBUEMENT_TILE;
    @ObjectHolder(LibBlockNames.RELAY_SPLITTER) public static RelaySplitter RELAY_SPLITTER;
    @ObjectHolder(LibBlockNames.RELAY_SPLITTER) public static BlockEntityType<RelaySplitterTile> RELAY_SPLITTER_TILE;
    @ObjectHolder(LibBlockNames.ARCANE_CORE) public static ArcaneCore ARCANE_CORE_BLOCK;
    @ObjectHolder(LibBlockNames.ARCANE_CORE) public static BlockEntityType<ArcaneCoreTile> ARCANE_CORE_TILE;
    @ObjectHolder(LibBlockNames.AB_ALTERNATE) public static ModBlock AB_ALTERNATE;
    @ObjectHolder(LibBlockNames.AB_BASKET) public static ModBlock AB_BASKET;
    @ObjectHolder(LibBlockNames.AB_HERRING) public static ModBlock AB_HERRING;
    @ObjectHolder(LibBlockNames.AB_MOSAIC) public static ModBlock AB_MOSAIC;
    @ObjectHolder(LibBlockNames.ARCANE_STONE) public static ModBlock ARCANE_STONE;
    @ObjectHolder(LibBlockNames.AB_SMOOTH) public static ModBlock AB_SMOOTH;
    @ObjectHolder(LibBlockNames.AB_SMOOTH_SLAB) public static ModBlock AB_SMOOTH_SLAB;
    @ObjectHolder(LibBlockNames.AB_CLOVER) public static ModBlock AB_CLOVER;
    @ObjectHolder(LibBlockNames.AB_SMOOTH_BASKET) public static ModBlock AB_SMOOTH_BASKET;
    @ObjectHolder(LibBlockNames.AB_SMOOTH_CLOVER) public static ModBlock AB_SMOOTH_CLOVER;
    @ObjectHolder(LibBlockNames.AB_SMOOTH_HERRING) public static ModBlock AB_SMOOTH_HERRING;
    @ObjectHolder(LibBlockNames.AB_SMOOTH_MOSAIC) public static ModBlock AB_SMOOTH_MOSAIC;
    @ObjectHolder(LibBlockNames.AB_SMOOTH_ALTERNATING) public static ModBlock AB_SMOOTH_ALTERNATING;
    @ObjectHolder(LibBlockNames.AB_SMOOTH_ASHLAR) public static ModBlock AB_SMOOTH_ASHLAR;
    @ObjectHolder(LibBlockNames.ENCHANTED_SPELL_TURRET) public static EnchantedSpellTurret ENCHANTED_SPELL_TURRET;
    @ObjectHolder(LibBlockNames.ENCHANTED_SPELL_TURRET) public static BlockEntityType<EnchantedTurretTile> ENCHANTED_SPELL_TURRET_TYPE;
    @ObjectHolder(LibBlockNames.REDSTONE_AIR) public static RedstoneAir REDSTONE_AIR;
    @ObjectHolder(LibBlockNames.INTANGIBLE_AIR) public static IntangibleAirBlock INTANGIBLE_AIR;
    @ObjectHolder(LibBlockNames.INTANGIBLE_AIR) public static  BlockEntityType<IntangibleAirTile> INTANGIBLE_AIR_TYPE;

    @ObjectHolder(LibBlockNames.VOLCANIC_SOURCELINK) public static VolcanicSourcelinkBlock VOLCANIC_BLOCK;
    @ObjectHolder(LibBlockNames.VOLCANIC_SOURCELINK) public static  BlockEntityType<VolcanicSourcelinkTile> VOLCANIC_TILE;
    @ObjectHolder(LibBlockNames.LAVA_LILY) public static LavaLily LAVA_LILY;
    @ObjectHolder(LibBlockNames.SOURCEBERRY_BUSH) public static SourceBerryBush SOURCEBERRY_BUSH;

    @ObjectHolder(LibBlockNames.WIXIE_CAULDRON) public static WixieCauldron WIXIE_CAULDRON;
    @ObjectHolder(LibBlockNames.WIXIE_CAULDRON) public static BlockEntityType<WixieCauldronTile> WIXIE_CAULDRON_TYPE;


    @ObjectHolder(LibBlockNames.CREATIVE_SOURCE_JAR) public static CreativeSourceJar CREATIVE_SOURCE_JAR;
    @ObjectHolder(LibBlockNames.CREATIVE_SOURCE_JAR) public static BlockEntityType<CreativeSourceJarTile> CREATIVE_SOURCE_JAR_TILE;

    @ObjectHolder(LibBlockNames.CASCADING_LOG) public static StrippableLog CASCADING_LOG;
    @ObjectHolder(LibBlockNames.CASCADING_LEAVES) public static MagicLeaves CASCADING_LEAVE;
    @ObjectHolder(LibBlockNames.CASCADING_SAPLING) public static SaplingBlock CASCADING_SAPLING;
    @ObjectHolder(LibBlockNames.CASCADING_WOOD) public static StrippableLog CASCADING_WOOD;

    @ObjectHolder(LibBlockNames.BLAZING_LOG) public static StrippableLog BLAZING_LOG;
    @ObjectHolder(LibBlockNames.BLAZING_LEAVES) public static MagicLeaves BLAZING_LEAVES;
    @ObjectHolder(LibBlockNames.BLAZING_SAPLING) public static SaplingBlock BLAZING_SAPLING;
    @ObjectHolder(LibBlockNames.BLAZING_WOOD) public static StrippableLog BLAZING_WOOD;

    @ObjectHolder(LibBlockNames.VEXING_LOG) public static StrippableLog VEXING_LOG;
    @ObjectHolder(LibBlockNames.VEXING_LEAVES) public static MagicLeaves VEXING_LEAVES;
    @ObjectHolder(LibBlockNames.VEXING_SAPLING) public static SaplingBlock VEXING_SAPLING;
    @ObjectHolder(LibBlockNames.VEXING_WOOD) public static StrippableLog VEXING_WOOD;

    @ObjectHolder(LibBlockNames.FLOURISHING_LOG) public static StrippableLog FLOURISHING_LOG;
    @ObjectHolder(LibBlockNames.FLOURISHING_LEAVES) public static MagicLeaves FLOURISHING_LEAVES;
    @ObjectHolder(LibBlockNames.FLOURISHING_SAPLING) public static SaplingBlock FLOURISHING_SAPLING;
    @ObjectHolder(LibBlockNames.FLOURISHING_WOOD) public static StrippableLog FLOURISHING_WOOD;
    @ObjectHolder(LibBlockNames.ARCHWOOD_PLANK) public static ModBlock ARCHWOOD_PLANK;

    @ObjectHolder(LibBlockNames.RITUAL_BRAZIER) public static RitualBrazierBlock RITUAL_BLOCK;
    @ObjectHolder(LibBlockNames.RITUAL_BRAZIER) public static BlockEntityType<RitualBrazierTile> RITUAL_TILE;

    @ObjectHolder(LibBlockNames.ARCHWOOD_BUTTON) public static WoodButtonBlock ARCHWOOD_BUTTON;
    @ObjectHolder(LibBlockNames.ARCHWOOD_STAIRS) public static StairBlock ARCHWOOD_STAIRS;
    @ObjectHolder(LibBlockNames.ARCHWOOD_SLABS) public static SlabBlock ARCHWOOD_SLABS;
   // @ObjectHolder(LibBlockNames.ARCHWOOD_SIGN) public static WallSignBlock ARCHWOOD_SIGN;
    @ObjectHolder(LibBlockNames.ARCHWOOD_FENCE_GATE) public static FenceGateBlock ARCHWOOD_FENCE_GATE;
    @ObjectHolder(LibBlockNames.ARCHWOOD_TRAPDOOR) public static TrapDoorBlock ARCHWOOD_TRAPDOOR;
    @ObjectHolder(LibBlockNames.ARCHWOOD_PPlate) public static PressurePlateBlock ARCHWOOD_PPlate;
    @ObjectHolder(LibBlockNames.ARCHWOOD_FENCE) public static FenceBlock ARCHWOOD_FENCE;
    @ObjectHolder(LibBlockNames.ARCHWOOD_DOOR) public static DoorBlock ARCHWOOD_DOOR;

    @ObjectHolder(LibBlockNames.STRIPPED_AWLOG_BLUE) public static RotatedPillarBlock STRIPPED_AWLOG_BLUE;
    @ObjectHolder(LibBlockNames.STRIPPED_AWWOOD_BLUE) public static RotatedPillarBlock STRIPPED_AWWOOD_BLUE;
    @ObjectHolder(LibBlockNames.STRIPPED_AWLOG_GREEN) public static RotatedPillarBlock STRIPPED_AWLOG_GREEN;
    @ObjectHolder(LibBlockNames.STRIPPED_AWWOOD_GREEN) public static RotatedPillarBlock STRIPPED_AWWOOD_GREEN;
    @ObjectHolder(LibBlockNames.STRIPPED_AWLOG_RED) public static RotatedPillarBlock STRIPPED_AWLOG_RED;
    @ObjectHolder(LibBlockNames.STRIPPED_AWWOOD_RED) public static RotatedPillarBlock STRIPPED_AWWOOD_RED;
    @ObjectHolder(LibBlockNames.STRIPPED_AWLOG_PURPLE) public static RotatedPillarBlock STRIPPED_AWLOG_PURPLE;
    @ObjectHolder(LibBlockNames.STRIPPED_AWWOOD_PURPLE) public static RotatedPillarBlock STRIPPED_AWWOOD_PURPLE;
    @ObjectHolder(LibBlockNames.SOURCE_GEM_BLOCK) public static ModBlock SOURCE_GEM_BLOCK;

    @ObjectHolder(LibBlockNames.POTION_JAR_BLOCK) public static PotionJar POTION_JAR;
    @ObjectHolder(LibBlockNames.POTION_JAR_BLOCK) public static BlockEntityType<PotionJarTile> POTION_JAR_TYPE;
    @ObjectHolder(LibBlockNames.POTION_MELDER_BLOCK) public static PotionMelder POTION_MELDER;
    @ObjectHolder(LibBlockNames.POTION_MELDER_BLOCK) public static BlockEntityType<PotionMelderTile> POTION_MELDER_TYPE;

    @ObjectHolder(LibBlockNames.SCONCE) public static SconceBlock SCONCE_BLOCK;
    @ObjectHolder(LibBlockNames.SCONCE) public static BlockEntityType<SconceTile> SCONCE_TILE;

    @ObjectHolder(LibBlockNames.DRYGMY_STONE) public static DrygmyStone DRYGMY_BLOCK;
    @ObjectHolder(LibBlockNames.DRYGMY_STONE) public static BlockEntityType<DrygmyTile> DRYGMY_TILE;

    @ObjectHolder(LibBlockNames.AS_GOLD_ALT) public static ModBlock AS_GOLD_ALT;
    @ObjectHolder(LibBlockNames.AS_GOLD_ASHLAR) public static ModBlock AS_GOLD_ASHLAR;
    @ObjectHolder(LibBlockNames.AS_GOLD_BASKET) public static ModBlock AS_GOLD_BASKET;
    @ObjectHolder(LibBlockNames.AS_GOLD_CLOVER) public static ModBlock AS_GOLD_CLOVER;
    @ObjectHolder(LibBlockNames.AS_GOLD_HERRING) public static ModBlock AS_GOLD_HERRING;
    @ObjectHolder(LibBlockNames.AS_GOLD_MOSAIC) public static ModBlock AS_GOLD_MOSAIC;
    @ObjectHolder(LibBlockNames.AS_GOLD_SLAB) public static ModBlock AS_GOLD_SLAB;
    @ObjectHolder(LibBlockNames.AS_GOLD_STONE) public static ModBlock AS_GOLD_STONE;

    @ObjectHolder(LibBlockNames.ALCHEMICAL_SOURCELINK) public static AlchemicalSourcelinkBlock ALCHEMICAL_BLOCK;
    @ObjectHolder(LibBlockNames.ALCHEMICAL_SOURCELINK) public static BlockEntityType<AlchemicalSourcelinkTile> ALCHEMICAL_TILE;

    @ObjectHolder(LibBlockNames.VITALIC_SOURCELINK) public static VitalicSourcelinkBlock VITALIC_BLOCK;
    @ObjectHolder(LibBlockNames.VITALIC_SOURCELINK) public static BlockEntityType<VitalicSourcelinkTile> VITALIC_TILE;

    @ObjectHolder(LibBlockNames.MYCELIAL_SOURCELINK) public static MycelialSourcelinkBlock MYCELIAL_BLOCK;
    @ObjectHolder(LibBlockNames.MYCELIAL_SOURCELINK) public static BlockEntityType<MycelialSourcelinkTile> MYCELIAL_TILE;

    @ObjectHolder(LibBlockNames.RELAY_DEPOSIT) public static RelayDepositBlock RELAY_DEPOSIT;
    @ObjectHolder(LibBlockNames.RELAY_DEPOSIT) public static BlockEntityType<RelayDepositTile> RELAY_DEPOSIT_TILE;

    @ObjectHolder(LibBlockNames.RELAY_WARP) public static RelayWarpBlock RELAY_WARP;
    @ObjectHolder(LibBlockNames.RELAY_WARP) public static BlockEntityType<RelayWarpTile> RELAY_WARP_TILE;


    @ObjectHolder(LibBlockNames.BOOKWYRM_LECTERN) public static BookwyrmLectern BOOKWYRM_LECTERN;
    @ObjectHolder(LibBlockNames.BOOKWYRM_LECTERN) public static BlockEntityType<BookwyrmLecternTile> BOOKWYRM_LECTERN_TILE;

    @ObjectHolder(LibBlockNames.BASIC_SPELL_TURRET) public static BasicSpellTurret BASIC_SPELL_TURRET;
    @ObjectHolder(LibBlockNames.BASIC_SPELL_TURRET) public static BlockEntityType<BasicSpellTurretTile> BASIC_SPELL_TURRET_TILE;

    @ObjectHolder(LibBlockNames.TIMER_SPELL_TURRET) public static TimerSpellTurret TIMER_SPELL_TURRET;
    @ObjectHolder(LibBlockNames.TIMER_SPELL_TURRET) public static BlockEntityType<TimerSpellTurretTile> TIMER_SPELL_TURRET_TILE;
    @ObjectHolder(LibBlockNames.ARCHWOOD_CHEST) public static  ArchwoodChest ARCHWOOD_CHEST;
    @ObjectHolder(LibBlockNames.ARCHWOOD_CHEST) public static  BlockEntityType<ArchwoodChestTile> ARCHWOOD_CHEST_TILE;
    @ObjectHolder(LibBlockNames.SPELL_PRISM) public static  SpellPrismBlock SPELL_PRISM;
    @ObjectHolder(LibBlockNames.WHIRLISPRIG_BLOCK) public static BlockEntityType<WhirlisprigTile> WHIRLISPRIG_TILE;
    @ObjectHolder(LibBlockNames.WHIRLISPRIG_BLOCK) public static WhirlisprigFlower WHIRLISPRIG_FLOWER;
    @ObjectHolder(LibBlockNames.RELAY_COLLECTOR) public static RelayCollectorBlock RELAY_COLLECTOR;
    @ObjectHolder(LibBlockNames.RELAY_COLLECTOR) public static BlockEntityType<RelayCollectorTile> RELAY_COLLECTOR_TILE;

    @ObjectHolder(LibBlockNames.RED_SBED) public static SummonBed RED_SBED;
    @ObjectHolder(LibBlockNames.BLUE_SBED) public static SummonBed BLUE_SBED;
    @ObjectHolder(LibBlockNames.GREEN_SBED) public static SummonBed GREEN_SBED;
    @ObjectHolder(LibBlockNames.ORANGE_SBED) public static SummonBed ORANGE_SBED;
    @ObjectHolder(LibBlockNames.YELLOW_SBED) public static SummonBed YELLOW_SBED;
    @ObjectHolder(LibBlockNames.PURPLE_SBED) public static SummonBed PURPLE_SBED;

    @ObjectHolder(LibBlockNames.STATE_PROVIDER) public static BlockStateProviderType stateProviderType;

    @ObjectHolder(LibBlockNames.SCRYERS_OCULUS) public static ScryersOculus SCRYERS_OCULUS;
    @ObjectHolder(LibBlockNames.SCRYERS_OCULUS) public static BlockEntityType<ScryersOculusTile> SCRYERS_OCULUS_TILE;

    @ObjectHolder(LibBlockNames.SCRYERS_CRYSTAL) public static ScryerCrystal SCRYERS_CRYSTAL;
    @ObjectHolder(LibBlockNames.SCRYERS_CRYSTAL) public static BlockEntityType<ScryerCrystalTile> SCRYER_CRYSTAL_TILE;

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();
            registry.register(new MageBlock());
            registry.register(new LightBlock());
            registry.register(new AgronomicSourcelinkBlock());
            registry.register(new SourceJar());
            registry.register(new MageBloomCrop());
            registry.register(new EnchantingApparatusBlock());
            registry.register(new ArcanePedestal());
            registry.register(new ModBlock(LibBlockNames.ARCANE_BRICKS));
            registry.register(new ScribesBlock());
            registry.register(new Relay());
            registry.register(new RuneBlock());
            registry.register(new PortalBlock());
            registry.register(new RelaySplitter());
            registry.register(new ArcaneCore());
            registry.register(new ModBlock(LibBlockNames.AB_ALTERNATE));
            registry.register(new ModBlock(LibBlockNames.ARCANE_STONE));
            registry.register(new ModBlock(LibBlockNames.AB_BASKET));
            registry.register(new ModBlock(LibBlockNames.AB_HERRING));
            registry.register(new ModBlock(LibBlockNames.AB_MOSAIC));
            registry.register(new ImbuementBlock());
            registry.register(new EnchantedSpellTurret());
            registry.register(new RedstoneAir());
            registry.register(new IntangibleAirBlock());
            registry.register(new VolcanicSourcelinkBlock());
            registry.register(new LavaLily());
            registry.register(new SourceBerryBush(BlockBehaviour.Properties.of(Material.PLANT).randomTicks().noCollission().sound(SoundType.SWEET_BERRY_BUSH)));
            registry.register(new SaplingBlock(new MagicTree(() -> WorldEvent.CASCADING_TREE),SAP_PROP).setRegistryName(LibBlockNames.CASCADING_SAPLING));
            registry.register(new SaplingBlock(new MagicTree(() -> WorldEvent.BLAZING_TREE),SAP_PROP).setRegistryName(LibBlockNames.BLAZING_SAPLING));
            registry.register(new SaplingBlock(new MagicTree(() -> WorldEvent.VEXING_TREE), SAP_PROP).setRegistryName(LibBlockNames.VEXING_SAPLING));
            registry.register(new SaplingBlock(new MagicTree(() -> WorldEvent.FLOURISHING_TREE),SAP_PROP).setRegistryName(LibBlockNames.FLOURISHING_SAPLING));
            registry.register(new WixieCauldron());
            registry.register(new CreativeSourceJar());
            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH));
            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH_SLAB));
            registry.register(new ModBlock(LibBlockNames.AB_CLOVER));
            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.CASCADING_LOG, () ->BlockRegistry.STRIPPED_AWLOG_BLUE));
            registry.register(createLeavesBlock().setRegistryName(LibBlockNames.CASCADING_LEAVES));
            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.BLAZING_LOG, () ->BlockRegistry.STRIPPED_AWLOG_RED));
            registry.register(createLeavesBlock().setRegistryName(LibBlockNames.BLAZING_LEAVES));
            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.FLOURISHING_LOG, () ->BlockRegistry.STRIPPED_AWLOG_GREEN));
            registry.register(createLeavesBlock().setRegistryName(LibBlockNames.FLOURISHING_LEAVES));
            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.VEXING_LOG, () ->BlockRegistry.STRIPPED_AWLOG_PURPLE));
            registry.register(createLeavesBlock().setRegistryName(LibBlockNames.VEXING_LEAVES));

            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.VEXING_WOOD, () ->BlockRegistry.STRIPPED_AWWOOD_PURPLE));
            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.CASCADING_WOOD, () ->BlockRegistry.STRIPPED_AWWOOD_BLUE));
            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.FLOURISHING_WOOD, () ->BlockRegistry.STRIPPED_AWWOOD_GREEN));
            registry.register(new StrippableLog(LOG_PROP, LibBlockNames.BLAZING_WOOD, () ->BlockRegistry.STRIPPED_AWWOOD_RED));
            registry.register(new ModBlock(LOG_PROP, LibBlockNames.ARCHWOOD_PLANK));
            registry.register(new RitualBrazierBlock(LibBlockNames.RITUAL_BRAZIER));
            registry.register(new WoodButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(LibBlockNames.ARCHWOOD_BUTTON));
            registry.register(new StairBlock(()-> ARCHWOOD_PLANK.defaultBlockState(),woodProp).setRegistryName(LibBlockNames.ARCHWOOD_STAIRS));
            registry.register(new SlabBlock(woodProp).setRegistryName(LibBlockNames.ARCHWOOD_SLABS));
            registry.register(new FenceGateBlock(woodProp).setRegistryName(LibBlockNames.ARCHWOOD_FENCE_GATE));
            registry.register(new FenceBlock(woodProp).setRegistryName(LibBlockNames.ARCHWOOD_FENCE));
            registry.register(new DoorBlock(woodProp).setRegistryName(LibBlockNames.ARCHWOOD_DOOR));
            registry.register(new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, woodProp).setRegistryName(LibBlockNames.ARCHWOOD_PPlate));
            registry.register(new TrapDoorBlock(woodProp).setRegistryName(LibBlockNames.ARCHWOOD_TRAPDOOR));

            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWLOG_BLUE));
            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWWOOD_BLUE));
            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWLOG_GREEN));
            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWWOOD_GREEN));
            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWLOG_RED));
            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWWOOD_RED));
            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWLOG_PURPLE));
            registry.register(new RotatedPillarBlock(LOG_PROP).setRegistryName(LibBlockNames.STRIPPED_AWWOOD_PURPLE));
            registry.register(new ModBlock(ModBlock.defaultProperties().noOcclusion().lightLevel((s) -> 6),LibBlockNames.SOURCE_GEM_BLOCK));
            registry.register(new PotionJar(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.POTION_JAR_BLOCK));
            registry.register(new PotionMelder(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.POTION_MELDER_BLOCK));

            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH_BASKET));
            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH_CLOVER));
            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH_HERRING));
            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH_MOSAIC));
            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH_ALTERNATING));
            registry.register(new ModBlock(LibBlockNames.AB_SMOOTH_ASHLAR));

            registry.register(new ModBlock(LibBlockNames.AS_GOLD_ALT));
            registry.register(new ModBlock(LibBlockNames.AS_GOLD_ASHLAR));
            registry.register(new ModBlock(LibBlockNames.AS_GOLD_BASKET));
            registry.register(new ModBlock(LibBlockNames.AS_GOLD_CLOVER));
            registry.register(new ModBlock(LibBlockNames.AS_GOLD_HERRING));
            registry.register(new ModBlock(LibBlockNames.AS_GOLD_MOSAIC));
            registry.register(new ModBlock(LibBlockNames.AS_GOLD_SLAB));
            registry.register(new ModBlock(LibBlockNames.AS_GOLD_STONE));

            registry.register(new SconceBlock(LibBlockNames.SCONCE));
            registry.register(new DrygmyStone());
            registry.register(new AlchemicalSourcelinkBlock());
            registry.register(new VitalicSourcelinkBlock());
            registry.register(new MycelialSourcelinkBlock());
            registry.register(new RelayDepositBlock());
            registry.register(new RelayWarpBlock());
            registry.register(new BookwyrmLectern(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.BOOKWYRM_LECTERN));
            registry.register(new BasicSpellTurret());
            registry.register(new TimerSpellTurret());
            registry.register(new ArchwoodChest().setRegistryName(LibBlockNames.ARCHWOOD_CHEST));
            registry.register(new SpellPrismBlock(LibBlockNames.SPELL_PRISM));
            registry.register(new WhirlisprigFlower(LibBlockNames.WHIRLISPRIG_BLOCK));
            registry.register(new RelayCollectorBlock(LibBlockNames.RELAY_COLLECTOR));

            registry.register(new SummonBed(LibBlockNames.RED_SBED));
            registry.register(new SummonBed(LibBlockNames.BLUE_SBED));
            registry.register(new SummonBed(LibBlockNames.GREEN_SBED));
            registry.register(new SummonBed(LibBlockNames.ORANGE_SBED));
            registry.register(new SummonBed(LibBlockNames.YELLOW_SBED));
            registry.register(new SummonBed(LibBlockNames.PURPLE_SBED));
            registry.register(new ScryersOculus(LibBlockNames.SCRYERS_OCULUS));
            registry.register(new ScryerCrystal(LibBlockNames.SCRYERS_CRYSTAL));
        }
        static Block.Properties woodProp = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD);
        public static MagicLeaves createLeavesBlock() {
            return new MagicLeaves(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(
                    BlockRegistry::allowsSpawnOnLeaves).isSuffocating(BlockRegistry::isntSolid).isViewBlocking(BlockRegistry::isntSolid));
        }


        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<BlockEntityType<?>> event){
            IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
            registry.register(BlockEntityType.Builder.of(MageBlockTile::new, BlockRegistry.MAGE_BLOCK).build(null).setRegistryName(LibBlockNames.MAGE_BLOCK));
            registry.register(BlockEntityType.Builder.of(AgronomicSourcelinkTile::new, BlockRegistry.AGRONOMIC_SOURCELINK).build(null).setRegistryName(LibBlockNames.AGRONOMIC_SOURCELINK));
            registry.register(BlockEntityType.Builder.of(SourceJarTile::new, BlockRegistry.SOURCE_JAR).build(null).setRegistryName(LibBlockNames.SOURCE_JAR));
            registry.register(BlockEntityType.Builder.of(LightTile::new, BlockRegistry.LIGHT_BLOCK).build(null).setRegistryName(LibBlockNames.LIGHT_BLOCK));
            registry.register(BlockEntityType.Builder.of(EnchantingApparatusTile::new, BlockRegistry.ENCHANTING_APP_BLOCK).build(null).setRegistryName(LibBlockNames.ENCHANTING_APPARATUS));
            registry.register(BlockEntityType.Builder.of(ArcanePedestalTile::new, BlockRegistry.ARCANE_PEDESTAL).build(null).setRegistryName(LibBlockNames.ARCANE_PEDESTAL));
            registry.register(BlockEntityType.Builder.of(ScribesTile::new, BlockRegistry.SCRIBES_BLOCK).build(null).setRegistryName(LibBlockNames.SCRIBES_BLOCK));
            registry.register(BlockEntityType.Builder.of(RelayTile::new, BlockRegistry.RELAY).build(null).setRegistryName(LibBlockNames.RELAY));
            registry.register(BlockEntityType.Builder.of(RuneTile::new, BlockRegistry.RUNE_BLOCK).build(null).setRegistryName(LibBlockNames.RUNE));
            registry.register(BlockEntityType.Builder.of(PortalTile::new, BlockRegistry.PORTAL_BLOCK).build(null).setRegistryName(LibBlockNames.PORTAL));
            registry.register(BlockEntityType.Builder.of(RelaySplitterTile::new, BlockRegistry.RELAY_SPLITTER).build(null).setRegistryName(LibBlockNames.RELAY_SPLITTER));
            registry.register(BlockEntityType.Builder.of(ArcaneCoreTile::new, BlockRegistry.ARCANE_CORE_BLOCK).build(null).setRegistryName(LibBlockNames.ARCANE_CORE));
            registry.register(BlockEntityType.Builder.of(ImbuementTile::new, BlockRegistry.IMBUEMENT_BLOCK).build(null).setRegistryName(LibBlockNames.IMBUEMENT_CHAMBER));
            registry.register(BlockEntityType.Builder.of(EnchantedTurretTile::new, BlockRegistry.ENCHANTED_SPELL_TURRET).build(null).setRegistryName(LibBlockNames.ENCHANTED_SPELL_TURRET));
            registry.register(BlockEntityType.Builder.of(IntangibleAirTile::new, BlockRegistry.INTANGIBLE_AIR).build(null).setRegistryName(LibBlockNames.INTANGIBLE_AIR));
            registry.register(BlockEntityType.Builder.of(VolcanicSourcelinkTile::new, BlockRegistry.VOLCANIC_BLOCK).build(null).setRegistryName(LibBlockNames.VOLCANIC_SOURCELINK));
            registry.register(BlockEntityType.Builder.of(WixieCauldronTile::new, BlockRegistry.WIXIE_CAULDRON).build(null).setRegistryName(LibBlockNames.WIXIE_CAULDRON));
            registry.register(BlockEntityType.Builder.of(CreativeSourceJarTile::new, BlockRegistry.CREATIVE_SOURCE_JAR).build(null).setRegistryName(LibBlockNames.CREATIVE_SOURCE_JAR));
            registry.register(BlockEntityType.Builder.of(RitualBrazierTile::new, BlockRegistry.RITUAL_BLOCK).build(null).setRegistryName(LibBlockNames.RITUAL_BRAZIER));
            registry.register(BlockEntityType.Builder.of(PotionJarTile::new, BlockRegistry.POTION_JAR).build(null).setRegistryName(LibBlockNames.POTION_JAR_BLOCK));
            registry.register(BlockEntityType.Builder.of(PotionMelderTile::new, BlockRegistry.POTION_MELDER).build(null).setRegistryName(LibBlockNames.POTION_MELDER_BLOCK));
            registry.register(BlockEntityType.Builder.of(SconceTile::new, BlockRegistry.SCONCE_BLOCK).build(null).setRegistryName(LibBlockNames.SCONCE));
            registry.register(BlockEntityType.Builder.of(DrygmyTile::new, BlockRegistry.DRYGMY_BLOCK).build(null).setRegistryName(LibBlockNames.DRYGMY_STONE));
            registry.register(BlockEntityType.Builder.of(AlchemicalSourcelinkTile::new, BlockRegistry.ALCHEMICAL_BLOCK).build(null).setRegistryName(LibBlockNames.ALCHEMICAL_SOURCELINK));
            registry.register(BlockEntityType.Builder.of(VitalicSourcelinkTile::new, BlockRegistry.VITALIC_BLOCK).build(null).setRegistryName(LibBlockNames.VITALIC_SOURCELINK));
            registry.register(BlockEntityType.Builder.of(MycelialSourcelinkTile::new, BlockRegistry.MYCELIAL_BLOCK).build(null).setRegistryName(LibBlockNames.MYCELIAL_SOURCELINK));
            registry.register(BlockEntityType.Builder.of(RelayDepositTile::new, BlockRegistry.RELAY_DEPOSIT).build(null).setRegistryName(LibBlockNames.RELAY_DEPOSIT));
            registry.register(BlockEntityType.Builder.of(RelayWarpTile::new, BlockRegistry.RELAY_WARP).build(null).setRegistryName(LibBlockNames.RELAY_WARP));
            registry.register(BlockEntityType.Builder.of(BookwyrmLecternTile::new, BlockRegistry.BOOKWYRM_LECTERN).build(null).setRegistryName(LibBlockNames.BOOKWYRM_LECTERN));
            registry.register(BlockEntityType.Builder.of(BasicSpellTurretTile::new, BlockRegistry.BASIC_SPELL_TURRET).build(null).setRegistryName(LibBlockNames.BASIC_SPELL_TURRET));
            registry.register(BlockEntityType.Builder.of(TimerSpellTurretTile::new, BlockRegistry.TIMER_SPELL_TURRET).build(null).setRegistryName(LibBlockNames.TIMER_SPELL_TURRET));
            registry.register(BlockEntityType.Builder.of(ArchwoodChestTile::new, BlockRegistry.ARCHWOOD_CHEST).build(null).setRegistryName(LibBlockNames.ARCHWOOD_CHEST));
            registry.register(BlockEntityType.Builder.of(WhirlisprigTile::new, BlockRegistry.WHIRLISPRIG_FLOWER).build(null).setRegistryName(LibBlockNames.WHIRLISPRIG_BLOCK));
            registry.register(BlockEntityType.Builder.of(RelayCollectorTile::new, BlockRegistry.RELAY_COLLECTOR).build(null).setRegistryName(LibBlockNames.RELAY_COLLECTOR));
            registry.register(BlockEntityType.Builder.of(ScryersOculusTile::new, BlockRegistry.SCRYERS_OCULUS).build(null).setRegistryName(LibBlockNames.SCRYERS_OCULUS));
            registry.register(BlockEntityType.Builder.of(ScryerCrystalTile::new, BlockRegistry.SCRYERS_CRYSTAL).build(null).setRegistryName(LibBlockNames.SCRYERS_CRYSTAL));

        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {

            IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
            Item berry = new BlockItem(BlockRegistry.SOURCEBERRY_BUSH, ItemsRegistry.defaultItemProperties().food(ItemsRegistry.SOURCE_BERRY_FOOD)).setRegistryName(LibItemNames.SOURCE_BERRY);
            registry.register(berry);
            registry.register(new BlockItem(BlockRegistry.MAGE_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.MAGE_BLOCK));
            registry.register(new BlockItem(BlockRegistry.LIGHT_BLOCK, new Item.Properties()).setRegistryName(LibBlockNames.LIGHT_BLOCK));
            registry.register(new RendererBlockItem(BlockRegistry.AGRONOMIC_SOURCELINK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return AgronomicRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.AGRONOMIC_SOURCELINK));
            registry.register(new BlockItem(BlockRegistry.SOURCE_JAR, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.SOURCE_JAR));
            registry.register(new BlockItem(BlockRegistry.MAGE_BLOOM_CROP, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.MAGE_BLOOM));
            registry.register(new RendererBlockItem(BlockRegistry.ENCHANTING_APP_BLOCK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return GenericRenderer.getISTER("enchanting_apparatus");
                }
            }.setRegistryName(LibBlockNames.ENCHANTING_APPARATUS));
            registry.register(new BlockItem(BlockRegistry.ARCANE_PEDESTAL, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_PEDESTAL));
            registry.register(new BlockItem(BlockRegistry.ARCANE_BRICKS, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_BRICKS));
            registry.register(new RendererBlockItem(BlockRegistry.SCRIBES_BLOCK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return ScribesRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.SCRIBES_BLOCK));
            registry.register(new RendererBlockItem(BlockRegistry.RELAY, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return GenericRenderer.getISTER("source_relay");
                }
            }.setRegistryName(LibBlockNames.RELAY));
            registry.register(new BlockItem(BlockRegistry.RUNE_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.RUNE));
            registry.register(new BlockItem(BlockRegistry.PORTAL_BLOCK, new Item.Properties()).setRegistryName(LibBlockNames.PORTAL));
            registry.register(new RendererBlockItem(BlockRegistry.RELAY_SPLITTER, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return GenericRenderer.getISTER("source_splitter");
                }
            }.setRegistryName(LibBlockNames.RELAY_SPLITTER));
            registry.register(new RendererBlockItem(BlockRegistry.IMBUEMENT_BLOCK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return GenericRenderer.getISTER("imbuement_chamber");
                }
            }.setRegistryName(LibBlockNames.IMBUEMENT_CHAMBER));
            registry.register(new RendererBlockItem(BlockRegistry.ARCANE_CORE_BLOCK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return ArcaneCoreRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.ARCANE_CORE));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_ALTERNATE, LibBlockNames.AB_ALTERNATE));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_BASKET, LibBlockNames.AB_BASKET));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_HERRING, LibBlockNames.AB_HERRING));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_MOSAIC, LibBlockNames.AB_MOSAIC));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCANE_STONE, LibBlockNames.ARCANE_STONE));
            registry.register(new RendererBlockItem(BlockRegistry.VOLCANIC_BLOCK, ItemsRegistry.defaultItemProperties().fireResistant()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return  VolcanicRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.VOLCANIC_SOURCELINK));
            registry.register(new FluidBlockItem(BlockRegistry.LAVA_LILY, ItemsRegistry.defaultItemProperties().fireResistant()).setRegistryName(LibBlockNames.LAVA_LILY));
            registry.register(new BlockItem(BlockRegistry.WIXIE_CAULDRON, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.WIXIE_CAULDRON));
            registry.register(new BlockItem(BlockRegistry.CREATIVE_SOURCE_JAR, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.CREATIVE_SOURCE_JAR));
            registry.register(new RendererBlockItem(BlockRegistry.RELAY_WARP, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return GenericRenderer.getISTER("source_warp");
                }
            }.setRegistryName(LibBlockNames.RELAY_WARP));
            registry.register(new RendererBlockItem(BlockRegistry.RELAY_DEPOSIT, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return GenericRenderer.getISTER("source_deposit");
                }
            }.setRegistryName(LibBlockNames.RELAY_DEPOSIT));
//
            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH_SLAB, LibBlockNames.AB_SMOOTH_SLAB));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH, LibBlockNames.AB_SMOOTH));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_CLOVER, LibBlockNames.AB_CLOVER));
            registry.register(getDefaultBlockItem(BlockRegistry.CASCADING_LEAVE, LibBlockNames.CASCADING_LEAVES));
            registry.register(getDefaultBlockItem(BlockRegistry.CASCADING_LOG, LibBlockNames.CASCADING_LOG));
            registry.register(getDefaultBlockItem(BlockRegistry.CASCADING_SAPLING, LibBlockNames.CASCADING_SAPLING));
            registry.register(getDefaultBlockItem(BlockRegistry.CASCADING_WOOD, LibBlockNames.CASCADING_WOOD));
            registry.register(getDefaultBlockItem(BlockRegistry.VEXING_LEAVES, LibBlockNames.VEXING_LEAVES));
            registry.register(getDefaultBlockItem(BlockRegistry.VEXING_LOG, LibBlockNames.VEXING_LOG));
            registry.register(getDefaultBlockItem(BlockRegistry.VEXING_SAPLING, LibBlockNames.VEXING_SAPLING));
            registry.register(getDefaultBlockItem(BlockRegistry.VEXING_WOOD, LibBlockNames.VEXING_WOOD));
            registry.register(getDefaultBlockItem(BlockRegistry.FLOURISHING_LEAVES, LibBlockNames.FLOURISHING_LEAVES));
            registry.register(getDefaultBlockItem(BlockRegistry.FLOURISHING_LOG, LibBlockNames.FLOURISHING_LOG));
            registry.register(getDefaultBlockItem(BlockRegistry.FLOURISHING_SAPLING, LibBlockNames.FLOURISHING_SAPLING));
            registry.register(getDefaultBlockItem(BlockRegistry.FLOURISHING_WOOD, LibBlockNames.FLOURISHING_WOOD));
            registry.register(getDefaultBlockItem(BlockRegistry.BLAZING_LEAVES, LibBlockNames.BLAZING_LEAVES));
            registry.register(getDefaultBlockItem(BlockRegistry.BLAZING_LOG, LibBlockNames.BLAZING_LOG));
            registry.register(getDefaultBlockItem(BlockRegistry.BLAZING_SAPLING, LibBlockNames.BLAZING_SAPLING));
            registry.register(getDefaultBlockItem(BlockRegistry.BLAZING_WOOD, LibBlockNames.BLAZING_WOOD));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_PLANK, LibBlockNames.ARCHWOOD_PLANK));
            registry.register(new RendererBlockItem(BlockRegistry.RITUAL_BLOCK,
                    ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return  RitualBrazierRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.RITUAL_BRAZIER));

            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_BUTTON, LibBlockNames.ARCHWOOD_BUTTON));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_STAIRS, LibBlockNames.ARCHWOOD_STAIRS));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_SLABS, LibBlockNames.ARCHWOOD_SLABS));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_FENCE_GATE, LibBlockNames.ARCHWOOD_FENCE_GATE));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_TRAPDOOR, LibBlockNames.ARCHWOOD_TRAPDOOR));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_PPlate, LibBlockNames.ARCHWOOD_PPlate));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_FENCE, LibBlockNames.ARCHWOOD_FENCE));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCHWOOD_DOOR, LibBlockNames.ARCHWOOD_DOOR));

            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_BLUE, LibBlockNames.STRIPPED_AWLOG_BLUE));
            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_BLUE, LibBlockNames.STRIPPED_AWWOOD_BLUE));
            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_GREEN, LibBlockNames.STRIPPED_AWLOG_GREEN));
            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_GREEN, LibBlockNames.STRIPPED_AWWOOD_GREEN));
            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_RED, LibBlockNames.STRIPPED_AWLOG_RED));
            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_RED, LibBlockNames.STRIPPED_AWWOOD_RED));
            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWLOG_PURPLE, LibBlockNames.STRIPPED_AWLOG_PURPLE));
            registry.register(getDefaultBlockItem(BlockRegistry.STRIPPED_AWWOOD_PURPLE, LibBlockNames.STRIPPED_AWWOOD_PURPLE));

            registry.register(getDefaultBlockItem(BlockRegistry.SOURCE_GEM_BLOCK, LibBlockNames.SOURCE_GEM_BLOCK));
            ComposterBlock.COMPOSTABLES.put(BlockRegistry.MAGE_BLOOM_CROP.asItem(), 0.3f);

            registry.register(getDefaultBlockItem(BlockRegistry.POTION_JAR, LibBlockNames.POTION_JAR_BLOCK));
            registry.register(new RendererBlockItem(BlockRegistry.POTION_MELDER, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return PotionMelderRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.POTION_MELDER_BLOCK));

            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH_BASKET, LibBlockNames.AB_SMOOTH_BASKET));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH_CLOVER, LibBlockNames.AB_SMOOTH_CLOVER));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH_HERRING, LibBlockNames.AB_SMOOTH_HERRING));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH_MOSAIC, LibBlockNames.AB_SMOOTH_MOSAIC));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH_ALTERNATING, LibBlockNames.AB_SMOOTH_ALTERNATING));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_SMOOTH_ASHLAR, LibBlockNames.AB_SMOOTH_ASHLAR));
            registry.register(getDefaultBlockItem(BlockRegistry.SCONCE_BLOCK, LibBlockNames.SCONCE));
            registry.register(getDefaultBlockItem(BlockRegistry.DRYGMY_BLOCK, LibBlockNames.DRYGMY_STONE));

            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_ALT, LibBlockNames.AS_GOLD_ALT));
            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_ASHLAR, LibBlockNames.AS_GOLD_ASHLAR));
            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_BASKET, LibBlockNames.AS_GOLD_BASKET));
            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_CLOVER, LibBlockNames.AS_GOLD_CLOVER));
            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_HERRING, LibBlockNames.AS_GOLD_HERRING));
            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_MOSAIC, LibBlockNames.AS_GOLD_MOSAIC));
            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_SLAB, LibBlockNames.AS_GOLD_SLAB));
            registry.register(getDefaultBlockItem(BlockRegistry.AS_GOLD_STONE, LibBlockNames.AS_GOLD_STONE));
            registry.register(new RendererBlockItem(BlockRegistry.ALCHEMICAL_BLOCK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return  AlchemicalRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.ALCHEMICAL_SOURCELINK));
            registry.register(new RendererBlockItem(BlockRegistry.VITALIC_BLOCK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return VitalicRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.VITALIC_SOURCELINK));
            registry.register(new RendererBlockItem(BlockRegistry.MYCELIAL_BLOCK, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return MycelialRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.MYCELIAL_SOURCELINK));
            registry.register(getDefaultBlockItem(BlockRegistry.BOOKWYRM_LECTERN, LibBlockNames.BOOKWYRM_LECTERN));
            registry.register(new RendererBlockItem(BlockRegistry.TIMER_SPELL_TURRET, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return  TimerTurretRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.TIMER_SPELL_TURRET));
            registry.register(new RendererBlockItem(BlockRegistry.BASIC_SPELL_TURRET, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return  BasicTurretRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.BASIC_SPELL_TURRET));
            registry.register(new RendererBlockItem(BlockRegistry.ENCHANTED_SPELL_TURRET, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return ReducerTurretRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.ENCHANTED_SPELL_TURRET));
            registry.register(new ArchwoodChest.Item(BlockRegistry.ARCHWOOD_CHEST, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCHWOOD_CHEST));
            registry.register(getDefaultBlockItem(BlockRegistry.SPELL_PRISM, LibBlockNames.SPELL_PRISM));
            registry.register(new RendererBlockItem(BlockRegistry.WHIRLISPRIG_FLOWER, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return  WhirlisprigFlowerRenderer::getISTER;
                }
            }.setRegistryName(LibBlockNames.WHIRLISPRIG_BLOCK));
            registry.register(new RendererBlockItem(BlockRegistry.RELAY_COLLECTOR, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return GenericRenderer.getISTER("source_collector");
                }
            }.setRegistryName(LibBlockNames.RELAY_COLLECTOR));
            registry.register(getDefaultBlockItem(BlockRegistry.RED_SBED, LibBlockNames.RED_SBED));
            registry.register(getDefaultBlockItem(BlockRegistry.BLUE_SBED, LibBlockNames.BLUE_SBED));
            registry.register(getDefaultBlockItem(BlockRegistry.GREEN_SBED, LibBlockNames.GREEN_SBED));
            registry.register(getDefaultBlockItem(BlockRegistry.YELLOW_SBED, LibBlockNames.YELLOW_SBED));
            registry.register(getDefaultBlockItem(BlockRegistry.PURPLE_SBED, LibBlockNames.PURPLE_SBED));
            registry.register(getDefaultBlockItem(BlockRegistry.ORANGE_SBED, LibBlockNames.ORANGE_SBED));
            registry.register(getDefaultBlockItem(BlockRegistry.SCRYERS_CRYSTAL, LibBlockNames.SCRYERS_CRYSTAL));
            registry.register(new RendererBlockItem(BlockRegistry.SCRYERS_OCULUS, ItemsRegistry.defaultItemProperties()) {
                @Override
                public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                    return ScryerEyeRenderer::getISTER;
                }
            }.withTooltip(Component.translatable("ars_nouveau.tooltip.scryers_oculus").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE))).setRegistryName(LibBlockNames.SCRYERS_OCULUS));
        }

        public static ModBlockItem getDefaultBlockItem(Block block, String registry){
            return (ModBlockItem) new ModBlockItem(block, ItemsRegistry.defaultItemProperties()).setRegistryName(registry);
        }

        @SubscribeEvent
        public static void registerBlockProvider(final RegistryEvent.Register<BlockStateProviderType<?>> e) {
            e.getRegistry().register(new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC).setRegistryName(ArsNouveau.MODID, LibBlockNames.STATE_PROVIDER));
        }
    }

    private static Boolean allowsSpawnOnLeaves(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

}
