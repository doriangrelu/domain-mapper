package com.grelu.domain.wrapper.builder;


import com.grelu.domain.wrapper.EntityDomainWrapper;
import com.grelu.domain.wrapper.PureObject;
import com.grelu.domain.wrapper.helper.Converter;
import com.grelu.domain.wrapper.helper.Mapper;
import com.grelu.domain.wrapper.helper.Resolvable;
import org.modelmapper.ModelMapper;
import org.springframework.util.Assert;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


class EntityDomainWrapperImpl<E, D> implements EntityDomainWrapper<E, D> {

	private ModelMapper modelMapper;
	/**
	 * Business datas
	 */
	private final Converter<D, E> toEntityConverter;
	private final Converter<E, D> toDomainConverter;
	private final Deque<Mapper<E>> entityMapper;
	private final Deque<Mapper<D>> domainMapper;
	private final Resolvable supportEntity;
	private final Resolvable supportDomain;
	private final Class<E> entityClazz;
	private final Class<D> domainClazz;
	private final int priority;


	/**
	 * Concurrent management
	 */
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


	public EntityDomainWrapperImpl(ModelMapper modelMapper,
								   Converter<D, E> toEntityConverter,
								   Converter<E, D> toDomainConverter,
								   Deque<Mapper<E>> entityMapper,
								   Deque<Mapper<D>> domainMapper, Class<E> entityClazz,
								   Class<D> domainClazz, Resolvable supportEntity,
								   Resolvable supportDomain,
								   int priority
	) {
		this.modelMapper = modelMapper;
		this.toEntityConverter = toEntityConverter;
		this.toDomainConverter = toDomainConverter;
		this.entityMapper = entityMapper;
		this.domainMapper = domainMapper;
		this.entityClazz = entityClazz;
		this.domainClazz = domainClazz;
		this.supportEntity = supportEntity;
		this.supportDomain = supportDomain;
		this.priority = priority;
	}

	@Override
	public E toEntity(D domain, boolean triggerMap) {
		return this.to(domain, this.toEntityConverter, this.entityClazz, triggerMap ? this.entityMapper : defaultDeque());
	}

	@Override
	public List<E> toEntities(List<D> domains, boolean triggerMap) {
		return this.tos(domains, this.toEntityConverter, this.entityClazz, triggerMap ? this.entityMapper : defaultDeque());
	}

	public D toDomain(E entity, boolean triggerMap) {
		return this.to(entity, this.toDomainConverter, this.domainClazz, triggerMap ? this.domainMapper : defaultDeque());
	}

	@Override
	public List<D> toDomains(List<E> entities, boolean triggerMap) {
		return this.tos(entities, this.toDomainConverter, this.domainClazz, triggerMap ? this.domainMapper : defaultDeque());
	}

	public E mapEntity(E entity) {
		return this.map(entity, this.entityMapper);
	}

	@Override
	public List<E> mapEntities(List<E> entities) {
		return this.maps(entities, this.entityMapper);
	}

	@Override
	public D mapDomain(D domain) {
		return this.map(domain, this.domainMapper);
	}

	@Override
	public List<D> mapDomains(List<D> domains) {
		return this.maps(domains, this.domainMapper);
	}

	@Override
	public boolean supportDomain(Class<?> clazz, String option) {
		return this.support(clazz, this.domainClazz, option, this.supportDomain);
	}

	@Override
	public boolean supportEntity(Class<?> clazz, String option) {
		return this.support(clazz, this.entityClazz, option, this.supportEntity);
	}

	@SuppressWarnings("unchecked")
	public <F, T> List<T> tos(List<F> o, Converter<F, T> converterDelegate, Class<T> clazz, Deque<Mapper<T>> mapperDelegate) {
		return o.parallelStream().map(f -> this.to(f, converterDelegate, clazz, mapperDelegate)).toList();
	}


	public <F, T> T to(F o, Converter<F, T> converterDelegate, Class<T> clazz, Deque<Mapper<T>> mapperDelegate) {
		try {
			this.readWriteLock.readLock().lock();
			if (converterDelegate == null) {
				return this.createContext(o, clazz).useDefaultModelMapper();
			}
			return this.map(converterDelegate.convert(this.createContext(o, clazz)), mapperDelegate);
		} finally {
			this.readWriteLock.readLock().lock();
		}
	}

	public <T> List<T> maps(List<T> os, Deque<Mapper<T>> mapperDelegates) {
		return os.parallelStream().map(t -> this.map(t, mapperDelegates)).toList();
	}

	private static <T> Deque<Mapper<T>> defaultDeque() {
		return new ArrayDeque<>();
	}

	private <T, P> boolean support(Class<T> targetClazz, Class<P> compareClazz, String option, Resolvable delegate) {
		Assert.notNull(option, "Required option is missing");
		if (delegate != null) {
			return delegate.support(targetClazz, option);
		}
		return compareClazz.equals(targetClazz);
	}

	private <F, T> WrapperContext<F, T> createContext(F value, Class<T> clazz) {
		return new WrapperContext<>(this.modelMapper, value, clazz);
	}

	@SuppressWarnings("unchecked")
	private <T> T map(T o, Deque<Mapper<T>> mapperDelegates) {
		T target = o instanceof PureObject ? (T) ((PureObject) o).clone() : o;
		Deque<Mapper<T>> mapperDelegateCopy = new ArrayDeque<>(mapperDelegates);
		Mapper<T> delegate = mapperDelegateCopy.poll();

		if (delegate == null) {
			return target;
		}

		return this.map(delegate.map(target), mapperDelegateCopy);
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

}
