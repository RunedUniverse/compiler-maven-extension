package net.runeduniverse.tools.maven.compiler.pipeline;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import net.runeduniverse.tools.maven.compiler.pipeline.api.Pipeline;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceIndex;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

public class DefaultResourceIndex implements ResourceIndex {

	private final Map<File, Resource> fileIndex = new LinkedHashMap<>();

	private final Pipeline pipeline;

	public DefaultResourceIndex(final Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public ResourceType identify(final File file) {
		if (file == null)
			return null;
		String suffix = file.getName();
		for (int dotIdx = suffix.indexOf('.'); -1 < dotIdx; dotIdx = suffix.indexOf('.')) {
			suffix = suffix.substring(dotIdx + 1);
			final ResourceType type = this.pipeline.getType(suffix);
			if (type != null)
				return type;
		}
		return this.pipeline.getType(suffix);
	}

	@Override
	public Resource createResource(File file) {
		Resource resource = this.fileIndex.get(file);
		if (resource == null)
			resource = _createResource(file);
		this.fileIndex.put(file, resource);
		return resource;
	}

	protected Resource _createResource(final File file) {
		final ResourceType type = identify(file);
		if (type == null)
			return null;
		// TODO load custom ResourceFactories + default factory
		return new DefaultResource(type, file);
	}

}
