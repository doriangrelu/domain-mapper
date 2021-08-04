package com.grelu.domain.wrapper.resolver;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ModelMapperResolverImpl implements ModelMapperResolver {

	private final Optional<ModelMapper> resolvedMapper;

	public ModelMapperResolverImpl(Optional<ModelMapper> resolvedMapper) {
		this.resolvedMapper = resolvedMapper;
	}

	public synchronized ModelMapper resolve() {
		return this.resolvedMapper.orElseGet(ModelMapper::new);
	}

}
