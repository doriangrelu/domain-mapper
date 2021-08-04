# Mapper / Wrapper d'entité et domaine métiers

## Introduction

Ce module permets de metrre à disposition des outils pour faciliter la conversion de vos entités / domaines. Il met à
disposition composants permettant de construire à la demande des Wrapper, mais aussi de conteuriser ces derniers, pour
répondre à la demande de manière plus abstraite.

Cela permet de prédéfinir au démarage potentiellement des configurations à appliquer. Cette méthode peut cependant être
plus gourmande en terme de mémoire.

## Création d'un nouveau Wrapper

Pour créer un nouveau Wrapper il faut utiliser un **WrapperBuilder**. Le Builder permets de définir des éléments de
configuration, mais aussi d'assurer la délivrance finale et unique d'un Wrapper conforme et en état de fonctionner
immédiatement.

Comment utiliser le Builder ?

    // Définition du type au getInstance, mais il est possible d'utiliser deux setter (setDomainClazz, setEntityClazz)
    WrapperBuilder<EntityType, DomainType> wrapperBuilder = WrapperBuilder.getInstance(EntityType.class, DomainType.class);
    
    // Construction du Wrapper
    EntityDomainWrapper<EntityType, DomainType> wrapper = wrapperBuilder.build(); 

Votre Wrapper est donc créé et conforme. Redemander un second build engendrera une Eception (IllegalState) car il n'est
pas possible de modifier un Wrapper. Ce dernier doit rester "pure".

## Appliquer un "Map" sur un objet

Pour appliquer un ou plusieurs Map sur un objet, c'est très simple. Il vous faudra dans un premier temps ajouter des
mappers à votre Builder

    // Définition du type au getInstance, mais il est possible d'utiliser deux setter (setDomainClazz, setEntityClazz)
    WrapperBuilder<EntityType, DomainType> wrapperBuilder = WrapperBuilder.getInstance(EntityType.class, DomainType.class);

    // Ajout des Mappers en mode Fluent    
    wrapperBuilder.addEntityMapper(o -> {
			o.lastname = "éric";
			return o;
		}).addEntityMapper(o -> {
			o.lastname = "jean-michel";
			return o;
		});

    // Construction du Wrapper
    EntityDomainWrapper<EntityType, DomainType> wrapper = wrapperBuilder.build(); 

    // Application d'un Map sur une entité (la variable myEntityToMap est déjà existe et de type EntityType)
    EntityType myEntity = wrapper.mapEntity(myEntityToMap)

La demande de Map dépilera les Mappers dans l'ordre d'ajout.
**Si votre entité est un PureObject, le Map sera appliqué sur une copie de l'instance initiale**
L'implémentation est **ThreadSafe** et donc plusieurs application peuvent être faites en parallèle.

Pour Map un domaine la procédure est similaire, seulement la fonction est nommée **mapDomain**.