package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.client.particle.ParticleArc;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSource;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedHelix;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;

public class MethodTouch extends AbstractCastMethod {

    public MethodTouch() {
        super(ModConfig.MethodTouchID, "Touch");
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, World world, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {
        World world = context.getWorld();
        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);

       resolver.onResolveEffect(world, context.getPlayer(), res);
        resolver.expendMana(context.getPlayer());
        if(context.getWorld() instanceof ServerWorld){
            Vec3d pos = res.getHitVec();
            pos = pos.add(0.0, -1.0, 0.0);
//            ParticleEngine.getInstance().addEffect(new TimedHelix(new BlockPos(pos), 0, ParticleTypes.WITCH, (ServerWorld) context.getWorld()));
            for(int i =0; i < 3; i++){
                double d0 = pos.getX(); //+ world.rand.nextFloat();
                double d1 = pos.getY() +1.2;//+ world.rand.nextFloat() ;
                double d2 = pos.getZ()  ; //+ world.rand.nextFloat();
//                world.sap(ParticleTypes.END_ROD, d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
                ((ServerWorld)world).spawnParticle(ParticleTypes.WITCH,d0, d1, d2, world.rand.nextInt(2), (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, 0.1);

            }
        }
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult res, LivingEntity caster, ArrayList<AbstractAugment> augments) {
        resolver.onResolveEffect(caster.getEntityWorld(),caster, res);
        resolver.expendMana(caster);
        if(caster.world instanceof ServerWorld){
            Vec3d pos = res.getHitVec();
            World world = caster.world;
//            pos = pos.add(0.0, -1.0, 0.0);
            for(int i =0; i < 10; i++){
                double d0 = pos.getX() +0.5; //+ world.rand.nextFloat();
                double d1 = pos.getY() +1.2;//+ world.rand.nextFloat() ;
                double d2 = pos.getZ() +.5 ; //+ world.rand.nextFloat();
//                world.sap(ParticleTypes.END_ROD, d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
                ((ServerWorld)world).spawnParticle(ParticleTypes.WITCH,d0, d1, d2, world.rand.nextInt(4), (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, 0.1);

            }
        }
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {
        resolver.onResolveEffect(caster.getEntityWorld(), caster, new EntityRayTraceResult(target));
        resolver.expendMana(caster);
        if(caster.world instanceof ServerWorld){
            Vec3d pos = target.getPositionVec();
            pos = pos.add(0.0, -1.0, 0.0);
            ParticleEngine.getInstance().addEffect(new TimedHelix(new BlockPos(pos), 0, ParticleTypes.WITCH, (ServerWorld) caster.world));
        }
    }

    @Override
    protected String getBookDescription() {
        return "Applies spells at the block or entity that is targeted.";
    }
}
