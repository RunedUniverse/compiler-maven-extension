package net.runeduniverse.tools.maven.compiler.pipeline;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.execution.MavenSession;

import net.runeduniverse.tools.maven.compiler.pipeline.api.Node;
import net.runeduniverse.tools.maven.compiler.pipeline.api.NodeContext;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Pipeline;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

public class DefaultNodeContext implements NodeContext {

	protected final Pipeline pipeline;
	protected final MavenSession mvnSession;
	protected final Node node;
	protected final Set<Resource> resources = new LinkedHashSet<>();

	public DefaultNodeContext(final Pipeline pipeline, final MavenSession mvnSession, final Node node) {
		this.pipeline = pipeline;
		this.mvnSession = mvnSession;
		this.node = node;
	}

	@Override
	public Node getNode() {
		return this.node;
	}

	@Override
	public Collection<Resource> getResources() {
		return Collections.unmodifiableCollection(this.resources);
	}

	@Override
	public NodeContext addResource(Resource resource) {
		this.resources.add(resource);
		return this;
	}

	@Override
	public NodeContext addResult(Resource result) {
		final Node node = this.pipeline.getNextNodeForType(DefaultPipeline.toPhase(this.node.getKey()),
				result.getType());
		if (node == null)
			return null;
		NodeContext context = this.pipeline.getNodeContext(this.mvnSession, node.getKey());
		context.addResource(result);
		return this;
	}

	@Override
	public Resource addResult(ResourceType type) {
		Resource result = this.pipeline.createResource(type);
		addResult(result);
		return result;
	}

}
