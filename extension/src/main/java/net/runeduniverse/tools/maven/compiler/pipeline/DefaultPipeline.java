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
import net.runeduniverse.tools.maven.compiler.pipeline.api.Phase;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Pipeline;
import net.runeduniverse.tools.maven.compiler.pipeline.api.PipelineFactory;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

import static net.runeduniverse.lib.utils.common.StringUtils.isBlank;

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
	public Node acquireNode(Phase phase, String id) {
		return acquireNode(toKey(phase, id));
	}

	@Override
	public void destroyNode(String key) {
		this.nodes.remove(key);
	}

	@Override
	public void destroyNode(Phase phase, String id) {
		destroyNode(toKey(phase, id));
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
	public CompoundTree toRecord() {
		CompoundTree nodes = new CompoundTree("nodes");
		for (Node node : this.nodes.values())
			nodes.append(node.toRecord());
		CompoundTree resourceTypes = new CompoundTree("resourceTypes",
				'[' + String.join(", ", this.resourceTypes.keySet()) + ']');
		return new CompoundTree("Pipeline").append(nodes)
				.append(resourceTypes);
	}

	protected static String toKey(Phase phase, String id) {
		String phaseId = "";
		if (phase != null && !isBlank(phase.getId()))
			phaseId = phase.getId();
		if (isBlank(id))
			id = "null";
		return phaseId + ':' + id;
	}

}
