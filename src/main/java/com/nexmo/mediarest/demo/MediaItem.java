package com.nexmo.mediarest.demo;

import java.util.Date;
import java.util.Map;

//Temp standin - this class will be replaced by its media-core namesake */
public class MediaItem {

    private String id;
    private String originalFileName;
    private String sourceUrl; /** If we uploaded media from a remote url, where was that .... **/
    private String mimeType;
    private String description;
    private String path;
    private String title;
    private String applicationId;
    private String accountId;
    private String storeId;
    private String storeMetaParams;
    private int maxDownloadsAllowed;
    private Date endOfLife;
    private int timesDownloaded;
    private String requiresPinCode;
    private String etag;
    private long mediaSize;
    private Date lastDownloaded;
    private Date timeCreated;
    private Date timeLastUpdated;
    private String metadataPrimary;
    private String metadataSecondary;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalFileName() {
        return this.originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreMetaParams() {
        return this.storeMetaParams;
    }

    public void setStoreMetaParams(String storeMetaParams) {
        this.storeMetaParams = storeMetaParams;
    }

    public void setStoreMetaParams(Map<String, String> storeMetaParams) {
        this.storeMetaParams = storeMetaParams.toString();
    }

    public int getMaxDownloadsAllowed() {
        return this.maxDownloadsAllowed;
    }

    public void setMaxDownloadsAllowed(int maxDownloadsAllowed) {
        this.maxDownloadsAllowed = maxDownloadsAllowed;
    }

    public Date getEndOfLife() {
        return this.endOfLife;
    }

    public void setEndOfLife(Date endOfLife) {
        this.endOfLife = endOfLife;
    }

    public int getTimesDownloaded() {
        return this.timesDownloaded;
    }

    public void setTimesDownloaded(int timesDownloaded) {
        this.timesDownloaded = timesDownloaded;
    }

    public String getRequiresPinCode() {
        return this.requiresPinCode;
    }

    public void setRequiresPinCode(String requiresPinCode) {
        this.requiresPinCode = requiresPinCode;
    }

    public String getEtag() {
        return this.etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public long getMediaSize() {
        return this.mediaSize;
    }

    public void setMediaSize(long mediaSize) {
        this.mediaSize = mediaSize;
    }

    public Date getLastDownloaded() {
        return this.lastDownloaded;
    }

    public void setLastDownloaded(Date lastDownloaded) {
        this.lastDownloaded = lastDownloaded;
    }

    public Date getTimeCreated() {
        return this.timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeLastUpdated() {
        return this.timeLastUpdated;
    }

    public void setTimeLastUpdated(Date timeLastUpdated) {
        this.timeLastUpdated = timeLastUpdated;
    }

    public String getMetadataPrimary() {
        return this.metadataPrimary;
    }

    public String getMetadataSecondary() {
        return this.metadataSecondary;
    }

    public void touch() {
        this.timeLastUpdated = new Date(System.currentTimeMillis());
    }

    public boolean isDownloadable() {
        if (this.maxDownloadsAllowed > 0 && this.timesDownloaded >= this.maxDownloadsAllowed)
            return false;
        if (this.endOfLife != null && this.endOfLife.getTime() <= System.currentTimeMillis())
            return false;
        return true;
    }
}
