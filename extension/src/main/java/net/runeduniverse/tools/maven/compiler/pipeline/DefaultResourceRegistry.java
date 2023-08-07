package net.runeduniverse.tools.maven.compiler.pipeline;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import net.runeduniverse.tools.maven.compiler.pipeline.api.Node;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceRegistry;

public class DefaultResourceRegistry implements ResourceRegistry {

	@Override
	public Resource acquireResource(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Resource> selectBatch(Node node) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

}
