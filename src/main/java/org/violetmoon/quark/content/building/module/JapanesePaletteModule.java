/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 03:18:35 (GMT)]
 */
package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTabs;

import org.violetmoon.quark.content.building.block.BambooMatBlock;
import org.violetmoon.quark.content.building.block.BambooMatCarpetBlock;
import org.violetmoon.quark.content.building.block.PaperLanternBlock;
import org.violetmoon.quark.content.building.block.PaperWallBlock;
import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;

import java.util.function.BooleanSupplier;

@ZetaLoadModule(category = "building")
public class JapanesePaletteModule extends ZetaModule {

	@Config(flag = "paper_decor")
	public static boolean enablePaperBlocks = true;

	@Config(flag = "bamboo_mat")
	public static boolean enableBambooMats = true;

	@LoadEvent
	public final void register(ZRegister event) {
		BooleanSupplier paperBlockCond = () -> enablePaperBlocks;
		BooleanSupplier bambooMatCond = () -> enableBambooMats;

		IZetaBlock paperLantern = new PaperLanternBlock("paper_lantern", this).setCondition(paperBlockCond);
		IZetaBlock paperLanternSakura = new PaperLanternBlock("paper_lantern_sakura", this).setCondition(paperBlockCond);

		CreativeTabManager.daisyChain();
		new PaperWallBlock(paperLantern, "paper_wall").setCondition(paperBlockCond).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, paperLantern.getBlock(), false);
		new PaperWallBlock(paperLantern, "paper_wall_big").setCondition(paperBlockCond).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, paperLantern.getBlock(), false);
		CreativeTabManager.endDaisyChain();

		new PaperWallBlock(paperLantern, "paper_wall_sakura").setCondition(paperBlockCond).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, paperLanternSakura.getBlock(), false);

		new BambooMatBlock("bamboo_mat", this).setCondition(bambooMatCond);
		new BambooMatCarpetBlock("bamboo_mat_carpet", this).setCondition(bambooMatCond);
	}

}
