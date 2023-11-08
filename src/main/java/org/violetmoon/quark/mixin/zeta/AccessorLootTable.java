package org.violetmoon.quark.mixin.zeta;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootTable.class)
public interface AccessorLootTable {

	@Accessor("pools")
	List<LootPool> zeta$getPools();

}
