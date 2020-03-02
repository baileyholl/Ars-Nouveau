package com.hollingsworth.craftedmagic.block;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static net.minecraft.world.biome.Biome.LOGGER;

public class ModBlocks {

    @ObjectHolder(ArsNouveau.MODID + ":phantom_block")
    public static PhantomBlock PHANTOM_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":light_block")
    public static LightBlock lightBlock;

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
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
            event.getRegistry().register(TileEntityType.Builder.create(PhantomBlockTile::new, ModBlocks.PHANTOM_BLOCK).build(null).setRegistryName("phantom_block"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaCondenserTile::new, ModBlocks.MANA_CONDENSER).build(null).setRegistryName("mana_condenser"));
            event.getRegistry().register(TileEntityType.Builder.create(ManaJarTile::new, ModBlocks.MANA_JAR).build(null).setRegistryName("mana_jar"));
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            //  itemRegistryEvent.getRegistry().register(new SpellBook());
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.PHANTOM_BLOCK, new Item.Properties()).setRegistryName("phantom_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.lightBlock, new Item.Properties()).setRegistryName("light_block"));
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.MANA_CONDENSER, new Item.Properties()).setRegistryName("mana_condenser"));

            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.MANA_JAR, new Item.Properties()).setRegistryName("mana_jar"));
        }
    }
}
