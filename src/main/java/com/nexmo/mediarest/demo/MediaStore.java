package com.nexmo.mediarest.demo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.nexmo.mediarest.entities.MediaDescriptor;

public class MediaStore {
    private final AtomicInteger nextId = new AtomicInteger(1);

    private final ConcurrentHashMap<String, StoreItem> storeItems = new ConcurrentHashMap<>();

    public StoreItem get(String id) {
        return storeItems.get(id);
    }

    public void update(StoreItem item, MediaDescriptor update) {
        if (update.getMimeType() != null)
            item.meta.setMimeType(update.getMimeType());
        if (update.getTitle() != null)
            item.meta.setTitle(update.getTitle());
        if (update.getDescription() != null)
            item.meta.setDescription(update.getDescription());
        if (update.getEtag() != null)
            item.meta.setEtag(update.getEtag());
        if (update.getTimesDownloaded() != null)
            item.meta.setTimesDownloaded(update.getTimesDownloaded());
        if (update.getMaxDownloadsAllowed() != null)
            item.meta.setMaxDownloadsAllowed(update.getMaxDownloadsAllowed());
        if (update.getEndOfLife() != null)
            item.meta.setEndOfLife(new Date(update.getEndOfLife()));
        item.meta.setTimeLastUpdated(new Date());
    }

    public StoreItem delete(String id) {
        return storeItems.remove(id);
    }

    public StoreItem create(String mimeType, String filename, byte[] data) {
        String id = "m"+nextId.getAndIncrement();
        MediaItem meta = new MediaItem();
        Date dt = new Date();
        meta.setId(id);
        meta.setMimeType(mimeType);
        meta.setOriginalFileName(filename);
        meta.setTimeCreated(dt);
        if (data != null) meta.setMediaSize(data.length);
        StoreItem item = new StoreItem(meta, data);
        storeItems.put(id, item);
        return item;
    }

    public Collection<StoreItem> getAll() {
        return storeItems.values();
    }


    public static class StoreItem {
        private final MediaItem meta;
        private final byte[] file;

        public String getId() {return meta.getId();}
        public MediaItem getMeta() {return meta;}
        public InputStream getFile() {return new ByteArrayInputStream(file);}

        private StoreItem(MediaItem meta, byte[] data) {
            this.meta = meta;
            this.file = data;
        }
    }
}
