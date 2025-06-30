package com.bnwzy.smartclassesspringbootweb.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class ALiOssUtil {

    private static final String ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";

    private static final String ACCESS_KEY_ID="LTAI5tNnJmjR8fKkrh9nBb7B";
    private static final String ACCESS_KEY_SECRET="lmX8nWIX5DuBae6RKkoYhmM9SDGXeD";
    private static final String BUCKET_NAME = "smart-class-northeast";
    public static String uploadFile(String objectName, InputStream in){


        String url="";
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName, in);
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            url="https://"+BUCKET_NAME+"."+ENDPOINT.substring(ENDPOINT.lastIndexOf("/")+1)+"/"+objectName;
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
    public static boolean fileExists(String filename) {
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        try {
            return ossClient.doesObjectExist(BUCKET_NAME, filename);
        } catch (Exception e) {
            System.err.println("检查文件存在时出错: " + e.getMessage());
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    public static String extractPathFromUrl(String url) {
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
    public static String generateUniqueFilename(String originalFilename) {
        String filename = originalFilename;
        int counter = 1;

        while (ALiOssUtil.fileExists(filename)) {
            int dotIndex = originalFilename.lastIndexOf(".");
            String nameWithoutExt = dotIndex == -1 ? originalFilename : originalFilename.substring(0, dotIndex);
            String ext = dotIndex == -1 ? "" : originalFilename.substring(dotIndex);

            filename = nameWithoutExt + "(" + counter + ")" + ext;
            counter++;
        }

        return filename;
    }
}
