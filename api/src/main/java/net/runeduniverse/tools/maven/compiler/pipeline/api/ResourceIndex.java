package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;

public interface ResourceIndex {

	public ResourceType identify(final File file);

	public Resource createResource(final File file);

}
