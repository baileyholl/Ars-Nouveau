package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.CheckStuckGoal;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.EnumSet;

public class TakeItemGoal extends CheckStuckGoal {
    EntityCarbuncle carbuncle;
    public TakeItemGoal(EntityCarbuncle carbuncle){
        super(carbuncle::getPosition, 1, carbuncle::setStuck);
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.carbuncle = carbuncle;
    }


    @Override
    public void startExecuting() {
        super.startExecuting();
        if(carbuncle.isTamed() && carbuncle.fromPos != null && carbuncle.getHeldStack().isEmpty())
            setPath(carbuncle.fromPos.getX(),carbuncle.fromPos.getY(),carbuncle.fromPos.getZ(), 1.2D);
    }

    public static boolean isValidItem(EntityCarbuncle carbuncle,ItemStack stack){
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

    public void getItem(){
        World world = carbuncle.world;
        IInventory i = (IInventory) world.getTileEntity(carbuncle.fromPos);
        for(int j = 0; j < i.getSizeInventory(); j++){
            if(!i.getStackInSlot(j).isEmpty() && isValidItem(carbuncle, i.getStackInSlot(j))){

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
    }
    public void setPath(double x, double y, double z, double speedIn){
        carbuncle.getNavigator().setPath( carbuncle.getNavigator().getPathToPos(x+0.5, y+0.5, z+0.5, 0), speedIn);
    }

    @Override
    public void tick() {
        if(carbuncle.getHeldStack().isEmpty() && carbuncle.fromPos != null && BlockUtil.distanceFrom(carbuncle.getPosition(), carbuncle.fromPos) < 1.5D){
            World world = carbuncle.world;
            if(world.getTileEntity(carbuncle.fromPos) instanceof IInventory){
                getItem();
                return;
            }
        }

        if(carbuncle.fromPos != null && carbuncle.getHeldStack().isEmpty()) {
            setPath(carbuncle.fromPos.getX(), carbuncle.fromPos.getY(), carbuncle.fromPos.getZ(), 1.2D);
            super.tick();
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !carbuncle.isStuck && carbuncle.getHeldStack() != null && carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed();
    }

    @Override
    public boolean shouldExecute() {
        return !carbuncle.isStuck && carbuncle.getHeldStack() != null &&carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed()  &&carbuncle.fromPos != null;
    }
}
