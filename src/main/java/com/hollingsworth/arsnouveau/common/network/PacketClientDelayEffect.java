package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketClientDelayEffect {

    public Spell spell;
    public int duration;
    public int shooterID; // -1 is a null entity
    public ParticleColor.IntWrapper color;
    public @Nullable BlockRayTraceResult hitPos;
    public int hitEntityID;

    //Decoder
    public PacketClientDelayEffect(PacketBuffer buf){
        duration = buf.readInt();
        spell = Spell.deserialize(buf.readUtf());
        shooterID = buf.readInt();
        color = ParticleColor.IntWrapper.deserialize(buf.readUtf());
        hitEntityID = buf.readInt();
        if(hitEntityID == -1) {
            hitPos = buf.readBlockHitResult();
        }
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeInt(duration);
        buf.writeUtf(spell.serialize());
        buf.writeInt(shooterID);
        buf.writeUtf(color.serialize());
        buf.writeInt(hitEntityID);

        if(hitEntityID == -1) {
            buf.writeBlockHitResult(hitPos);
        }
       // buf.writeBlockHitResult();
    }

    public PacketClientDelayEffect(int duration, @Nullable LivingEntity shooter, Spell spell, SpellContext context, @Nullable BlockRayTraceResult hitPos, @Nullable Entity hitEntity){
        this.duration = duration;
        this.shooterID = shooter == null ? -1 : shooter.getId();
        this.color = context.colors;
        this.spell = spell;
        this.hitPos = hitPos;
        this.hitEntityID = hitEntity == null ? -1 : hitEntity.getId();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            World world = ArsNouveau.proxy.getClientWorld();
            Entity hitEntity = world.getEntity(hitEntityID);
            RayTraceResult result;
            if(hitEntityID == -1){
                result = hitPos;
            }else if(hitEntity == null){
                result = BlockRayTraceResult.miss(new Vector3d(0, 0,0), Direction.UP, BlockPos.ZERO);
            }else{
                result = new EntityRayTraceResult(hitEntity);
            }
            EventQueue.getClientQueue().addEvent(new DelayedSpellEvent(duration, spell, result, world, (LivingEntity) world.getEntity(shooterID), new SpellContext(spell, (LivingEntity) world.getEntity(shooterID)).withColors(color)));
        } );
        ctx.get().setPacketHandled(true);
    }
}
