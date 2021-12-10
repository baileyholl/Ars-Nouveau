package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.block.SpellPrismBlock;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.HashSet;
import java.util.Set;

public class EntityProjectileSpell extends ColoredProjectile {

    public int age;
    public SpellResolver spellResolver;
    public int pierceLeft;
    public int numSensitive;
    public Set<BlockPos> hitList = new HashSet<>();

    public EntityProjectileSpell(final EntityType<? extends EntityProjectileSpell> entityType, final Level world) {
        super(entityType, world);

    }

    public EntityProjectileSpell(final Level world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public EntityProjectileSpell(Level world, SpellResolver resolver){
        super(world, resolver.spellContext.caster);
        this.spellResolver = resolver;
        this.pierceLeft = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.caster, AugmentPierce.INSTANCE);
        this.numSensitive = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.caster, AugmentSensitive.INSTANCE);
        resolver.spellContext.colors.makeVisible();
        setColor(resolver.spellContext.colors);
    }

    public EntityProjectileSpell(final Level world, final LivingEntity shooter) {
        super(world, shooter);
    }

    @Override
    public void tick() {
        age++;


        Vec3 vector3d = this.getDeltaMovement();

        if(this.age > 60*20){
            this.remove(RemovalReason.DISCARDED);
            return;
        }


        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();


        if (this.inGround) {
            this.inGround = false;
            this.setDeltaMovement(this.getDeltaMovement());
        }


        Vec3 vector3d2 = this.position();
        Vec3 vector3d3 = vector3d2.add(vector3d);
        HitResult raytraceresult = this.level.clip(new ClipContext(vector3d2, vector3d3, numSensitive > 0 ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            vector3d3 = raytraceresult.getLocation();
        }
        EntityHitResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
        if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
        }

        if (raytraceresult != null && raytraceresult instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult)raytraceresult).getEntity();
            Entity entity1 = this.getOwner();
            if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                raytraceresult = null;
            }
        }

        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS  && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
        }
        if(raytraceresult != null && raytraceresult.getType() == HitResult.Type.MISS && raytraceresult instanceof BlockHitResult){
            BlockRegistry.PORTAL_BLOCK.onProjectileHit(level,level.getBlockState(new BlockPos(raytraceresult.getLocation())),
                    (BlockHitResult)raytraceresult, this );

        }



        Vec3 vec3d = this.getDeltaMovement();
        double x = this.getX() + vec3d.x;
        double y = this.getY() + vec3d.y;
        double z = this.getZ() + vec3d.z;


        if (!this.isNoGravity()) {
            Vec3 vec3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vec3d1.x, vec3d1.y , vec3d1.z);
        }

        this.setPos(x,y,z);

        if(level.isClientSide && this.age > 2) {
//
            for (int i = 0; i < 3; i++) {

                double deltaX = getX() - xOld;
                double deltaY = getY() - yOld;
                double deltaZ = getZ() - zOld;
                double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);

                for (double j = 0; j < dist; j++) {
                    double coeff = j / dist;

                    level.addParticle(GlowParticleData.createData(getParticleColor()),
                            (float) (xo + deltaX * coeff),
                            (float) (yo + deltaY * coeff), (float)
                                    (zo + deltaZ * coeff),
                            0.0125f * (random.nextFloat() - 0.5f),
                            0.0125f * (random.nextFloat() - 0.5f),
                            0.0125f * (random.nextFloat() - 0.5f));

                }
            }
        }
    }


    @Override
    public void baseTick() {
        super.baseTick();
    }



    /**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -Mth.sin(rotationYawIn * ((float)Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(rotationYawIn * ((float)Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3d = entityThrower.getLookAngle();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3d.x, vec3d.y, vec3d.z));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        Vec3 vec3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale(velocity);
        this.setDeltaMovement(vec3d);
        float f = Mth.sqrt((float) vec3d.horizontalDistanceSqr());
        this.yRot = (float)(Mth.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.xRot = (float)(Mth.atan2(vec3d.y, f) * (double)(180F / (float)Math.PI));
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.SPELL_PROJ;
    }

    protected void attemptRemoval(){
        this.pierceLeft--;
        if(this.pierceLeft < 0){
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if(!level.isClientSide &&  result != null && result.getType() == HitResult.Type.ENTITY) {
            if (((EntityHitResult) result).getEntity().equals(this.getOwner())) return;
            if(this.spellResolver != null) {
                this.spellResolver.onResolveEffect(level, (LivingEntity) this.getOwner(), result);
                Networking.sendToNearby(level, new BlockPos(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        new BlockPos(result.getLocation()),getParticleColorWrapper()));
                attemptRemoval();
            }
        }

        if (!level.isClientSide && result instanceof BlockHitResult  && !this.isRemoved() && !hitList.contains(((BlockHitResult) result).getBlockPos())) {

            BlockHitResult blockraytraceresult = (BlockHitResult)result;
            BlockState state = level.getBlockState(((BlockHitResult) result).getBlockPos());

            if(state.getBlock() instanceof SpellPrismBlock){
                SpellPrismBlock.redirectSpell((ServerLevel) level, blockraytraceresult.getBlockPos(), this);
                return;
            }


            if(state.getMaterial() == Material.PORTAL){
                state.getBlock().entityInside(state, level, ((BlockHitResult) result).getBlockPos(),this);
                return;
            }

            if(this.spellResolver != null) {
                this.hitList.add(blockraytraceresult.getBlockPos());
                this.spellResolver.onResolveEffect(this.level, (LivingEntity) this.getOwner(), blockraytraceresult);
            }
            Networking.sendToNearby(level, ((BlockHitResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                    new BlockPos(result.getLocation()).below(), getParticleColorWrapper()));
           attemptRemoval();
        }
    }


    public EntityProjectileSpell(PlayMessages.SpawnEntity packet, Level world){
        super(ModEntities.SPELL_PROJ, world);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("pierce")){
            this.pierceLeft = tag.getInt("pierce");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("pierce", this.pierceLeft);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }
}
