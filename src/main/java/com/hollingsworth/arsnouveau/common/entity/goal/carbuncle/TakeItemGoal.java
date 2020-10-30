package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.EnumSet;

public class TakeItemGoal extends Goal {
    EntityCarbuncle carbuncle;
    public TakeItemGoal(EntityCarbuncle carbuncle){
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.carbuncle = carbuncle;
    }

    public static boolean isValidItem(EntityCarbuncle carbuncle, ItemStack stack){
        if(!carbuncle.whitelist && !carbuncle.blacklist)
            return true;
        if(carbuncle.whitelist){
            for(ItemStack s : carbuncle.allowedItems)
                if(s.isItemEqual(stack))
                    return true;
        }
        if(carbuncle.blacklist){
            for(ItemStack s : carbuncle.ignoreItems)
                if(!s.isItemEqual(stack))
                    return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if(carbuncle.isTamed() && carbuncle.fromPos != null && carbuncle.getHeldStack().isEmpty())
            carbuncle.getNavigator().tryMoveToXYZ(carbuncle.fromPos.getX(),carbuncle.fromPos.getY(),carbuncle.fromPos.getZ(), 1.2D);
    }

    @Override
    public void tick() {
        super.tick();

        if(carbuncle.getHeldStack().isEmpty() && carbuncle.fromPos != null && BlockUtil.distanceFrom(carbuncle.getPosition(), carbuncle.fromPos) < 1.25D){
            World world = carbuncle.world;
            if(world.getTileEntity(carbuncle.fromPos) instanceof IInventory){
                IInventory i = (IInventory) world.getTileEntity(carbuncle.fromPos);
                for(int j = 0; j < i.getSizeInventory(); j++){
                    if(!i.getStackInSlot(j).isEmpty()){

                        carbuncle.setHeldStack(i.removeStackFromSlot(j));

                        carbuncle.world.playSound(null, carbuncle.getPosX(),carbuncle.getPosY(), carbuncle.getPosZ(),
                                SoundEvents.ENTITY_ITEM_PICKUP, carbuncle.getSoundCategory(),1.0F, 1.0F);

                        if(world instanceof ServerWorld){
                            OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), carbuncle.fromPos, 20);
                            event.open();
                            EventQueue.getInstance().addEvent(event);
                        }
                        break;
                    }
                }
                return;
            }
        }

        if(carbuncle.fromPos != null && carbuncle.getHeldStack().isEmpty()) {
            carbuncle.getNavigator().tryMoveToXYZ(carbuncle.fromPos.getX(), carbuncle.fromPos.getY(), carbuncle.fromPos.getZ(), 1.2D);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return carbuncle.getHeldStack() != null && carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed();
    }

    @Override
    public boolean shouldExecute() {
        return carbuncle.getHeldStack() != null &&carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed()  &&carbuncle.fromPos != null;
    }
}
