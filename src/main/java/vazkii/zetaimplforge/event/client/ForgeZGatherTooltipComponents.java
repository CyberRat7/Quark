package vazkii.zetaimplforge.event.client;

import java.util.List;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import vazkii.zeta.client.event.ZGatherTooltipComponents;

public record ForgeZGatherTooltipComponents(RenderTooltipEvent.GatherComponents e) implements ZGatherTooltipComponents {
	@Override
	public ItemStack getItemStack() {
		return e.getItemStack();
	}

	@Override
	public int getScreenWidth() {
		return e.getScreenWidth();
	}

	@Override
	public int getScreenHeight() {
		return e.getScreenHeight();
	}

	@Override
	public List<Either<FormattedText, TooltipComponent>> getTooltipElements() {
		return e.getTooltipElements();
	}

	@Override
	public int getMaxWidth() {
		return e.getMaxWidth();
	}

	@Override
	public void setMaxWidth(int maxWidth) {
		e.setMaxWidth(maxWidth);
	}
}
