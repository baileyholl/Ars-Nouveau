package com.hollingsworth.arsnouveau.client.renderer.entity;// Made with Blockbench 3.6.6

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class BookwyrmModel<T extends LivingEntity & IAnimatable> extends AnimatedGeoModel<T> {

	private static final ResourceLocation WILD_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_blue.png");
	public static final ResourceLocation NORMAL_MODEL = new ResourceLocation(ArsNouveau.MODID , "geo/book_wyrm_dragon.geo.json");
	public static final ResourceLocation ANIMATIONS = new ResourceLocation(ArsNouveau.MODID , "animations/book_wyrm_dragon_animation.json");

	@Override
	public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("head");
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * 0.010453292F);
		head.setRotationY(extraData.netHeadYaw * 0.015453292F);
	}

	@Override
	public ResourceLocation getModelLocation(T wyrm) {
		return NORMAL_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(T wyrm) {
		return WILD_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T wyrm) {
		return ANIMATIONS;
	}
}