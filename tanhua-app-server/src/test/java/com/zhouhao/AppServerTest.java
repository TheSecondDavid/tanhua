package com.zhouhao;

import com.baidu.aip.face.AipFace;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easemob.im.server.EMException;
import com.easemob.im.server.EMService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.zhouhao.api.CommentApi;
import com.zhouhao.constants.CommentType;
import com.zhouhao.constants.Constants;
import com.zhouhao.dao.UserApi;
import com.zhouhao.entity.Comment;
import com.zhouhao.entity.User;
import com.zhouhao.template.BaiduTemplate;
import com.zhouhao.template.HuanXinTemplate;
import com.zhouhao.template.QiniuTemplate;
import com.zhouhao.utils.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppServerTest {
    @Value("${tanhua.qiniu.accessKey}")
    String accessKey;
    @Value("${tanhua.qiniu.secretKey}")
    String secretKey;
    @Autowired
    BaiduTemplate baiduTemplate;
    @Autowired
    QiniuTemplate qiniuTemplate;
    @DubboReference(version = "2.0")
    private CommentApi commentApi;
    @Autowired
    HuanXinTemplate huanXinTemplate;
    @Autowired
    UserApi userApi;

    @Test
    public void qiniu() {
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;
        String bucket = "tanhua-zhouhao";
        String localFilePath = "src/main/resources/application.yml";
        String key = "application.yml";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            ex.printStackTrace();
            if (ex.response != null) {
                System.err.println(ex.response);
                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Test
    public void baidu() {
        // 初始化一个AipFace
        AipFace client = new AipFace("42356012", "qvr7ho3pfHkcAiHlbtLj4aNO", "aebTsp2jpaDGjrvvGrDi5hssWErivnxS");

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        String image = "https://pic.baike.soso.com/p/20131227/20131227161357-1475638820.jpg";
        String imageType = "URL";

        // 人脸检测
        JSONObject res = client.detect(image, imageType, new HashMap<>());
        System.out.println(res.toString());

        if (res.get("result") == null || res.get("result").toString().equals("null") || res.get("result").toString().equals(""))
            System.out.println("不是脸");
        else
            System.out.println((int) res.getJSONObject("result").get("face_num") > 0);

    }

    @Test
    public void baidu2() {
        boolean detect = baiduTemplate.detect("https://pic.baike.soso.com/p/20131227/20131227161357-1475638820.jpg");
        System.out.println(detect);
    }

    @Test
    public void tokenTest() {
        User user = UserHolder.get();
        System.out.println(user);
    }

    @Test
    public void testSave() {
        Comment comment = new Comment();
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setUserId(106l);
        comment.setCreated(System.currentTimeMillis());
        comment.setContent("测试评论");
        comment.setPublishId(new ObjectId("6094d6adfd2311329c3647b1"));
        commentApi.saveComment(comment);
    }

    @Test
    public void huanXin(){
        try {
            huanXinTemplate.createUser("zhouhao", "18846178317");
        } catch (EMException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void DataUpLoad(){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        List<User> users = userApi.selectList(queryWrapper);
        for(User user: users){
            huanXinTemplate.createUser(Constants.HX_USER_PREFIX + user.getId(), Constants.INIT_PASSWORD);
            user.setHxUser(Constants.HX_USER_PREFIX + user.getId());
            user.setHxPassword(Constants.INIT_PASSWORD);
            userApi.updateById(user);
        }
    }

    @Test
    public void testSend(){
        Boolean res = huanXinTemplate.sendMsg("hx106", "hello world");
        System.out.println(res);
    }
}