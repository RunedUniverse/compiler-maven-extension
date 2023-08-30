package net.runeduniverse.tools.maven.compiler.mojos;

import static net.runeduniverse.lib.utils.maven.PlexusContextUtils.getPlexusComponentDescriptorMap;
import static net.runeduniverse.lib.utils.maven.PlexusContextUtils.loadPlexusComponent;
import static net.runeduniverse.lib.utils.maven.SessionContextUtils.loadSessionComponent;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import net.runeduniverse.tools.maven.compiler.api.CompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.ResourceScanner;

/**
 * Maps all source files to nodes to later be used in the compiler pipeline
 *
 * @author VenaNocta
 * @phase index-sources
 * @goal scan-sources
 */
public class ScanSourcesMojo extends AbstractMojo {
	// https://maven.apache.org/developers/mojo-api-specification.html
	// https://maven.apache.org/plugin-tools/maven-plugin-tools-java/index.html

	/**
	 * @parameter default-value="${session}"
	 * @readonly
	 */
	private MavenSession mvnSession;
	/**
	 * @parameter default-value="${project}"
	 * @readonly
	 */
	private MavenProject mvnProject;

	/**
	 * @component
	 */
	private PlexusContainer container;
	/**
	 * @component
	 */
	private MavenPluginManager pluginManager;

	// RUNTIME PLEXUS COMPONENTS

	private Map<String, ComponentDescriptor<ResourceScanner>> resourceScannerDescriptors = new LinkedHashMap<>(1);

	// RUNTIME VALUES

	private CompilerRuntime runtime;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.runtime = loadSessionComponent(this.mvnSession, CompilerRuntime.class);

		// seed with build plugins
		this.scanBuildPlugins();

		// scan
		for (ComponentDescriptor<ResourceScanner> descriptor : this.resourceScannerDescriptors.values()) {
			try {
				loadPlexusComponent(this.container, descriptor, (c, scanner) -> {
					scanner.scan(ScanSourcesMojo.this.runtime);
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}
	}

	private void scanBuildPlugins() {
		for (Plugin mvnPlugin : this.mvnProject.getBuildPlugins())
			try {
				PluginDescriptor descriptor = this.pluginManager.getPluginDescriptor(mvnPlugin,
						this.mvnProject.getRemotePluginRepositories(), this.mvnSession.getRepositorySession());
				// forceload all build plugins
				this.pluginManager.setupPluginRealm(descriptor, this.mvnSession, null, null, null);
				ClassRealm pluginRealm = descriptor.getClassRealm();

				this.resourceScannerDescriptors.putAll(getPlexusComponentDescriptorMap(this.container, pluginRealm,
						null, ResourceScanner.class.getCanonicalName()));

			} catch (PluginResolutionException | PluginManagerException | PluginDescriptorParsingException
					| InvalidPluginDescriptorException e) {
				getLog().error(e);
			}
	}

}
