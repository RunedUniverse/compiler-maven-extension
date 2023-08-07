package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.util.Collection;

import net.runeduniverse.lib.utils.logging.logs.Recordable;

public interface Pipeline extends Recordable {

	public Node acquireNode(String key);

	public void destroyNode(String key);

	public void destroyNode(Node node);

	public ResourceType acquireType(String suffix);

	public Collection<ResourceType> acquireTypes(String... suffixes);

	// return true if already registered
	public boolean registerResource(Resource resource);

}
