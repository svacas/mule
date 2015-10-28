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
import org.mule.module.extension.internal.runtime.ObjectBuilder;
import org.mule.module.extension.internal.runtime.config.ConnectionProviderObjectBuilder;
import org.mule.module.extension.internal.runtime.resolver.ObjectBuilderValueResolver;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;
import org.mule.module.extension.internal.runtime.resolver.ValueResolver;

import org.springframework.beans.factory.FactoryBean;

public class ConnectionProviderFactoryBean implements FactoryBean<ValueResolver>
{

    private final ConnectionProviderModel providerModel;
    private final ElementDescriptor element;

    public ConnectionProviderFactoryBean(ConnectionProviderModel providerModel, ElementDescriptor element)
    {
        this.providerModel = providerModel;
        this.element = element;
    }

    @Override
    public ValueResolver getObject() throws Exception
    {
        ResolverSet resolverSet = getResolverSet(element, providerModel.getParameterModels());
        ObjectBuilder<ConnectionProvider> builder = new ConnectionProviderObjectBuilder(providerModel, resolverSet);
        return new ObjectBuilderValueResolver<>(builder);
    }

    @Override
    public Class<?> getObjectType()
    {
        return ValueResolver.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
