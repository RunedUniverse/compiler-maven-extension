package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.util.Collection;

import org.apache.maven.execution.MavenSession;

import net.runeduniverse.lib.utils.logging.log.api.Recordable;

public interface Pipeline extends Recordable {

	public Node acquireNode(String key);

	public Node acquireNode(Phase phase, String id);

	public void destroyNode(String key);

	public void destroyNode(Phase phase, String id);

	public void destroyNode(Node node);

	public ResourceType acquireType(String suffix);

	public Collection<ResourceType> acquireTypes(String... suffixes);

	public Collection<Node> getNodesForType(final ResourceType type);

	public Node getNextNodeForType(final String currentPhaseId, final ResourceType type);

	public NodeContext getNodeContext(final MavenSession mvnSession, final String key);

	public NodeContext getNodeContext(final MavenSession mvnSession, final Phase phase, final String id);

	public void addResourceToNextNodeContext(final MavenSession mvnSession, final String curPhaseId,
			final Resource resource);

	public ResourceType getType(String suffix);

	public ResourceIndex getResourceIndex(final MavenSession mvnSession);

	public Collection<String> getResourceSuffixes();

	public Collection<ResourceType> getResourceTypes();

}
