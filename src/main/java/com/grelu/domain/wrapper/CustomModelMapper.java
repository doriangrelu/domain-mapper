package com.grelu.domain.wrapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

/**
 * Définition d'un mapper avec une surcharge de configuration
 *
 * @author Dorian GRELU
 */
public class CustomModelMapper extends ModelMapper {
	public CustomModelMapper() {
		this.getConfiguration()
				.setFieldMatchingEnabled(true)
				.setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
				.setAmbiguityIgnored(true);
	}
}
