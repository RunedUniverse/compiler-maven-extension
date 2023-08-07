package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;
import java.util.Collection;

public interface ResourceRegistry {

	public Resource acquireResource(File file);

	public Collection<Resource> selectBatch(Node node);

}
