package net.runeduniverse.tools.maven.compiler.pipeline.api;

import org.apache.maven.execution.MavenSession;

public interface PipelineFactory {

	public Node createNode(final String key);

	public NodeContext createNodeContext(final Pipeline pipeline, final MavenSession mvnSession, final Node node);

	public ResourceType createResourceType(final String suffix);

	public ResourceIndex createResourceIndex(final Pipeline pipeline);

}
