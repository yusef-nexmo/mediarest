package com.nexmo.mediarest.handlers;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.container.AsyncResponse;
import javax.servlet.http.HttpServletRequest;

import com.nexmo.mediarest.demo.MediaStore;
import com.nexmo.mediarest.entities.MediaUpdate;
import com.nexmo.services.media.client.entity.MediaItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaRequest implements Runnable {
    private static final Logger Logger = LoggerFactory.getLogger(MediaRequest.class);

    private final AsyncResponse asyrsp;
    private final boolean deleteOnly;
    protected final MediaStore store;
    protected final String mediaId;
    protected MediaStore.StoreItem mediaItem;
    protected boolean sentResponse;

    public MediaRequest(String id, boolean del, MediaStore store, AsyncResponse asyrsp) {
        this.mediaId = id;
        this.deleteOnly = del;
        this.store = store;
        this.asyrsp = asyrsp;
    }

    public MediaRequest(String id, MediaStore store, AsyncResponse asyrsp) {
        this(id, false, store, asyrsp);
    }

    @Override
    public void run() {
        if (mediaId != null) {
            if (deleteOnly)
                mediaItem = store.delete(mediaId);
            else
                mediaItem = store.get(mediaId);
            if (mediaItem == null) {
                issueResponse(404);
                return;
            }
        }
        try {
            execute();
        } catch (Throwable ex) {
            Logger.error("Request="+getClass().getName()+" failed on ID="+mediaId+" with responded="+sentResponse+" - "+ex, ex);
            if (!sentResponse)
                issueResponse(500);
        }
    }

    protected void execute() throws Exception {
        issueResponse(200);
    }

    protected void issueResponse(Object obj) {
        issueResponse(200, obj);
    }

    protected void issueResponse(int http_status) {
        issueResponse(http_status, null);
    }

    protected void issueResponse(int http_status, Object obj) {
        Response httprsp = makeResponse(http_status, obj);
        issueResponse(httprsp);
    }

    protected void issueResponse(Response httprsp) {
        asyrsp.resume(httprsp);
        sentResponse = true;
    }

    public static Response makeResponse(int http_status, Object obj) {
        if (obj == null) obj = "";
        return Response.status(Response.Status.fromStatusCode(http_status)).entity(obj).build();
    }


    public static class GetInfoRequest extends MediaRequest {
        public GetInfoRequest(String id, MediaStore store, AsyncResponse asyrsp) {
            super(id, store, asyrsp);
        } 

        @Override
        public void execute() {
            issueResponse(mediaItem.getMeta());
        }
    }


    public static class UpdateRequest extends MediaRequest {
        private final MediaUpdate update;

        public UpdateRequest(String id, MediaUpdate update, MediaStore store, AsyncResponse asyrsp) {
            super(id, store, asyrsp);
            this.update = update;
        } 

        @Override
        public void execute() throws Exception {
            store.update(mediaItem, update);
            super.execute();
        }
    }


    public static class DownloadRequest extends MediaRequest {
        public DownloadRequest(String id, MediaStore store, AsyncResponse asyrsp) {
            super(id, store, asyrsp);
        } 

        @Override
        public void execute() throws IOException {
            try (InputStream strm = mediaItem.getFile()) {
                ResponseBuilder bldr = Response.ok(strm, mediaItem.getMeta().getMimeType());
                bldr = bldr.header("Content-Disposition", "attachment");
                issueResponse(bldr.build());
            }
        }
    }


    public static class UploadRequest extends MediaRequest {
        private final byte[] fileData;
        private final String fileName;
        private final String mimeType;
        private final HttpServletRequest httpreq;

        public UploadRequest(byte[] fileData, String fileName, String mimeType, HttpServletRequest httpreq, MediaStore store, AsyncResponse asyrsp) {
            super(null, store, asyrsp);
            this.fileData = fileData;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.httpreq = httpreq;
        } 

        @Override
        public void execute() {
            MediaStore.StoreItem item = store.create(mimeType, fileName, fileData);
            String location = httpreq.getRequestURI()+"/"+item.getId()+"/info";
            ResponseBuilder bldr = Response.status(Response.Status.fromStatusCode(201));
            bldr = bldr.header("Location", location);
            issueResponse(bldr.build());
        }
    }


    public static class SearchRequest extends MediaRequest {
        public SearchRequest(String startDate, String endDate, String order, int pageNumber, int pageSize, MediaStore store, AsyncResponse asyrsp) {
            super(null, store, asyrsp);
        } 

        @Override
        public void execute() {
            List<MediaItem> lst = new ArrayList<>();
            for (MediaStore.StoreItem item : store.getAll())
                lst.add(item.getMeta());
            issueResponse(lst);
        }
    }
}