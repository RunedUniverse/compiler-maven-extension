package net.runeduniverse.tools.maven.compiler.pipeline;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

public class DefaultResource implements Resource {

	protected final Set<String> tags = new LinkedHashSet<>();
	protected final Set<Resource> sources = new LinkedHashSet<>();
	protected final Set<Resource> importedSources = new LinkedHashSet<>();

	protected final ResourceType type;
	protected final File file;

	public DefaultResource(final ResourceType type, final File file) {
		this.type = type;
		this.file = file;
	}

	@Override
	public ResourceType getType() {
		return this.type;
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public Collection<String> getTags() {
		return Collections.unmodifiableCollection(this.tags);
	}

	@Override
	public Collection<Resource> getSources() {
		return this.sources;
	}

	@Override
	public Collection<Resource> getImportedSources() {
		return this.importedSources;
	}

	@Override
	public Resource addTags(String... tags) {
		for (String tag : tags)
			this.tags.add(tag);
		return this;
	}

	@Override
	public Resource addTags(Collection<String> tags) {
		this.tags.addAll(tags);
		return this;
	}

	@Override
	public Resource addSources(Resource... sources) {
		for (Resource resource : sources)
			this.sources.add(resource);
		return this;
	}

	@Override
	public Resource addSources(Collection<Resource> sources) {
		this.sources.addAll(sources);
		return this;
	}

	@Override
	public Resource addImportedSources(Resource... sources) {
		for (Resource resource : importedSources)
			this.importedSources.add(resource);
		return this;
	}

	@Override
	public Resource addImportedSources(Collection<Resource> sources) {
		this.importedSources.addAll(sources);
		return this;
	}

	@Override
	public CompoundTree toRecord() {
		// TODO Auto-generated method stub
		return null;
	}

}
