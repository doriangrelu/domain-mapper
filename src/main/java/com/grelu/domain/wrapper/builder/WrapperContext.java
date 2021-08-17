package com.grelu.domain.wrapper.builder;

import com.grelu.domain.wrapper.WrapperContainer;
import org.modelmapper.ModelMapper;
import org.springframework.util.Assert;

public class WrapperContext<F, T> {

	private static WrapperContainer container = null;

	private final ModelMapper modelMapper;
	private final F value;
	private Class<T> clazz;

	public WrapperContext(final ModelMapper modelMapper, final F value, final Class<T> clazz) {
		this.modelMapper = modelMapper;
		this.value = value;
		this.clazz = clazz;

	}

	public static void setContainer(WrapperContainer container) {
		WrapperContext.container = container;
	}

	public T useDefaultModelMapper(Class<T> clazz) {
		if (!this.clazz.equals(clazz)) {
			this.clazz = clazz;
		}
		return this.useDefaultModelMapper();
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

	/**
	 * Attention, cette méthode n'est utile que lorsque qu'un WrapperContainer est instancié
	 * Si ce n'est pas le cas, alors aucun conteneur n'est connu dans le context, donc une exception sera levée
	 * Dans le cas contaire, cela permet de résoudre le wrapper container, pour ensuite effectuer des mapping/conversions custom
	 *
	 * @return Wrappers container
	 */
	public WrapperContainer useContainerWrappers() {
		Assert.notNull(container, "Missing required container in context");
		return container;
	}

	public F getValue() {
		return value;
	}
}
