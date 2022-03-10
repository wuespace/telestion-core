package de.wuespace.telestion.api;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class CamelCase extends DisplayNameGenerator.Standard {
	@Override
	public String generateDisplayNameForClass(Class<?> testClass) {
		var name = super.generateDisplayNameForClass(testClass);
		return replaceCapitals(name);
	}

	@Override
	public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
		var name = super.generateDisplayNameForNestedClass(nestedClass);
		return replaceCapitals(name);
	}

	@Override
	public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
		var name = capitalizeFirstLetter(replaceCapitals(testMethod.getName()));
		if (testMethod.getParameterTypes().length == 0) {
			return name;
		}

		return name + " " + generateParameterList(testMethod);
	}

	private String replaceCapitals(String name) {
		return name.replaceAll("([0-9]+|[A-Z]+)", " $1")
				.replaceAll("(A)([^n])", "$1 $2").trim();
	}

	private String capitalizeFirstLetter(String name) {
		if (name.isEmpty()) {
			return name;
		}
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private String generateParameterList(Method testMethod) {
		return DisplayNameGenerator.parameterTypesAsString(testMethod)
				.replaceAll("\\((.*)\\)", "[$1]");
	}
}
