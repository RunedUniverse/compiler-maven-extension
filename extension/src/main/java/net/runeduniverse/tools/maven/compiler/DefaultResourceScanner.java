package net.runeduniverse.tools.maven.compiler;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import net.runeduniverse.tools.maven.compiler.api.CompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.ResourceScanner;
import net.runeduniverse.tools.maven.compiler.mojos.api.SessionContextUtils;

@Component(role = ResourceScanner.class, hint = DefaultResourceScanner.HINT)
public class DefaultResourceScanner implements ResourceScanner {

	public static final String HINT = "default";

	@Requirement
	private MavenSession mvnSession;

	protected Log log;
	protected CompilerRuntime runtime;

	public DefaultResourceScanner() {
	}

	@Override
	public boolean scan() {
		this.log = SessionContextUtils.lookupSessionComponent(this.mvnSession, Log.class);
		this.runtime = SessionContextUtils.lookupSessionComponent(this.mvnSession, CompilerRuntime.class);

		log.info("SourceDirectory: " + this.runtime.getSourceDirectory());
		log.info("TargetDirectory: " + this.runtime.getTargetDirectory());
		return true;
	}

	@Override
	public boolean logAnalisis(Log log) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void identifyNodes() {
		// TODO Auto-generated method stub

	}

}
