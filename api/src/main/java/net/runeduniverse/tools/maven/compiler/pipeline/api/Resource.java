package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;
import java.util.Collection;

import net.runeduniverse.lib.utils.logging.logs.Recordable;

public interface Resource extends Recordable {

	public File getFile();

	public Resource getSource();

	public Collection<Resource> getImportedSources();

	public Resource setSource(Resource source);

	public Resource addImportedSource(Resource source);

	public Resource addImportedSources(Collection<Resource> sources);

}
