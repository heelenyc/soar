package com.heelenyc.soar.core.common.utils;

/**
 * @author yicheng
 * @since 2014年10月15日
 *
 */

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesUtil {
   
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private Properties props;

    public PropertiesUtil(InputStream inputStream) {
        try {
            props = new Properties();
            props.load(inputStream);
        } catch (Exception e) {
            logger.error("Error loading input stream", e);
        }
    }

    public PropertiesUtil(String propertiesFilePath) {
        try {
            props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFilePath));

        } catch (Exception e) {
            logger.error("Error loading properties, filepath: " + propertiesFilePath, e);
        }
    }
    
    public PropertiesUtil(Map<String,Object> map){
        props = new Properties();
        props.putAll(map);
    }

    public boolean hasProperty(String key) {
        return props.containsKey(key);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public int getIntProperty(String key) {
        if (hasProperty(key)) {
            String value = getProperty(key);
            return Integer.parseInt(value);
        }

        return 0;
    }

    public String getProperty(String key, String defaultValue) {
        if (hasProperty(key)) {
            return getProperty(key);
        }

        return defaultValue;
    }

    public int getIntProperty(String key, int defaultValue) {
        if (hasProperty(key)) {
            String value = getProperty(key);
            return Integer.parseInt(value);
        }

        return defaultValue;
    }

    public long getLongProperty(String key, long defaultValue) {
        if (hasProperty(key)) {
            String value = getProperty(key);
            return Long.parseLong(value);
        }

        return defaultValue;
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        if (hasProperty(key)) {
            String value = getProperty(key);
            return Boolean.parseBoolean(value);
        }

        return defaultValue;
    }

    public float getFloatProperty(String key, float defaultValue) {
        if (hasProperty(key)) {
            String value = getProperty(key);
            return Float.parseFloat(value);
        }

        return defaultValue;
    }

    /**
     * redis_master.port.sdf = 2 <br>
     * redis_master.host = 9 <br>
     * redis_master.port = 9 <br>
     * redis_master1.host = 0 <br>
     * redis_master1.port = 0 <br>
     * redis_master1.disable = true <br>
     * 
     * @param prefix
     *            redis_
     * @return {redis_master1={port=0, host=0, disable=true},
     *         redis_master={port={sdf=2}, host=9}}
     */
    public Map<String, Object> getPropsByPrefix(String prefix) {
        return getPropertiesByPrefix(prefix, true);
    }

    public Map<String, Object> getPropsByPrefix(String prefix, boolean isNodeBelongedPrefix) {
        if (StringUtils.isEmpty(prefix)) {
            return getPropertiesByPrefix(prefix, true);
        } else {
            return getPropertiesByPrefix(prefix, isNodeBelongedPrefix);
        }
    }

    private Map<String, Object> getPropertiesByPrefix(String prefix, boolean isNodeBelongedPrefix) {
        Map<String, Object> properties = new HashMap<String, Object>();
        Set<Object> keys = props.keySet();
        for (Object key : keys) {
            String propKey = key + "";
            if (propKey.startsWith(prefix)) {
                String completePrefix = "";
                if (isNodeBelongedPrefix) {
                    completePrefix = getCompletePrefix(prefix, propKey);
                } else {
                    completePrefix = getNextPrefix(prefix, propKey);
                    if (StringUtils.isEmpty(completePrefix))
                        continue;
                }

                if (propKey.contains(completePrefix)) {
                    String node = getNode(completePrefix, completePrefix, true);
                    if (completePrefix.equals(propKey)) {
                        properties.put(node, getProperty(completePrefix));
                    } else {
                        properties.put(node, getPropertiesByPrefix(completePrefix, false));
                    }
                }
            }

        }

        return properties;
    }

    /**
     * redis_master.port.sdf = 2
     * 
     * @param prefix
     *            redis_master.p
     * @param key
     *            redis_master.port.sdf
     * @return redis_master.port
     */
    private String getCompletePrefix(String prefix, String key) {
        if (!key.contains(prefix))
            return null;
        String parts[] = key.split("\\.");
        String completePrefix = null;
        int len = prefix.length();
        if (prefix.endsWith("."))
            len = len - 1;
        for (int i = 0; i < parts.length; i++) {
            len = len - parts[i].length();
            if (len > 0) {
                len = len - 1;
                continue;
            } else {
                try {
                    StringBuffer sb = new StringBuffer();
                    for (int j = 0; j <= i; j++) {
                        sb.append(parts[j]);
                        if (j != i)
                            sb.append(".");
                    }
                    completePrefix = sb.toString();
                } catch (Exception e) {
                    logger.error( "getCompletePrefix error , please check your prefix ",e);
                    return null;
                }
                break;
            }
        }
        return completePrefix;
    }

    /**
     * redis_master.port.sdf = 2
     * 
     * @param prefix
     *            redis_master
     * @param key
     *            redis_master.port.sdf
     * @return redis_master.port
     */
    private String getNextPrefix(String completePrefix, String key) {
        // ������user.a user.aa���������
        if (!key.contains(completePrefix + "."))
            return null;
        String parts[] = key.split("\\.");
        String nexPrefix = null;
        int len = completePrefix.length();
        for (int i = 0; i < parts.length; i++) {
            len = len - parts[i].length();
            if (len > 0) {
                len = len - 1;
                continue;
            } else {
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j <= i + 1; j++) {
                    sb.append(parts[j]);
                    if (j != i + 1)
                        sb.append(".");
                }
                nexPrefix = sb.toString();
                break;
            }
        }
        return nexPrefix;
    }

    /**
     * redis_master.port.sdf = 2 <br>
     * redis_master.host = 9 <br>
     * redis_master1.host = 0 <br>
     * 
     * @param prefix
     *            redis_
     * @return (isNodeBelongedPrefix:true)->redis_master,redis_master1
     *         (isNodeBelongedPrefix:false)->port,host
     */
    private Set<String> getGroups(String prefix, boolean isNodeBelongedPrefix) {
        Set<String> propertyWords = new HashSet<String>();
        Set<Object> keys = props.keySet();
        for (Object key : keys) {
            if ((key + "").startsWith(prefix)) {
                propertyWords.add(getNode(prefix, key + "", isNodeBelongedPrefix));
            }
        }
        return propertyWords;
    }

    public Set<String> getGroups(String prefix) {
        return getGroups(prefix, true);
    }

    /**
     * redis_master.port.sdf = 2
     * 
     * @param prefix
     *            redis_
     * @param key
     *            redis_master.port.sdf
     * @return (isNodeBelongedPrefix:true)->redis_master
     *         (isNodeBelongedPrefix:false)->port
     */
    private String getNode(String prefix, String key, boolean isNodeBelongPrefix) {
        if (!key.contains(prefix))
            return null;
        String parts[] = key.split("\\.");
        String group = null;
        int len = prefix.length();
        if (prefix.endsWith(".")) {
            len = len - 1;
        }
        for (int i = 0; i < parts.length; i++) {
            len = len - parts[i].length();
            if (len > 0) {
                len = len - 1;
                continue;
            }
            if (isNodeBelongPrefix) {
                group = parts[i];
            } else {
                if (i + 1 == parts.length) {
                    return null;
                }
                group = parts[i + 1];
            }
            break;
        }
        return group;
    }

    public static void main(String[] args) {
        PropertiesUtil p = new PropertiesUtil("alarm.properties");
        String prefix = "user";
        //
        System.out.println(p.getNextPrefix(prefix, "user.user"));
        System.out.println(p.getPropertiesByPrefix(prefix, false));
    }

}

