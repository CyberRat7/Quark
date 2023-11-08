package org.violetmoon.zetaimplforge.client.event.player;

import org.violetmoon.zeta.client.event.play.ZRenderTick;

import net.minecraftforge.event.TickEvent;

public record ForgeZRenderTick(TickEvent.RenderTickEvent e) implements ZRenderTick {
	@Override
	public float getRenderTickTime() {
		return e.renderTickTime;
	}

	@Override
	public boolean isEndPhase() {
		return e.phase == TickEvent.Phase.END;
	}
}
