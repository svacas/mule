/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection.validation;

import org.mule.extension.api.exception.IllegalModelDefinitionException;
import org.mule.extension.api.introspection.ConfigurationModel;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.extension.api.introspection.DataQualifier;
import org.mule.extension.api.introspection.Described;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.extension.api.introspection.OperationModel;
import org.mule.extension.api.introspection.ParameterModel;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class NameClashModelValidator implements ModelValidator
{

    @Override
    public void validate(ExtensionModel model) throws IllegalModelDefinitionException
    {
        Multimap<String, Described> names = HashMultimap.create();

        getNames(names, model.getConfigurationModels());
        getNames(names, model.getOperationModels());
        getNames(names, model.getConnectionProviders());
        getTopLevelParameterNames(names, model);

        StringBuilder messageBuilder = new StringBuilder();

        names.asMap().entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry -> messageBuilder.append(String.format("'%s': Used for [%s]\n", entry.getKey(), clashingDescribedTypes(entry.getValue()))));

        if (messageBuilder.length() > 0)
        {
            messageBuilder.insert(0, String.format("Extension '%s' used the same names for defining different concepts. The following name clashes were detected:\n", model.getName()));
            throw new IllegalModelDefinitionException(messageBuilder.toString());
        }
    }

    private Multimap<String, Described> getNames(Multimap<String, Described> accumulator, Collection<? extends Described> describedList)
    {
        describedList.stream().forEach(d -> accumulator.put(d.getName(), d));
        return accumulator;
    }

    private Multimap<String, Described> getTopLevelParameterNames(Multimap<String, Described> names, ExtensionModel extensionModel)
    {
        getTopLevelParameterNames(names, extensionModel.getConfigurationModels(), ConfigurationModel::getParameterModels);
        getTopLevelParameterNames(names, extensionModel.getOperationModels(), OperationModel::getParameterModels);
        getTopLevelParameterNames(names, extensionModel.getConnectionProviders(), ConnectionProviderModel::getParameterModels);

        return names;
    }

    private <T> Multimap<String, Described> getTopLevelParameterNames(Multimap<String, Described> names, Collection<T> parameterized, Function<T, List<ParameterModel>> mapFunciton)
    {
        parameterized.stream()
                .map(mapFunciton)
                .flatMap(List::stream)
                .filter(p -> p.getType().getQualifier() == DataQualifier.POJO)
                .forEach(p -> names.put(p.getName(), p));

        return names;
    }

    private String clashingDescribedTypes(Collection<Described> describedList)
    {
        Set<String> types = describedList.stream().map(d -> d.getClass().getSimpleName()).collect(Collectors.toSet());
        return Joiner.on(", ").join(types);
    }
}
