package com.grelu.domain.wrapper.helper;

@FunctionalInterface
public interface Resolvable {

	boolean support(Class<?> clazz, String option);

}
