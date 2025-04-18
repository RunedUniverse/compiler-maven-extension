package net.runeduniverse.tools.maven.compiler.pipeline;

import static net.runeduniverse.lib.utils.common.StringUtils.isBlank;
import static net.runeduniverse.lib.utils.maven.SessionContextUtils.loadSessionComponent;
import static net.runeduniverse.lib.utils.maven.SessionContextUtils.putSessionComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import net.runeduniverse.lib.utils.logging.log.DefaultCompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Node;
import net.runeduniverse.tools.maven.compiler.pipeline.api.NodeContext;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Phase;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Pipeline;
import net.runeduniverse.tools.maven.compiler.pipeline.api.PipelineFactory;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceIndex;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

@Component(role = Pipeline.class)
public class DefaultPipeline implements Pipeline {

	private final Map<String, Node> nodes = new LinkedHashMap<>();
	private final Map<String, Set<Node>> nodesPerPhase = new LinkedHashMap<>();
	private final Map<String, ResourceType> resourceTypes = new LinkedHashMap<>();
	private final Set<String> phaseOrder = new LinkedHashSet<>();

	@Requirement
	private PipelineFactory factory;

	public DefaultPipeline() {
		for (Phase phase : Phase.values()) {
			this.phaseOrder.add(phase.getId());
		}
	}

	@Override
	public Node acquireNode(String key) {
		Node node = this.nodes.get(key);
		if (node == null) {
			node = this.factory.createNode(key);
			if (node != null) {
				this.nodes.put(node.getKey(), node);
				final String phase = toPhase(key);
				Set<Node> phases = this.nodesPerPhase.get(phase);
				if (phases == null) {
					phases = new LinkedHashSet<>();
					this.nodesPerPhase.put(phase, phases);
				}
				phases.add(node);
			}
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
	public NodeContext getNodeContext(final MavenSession mvnSession, String key) {
		final Node node = this.nodes.get(key);
		if (node == null)
			return null;
		NodeContext context = loadSessionComponent(mvnSession, NodeContext.class, key);
		if (context == null) {
			context = this.factory.createNodeContext(this, mvnSession, node);
			putSessionComponent(mvnSession, NodeContext.class, key, context);
		}
		return context;
	}

	@Override
	public NodeContext getNodeContext(final MavenSession mvnSession, Phase phase, String id) {
		return getNodeContext(mvnSession, toKey(phase, id));
	}

	@Override
	public Collection<Node> getNodesForType(final ResourceType type) {
		final Set<Node> nodes = new LinkedHashSet<>();
		for (Node node : this.nodes.values()) {
			if (node.getResourceTypes()
					.contains(type))
				nodes.add(node);
		}
		return nodes;
	}

	@Override
	public Node getNextNodeForType(final String phase, final ResourceType type) {
		boolean found = false;
		if (isBlank(phase))
			found = true;
		for (Iterator<String> i = this.phaseOrder.iterator(); i.hasNext();) {
			if (found) {
				final Collection<Node> nodes = this.nodesPerPhase.get(i.next());
				if (nodes == null)
					continue;
				for (Node node : nodes)
					if (node.getResourceTypes()
							.contains(type))
						return node;
			} else if (phase.equals(i.next()))
				found = true;
		}
		return null;
	}

	@Override
	public void addResourceToNextNodeContext(final MavenSession mvnSession, final String curPhaseId,
			final Resource resource) {
		if (resource == null)
			return;
		final Node node = getNextNodeForType(curPhaseId, resource.getType());
		if (node == null)
			return;
		final NodeContext context = getNodeContext(mvnSession, node.getKey());
		context.addResource(resource);
	}

	@Override
	public ResourceType getType(final String suffix) {
		return this.resourceTypes.get(suffix);
	}

	@Override
	public ResourceIndex getResourceIndex(final MavenSession mvnSession) {
		ResourceIndex index = loadSessionComponent(mvnSession, ResourceIndex.class, "default");
		if (index == null) {
			index = this.factory.createResourceIndex(this);
			putSessionComponent(mvnSession, ResourceIndex.class, "default", index);
		}
		return index;
	}

	@Override
	public Collection<String> getResourceSuffixes() {
		return Collections.unmodifiableSet(this.resourceTypes.keySet());
	}

	@Override
	public Collection<ResourceType> getResourceTypes() {
		return Collections.unmodifiableCollection(this.resourceTypes.values());
	}

	@Override
	public CompoundTree toRecord() {
		CompoundTree nodes = new DefaultCompoundTree("nodes");
		for (Node node : this.nodes.values())
			nodes.append(node.toRecord());
		CompoundTree resourceTypes = new DefaultCompoundTree("resourceTypes",
				'[' + String.join(", ", this.resourceTypes.keySet()) + ']');
		return new DefaultCompoundTree("Pipeline").append(nodes)
				.append(resourceTypes);
	}

	protected static String toKey(final Phase phase, String id) {
		String phaseId = "";
		if (phase != null && !isBlank(phase.getId()))
			phaseId = phase.getId();
		if (isBlank(id))
			id = "null";
		return phaseId + ':' + id;
	}

	protected static String toPhase(final String key) {
		if (key == null)
			return null;
		final String phase = key.substring(0, key.indexOf(':'));
		if (isBlank(phase))
			return null;
		return phase;
	}
}
