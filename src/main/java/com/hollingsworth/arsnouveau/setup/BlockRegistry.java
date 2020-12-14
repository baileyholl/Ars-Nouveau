package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.*;
import com.hollingsworth.arsnouveau.common.block.*;
import com.hollingsworth.arsnouveau.common.block.tile.*;
import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import com.hollingsworth.arsnouveau.common.items.FluidBlockItem;
import com.hollingsworth.arsnouveau.common.items.VolcanicAccumulatorBI;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTree;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static net.minecraft.world.biome.Biome.LOGGER;
@ObjectHolder(ArsNouveau.MODID)
public class BlockRegistry {

    @ObjectHolder(ArsNouveau.MODID + ":phantom_block")
    public static PhantomBlock PHANTOM_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":light_block")
    public static LightBlock LIGHT_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":light_block")
    public static TileEntityType<LightTile> LIGHT_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":phantom_block")
    public static TileEntityType<PhantomBlockTile> PHANTOM_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":mana_condenser")
    public static TileEntityType<ManaCondenserTile> MANA_CONDENSER_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":enchanting_apparatus")
    public static TileEntityType<EnchantingApparatusTile> ENCHANTING_APP_TILE;


    @ObjectHolder(ArsNouveau.MODID + ":glyph_press")
    public static TileEntityType<GlyphPressTile> GLYPH_PRESS_TILE;

    @ObjectHolder(LibBlockNames.ARCANE_PEDESTAL)
    public static TileEntityType<ArcanePedestalTile> ARCANE_PEDESTAL_TILE;



    @ObjectHolder(ArsNouveau.MODID + ":mana_condenser")
    public static ManaCondenserBlock MANA_CONDENSER;

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar")
    public static ManaJar MANA_JAR;

    @ObjectHolder(LibBlockNames.ARCANE_PEDESTAL) public static ArcanePedestal ARCANE_PEDESTAL;

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar")
    public static TileEntityType<ManaJarTile> MANA_JAR_TILE;
    @ObjectHolder(LibBlockNames.ARCANE_RELAY)
    public static TileEntityType<ArcaneRelayTile> ARCANE_RELAY_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":warding_stone") public static WardBlock WARD_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":glyph_press") public static GlyphPressBlock GLYPH_PRESS_BLOCK;

    @ObjectHolder("arcane_ore") public static ArcaneOre ARCANE_ORE;

    @ObjectHolder(ArsNouveau.MODID + ":mana_bloom_crop") public static ManaBloomCrop MANA_BLOOM_CROP;

    @ObjectHolder(ArsNouveau.MODID + ":enchanting_apparatus") public static EnchantingApparatusBlock ENCHANTING_APP_BLOCK;

    @ObjectHolder(LibBlockNames.ARCANE_BRICKS) public static ModBlock ARCANE_BRICKS;


    @ObjectHolder(LibBlockNames.SCRIBES_BLOCK) public static ScribesBlock SCRIBES_BLOCK;

    @ObjectHolder(LibBlockNames.SUMMONING_CRYSTAL) public static SummoningCrystal SUMMONING_CRYSTAL;

    @ObjectHolder(LibBlockNames.SUMMONING_CRYSTAL) public static TileEntityType<SummoningCrytalTile> SUMMONING_CRYSTAL_TILE;

    @ObjectHolder(LibBlockNames.SCRIBES_BLOCK) public static TileEntityType<ScribesTile> SCRIBES_TABLE_TILE;

    @ObjectHolder(LibBlockNames.ARCANE_ROAD) public static ModBlock ARCANE_ROAD;

    @ObjectHolder(LibBlockNames.ARCANE_RELAY) public static ArcaneRelay ARCANE_RELAY;

    @ObjectHolder(LibBlockNames.RUNE) public static TileEntityType<RuneTile> RUNE_TILE;
    @ObjectHolder(LibBlockNames.RUNE) public static RuneBlock RUNE_BLOCK;
    @ObjectHolder(LibBlockNames.PORTAL) public static PortalBlock PORTAL_BLOCK;

    @ObjectHolder(LibBlockNames.PORTAL) public static TileEntityType<PortalTile> PORTAL_TILE_TYPE;
    @ObjectHolder(LibBlockNames.CRYSTALLIZER) public static CrystallizerBlock CRYSTALLIZER_BLOCK;
    @ObjectHolder(LibBlockNames.CRYSTALLIZER) public static TileEntityType<CrystallizerTile> CRYSTALLIZER_TILE;

    @ObjectHolder(LibBlockNames.ARCANE_RELAY_SPLITTER) public static ArcaneRelaySplitter ARCANE_RELAY_SPLITTER;
    @ObjectHolder(LibBlockNames.ARCANE_RELAY_SPLITTER) public static TileEntityType<ArcaneRelaySplitterTile> ARCANE_RELAY_SPLITTER_TILE;
    @ObjectHolder(LibBlockNames.ARCANE_CORE) public static ArcaneCore ARCANE_CORE_BLOCK;
    @ObjectHolder(LibBlockNames.ARCANE_CORE) public static TileEntityType<ArcaneCoreTile> ARCANE_CORE_TILE;

    @ObjectHolder(LibBlockNames.AB_ALTERNATE) public static ModBlock AB_ALTERNATE;
    @ObjectHolder(LibBlockNames.AB_BASKET) public static ModBlock AB_BASKET;
    @ObjectHolder(LibBlockNames.AB_HERRING) public static ModBlock AB_HERRING;
    @ObjectHolder(LibBlockNames.AB_MOSAIC) public static ModBlock AB_MOSAIC;
    @ObjectHolder(LibBlockNames.ARCANE_STONE) public static ModBlock ARCANE_STONE;

    @ObjectHolder(LibBlockNames.SPELL_TURRET) public static SpellTurret SPELL_TURRET;
    @ObjectHolder(LibBlockNames.SPELL_TURRET) public static TileEntityType<SpellTurretTile> SPELL_TURRET_TYPE;
    @ObjectHolder(LibBlockNames.REDSTONE_AIR) public static RedstoneAir REDSTONE_AIR;
    @ObjectHolder(LibBlockNames.INTANGIBLE_AIR) public static IntangibleAirBlock INTANGIBLE_AIR;
    @ObjectHolder(LibBlockNames.INTANGIBLE_AIR) public static  TileEntityType<IntangibleAirTile> INTANGIBLE_AIR_TYPE;


    @ObjectHolder(LibBlockNames.VOLCANIC_ACCUMULATOR) public static VolcanicAccumulator VOLCANIC_BLOCK;
    @ObjectHolder(LibBlockNames.VOLCANIC_ACCUMULATOR) public static  TileEntityType<VolcanicTile> VOLCANIC_TILE;
    @ObjectHolder(LibBlockNames.LAVA_LILY) public static LavaLily LAVA_LILY;
    @ObjectHolder(LibBlockNames.MANA_BERRY_BUSH) public static ManaBerryBush MANA_BERRY_BUSH;
    @ObjectHolder("magic_sapling") public static SaplingBlock MAGIC_SAPLING;
    @ObjectHolder(LibBlockNames.WIXIE_CAULDRON) public static WixieCauldron WIXIE_CAULDRON;
    @ObjectHolder(LibBlockNames.WIXIE_CAULDRON) public static TileEntityType<WixieCauldronTile> WIXIE_CAULDRON_TYPE;

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            blockRegistryEvent.getRegistry().register(new PhantomBlock());
            blockRegistryEvent.getRegistry().register(new LightBlock());
            blockRegistryEvent.getRegistry().register(new ManaCondenserBlock());
            blockRegistryEvent.getRegistry().register(new ManaJar());
            blockRegistryEvent.getRegistry().register(new WardBlock());
            blockRegistryEvent.getRegistry().register(new GlyphPressBlock());
            blockRegistryEvent.getRegistry().register(new ArcaneOre());
            blockRegistryEvent.getRegistry().register(new ManaBloomCrop());
            blockRegistryEvent.getRegistry().register(new EnchantingApparatusBlock());
            blockRegistryEvent.getRegistry().register(new ArcanePedestal());
            blockRegistryEvent.getRegistry().register(new SummoningCrystal());
            blockRegistryEvent.getRegistry().register(new ModBlock(LibBlockNames.ARCANE_BRICKS));
            blockRegistryEvent.getRegistry().register(new ScribesBlock());
            blockRegistryEvent.getRegistry().register(new ArcaneRoad());
            blockRegistryEvent.getRegistry().register(new ArcaneRelay());
            blockRegistryEvent.getRegistry().register(new RuneBlock());
            blockRegistryEvent.getRegistry().register(new PortalBlock());
            blockRegistryEvent.getRegistry().register(new ArcaneRelaySplitter());
            blockRegistryEvent.getRegistry().register(new ArcaneCore());
            blockRegistryEvent.getRegistry().register(new ModBlock(LibBlockNames.AB_ALTERNATE));
            blockRegistryEvent.getRegistry().register(new ModBlock(LibBlockNames.ARCANE_STONE));
            blockRegistryEvent.getRegistry().register(new ModBlock(LibBlockNames.AB_BASKET));
            blockRegistryEvent.getRegistry().register(new ModBlock(LibBlockNames.AB_HERRING));
            blockRegistryEvent.getRegistry().register(new ModBlock(LibBlockNames.AB_MOSAIC));
            blockRegistryEvent.getRegistry().register(new CrystallizerBlock());
            blockRegistryEvent.getRegistry().register(new SpellTurret());
            blockRegistryEvent.getRegistry().register(new RedstoneAir());
            blockRegistryEvent.getRegistry().register(new IntangibleAirBlock());
            blockRegistryEvent.getRegistry().register(new VolcanicAccumulator());
            blockRegistryEvent.getRegistry().register(new LavaLily());
            blockRegistryEvent.getRegistry().register(new ManaBerryBush(AbstractBlock.Properties.create(Material.PLANTS).tickRandomly().doesNotBlockMovement().sound(SoundType.SWEET_BERRY_BUSH)));
            blockRegistryEvent.getRegistry().register(new SaplingBlock(new MagicTree(),ModBlock.defaultProperties()).setRegistryName("magic_sapling"));
            blockRegistryEvent.getRegistry().register(new WixieCauldron());
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
            event.getRegistry().register(TileEntityType.Builder.create(PhantomBlockTile::new, BlockRegistry.PHANTOM_BLOCK).build(null).setRegistryName("phantom_block"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaCondenserTile::new, BlockRegistry.MANA_CONDENSER).build(null).setRegistryName("mana_condenser"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaJarTile::new, BlockRegistry.MANA_JAR).build(null).setRegistryName("mana_jar"));
            event.getRegistry().register(TileEntityType.Builder.create(LightTile::new, BlockRegistry.LIGHT_BLOCK).build(null).setRegistryName("light_block"));
            event.getRegistry().register(TileEntityType.Builder.create(GlyphPressTile::new, BlockRegistry.GLYPH_PRESS_BLOCK).build(null).setRegistryName("glyph_press"));
            event.getRegistry().register(TileEntityType.Builder.create(EnchantingApparatusTile::new, BlockRegistry.ENCHANTING_APP_BLOCK).build(null).setRegistryName("enchanting_apparatus"));
            event.getRegistry().register(TileEntityType.Builder.create(ArcanePedestalTile::new, BlockRegistry.ARCANE_PEDESTAL).build(null).setRegistryName(LibBlockNames.ARCANE_PEDESTAL));
            event.getRegistry().register(TileEntityType.Builder.create(SummoningCrytalTile::new, BlockRegistry.SUMMONING_CRYSTAL).build(null).setRegistryName(LibBlockNames.SUMMONING_CRYSTAL));
            event.getRegistry().register(TileEntityType.Builder.create(ScribesTile::new, BlockRegistry.SCRIBES_BLOCK).build(null).setRegistryName(LibBlockNames.SCRIBES_BLOCK));
            event.getRegistry().register(TileEntityType.Builder.create(ArcaneRelayTile::new, BlockRegistry.ARCANE_RELAY).build(null).setRegistryName(LibBlockNames.ARCANE_RELAY));
            event.getRegistry().register(TileEntityType.Builder.create(RuneTile::new, BlockRegistry.RUNE_BLOCK).build(null).setRegistryName(LibBlockNames.RUNE));
            event.getRegistry().register(TileEntityType.Builder.create(PortalTile::new, BlockRegistry.PORTAL_BLOCK).build(null).setRegistryName(LibBlockNames.PORTAL));
            event.getRegistry().register(TileEntityType.Builder.create(ArcaneRelaySplitterTile::new, BlockRegistry.ARCANE_RELAY_SPLITTER).build(null).setRegistryName(LibBlockNames.ARCANE_RELAY_SPLITTER));
            event.getRegistry().register(TileEntityType.Builder.create(ArcaneCoreTile::new, BlockRegistry.ARCANE_CORE_BLOCK).build(null).setRegistryName(LibBlockNames.ARCANE_CORE));
            event.getRegistry().register(TileEntityType.Builder.create(CrystallizerTile::new, BlockRegistry.CRYSTALLIZER_BLOCK).build(null).setRegistryName(LibBlockNames.CRYSTALLIZER));
            event.getRegistry().register(TileEntityType.Builder.create(SpellTurretTile::new, BlockRegistry.SPELL_TURRET).build(null).setRegistryName(LibBlockNames.SPELL_TURRET));
            event.getRegistry().register(TileEntityType.Builder.create(IntangibleAirTile::new, BlockRegistry.INTANGIBLE_AIR).build(null).setRegistryName(LibBlockNames.INTANGIBLE_AIR));
            event.getRegistry().register(TileEntityType.Builder.create(VolcanicTile::new, BlockRegistry.VOLCANIC_BLOCK).build(null).setRegistryName(LibBlockNames.VOLCANIC_ACCUMULATOR));
            event.getRegistry().register(TileEntityType.Builder.create(WixieCauldronTile::new, BlockRegistry.WIXIE_CAULDRON).build(null).setRegistryName(LibBlockNames.WIXIE_CAULDRON));

        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {

            IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
            registry.register(new BlockItem(BlockRegistry.PHANTOM_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("phantom_block"));
            registry.register(new BlockItem(BlockRegistry.LIGHT_BLOCK, new Item.Properties()).setRegistryName("light_block"));
            registry.register(new BlockItem(BlockRegistry.MANA_CONDENSER, ItemsRegistry.defaultItemProperties().setISTER(()-> ManaCondenserRenderer.ISRender::new)).setRegistryName("mana_condenser"));
            registry.register(new BlockItem(BlockRegistry.MANA_JAR, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_jar"));
            registry.register(new BlockItem(BlockRegistry.WARD_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("warding_stone"));
            registry.register(new AnimBlockItem(BlockRegistry.GLYPH_PRESS_BLOCK, ItemsRegistry.defaultItemProperties().setISTER(() -> PressRenderer::getISTER)).setRegistryName("glyph_press"));
            registry.register(new BlockItem(BlockRegistry.ARCANE_ORE, ItemsRegistry.defaultItemProperties()).setRegistryName("arcane_ore"));
            registry.register(new BlockItem(BlockRegistry.MANA_BLOOM_CROP, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_bloom_crop"));
            registry.register(new BlockItem(BlockRegistry.ENCHANTING_APP_BLOCK, ItemsRegistry.defaultItemProperties().setISTER(()-> EnchantingApparatusRenderer.ISRender::new)).setRegistryName("enchanting_apparatus"));
            registry.register(new BlockItem(BlockRegistry.ARCANE_PEDESTAL, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_PEDESTAL));
            registry.register(new BlockItem(BlockRegistry.SUMMONING_CRYSTAL, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.SUMMONING_CRYSTAL));
            registry.register(new BlockItem(BlockRegistry.ARCANE_BRICKS, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_BRICKS));
            registry.register(new BlockItem(BlockRegistry.SCRIBES_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.SCRIBES_BLOCK));
            registry.register(new BlockItem(BlockRegistry.ARCANE_ROAD, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_ROAD));
            registry.register(new BlockItem(BlockRegistry.ARCANE_RELAY, ItemsRegistry.defaultItemProperties().setISTER(()-> RelayRenderer.ISRender::new)).setRegistryName(LibBlockNames.ARCANE_RELAY));
            registry.register(new BlockItem(BlockRegistry.RUNE_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.RUNE));
            registry.register(new BlockItem(BlockRegistry.PORTAL_BLOCK, new Item.Properties()).setRegistryName(LibBlockNames.PORTAL));
            registry.register(new BlockItem(BlockRegistry.ARCANE_RELAY_SPLITTER, ItemsRegistry.defaultItemProperties().setISTER(()-> RelaySplitterRenderer.ISRender::new)).setRegistryName(LibBlockNames.ARCANE_RELAY_SPLITTER));
            registry.register(new BlockItem(BlockRegistry.CRYSTALLIZER_BLOCK, ItemsRegistry.defaultItemProperties().setISTER(()-> CrystallizerRenderer.ISRender::new)).setRegistryName(LibBlockNames.CRYSTALLIZER));
            registry.register(new BlockItem(BlockRegistry.ARCANE_CORE_BLOCK, ItemsRegistry.defaultItemProperties().setISTER(()-> ArcaneCoreRenderer.ISRender::new)).setRegistryName(LibBlockNames.ARCANE_CORE));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_ALTERNATE, LibBlockNames.AB_ALTERNATE));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_BASKET, LibBlockNames.AB_BASKET));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_HERRING, LibBlockNames.AB_HERRING));
            registry.register(getDefaultBlockItem(BlockRegistry.AB_MOSAIC, LibBlockNames.AB_MOSAIC));
            registry.register(getDefaultBlockItem(BlockRegistry.ARCANE_STONE, LibBlockNames.ARCANE_STONE));
            registry.register(new BlockItem(BlockRegistry.SPELL_TURRET, ItemsRegistry.defaultItemProperties().setISTER(()-> SpellTurretRenderer.ISRender::new)).setRegistryName(LibBlockNames.SPELL_TURRET));
            registry.register(new VolcanicAccumulatorBI(BlockRegistry.VOLCANIC_BLOCK, ItemsRegistry.defaultItemProperties().isImmuneToFire().setISTER(() -> VolcanicRenderer::getISTER)).setRegistryName(LibBlockNames.VOLCANIC_ACCUMULATOR));
            registry.register(new FluidBlockItem(BlockRegistry.LAVA_LILY, ItemsRegistry.defaultItemProperties().isImmuneToFire()).setRegistryName(LibBlockNames.LAVA_LILY));
            registry.register(new BlockItem(BlockRegistry.MANA_BERRY_BUSH, ItemsRegistry.defaultItemProperties().food(ItemsRegistry.MANA_BERRY_FOOD)).setRegistryName(LibItemNames.MANA_BERRY));
            registry.register(getDefaultBlockItem(BlockRegistry.MAGIC_SAPLING, "magic_sapling"));
            registry.register(new BlockItem(BlockRegistry.WIXIE_CAULDRON, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.WIXIE_CAULDRON));

        }

        public static Item getDefaultBlockItem(Block block, String registry){
            return new BlockItem(block, ItemsRegistry.defaultItemProperties()).setRegistryName(registry);
        }
    }

}
