/*
 * Copyright 2010 the original author or authors
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package org.springframework.integration.twitter.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

import static org.springframework.integration.twitter.config.TwitterNamespaceHandler.BASE_PACKAGE;

/**
 * A parser for InboundTimelineUpdateEndpoint endpoint. 
 * 
 * @author Oleg Zhurakousky
 * @since 2.0
 */
public class UpdateEndpointParser extends AbstractSingleBeanDefinitionParser {
	
    @Override
    protected String getBeanClassName(Element element) {
    	String elementName = element.getLocalName().trim();
    	if ("inbound-update-channel-adapter".equals(elementName)){
    		 return BASE_PACKAGE +".inbound.InboundTimelineUpdateEndpoint" ;
    	}
    	else if ("inbound-dm-channel-adapter".equals(elementName)){
    		 return  BASE_PACKAGE +  ".inbound.InboundDirectMessageEndpoint"; 
    	}
    	else if ("inbound-mention-channel-adapter".equals(elementName)){
    		 return BASE_PACKAGE + ".inbound.InboundMentionEndpoint";
    	}
    	else {
    		throw new IllegalArgumentException("Element '" + elementName + "' is not supported by this parser");
    	}
    }
    
    @Override
    protected boolean shouldGenerateId() {
		return true;
	}

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "channel", "outputChannel");
        IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "twitter-connection", "configuration");
        IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "id", "persistentIdentifier");
    }
}