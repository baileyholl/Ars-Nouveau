package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityChimeraProjectile extends AbstractArrowEntity implements IAnimatable {

    public EntityChimeraProjectile(double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE, p_i48547_2_, p_i48547_4_, p_i48547_6_, p_i48547_8_);
    }

    public EntityChimeraProjectile(LivingEntity p_i48548_2_, World p_i48548_3_) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE, p_i48548_2_, p_i48548_3_);
    }

    public EntityChimeraProjectile(World world){
        super(ModEntities.ENTITY_CHIMERA_SPIKE, world);
    }

    public EntityChimeraProjectile(EntityType<EntityChimeraProjectile> entityChimeraProjectileEntityType, World world) {
        super(entityChimeraProjectileEntityType, world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    private <E extends Entity> PlayState attackPredicate(AnimationEvent e) {
        e.getController().setAnimation(new AnimationBuilder().addAnimation("spike_spin"));
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 1, this::attackPredicate));
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return !(entity instanceof EntityChimera) && super.canHitEntity(entity);
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_CHIMERA_SPIKE;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityChimeraProjectile(FMLPlayMessages.SpawnEntity packet, World world) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE, world);
    }
}
