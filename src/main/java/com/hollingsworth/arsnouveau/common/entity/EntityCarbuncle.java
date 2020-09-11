package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityCarbuncle extends CreatureEntity {

    public BlockPos fromPos;
    public BlockPos toPos;
    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityCarbuncle.class, DataSerializers.ITEMSTACK);


    public EntityCarbuncle(EntityType<EntityCarbuncle> entityCarbuncleEntityType, World world) {
        super(entityCarbuncleEntityType, world);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
//        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
    }


    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        if(world.isRemote)
            return true;
        System.out.println("hello");
        ItemStack stack = player.getHeldItem(hand);

        if(stack != ItemStack.EMPTY){
            setHeldStack(new ItemStack(stack.getItem()));
        }

        return false;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_CARBUNCLE_TYPE;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HELD_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if(tag.contains("held"))
            setHeldStack(ItemStack.read((CompoundNBT)tag.get("held")));
    }
    public void setHeldStack(ItemStack stack){
        this.dataManager.set(HELD_ITEM,stack);
    }

    public ItemStack getHeldStack(){
        return this.dataManager.get(HELD_ITEM);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if(getHeldStack() != null) {
            CompoundNBT itemTag = new CompoundNBT();
            getHeldStack().write(itemTag);
            tag.put("held", itemTag);
        }
    }
}
