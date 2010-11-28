/* 
 * Copyright (c) 2008-2010, Hazel Ltd. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hazelcast.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.regex.Pattern;

/**
 * Handle JMX objectName
 *
 * @author Marco Ferrante, DISI - University of Genoa
 */
public class ObjectNameSpec {

    /**
     * MBean name domain
     */
    static String NAME_DOMAIN = "com.hazelcast:";

    private String type = null;
    private String cluster = null;
    private String name = null;

    public static ObjectName getClusterNameFilter(String clusterName) throws MalformedObjectNameException {
        return new ObjectName(NAME_DOMAIN + "Cluster=" + clusterName);
    }

    public static ObjectName getClustersFilter() throws MalformedObjectNameException {
        return new ObjectName(NAME_DOMAIN + "type=Cluster,*");
    }

    public ObjectNameSpec() {
    }

    public ObjectNameSpec(String type, String name) {
        this.type = clean(type);
        this.name = clean(name);
    }

    public ObjectNameSpec(String name) {
        cluster = clean(name);
    }

    /**
     * Escape with quote if required
     */
    private String clean(String name) {
        if (name == null) {
            return null;
        }
        if (Pattern.matches(":\",=\\*\\?", name)) {
            return ObjectName.quote(name);
        } else {
            return name;
        }
    }

    /**
     * Return a nested name, for clustered object
     */
    public ObjectNameSpec getNested(String type) {
        if (cluster == null) {
            throw new IllegalStateException("Not clustered object");
        }
        ObjectNameSpec result = new ObjectNameSpec(cluster);
        result.type = clean(type);
        return result;
    }

    /**
     * Return a nested name, for clustered object
     */
    public ObjectNameSpec getNested(String type, String name) {
        if (cluster == null) {
            throw new IllegalStateException("Not clustered object");
        }
        ObjectNameSpec result = new ObjectNameSpec(cluster);
        result.type = clean(type);
        result.name = clean(name);
        return result;
    }

    public ObjectName buildObjectName() throws Exception {
        StringBuffer sb = new StringBuffer(NAME_DOMAIN);
        if (type != null) {
            sb.append("type=").append(type);
        }
        if (cluster != null) {
            if (type != null) {
                sb.append(',');
            }
            sb.append("Cluster=").append(cluster);
        }
        if (name != null) {
            if (type != null || cluster != null) {
                sb.append(',');
            }
            sb.append("name=").append(name);
        }
        return new ObjectName(sb.toString());
    }

    /**
     * Builde the name, overwriting the defaults
     */
    public ObjectName buildObjectName(String type, String name) throws Exception {
        StringBuffer sb = new StringBuffer(NAME_DOMAIN);
        if (type != null) {
            sb.append("type=").append(clean(type));
        }
//		if (cluster != null) {
//			if (type != null) {
//				sb.append(',');
//			}	
//			sb.append("Cluster=").append(cluster);
//		}
        if (name != null) {
            if (type != null || cluster != null) {
                sb.append(',');
            }
            sb.append("name=").append(clean(name));
        }
        return new ObjectName(sb.toString());
    }
}
