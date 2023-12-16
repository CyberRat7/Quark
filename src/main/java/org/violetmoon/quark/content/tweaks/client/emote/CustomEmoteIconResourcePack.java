package org.violetmoon.quark.content.tweaks.client.emote;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.tweaks.module.EmotesModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class CustomEmoteIconResourcePack extends AbstractPackResources {

	private final List<String> verifiedNames = new ArrayList<>();
	private final List<String> existingNames = new ArrayList<>();

	public CustomEmoteIconResourcePack() {
		super("quark-emote-pack", true);
		//super(EmotesModule.emotesDir);
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String @NotNull... file) {
		return null;
	}

	//Todo: Dawg this shit is probably broken as hell - Siuol
	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(@NotNull PackType packType, ResourceLocation name) {
		if(name.getPath().equals("pack.mcmeta")) {
			try {
				return IoSupplier.create(Path.of(Quark.class.getResource("/proxypack.mcmeta").toURI()));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}

		if(name.getPath().equals("pack.png")) {
			try {
				return IoSupplier.create(Path.of(Quark.class.getResource("/proxypack.png").toURI()));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}

		File file = getFile(name.getPath());
		if(!file.exists())
			try {
				throw new FileNotFoundException(name.getPath());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}

		return IoSupplier.create(file.toPath());
	}

	@Override
	public void listResources(@NotNull PackType packType, @NotNull String thing, @NotNull String ugh, @NotNull ResourceOutput resourceOutput) {

	}

	@NotNull
	@Override
	public Set<String> getNamespaces(@NotNull PackType type) {
		if(type == PackType.CLIENT_RESOURCES)
			return ImmutableSet.of(EmoteHandler.CUSTOM_EMOTE_NAMESPACE);
		return ImmutableSet.of();
	}

	@NotNull
	public Collection<ResourceLocation> getResources(@NotNull PackType type, @NotNull String pathIn, @NotNull String idk, @NotNull Predicate<ResourceLocation> filter) {
		File rootPath = new File(this.getFile(idk), type.getDirectory());
		List<ResourceLocation> allResources = Lists.newArrayList();

		for(String namespace : this.getNamespaces(type))
			this.crawl(new File(new File(rootPath, namespace), pathIn), 32, namespace, allResources, pathIn + "/", filter);

		return allResources;
	}

	private void crawl(File rootPath, int maxDepth, String namespace, List<ResourceLocation> allResources, String path, Predicate<ResourceLocation> filter) {
		File[] files = rootPath.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.isDirectory()) {
					if(maxDepth > 0)
						this.crawl(file, maxDepth - 1, namespace, allResources, path + file.getName() + "/", filter);
				} else if(!file.getName().endsWith(".mcmeta") && filter.test(new ResourceLocation(namespace, path + file.getName()))) {
					try {
						allResources.add(new ResourceLocation(namespace, path + file.getName()));
					} catch (ResourceLocationException e) {
						Quark.LOG.error(e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void close() {
		// NO-OP
	}

	protected boolean hasResource(@NotNull String name) {
		if(!verifiedNames.contains(name)) {
			File file = getFile(name);
			if(file.exists())
				existingNames.add(name);
			verifiedNames.add(name);
		}

		return existingNames.contains(name);
	}

	private File getFile(String name) {
		String filename = name.substring(name.indexOf(":") + 1) + ".png";
		filename = filename.replaceAll("(.+/)+", "");

		return new File(EmotesModule.emotesDir, filename);
	}

	@Override
	public boolean isHidden() {
		return true;
	}

}
