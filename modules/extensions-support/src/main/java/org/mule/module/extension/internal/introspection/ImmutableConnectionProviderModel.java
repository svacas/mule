/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.extension.api.introspection.ConnectionProviderFactory;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.extension.api.introspection.ParameterModel;

import java.util.List;

import org.apache.commons.lang.StringUtils;

final class ImmutableConnectionProviderModel implements ConnectionProviderModel
{
    private final String name;
    private final List<ParameterModel> parameterModels;
    private final ConnectionProviderFactory factory;

    public ImmutableConnectionProviderModel(String name, List<ParameterModel> parameterModels, ConnectionProviderFactory factory)
    {
        checkArgument(!StringUtils.isBlank(name), "name attribute cannot be null or blank");
        this.name = name;
        this.parameterModels = parameterModels;
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParameterModel> getParameterModels()
    {
        return parameterModels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionProviderFactory getFactory()
    {
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return name;
    }
}
