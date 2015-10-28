/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime;

import static org.mule.module.extension.internal.util.IntrospectionUtils.getField;
import static org.mule.util.Preconditions.checkArgument;
import static org.springframework.util.ReflectionUtils.setField;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.extension.api.introspection.EnrichableModel;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;
import org.mule.module.extension.internal.runtime.resolver.ResolverSetResult;
import org.mule.module.extension.internal.runtime.resolver.ValueResolver;
import org.mule.module.extension.internal.util.GroupValueSetter;
import org.mule.module.extension.internal.util.MuleExtensionUtils;
import org.mule.module.extension.internal.util.SingleValueSetter;
import org.mule.module.extension.internal.util.ValueSetter;
import org.mule.util.collection.ImmutableListCollector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for implementations of {@link ObjectBuilder}. It implements
 * all of the contract's behavior, except for how to actually
 * create the instance to be returned. Implementors must provide
 * that piece through the abstract {@link #instantiateObject()} method
 *
 * @since 3.7.0
 */
public abstract class BaseObjectBuilder<T> implements ObjectBuilder<T>
{

    protected final ResolverSet resolverSet;
    private final Map<Field, ValueResolver<Object>> resolvers = new HashMap<>();
    private final List<ValueSetter> singleValueSetters;
    private final List<ValueSetter> groupValueSetters;

    public BaseObjectBuilder(Class<?> prototypeClass, EnrichableModel model, ResolverSet resolverSet)
    {
        this.resolverSet = resolverSet;
        singleValueSetters = createSingleValueSetters(prototypeClass, resolverSet);
        groupValueSetters = GroupValueSetter.settersFor(model);
    }

    /**
     * Returns the instance to be returned before the properties have
     * been applied to it
     */
    protected abstract T instantiateObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectBuilder<T> addPropertyResolver(Field field, ValueResolver<? extends Object> resolver)
    {
        checkArgument(field != null, "field cannot be null");
        checkArgument(resolver != null, "resolver cannot be null");

        field.setAccessible(true);
        resolvers.put(field, (ValueResolver<Object>) resolver);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDynamic()
    {
        return MuleExtensionUtils.hasAnyDynamic(resolvers.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T build(MuleEvent event) throws MuleException
    {
        T object = instantiateObject();

        for (Map.Entry<Field, ValueResolver<Object>> resolver : resolvers.entrySet())
        {
            setField(resolver.getKey(), object, resolver.getValue().resolve(event));
        }

        return object;
    }

    public T build(ResolverSetResult result) throws MuleException
    {
        T configuration = instantiateObject();

        setValues(configuration, result, groupValueSetters);
        setValues(configuration, result, singleValueSetters);

        return configuration;
    }

    private List<ValueSetter> createSingleValueSetters(Class<?> prototypeClass, ResolverSet resolverSet)
    {
        return resolverSet.getResolvers().keySet().stream()
                .map(parameterModel -> {
                    Field field = getField(prototypeClass, parameterModel);

                    // if no field, then it means this is a group attribute
                    return field != null ? new SingleValueSetter(parameterModel, field) : null;
                })
                .filter(field -> field != null)
                .collect(new ImmutableListCollector<>());
    }

    private void setValues(Object target, ResolverSetResult result, List<ValueSetter> setters) throws MuleException
    {
        for (ValueSetter setter : setters)
        {
            setter.set(target, result);
        }
    }
}
