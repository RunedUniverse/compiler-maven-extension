package net.runeduniverse.tools.maven.compiler.api.mojos;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public interface ContextUtils {

	public static <T> List<ComponentDescriptor<T>> getComponentDescriptorList(final PlexusContainer container,
			final ClassRealm realm, Class<T> type, String role) {
		synchronized (container) {
			ClassRealm oldLookupRealm = container.setLookupRealm(realm);
			ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread()
					.setContextClassLoader(realm);
			try {
				return container.getComponentDescriptorList(type, role);
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	public static <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap(final PlexusContainer container,
			final ClassRealm realm, Class<T> type, String role) {
		synchronized (container) {
			ClassRealm oldLookupRealm = container.setLookupRealm(realm);
			ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread()
					.setContextClassLoader(realm);
			try {
				return container.getComponentDescriptorMap(type, role);
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	public static boolean hasComponent(final PlexusContainer container, final ClassRealm realm, Class<?> type,
			ClassRealm... excludedRealms) {
		Set<ComponentDescriptor<?>> excluded = new LinkedHashSet<>();
		for (ClassRealm excludedRealm : excludedRealms) {
			if (realm == excludedRealm)
				return false;
			excluded.addAll(getComponentDescriptorList(container, excludedRealm, type, null));
		}
		return !excluded.containsAll(getComponentDescriptorList(container, realm, type, null));
	}

	public static <T> void loadComponent(final PlexusContainer container, ComponentDescriptor<T> descriptor,
			ComponentHandler<T> handler) throws ComponentLookupException {
		synchronized (container) {
			ClassRealm oldLookupRealm = container.setLookupRealm(descriptor.getRealm());
			ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread()
					.setContextClassLoader(descriptor.getRealm());
			try {
				handler.accept(container, container.lookup(descriptor.getImplementationClass(), descriptor.getRole(),
						descriptor.getRoleHint()));
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	@FunctionalInterface
	public interface ComponentHandler<T> {

		public void accept(final PlexusContainer container, final T instance);

	}

}
