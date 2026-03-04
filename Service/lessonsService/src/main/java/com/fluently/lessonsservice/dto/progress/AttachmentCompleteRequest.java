package com.fluently.lessonsservice.dto.progress;


import lombok.Data;

@Data
public class AttachmentCompleteRequest {
    private String learnerKey;
    private Long attachmentId;

    public String getLearnerKey() {
        return learnerKey;
    }

    public void setLearnerKey(String learnerKey) {
        this.learnerKey = learnerKey;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
