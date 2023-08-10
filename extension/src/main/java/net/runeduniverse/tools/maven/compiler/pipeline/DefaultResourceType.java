package net.runeduniverse.tools.maven.compiler.pipeline;

import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

public class DefaultResourceType implements ResourceType {

	protected final String suffix;

	public DefaultResourceType(final String suffix) {
		this.suffix = suffix;
	}

	@Override
	public String getSuffix() {
		return this.suffix;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ResourceType))
			return false;
		final String suffix = ((ResourceType) obj).getSuffix();
		if (this.suffix == null && suffix == null)
			return true;
		return this.suffix.equals(suffix);
	}

}
