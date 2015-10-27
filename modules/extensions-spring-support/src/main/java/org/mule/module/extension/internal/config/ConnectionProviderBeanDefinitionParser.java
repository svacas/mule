/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.config;

import static org.mule.api.config.MuleProperties.OBJECT_MULE_CONTEXT;
import static org.mule.api.config.MuleProperties.OBJECT_TIME_SUPPLIER;
import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.parseConfigName;
import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.parseConnectionProviderName;
import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.toElementDescriptorBeanDefinition;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

final class ConnectionProviderBeanDefinitionParser extends BaseExtensionBeanDefinitionParser
{

    @Override
    protected void doParse(BeanDefinitionBuilder builder, Element element)
    {
        parseConnectionProviderName(element, builder);

        builder.addConstructorArgValue(configurationModel);
        builder.addConstructorArgValue(toElementDescriptorBeanDefinition(element));
        builder.addConstructorArgReference(OBJECT_MULE_CONTEXT);
        builder.addConstructorArgReference(OBJECT_TIME_SUPPLIER);
    }
}
