package vazkii.quark.content.automation.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.automation.block.EnderWatcherBlock;
import vazkii.quark.content.automation.block.be.EnderWatcherBlockEntity;

@LoadModule(category = ModuleCategory.AUTOMATION)
public class EnderWatcherModule extends QuarkModule {

	public static BlockEntityType<EnderWatcherBlockEntity> blockEntityType;
	
	public static QuarkGenericTrigger watcherCenterTrigger;
	@Hint Block ender_watcher;

	@Override
	public void register() {
		ender_watcher = new EnderWatcherBlock(this);
		blockEntityType = BlockEntityType.Builder.of(EnderWatcherBlockEntity::new, ender_watcher).build(null);
		RegistryHelper.register(blockEntityType, "ender_watcher", ForgeRegistries.BLOCK_ENTITY_TYPES);
		
		watcherCenterTrigger = QuarkAdvancementHandler.registerGenericTrigger("watcher_center");
	}
	
}
