package com.heelenyc.soar.keeper.common;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * MongoCollection 管理器，通过这个管理器来操作所有的Collection
 * 
 */
public class MongoCollectionFactory {
    private static Logger logger = LoggerFactory.getLogger(MongoCollectionFactory.class);
    private static final String REPLICA_SET = "localhost:28018";
    private static Object rawMongoClientMutex = new Object();
    private static MongoClient rawMongoClient;

    /**
     * 获取原始的java mongodb  db
     * @param dbName
     * @param collectionName
     * @return
     */
    public static DBCollection getRawColleciton(String dbName, String collectionName) {
        try {
            if (rawMongoClient == null) {
                synchronized (rawMongoClientMutex) {
                    if (rawMongoClient == null) {
                        rawMongoClient = new MongoClient("localhost", 28018);
                    }
                }
            }
            DB db = rawMongoClient.getDB(dbName);
            DBCollection coll = db.getCollection(collectionName);
            return coll;
        } catch (UnknownHostException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("when get db : " + dbName + " collection : " + collectionName, e);
            }
            return null;
        }

    }
    
    /**
     * 获取原始的java mongodb  collection
     * @param dbName
     * @param collectionName
     * @return
     */
    public static DB getRawDB(String dbName) {
        try {
            if (rawMongoClient == null) {
                synchronized (rawMongoClientMutex) {
                    if (rawMongoClient == null) {
                        rawMongoClient = new MongoClient("localhost", 28018);
                    }
                }
            }
            DB db = rawMongoClient.getDB(dbName);
            return db;
        } catch (UnknownHostException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("when get db : " + dbName, e);
            }
            return null;
        }

    }

    @SuppressWarnings("rawtypes")
    private static Map<String, MongoCollection> mongoCollectionMap = new HashMap<String, MongoCollection>();
    // 不依赖于entity的collection，其实就是mongotemplate
    private static Map<String, MongoTemplate> mongoTemplateMap = new HashMap<String, MongoTemplate>();
    private static Map<String, MongoClient> mongoClientMap = new HashMap<String, MongoClient>();

    @SuppressWarnings("unchecked")
    private static <T> MongoCollection<T> getMongoCollection(String replicaSet, String db, String collection, Class<T> entityClass) {
        // create MongoCollection instance with double check
        String collectionKey = entityClass.getName() + "::" + replicaSet + "::" + db + "::" + collection;
        MongoCollection<T> dao = mongoCollectionMap.get(collectionKey);
        if (dao == null) {
            synchronized (mongoCollectionMap) {
                dao = mongoCollectionMap.get(collectionKey);
                if (dao == null) {
                    dao = new MongoCollection<T>(replicaSet, db, collection, entityClass);
                    mongoCollectionMap.put(collectionKey, dao);

                    logger.info("create MongoCollection for replica set [{}], database is [{}], collection is [{}]", replicaSet, db, collection);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("reuse MongoCollection instance for replica set [{}], database is [{}], collection is [{}]", replicaSet, db, collection);
                    }
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("reuse MongoCollection instance for replica set [{}], database is [{}], collection is [{}]", replicaSet, db, collection);
            }
        }
        return dao;
    }

    public static MongoTemplate getMongoTemplate(String db) {
        return getMongoTemplate(REPLICA_SET, db);
    }

    public static MongoTemplate getMongoTemplate(String repliaSet, String db) {
        String mongoTemplateKey = repliaSet + "::" + db;
        MongoTemplate mongoTemplate = mongoTemplateMap.get(mongoTemplateKey);
        if (mongoTemplate == null) {
            synchronized (mongoTemplateMap) {
                mongoTemplate = mongoTemplateMap.get(mongoTemplateKey);
                if (mongoTemplate == null) {
                    mongoTemplate = new MongoTemplate(getMongoClient(repliaSet), db);
                    MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
                    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
                    mongoTemplateMap.put(mongoTemplateKey, mongoTemplate);

                    logger.info("create raw MongoTemplate for replica set [{}], database is [{}]", repliaSet, db);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("reuse raw MongoTemplate instance for replica set [{}], database is [{}]", repliaSet, db);
                    }
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("reuse raw MongoTemplate instance for replica set [{}], database is [{}]", repliaSet, db);
            }
        }
        return mongoTemplate;
    }

    private static MongoClient getMongoClient(String replicaSet) {
        // create MongoClient instance with double check
        MongoClient mongoClient = mongoClientMap.get(replicaSet);
        if (mongoClient == null) {
            synchronized (mongoClientMap) {
                mongoClient = mongoClientMap.get(replicaSet);
                if (mongoClient == null) {
                    String[] hosts = replicaSet.split(",");
                    List<ServerAddress> replSetAddr = new ArrayList<ServerAddress>(hosts.length);
                    for (String host : hosts) {
                        String[] array = host.split(":");
                        try {
                            replSetAddr.add(new ServerAddress(array[0], Integer.parseInt(array[1])));
                        } catch (Exception e) {
                            logger.error("replicaSet format should be \"example1.com:27017,example2.com:27017,example3.com:27017\".", e);
                        }
                    }
                    mongoClient = new MongoClient(replSetAddr);
                    mongoClientMap.put(replicaSet, mongoClient);

                    logger.info("create MongoClient for replica set [{}]", replicaSet);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.debug("reuse MongoClient instance for replica set [{}]", replicaSet);
                    }
                }
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.debug("reuse MongoClient instance for replica set [{}]", replicaSet);
            }
        }
        return mongoClient;
    }


    public static class soarKeeper {
        private static final String REPLICA_SET = "localhost:28018";

        /**
         * monitorUnit collection
         * 
         * @param entityClass
         * @return
         */
        public static <T> MongoCollection<T> service(Class<T> entityClass) {
            return getMongoCollection(REPLICA_SET, "soarKeeper", "service", entityClass);
        }
    }
}
