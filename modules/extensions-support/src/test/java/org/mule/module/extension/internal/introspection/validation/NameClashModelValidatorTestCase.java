/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection.validation;

import static java.util.Arrays.asList;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;
import static org.mule.module.extension.internal.util.ExtensionsTestUtils.getParameter;
import org.mule.extension.api.introspection.ConfigurationModel;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.extension.api.introspection.OperationModel;
import org.mule.extension.api.introspection.ParameterModel;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.tck.testmodels.fruit.Apple;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class NameClashModelValidatorTestCase extends AbstractMuleTestCase
{

    @Mock
    private ExtensionModel extensionModel;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationModel configurationModel;

    @Mock
    private OperationModel operationModel;

    private ParameterModel simpleConfigParam;
    private ParameterModel topLevelConfigParam;
    private ParameterModel simpleOperationParam;
    private ParameterModel topLevelOperationParam;

    private NameClashModelValidator validator = new NameClashModelValidator();

    @Before
    public void before()
    {
        when(extensionModel.getName()).thenReturn("extensionName");
        when(extensionModel.getConfigurations()).thenReturn(asList(configurationModel));
        when(extensionModel.getOperations()).thenReturn(asList(operationModel));

        simpleConfigParam = getParameter("simpleConfigParam", String.class);
        topLevelConfigParam = getParameter("topLevelConfigParam", Apple.class);
        simpleOperationParam = getParameter("simpleOperationParam", String.class);
        topLevelOperationParam = getParameter("topLevelOperationParam", Apple.class);

        when(configurationModel.getName()).thenReturn("config");
        when(configurationModel.getParameterModels()).thenReturn(asList(simpleConfigParam, topLevelConfigParam));

        when(operationModel.getName()).thenReturn("operation");
        when(operationModel.getParameterModels()).thenReturn(asList(simpleOperationParam, topLevelOperationParam));
    }

    @Test
    public void validModel()
    {
        validator.validate(extensionModel);
    }
}
