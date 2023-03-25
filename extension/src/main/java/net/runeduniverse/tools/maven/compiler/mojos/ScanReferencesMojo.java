package net.runeduniverse.tools.maven.compiler.mojos;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.classrealm.ClassRealmManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceScanner;
import net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner;

/**
 * Maps out all references of the source files to later be able to compile
 * source files in order
 * 
 * @author Pl4yingNight
 * @phase scan-references
 * @goal scan-references
 */
public class ScanReferencesMojo extends AbstractMojo {
	// https://maven.apache.org/developers/mojo-api-specification.html
	// https://maven.apache.org/plugin-tools/maven-plugin-tools-java/index.html

	/**
	 * @parameter property="compiler-extension.main.source.dir"
	 *            default-value="${project.build.sourceDirectory}"
	 * @readonly
	 * @required
	 */
	private File sourceDirectory;
	/**
	 * @parameter property="compiler-extension.test.source.dir"
	 *            default-value="${project.build.testSourceDirectory}"
	 * @readonly
	 * @required
	 */
	private File testSourceDirectory;
	/**
	 * @parameter property="compiler-extension.target.dir"
	 *            default-value="${project.build.outputDirectory}"
	 * @readonly
	 * @required
	 */
	private File targetDirectory;
	/**
	 * @parameter property="compiler-extension.test.target.dir"
	 *            default-value="${project.build.testOutputDirectory}"
	 * @readonly
	 * @required
	 */
	private File testTargetDirectory;
	/**
	 * @parameter default-value="${mojoExecution}"
	 * @readonly
	 * @required
	 */
	private MojoExecution mojoExecution;
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
	private ClassRealmManager classRealmManager;
	/**
	 * @component
	 */
	private MavenPluginManager pluginManager;
	/**
	 * @component
	 */
	private BuildPluginManager buildPluginManager;
	/**
	 * @component
	 */
	private ICompilerRuntime runtime;
	/**
	 * @component role="net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner"
	 */
	private Map<String, IRuntimeScanner> runtimeScannerMap;
	/**
	 * @component role="net.runeduniverse.tools.maven.compiler.api.IReferenceScanner"
	 */
	private Map<String, IReferenceScanner> refScanner;

	private IRuntimeScanner runtimeScanner;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// select IRuntimeScanner
		this.runtimeScanner = this.runtimeScannerMap.get(this.mojoExecution.getExecutionId());
		if (this.runtimeScanner == null)
			this.runtimeScanner = this.runtimeScannerMap.get("default");

		// initialize ICompilerRuntime
		if (this.runtimeScanner.isTestExecution()) {
			this.runtime.initialize(this.testSourceDirectory, this.testTargetDirectory);
		} else {
			this.runtime.initialize(this.sourceDirectory, this.targetDirectory);
		}

		this.analyzeScanner();

		getLog().info("");

