/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection;

import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.extension.api.connection.ConnectionProvider;
import org.mule.extension.api.exception.IllegalModelDefinitionException;
import org.mule.extension.api.introspection.ConnectionProviderFactory;
import org.mule.extension.api.introspection.declaration.fluent.DeclarationDescriptor;

final class DefaultConnectionProviderFactory<Config, Connection> implements ConnectionProviderFactory<Config, Connection>
{

    private final Class<? extends ConnectionProvider> providerClass;

    DefaultConnectionProviderFactory(DeclarationDescriptor declaration, Class<?> providerClass)
    {
        if (!ConnectionProvider.class.isAssignableFrom(providerClass))
        {
            throw new IllegalModelDefinitionException(String.format(
                    "Class '%s' was specified as a connection provider for extension '%s' but it doesn't implement the '%s' interface",
                    providerClass.getName(), declaration.getDeclaration().getName(), ConnectionProvider.class.getName()));
        }

        this.providerClass = (Class<? extends ConnectionProvider>) providerClass;
    }

    @Override
    public ConnectionProvider<Config, Connection> newInstance()
    {
        try
        {
            return (ConnectionProvider) providerClass.newInstance();
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(createStaticMessage("Could not create connection provider of type " + providerClass.getName()), e);
        }
    }

    @Override
    public Class<? extends ConnectionProvider> getObjectType()
    {
        return providerClass;
    }
}
