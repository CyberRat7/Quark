package org.violetmoon.quark.content.tools.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.api.IRuneColorProvider;
import org.violetmoon.quark.api.QuarkCapabilities;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.network.message.UpdateTridentMessage;
import org.violetmoon.quark.content.tools.base.RuneColor;
import org.violetmoon.quark.content.tools.client.render.GlintRenderTypes;
import org.violetmoon.quark.content.tools.item.RuneItem;
import org.violetmoon.quark.content.tools.recipe.SmithingRuneRecipe;
import org.violetmoon.zeta.advancement.ManualTrigger;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.event.play.entity.player.ZPlayerTick;
import org.violetmoon.zeta.event.play.loading.ZLootTableLoad;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.network.ZetaNetworkDirection;
import org.violetmoon.zeta.util.Hint;
import org.violetmoon.zeta.util.ItemNBTHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author WireSegal
 *         Hacked by svenhjol
 *         Created at 1:52 PM on 8/17/19.
 */
@ZetaLoadModule(category = "tools")
public class ColorRunesModule extends ZetaModule {

	public static final String TAG_RUNE_COLOR = Quark.MOD_ID + ":RuneColor";

	private static final ThreadLocal<RuneColor> targetColor = new ThreadLocal<>();
	@Hint
	public static Item rune;

	@Config
	public static int dungeonWeight = 10;
	@Config
	public static int netherFortressWeight = 8;
	@Config
	public static int jungleTempleWeight = 8;
	@Config
	public static int desertTempleWeight = 8;
	@Config
	public static int itemQuality = 0;

	public static ManualTrigger fullRainbowTrigger;

	public static void setTargetStack(ItemStack stack) {
		setTargetColor(getStackColor(stack));
	}

	public static void setTargetColor(RuneColor color) {
		targetColor.set(color);
	}

	public static RuneColor changeColor() {
		return targetColor.get();
	}

	@Nullable
	public static RuneColor getStackColor(ItemStack target) {
		if(target == null)
			return null;

		RuneColor manualColor = getAppliedStackColor(target);
		if (manualColor != null)
			return manualColor;

		@Nullable
		IRuneColorProvider cap = get(target);

		return cap != null ? cap.getRuneColor(target) : null;

	}

	@Nullable
	public static RuneColor getAppliedStackColor(ItemStack target) {
		if(target == null)
			return null;
		return RuneColor.byName(ItemNBTHelper.getString(target, TAG_RUNE_COLOR, null));
	}

	private static final Map<ThrownTrident, ItemStack> TRIDENT_STACK_REFERENCES = new WeakHashMap<>();

	public static void syncTrident(Consumer<Packet<?>> packetConsumer, ThrownTrident trident, boolean force) {
		ItemStack stack = trident.getPickupItem();
		ItemStack prev = TRIDENT_STACK_REFERENCES.get(trident);
		if(force || prev == null || ItemStack.isSameItemSameTags(stack, prev))
			packetConsumer.accept(Quark.ZETA.network.wrapInVanilla(new UpdateTridentMessage(trident.getId(), stack), ZetaNetworkDirection.PLAY_TO_CLIENT));
		else
			TRIDENT_STACK_REFERENCES.put(trident, stack);
	}

	public static ItemStack withRune(ItemStack stack, RuneColor color) {
		ItemNBTHelper.setString(stack, ColorRunesModule.TAG_RUNE_COLOR, color.getSerializedName());
		return stack;
	}

	@LoadEvent
	public final void register(ZRegister event) {
		event.getRegistry().register(SmithingRuneRecipe.SERIALIZER, "smithing_rune", Registries.RECIPE_SERIALIZER);

		rune = new RuneItem("smithing_template_rune", this);

		fullRainbowTrigger = event.getAdvancementModifierRegistry().registerManualTrigger("full_rainbow");
	}

