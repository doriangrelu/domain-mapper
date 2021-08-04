package com.grelu.domain.wrapper.helper;

import com.grelu.domain.wrapper.builder.WrapperContext;

@FunctionalInterface
public interface Converter<F, T> {

	T convert(WrapperContext<F, T> context);

}
