/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import org.mule.api.expression.ExpressionManager;
import org.mule.extension.api.introspection.ParameterModel;
import org.mule.extension.api.introspection.ParametrizedModel;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;
import org.mule.module.extension.internal.runtime.resolver.StaticValueResolver;
import org.mule.module.extension.internal.runtime.resolver.TypeSafeExpressionValueResolver;
import org.mule.module.extension.internal.runtime.resolver.ValueResolver;

import java.util.List;

abstract class BaseImplicitObjectFactory
{

    private final ExpressionManager expressionManager;

    protected BaseImplicitObjectFactory(ExpressionManager expressionManager)
    {
        this.expressionManager = expressionManager;
    }

    protected ResolverSet buildImplicitResolverSet(ParametrizedModel parametrizedModel)
    {
        ResolverSet resolverSet = new ResolverSet();
        for (ParameterModel parameterModel : parametrizedModel.getParameterModels())
        {
            Object defaultValue = parameterModel.getDefaultValue();
            if (defaultValue != null)
            {
                ValueResolver<Object> valueResolver;
                if (defaultValue instanceof String && expressionManager.isExpression((String) defaultValue))
                {
                    valueResolver = new TypeSafeExpressionValueResolver<>((String) defaultValue, parameterModel.getType());
                }
                else
                {
                    valueResolver = new StaticValueResolver<>(defaultValue);
                }

                resolverSet.add(parameterModel, valueResolver);
            }
        }

        return resolverSet;
    }

    protected <T extends ParametrizedModel> T getFirstImplicit(List<T> models)
    {
        for (T model : models)
        {
            if (canBeUsedImplicitly(model))
            {
                return model;
            }
        }

        return null;
    }

    private boolean canBeUsedImplicitly(ParametrizedModel parametrizedModel)
    {
        for (ParameterModel parameterModel : parametrizedModel.getParameterModels())
        {
            if (parameterModel.isRequired() && parameterModel.getDefaultValue() == null)
            {
                return false;
            }
        }

        return true;
    }
}
