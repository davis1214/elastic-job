/*
 * Copyright 1999-2015 ami.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.ami.ddframe.job.lite.spring.job.parser.script;

import com.ami.ddframe.job.config.script.ScriptJobConfiguration;
import com.ami.ddframe.job.lite.spring.job.parser.common.AbstractJobBeanDefinitionParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 脚本作业的命名空间解析器.
 * 
 * @author caohao
 */
public final class ScriptJobBeanDefinitionParser extends AbstractJobBeanDefinitionParser {
    
    @Override
    protected BeanDefinition getJobTypeConfigurationBeanDefinition(final ParserContext parserContext, final BeanDefinition jobCoreConfigurationBeanDefinition, final Element element) {
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ScriptJobConfiguration.class);
        result.addConstructorArgValue(jobCoreConfigurationBeanDefinition);
        result.addConstructorArgValue(element.getAttribute(ScriptJobBeanDefinitionParserTag.SCRIPT_COMMAND_LINE_ATTRIBUTE));
        return result.getBeanDefinition();
    }
}