		getLog().info("mapping references of source-files");
		for (IReferenceScanner scanner : this.refScanner.values()) {
			scanner.logInfo(getLog());
		}
		// TODO collect collectors from compiler plugins and run those
		for (IReferenceScanner scanner : this.refScanner.values()) {
			scanner.logAnalisis(getLog());
		}
		getLog().info("finished mapping references of source-files");
	}

	private void analyzeScanner(Plugin mvnPlugin) {
		try {
			PluginDescriptor descriptor = this.pluginManager.getPluginDescriptor(mvnPlugin,
					this.mvnProject.getRemotePluginRepositories(), this.mvnSession.getRepositorySession());

			getLog().info("");
			getLog().warn(descriptor.getGroupId() + ":" + descriptor.getArtifactId());
			// getLog().warn("Dependencies:");
			// for (ComponentDependency dependency : descriptor.getDependencies()) {
			// getLog().error(dependency.getGroupId()+":"+dependency.getArtifactId());
			// }
			// getLog().warn("Artifacts:");
			// if (descriptor.getIntroducedDependencyArtifacts() != null)
			// for (Artifact artifact : descriptor.getIntroducedDependencyArtifacts()) {
			// getLog().error(artifact.getGroupId() + ":" + artifact.getArtifactId());
			// }
			getLog().warn("PRE_Components:");
			for (ComponentDescriptor<?> cDescriptor : descriptor.getComponents()) {
				getLog().error(cDescriptor.getRole());
			}

			final ClassRealm pluginRealm;
			// pluginRealm = this.buildPluginManager.getPluginRealm(this.mvnSession,
			// descriptor);
			this.pluginManager.setupPluginRealm(descriptor, this.mvnSession, null, null, null);
			pluginRealm = descriptor.getClassRealm();

			getLog().warn("POST_Components:");
			for (ComponentDescriptor<?> cDescriptor : descriptor.getComponents()) {
				getLog().error(cDescriptor.getRole());
			}

			// getLog().warn("Realm:");
			// for (URL url : pluginRealm.getURLs()) {
			// getLog().error(url.getFile());
			// }
			// if we are unable to detect if the interface <IReferenceScanner> is
			// implemented via plexus throw it all into the PackageScanner and scan it this
			// way -> it will find it as long as the file exists
		} catch (PluginResolutionException | PluginManagerException | PluginDescriptorParsingException
				| InvalidPluginDescriptorException e) {
			e.printStackTrace();
		}
	}

	public static <T> List<ComponentDescriptor<T>> getComponentDescriptorList(final PlexusContainer container,
			final ClassRealm realm, Class<T> type, String role) {
		synchronized (container) {
			ClassRealm oldLookupRealm = container.setLookupRealm(realm);
			ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			try {
				return container.getComponentDescriptorList(type, role);
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	public static boolean hasComponent(final PlexusContainer container, final ClassRealm realm, Class<?> type,
			ClassRealm... excludedRealms) {
		List<ComponentDescriptor<?>> excluded = new LinkedList<>();
		for (ClassRealm excludedRealm : excludedRealms) {
			if (realm == excludedRealm)
				return false;
			for (ComponentDescriptor<?> component : getComponentDescriptorList(container, realm, type, null))
				if (!excluded.contains(component))
					excluded.add(component);
		}
		for (ComponentDescriptor<?> component : getComponentDescriptorList(container, realm, type, null))
			if (!excluded.contains(component))
				return true;
		return false;
	}

	private void crawlRealm(ClassRealm pluginRealm) {
		ClassRealm oldLookupRealm = this.container.setLookupRealm(pluginRealm);
		ClassLoader oldClassLoader = Thread.currentThread()
				.getContextClassLoader();

		Thread.currentThread()
				.setContextClassLoader(pluginRealm);

		try {

			getLog().warn(pluginRealm.getId());
			List<ComponentDescriptor<IReferenceScanner>> components = new LinkedList<>(
					this.container.getComponentDescriptorList(IReferenceScanner.class, null));

			for (ComponentDescriptor<IReferenceScanner> component : components) {
				getLog().error(component.getRoleHint());
			}

			if (this.container.hasComponent(IReferenceScanner.class)) {

				// pluginRealm.display();

				Map<String, IReferenceScanner> scanner = this.container.lookupMap(IReferenceScanner.class);

				// for (String id : scanner.keySet()) {
				// getLog().error(id);
				// }
			}

		} catch (ComponentLookupException e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread()
					.setContextClassLoader(oldClassLoader);
			this.container.setLookupRealm(oldLookupRealm);
		}

	}

	private void analyzeScanner() {
		// forceload all build plugins
		// maybe later only load plugins which are active in current lifecycle
		// execution?
		for (Plugin mvnPlugin : this.mvnProject.getBuildPlugins()) {
			analyzeScanner(mvnPlugin);
		}

		ClassRealm currentRealm = (ClassRealm) Thread.currentThread()
				.getContextClassLoader();
		currentRealm.display();
		getLog().info("");

		ClassWorld classWorld = currentRealm.getWorld();
		for (ClassRealm pluginRealm : classWorld.getRealms()) {
			
			getLog().warn(pluginRealm.getId());
			if(hasComponent(this.container, pluginRealm, IReferenceScanner.class, this.classRealmManager.getMavenApiRealm()))
				getLog().error("yep");
			else
				getLog().error("nope");
			
			//if (!pluginRealm.getId()
			//		.startsWith("plugin")
			//		&& !pluginRealm.getId()
			//				.equals("maven.api"))
			//	continue;
			//crawlRealm(pluginRealm);

		}
	}
}
