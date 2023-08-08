package net.runeduniverse.tools.maven.compiler.pipeline;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.pipeline.api.Node;
import net.runeduniverse.tools.maven.compiler.pipeline.api.NodeContext;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Pipeline;
import net.runeduniverse.tools.maven.compiler.pipeline.api.PipelineFactory;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

@Component(role = PipelineFactory.class)
public class DefaultPipelineFactory implements PipelineFactory {

	@Override
	public Node createNode(final String key) {
		return new DefaultNode(key);
	}

	@Override
	public NodeContext createNodeContext(final Pipeline pipeline, final MavenSession mvnSession, final Node node) {
		return new DefaultNodeContext(pipeline, mvnSession, node);
	}

	@Override
	public ResourceType createResourceType(final String suffix) {
		return new DefaultResourceType(suffix);
	}

	@Override
	public Resource createResource(final ResourceType type) {
		return new DefaultResource(type);
	}

}
