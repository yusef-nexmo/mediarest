package com.nexmo.mediarest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

/*
 * All fields are non-primitive, so that we can distinguish their absence from their default value
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaUpdate{

    private String title;
    private String description;
    private String mimeType;
    private Boolean isPublic;

    public MediaUpdate() {}

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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(Boolean v) {
        this.isPublic = v;
    }
}