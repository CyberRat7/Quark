package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.function.BooleanSupplier;

@ZetaLoadModule(category = "building")
public class MoreBrickTypesModule extends ZetaModule {

	@Config(flag = "blue_nether_bricks", description = "This also comes with a utility recipe for Red Nether Bricks")
	public boolean enableBlueNetherBricks = true;

	@Config(flag = "sandstone_bricks", description = "This also includes Red Sandstone Bricks and Soul Sandstone Bricks")
	public boolean enableSandstoneBricks = true;

	@Config(flag = "cobblestone_bricks", description = "This also includes Mossy Cobblestone Bricks")
	private static boolean enableCobblestoneBricks = true;

	@Config(flag = "blackstone_bricks", description = "Requires Cobblestone Bricks to be enabled")
	private static boolean enableBlackstoneBricks = true;

	@Config(flag = "dirt_bricks", description = "Requires Cobblestone Bricks to be enabled")
	private static boolean enableDirtBricks = true;

	@Config(flag = "netherrack_bricks", description = "Requires Cobblestone Bricks to be enabled")
	private static boolean enableNetherrackBricks = true;

	@LoadEvent
	public final void register(ZRegister event) {
		add(event, "blue_nether", Blocks.NETHER_BRICKS, () -> enableBlueNetherBricks, Blocks.BASALT);

		add(event, "sandstone", Blocks.SANDSTONE, () -> enableSandstoneBricks, Blocks.RED_SANDSTONE);
		add(event, "red_sandstone", Blocks.RED_SANDSTONE, () -> enableSandstoneBricks, Blocks.SEA_LANTERN);
		add(event, "soul_sandstone", Blocks.SANDSTONE, () -> enableSandstoneBricks && Quark.ZETA.modules.isEnabled(SoulSandstoneModule.class), Blocks.SEA_LANTERN);

		add(event, "cobblestone", Blocks.COBBLESTONE, () -> enableCobblestoneBricks, Blocks.MOSSY_COBBLESTONE);
		add(event, "mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, () -> enableCobblestoneBricks, Blocks.SMOOTH_STONE);

		add(event, "blackstone", Blocks.BLACKSTONE, () -> enableBlackstoneBricks && enableCobblestoneBricks, Blocks.END_STONE);
		add(event, "dirt", Blocks.DIRT, () -> enableDirtBricks && enableCobblestoneBricks, Blocks.PACKED_MUD);
		add(event, "netherrack", Blocks.NETHERRACK, () -> enableNetherrackBricks && enableCobblestoneBricks, Blocks.NETHER_BRICKS);
	}

	private void add(ZRegister event, String name, Block parent, BooleanSupplier cond, Block placeBehind) {
		event.getVariantRegistry().addSlabStairsWall((IZetaBlock) new ZetaBlock(name + "_bricks", this,
				Block.Properties.copy(parent)
						.requiresCorrectToolForDrops())
				.setCondition(cond)
				.setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, placeBehind, true), null);
	}

}
