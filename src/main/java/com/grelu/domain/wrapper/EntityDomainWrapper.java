package com.grelu.domain.wrapper;

import java.util.List;

/**
 * @param <E> Type d'entité
 * @param <D> Type de domaine
 * @author Dorian GRELU
 * <p>
 * Permets de mettre à disposition un "wrapper"
 * Ce wrapper permettra convertir des entités, des DTO.
 * Il permettra aussi d'effectuer un map sur les différents objets soit à la demande, soit à la conversion
 */
public interface EntityDomainWrapper<E, D> {

	public static final String DEFAULT_OPTION = "_";

	/**
	 * Convertis un objet métier en entité
	 * Attention déclenche un Map après la conversion
	 *
	 * @param domain domaine métier
	 * @return entité
	 */
	default E toEntity(D domain) {
		return this.toEntity(domain, true);
	}

	/**
	 * Convertis un objet métier en entité, en forçant le type de retours, sans utiliser celui configuré dans le bean
	 *
	 * @param domain domaine métier
	 * @param clazz  type d'entité
	 * @return entité
	 */
	default E toEntity(D domain, Class<E> clazz) {
		return this.toEntity(domain, clazz, true);
	}

	/**
	 * Convertis un objet métier en entité, en forçant le type de retours, sans utiliser celui configuré dans le bean
	 *
	 * @param domain     domaine métier
	 * @param clazz      type d'entité
	 * @param triggerMap déclencher un map ?
	 * @return entité
	 */
	E toEntity(D domain, Class<E> clazz, boolean triggerMap);

	/**
	 * Convertis un objet métier en entité
	 *
	 * @param domain     domaine métier
	 * @param triggerMap déclenchement du Map ?
	 * @return entité
	 */
	E toEntity(D domain, boolean triggerMap);

	default List<E> toEntities(List<D> domains, Class<E> clazz) {
		return this.toEntities(domains, clazz, true);
	}

	List<E> toEntities(List<D> domains, Class<E> clazz, boolean triggerMap);

	/**
	 * Convertis une liste d'objets métier en entité
	 * Déclenche un Map automatiquement
	 *
	 * @param domains domaines métiers
	 * @return Liste d'entité
	 */
	default List<E> toEntities(List<D> domains) {
		return this.toEntities(domains, true);
	}

	/**
	 * Convertis une liste d'objets métier en entité
	 *
	 * @param domains    domaines métiers
	 * @param triggerMap Déclenchement du Map ?
	 * @return Liste d'entité
	 */
	List<E> toEntities(List<D> domains, boolean triggerMap);

	/**
	 * Convertis une entité en domaine métier
	 * Déclenche un Map automatiquement
	 *
	 * @param entity entité
	 * @return domaine
	 */
	default D toDomain(E entity) {
		return this.toDomain(entity, true);
	}

	/**
	 * Convertis une entité en domaine métier
	 *
	 * @param entity     entité
	 * @param triggerMap Déclenchement du Map ?
	 * @return
	 */
	D toDomain(E entity, boolean triggerMap);

	default D toDomain(E entity, Class<D> clazz) {
		return this.toDomain(entity, clazz, true);
	}

	D toDomain(E entity, Class<D> clazz, boolean triggerMap);

	/**
	 * Convertis une liste d'entité en domaine métier
	 * Déclenche un Map automatiquement
	 *
	 * @param entities liste des entités
	 * @return liste des domaines
	 */
	default List<D> toDomains(List<E> entities) {
		return this.toDomains(entities, true);
	}

	/**
	 * Convertis une liste d'entité en domaine métier
	 *
	 * @param entities   iste des entités
	 * @param triggerMap Déclenchement du Map ?
	 * @return liste des domaines
	 */
	List<D> toDomains(List<E> entities, boolean triggerMap);

	default List<D> toDomains(List<E> entities, Class<D> clazz) {
		return this.toDomains(entities, clazz, true);
	}

	List<D> toDomains(List<E> entities, Class<D> clazz, boolean triggerMap);

	/**
	 * Map une entité
	 * Attention, si l'entité est un Pure Object, alors l'instance retournée sera différente de celle en entrée
	 *
	 * @param entity entité à Map
	 * @return Entité après Map
	 */
	E mapEntity(E entity);

	/**
	 * Applique la même chose que la fonction mapEntity, mais sur une liste
	 *
	 * @param entities liste des entités
	 * @return liste des entités après Map
	 */
	List<E> mapEntities(List<E> entities);

	/**
	 * Même fonctionnement que le mapping des entités
	 *
	 * @param domain domaine
	 * @return domaine après map
	 */
	D mapDomain(D domain);

	/**
	 * Même principe que la fonction mapDomain, mais sur une liste
	 *
	 * @param domains liste des domaines
	 * @return liste des domaines après Map
	 */
	List<D> mapDomains(List<D> domains);

	/**
	 * Support un domaine ?
	 *
	 * @param clazz type du domaine
	 * @return Oui / Non ?
	 */
	default boolean supportDomain(Class<?> clazz) {
		return this.supportDomain(clazz, DEFAULT_OPTION);
	}

	/**
	 * Support un domaine dépendant d'une spécification ?
	 *
	 * @param clazz  type de domaine
	 * @param option spécification
	 * @return Oui / Non ?
	 */
	boolean supportDomain(Class<?> clazz, String option);

	/**
	 * Support une entité ?
	 *
	 * @param clazz type d'entité
	 * @return Oui / Non ?
	 */
	default boolean supportEntity(Class<?> clazz) {
		return this.supportEntity(clazz, DEFAULT_OPTION);
	}

	/**
	 * Support une entité dépendant d'une spécification ?
	 *
	 * @param clazz  type d'entité
	 * @param option spécification
	 * @return Oui / Non ?
	 */
	boolean supportEntity(Class<?> clazz, String option);

	/**
	 * Permets de donner un niveau de priorité
	 * Cette option est nécéssaire lors de l'usage en mode "conteneur"
	 * Si deux composants répondent présents à la demande, alors celui avec le plus haut niveau de priorité sera conservé
	 *
	 * @return niveau de priorité
	 */
	default int getPriority() {
		return -1;
	}
}
