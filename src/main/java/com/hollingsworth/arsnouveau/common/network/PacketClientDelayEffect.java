package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.NetworkEvent;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketClientDelayEffect {

    public Spell spell;
    public int duration;
    public int shooterID; // -1 is a null entity
    public @Nullable BlockHitResult hitPos;
    public int hitEntityID;

    //Decoder
    public PacketClientDelayEffect(FriendlyByteBuf buf) {
        duration = buf.readInt();
        spell = Spell.fromTag(buf.readNbt());
        shooterID = buf.readInt();
        hitEntityID = buf.readInt();
        if (hitEntityID == -1) {
            hitPos = buf.readBlockHitResult();
        }
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(duration);
        buf.writeNbt(spell.serialize());
        buf.writeInt(shooterID);
        buf.writeInt(hitEntityID);

        if (hitEntityID == -1) {
            buf.writeBlockHitResult(hitPos);
        }
        // buf.writeBlockHitResult();
    }

    public PacketClientDelayEffect(int duration, @Nullable LivingEntity shooter, Spell spell, SpellContext context, @Nullable BlockHitResult hitPos, @Nullable Entity hitEntity) {
        this.duration = duration;
        this.shooterID = shooter == null ? -1 : shooter.getId();
        this.spell = spell;
        this.hitPos = hitPos;
        this.hitEntityID = hitEntity == null ? -1 : hitEntity.getId();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world = ArsNouveau.proxy.getClientWorld();
            Entity hitEntity = world.getEntity(hitEntityID);
            HitResult result;
            if (hitEntityID == -1) {
                result = hitPos;
            } else if (hitEntity == null) {
                result = BlockHitResult.miss(new Vec3(0, 0, 0), Direction.UP, BlockPos.ZERO);
            } else {
                result = new EntityHitResult(hitEntity);
            }
            EventQueue.getClientQueue().addEvent(new DelayedSpellEvent(duration, spell, result, world, (LivingEntity) world.getEntity(shooterID),
                    new SpellContext(world, spell, (LivingEntity) world.getEntity(shooterID), new LivingCaster((LivingEntity) world.getEntity(shooterID)))));
        });
        ctx.get().setPacketHandled(true);
    }
}
