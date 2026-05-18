package blog.util;

import blog.exception.BaseException;
import io.minio.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Component
public class MinioUtil {
    private static MinioClient minioClient;
    private static String bucketName;
    private static String clientPoint;

    public MinioUtil(
            @Value("${minio.clientPoint}") String clientPoint,
            @Value("${minio.serverPoint}") String serverPoint,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey,
            @Value("${minio.bucket}") String bucket
    ) {

        MinioUtil.minioClient = MinioClient.builder()
                .endpoint(serverPoint)
                .credentials(accessKey, secretKey)
                .build();
        MinioUtil.bucketName = bucket;
        MinioUtil.clientPoint = clientPoint;

    }


    private Boolean isFileExists(String objectName) {
        try {
            // 通过获取文件元数据来判断文件是否存在
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SneakyThrows
    public void loadFile(MultipartFile multipartFile, String objectName) {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(multipartFile.getInputStream(),
                        multipartFile.getSize(),
                        -1)
                .contentType(multipartFile.getContentType())
                .build();
        minioClient.putObject(args);
        log.info("上传成功:{}", objectName);
    }

    @SneakyThrows
    public InputStream downLoadFile(String objectName) {
        if (!isFileExists(objectName)) {
            log.error("下载的文件不存在:{}", objectName);
            throw new BaseException("文件不存在:" + objectName);
        }
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        return minioClient.getObject(args);
    }

    public String getFileUrl(String objectName) {
        return clientPoint + "/" + bucketName + "/" + objectName;
    }

    @SneakyThrows
    public void deleteFile(String objectName) {
        if (!isFileExists(objectName)) {
            log.error("需删除的文件不存在:{}", objectName);
            throw new BaseException("文件不存在:" + objectName);
        }
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        minioClient.removeObject(args);
        log.info("删除文件成功:{}", objectName);
    }

}
