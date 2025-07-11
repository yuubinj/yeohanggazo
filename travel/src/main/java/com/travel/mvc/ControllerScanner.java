package com.travel.mvc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.travel.mvc.annotation.Controller;

public class ControllerScanner {
	public Map<Class<?>, Object> getControllers(final String[] basePackages) {
		final Set<Class<?>> classes = getAllControllers(basePackages);

		return instantiateControllers(classes);
	}

	private Map<Class<?>, Object> instantiateControllers(final Set<Class<?>> classes) {
		return classes.stream()
				.collect(Collectors.toMap(clazz -> clazz, this::instantiateClass));
	}

	private Object instantiateClass(final Class<?> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException("Controller를 인스턴스화 할 수 없습니다.");
		}
	}

	// Controller Scan 시작 --
	private Set<Class<?>> getAllControllers(final String[] basePackages) {
		Set<Class<?>> set = new LinkedHashSet<Class<?>>();

		for (String basePackage : basePackages) {
			set.addAll(scan(basePackage));
		}

		return set;
	}

	private Set<Class<?>> scan(String basePackage) {
		// 현재 실행 중인 쓰레드의 클래스 로더 반환
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = basePackage.replace('.', '/');

		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

		try {
			List<File> files = new ArrayList<File>();
			Enumeration<URL> resources = classLoader.getResources(path);
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				files.add(new File(resource.getFile()));
			}
			for (File file : files) {
				if (file.isDirectory()) {
					classes.addAll(findClasses(file, basePackage));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	private Set<Class<?>> findClasses(File directory, String basePackage) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, basePackage + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				try {
					// forName(String className, boolean initialize, ClassLoader classLoader)
					// initialize : 클래스 초기화 여부(기본:true-static 초기화블럭이실행됨)
					// classLoader : 현재의 클래스를 정의하는 클래스 로더
					Class<?> cls = Class.forName(className, false, classLoader);
					if (cls.isAnnotationPresent(Controller.class)) {
						classes.add(Class.forName(className, false, classLoader));
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		return classes;
	}
	// Controller Scan 종료 --
}
