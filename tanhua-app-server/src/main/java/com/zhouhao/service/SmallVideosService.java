package com.zhouhao.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zhouhao.api.UserInfoApi;
import com.zhouhao.api.VideoApi;
import com.zhouhao.entity.UserInfo;
import com.zhouhao.entity.Video;
import com.zhouhao.template.QiniuTemplate;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.VideoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmallVideosService {

    @Autowired
    private QiniuTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;

    @DubboReference(version = "2.0")
    private VideoApi videoApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @CacheEvict(value="videoList",allEntries = true)
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        String picUrl = ossTemplate.upload(videoThumbnail);
        //2、视频上传到fdfs上，获取请求地址
        //获取文件的后缀名
        String filename = videoFile.getOriginalFilename();  //ssss.avi
        String sufix = filename.substring(filename.lastIndexOf(".")+1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(),
                videoFile.getSize(), sufix, null);//文件输入流，文件长度，文件后缀，元数据
        String videoUrl = webServer.getWebServerUrl() + storePath.getFullPath();
        //3、创建Video对象，并设置属性
        Video video = new Video();
        video.setUserId(UserHolder.getUserId());
        video.setPicUrl(picUrl);
        video.setVideoUrl(videoUrl);
        video.setText("我就是我，不一样的烟火");
        //4、调用API完成保存
        videoApi.save(video);
        //5、构造返回值
        return ResponseEntity.ok(null);
    }

    @Cacheable(value="videoList",key="#page + '_' +  #pagesize")
    public PageResult queryVideoList(Integer page, Integer pagesize) {
        //1、调用API查询分页数据 PageResult<Video>
        PageResult result = videoApi.findAll(page,pagesize);
        //2、获取分页对象中list集合  List<Video>
        List<Video> items = (List<Video>)result.getItems();
        //3、一个Video转化成一个VideoVo对象
        List<VideoVo> list = new ArrayList<>();
        for (Video item : items) {
            UserInfo info = userInfoApi.findById(String.valueOf(item.getUserId()));
            VideoVo vo = VideoVo.init(info, item);
            //加入了作者关注功能，从redis判断是否存在关注的key，如果存在设置hasFocus=1
            if(redisTemplate.hasKey("followUser_"+UserHolder.getUserId()+"_"+item.getUserId())) {
                vo.setHasFocus(1);
            }
            list.add(vo);
        }
        //4、替换PageResult中的list列表
        result.setItems(list);
        //5、构造返回值
        return result;
    }
}
