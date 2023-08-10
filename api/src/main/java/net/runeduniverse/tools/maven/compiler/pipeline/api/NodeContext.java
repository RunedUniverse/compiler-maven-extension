package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;
import java.util.Collection;

public interface NodeContext {

	public Node getNode();

	public Collection<Resource> getResources();

	public NodeContext addResource(final Resource resource);

	public NodeContext addResult(final Resource result);

	public Resource addResult(final File file);

}
