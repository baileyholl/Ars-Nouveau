package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.*;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemRenderer;
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

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar")
    public static ManaJar MANA_JAR;

    @ObjectHolder(LibBlockNames.ARCANE_PEDESTAL) public static ArcanePedestal ARCANE_PEDESTAL;

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar")
    public static TileEntityType<ManaJarTile> MANA_JAR_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":warding_stone")
    public static WardBlock WARD_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":mana_siphon")
    public static ManaSiphonBlock MANA_SIPHON_BLOCK;


    @ObjectHolder(ArsNouveau.MODID + ":glyph_press")
    public static GlyphPressBlock GLYPH_PRESS_BLOCK;

    @ObjectHolder("arcane_ore")
    public static ArcaneOre ARCANE_ORE;

    @ObjectHolder(ArsNouveau.MODID + ":mana_bloom_crop")
    public static ManaBloomCrop MANA_BLOOM_CROP;

    @ObjectHolder(ArsNouveau.MODID + ":enchanting_apparatus")
    public static EnchantingApparatusBlock ENCHANTING_APP_BLOCK;


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
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            //  itemRegistryEvent.getRegistry().register(new SpellBook());
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.PHANTOM_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("phantom_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.LIGHT_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("light_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_CONDENSER, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_condenser"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_JAR, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_jar"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.WARD_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("warding_stone"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_SIPHON_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_siphon"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.GLYPH_PRESS_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("glyph_press"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ARCANE_ORE, ItemsRegistry.defaultItemProperties()).setRegistryName("arcane_ore"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.MANA_BLOOM_CROP, ItemsRegistry.defaultItemProperties()).setRegistryName("mana_bloom_crop"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ENCHANTING_APP_BLOCK, ItemsRegistry.defaultItemProperties()).setRegistryName("enchanting_apparatus"));
            itemRegistryEvent.getRegistry().register(new BlockItem(BlockRegistry.ARCANE_PEDESTAL, ItemsRegistry.defaultItemProperties()).setRegistryName(LibBlockNames.ARCANE_PEDESTAL));

        }
    }
}
