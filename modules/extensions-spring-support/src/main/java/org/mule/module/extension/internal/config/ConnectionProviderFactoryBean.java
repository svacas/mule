/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.config;

import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.getResolverSet;
import org.mule.extension.api.connection.ConnectionProvider;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;

import org.springframework.beans.factory.FactoryBean;

public class ConnectionProviderFactoryBean implements FactoryBean<ConnectionProvider>
{

    private final ConnectionProviderModel providerModel;
    private final ElementDescriptor element;

    @Override
    public ConnectionProvider getObject() throws Exception
    {
        ResolverSet resolverSet = getResolverSet(element, providerModel.getParameterModels());
    }

    @Override
    public Class<?> getObjectType()
    {
        return ConnectionProvider.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
