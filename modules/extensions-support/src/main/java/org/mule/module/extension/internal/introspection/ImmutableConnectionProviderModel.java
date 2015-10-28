/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection;

import static org.mule.util.CollectionUtils.immutableList;
import static org.mule.util.Preconditions.checkArgument;
import org.mule.extension.api.introspection.ConnectionProviderFactory;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.extension.api.introspection.ParameterModel;

import java.util.List;
import java.util.Map;

final class ImmutableConnectionProviderModel extends AbstractImmutableModel implements ConnectionProviderModel
{

    private final List<ParameterModel> parameterModels;
    private final ConnectionProviderFactory connectionProviderFactory;
    private final Class<?> configurationType;
    private final Class<?> connectionType;

    protected ImmutableConnectionProviderModel(String name,
                                               String description,
                                               Class<?> configurationType,
                                               Class<?> connectionType,
                                               ConnectionProviderFactory connectionProviderFactory,
                                               List<ParameterModel> parameterModels,
                                               Map<String, Object> modelProperties)
    {
        super(name, description, modelProperties);
        this.parameterModels = immutableList(parameterModels);

        checkArgument(connectionProviderFactory != null, "connectionProviderFactory cannot be null");
        checkArgument(configurationType != null, "configurationType cannot be null");
        checkArgument(connectionType != null, "connectionType cannot be null");

        this.connectionProviderFactory = connectionProviderFactory;
        this.configurationType = configurationType;
        this.connectionType = connectionType;
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
    public ConnectionProviderFactory getConnectionProviderFactory()
    {
        return connectionProviderFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getConfigurationType()
    {
        return configurationType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getConnectionType()
    {
        return connectionType;
    }
}
