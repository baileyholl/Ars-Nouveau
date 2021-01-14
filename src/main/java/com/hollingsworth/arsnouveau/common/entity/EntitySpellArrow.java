package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntitySpellArrow extends ArrowEntity {
    public SpellResolver spellResolver;
    public int pierceLeft;
    public EntitySpellArrow(EntityType<? extends ArrowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public EntitySpellArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntitySpellArrow(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    @Override
    public void tick() {
        super.tick();
        if(world.isRemote && ticksExisted > 1) {
            for (int i = 0; i < 10; i++) {

                double deltaX = getPosX() - lastTickPosX;
                double deltaY = getPosY() - lastTickPosY;
                double deltaZ = getPosZ() - lastTickPosZ;
                double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);
                int counter = 0;

                for (double j = 0; j < dist; j++) {
                    double coeff = j / dist;
                    counter += world.rand.nextInt(3);
                    if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
                        world.addParticle(GlowParticleData.createData(ParticleUtil.defaultParticleColor()), (float) (prevPosX + deltaX * coeff), (float) (prevPosY + deltaY * coeff), (float) (prevPosZ + deltaZ * coeff), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f));
                    }
                }
            }
        }
    }


    @Override
    protected void onImpact(RayTraceResult result) {
        if(this.spellResolver != null)
            this.spellResolver.onResolveEffect(world, (LivingEntity) this.func_234616_v_(), result);
        RayTraceResult.Type raytraceresult$type = result.getType();
        LivingEntity shooter = func_234616_v_() instanceof LivingEntity ? (LivingEntity) func_234616_v_() : null;
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            if(spellResolver != null) {
                spellResolver.onResolveEffect(world, shooter, result);
            }
            this.onEntityHit((EntityRayTraceResult)result);
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
            if(spellResolver != null) {
                spellResolver.onResolveEffect(world, shooter, result);
            }
            this.func_230299_a_((BlockRayTraceResult)result);
        }
        this.remove();
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_SPELL_ARROW;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntitySpellArrow(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.ENTITY_SPELL_ARROW, world);
    }
}
