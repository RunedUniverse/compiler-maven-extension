package net.runeduniverse.tools.maven.compiler.runtime;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IExecutionMapper;

@Component(role = IExecutionMapper.class, hint = DefaultExecutionMapper.HINT)
public class DefaultExecutionMapper extends AExecutionMapper {

	public static final String HINT = "default";

	@Override
	protected IExecutionMapper getThis() {
		return this;
	}

	@Override
	public boolean isTestExecution() {
		return false;
	}

	@Override
	public ICompilerRuntime createRuntime() {
		return new CompilerRuntime(this.sourceDirectory, this.targetDirectory);
	}

}
