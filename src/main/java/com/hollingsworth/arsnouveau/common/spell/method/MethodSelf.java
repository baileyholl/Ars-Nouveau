package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedHelix;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MethodSelf extends AbstractCastMethod {
    public MethodSelf() {
        super(ModConfig.MethodSelfID, "Self");
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, World world, ArrayList<AbstractAugment> augments) {
        resolver.onResolveEffect(caster.getEntityWorld(), caster, new EntityRayTraceResult(caster));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.world, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.getPosition()));
//        if(caster.world.isRemote){
//            Vec3d pos = caster.getPositionVec();
//            pos = pos.add(0.0, -1.0, 0.0);
//            System.out.println("adding");
//            ParticleEngine.getInstance().addEffect(new TimedHelix(new BlockPos(pos), 0, GlowParticleData.createData(new ParticleColor(255,25,180)), (ClientWorld) caster.world));
//        }
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity playerIn, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    protected String getBookDescription() {
        return "Applies spells on the caster.";
    }
}
