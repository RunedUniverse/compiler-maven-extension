package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.util.Collection;

import net.runeduniverse.lib.utils.logging.logs.Recordable;

public interface Node extends Recordable {

	// <phase>:<language>
	public String getKey();

	public void registerResourceType(ResourceType type);

	public void registerResourceTypes(Collection<ResourceType> types);

	public Collection<ResourceType> getResourceTypes();

}
