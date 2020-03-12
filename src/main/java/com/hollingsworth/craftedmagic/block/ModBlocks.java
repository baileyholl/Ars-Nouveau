package com.hollingsworth.craftedmagic.block;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

import static net.minecraft.world.biome.Biome.LOGGER;

public class ModBlocks {

    @ObjectHolder(ArsNouveau.MODID + ":phantom_block")
    public static PhantomBlock PHANTOM_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":light_block")
    public static LightBlock lightBlock;

    @ObjectHolder(ArsNouveau.MODID + ":light_block")
    public static TileEntityType<LightTile> LIGHT_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":phantom_block")
    public static TileEntityType<PhantomBlockTile> PHANTOM_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":mana_condenser")
    public static TileEntityType<ManaCondenserTile> MANA_CONDENSER_TILE;


    @ObjectHolder(ArsNouveau.MODID + ":mana_condenser")
    public static ManaCondenserBlock MANA_CONDENSER;

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar")
    public static ManaJar MANA_JAR;

    @ObjectHolder(ArsNouveau.MODID + ":mana_jar")
    public static TileEntityType<ManaJarTile> MANA_JAR_TILE;

    @ObjectHolder(ArsNouveau.MODID + ":ward_block")
    public static WardBlock WARD_BLOCK;

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
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
            event.getRegistry().register(TileEntityType.Builder.create(PhantomBlockTile::new, ModBlocks.PHANTOM_BLOCK).build(null).setRegistryName("phantom_block"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaCondenserTile::new, ModBlocks.MANA_CONDENSER).build(null).setRegistryName("mana_condenser"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaJarTile::new, ModBlocks.MANA_JAR).build(null).setRegistryName("mana_jar"));
            event.getRegistry().register(TileEntityType.Builder.create(LightTile::new, ModBlocks.lightBlock).build(null).setRegistryName("light_block"));
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            //  itemRegistryEvent.getRegistry().register(new SpellBook());
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.PHANTOM_BLOCK, ModItems.defaultItemProperties()).setRegistryName("phantom_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.lightBlock, ModItems.defaultItemProperties()).setRegistryName("light_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.MANA_CONDENSER, ModItems.defaultItemProperties()).setRegistryName("mana_condenser"));
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.MANA_JAR, ModItems.defaultItemProperties()).setRegistryName("mana_jar"));
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.WARD_BLOCK, ModItems.defaultItemProperties()).setRegistryName("ward_block"));
        }
    }
}
