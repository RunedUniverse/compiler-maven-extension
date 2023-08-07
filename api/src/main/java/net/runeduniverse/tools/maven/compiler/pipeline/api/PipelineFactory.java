package net.runeduniverse.tools.maven.compiler.pipeline.api;

public interface PipelineFactory {

	public Node createNode(String key);

	public ResourceType createResourceType(String suffix);

}
