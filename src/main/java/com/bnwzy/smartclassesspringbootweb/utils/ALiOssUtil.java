package com.bnwzy.smartclassesspringbootweb.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.bnwzy.smartclassesspringbootweb.config.OssProperties;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableConfigurationProperties(OssProperties.class)
public class ALiOssUtil {

    @Qualifier("aliyun.oss-com.bnwzy.smartclassesspringbootweb.config.OssProperties")
    @Setter
    @Autowired
    private OssProperties ossProperties;


    public String uploadFile(String objectName, InputStream in){


        String url="";
        OSS ossClient = new OSSClientBuilder()
                .build(ossProperties.getEndpoint(),
                        ossProperties.getAccessKeyId(),
                        ossProperties.getAccessKeySecret());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, in);
            ossClient.putObject(putObjectRequest);
            url="https://"+ossProperties.getBucketName()+"."+ossProperties.getEndpoint().substring(ossProperties.getEndpoint().lastIndexOf("/")+1)+"/"+objectName;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return url;
    }
    public boolean fileExists(String filename) {
        OSS ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        try {
            return ossClient.doesObjectExist(ossProperties.getBucketName(), filename);
        } catch (Exception e) {
            System.err.println("检查文件存在时出错: " + e.getMessage());
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    public  String extractPathFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            // 去掉开头的斜杠（如果需要）
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (URISyntaxException e) {
            // 如果解析失败，返回原始URL或抛出异常，根据你的需求决定
            return url;
        }
    }
    public String generateUniqueFilename(String originalFilename) {
        String filename = originalFilename;
        int counter = 1;

        while (fileExists(filename)) {
            int dotIndex = originalFilename.lastIndexOf(".");
            String nameWithoutExt = dotIndex == -1 ? originalFilename : originalFilename.substring(0, dotIndex);
            String ext = dotIndex == -1 ? "" : originalFilename.substring(dotIndex);

            filename = nameWithoutExt + "(" + counter + ")" + ext;
            counter++;
        }

        return filename;
    }
    public List<String> listFilesInFolder(String folderPath) {
        // 初始化OSS客户端
        OSS ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());

        List<String> files = new ArrayList<>();
        try {

            ObjectListing objectListing = ossClient.listObjects(ossProperties.getBucketName(), folderPath);

            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                files.add(objectSummary.getKey());
            }
        } finally {
            ossClient.shutdown();
        }
        return files;
    }
}
