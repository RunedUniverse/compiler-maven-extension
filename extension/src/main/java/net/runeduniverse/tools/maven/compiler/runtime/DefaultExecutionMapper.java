package net.runeduniverse.tools.maven.compiler.runtime;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.CompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.ExecutionMapper;

@Component(role = ExecutionMapper.class, hint = DefaultExecutionMapper.HINT)
public class DefaultExecutionMapper extends AExecutionMapper {

	public static final String HINT = "default";

	@Override
	protected ExecutionMapper getThis() {
		return this;
	}

	@Override
	public boolean isTestExecution() {
		return false;
	}

	@Override
	public CompilerRuntime createRuntime() {
		return new DefaultCompilerRuntime(this.sourceDirectory, this.targetDirectory);
	}

}
