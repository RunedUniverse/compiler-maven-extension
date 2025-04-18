package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;
import java.util.Collection;

import net.runeduniverse.lib.utils.logging.log.api.Recordable;

public interface Resource extends Recordable {

	public ResourceType getType();

	public File getFile();

	public Collection<String> getTags();

	public Collection<Resource> getSources();

	public Collection<Resource> getImportedSources();

	public Resource addTags(String... tags);

	public Resource addTags(Collection<String> tags);

	public Resource addSources(Resource... sources);

	public Resource addSources(Collection<Resource> sources);

	public Resource addImportedSources(Resource... sources);

	public Resource addImportedSources(Collection<Resource> sources);

}
