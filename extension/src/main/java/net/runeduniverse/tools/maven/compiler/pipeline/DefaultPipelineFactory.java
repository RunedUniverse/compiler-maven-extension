package net.runeduniverse.tools.maven.compiler.pipeline;

import java.io.File;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.pipeline.api.Node;
import net.runeduniverse.tools.maven.compiler.pipeline.api.PipelineFactory;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;
import net.runeduniverse.tools.maven.compiler.pipeline.api.ResourceType;

@Component(role = PipelineFactory.class)
public class DefaultPipelineFactory implements PipelineFactory {

	@Override
	public Node createNode(String key) {
		return new DefaultNode(key);
	}

	@Override
	public ResourceType createResourceType(String suffix) {
		return new DefaultResourceType(suffix);
	}

	@Override
	public Resource createResource(File file) {
		// TODO Auto-generated method stub
		return null;
	}

}
