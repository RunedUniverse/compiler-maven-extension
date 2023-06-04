package net.runeduniverse.tools.maven.compiler.mojos.api;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.eclipse.sisu.inject.Logs;

public interface CurrentContextUtils {

	@SuppressWarnings("unchecked")
	public static <R> Map<String, R> getContext(final MavenSession mvnSession, final Class<R> role) {
		Object obj = mvnSession.getCurrentProject()
				.getContextValue(role.getCanonicalName());
		if (obj == null)
			return null;
		try {
			return (Map<String, R>) obj;
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static <R> Map<String, R> putContext(final MavenSession mvnSession, final Class<R> role,
			Map<String, R> context) {
		Map<String, R> oldContext = getContext(mvnSession, role);
		mvnSession.getCurrentProject()
				.setContextValue(role.getCanonicalName(), context);
		return oldContext;
	}

	public static <R> void releaseContext(final MavenSession mvnSession, final Class<R> role) {
		Map<String, R> context = getContext(mvnSession, role);
		if (context != null)
			for (R component : context.values())
				CurrentContextUtils.releaseComponent(mvnSession, role, component);
		mvnSession.getCurrentProject()
				.setContextValue(role.getCanonicalName(), null);
	}

	public static <R> R lookupComponent(final MavenSession mvnSession, final Class<R> role) {
		Map<String, R> map = getContext(mvnSession, role);
		if (map == null)
			return null;
		for (Iterator<R> i = map.values()
				.iterator(); i.hasNext();) {
			R val = i.next();
			if (val != null)
				return val;
		}
		return null;
	}

	public static <R> R lookupComponent(final MavenSession mvnSession, final Class<R> role, final String hint) {
		Map<String, R> map = getContext(mvnSession, role);
		if (map == null)
			return null;
		return map.get(hint);
	}

	public static <R, T extends R> void addComponent(final MavenSession mvnSession, final Class<R> role,
			final String hint, T component) {
		Map<String, R> map = getContext(mvnSession, role);
		if (map == null) {
			map = new LinkedHashMap<>();
			putContext(mvnSession, role, map);
		}
		map.put(hint, component);
	}

	public static <R, T extends R> void releaseComponent(final MavenSession mvnSession, final Class<R> role,
			T component) {
		Map<String, R> map = getContext(mvnSession, role);
		if (component != null) {
			if (component instanceof Startable)
				try {
					((Startable) component).stop();
				} catch (StoppingException e) {
					Logs.catchThrowable(e);
				}
			if (component instanceof Disposable)
				((Disposable) component).dispose();
		}
		if (map == null)
			return;
		map.values()
				.remove(component);
	}

}
