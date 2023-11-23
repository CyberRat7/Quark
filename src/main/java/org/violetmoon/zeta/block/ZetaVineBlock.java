package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

public class ZetaVineBlock extends VineBlock implements IZetaBlock {

	private final ZetaModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public ZetaVineBlock(ZetaModule module, String name, boolean creative) {
		super(OldMaterials.replaceablePlant().noCollission().randomTicks().strength(0.2F).sound(SoundType.GRASS));
		this.module = module;

		module.zeta.registry.registerBlock(this, name, true);
		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);

		if (creative) module.zeta.registry.setCreativeTab(this, "DECORATIONS");
	}

	@Override
	public void randomTick(@NotNull BlockState state, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource random) {
		tick(state, worldIn, pos, random);
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 300;
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public ZetaVineBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
