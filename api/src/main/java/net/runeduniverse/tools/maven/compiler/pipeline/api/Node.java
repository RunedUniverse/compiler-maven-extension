package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.util.Collection;

import net.runeduniverse.lib.utils.logging.logs.Recordable;

public interface Node extends Recordable {

	public String getTypeId();

	public Collection<Resource> getResources();

	public void addResource(Resource resource);

}
