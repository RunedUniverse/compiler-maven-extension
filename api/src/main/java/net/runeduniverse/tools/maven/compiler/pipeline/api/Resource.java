package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;
import java.util.Collection;

import net.runeduniverse.lib.utils.logging.logs.Recordable;

public interface Resource extends Recordable {

	public File getFile();

	public Collection<String> getTags();

	public ResourceType getType();

	public Collection<Resource> getSources();

	public Collection<Resource> getImportedSources();

	public Resource addTags(String... tags);

	public Resource addTags(Collection<String> tags);

	public Resource addSources(Resource... sources);

	public Resource addSources(Collection<Resource> sources);

	public Resource addImportedSource(Resource source);

	public Resource addImportedSources(Collection<Resource> sources);

}
