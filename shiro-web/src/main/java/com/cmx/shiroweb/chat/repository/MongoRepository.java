package com.cmx.shiroweb.chat.repository;

import com.alibaba.fastjson.JSON;
import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * @author cmx
 * @date 2019/3/6
 */
@Slf4j
@Component
public class MongoRepository<T> {

    @Autowired
    private MongoTemplate mongoTemplate;


    public void insert(T t, String collectionName) {
        mongoTemplate.insert(JSON.toJSONString(t), collectionName);
    }

    public void insert(String jsonString, String collectionName) {
        mongoTemplate.insert(jsonString, collectionName);
    }

    public T findOne(Map<String, Object> fieldMap, String collectionName, Class<T> clazz){
        Criteria criteria = new Criteria();
        fieldMap.forEach((k, v) -> criteria.andOperator(Criteria.where(k).is(v)));
        return mongoTemplate.findOne(new Query(criteria), clazz , collectionName);
    }

    public int update(Map<String, Object> fieldMap, Map<String, Object> param, String collectionName){
        Criteria criteria = new Criteria();
        fieldMap.forEach((k, v) -> criteria.andOperator(Criteria.where(k).is(v)));
        Update update = new Update();
        param.forEach(update::set);
        WriteResult writeResult = mongoTemplate.updateFirst(new Query(criteria), update, collectionName);
        return writeResult.getN();
    }

}
