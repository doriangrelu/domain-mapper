package com.grelu.domain.wrapper.impl;


import com.grelu.domain.wrapper.EntityDomainWrapper;
import com.grelu.domain.wrapper.WrapperContainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Component
public class WrapperContainerImpl implements WrapperContainer {

	private final List<EntityDomainWrapper<?, ?>> wrappers;

	public WrapperContainerImpl() {
		this.wrappers = Collections.synchronizedList(new ArrayList<>());
	}

	@Override
	public WrapperContainer registerWrapper(EntityDomainWrapper<?, ?> wrapper) {
		this.wrappers.add(wrapper);
		return this;
	}


	@Override
	public WrapperContainer registerWrapper(EntityDomainWrapper<?, ?>... wrapper) {
		this.wrappers.addAll(List.of(wrapper));
		return this;
	}


	@Override
	public <E, D> E toEntity(Class<?> clazz, D domain, boolean triggerMap, String option) {
		return this.<E, D>resolveEntityWrapper(clazz, option).toEntity(domain, triggerMap);
	}

	@Override
	public <E, D> D toDomain(Class<?> clazz, E entity, boolean triggerMap, String option) {
		return this.<E, D>resolveDomainWrapper(clazz, option).toDomain(entity, triggerMap);
	}

	@Override
	public <E> E mapEntity(Class<?> clazz, E entity, String option) {
		return this.<E, Object>resolveEntityWrapper(clazz, option).mapEntity(entity);
	}

	@Override
	public <D> D mapDomain(Class<?> clazz, D domain, String option) {
		return this.<Object, D>resolveDomainWrapper(clazz, option).mapDomain(domain);
	}


	private <E, D> EntityDomainWrapper<? super E, D> resolveDomainWrapper(Class<?> target, String option) {
		return this.resolveWrapper(
				entityDomainWrapper -> entityDomainWrapper.supportDomain(target, option),
				"Missing required mapper for " + target + " with options {" + option + "}"
		);
	}

	private <E, D> EntityDomainWrapper<E, ? super D> resolveEntityWrapper(Class<?> target, String option) {
		return this.resolveWrapper(
				entityDomainWrapper -> entityDomainWrapper.supportEntity(target, option),
				"Missing required mapper for " + target + " with options {" + option + "}"
		);
	}

	@SuppressWarnings("unchecked")
	private <E, D> EntityDomainWrapper<E, D> resolveWrapper(Predicate<EntityDomainWrapper<?, ?>> predicate, String exceptionMessage) {
		return (EntityDomainWrapper<E, D>) this.wrappers.stream()
				.sorted((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()))
				.filter(predicate::test)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(exceptionMessage));
	}

}
