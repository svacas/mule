/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection.validation;

import org.mule.extension.api.connection.ConnectionProvider;
import org.mule.extension.api.exception.IllegalModelDefinitionException;
import org.mule.extension.api.introspection.ConfigurationModel;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.module.extension.internal.model.property.ConnectionTypeModelProperty;
import org.mule.module.extension.internal.model.property.ImplementingTypeModelProperty;
import org.mule.util.CollectionUtils;

import com.google.common.base.Joiner;

import java.util.List;
import java.util.stream.Collectors;

public final class ConnectionProviderModelValidator implements ModelValidator
{

    @Override
    public void validate(ExtensionModel extensionModel) throws IllegalModelDefinitionException
    {
        Class<?> connectionType = getOperationsConnectionType(extensionModel);
        validateConnectionProviders(extensionModel, connectionType);
    }

    private void validateConnectionProviders(ExtensionModel extensionModel, Class<?> connectionType)
    {
        extensionModel.getConnectionProviders().stream().forEach(providerModel -> {
            validateConfigType(providerModel, extensionModel);
            if (connectionType != null)
            {
                validateConnectionTypes(providerModel, extensionModel, connectionType);
            }
        });
    }

    private void validateConfigType(ConnectionProviderModel providerModel, ExtensionModel extensionModel)
    {
        Class<?> providerConfigType = providerModel.getConfigurationType();
        for (ConfigurationModel configurationModel : extensionModel.getConfigurations())
        {
            ImplementingTypeModelProperty typeProperty = configurationModel.getModelProperty(ImplementingTypeModelProperty.KEY);
            if (typeProperty != null && !providerConfigType.isAssignableFrom(typeProperty.getType()))
            {
                throw new IllegalModelDefinitionException(String.format(
                        "Configuration '%s' in Extension '%s' is of type '%s' which cannot be used with the connection provider of type '%s'. " +
                        "Please make sure that all configuration models in the extension can be used with any of the defined connection providers",
                        configurationModel.getName(), extensionModel.getName(), typeProperty.getType().getName(), providerConfigType.getName()));
            }
        }
    }

    private void validateConnectionTypes(ConnectionProviderModel providerModel, ExtensionModel extensionModel, Class<?> connectionType)
    {
        if (!providerModel.getConnectionType().isAssignableFrom(connectionType))
        {
            throw new IllegalModelDefinitionException(String.format("Extension '%s' defines a connection provider of name '%s' which yields connections of type '%s'. " +
                                                                    "However, the extension's operations expect connections of type '%s'. Please make sure that all connection " +
                                                                    "providers in the extension can be used with all its operations",
                                                                    extensionModel.getName(), providerModel.getName(), providerModel.getConnectionType().getName(),
                                                                    connectionType.getName()));
        }
    }

    private Class<?> getOperationsConnectionType(ExtensionModel extensionModel)
    {
        List<Class<?>> connectionTypes = extensionModel.getOperations().stream()
                .map(operation -> {
                    ConnectionTypeModelProperty connectionProperty = operation.getModelProperty(ConnectionTypeModelProperty.KEY);
                    return connectionProperty != null ? connectionProperty.getConnectionType() : null;
                })
                .filter(type -> type != null)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(connectionTypes))
        {
            return null;
        }
        else if (connectionTypes.size() > 1)
        {
            throw new IllegalModelDefinitionException(String.format("Extension '%s' has operation which require connections of different types ([%s]). " +
                                                                    "Please standarize on one single connection type to ensure that all operations work with any compatible %s",
                                                                    extensionModel.getName(), Joiner.on(", ").join(connectionTypes), ConnectionProvider.class.getSimpleName()));
        }
        else
        {
            return connectionTypes.get(0);
        }
    }
}
