package com.grelu.domain.wrapper.builder;

import org.modelmapper.ModelMapper;

public class WrapperContext<F, T> {

	private final ModelMapper modelMapper;
	private final F value;
	private final Class<T> clazz;

	public WrapperContext(final ModelMapper modelMapper, final F value, final Class<T> clazz) {
		this.modelMapper = modelMapper;
		this.value = value;
		this.clazz = clazz;
	}

	public T useDefaultModelMapper() {
		return this.useCustomModelMapper(this.modelMapper);
	}

	public T useCustomModelMapper(final ModelMapper mapper) {
		if (mapper == null) {
			throw new IllegalStateException("Missing required default model mapper");
		}
		return mapper.map(this.getValue(), clazz);
	}

	public F getValue() {
		return value;
	}
}
