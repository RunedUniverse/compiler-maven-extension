package net.runeduniverse.tools.maven.compiler.pipeline;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import net.runeduniverse.lib.utils.logging.logs.CompoundTree;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Node;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Pipeline;
import net.runeduniverse.tools.maven.compiler.pipeline.api.PipelineFactory;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

@Component(role = Pipeline.class)
public class DefaultPipeline implements Pipeline {

	private final Map<String, Node> nodes = new LinkedHashMap<>();
	private final Map<String, ResourceType> resourceTypes = new LinkedHashMap<>();

	@Requirement
	private PipelineFactory factory;

	@Override
	public Node acquireNode(String key) {
		Node node = this.nodes.get(key);
		if (node == null) {
			node = this.factory.createNode(key);
			if (node != null)
				this.nodes.put(node.getKey(), node);
		}
		return node;
	}

	@Override
	public void destroyNode(String key) {
		this.nodes.remove(key);
	}

	@Override
	public void destroyNode(Node node) {
		if (node == null)
			return;
		destroyNode(node.getKey());
	}

	@Override
	public ResourceType acquireType(String suffix) {
		ResourceType type = this.resourceTypes.get(suffix);
		if (type == null) {
			type = this.factory.createResourceType(suffix);
			if (type != null)
				this.resourceTypes.put(type.getSuffix(), type);
		}
		return type;
	}

	@Override
	public Collection<ResourceType> acquireTypes(String... suffixes) {
		final Set<ResourceType> types = new LinkedHashSet<>();
		for (String suffix : suffixes)
			types.add(acquireType(suffix));
		return types;
	}

	@Override
	public boolean registerResource(Resource resource) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CompoundTree toRecord() {
		CompoundTree nodes = new CompoundTree("nodes");
		for (Node node : this.nodes.values())
			nodes.append(node.toRecord());
		CompoundTree resourceTypes = new CompoundTree("resourceTypes");
		for (String suffix : this.resourceTypes.keySet())
			resourceTypes.append(suffix);
		return new CompoundTree("Pipeline").append(nodes)
				.append(resourceTypes);
	}

}
