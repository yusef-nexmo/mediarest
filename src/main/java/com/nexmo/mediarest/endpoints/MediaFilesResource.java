package com.nexmo.mediarest.endpoints;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.Authorization;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import com.nexmo.mediarest.demo.MediaStore;
import com.nexmo.mediarest.entities.MediaUpdate;
import com.nexmo.mediarest.handlers.MediaRequest;
import com.nexmo.services.media.client.entity.MediaItem;
import com.nexmo.restsvc.auth.NexmoIdentity;

@javax.ws.rs.Path(MediaFilesResource.APIVERSION+"/media")
@javax.ws.rs.Consumes(MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(MediaType.APPLICATION_JSON)
@Api(authorizations = {@Authorization(value="Bearer")})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Authentication failure"),
        @ApiResponse(code = 404, message = "No such item"),
        @ApiResponse(code = 500, message = "Internal server error") })
public class MediaFilesResource {

    public static final String APIVERSION = "v3";
    private final MediaStore store = new MediaStore();
    private final ExecutorService taskExecutor = Executors.newCachedThreadPool();

    @javax.ws.rs.Path("{media_id}")
    @javax.ws.rs.GET
    @javax.ws.rs.Produces(MediaType.WILDCARD)
    @ApiOperation(value="Download media file", response=OutputStream.class)
    @ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void downloadFile(@PathParam("media_id") String mediaId,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.DownloadRequest(mediaId, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.Path("{media_id}/info")
    @javax.ws.rs.GET
    @ApiOperation(value="Get media-file metadata", response=MediaItem.class)
    @ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void getFileInfo(@PathParam("media_id") String mediaId,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.GetInfoRequest(mediaId, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.GET
    @ApiOperation(value="Search media files", response=MediaItem.class, responseContainer="List")
    @ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void searchFiles(@QueryParam("application_id") String applicationId,
            @QueryParam("start_date") String startDate,
            @QueryParam("end_date") String endDate,
            @QueryParam("order") String order,
            @QueryParam("page_index") int pageNumber,
            @QueryParam("page_size") int pageSize,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.SearchRequest(startDate, endDate, order, pageNumber, pageSize, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.Path("{media_id}")
    @javax.ws.rs.DELETE
    @ApiOperation(value="Delete media file")
    @ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void deleteFile(@PathParam("media_id") String mediaId,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest(mediaId, true, store, asyrsp);
        taskExecutor.submit(task);
    }

    // RFC-1867 compliant file upload
    // TODO: Support upload via URL as well? Would just be another form-data field that's mutually exclusive with filedata
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @javax.ws.rs.POST
    @ApiOperation(value="Upload media file")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Media item created") })
    @ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void uploadFile(@FormDataParam("filedata") FormDataBodyPart fileContent,
            @FormDataParam("filename") String fileName, //allows FormDataBodyPart params to be overridden
            @FormDataParam("mimetype") String mimeType, //allows FormDataBodyPart params to be overridden
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Context UriInfo uri,
            @Suspended AsyncResponse asyrsp) throws IOException {
        InputStream istrm = (fileContent == null ? null : fileContent.getValueAs(InputStream.class));
        if (istrm == null) {
            Response httprsp = MediaRequest.makeResponse(400, "No file data");
            asyrsp.resume(httprsp);
            return;
        }
        byte[] data = readStream(istrm);
        fileName = getFilename(fileName, fileContent);
        mimeType = getMimeType(mimeType, fileContent, data);
        MediaRequest task = new MediaRequest.UploadRequest(data, fileName, mimeType, uri, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.Path("raw")
    @Consumes(MediaType.WILDCARD)
    @javax.ws.rs.POST
    @ApiOperation(value="Upload raw media file")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Media item created") })
    @ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void uploadFileRaw(InputStream istrm,
            @HeaderParam("Content-Type") String mimeType,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Context UriInfo uri,
            @Suspended AsyncResponse asyrsp) throws IOException {
        if (istrm == null) {
            Response httprsp = MediaRequest.makeResponse(400, "No file data");
            asyrsp.resume(httprsp);
            return;
        }
        byte[] data = readStream(istrm);
        mimeType = getMimeType(mimeType, null, data);
        MediaRequest task = new MediaRequest.UploadRequest(data, "filename1", mimeType, uri, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.Path("{media_id}/info")
    @javax.ws.rs.POST
    @ApiOperation(value="Update media-file metadata")
    @ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void updateFile(@PathParam("media_id") String mediaId,
            MediaUpdate update,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.UpdateRequest(mediaId, update, store, asyrsp);
        taskExecutor.submit(task);
    }

    private static String getFilename(String filename, FormDataBodyPart fileContent) {
        if (filename == null || filename.isEmpty()) {
            if (fileContent != null && fileContent.getContentDisposition() != null)
                filename = fileContent.getContentDisposition().getFileName();
        }
        return filename;
    }

    private static String getMimeType(String mimeType, FormDataBodyPart fileContent, byte[] data) throws IOException {
        if (mimeType == null || mimeType.isEmpty()) {
            if (fileContent != null)
                mimeType = fileContent.getMediaType().toString();
        }
        if (mimeType == null || mimeType.isEmpty()) {
            InputStream bstrm = new ByteArrayInputStream(data);
            mimeType = URLConnection.guessContentTypeFromStream(bstrm);
        }
        return mimeType;  
    }

    private static byte[] readStream(InputStream istrm) throws IOException {
        int provisionalSize = istrm.available();
        byte[] readbuf = new byte[provisionalSize == 0 ? 4096 : provisionalSize];
        ByteArrayOutputStream ostrm = new ByteArrayOutputStream(readbuf.length);
        int nbytes;
        while ((nbytes = istrm.read(readbuf)) != -1)
            ostrm.write(readbuf, 0, nbytes);
        return ostrm.toByteArray();
    }
}