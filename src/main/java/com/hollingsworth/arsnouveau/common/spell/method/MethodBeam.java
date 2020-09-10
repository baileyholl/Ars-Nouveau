package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketBeam;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MethodBeam extends AbstractCastMethod {
    public MethodBeam() {
        super(ModConfig.MethodBeamID, "Beam");
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments) {
        PlayerEntity playerEntity = context.getPlayer();
        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);
        if(playerEntity != null) {
            Networking.sendToNearby(context.getWorld(), playerEntity.getPosition(), new PacketBeam(new BlockPos(MathUtil.getEntityLookHit(playerEntity, 8f)), playerEntity.getPosition().add(0, playerEntity.getEyeHeight() -0.2f, 0), 0));
            resolver.onResolveEffect(playerEntity.getEntityWorld(), playerEntity, res);
            resolver.expendMana(playerEntity);
        }
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments) {
        if(caster instanceof PlayerEntity) {
            Networking.sendToNearby(caster.world, caster.getPosition(), new PacketBeam(new BlockPos(MathUtil.getEntityLookHit(caster, 8f)), caster.getPosition().add(0, caster.getEyeHeight() -0.2f, 0), 0));
            resolver.onResolveEffect(caster.getEntityWorld(), caster, blockRayTraceResult);
            resolver.expendMana(caster);
        }
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments) {
        if(caster instanceof PlayerEntity) {
            Networking.sendToNearby(caster.world, caster.getPosition(), new PacketBeam(new BlockPos(MathUtil.getEntityLookHit(caster, 8f)), caster.getPosition().add(0, caster.getEyeHeight() -0.2f, 0), 0));
            resolver.onResolveEffect(caster.getEntityWorld(), caster, new EntityRayTraceResult(target));
            resolver.expendMana(caster);
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
