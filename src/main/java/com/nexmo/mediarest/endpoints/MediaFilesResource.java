package com.nexmo.mediarest.endpoints;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.nexmo.mediarest.demo.MediaStore;
import com.nexmo.mediarest.entities.MediaUpdate;
import com.nexmo.mediarest.handlers.MediaRequest;
import com.nexmo.services.media.client.entity.MediaItem;
import com.nexmo.restsvc.auth.NexmoIdentity;

@javax.ws.rs.Path(MediaFilesResource.APIVERSION+"/media")
@javax.ws.rs.Consumes(MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(authorizations = {@Authorization(value="Bearer")})
public class MediaFilesResource {

    public static final String APIVERSION = "v3";
    private final MediaStore store = new MediaStore();
    private final ExecutorService taskExecutor = Executors.newCachedThreadPool();

    @javax.ws.rs.Path("{media_id}")
    @javax.ws.rs.GET
    @javax.ws.rs.Produces(MediaType.WILDCARD)
    @io.swagger.annotations.ApiOperation(value="Download media file")
    @io.swagger.annotations.ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Authentication failure"),
            @ApiResponse(code = 404, message = "No such item"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @io.swagger.annotations.ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @io.swagger.annotations.ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @io.swagger.annotations.ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void downloadFile(@PathParam("media_id") String mediaId,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.DownloadRequest(mediaId, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.Path("{media_id}/info")
    @javax.ws.rs.GET
    @io.swagger.annotations.ApiOperation(value="Get media-file metadata", response=MediaItem.class)
    @io.swagger.annotations.ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Authentication failure"),
            @ApiResponse(code = 404, message = "No such item"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @io.swagger.annotations.ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
            @io.swagger.annotations.ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
            @io.swagger.annotations.ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void getFileInfo(@PathParam("media_id") String mediaId,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.GetInfoRequest(mediaId, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.GET
    @io.swagger.annotations.ApiOperation(value="Search media files", response=MediaItem.class, responseContainer="List")
    @io.swagger.annotations.ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Authentication failure"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @io.swagger.annotations.ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @io.swagger.annotations.ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @io.swagger.annotations.ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void searchFiles(@QueryParam("application_id") String applicatoinId,
            @QueryParam("start_date") String startDate,
            @QueryParam("end_date") String endDate,
            @QueryParam("order") String order,
            @QueryParam("page_number") int pageNumber,
            @QueryParam("page_size") int pageSize,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.SearchRequest(startDate, endDate, order, pageNumber, pageSize, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.Path("{media_id}")
    @javax.ws.rs.DELETE
    @io.swagger.annotations.ApiOperation(value="Delete media file")
    @io.swagger.annotations.ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Authentication failure"),
            @ApiResponse(code = 404, message = "No such item"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @io.swagger.annotations.ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @io.swagger.annotations.ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @io.swagger.annotations.ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void deleteFile(@PathParam("media_id") String mediaId,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest(mediaId, true, store, asyrsp);
        taskExecutor.submit(task);
    }

    // TODO: Support upload via URL as well? Would just be another form-data field that's mutually exclusive with filedata
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @javax.ws.rs.POST
    @io.swagger.annotations.ApiOperation(value="Upload media file")
    @io.swagger.annotations.ApiResponses(value = {
            @ApiResponse(code = 201, message = "Media item created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Authentication failure"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @io.swagger.annotations.ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @io.swagger.annotations.ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @io.swagger.annotations.ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void uploadFile(@FormDataParam("filedata") InputStream istrm,
            @FormDataParam("filename") FormDataContentDisposition contentDispositionHeader,
            @FormDataParam("mimetype") String mimeType,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmo_id,
            @Context UriInfo uri,
            @Suspended AsyncResponse asyrsp) throws IOException {
        if (istrm == null) {
            Response httprsp = MediaRequest.makeResponse(400, "No file data");
            asyrsp.resume(httprsp);
            return;
        }
        byte[] data = readStream(istrm);
        String filename = (contentDispositionHeader == null ? "anonfile" : contentDispositionHeader.getFileName());
        if (mimeType == null || mimeType.isEmpty()) {
            InputStream bstrm = new ByteArrayInputStream(data);
            mimeType = URLConnection.guessContentTypeFromStream(bstrm);
        }
        MediaRequest task = new MediaRequest.UploadRequest(data, filename, mimeType, uri, store, asyrsp);
        taskExecutor.submit(task);
    }

    @javax.ws.rs.Path("{media_id}/info")
    @javax.ws.rs.POST
    @io.swagger.annotations.ApiOperation(value="Update media-file metadata")
    @io.swagger.annotations.ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Authentication failure"),
            @ApiResponse(code = 404, message = "No such item"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @io.swagger.annotations.ApiImplicitParams({ //this aids the Swagger UI's try-it-out feature
        @io.swagger.annotations.ApiImplicitParam(name = "api_key", value = "Username for password-based login", paramType="query", dataType="string"),
        @io.swagger.annotations.ApiImplicitParam(name = "api_secret", value = "Password for password-based login", paramType="query", dataType="string", defaultValue="secret1")
    })
    public void updateFile(@PathParam("media_id") String mediaId,
            MediaUpdate update,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaRequest task = new MediaRequest.UpdateRequest(mediaId, update, store, asyrsp);
        taskExecutor.submit(task);
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