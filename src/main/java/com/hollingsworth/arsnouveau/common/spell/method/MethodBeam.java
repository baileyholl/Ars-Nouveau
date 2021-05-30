package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MethodBeam extends AbstractCastMethod {
    public MethodBeam() {
        super(GlyphLib.MethodBeamID, "Beam");
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellContext context) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext) {
        PlayerEntity playerEntity = context.getPlayer();
        BlockRayTraceResult res = new BlockRayTraceResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false);
        if(playerEntity != null) {
//            Networking.sendToNearby(context.getWorld(), playerEntity.getPosition(), new PacketBeam(new BlockPos(MathUtil.getEntityLookHit(playerEntity, 8f)), playerEntity.getPosition().add(0, playerEntity.getEyeHeight() -0.2f, 0), 0));
            resolver.onResolveEffect(playerEntity.getCommandSenderWorld(), playerEntity, res);
            resolver.expendMana(playerEntity);
        }
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext) {
        if(caster instanceof PlayerEntity) {
//            Networking.sendToNearby(caster.world, caster.getPosition(), new PacketBeam(new BlockPos(MathUtil.getEntityLookHit(caster, 8f)), caster.getPosition().add(0, caster.getEyeHeight() -0.2f, 0), 0));
            resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, blockRayTraceResult);
            resolver.expendMana(caster);
        }
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext) {
        if(caster instanceof PlayerEntity) {
//            Networking.sendToNearby(caster.world, caster.getPosition(), new PacketBeam(new BlockPos(MathUtil.getEntityLookHit(caster, 8f)), caster.getPosition().add(0, caster.getEyeHeight() -0.2f, 0), 0));
            resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new EntityRayTraceResult(target));
            resolver.expendMana(caster);
        }
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
