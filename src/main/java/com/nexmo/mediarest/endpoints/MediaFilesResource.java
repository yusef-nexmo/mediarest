package com.nexmo.mediarest.endpoints;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

import com.nexmo.mediarest.MediaServiceApplication;
import com.nexmo.mediarest.demo.MediaStore;
import com.nexmo.mediarest.entities.MediaDescriptor;
import com.nexmo.restsvc.auth.NexmoIdentity;

@javax.ws.rs.Path(MediaServiceApplication.APIVERSION+"/media")
@javax.ws.rs.Consumes(MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(authorizations = {@Authorization(value="Bearer")})
public class MediaFilesResource {
    //xxx TODO: pipe all these actions into a CompletableFuture thread, to make use of async
    
    private final MediaStore store = new MediaStore();

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
            @Suspended AsyncResponse asyrsp) throws IOException {
        MediaStore.StoreItem item = store.get(mediaId);
        if (item == null) {
            issueResponse(asyrsp, 404);
            return;
        }
        InputStream strm = null;
        try {
            strm = item.getFile();
            Response httprsp = Response.ok(strm, item.getMeta().getMimeType()).header("Content-Disposition", "attachment").build();
            asyrsp.resume(httprsp);
        } catch (Exception ex) {
            issueResponse(asyrsp, 500);
        } finally {
            if (strm != null)
                strm.close();
        }
    }

    @javax.ws.rs.Path("{media_id}/info")
    @javax.ws.rs.GET
    @io.swagger.annotations.ApiOperation(value="Get media-file metadata", response=MediaDescriptor.class)
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
        MediaStore.StoreItem item = store.get(mediaId);
        if (item == null) {
            issueResponse(asyrsp, 404);
            return;
        }
        issueResponse(asyrsp, new MediaDescriptor(item.getMeta()));
    }

    @javax.ws.rs.GET
    @io.swagger.annotations.ApiOperation(value="Search media files", response=MediaDescriptor.class, responseContainer="List")
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
        List<MediaDescriptor> lst = new ArrayList<>();
        for (MediaStore.StoreItem item : store.getAll()) {
            MediaDescriptor xfer = new MediaDescriptor(item.getMeta());
            lst.add(xfer);
        }
        issueResponse(asyrsp, lst);
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
        MediaStore.StoreItem item = store.delete(mediaId);
        issueResponse(asyrsp, item == null ? 404 : 200);
    }

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
        String filename = (contentDispositionHeader == null ? "anonfile" : contentDispositionHeader.getFileName());
        if (istrm == null) {
            issueResponse(asyrsp, 400, "No file data");
            return;
        }
        int bufsiz = istrm.available();
        if (bufsiz == 0) bufsiz = 4096;
        byte[] buf = new byte[bufsiz];
        ByteArrayOutputStream ostrm = new ByteArrayOutputStream(bufsiz);
        int nbytes = 0;
        while ((nbytes = istrm.read(buf)) != -1) {
            ostrm.write(buf, 0, nbytes);
        }
        MediaStore.StoreItem item = store.create(mimeType, filename, ostrm.toByteArray());
        ostrm.close();
        String location = uri.getAbsolutePath().getPath()+"/"+item.getId()+"/info";
        Response httprsp = Response.status(Response.Status.fromStatusCode(201)).header("Location", location).build();
        asyrsp.resume(httprsp);
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
            MediaDescriptor update,
            @Auth @ApiParam(hidden=true) NexmoIdentity nexmoId,
            @Suspended AsyncResponse asyrsp) {
        MediaStore.StoreItem item = store.get(mediaId);
        if (item == null) {
            issueResponse(asyrsp, 404);
            return;
        }
        store.update(item, update);
        issueResponse(asyrsp, 200);
    }

    private static void issueResponse(AsyncResponse asyrsp, Object obj) {
        issueResponse(asyrsp, 200, obj);
    }

    private static void issueResponse(AsyncResponse asyrsp, int http_status) {
        issueResponse(asyrsp, http_status, null);
    }

    private static void issueResponse(AsyncResponse asyrsp, int http_status, Object obj) {
        if (obj == null) obj = "";
        Response httprsp = Response.status(Response.Status.fromStatusCode(http_status)).entity(obj).build();
        asyrsp.resume(httprsp);
    }
}
