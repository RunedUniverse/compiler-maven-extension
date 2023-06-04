package net.runeduniverse.tools.maven.compiler.pipeline.api;

import net.runeduniverse.lib.utils.logging.logs.Recordable;

public interface Pipeline extends Recordable {

	// return true if already registered
	public boolean registerNode(String typeId);

	public void registerNodesAsAliases(String... aliasTypeIds);

	public Node getNode(String typeId);

	// lookup node by longest match
	public Node matchNode(CharSequence typeId);

	// return true if already registered
	public boolean registerResource(Resource resource);

}
