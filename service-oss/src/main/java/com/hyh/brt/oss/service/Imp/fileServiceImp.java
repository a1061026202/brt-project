package com.atguigu.srb.oss.service.Imp;

import com.atguigu.srb.oss.service.fileService;
import com.atguigu.srb.oss.util.OssProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class fileServiceImp implements fileService {
    @Override
    public String upload(InputStream inputStream, String module, String fileName) {

        // 创建一个 COSClient 的实例
        COSClient cosClient = createCOSClient();

        // 判断BUCKET_NAME是否存在,不存在则创建
        List<Bucket> buckets = cosClient.listBuckets();
        for (Bucket bucketElement : buckets) {
            log.info("buckName不存在,正在创建");
            if (!OssProperties.BUCKET_NAME.equals(bucketElement.getName())) {

                CreateBucketRequest createBucketRequest = new CreateBucketRequest(OssProperties.BUCKET_NAME);
                // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
                createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                Bucket bucketResult = cosClient.createBucket(createBucketRequest);
                log.info("创建oss: {}", bucketResult);
            }
        }

        // 上传文件流
        // 使用高级接口必须先保证本进程存在一个 TransferManager 实例，如果没有则创建
        // 详细代码参见本页：高级接口 -> 创建 TransferManager
        TransferManager transferManager = createTransferManager();


        ObjectMetadata objectMetadata = new ObjectMetadata();

        //构建日期路径
        String timeFolder = new DateTime().toString("/yyyy/MM/dd/");
        fileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
        String key = module + timeFolder + fileName;
        // log.info("key: {}",key);
        PutObjectRequest putObjectRequest = new PutObjectRequest(OssProperties.BUCKET_NAME, key, inputStream,objectMetadata);

        try {
            // 高级接口会返回一个异步结果Upload
            // 可同步地调用 waitForUploadResult 方法等待上传完成，成功返回UploadResult, 失败抛出异常
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
            log.info("结果: {}",uploadResult);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 在确定不再通过 TransferManager 的实例调用高级接口之后，关闭这个实例，防止资源泄露
        shutdownTransferManager(transferManager);

        //文件的url地址
        //https:// bucketname   .  endpoint / + key
        return "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/" + key;
    }

    /**
     * 删除oss文件
     * @param url
     */
    @Override
    public void removeFile(String url) {
        COSClient cosClient = createCOSClient();

//        https://srb-1253645988.cos.ap-guangzhou.myqcloud.com/test/2022/06/30/0cce0512-6b42-410e-9490-3159e8dc5f40.jpg

        String Host = "https://"+OssProperties.BUCKET_NAME+"."+OssProperties.ENDPOINT+"/";
        String key = url.substring(Host.length());
        log.info("key: {}",key);

        try {
            cosClient.deleteObject(OssProperties.BUCKET_NAME, key);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }

// 确认本进程不再使用 cosClient 实例之后，关闭之
        cosClient.shutdown();
    }

    private TransferManager createTransferManager() {
        // 创建一个 COSClient 实例，这是访问 COS 服务的基础实例。
        // 详细代码参见本页: 简单操作 -> 创建 COSClient
        COSClient cosClient = createCOSClient();

        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);

        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5*1024*1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1*1024*1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    private void shutdownTransferManager(TransferManager transferManager) {
        // 指定参数为 true, 则同时会关闭 transferManager 内部的 COSClient 实例。
        // 指定参数为 false, 则不会关闭 transferManager 内部的 COSClient 实例。
        transferManager.shutdownNow(true);
    }


    // 创建 COSClient 实例，这个实例用来后续调用请求
    private COSClient createCOSClient() {
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        COSCredentials cred = new BasicCOSCredentials(OssProperties.SECRET_ID, OssProperties.SECRET_KEY);

        // ClientConfig 中包含了后续请求 COS 的客户端设置：
        ClientConfig clientConfig = new ClientConfig();

        // 设置 bucket 的地域
        // COS_REGION 请参照 https://cloud.tencent.com/document/product/436/6224
        clientConfig.setRegion(new Region("ap-guangzhou"));

        // 设置请求协议, http 或者 https
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 以下的设置，是可选的：

        // 设置 socket 读取超时，默认 30s
        clientConfig.setSocketTimeout(30*1000);
        // 设置建立连接超时，默认 30s
        clientConfig.setConnectionTimeout(30*1000);


        // 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }
}
