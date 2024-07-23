package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.LightBlock;
import com.hollingsworth.arsnouveau.common.block.*;
import com.hollingsworth.arsnouveau.common.block.tile.*;
import com.hollingsworth.arsnouveau.common.items.MobJarItem;
import com.hollingsworth.arsnouveau.common.items.ModBlockItem;
import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTree;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.ITEMS;
import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.defaultItemProperties;

public class BlockRegistry {

    public static Block.Properties woodProp = BlockBehaviour.Properties.of().strength(2.0F, 3.0F).ignitedByLava().mapColor(MapColor.WOOD).sound(SoundType.WOOD);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ArsNouveau.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ArsNouveau.MODID);
    public static final DeferredRegister<BlockStateProviderType<?>> BS_PROVIDERS = DeferredRegister.create(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE, ArsNouveau.MODID);
    public static final Holder<BlockStateProviderType<?>> stateProviderType = BS_PROVIDERS.register(LibBlockNames.STATE_PROVIDER, () -> new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC));

    public static BlockBehaviour.Properties LOG_PROP = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD);
    public static BlockBehaviour.Properties SAP_PROP = BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY);

    public static BlockRegistryWrapper<MageBlock> MAGE_BLOCK = registerBlockAndItem(LibBlockNames.MAGE_BLOCK, MageBlock::new);
    public static BlockEntityTypeRegistryWrapper<MageBlockTile> MAGE_BLOCK_TILE = registerTile(LibBlockNames.MAGE_BLOCK, MageBlockTile::new, MAGE_BLOCK);
    public static BlockRegistryWrapper<LightBlock> LIGHT_BLOCK = registerBlock(LibBlockNames.LIGHT_BLOCK, LightBlock::new);
    public static BlockEntityTypeRegistryWrapper<LightTile> LIGHT_TILE = registerTile(LibBlockNames.LIGHT_BLOCK, LightTile::new, LIGHT_BLOCK);

    public static BlockRegistryWrapper<TempLightBlock> T_LIGHT_BLOCK = registerBlock(LibBlockNames.T_LIGHT_BLOCK, TempLightBlock::new);
    public static BlockEntityTypeRegistryWrapper<TempLightTile> T_LIGHT_TILE = registerTile(LibBlockNames.T_LIGHT_BLOCK, TempLightTile::new, T_LIGHT_BLOCK);
    public static BlockRegistryWrapper<AgronomicSourcelinkBlock> AGRONOMIC_SOURCELINK = registerBlockAndItem(LibBlockNames.AGRONOMIC_SOURCELINK, AgronomicSourcelinkBlock::new);
    public static BlockEntityTypeRegistryWrapper<AgronomicSourcelinkTile> AGRONOMIC_SOURCELINK_TILE = registerTile(LibBlockNames.AGRONOMIC_SOURCELINK, AgronomicSourcelinkTile::new, AGRONOMIC_SOURCELINK);


    public static BlockRegistryWrapper<EnchantingApparatusBlock> ENCHANTING_APP_BLOCK = registerBlockAndItem(LibBlockNames.ENCHANTING_APPARATUS, EnchantingApparatusBlock::new, (reg) -> new RendererBlockItem(reg.get(), defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("enchanting_apparatus");
        }
    });

    public static BlockEntityTypeRegistryWrapper<EnchantingApparatusTile> ENCHANTING_APP_TILE = registerTile(LibBlockNames.ENCHANTING_APPARATUS, EnchantingApparatusTile::new, ENCHANTING_APP_BLOCK);

    public static BlockRegistryWrapper<SourceJar> SOURCE_JAR = registerBlockAndItem(LibBlockNames.SOURCE_JAR, SourceJar::new);
    public static BlockEntityTypeRegistryWrapper<SourceJarTile> SOURCE_JAR_TILE = registerTile(LibBlockNames.SOURCE_JAR, SourceJarTile::new, SOURCE_JAR);

    public static BlockRegistryWrapper<Relay> RELAY = registerBlockAndItem(LibBlockNames.RELAY, Relay::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("source_relay");
        }
    });
    public static BlockEntityTypeRegistryWrapper<RelayTile> ARCANE_RELAY_TILE = registerTile(LibBlockNames.RELAY, RelayTile::new, RELAY);
    public static BlockRegistryWrapper<MageBloomCrop> MAGE_BLOOM_CROP = registerBlockAndItem(LibBlockNames.MAGE_BLOOM, MageBloomCrop::new);
    public static BlockRegistryWrapper<ScribesBlock> SCRIBES_BLOCK = registerBlockAndItem(LibBlockNames.SCRIBES_BLOCK, ScribesBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return ScribesRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<ScribesTile> SCRIBES_TABLE_TILE = registerTile(LibBlockNames.SCRIBES_BLOCK, ScribesTile::new, SCRIBES_BLOCK);
    public static BlockRegistryWrapper<RuneBlock> RUNE_BLOCK = registerBlockAndItem(LibBlockNames.RUNE, RuneBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return RuneRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<RuneTile> RUNE_TILE = registerTile(LibBlockNames.RUNE, RuneTile::new, RUNE_BLOCK);
    public static BlockRegistryWrapper<PortalBlock> PORTAL_BLOCK = registerBlockAndItem(LibBlockNames.PORTAL, PortalBlock::new);
    public static BlockEntityTypeRegistryWrapper<PortalTile> PORTAL_TILE_TYPE = registerTile(LibBlockNames.PORTAL, PortalTile::new, PORTAL_BLOCK);


    public static BlockRegistryWrapper<ImbuementBlock> IMBUEMENT_BLOCK = registerBlockAndItem(LibBlockNames.IMBUEMENT_CHAMBER, ImbuementBlock::new, (reg) -> new RendererBlockItem(BlockRegistry.IMBUEMENT_BLOCK, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("imbuement_chamber");
        }
    });
    public static BlockEntityTypeRegistryWrapper<ImbuementTile> IMBUEMENT_TILE = registerTile(LibBlockNames.IMBUEMENT_CHAMBER, ImbuementTile::new, IMBUEMENT_BLOCK);
    public static BlockRegistryWrapper<RelaySplitter> RELAY_SPLITTER = registerBlockAndItem(LibBlockNames.RELAY_SPLITTER, RelaySplitter::new, (reg) -> new RendererBlockItem(BlockRegistry.RELAY_SPLITTER, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("source_splitter");
        }
    });
    public static BlockEntityTypeRegistryWrapper<RelaySplitterTile> RELAY_SPLITTER_TILE = registerTile(LibBlockNames.RELAY_SPLITTER, RelaySplitterTile::new, RELAY_SPLITTER);
    public static BlockRegistryWrapper<ArcaneCore> ARCANE_CORE_BLOCK = registerBlockAndItem(LibBlockNames.ARCANE_CORE, ArcaneCore::new, (reg) -> new RendererBlockItem(BlockRegistry.ARCANE_CORE_BLOCK, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return ArcaneCoreRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<ArcaneCoreTile> ARCANE_CORE_TILE = registerTile(LibBlockNames.ARCANE_CORE, ArcaneCoreTile::new, ARCANE_CORE_BLOCK);
    public static BlockRegistryWrapper<EnchantedSpellTurret> ENCHANTED_SPELL_TURRET = registerBlockAndItem(LibBlockNames.ENCHANTED_SPELL_TURRET, EnchantedSpellTurret::new, (reg) -> new RendererBlockItem(BlockRegistry.ENCHANTED_SPELL_TURRET, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return ReducerTurretRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<EnchantedTurretTile> ENCHANTED_SPELL_TURRET_TYPE = registerTile(LibBlockNames.ENCHANTED_SPELL_TURRET, EnchantedTurretTile::new, ENCHANTED_SPELL_TURRET);
    public static BlockRegistryWrapper<IntangibleAirBlock> INTANGIBLE_AIR = registerBlock(LibBlockNames.INTANGIBLE_AIR, IntangibleAirBlock::new);
    public static BlockEntityTypeRegistryWrapper<IntangibleAirTile> INTANGIBLE_AIR_TYPE = registerTile(LibBlockNames.INTANGIBLE_AIR, IntangibleAirTile::new, INTANGIBLE_AIR);
    public static BlockRegistryWrapper<VolcanicSourcelinkBlock> VOLCANIC_BLOCK = registerBlockAndItem(LibBlockNames.VOLCANIC_SOURCELINK, VolcanicSourcelinkBlock::new);
    public static BlockEntityTypeRegistryWrapper<VolcanicSourcelinkTile> VOLCANIC_TILE = registerTile(LibBlockNames.VOLCANIC_SOURCELINK, VolcanicSourcelinkTile::new, VOLCANIC_BLOCK);

    public static BlockRegistryWrapper<SourceBerryBush> SOURCEBERRY_BUSH = registerBlockAndItem(LibBlockNames.SOURCEBERRY_BUSH, () -> new SourceBerryBush(BlockBehaviour.Properties.of().randomTicks().noCollission().sound(SoundType.SWEET_BERRY_BUSH)), (reg) -> new BlockItem(reg.get(), defaultItemProperties().food(ItemsRegistry.SOURCE_BERRY_FOOD)));
    public static BlockRegistryWrapper<WixieCauldron> WIXIE_CAULDRON = registerBlockAndItem(LibBlockNames.WIXIE_CAULDRON, WixieCauldron::new);
    public static BlockEntityTypeRegistryWrapper<WixieCauldronTile> WIXIE_CAULDRON_TYPE = registerTile(LibBlockNames.WIXIE_CAULDRON, WixieCauldronTile::new, WIXIE_CAULDRON);
    public static BlockRegistryWrapper<CreativeSourceJar> CREATIVE_SOURCE_JAR = registerBlockAndItem(LibBlockNames.CREATIVE_SOURCE_JAR, CreativeSourceJar::new);
    public static BlockEntityTypeRegistryWrapper<CreativeSourceJarTile> CREATIVE_SOURCE_JAR_TILE = registerTile(LibBlockNames.CREATIVE_SOURCE_JAR, CreativeSourceJarTile::new, CREATIVE_SOURCE_JAR);
    public static BlockRegistryWrapper<StrippableLog> CASCADING_LOG = registerBlockAndItem(LibBlockNames.CASCADING_LOG, () ->  new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWLOG_BLUE));
    public static BlockRegistryWrapper<MagicLeaves> CASCADING_LEAVE = registerBlockAndItem(LibBlockNames.CASCADING_LEAVES, () -> createLeavesBlock(MapColor.COLOR_BLUE));
    public static BlockRegistryWrapper<SaplingBlock> CASCADING_SAPLING = registerBlockAndItem(LibBlockNames.CASCADING_SAPLING, () -> new SaplingBlock(MagicTree.getGrower("cascading_tree", WorldgenRegistry.CONFIGURED_CASCADING_TREE), SAP_PROP));
    public static BlockRegistryWrapper<StrippableLog> CASCADING_WOOD = registerBlockAndItem(LibBlockNames.CASCADING_WOOD, () -> new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWWOOD_BLUE));
    public static BlockRegistryWrapper<StrippableLog> BLAZING_LOG = registerBlockAndItem(LibBlockNames.BLAZING_LOG, () -> new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWLOG_RED));
    public static BlockRegistryWrapper<MagicLeaves> BLAZING_LEAVES = registerBlockAndItem(LibBlockNames.BLAZING_LEAVES, () -> createLeavesBlock(MapColor.COLOR_RED));

    public static BlockRegistryWrapper<SaplingBlock> BLAZING_SAPLING = registerBlockAndItem(LibBlockNames.BLAZING_SAPLING, () -> new SaplingBlock(MagicTree.getGrower("blazing_tree", WorldgenRegistry.CONFIGURED_BLAZING_TREE), SAP_PROP));
    public static BlockRegistryWrapper<StrippableLog> BLAZING_WOOD = registerBlockAndItem(LibBlockNames.BLAZING_WOOD, () -> new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWWOOD_RED));
    public static BlockRegistryWrapper<StrippableLog> VEXING_LOG = registerBlockAndItem(LibBlockNames.VEXING_LOG, () -> new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWLOG_PURPLE));
    public static BlockRegistryWrapper<MagicLeaves> VEXING_LEAVES = registerBlockAndItem(LibBlockNames.VEXING_LEAVES, () -> createLeavesBlock(MapColor.COLOR_PURPLE));
    public static BlockRegistryWrapper<SaplingBlock> VEXING_SAPLING = registerBlockAndItem(LibBlockNames.VEXING_SAPLING, () -> new SaplingBlock(MagicTree.getGrower("vexing_tree", WorldgenRegistry.CONFIGURED_VEXING_TREE), SAP_PROP));
    public static BlockRegistryWrapper<StrippableLog> VEXING_WOOD = registerBlockAndItem(LibBlockNames.VEXING_WOOD, () -> new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWWOOD_PURPLE));
    public static BlockRegistryWrapper<StrippableLog> FLOURISHING_LOG = registerBlockAndItem(LibBlockNames.FLOURISHING_LOG, () -> new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWLOG_GREEN));
    public static BlockRegistryWrapper<MagicLeaves> FLOURISHING_LEAVES = registerBlockAndItem(LibBlockNames.FLOURISHING_LEAVES, () -> createLeavesBlock(MapColor.COLOR_GREEN));
    public static BlockRegistryWrapper<SaplingBlock> FLOURISHING_SAPLING = registerBlockAndItem(LibBlockNames.FLOURISHING_SAPLING, () -> new SaplingBlock(MagicTree.getGrower("flourishing_tree", WorldgenRegistry.CONFIGURED_FLOURISHING_TREE), SAP_PROP));
    public static BlockRegistryWrapper<StrippableLog> FLOURISHING_WOOD = registerBlockAndItem(LibBlockNames.FLOURISHING_WOOD, () -> new StrippableLog(LOG_PROP, BlockRegistry.STRIPPED_AWWOOD_GREEN));
    public static BlockRegistryWrapper<ModBlock> ARCHWOOD_PLANK = registerBlockAndItem(LibBlockNames.ARCHWOOD_PLANK, () -> new ModBlock(LOG_PROP));
    public static BlockRegistryWrapper<ButtonBlock> ARCHWOOD_BUTTON = registerBlockAndItem(LibBlockNames.ARCHWOOD_BUTTON, () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static BlockRegistryWrapper<StairBlock> ARCHWOOD_STAIRS = registerBlockAndItem(LibBlockNames.ARCHWOOD_STAIRS, () ->  new StairBlock(ARCHWOOD_PLANK.defaultBlockState(), woodProp));
    public static BlockRegistryWrapper<SlabBlock> ARCHWOOD_SLABS = registerBlockAndItem(LibBlockNames.ARCHWOOD_SLABS, () -> new SlabBlock(woodProp));
    public static BlockRegistryWrapper<FenceGateBlock> ARCHWOOD_FENCE_GATE = registerBlockAndItem(LibBlockNames.ARCHWOOD_FENCE_GATE, () -> new FenceGateBlock(WoodType.OAK, woodProp));
    public static BlockRegistryWrapper<TrapDoorBlock> ARCHWOOD_TRAPDOOR = registerBlockAndItem(LibBlockNames.ARCHWOOD_TRAPDOOR, () ->  new TrapDoorBlock(BlockSetType.OAK, woodProp));

    public static BlockRegistryWrapper<PressurePlateBlock> ARCHWOOD_PPlate = registerBlockAndItem(LibBlockNames.ARCHWOOD_PRESSURE_PLATE, () -> new PressurePlateBlock(BlockSetType.OAK, woodProp));
    public static BlockRegistryWrapper<FenceBlock> ARCHWOOD_FENCE = registerBlockAndItem(LibBlockNames.ARCHWOOD_FENCE, () -> new FenceBlock(woodProp));
    public static BlockRegistryWrapper<DoorBlock> ARCHWOOD_DOOR = registerBlockAndItem(LibBlockNames.ARCHWOOD_DOOR, () -> new DoorBlock(BlockSetType.OAK, woodProp));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWLOG_BLUE = registerBlockAndItem(LibBlockNames.STRIPPED_AWLOG_BLUE, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWWOOD_BLUE = registerBlockAndItem(LibBlockNames.STRIPPED_AWWOOD_BLUE, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWLOG_GREEN = registerBlockAndItem(LibBlockNames.STRIPPED_AWLOG_GREEN, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWWOOD_GREEN = registerBlockAndItem(LibBlockNames.STRIPPED_AWWOOD_GREEN, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWLOG_RED = registerBlockAndItem(LibBlockNames.STRIPPED_AWLOG_RED, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWWOOD_RED = registerBlockAndItem(LibBlockNames.STRIPPED_AWWOOD_RED, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWLOG_PURPLE = registerBlockAndItem(LibBlockNames.STRIPPED_AWLOG_PURPLE, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<RotatedPillarBlock> STRIPPED_AWWOOD_PURPLE = registerBlockAndItem(LibBlockNames.STRIPPED_AWWOOD_PURPLE, () -> new RotatedPillarBlock(LOG_PROP));
    public static BlockRegistryWrapper<ModBlock> SOURCE_GEM_BLOCK = registerBlockAndItem(LibBlockNames.SOURCE_GEM_BLOCK, () -> new ModBlock(ModBlock.defaultProperties().noOcclusion().lightLevel(s -> 6)));
    public static BlockRegistryWrapper<PotionJar> POTION_JAR = registerBlockAndItem(LibBlockNames.POTION_JAR_BLOCK, PotionJar::new);
    public static BlockEntityTypeRegistryWrapper<PotionJarTile> POTION_JAR_TYPE = registerTile(LibBlockNames.POTION_JAR_BLOCK, PotionJarTile::new, POTION_JAR);
    public static BlockRegistryWrapper<PotionMelder> POTION_MELDER = registerBlockAndItem(LibBlockNames.POTION_MELDER_BLOCK, PotionMelder::new);
    public static BlockEntityTypeRegistryWrapper<PotionMelderTile> POTION_MELDER_TYPE = registerTile(LibBlockNames.POTION_MELDER_BLOCK, PotionMelderTile::new, POTION_MELDER);
    public static BlockRegistryWrapper<SconceBlock> GOLD_SCONCE_BLOCK = registerBlockAndItem(LibBlockNames.SCONCE, SconceBlock::new);
    public static BlockRegistryWrapper<SconceBlock> SOURCESTONE_SCONCE_BLOCK = registerBlockAndItem(LibBlockNames.SOURCESTONE_SCONCE, SconceBlock::new);
    public static BlockRegistryWrapper<SconceBlock> POLISHED_SCONCE_BLOCK = registerBlockAndItem(LibBlockNames.POLISHED_SCONCE, SconceBlock::new);
    public static BlockRegistryWrapper<SconceBlock> ARCHWOOD_SCONCE_BLOCK = registerBlockAndItem(LibBlockNames.ARCHWOOD_SCONCE, SconceBlock::new);
    public static BlockRegistryWrapper<MagicFire> MAGIC_FIRE = registerBlockAndItem(LibBlockNames.MAGIC_FIRE, () -> new MagicFire(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).replaceable().noCollission().instabreak().lightLevel((p_152607_) -> {
        return 15;
    }).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY), 1.0f));

    public static BlockEntityTypeRegistryWrapper<SconceTile> SCONCE_TILE = new BlockEntityTypeRegistryWrapper<>(BLOCK_ENTITIES.register(LibBlockNames.SCONCE, () -> BlockEntityType.Builder.of(SconceTile::new, GOLD_SCONCE_BLOCK.get(), SOURCESTONE_SCONCE_BLOCK.get(), POLISHED_SCONCE_BLOCK.get(), ARCHWOOD_SCONCE_BLOCK.get()).build(null)));
    public static BlockRegistryWrapper<DrygmyStone> DRYGMY_BLOCK = registerBlockAndItem(LibBlockNames.DRYGMY_STONE, DrygmyStone::new);
    public static BlockEntityTypeRegistryWrapper<DrygmyTile> DRYGMY_TILE = registerTile(LibBlockNames.DRYGMY_STONE, DrygmyTile::new, DRYGMY_BLOCK);
    public static BlockRegistryWrapper<AlchemicalSourcelinkBlock> ALCHEMICAL_BLOCK = registerBlockAndItem(LibBlockNames.ALCHEMICAL_SOURCELINK, AlchemicalSourcelinkBlock::new);
    public static BlockEntityTypeRegistryWrapper<AlchemicalSourcelinkTile> ALCHEMICAL_TILE = registerTile(LibBlockNames.ALCHEMICAL_SOURCELINK, AlchemicalSourcelinkTile::new, ALCHEMICAL_BLOCK);
    public static BlockRegistryWrapper<VitalicSourcelinkBlock> VITALIC_BLOCK = registerBlockAndItem(LibBlockNames.VITALIC_SOURCELINK, VitalicSourcelinkBlock::new);
    public static BlockEntityTypeRegistryWrapper<VitalicSourcelinkTile> VITALIC_TILE = registerTile(LibBlockNames.VITALIC_SOURCELINK, VitalicSourcelinkTile::new, VITALIC_BLOCK);
    public static BlockRegistryWrapper<MycelialSourcelinkBlock> MYCELIAL_BLOCK = registerBlockAndItem(LibBlockNames.MYCELIAL_SOURCELINK, MycelialSourcelinkBlock::new);
    public static BlockEntityTypeRegistryWrapper<MycelialSourcelinkTile> MYCELIAL_TILE = registerTile(LibBlockNames.MYCELIAL_SOURCELINK, MycelialSourcelinkTile::new, MYCELIAL_BLOCK);
    public static BlockRegistryWrapper<RelayDepositBlock> RELAY_DEPOSIT = registerBlockAndItem(LibBlockNames.RELAY_DEPOSIT, RelayDepositBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("source_deposit");
        }
    });
    public static BlockEntityTypeRegistryWrapper<RelayDepositTile> RELAY_DEPOSIT_TILE = registerTile(LibBlockNames.RELAY_DEPOSIT, RelayDepositTile::new, RELAY_DEPOSIT);
    public static BlockRegistryWrapper<RelayWarpBlock> RELAY_WARP = registerBlockAndItem(LibBlockNames.RELAY_WARP, RelayWarpBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("source_warp");
        }
    });
    public static BlockEntityTypeRegistryWrapper<RelayWarpTile> RELAY_WARP_TILE = registerTile(LibBlockNames.RELAY_WARP, RelayWarpTile::new, RELAY_WARP);


    public static BlockRegistryWrapper<BasicSpellTurret> BASIC_SPELL_TURRET = registerBlockAndItem(LibBlockNames.BASIC_SPELL_TURRET, BasicSpellTurret::new, (reg) ->new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return BasicTurretRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<BasicSpellTurretTile> BASIC_SPELL_TURRET_TILE = registerTile(LibBlockNames.BASIC_SPELL_TURRET, BasicSpellTurretTile::new, BASIC_SPELL_TURRET);
    public static BlockRegistryWrapper<TimerSpellTurret> TIMER_SPELL_TURRET = registerBlockAndItem(LibBlockNames.TIMER_SPELL_TURRET, TimerSpellTurret::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return TimerTurretRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<TimerSpellTurretTile> TIMER_SPELL_TURRET_TILE = registerTile(LibBlockNames.TIMER_SPELL_TURRET, TimerSpellTurretTile::new, TIMER_SPELL_TURRET);
    public static BlockRegistryWrapper<ArchwoodChest> ARCHWOOD_CHEST = registerBlockAndItem(LibBlockNames.ARCHWOOD_CHEST, ArchwoodChest::new, (reg) -> new ArchwoodChest.Item(reg.get(), defaultItemProperties()));
    public static BlockEntityTypeRegistryWrapper<ArchwoodChestTile> ARCHWOOD_CHEST_TILE = registerTile(LibBlockNames.ARCHWOOD_CHEST, ArchwoodChestTile::new, ARCHWOOD_CHEST);
    public static BlockRegistryWrapper<SpellPrismBlock> SPELL_PRISM = registerBlockAndItem(LibBlockNames.SPELL_PRISM, SpellPrismBlock::new);
    public static BlockRegistryWrapper<WhirlisprigFlower> WHIRLISPRIG_FLOWER = registerBlockAndItem(LibBlockNames.WHIRLISPRIG_BLOCK, WhirlisprigFlower::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return WhirlisprigFlowerRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<WhirlisprigTile> WHIRLISPRIG_TILE = registerTile(LibBlockNames.WHIRLISPRIG_BLOCK, WhirlisprigTile::new, WHIRLISPRIG_FLOWER);
    public static BlockRegistryWrapper<RelayCollectorBlock> RELAY_COLLECTOR = registerBlockAndItem(LibBlockNames.RELAY_COLLECTOR, RelayCollectorBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return GenericRenderer.getISTER("source_collector");
        }
    });
    public static BlockEntityTypeRegistryWrapper<RelayCollectorTile> RELAY_COLLECTOR_TILE = registerTile(LibBlockNames.RELAY_COLLECTOR, RelayCollectorTile::new, RELAY_COLLECTOR);

    public static BlockRegistryWrapper<SummonBed> RED_SBED = registerBlockAndItem(LibBlockNames.RED_SBED, SummonBed::new);
    public static BlockRegistryWrapper<SummonBed> BLUE_SBED = registerBlockAndItem(LibBlockNames.BLUE_SBED, SummonBed::new);
    public static BlockRegistryWrapper<SummonBed> GREEN_SBED = registerBlockAndItem(LibBlockNames.GREEN_SBED, SummonBed::new);
    public static BlockRegistryWrapper<SummonBed> ORANGE_SBED = registerBlockAndItem(LibBlockNames.ORANGE_SBED, SummonBed::new);
    public static BlockRegistryWrapper<SummonBed> YELLOW_SBED = registerBlockAndItem(LibBlockNames.YELLOW_SBED, SummonBed::new);
    public static BlockRegistryWrapper<SummonBed> PURPLE_SBED = registerBlockAndItem(LibBlockNames.PURPLE_SBED, SummonBed::new);


    public static BlockRegistryWrapper<ScryersOculus> SCRYERS_OCULUS = registerBlockAndItem(LibBlockNames.SCRYERS_OCULUS, ScryersOculus::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return ScryerOculusRenderer::getISTER;
        }
    }.withTooltip(Component.translatable("ars_nouveau.tooltip.scryers_oculus").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE))));

    public static BlockEntityTypeRegistryWrapper<ScryersOculusTile> SCRYERS_OCULUS_TILE = registerTile(LibBlockNames.SCRYERS_OCULUS, ScryersOculusTile::new, SCRYERS_OCULUS);
    public static BlockRegistryWrapper<ScryerCrystal> SCRYERS_CRYSTAL = registerBlockAndItem(LibBlockNames.SCRYERS_CRYSTAL, ScryerCrystal::new);
    public static BlockEntityTypeRegistryWrapper<ScryerCrystalTile> SCRYER_CRYSTAL_TILE = registerTile(LibBlockNames.SCRYERS_CRYSTAL, ScryerCrystalTile::new, SCRYERS_CRYSTAL);
    public static BlockRegistryWrapper<ArchfruitPod> MENDOSTEEN_POD = registerBlockAndItem(LibBlockNames.MENDOSTEEN_POD, () -> new ArchfruitPod(() -> FLOURISHING_LOG.get()), (reg) -> new ItemNameBlockItem(reg.get(), defaultItemProperties().food(ItemsRegistry.MENDOSTEEN_FOOD)));
    public static BlockRegistryWrapper<ArchfruitPod> BASTION_POD = registerBlockAndItem(LibBlockNames.BASTION_POD, () -> new ArchfruitPod(() -> VEXING_LOG.get()), (reg) -> new ItemNameBlockItem(reg.get(), defaultItemProperties().food(ItemsRegistry.BASTION_FOOD)));
    public static BlockRegistryWrapper<ArchfruitPod> FROSTAYA_POD = registerBlockAndItem(LibBlockNames.FROSTAYA_POD, () -> new ArchfruitPod(() -> CASCADING_LOG.get()), (reg) -> new ItemNameBlockItem(reg.get(), defaultItemProperties().food(ItemsRegistry.FROSTAYA_FOOD)));
    public static BlockRegistryWrapper<ArchfruitPod> BOMBEGRANTE_POD = registerBlockAndItem(LibBlockNames.BOMBEGRANATE_POD, () -> new ArchfruitPod(() -> BLAZING_LOG.get()), (reg) -> new ItemNameBlockItem(reg.get(), defaultItemProperties().food(ItemsRegistry.BLASTING_FOOD)));
    public static BlockRegistryWrapper<PotionDiffuserBlock> POTION_DIFFUSER = registerBlockAndItem(LibBlockNames.POTION_DIFFUSER, PotionDiffuserBlock::new);
    public static BlockEntityTypeRegistryWrapper<PotionDiffuserTile> POTION_DIFFUSER_TILE = registerTile(LibBlockNames.POTION_DIFFUSER, PotionDiffuserTile::new, POTION_DIFFUSER);
    public static BlockRegistryWrapper<AlterationTable> ALTERATION_TABLE = registerBlockAndItem(LibBlockNames.ALTERATION_TABLE, AlterationTable::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return AlterationTableRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<AlterationTile> ARMOR_TILE = registerTile(LibBlockNames.ALTERATION_TABLE, AlterationTile::new, ALTERATION_TABLE);
    public static BlockRegistryWrapper<MobJar> MOB_JAR = registerBlockAndItem(LibBlockNames.MOB_JAR, MobJar::new, (reg) -> new MobJarItem(reg.get(), defaultItemProperties()));
    public static BlockEntityTypeRegistryWrapper<MobJarTile> MOB_JAR_TILE = registerTile(LibBlockNames.MOB_JAR, MobJarTile::new, MOB_JAR);
    public static BlockRegistryWrapper<VoidPrism> VOID_PRISM = registerBlockAndItem(LibBlockNames.VOID_PRISM, VoidPrism::new);
    public static BlockRegistryWrapper<RepositoryBlock> REPOSITORY = registerBlockAndItem(LibBlockNames.REPOSITORY, RepositoryBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return RepositoryRenderer::getISTER;
        }
    });
    public static BlockEntityTypeRegistryWrapper<RepositoryTile> REPOSITORY_TILE = registerTile(LibBlockNames.REPOSITORY, RepositoryTile::new, REPOSITORY);
    public static BlockRegistryWrapper<FalseWeave> FALSE_WEAVE = registerBlockAndItem(LibBlockNames.FALSE_WEAVE, FalseWeave::new);
    public static BlockEntityTypeRegistryWrapper<FalseWeaveTile> FALSE_WEAVE_TILE = registerTile(LibBlockNames.FALSE_WEAVE, FalseWeaveTile::new, FALSE_WEAVE);
    public static BlockRegistryWrapper<MirrorWeave> MIRROR_WEAVE = registerBlockAndItem(LibBlockNames.MIRROR_WEAVE, MirrorWeave::new);
    public static BlockEntityTypeRegistryWrapper<MirrorWeaveTile> MIRROR_WEAVE_TILE = registerTile(LibBlockNames.MIRROR_WEAVE, MirrorWeaveTile::new, MIRROR_WEAVE);
    public static BlockRegistryWrapper<GhostWeave> GHOST_WEAVE = registerBlockAndItem(LibBlockNames.GHOST_WEAVE, GhostWeave::new);
    public static BlockEntityTypeRegistryWrapper<GhostWeaveTile> GHOST_WEAVE_TILE = registerTile(LibBlockNames.GHOST_WEAVE, GhostWeaveTile::new, GHOST_WEAVE);
    public static BlockRegistryWrapper<ModBlock> MAGEBLOOM_BLOCK = registerBlockAndItem(LibBlockNames.MAGEBLOOM_BLOCK, () -> new ModBlock(BlockBehaviour.Properties.of().strength(0.1F).sound(SoundType.WOOL)));
    public static final BlockRegistryWrapper<Block> ROTATING_TURRET = registerBlockAndItem(LibBlockNames.ROTATING_SPELL_TURRET, RotatingSpellTurret::new, (reg) ->  new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return BasicTurretRenderer::getISTER;
        }
    }.withTooltip(Component.translatable("ars_nouveau.turret.tooltip")));

    public static final BlockEntityTypeRegistryWrapper<RotatingTurretTile> ROTATING_TURRET_TILE = registerTile(LibBlockNames.ROTATING_SPELL_TURRET, RotatingTurretTile::new, ROTATING_TURRET);
    public static final BlockRegistryWrapper<ArcanePlatform> ARCANE_PLATFORM = registerBlockAndItem(LibBlockNames.MINI_PEDESTAL, ArcanePlatform::new, (reg) -> new ModBlockItem(reg.get(), defaultItemProperties()).withTooltip(Component.translatable("ars_nouveau.arcane_platform.tooltip")));
    public static final BlockRegistryWrapper<MagelightTorch> MAGELIGHT_TORCH = registerBlockAndItem(LibBlockNames.MAGELIGHT_TORCH, MagelightTorch::new);
    public static final BlockRegistryWrapper<BrazierRelay> BRAZIER_RELAY = registerBlockAndItem(LibBlockNames.BRAZIER_RELAY, BrazierRelay::new);

    public static final BlockRegistryWrapper<CraftingLecternBlock> CRAFTING_LECTERN = registerBlockAndItem(LibBlockNames.STORAGE_LECTERN, CraftingLecternBlock::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return LecternRenderer::getISTER;
        }
    });
    public static final BlockRegistryWrapper<ArcanePedestal> ARCANE_PEDESTAL = registerBlockAndItem(LibBlockNames.ARCANE_PEDESTAL, ArcanePedestal::new);

    public static final BlockEntityTypeRegistryWrapper<ArcanePedestalTile> ARCANE_PEDESTAL_TILE = new BlockEntityTypeRegistryWrapper<>(BLOCK_ENTITIES.register(LibBlockNames.ARCANE_PEDESTAL, () -> BlockEntityType.Builder.of(ArcanePedestalTile::new, ARCANE_PEDESTAL.get(), ARCANE_PLATFORM.get()).build(null)));
    public static final BlockEntityTypeRegistryWrapper<MagelightTorchTile> MAGELIGHT_TORCH_TILE = registerTile(LibBlockNames.MAGELIGHT_TORCH, MagelightTorchTile::new, MAGELIGHT_TORCH);

    public static final BlockRegistryWrapper<RitualBrazierBlock> RITUAL_BLOCK = registerBlockAndItem(LibBlockNames.RITUAL_BRAZIER, RitualBrazierBlock::new);
    public static final BlockRegistryWrapper<SkyWeave> SKY_WEAVE = registerBlockAndItem(LibBlockNames.SKY_WEAVE, () ->  new SkyWeave(Block.Properties.of().strength(0.1F).sound(SoundType.WOOL).noOcclusion()));
    public static final BlockRegistryWrapper<TemporaryBlock> TEMPORARY_BLOCK = registerBlock(LibBlockNames.TEMPORARY_BLOCK, () -> new TemporaryBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).sound(SoundType.STONE)));
    public static final BlockRegistryWrapper<ItemDetector> ITEM_DETECTOR = registerBlockAndItem(LibBlockNames.ITEM_DETECTOR, ItemDetector::new);
    public static BlockRegistryWrapper<SpellSensor> SPELL_SENSOR = registerBlockAndItem(LibBlockNames.SPELL_SENSOR, SpellSensor::new);
    public static BlockRegistryWrapper<RedstoneRelay> REDSTONE_RELAY = registerBlockAndItem(LibBlockNames.REDSTONE_RELAY, RedstoneRelay::new, (reg) -> new RendererBlockItem(reg, defaultItemProperties()) {
        @Override
        public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
            return RedstoneRelayRenderer::getISTER;
        }
    });
    public static final BlockRegistryWrapper<ModBlock> SOURCEBERRY_SACK = registerBlockAndItem(LibBlockNames.SOURCEBERRY_SACK, () -> new ModBlock(BlockBehaviour.Properties.of().strength(0.1F).sound(SoundType.WOOL)));

    public static final BlockEntityTypeRegistryWrapper<RitualBrazierTile> RITUAL_TILE = registerTile(LibBlockNames.RITUAL_BRAZIER, RitualBrazierTile::new, RITUAL_BLOCK);
    public static final BlockEntityTypeRegistryWrapper<BrazierRelayTile> BRAZIER_RELAY_TILE = registerTile(LibBlockNames.BRAZIER_RELAY, BrazierRelayTile::new, BRAZIER_RELAY);
    public static final BlockEntityTypeRegistryWrapper<SkyBlockTile> SKYWEAVE_TILE = registerTile(LibBlockNames.SKY_WEAVE, SkyBlockTile::new, SKY_WEAVE);
    public static final BlockEntityTypeRegistryWrapper<TemporaryTile> TEMPORARY_TILE = registerTile(LibBlockNames.TEMPORARY_BLOCK, TemporaryTile::new, TEMPORARY_BLOCK);
    public static final BlockEntityTypeRegistryWrapper<CraftingLecternTile> CRAFTING_LECTERN_TILE = registerTile(LibBlockNames.STORAGE_LECTERN, CraftingLecternTile::new, CRAFTING_LECTERN);
    public static final BlockEntityTypeRegistryWrapper<ItemDetectorTile> ITEM_DETECTOR_TILE = registerTile(LibBlockNames.ITEM_DETECTOR, ItemDetectorTile::new, ITEM_DETECTOR);
    public static final BlockEntityTypeRegistryWrapper<SpellSensorTile> SPELL_SENSOR_TILE = registerTile(LibBlockNames.SPELL_SENSOR, SpellSensorTile::new, SPELL_SENSOR);
    public static final BlockEntityTypeRegistryWrapper<RedstoneRelayTile> REDSTONE_RELAY_TILE = registerTile(LibBlockNames.REDSTONE_RELAY, RedstoneRelayTile::new, REDSTONE_RELAY);


    public static void onBlocksRegistry() {
        for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
            if (LibBlockNames.DIRECTIONAL_SOURCESTONE.contains(s)) {
                BLOCKS.register(s, () -> new DirectionalModBlock());
            } else {
                BLOCKS.register(s, () -> new ModBlock());
            }
        }
        for(String s : LibBlockNames.DECORATIVE_SLABS){
            BLOCKS.register(s, () -> new SlabBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).sound(SoundType.STONE)));
        }
        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
            BLOCKS.register(s + "_stairs", () -> new StairBlock(BuiltInRegistries.BLOCK.get(ArsNouveau.prefix( s)).defaultBlockState(), BlockBehaviour.Properties.of().strength(1.5F, 6.0F).sound(SoundType.STONE)));
        }
        BLOCKS.register(LibBlockNames.Pot(LibBlockNames.MAGE_BLOOM), () -> createPottedBlock(() -> MAGE_BLOOM_CROP.get()));
        BLOCKS.register(LibBlockNames.Pot(LibBlockNames.BLAZING_SAPLING), () -> createPottedBlock(() -> BLAZING_SAPLING.get()));
        BLOCKS.register(LibBlockNames.Pot(LibBlockNames.CASCADING_SAPLING), () -> createPottedBlock(() -> CASCADING_SAPLING.get()));
        BLOCKS.register(LibBlockNames.Pot(LibBlockNames.FLOURISHING_SAPLING), () -> createPottedBlock(() -> FLOURISHING_SAPLING.get()));
        BLOCKS.register(LibBlockNames.Pot(LibBlockNames.VEXING_SAPLING), () -> createPottedBlock(() -> VEXING_SAPLING.get()));

    }

    public static MagicLeaves createLeavesBlock(MapColor color) {
        return new MagicLeaves(BlockBehaviour.Properties.of().mapColor(color).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(
                BlockRegistry::allowsSpawnOnLeaves).isSuffocating(BlockRegistry::isntSolid).isViewBlocking(BlockRegistry::isntSolid).pushReaction(PushReaction.DESTROY).isRedstoneConductor(BlockRegistry::isntSolid).ignitedByLava());
    }


    public static void onBlockItemsRegistry() {
        for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
            ITEMS.register(s, () -> getDefaultBlockItem(BuiltInRegistries.BLOCK.get(ArsNouveau.prefix( s))));
        }

        for (String s : LibBlockNames.DECORATIVE_STAIRS) {
            ITEMS.register(s, () -> getDefaultBlockItem(BuiltInRegistries.BLOCK.get(ArsNouveau.prefix( s))));
        }

        for (String s : LibBlockNames.DECORATIVE_SLABS) {
            ITEMS.register(s, () -> getDefaultBlockItem(BuiltInRegistries.BLOCK.get(ArsNouveau.prefix( s))));
        }
    }

    public static ModBlockItem getDefaultBlockItem(Block block) {
        return new ModBlockItem(block, defaultItemProperties());
    }

    private static Boolean allowsSpawnOnLeaves(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }


    public static Block getBlock(String s) {
        return BuiltInRegistries.BLOCK.get(ArsNouveau.prefix( s));
    }

    public static <T extends Block> BlockRegistryWrapper<T> registerBlock(String name, Supplier<T> blockSupp) {
        return new BlockRegistryWrapper<>(BLOCKS.register(name, blockSupp));
    }

    public static <T extends Block> BlockRegistryWrapper<T> registerBlockAndItem(String name, Supplier<T> blockSupp) {
        BlockRegistryWrapper<T> blockReg = new BlockRegistryWrapper<>(BLOCKS.register(name, blockSupp));
        ITEMS.register(name, () -> getDefaultBlockItem(blockReg.get()));
        return blockReg;
    }

    public static <T extends Block> BlockRegistryWrapper<T> registerBlockAndItem(String name, Supplier<T> blockSupp, Function<BlockRegistryWrapper<T>, Item> blockItemFunc) {
        BlockRegistryWrapper<T> blockReg = new BlockRegistryWrapper<>(BLOCKS.register(name, blockSupp));
        ITEMS.register(name, () -> blockItemFunc.apply(blockReg));
        return blockReg;
    }

    public static final Map<Supplier<ResourceLocation>, FlowerPotBlock> flowerPots = new HashMap<>();

    public static FlowerPotBlock createPottedBlock(Supplier<? extends Block> block) {
        FlowerPotBlock pot = new FlowerPotBlock(() -> (FlowerPotBlock)Blocks.FLOWER_POT, block, BlockBehaviour.Properties.of().instabreak().noOcclusion());
        flowerPots.put(() -> BuiltInRegistries.BLOCK.getKey(block.get()), pot);
        return pot;
    }

    public static <T extends BlockEntity> BlockEntityTypeRegistryWrapper<T> registerTile(String regName, BlockEntityType.BlockEntitySupplier<T> tile, BlockRegistryWrapper<? extends Block> block){
        return new BlockEntityTypeRegistryWrapper<>(BLOCK_ENTITIES.register(regName, () -> BlockEntityType.Builder.of(tile, block.get()).build(null)));
    }
}
