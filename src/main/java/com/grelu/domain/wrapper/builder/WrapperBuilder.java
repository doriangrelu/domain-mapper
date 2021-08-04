package com.grelu.domain.wrapper.builder;


import com.grelu.domain.wrapper.CustomModelMapper;
import com.grelu.domain.wrapper.EntityDomainWrapper;
import com.grelu.domain.wrapper.helper.Mapper;
import com.grelu.domain.wrapper.helper.Resolvable;
import com.grelu.domain.wrapper.helper.ToDomainConverter;
import com.grelu.domain.wrapper.helper.ToEntityConverter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WrapperBuilder<E, D> {

	private static final Logger logger = LoggerFactory.getLogger(WrapperBuilder.class);

	private final ModelMapper mapper;

	private final Deque<Mapper<E>> entityMapper;

	private final Deque<Mapper<D>> domainMapper;

	private ToDomainConverter<E, D> toDomainConverter = null;

	private ToEntityConverter<E, D> toEntityConverter = null;

	private Class<E> entityClazz = null;

	private Class<D> domainClazz = null;

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private boolean hasBuild = false;

	private Resolvable supportEntity = null;

	private Resolvable supportDomain = null;

	private int priority = -1;


	private WrapperBuilder() {
		this(null, null);
	}

	private WrapperBuilder(Class<E> entityClazz, Class<D> domainClazz) {
		this(new CustomModelMapper(), entityClazz, domainClazz);
	}

	private WrapperBuilder(ModelMapper modelMapper, Class<E> entityClazz, Class<D> domainClazz) {
		logger.trace("Create new builder for entity ({}), domain ({}), and {} mapper", entityClazz, domainClazz, modelMapper.getClass());
		this.entityClazz = entityClazz;
		this.domainClazz = domainClazz;
		this.entityMapper = new ArrayDeque<>();
		this.domainMapper = new ArrayDeque<>();
		this.mapper = modelMapper;
	}


	public static <E, D> WrapperBuilder<E, D> getInstance() {
		return new WrapperBuilder<>();
	}

	public static <E, D> WrapperBuilder<E, D> getInstance(ModelMapper mapper) {
		return new WrapperBuilder<>(mapper, null, null);
	}

	public static <E, D> WrapperBuilder<E, D> getInstance(ModelMapper mapper, Class<E> entityClazz, Class<D> domainClazz) {
		return new WrapperBuilder<>(mapper, entityClazz, domainClazz);
	}

	public static <E, D> WrapperBuilder<E, D> getInstance(Class<E> entityClazz, Class<D> domainClazz) {
		return new WrapperBuilder<>(entityClazz, domainClazz);
	}

	public WrapperBuilder<E, D> setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public WrapperBuilder<E, D> setSupportEntity(Resolvable supportEntity) {
		logger.trace("{} is support entity", supportEntity != null ? "Define" : "Reset");
		this.supportEntity = supportEntity;
		return this;
	}

	public WrapperBuilder<E, D> setSupportDomain(Resolvable supportDomain) {
		logger.trace("{} is support domain", supportDomain != null ? "Define" : "Reset");
		this.supportDomain = supportDomain;
		return this;
	}

	public WrapperBuilder<E, D> addDomainMapper(Mapper<D> mapper) {
		this.checkState();
		logger.trace("Add domain mapper");
		try {
			this.readWriteLock.readLock().lock();
			this.domainMapper.add(mapper);
		} finally {
			this.readWriteLock.readLock().unlock();
		}
		return this;
	}


	public WrapperBuilder<E, D> setEntityClazz(Class<E> clazz) {
		this.entityClazz = clazz;
		return this;
	}


	public WrapperBuilder<E, D> setDomainClazz(Class<D> clazz) {
		this.domainClazz = clazz;
		return this;
	}

	public WrapperBuilder<E, D> addEntityMapper(Mapper<E> mapper) {
		this.checkState();
		logger.trace("Add entity mapper");
		try {
			this.readWriteLock.readLock().lock();
			this.entityMapper.add(mapper);
		} finally {
			this.readWriteLock.readLock().unlock();
		}
		return this;
	}

	public WrapperBuilder<E, D> setToDomainConverter(ToDomainConverter<E, D> toDomainConverter) {
		this.checkState();
		logger.trace("{} domain converter", toDomainConverter != null ? "Define" : "Unset");
		try {
			this.readWriteLock.writeLock().lock();
			this.toDomainConverter = toDomainConverter;
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
		return this;
	}

	public WrapperBuilder<E, D> setToEntityConverter(ToEntityConverter<E, D> toEntityConverter) {
		this.checkState();
		logger.trace("{} entity converter", toEntityConverter != null ? "Define" : "Unset");
		try {
			this.readWriteLock.writeLock().lock();
			this.toEntityConverter = toEntityConverter;
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
		return this;
	}


	public EntityDomainWrapper<E, D> build() {
		this.checkState(true);
		logger.trace("Trigger build");
		try {
			this.readWriteLock.writeLock().lock();
			this.hasBuild = true;
			return new EntityDomainWrapperImpl<>(this.mapper,
					this.toEntityConverter,
					this.toDomainConverter,
					this.entityMapper,
					this.domainMapper,
					this.entityClazz,
					this.domainClazz,
					this.supportEntity,
					this.supportDomain,
					this.priority);
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	private void checkState() {
		this.checkState(false);
	}


	private void checkState(boolean checkIntegrity) {
		try {
			logger.trace("Trigger build check");
			this.readWriteLock.readLock().lock();
			if (this.hasBuild) {
				throw new IllegalStateException("The wrapper has already builded");
			}
			logger.trace("Check integrity");
			if (checkIntegrity && (this.entityClazz == null || this.domainClazz == null)) {
				throw new IllegalStateException("Cannot build Wrapper without class informations. Please use setEntityClazz() and setDomainClazz() methods.");
			}

		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}

}
