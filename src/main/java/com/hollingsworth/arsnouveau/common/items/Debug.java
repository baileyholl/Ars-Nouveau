package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class Debug extends ModItem{
    public Debug() {
        super(new Item.Properties());
        setRegistryName("debug");
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(!context.getLevel().isClientSide){

//            for(BlockPos p : BlockPos.betweenClosed(context.getClickedPos().immutable().east(20).north(20), context.getClickedPos().immutable().south(20).west(20))){
//                if(context.getLevel().random.nextFloat() < 0.03) {
//                    double distance = BlockUtil.distanceFrom(p, context.getClickedPos());
//                    int time = (int) (40 + distance * 5 + context.getLevel().random.nextInt(10));
//                    EruptionEvent event = new EruptionEvent(context.getLevel(), p.immutable(), time, (int) (distance*2));
//                    EventQueue.getServerInstance().addEvent(event);
//                    Networking.sendToNearby(context.getLevel(), context.getClickedPos(), new PacketTimedEvent(event.serialize(new CompoundNBT())));
//                }
//            }

            ServerWorld serverworld = (ServerWorld)context.getLevel();
            TemplateManager templatemanager = serverworld.getStructureManager();
            Template template = templatemanager.getOrCreate(new ResourceLocation("ars_nouveau:test"));
            System.out.println(template.getSize());
          //  template.fillFromWorld(context.getLevel(), context.getClickedPos(), new BlockPos(1, 1, 1), false, null);
            if(template.palettes.isEmpty())
                return ActionResultType.SUCCESS;
            Template.Palette palette = template.palettes.get(0);
            List<Template.BlockInfo> start = palette.blocks(Blocks.COBBLESTONE);
            Template.BlockInfo info = start.get(0);
            for(Template.BlockInfo blockInfo : palette.blocks()){
                context.getLevel().setBlock(context.getClickedPos().offset(blockInfo.pos.getX(), blockInfo.pos.getY(), blockInfo.pos.getZ())
                        .offset(-info.pos.getX(), -info.pos.getY(), -info.pos.getZ()), blockInfo.state, 2);
            }

           // template.placeInWorld(serverworld, context.getClickedPos(),context.getClickedPos(), new PlacementSettings(), context.getLevel().random, 2);

//            EventQueue.getServerInstance().addEvent(new EruptionEvent(context.getLevel(), context.getClickedPos(), 60));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).east(0)));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).east(20)));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).west(20)));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand handIn) {

        return ActionResult.success(playerIn.getItemInHand(handIn));
    }
}
