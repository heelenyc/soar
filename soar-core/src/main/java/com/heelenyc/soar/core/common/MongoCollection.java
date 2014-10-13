package com.heelenyc.soar.core.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.geo.GeoResult;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * 通用Mongodb层，基于Spring data
 * mongodb框架，主要对MongoTemplate进行了封装，使其所有的操作都对应到一个Collection上，主要原理如下：</p>
 * 1、一个副本集对应一个MongoClient实例；</br> 2、一个DB对应一个MongoTemplate实例；</br>
 * 3、一个Collection对应一个MongoCollection实例； </p>
 * 这样，对于一个MongoCollection实例的所有操作，都对应到初始化时传入的collection上。 </p> 默认属性（可以修改）：</br>
 * <code>
 * ReadPreference = ReadPreference.secondaryPreferred()</br>
 * WriteConcern = WriteConcern.UNACKNOWLEDGED
 * </code> </br>
 * <b>注意：</b>对这两个值的修改将影响到整个JVM里当前Collection的属性，因为MongoCollection是单例的。 </p>
 * 最终操作数据库以collection为准，collection取值问题，首先取参数，然后是document参数，最后取实例名称</p>
 */
public class MongoCollection<T> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String collection;
    private Class<T> entityClass;

    private ReadPreference readPreference = ReadPreference.secondaryPreferred();
    private WriteConcern writeConcern = WriteConcern.UNACKNOWLEDGED;

    private static Map<String, MongoClient> mongoClientMap = new HashMap<String, MongoClient>();
    private static Map<String, MongoTemplate> mongoTemplateMap = new HashMap<String, MongoTemplate>();
    private MongoTemplate mongoTemplate;

    /**
     * Constructor
     * 
     * @param replicaSet
     *            replicaSet
     * @param db
     *            db
     * @param collection
     *            collection
     * @param entityClass
     *            entityClass
     */
    public MongoCollection(String replicaSet, String db, String collection, Class<T> entityClass) {
        checkParameters(replicaSet, db, collection, entityClass);

        this.collection = collection;
        this.entityClass = entityClass;

        String DbKey = replicaSet + "::" + db;

        // create MongoTemplate instance with double check
        mongoTemplate = mongoTemplateMap.get(DbKey);
        if (mongoTemplate == null) {
            synchronized (mongoTemplateMap) {
                mongoTemplate = mongoTemplateMap.get(DbKey);
                if (mongoTemplate == null) {
                    mongoTemplate = new MongoTemplate(getMongoClient(replicaSet), db);
                    // remove _class
                    MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
                    converter.setTypeMapper(new DefaultMongoTypeMapper(null));

                    mongoTemplateMap.put(DbKey, mongoTemplate);

                    logger.info("create MongoTemplate for replica set [{}], database [{}]", replicaSet, db);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.debug("reuse MongoTemplate instance for replica set [{}], database [{}]", replicaSet, db);
                    }
                }
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.debug("reuse MongoTemplate instance for replica set [{}], database [{}]", replicaSet, db);
            }
        }

        DBCollection dbCollection = getCollection();
        dbCollection.setReadPreference(readPreference);
        dbCollection.setWriteConcern(writeConcern);
    }

    private void checkParameters(String replicaSet, String db, String collection, Class<T> entityClass) {
        if (!StringUtils.hasText(replicaSet)) {
            throw new IllegalArgumentException("argument of replicaSet is required.");
        }
        if (!StringUtils.hasText(db)) {
            throw new IllegalArgumentException("argument of db is required.");
        }
        if (!StringUtils.hasText(collection)) {
            throw new IllegalArgumentException("argument of collection is required.");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("argument of entityClass is required.");
        }

        // 检查entityClass的相关定义与collection是否一致，不一致则会出现不同方法操作不同collection的情况

        // 先检查annotation
        if (entityClass.isAnnotationPresent(Document.class)) {
            Document doc = entityClass.getAnnotation(Document.class);
            if (StringUtils.hasText(doc.collection())) {
                if (doc.collection().equals(collection)) {
                    return;
                } else {
                    throw new IllegalArgumentException("The entityClass's annotation of 'Document' defined [" + doc.collection() + "] should equal to the collection's name [" + collection + "].");
                }
            }
        }

        // 再检查类名
        // String className =
        // MongoCollectionUtils.getPreferredCollectionName(entityClass);
        // if (className.equals(collection)) {
        // return;
        // } else {
        // throw new IllegalArgumentException("The entityClass's name [" +
        // className
        // + "] should equal to the collection's name [" + collection
        // + "], or you can set the annotation of 'Document' in entityClass.");
        // }
    }

    private MongoClient getMongoClient(String replicaSet) {
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

    public void setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
        getCollection().setReadPreference(readPreference);
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
        getCollection().setWriteConcern(writeConcern);
    }

    /**
     * get the MongoTemplate
     * 
     * @return the MongoTemplate
     */
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    /**
     * get the DB
     * 
     * @return the DB
     */
    public DB getDb() {
        return mongoTemplate.getDb();
    }

    /**
     * get the collection
     * 
     * @return the DBCollection
     */
    public DBCollection getCollection() {
        return mongoTemplate.getCollection(collection);
    }

    /**
     * Map the results of an ad-hoc query on the collection for the entity class
     * to a single instance of an object of the specified type.
     * <p/>
     * The object is converted from the MongoDB native representation using an
     * instance of {@see MongoConverter}. Unless configured otherwise, an
     * instance of SimpleMongoConverter will be used.
     * <p/>
     * The query is specified as a {@link Query} which can be created either
     * using the {@link BasicQuery} or the more feature rich {@link Query}.
     * 
     * @param query
     *            the query class that specifies the criteria used to find a
     *            record and also an optional fields specification
     * @return the converted object
     */
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, entityClass, collection);
    }

    /**
     * Map the results of an ad-hoc query on the specified collection to a List
     * of the specified type.
     * <p/>
     * The object is converted from the MongoDB native representation using an
     * instance of {@see MongoConverter}. Unless configured otherwise, an
     * instance of SimpleMongoConverter will be used.
     * <p/>
     * The query is specified as a {@link Query} which can be created either
     * using the {@link BasicQuery} or the more feature rich {@link Query}.
     * 
     * @param query
     *            the query class that specifies the criteria used to find a
     *            record and also an optional fields specification
     * @return the List of converted objects
     */
    public List<T> find(Query query) {
        return mongoTemplate.find(query, entityClass, collection);
    }

    /**
     * Returns the document with the given id from the given collection mapped
     * onto the given target class.
     * 
     * @param id
     *            the id of the document to return
     * @return
     */
    public T findById(Object id) {
        return mongoTemplate.findById(id, entityClass, collection);
    }

    /**
     * Query for a list of objects of type T from the specified collection.
     * <p/>
     * The object is converted from the MongoDB native representation using an
     * instance of {@see MongoConverter}. Unless configured otherwise, an
     * instance of SimpleMongoConverter will be used.
     * <p/>
     * If your collection does not contain a homogeneous collection of types,
     * this operation will not be an efficient way to map objects since the test
     * for class type is done in the client and not on the server.
     * 
     * @return the converted collection
     */
    public List<T> findAll() {
        return mongoTemplate.findAll(entityClass, collection);
    }

    /**
     * Returns {@link GeoResult} for all entities matching the given
     * {@link NearQuery}.
     * 
     * @param near
     *            must not be {@literal null}.
     * @return
     */
    public GeoResults<T> geoNear(NearQuery near) {
        return mongoTemplate.geoNear(near, entityClass, collection);
    }

    public T findAndModify(Query query, Update update) {
        return mongoTemplate.findAndModify(query, update, entityClass, collection);
    }

    public T findAndModify(Query query, Update update, FindAndModifyOptions options) {
        return mongoTemplate.findAndModify(query, update, options, entityClass, collection);
    }

    /**
     * Map the results of an ad-hoc query on the specified collection to a
     * single instance of an object of the specified type. The first document
     * that matches the query is returned and also removed from the collection
     * in the database.
     * <p/>
     * The object is converted from the MongoDB native representation using an
     * instance of {@see MongoConverter}. Unless configured otherwise, an
     * instance of SimpleMongoConverter will be used.
     * <p/>
     * The query is specified as a {@link Query} which can be created either
     * using the {@link BasicQuery} or the more feature rich {@link Query}.
     * 
     * @param query
     *            the query class that specifies the criteria used to find a
     *            record and also an optional fields specification
     * @return the converted object
     */
    public T findAndRemove(Query query) {
        return mongoTemplate.findAndRemove(query, entityClass, collection);
    }

    /**
     * Returns the number of documents for the given {@link Query} query.
     * 
     * @param query
     * @return
     */
    public long count(Query query) {
        return mongoTemplate.count(query, entityClass);
    }

    /**
     * Returns the number of documents for the given {@link Query} querying the
     * given collection.
     * 
     * @param query
     * @param collectionName
     *            must not be {@literal null} or empty.
     * @return
     */
    public long count(Query query, String collectionName) {
        return mongoTemplate.count(query, collection);
    }

    /**
     * Insert the object into the specified collection.
     * <p/>
     * The object is converted to the MongoDB native representation using an
     * instance of {@see MongoConverter}. Unless configured otherwise, an
     * instance of SimpleMongoConverter will be used.
     * <p/>
     * Insert is used to initially store the object into the database. To update
     * an existing object use the save method.
     * 
     * @param objectToSave
     *            the object to store in the collection
     */
    public void insert(Object objectToSave) {
        mongoTemplate.insert(objectToSave, collection);
    }

    /**
     * Insert a list of objects into the collection in a single batch write to
     * the database.
     * 
     * @param batchToSave
     *            the list of objects to save.
     */
    public void batchInsert(Collection<? extends Object> batchToSave) {
        mongoTemplate.insert(batchToSave, collection);
    }

    /**
     * Save the object to the specified collection. This will perform an insert
     * if the object is not already present, that is an 'upsert'.
     * <p/>
     * The object is converted to the MongoDB native representation using an
     * instance of {@see MongoConverter}. Unless configured otherwise, an
     * instance of SimpleMongoConverter will be used.
     * <p/>
     * If you object has an "Id' property, it will be set with the generated Id
     * from MongoDB. If your Id property is a String then MongoDB ObjectId will
     * be used to populate that string. Otherwise, the conversion from ObjectId
     * to your property type will be handled by Spring's BeanWrapper class that
     * leverages Spring 3.0's new Type Cobnversion API. See <a href=
     * "http://static.springsource.org/spring/docs/3.0.x/reference/validation.html#core-convert"
     * >Spring 3 Type Conversion"</a> for more details.
     * 
     * @param objectToSave
     *            the object to store in the collection
     */
    public void save(Object objectToSave) {
        mongoTemplate.save(objectToSave, collection);
    }

    /**
     * Performs an upsert. If no document is found that matches the query, a new
     * document is created and inserted by combining the query document and the
     * update document.
     * 
     * @param query
     *            the query document that specifies the criteria used to select
     *            a record to be updated
     * @param update
     *            the update document that contains the updated object or $
     *            operators to manipulate the existing object.
     * @return the WriteResult which lets you access the results of the previous
     *         write.
     */
    public WriteResult upsert(Query query, Update update) {
        return mongoTemplate.upsert(query, update, collection);
    }

    /**
     * Updates the first object that is found in the specified collection that
     * matches the query document criteria with the provided updated document.
     * 
     * @param query
     *            the query document that specifies the criteria used to select
     *            a record to be updated
     * @param update
     *            the update document that contains the updated object or $
     *            operators to manipulate the existing object.
     * @return the WriteResult which lets you access the results of the previous
     *         write.
     */
    public WriteResult updateFirst(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, collection);
    }

    /**
     * Updates all objects that are found in the specified collection that
     * matches the query document criteria with the provided updated document.
     * 
     * @param query
     *            the query document that specifies the criteria used to select
     *            a record to be updated
     * @param update
     *            the update document that contains the updated object or $
     *            operators to manipulate the existing object.
     * @return the WriteResult which lets you access the results of the previous
     *         write.
     */
    public WriteResult updateMulti(Query query, Update update) {
        return mongoTemplate.updateMulti(query, update, collection);
    }

    /**
     * Removes the given object from the given collection.
     * 
     * @param object
     */
    public void remove(Object object) {
        mongoTemplate.remove(object, collection);
    }

    /**
     * Remove all documents from the specified collection that match the
     * provided query document criteria. There is no conversion/mapping done for
     * any criteria using the id field.
     * 
     * @param query
     *            the query document that specifies the criteria used to remove
     *            a record
     */

    public void remove(Query query) {
        mongoTemplate.remove(query, collection);
    }

    /**
     * Execute a group operation over the entire collection. The group operation
     * entity class should match the 'shape' of the returned object that takes
     * int account the initial document structure as well as any finalize
     * functions.
     * 
     * @param criteria
     *            The criteria that restricts the row that are considered for
     *            grouping. If not specified all rows are considered.
     * @param groupBy
     *            the conditions under which the group operation will be
     *            performed, e.g. keys, initial document, reduce function.
     * @return The results of the group operation
     */
    public GroupByResults<T> group(GroupBy groupBy) {
        return mongoTemplate.group(collection, groupBy, entityClass);
    }

    /**
     * Execute a group operation restricting the rows to those which match the
     * provided Criteria. The group operation entity class should match the
     * 'shape' of the returned object that takes int account the initial
     * document structure as well as any finalize functions.
     * 
     * @param criteria
     *            The criteria that restricts the row that are considered for
     *            grouping. If not specified all rows are considered.
     * @param groupBy
     *            the conditions under which the group operation will be
     *            performed, e.g. keys, initial document, reduce function.
     * @return The results of the group operation
     */
    public GroupByResults<T> group(Criteria criteria, GroupBy groupBy) {
        return mongoTemplate.group(criteria, collection, groupBy, entityClass);
    }

}
