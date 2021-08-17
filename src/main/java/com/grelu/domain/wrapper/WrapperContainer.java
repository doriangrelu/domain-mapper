package com.grelu.domain.wrapper;

import java.util.List;

/**
 * @author Dorian GRELU
 * Conteneur de wrapper
 */
public interface WrapperContainer {

	WrapperContainer registerWrapper(EntityDomainWrapper<?, ?> wrapper);

	WrapperContainer registerWrapper(EntityDomainWrapper<?, ?>... wrapper);

	default <E, D> E toEntity(Class<?> clazz, D domain) {
		return this.toEntity(clazz, domain, true, EntityDomainWrapper.DEFAULT_OPTION);
	}

	<E, D> E toEntity(Class<?> clazz, D domain, boolean triggerMap, String option);

	default <E, D> List<E> toEntities(Class<?> clazz, List<D> domains) {
		return this.toEntities(clazz, domains, true, EntityDomainWrapper.DEFAULT_OPTION);
	}

	@SuppressWarnings("unchecked")
	default <E, D> List<E> toEntities(Class<?> clazz, List<D> domains, boolean triggerMap, String option) {
		return (List<E>) domains.parallelStream()
				.map(domain -> this.toEntity(clazz, domain, triggerMap, option))
				.toList();
	}

	default <E, D> D toDomain(Class<?> clazz, E entity) {
		return this.toDomain(clazz, entity, true, EntityDomainWrapper.DEFAULT_OPTION);
	}

	<E, D> D toDomain(Class<?> clazz, E entity, boolean triggerMap, String option);

	default <E, D> List<D> toDomains(Class<?> clazz, List<E> entities) {
		return this.toDomains(clazz, entities, EntityDomainWrapper.DEFAULT_OPTION);
	}

	default <E, D> List<D> toDomains(Class<?> clazz, List<E> entities, String option) {
		return this.toDomains(clazz, entities, true, EntityDomainWrapper.DEFAULT_OPTION);
	}

	@SuppressWarnings("unchecked")
	default <E, D> List<D> toDomains(Class<?> clazz, List<E> entities, boolean triggerMap, String option) {
		return (List<D>) entities.parallelStream().map(entity -> this.toDomain(clazz, entity, triggerMap, option)).toList();
	}

	<E> E mapEntity(Class<?> clazz, E entity, String option);

	default <E> E mapEntity(Class<?> clazz, E entity) {
		return this.mapEntity(clazz, entity, EntityDomainWrapper.DEFAULT_OPTION);
	}

	default <E> List<E> mapEntities(Class<?> clazz, List<E> entities, String option) {
		return entities.parallelStream()
				.map(entity -> this.mapEntity(clazz, entity, option))
				.toList();
	}

	<D> D mapDomain(Class<?> clazz, D domain, String option);

	default <D> List<D> mapDomains(Class<?> clazz, List<D> domains, String option) {
		return domains.parallelStream()
				.map(domain -> this.mapDomain(clazz, domain, option))
				.toList();
	}

	public <E, D> EntityDomainWrapper<? super E, D> resolveDomainWrapper(Class<?> target, String option);

	public <E, D> EntityDomainWrapper<E, ? super D> resolveEntityWrapper(Class<?> target, String option);

}
