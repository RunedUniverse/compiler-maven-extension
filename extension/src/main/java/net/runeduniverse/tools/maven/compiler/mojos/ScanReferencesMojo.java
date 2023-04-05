package net.runeduniverse.tools.maven.compiler.mojos;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.classrealm.ClassRealmManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import net.runeduniverse.lib.utils.logging.logs.CompoundTree;
import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceScanner;
import net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner;

import static net.runeduniverse.tools.maven.compiler.api.mojos.ContextUtils.hasComponent;
import static net.runeduniverse.tools.maven.compiler.api.mojos.ContextUtils.getComponentDescriptorMap;
import static net.runeduniverse.tools.maven.compiler.api.mojos.ContextUtils.loadComponent;

/**
 * Maps out all references of the source files to later be able to compile
 * source files in order
 * 
 * @author VenaNocta
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
	private ICompilerRuntime runtime;
	/**
	 * @component role="net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner"
	 */
	private Map<String, IRuntimeScanner> runtimeScannerMap;

	private Map<String, ComponentDescriptor<IReferenceScanner>> refScanner = new LinkedHashMap<>();

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

		getLog().info("mapping references of source-files");
		for (ComponentDescriptor<IReferenceScanner> descriptor : this.refScanner.values()) {
			try {
				loadComponent(this.container, descriptor, (c, scanner) -> {
					scanner.logInfo(getLog());
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}
		getLog().info("");
		// TODO collect collectors from compiler plugins and run those
		for (ComponentDescriptor<IReferenceScanner> descriptor : this.refScanner.values()) {
			try {
				loadComponent(this.container, descriptor, (c, scanner) -> {
					scanner.logAnalisis(getLog());
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}
		getLog().info("finished mapping references of source-files");

		// debug:
		this.buildRealm();
	}

	private void analyzeScanner() {
		ClassRealm apiRealm = this.classRealmManager.getMavenApiRealm();

		for (Plugin mvnPlugin : this.mvnProject.getBuildPlugins())
			try {
				PluginDescriptor descriptor = this.pluginManager.getPluginDescriptor(mvnPlugin,
						this.mvnProject.getRemotePluginRepositories(), this.mvnSession.getRepositorySession());
				// forceload all build plugins
				this.pluginManager.setupPluginRealm(descriptor, this.mvnSession, null, null, null);
				ClassRealm pluginRealm = descriptor.getClassRealm();

				if (!hasComponent(container, pluginRealm, IReferenceScanner.class, apiRealm))
					continue;

				this.refScanner.putAll(getComponentDescriptorMap(this.container, pluginRealm, null,
						IReferenceScanner.class.getCanonicalName()));

			} catch (PluginResolutionException | PluginManagerException | PluginDescriptorParsingException
					| InvalidPluginDescriptorException e) {
				getLog().error(e);
			}
	}

	private void buildRealm() {
		getLog().warn("rebuilding REALM");

		ClassRealm curRealm = (ClassRealm) Thread.currentThread()
				.getContextClassLoader();
		ClassWorld world = curRealm.getWorld();

		Map<ClassRealm, CompoundTree> treeMap = new LinkedHashMap<>();
		Set<ClassRealm> rootlessRealms = new LinkedHashSet<>();

		for (ClassRealm realm : world.getRealms()) {
			CompoundTree t = new CompoundTree(realm.getId());
			treeMap.put(realm, t);
			rootlessRealms.add(realm);
		}
		for (ClassRealm r : treeMap.keySet()) {
			CompoundTree t = treeMap.get(r.getParentClassLoader());
			if (t == null)
				continue;
			t.append(treeMap.get(r));
			rootlessRealms.remove(r);
		}
		CompoundTree loadedRealmsTree = new CompoundTree("Loaded Realms");
		for (ClassRealm r : rootlessRealms)
			loadedRealmsTree.append(treeMap.get(r));
		getLog().info(loadedRealmsTree.toString());

		
		
		
		
		
		
		
		
		
		
		
	}

}
