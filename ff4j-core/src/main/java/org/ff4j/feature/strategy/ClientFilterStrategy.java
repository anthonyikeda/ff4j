package org.ff4j.feature.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ff4j.FF4jContext;
import org.ff4j.feature.Feature;
import org.ff4j.feature.ToggleStrategy;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 Ff4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * This strategy will check hostName and flipped only if it's contained in expected list.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class ClientFilterStrategy extends AbstractStrategy implements ToggleStrategy {

    /** Threshold. */
    public static final String PARAM_CLIENTLIST = "grantedClients";

    /** Parameter to be checked in context. */
    public static final String CLIENT_HOSTNAME = "clientHostName";
    
    /** List of client to be accepted. */
    private static final String SPLITTER = ",";

    /** Validate the target client against the available hostname. */
    private final Set<String> setOfGrantedClient = new HashSet<String>();

    /** raw client list. */
    private String rawClientList = null;

    /**
     * Default Constructor.
     */
    public ClientFilterStrategy() {}

    /**
     * Parameterized constructor.
     * 
     * @param threshold
     *            threshold
     */
    public ClientFilterStrategy(String clientList) {
        this.rawClientList = clientList;
        getInitParams().put(PARAM_CLIENTLIST, clientList);
        for (String client : rawClientList.split(SPLITTER)) {
            setOfGrantedClient.add(client.trim());
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void init(String featureName, Map<String, String> initParams) {
        super.init(featureName, initParams);
        if (initParams != null && initParams.containsKey(PARAM_CLIENTLIST)) {
            this.rawClientList = initParams.get(PARAM_CLIENTLIST);
        }
        setOfGrantedClient.addAll(Arrays.asList(rawClientList.split(SPLITTER)));
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isToggled(Feature feature, FF4jContext executionContext) {
        if (null == executionContext || !executionContext.getString(CLIENT_HOSTNAME).isPresent()) {
            throw new IllegalArgumentException("To work with " + getClass().getCanonicalName() + " you must provide '"
                    + CLIENT_HOSTNAME + "' parameter in execution context");
        }
        return setOfGrantedClient.contains(executionContext.getString(CLIENT_HOSTNAME).get());
    }


}