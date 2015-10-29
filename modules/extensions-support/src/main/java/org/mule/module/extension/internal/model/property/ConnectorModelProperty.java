/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.model.property;

public class ConnectorModelProperty
{
    /**
     * A unique key that identifies this property type
     */
    public static final String KEY = ConnectorModelProperty.class.getName();

    private final Class<?> connectionType;

    public ConnectorModelProperty(Class<?> connectionType)
    {
        this.connectionType = connectionType;
    }

    public Class<?> getConnectionType()
    {
        return connectionType;
    }
}
