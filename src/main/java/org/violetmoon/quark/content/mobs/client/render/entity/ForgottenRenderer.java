package org.violetmoon.quark.content.mobs.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.mobs.client.layer.forgotten.ForgottenClothingLayer;
import org.violetmoon.quark.content.mobs.client.layer.forgotten.ForgottenEyesLayer;
import org.violetmoon.quark.content.mobs.client.layer.forgotten.ForgottenSheathedItemLayer;

public class ForgottenRenderer extends SkeletonRenderer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/forgotten/main.png");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ForgottenRenderer(EntityRendererProvider.Context context) {
		super(context);
		addLayer(new ForgottenClothingLayer<>(this, context.getModelSet()));
		addLayer(new ForgottenEyesLayer(this));
		addLayer(new ForgottenSheathedItemLayer(this, context.getItemInHandRenderer()));
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull AbstractSkeleton entity) {
		return TEXTURE;
	}

	@Override
	protected void scale(@NotNull AbstractSkeleton entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(1.2F, 1.2F, 1.2F);
	}

}
