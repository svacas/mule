/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import org.mule.extension.api.connection.ConnectionProvider;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.module.extension.internal.runtime.BaseObjectBuilder;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;

public class ConnectionProviderObjectBuilder extends BaseObjectBuilder<ConnectionProvider>
{

    private final ConnectionProviderModel providerModel;

    public ConnectionProviderObjectBuilder(ConnectionProviderModel providerModel, ResolverSet resolverSet)
    {
        super(providerModel.getConnectionProviderFactory().getObjectType(), providerModel, resolverSet);
        this.providerModel = providerModel;
    }

    @Override
    protected ConnectionProvider instantiateObject()
    {
        return providerModel.getConnectionProviderFactory().newInstance();
    }
}
