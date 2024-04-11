package org.violetmoon.quark.content.building.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.building.block.WoodPostBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;
import org.violetmoon.zeta.util.VanillaWoods;
import org.violetmoon.zeta.util.VanillaWoods.Wood;
import org.violetmoon.zeta.util.ZetaToolActions;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;

@ZetaLoadModule(category = "building")
public class WoodenPostsModule extends ZetaModule {

	@Hint
	TagKey<Item> postsTag;

	@LoadEvent
	public final void register(ZRegister event) {
		for(Wood wood : VanillaWoods.ALL) {
			Block b = wood.fence();

			WoodPostBlock post = new WoodPostBlock(this, b, "", wood.soundWood());
			WoodPostBlock stripped = new WoodPostBlock(this, b, "stripped_", wood.soundWood());
			ToolInteractionHandler.registerInteraction(ZetaToolActions.AXE_STRIP, post, stripped);
		}
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		postsTag = TagKey.create(Registries.ITEM, new ResourceLocation(Quark.MOD_ID, "posts"));
	}

	public static boolean canHangingBlockConnect(BlockState state, LevelReader worldIn, BlockPos pos, boolean prev) {
		return prev ||
				(Quark.ZETA.modules.isEnabled(WoodenPostsModule.class)
						&& (!state.hasProperty(LanternBlock.HANGING) || state.getValue(LanternBlock.HANGING))
						&& worldIn.getBlockState(pos.above()).getBlock() instanceof WoodPostBlock);
	}

}
