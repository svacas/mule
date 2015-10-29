/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import org.mule.api.MuleEvent;
import org.mule.extension.api.connection.ConnectionProvider;
import org.mule.extension.api.introspection.ExtensionModel;

interface ImplicitConnectionProviderFactory
{

    <Config, Connector> ConnectionProvider<Config, Connector> createImplicitConnectionProvider(String configName, ExtensionModel extensionModel, MuleEvent event);
}