	@PlayEvent
	public void onLootTableLoad(ZLootTableLoad event) {
		int weight = 0;

		if(event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON))
			weight = dungeonWeight;
		else if(event.getName().equals(BuiltInLootTables.NETHER_BRIDGE))
			weight = netherFortressWeight;
		else if(event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE))
			weight = jungleTempleWeight;
		else if(event.getName().equals(BuiltInLootTables.DESERT_PYRAMID))
			weight = desertTempleWeight;

		if(weight > 0) {
			LootPoolEntryContainer entry = LootItem.lootTableItem(rune)
					.setWeight(weight)
					.setQuality(itemQuality)
					.build();
			event.add(entry);
		}
	}

	@PlayEvent
	public void onPlayerTick(ZPlayerTick.Start event) {
		final String tag = "quark:what_are_you_gay_or_something";
		Player player = event.getPlayer();

		boolean wasRainbow = player.getPersistentData().getBoolean(tag);
		boolean rainbow = isPlayerRainbow(player);

		if(wasRainbow != rainbow) {
			player.getPersistentData().putBoolean(tag, rainbow);
			if(rainbow && player instanceof ServerPlayer sp)
				fullRainbowTrigger.trigger(sp);
		}
	}

	private boolean isPlayerRainbow(Player player) {
		Set<EquipmentSlot> checks = ImmutableSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

		for(EquipmentSlot slot : checks) {
			ItemStack stack = player.getItemBySlot(slot);
			if(stack.isEmpty() || getStackColor(stack) != RuneColor.RAINBOW)
				return false;
		}

		return true;
	}

	public static boolean canHaveRune(ItemStack stack) {
		return stack.isEnchanted() || (stack.getItem() == Items.COMPASS && CompassItem.isLodestoneCompass(stack)); // isLodestoneCompass = is lodestone compass
	}

	public static Component extremeRainbow(Component component) {
		String emphasis = component.getString();

		float time = Quark.proxy.getVisualTime();

		MutableComponent emphasized = Component.empty();
		for (int i = 0; i < emphasis.length(); i++) {
			emphasized.append(rainbow(Component.literal("" + emphasis.charAt(i)), i, time));
		}

		return emphasized;
	}

	private static MutableComponent rainbow(MutableComponent component, int shift, float time) {
		return component.withStyle((s) -> s.withColor(
			TextColor.fromRgb(Mth.hsvToRgb((time + shift) * 2 % 360 / 360F, 1F, 1F))));
	}

	public static void appendRuneText(ItemStack stack, List<Component> components, Component upgradeTitle) {
		RuneColor color = getAppliedStackColor(stack);
		if (color != null) {
			if (!components.contains(upgradeTitle))
				components.add(upgradeTitle);

			MutableComponent baseComponent = Component.translatable("rune.quark." + color.getName());

			if (color == RuneColor.RAINBOW)
				components.add(CommonComponents.space().append(extremeRainbow(baseComponent)));
			else
				components.add(CommonComponents.space().append(baseComponent
					.withStyle((style) -> style.withColor(color.getTextColor()))));
		}
	}

	private static @Nullable IRuneColorProvider get(ItemStack stack) {
		return Quark.ZETA.capabilityManager.getCapability(QuarkCapabilities.RUNE_COLOR, stack);
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends ColorRunesModule {

		public static RenderType getGlint() {
			return renderType(GlintRenderTypes.glint, RenderType::glint);
		}

		public static RenderType getGlintTranslucent() {
			return renderType(GlintRenderTypes.glintTranslucent, RenderType::glintTranslucent);
		}

		public static RenderType getEntityGlint() {
			return renderType(GlintRenderTypes.entityGlint, RenderType::entityGlint);
		}

		public static RenderType getGlintDirect() {
			return renderType(GlintRenderTypes.glintDirect, RenderType::glintDirect);
		}

		public static RenderType getEntityGlintDirect() {
			return renderType(GlintRenderTypes.entityGlintDirect, RenderType::entityGlintDirect);
		}

		public static RenderType getArmorGlint() {
			return renderType(GlintRenderTypes.armorGlint, RenderType::armorGlint);
		}

		public static RenderType getArmorEntityGlint() {
			return renderType(GlintRenderTypes.armorEntityGlint, RenderType::armorEntityGlint);
		}

		private static RenderType renderType(Map<RuneColor, RenderType> map, Supplier<RenderType> vanilla) {
			RuneColor color = changeColor();
			return color != null ? map.get(color) : vanilla.get();
		}

	}
}
