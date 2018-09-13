package com.cmx.shiroservice.log.appender;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public abstract class MongoDBAppender<E> extends UnsynchronizedAppenderBase<E> {

    private MongoClient mongoClient;
    private MongoCollection<Document> collection;
    private String host;
    private int port;
    private String dbName;
    private String collectionName;
    private String userName;
    private String password;


    private int connectionsPerHost = 10;
    private int threadsAllowedToBlockForConnectionMutiplier = 5;
    private int maxWaitTime = 1000*60*2;
    private int connectTimeout;
    private int socketTimeout;


    protected MongoDBAppender(String collectionName){
        this.collectionName = collectionName;
    }

    @Override
    public void start() {
        try{
            connectionToMongoDB();
            super.start();
        } catch(Exception e){
            addError("error create mongo server Client error start fail!");
        }
    }

    private void connectionToMongoDB() {
        if(StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(password)){
            MongoCredential mongoCredential = MongoCredential.createCredential(userName, dbName, password.toCharArray());
            mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(mongoCredential), buildOptions());
        }else{
            mongoClient = new MongoClient(new ServerAddress(host, port), buildOptions());
        }
        MongoDatabase database = mongoClient.getDatabase(dbName);
        collection = database.getCollection(collectionName);
    }


    private MongoClientOptions buildOptions(){
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(connectionsPerHost)
                .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMutiplier)
                .maxWaitTime(maxWaitTime)
                .connectTimeout(connectTimeout)
                .socketTimeout(socketTimeout)
                .build();
        return options;
    }

    protected abstract Document tomongoDocument(E event);

    @Override
    protected void append(E eventObject) {
        if(collection == null){
            throw new NullPointerException("log appender error , mongo collection is null!");
        }

        collection.insertOne(tomongoDocument(eventObject));
    }


    @Override
    public void stop() {
        if(mongoClient != null ){
            mongoClient.close();
        }
        super.stop();
    }
}
