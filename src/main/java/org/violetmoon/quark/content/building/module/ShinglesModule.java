package org.violetmoon.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class ShinglesModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.TERRACOTTA)));

		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("white_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.WHITE_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("orange_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.ORANGE_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("magenta_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.MAGENTA_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("light_blue_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.LIGHT_BLUE_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("yellow_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.YELLOW_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("lime_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.LIME_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("pink_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.PINK_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("gray_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.GRAY_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("light_gray_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.LIGHT_GRAY_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("cyan_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.CYAN_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("purple_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.PURPLE_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("blue_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.BLUE_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("brown_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.BROWN_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("green_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.GREEN_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("red_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.RED_TERRACOTTA)));
		event.getVariantRegistry().addSlabAndStairs(new ZetaBlock("black_shingles", this, "BUILDING_BLOCKS", Block.Properties.copy(Blocks.BLACK_TERRACOTTA)));
	}

}
