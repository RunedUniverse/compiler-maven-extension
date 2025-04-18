package net.runeduniverse.tools.maven.compiler.pipeline;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.lib.utils.logging.log.DefaultCompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Node;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

import static net.runeduniverse.lib.utils.common.StringUtils.isBlank;

public class DefaultNode implements Node {

	protected final String key;
	protected final Map<String, ResourceType> resourceTypes;

	public DefaultNode(final String key) {
		this.key = key;
		this.resourceTypes = new LinkedHashMap<>();
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public void registerResourceType(ResourceType type) {
		if (type == null || isBlank(type.getSuffix()))
			return;
		this.resourceTypes.put(type.getSuffix(), type);
	}

	@Override
	public void registerResourceTypes(Collection<ResourceType> types) {
		for (ResourceType type : types)
			registerResourceType(type);
	}

	@Override
	public Collection<ResourceType> getResourceTypes() {
		return Collections.unmodifiableCollection(this.resourceTypes.values());
	}

	@Override
	public CompoundTree toRecord() {
		final Set<String> suffixes = new LinkedHashSet<>();
		for (String suffix : this.resourceTypes.keySet())
			suffixes.add(suffix);
		return new DefaultCompoundTree("Node", this.key).append("suffixes", String.join(", ", suffixes));
	}

}
