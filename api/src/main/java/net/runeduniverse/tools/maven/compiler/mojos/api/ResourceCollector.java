package net.runeduniverse.tools.maven.compiler.mojos.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceIndex;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

public class ResourceCollector implements FileVisitor<Path> {

	private final Map<Object, Resource> resources = new LinkedHashMap<>();

	private final ResourceIndex index;
	private final Set<ResourceType> filterTypes;

	public ResourceCollector(final ResourceIndex index, final ResourceType... filterTypes) {
		this(index, new LinkedHashSet<>());

		if (filterTypes == null)
			return;
		for (ResourceType resourceType : filterTypes)
			this.filterTypes.add(resourceType);
	}

	public ResourceCollector(final ResourceIndex index, final Set<ResourceType> filterTypes) {
		this.index = index;
		this.filterTypes = filterTypes;
	}

	public Set<Resource> getResources() {
		return new LinkedHashSet<>(this.resources.values());
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		if (!attrs.isRegularFile())
			return FileVisitResult.CONTINUE;

		final File file = path.toFile();
		if (this.filterTypes.isEmpty()) {
			this.resources.put(attrs.fileKey(), this.index.createResource(file));
			return FileVisitResult.CONTINUE;
		}

		if (this.filterTypes.contains(this.index.identify(file)))
			this.resources.put(attrs.fileKey(), this.index.createResource(file));

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

}
