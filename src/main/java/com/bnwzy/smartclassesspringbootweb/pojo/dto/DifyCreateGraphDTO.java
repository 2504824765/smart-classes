package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import java.util.List;
import java.util.Map;

public class DifyCreateGraphDTO {
    // Key: 变量名（如"files"）, Value: 文件对象列表
    private Map<String, List<FileInput>> inputs;
    private String response_mode;
    private String user;

    public Map<String, List<FileInput>> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, List<FileInput>> inputs) {
        this.inputs = inputs;
    }

    public String getResponse_mode() {
        return response_mode;
    }

    public void setResponse_mode(String response_mode) {
        this.response_mode = response_mode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "DifyCreateGraphDTO{" +
                "inputs=" + inputs +
                ", responseMode='" + response_mode + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

    public static class FileInput {
        private String type;          // "document"/"image"/etc.
        private String transfer_method; // "remote_url" 或 "local_file"
        private String url;           // transfer_method=remote_url时必填
        private String upload_file_id;   // transfer_method=local_file时必填

        @Override
        public String toString() {
            return "FileInput{" +
                    "type='" + type + '\'' +
                    ", transferMethod='" + transfer_method + '\'' +
                    ", url='" + url + '\'' +
                    ", uploadFileId='" + upload_file_id + '\'' +
                    '}';
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTransfer_method() {
            return transfer_method;
        }

        public void setTransfer_method(String transfer_method) {
            this.transfer_method = transfer_method;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUpload_file_id() {
            return upload_file_id;
        }

        public void setUpload_file_id(String upload_file_id) {
            this.upload_file_id = upload_file_id;
        }
    }
}