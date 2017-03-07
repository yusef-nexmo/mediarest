package com.nexmo.mediarest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.nexmo.mediarest.demo.MediaItem;

/*xxx TODO: Maybe replace this with media-client's MediaItem for sending info to the user and a MediaUpdate for data from the user.
 * But why does client-side MediatItem have Hibernate methods - just blindly copied from media-core?
 */
/*
 * All fields are non-primitive, so that we can distinguish their absence from their default value
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaDescriptor {
    
    private String id;
    private String accountId;
    private String mimeType;
    private Long size;
    private String originalFileName;
    private String title;
    private String description;
    private Long timeCreated;
    private Long timeLastUpdated;
    private Long timeLastDownloaded;
    private Long endOfLife;
    private String etag;
    private String metadataPrimary;
    private String metadataSecondary;
    private Integer timesDownloaded;
    private Integer maxDownloadsAllowed;

    public MediaDescriptor() {}

    public MediaDescriptor(MediaItem item) {
        this.id = item.getId();
        this.accountId = item.getAccountId();
        this.mimeType = item.getMimeType();
        this.size = item.getMediaSize();
        this.originalFileName = item.getOriginalFileName();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.timeCreated = item.getTimeCreated().getTime();
        this.timeLastUpdated = (item.getTimeLastUpdated() == null ? null : item.getTimeCreated().getTime());
        this.timeLastDownloaded = (item.getLastDownloaded() == null ? null : item.getLastDownloaded().getTime());
        this.endOfLife = (item.getEndOfLife() == null ? null : item.getEndOfLife().getTime());
        this.etag = item.getEtag();
        this.metadataPrimary = item.getMetadataPrimary();
        this.metadataSecondary = item.getMetadataSecondary();
        this.maxDownloadsAllowed = item.getMaxDownloadsAllowed();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getMimeType() {
        return mimeType;
    }
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }
    public String getOriginalFileName() {
        return originalFileName;
    }
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getTimeCreated() {
        return timeCreated;
    }
    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }
    public Long getTimeLastUpdated() {
        return timeLastUpdated;
    }
    public void setTimeLastUpdated(Long timeLastUpdated) {
        this.timeLastUpdated = timeLastUpdated;
    }
    public Long getTimeLastDownloaded() {
        return timeLastDownloaded;
    }
    public void setTimeLastDownloaded(Long timeLastDownloaded) {
        this.timeLastDownloaded = timeLastDownloaded;
    }
    public Long getEndOfLife() {
        return endOfLife;
    }
    public void setEndOfLife(Long endOfLife) {
        this.endOfLife = endOfLife;
    }
    public String getEtag() {
        return etag;
    }
    public void setEtag(String etag) {
        this.etag = etag;
    }
    public String getMetadataPrimary() {
        return metadataPrimary;
    }
    public void setMetadataPrimary(String metadataPrimary) {
        this.metadataPrimary = metadataPrimary;
    }
    public String getMetadataSecondary() {
        return metadataSecondary;
    }
    public void setMetadataSecondary(String metadataSecondary) {
        this.metadataSecondary = metadataSecondary;
    }
    public Integer getTimesDownloaded() {
        return timesDownloaded;
    }
    public void setTimesDownloaded(Integer timesDownloaded) {
        this.timesDownloaded = timesDownloaded;
    }
    public Integer getMaxDownloadsAllowed() {
        return maxDownloadsAllowed;
    }
    public void setMaxDownloadsAllowed(Integer maxDownloadsAllowed) {
        this.maxDownloadsAllowed = maxDownloadsAllowed;
    }
}