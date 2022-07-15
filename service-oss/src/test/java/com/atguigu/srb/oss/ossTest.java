package com.atguigu.srb.oss;

import com.atguigu.srb.oss.util.OssProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.region.Region;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ossTest {

    @Test
    public void test(){

        COSCredentials cred = new BasicCOSCredentials(OssProperties.SECRET_ID, OssProperties.SECRET_KEY);

        Region region = new Region("COS_REGION");
        ClientConfig clientConfig = new ClientConfig(region);
// 这里建议设置使用 https 协议
// 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
// 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        List<Bucket> buckets = cosClient.listBuckets();
        for (Bucket bucketElement : buckets) {
            if(!OssProperties.BUCKET_NAME.equals(bucketElement.getName())){

                CreateBucketRequest createBucketRequest = new CreateBucketRequest(OssProperties.BUCKET_NAME);
// 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
                createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                Bucket bucketResult = cosClient.createBucket(createBucketRequest);
            }
        }
    }
}
