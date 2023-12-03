package com.zhouhao;

import com.zhouhao.entity.Movement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMongo {
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void Test1(){
        Criteria criteria = Criteria.where("_id").in("5f0d77b35a319e6efab7fb5d");
        Query query = Query.query(criteria);
        System.out.println(mongoTemplate.findOne(query, Movement.class));
    }

}
