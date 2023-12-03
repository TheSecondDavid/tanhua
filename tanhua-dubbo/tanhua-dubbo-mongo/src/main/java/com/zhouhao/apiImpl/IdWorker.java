package com.zhouhao.apiImpl;

import com.zhouhao.entity.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * 查主键
 */
@Component
public class IdWorker {
    @Autowired
    MongoTemplate mongoTemplate;

    public Long getNextId(String collName){
        Criteria criteria = Criteria.where("collName").is(collName);
        Update update = new Update();
        update.inc(collName, 1);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.upsert(true);
        findAndModifyOptions.returnNew(true);

        Sequence sequence = mongoTemplate.findAndModify(Query.query(criteria), update, findAndModifyOptions, Sequence.class);
        return sequence.getSeqId();
    }
}
