package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.RelayRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.ManaCondenserRenderer;
import com.hollingsworth.arsnouveau.common.block.*;
import com.hollingsworth.arsnouveau.common.block.tile.*;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

    @ObjectHolder(ArsNouveau.MODID + ":mana_siphon")
    public static TileEntityType<ManaSiphonTile> MANA_SIPHON_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":enchanting_apparatus")
    public static TileEntityType<EnchantingApparatusTile> ENCHANTING_APP_TILE;


    @ObjectHolder(ArsNouveau.MODID + ":glyph_press")
    public static TileEntityType<GlyphPressTile> GLYPH_PRESS_TILE;

    @ObjectHolder(LibBlockNames.ARCANE_PEDESTAL)
    public static TileEntityType<ArcanePedestalTile> ARCANE_PEDESTAL_TILE;



    @ObjectHolder(ArsNouveau.MODID + ":mana_condenser")
    public static ManaCondenserBlock MANA_CONDENSER;

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar") public static ManaJar MANA_JAR;

    @ObjectHolder(LibBlockNames.ARCANE_PEDESTAL) public static ArcanePedestal ARCANE_PEDESTAL;

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar") public static TileEntityType<ManaJarTile> MANA_JAR_TILE;

    @ObjectHolder(LibBlockNames.ARCANE_RELAY) public static TileEntityType<ArcaneRelayTile> ARCANE_RELAY_TILE;

    @ObjectHolder(LibBlockNames.RUNE) public static TileEntityType<RuneTile> RUNE_TILE;


    @ObjectHolder(ArsNouveau.MODID + ":warding_stone") public static WardBlock WARD_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":mana_siphon") public static ManaSiphonBlock MANA_SIPHON_BLOCK;


    @ObjectHolder(ArsNouveau.MODID + ":glyph_press") public static GlyphPressBlock GLYPH_PRESS_BLOCK;

    @ObjectHolder("arcane_ore") public static ArcaneOre ARCANE_ORE;

    @ObjectHolder(ArsNouveau.MODID + ":mana_bloom_crop") public static ManaBloomCrop MANA_BLOOM_CROP;

    @ObjectHolder(ArsNouveau.MODID + ":enchanting_apparatus") public static EnchantingApparatusBlock ENCHANTING_APP_BLOCK;

    @ObjectHolder(LibBlockNames.ARCANE_BRICKS) public static ModBlock ARCANE_BRICKS;


    @ObjectHolder(LibBlockNames.SCRIBES_BLOCK) public static ScribesBlock SCRIBES_BLOCK;

    @ObjectHolder(LibBlockNames.SUMMONING_CRYSTAL) public static SummoningCrystal SUMMONING_CRYSTAL;

    @ObjectHolder(LibBlockNames.SUMMONING_CRYSTAL) public static TileEntityType<SummoningCrytalTile> SUMMONING_CRYSTAL_TILE;

    @ObjectHolder(LibBlockNames.SCRIBES_BLOCK) public static TileEntityType<ScribesTile> SCRIBES_TABLE_TILE;
    @ObjectHolder(LibBlockNames.FORGE) public static TileEntityType<ForgeTile> FORGE_TILE_TYPE;

    @ObjectHolder(LibBlockNames.PORTAL) public static TileEntityType<PortalTile> PORTAL_TILE_TYPE;


    @ObjectHolder(LibBlockNames.ARCANE_ROAD) public static ModBlock ARCANE_ROAD;

    @ObjectHolder(LibBlockNames.ARCANE_RELAY) public static ArcaneRelay ARCANE_RELAY;

    @ObjectHolder(LibBlockNames.RUNE) public static RuneBlock RUNE_BLOCK;

    @ObjectHolder(LibBlockNames.FORGE) public static ForgeBlock FORGE_BLOCK;

    @ObjectHolder(LibBlockNames.PORTAL)
    public static PortalBlock PORTAL_BLOCK;


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
            blockRegistryEvent.getRegistry().register(new ManaSiphonBlock());
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
            blockRegistryEvent.getRegistry().register(new ForgeBlock());
            blockRegistryEvent.getRegistry().register(new PortalBlock());
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
            event.getRegistry().register(TileEntityType.Builder.create(PhantomBlockTile::new, BlockRegistry.PHANTOM_BLOCK).build(null).setRegistryName("phantom_block"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaCondenserTile::new, BlockRegistry.MANA_CONDENSER).build(null).setRegistryName("mana_condenser"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaJarTile::new, BlockRegistry.MANA_JAR).build(null).setRegistryName("mana_jar"));
            event.getRegistry().register(TileEntityType.Builder.create(LightTile::new, BlockRegistry.LIGHT_BLOCK).build(null).setRegistryName("light_block"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaSiphonTile::new, BlockRegistry.MANA_SIPHON_BLOCK).build(null).setRegistryName("mana_siphon"));

            event.getRegistry().register(TileEntityType.Builder.create(GlyphPressTile::new, BlockRegistry.GLYPH_PRESS_BLOCK).build(null).setRegistryName("glyph_press"));
            event.getRegistry().register(TileEntityType.Builder.create(EnchantingApparatusTile::new, BlockRegistry.ENCHANTING_APP_BLOCK).build(null).setRegistryName("enchanting_apparatus"));
            event.getRegistry().register(TileEntityType.Builder.create(ArcanePedestalTile::new, BlockRegistry.ARCANE_PEDESTAL).build(null).setRegistryName(LibBlockNames.ARCANE_PEDESTAL));
            event.getRegistry().register(TileEntityType.Builder.create(SummoningCrytalTile::new, BlockRegistry.SUMMONING_CRYSTAL).build(null).setRegistryName(LibBlockNames.SUMMONING_CRYSTAL));
            event.getRegistry().register(TileEntityType.Builder.create(ScribesTile::new, BlockRegistry.SCRIBES_BLOCK).build(null).setRegistryName(LibBlockNames.SCRIBES_BLOCK));
            event.getRegistry().register(TileEntityType.Builder.create(ArcaneRelayTile::new, BlockRegistry.ARCANE_RELAY).build(null).setRegistryName(LibBlockNames.ARCANE_RELAY));
            event.getRegistry().register(TileEntityType.Builder.create(RuneTile::new, BlockRegistry.RUNE_BLOCK).build(null).setRegistryName(LibBlockNames.RUNE));
            event.getRegistry().register(TileEntityType.Builder.create(ForgeTile::new, BlockRegistry.FORGE_BLOCK).build(null).setRegistryName(LibBlockNames.FORGE));
            event.getRegistry().register(TileEntityType.Builder.create(PortalTile::new, BlockRegistry.PORTAL_BLOCK).build(null).setRegistryName(LibBlockNames.PORTAL));

        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            //  itemRegistryEvent.getRegistry().register(new SpellBook());
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.PHANTOM_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("phantom_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.LIGHT_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("light_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_CONDENSER, ItemsRegistry.defaultItemProperties().setISTER(()-> ManaCondenserRenderer.ISRender::new)).setRegistryName("mana_condenser"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_JAR, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_jar"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.WARD_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("warding_stone"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_SIPHON_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_siphon"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.GLYPH_PRESS_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("glyph_press"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ARCANE_ORE, ItemsRegistry.defaultItemProperties()).setRegistryName("arcane_ore"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_BLOOM_CROP, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_bloom_crop"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ENCHANTING_APP_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("enchanting_apparatus"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ARCANE_PEDESTAL, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_PEDESTAL));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.SUMMONING_CRYSTAL, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.SUMMONING_CRYSTAL));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ARCANE_BRICKS, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_BRICKS));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.SCRIBES_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.SCRIBES_BLOCK));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ARCANE_ROAD, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_ROAD));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ARCANE_RELAY, ItemsRegistry.defaultItemProperties().setISTER(()-> RelayRenderer.ISRender::new)).setRegistryName(LibBlockNames.ARCANE_RELAY));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.RUNE_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.RUNE));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.FORGE_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.FORGE));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.PORTAL_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.PORTAL));

        }
    }

}
