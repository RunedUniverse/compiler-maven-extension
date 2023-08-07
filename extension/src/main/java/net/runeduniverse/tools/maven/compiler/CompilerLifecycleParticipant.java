package net.runeduniverse.tools.maven.compiler;

import java.util.List;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = Properties.LIFECYCLE_PARTICIPANT_HINT)
public class CompilerLifecycleParticipant extends AbstractMavenLifecycleParticipant {

	public void afterSessionStart(MavenSession mvnSession) throws MavenExecutionException {
		// insert before org.apache.maven.plugins to override maven-compiler-plugin
		List<String> pluginGroups = mvnSession.getSettings()
				.getPluginGroups();
		if (!pluginGroups.contains(Properties.GROUP_ID))
			pluginGroups.add(0, Properties.GROUP_ID);
	}

}
