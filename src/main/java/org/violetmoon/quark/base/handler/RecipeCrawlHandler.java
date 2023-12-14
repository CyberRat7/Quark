package org.violetmoon.quark.base.handler;

import com.google.common.collect.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZAddReloadListener;
import org.violetmoon.zeta.event.load.ZTagsUpdated;
import org.violetmoon.zeta.event.play.ZServerTick;
import org.violetmoon.zeta.event.play.ZRecipeCrawl;
import org.violetmoon.zeta.util.RegistryUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

// TODO TODO: split off from Quark

public class RecipeCrawlHandler {

	private static final List<Recipe<?>> recipesToLazyDigest = new ArrayList<>();
	private static final Multimap<Item, ItemStack> recipeDigestion = HashMultimap.create();
	private static final Multimap<Item, ItemStack> backwardsDigestion = HashMultimap.create();

	private static final Object mutex = new Object();
	private static boolean needsCrawl = false;
	private static boolean mayCrawl = false;

	@LoadEvent
	public static void addListener(ZAddReloadListener event) {
		event.addListener(new SimplePreparableReloadListener<Void>() {
			@Override
			protected Void prepare(ResourceManager mgr, ProfilerFiller prof) {
				clear();
				return null;
			}

			@Override
			protected void apply(Void what, ResourceManager mgr, ProfilerFiller prof) {
				needsCrawl = true;
			}
		});
	}

	@LoadEvent
	public static void tagsHaveUpdated(ZTagsUpdated event) {
		mayCrawl = true;
	}

	private static void clear() {
		mayCrawl = false;
		Quark.ZETA.playBus.fire(new ZRecipeCrawl.Reset());
	}

	@SuppressWarnings("ConstantValue") // some nullchecks on stuff that is ostensibly non-null, but you never know with mods
	private static void load(RecipeManager manager, RegistryAccess access) {
		if(!manager.getRecipes().isEmpty()) {
			Quark.ZETA.playBus.fire(new ZRecipeCrawl.Starting());

			recipesToLazyDigest.clear();
			recipeDigestion.clear();
			backwardsDigestion.clear();

			for(Recipe<?> recipe : manager.getRecipes()) {
				try {
					if(recipe == null)
						throw new IllegalStateException("Recipe is null");
					if(recipe.getIngredients() == null)
						throw new IllegalStateException("Recipe ingredients are null");
					if(recipe.getResultItem(access) == null)
						throw new IllegalStateException("Recipe getResultItem is null");

					ZRecipeCrawl.Visit<?> event;
					if (recipe instanceof ShapedRecipe sr)
						event = new ZRecipeCrawl.Visit.Shaped(sr, access);
					else if (recipe instanceof ShapelessRecipe sr)
						event = new ZRecipeCrawl.Visit.Shapeless(sr, access);
					else if (recipe instanceof CustomRecipe cr)
						event = new ZRecipeCrawl.Visit.Custom(cr, access);
					else if (recipe instanceof AbstractCookingRecipe acr)
						event = new ZRecipeCrawl.Visit.Cooking(acr, access);
					else
						event = new ZRecipeCrawl.Visit.Misc(recipe, access);

					recipesToLazyDigest.add(recipe);
					Quark.ZETA.playBus.fire(event);
				} catch (Exception e) {
					if(recipe == null)
						Quark.LOG.error("Encountered null recipe in RecipeManager.getRecipes. This is not good");
					else
						Quark.LOG.error("Failed to scan recipe " + recipe.getId() + ". This should be reported to " + recipe.getId().getNamespace() + "!", e);
				}
			}
		}
	}

	@PlayEvent
	public static void onTick(ZServerTick.Start tick) {
		synchronized(mutex) {
			if(mayCrawl && needsCrawl) {
				RecipeManager manager = tick.getServer().getRecipeManager();
				RegistryAccess access = tick.getServer().registryAccess();
				load(manager, access);
				needsCrawl = false;
			}

			if(!recipesToLazyDigest.isEmpty()) {
				recipeDigestion.clear();
				backwardsDigestion.clear();

				for(Recipe<?> recipe : recipesToLazyDigest)
					digest(recipe, tick.getServer().registryAccess());

				recipesToLazyDigest.clear();
				Quark.ZETA.playBus.fire(new ZRecipeCrawl.Digest(recipeDigestion, backwardsDigestion));
			}
		}
	}

	private static void digest(Recipe<?> recipe, RegistryAccess access) {
		ItemStack out = recipe.getResultItem(access);
		Item outItem = out.getItem();

		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		for(Ingredient ingredient : ingredients) {
			for (ItemStack inStack : ingredient.getItems()) {
				recipeDigestion.put(inStack.getItem(), out);
				backwardsDigestion.put(outItem, inStack);
			}
		}
	}

	/*
	 * Derivation list -> items to add and then derive (raw materials)
	 * Whitelist -> items to add and not derive from
	 * Blacklist -> items to ignore
	 */

	public static void recursivelyFindCraftedItemsFromStrings(@Nullable Collection<String> derivationList, @Nullable Collection<String> whitelist, @Nullable Collection<String> blacklist, Consumer<Item> callback) {
		List<Item> parsedDerivationList = derivationList == null ? null : RegistryUtil.massRegistryGet(derivationList, BuiltInRegistries.ITEM);
		List<Item> parsedWhitelist      = whitelist == null      ? null : RegistryUtil.massRegistryGet(whitelist, BuiltInRegistries.ITEM);
		List<Item> parsedBlacklist      = blacklist == null      ? null : RegistryUtil.massRegistryGet(blacklist, BuiltInRegistries.ITEM);

		recursivelyFindCraftedItems(parsedDerivationList, parsedWhitelist, parsedBlacklist, callback);
	}

	public static void recursivelyFindCraftedItems(@Nullable Collection<Item> derivationList, @Nullable Collection<Item> whitelist, @Nullable Collection<Item> blacklist, Consumer<Item> callback) {
		Collection<Item> trueDerivationList = derivationList == null  ? Lists.newArrayList() : derivationList;
		Collection<Item> trueWhitelist      = whitelist == null       ? Lists.newArrayList() : whitelist;
		Collection<Item> trueBlacklist      = blacklist == null       ? Lists.newArrayList() : blacklist;

		Streams.concat(trueDerivationList.stream(), trueWhitelist.stream()).forEach(callback);

		Set<Item> scanned = Sets.newHashSet(trueDerivationList);
		List<Item> toScan = Lists.newArrayList(trueDerivationList);

		while (!toScan.isEmpty()) {
			Item scan = toScan.remove(0);

			if (recipeDigestion.containsKey(scan)) {
				for (ItemStack digestedStack : recipeDigestion.get(scan)) {
					Item candidate = digestedStack.getItem();

					if (!scanned.contains(candidate)) {
						scanned.add(candidate);
						toScan.add(candidate);

						if(!trueBlacklist.contains(candidate))
							callback.accept(candidate);
					}
				}
			}
		}
	}

}
