package com.grelu.domain.wrapper.impl;

import com.grelu.domain.wrapper.EntityDomainWrapper;
import com.grelu.domain.wrapper.WrapperContainer;
import com.grelu.domain.wrapper.builder.WrapperBuilder;
import com.grelu.domain.wrapper.builder.WrapperContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Component
public class WrapperContainerImpl implements WrapperContainer {

	private final List<EntityDomainWrapper<?, ?>> wrappers;

	private EntityDomainWrapper<?, ?> defaultWrapper = null;

	public WrapperContainerImpl(List<EntityDomainWrapper<?, ?>> wrappersComponents) {
		this.wrappers = Collections.synchronizedList(wrappersComponents);
		WrapperContext.setContainer(this);
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

	@SuppressWarnings("unchecked")
	@Override
	public <E, D> E toEntity(Class<?> clazz, D domain, boolean triggerMap, String option) {
		return this.<E, D>resolveEntityWrapper(clazz, option).toEntity(domain, (Class<E>) clazz, triggerMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E, D> D toDomain(Class<?> clazz, E entity, boolean triggerMap, String option) {
		return this.<E, D>resolveDomainWrapper(clazz, option).toDomain(entity, (Class<D>) clazz, triggerMap);
	}

	@Override
	public <E> E mapEntity(Class<?> clazz, E entity, String option) {
		return this.<E, Object>resolveEntityWrapper(clazz, option).mapEntity(entity);
	}

	@Override
	public <D> D mapDomain(Class<?> clazz, D domain, String option) {
		return this.<Object, D>resolveDomainWrapper(clazz, option).mapDomain(domain);
	}

	@Override
	public <E, D> EntityDomainWrapper<? super E, D> resolveDomainWrapper(Class<?> target, String option) {
		return this.resolveWrapper(
				entityDomainWrapper -> entityDomainWrapper.supportDomain(target, option),
				"Missing required mapper for " + target + " with options {" + option + "}"
		);
	}

	@Override
	public <E, D> EntityDomainWrapper<E, ? super D> resolveEntityWrapper(Class<?> target, String option) {
		return this.resolveWrapper(
				entityDomainWrapper -> entityDomainWrapper.supportEntity(target, option),
				"Missing required mapper for " + target + " with options {" + option + "}"
		);
	}

	@SuppressWarnings("unchecked")
	private <E, D> EntityDomainWrapper<E, D> resolveWrapper(Predicate<EntityDomainWrapper<?, ?>> predicate, String exceptionMessage) {
		return (EntityDomainWrapper<E, D>) this.wrappers.stream()
				.sorted((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()))
				.filter(predicate)
				.findFirst()
				.orElseGet(this::defaultWrapper);
	}

	@SuppressWarnings("unchecked")
	private <E, D> EntityDomainWrapper<E, D> defaultWrapper() {
		if (null == this.defaultWrapper) {
			this.defaultWrapper = WrapperBuilder.getInstance()
					.setDomainClazz(Object.class)
					.setEntityClazz(Object.class)
					.build();
		}
		return (EntityDomainWrapper<E, D>) this.defaultWrapper;
	}

}
