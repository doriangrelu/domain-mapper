package com.grelu.domain.wrapper.helper;

@FunctionalInterface
public interface Mapper<E> {
	/**
	 * Permets d'appliquer un mapping sur les objets retournés
	 *
	 * @param o
	 * @return
	 */
	E map(E o);
}
