/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.config;

import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.CONNECTION_PROVIDER_RESOLVER_PROPERTY;
import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.parseConnectionProviderName;
import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.toElementDescriptorBeanDefinition;
import org.mule.config.spring.util.SpringXMLUtils;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.util.StringUtils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

final class ConnectionProviderBeanDefinitionParser extends BaseExtensionBeanDefinitionParser
{

    private final ConnectionProviderModel providerModel;

    public ConnectionProviderBeanDefinitionParser(ConnectionProviderModel providerModel)
    {
        super(ConnectionProviderFactoryBean.class);
        this.providerModel = providerModel;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext)
    {
        BeanDefinition definition = super.parse(element, parserContext);

        BeanDefinition parentDefinition = getParentBeanDefinition(element, parserContext);
        parentDefinition.getPropertyValues().addPropertyValue(CONNECTION_PROVIDER_RESOLVER_PROPERTY, definition);

        return null;
    }

    @Override
    protected void doParse(BeanDefinitionBuilder builder, Element element)
    {
        parseConnectionProviderName(element, builder);

        builder.addConstructorArgValue(providerModel);
        builder.addConstructorArgValue(toElementDescriptorBeanDefinition(element));
    }

    private BeanDefinition getParentBeanDefinition(Element element, ParserContext parserContext)
    {
        String parentBean = getParentBeanName(element);
        if (StringUtils.isBlank(parentBean))
        {
            throw new IllegalStateException("No parent for " + SpringXMLUtils.elementToString(element));
        }
        return parserContext.getRegistry().getBeanDefinition(parentBean);
    }

    private String getParentBeanName(Element element)
    {
        return ((Element) element.getParentNode()).getAttribute("name");
    }

}
