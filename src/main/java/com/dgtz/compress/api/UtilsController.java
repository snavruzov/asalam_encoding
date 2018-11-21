package com.dgtz.compress.api;

import com.dgtz.api.compresser.CompressFactory;
import com.dgtz.api.constants.Media;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.feature.AmazonS3Module;
import com.dgtz.api.utils.ImageConverter;
import com.dgtz.compress.api.compresser.CompressBuilding;
import com.dgtz.db.api.domain.MediaStatus;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sardor on 1/3/14.
 */

@Path("/tools")
public class UtilsController extends Resource {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger("transCoder");

    public UtilsController() {
    }

    @GET
    @Produces("application/json")
    @Path("/compress")
    @Context
    public Response startCompressing(@QueryParam("idm") long idMedia,
                                     @QueryParam("idu") long idUser,
                                     @QueryParam("dura") Short duration,
                                     @QueryParam("live") boolean isLive,
                                     @QueryParam("rotation") int rotation) throws Exception {

        JSONObject json = new JSONObject();
        EnumErrors errors = EnumErrors.NO_ERRORS;

        boolean queue = RMemoryAPI.getInstance().pullIfSetElem(Constants.MEDIA_KEY + "queue", idMedia + "");
        if (queue) {

            log.debug("START BUILIDNG: {}",idMedia);

            try {
                CompressBuilding building = new CompressBuilding(duration, idUser, idMedia, isLive, rotation);
                building.begin();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY +idMedia, "progress", new MediaStatus(2).toString());
            errors = EnumErrors.ERROR_IN_COMPRESSING;
        }

        log.debug("COMPRESS ERROR STATUS: {}", errors);
        json.put("error", errors);

        return createResponse(json);
    }

    @GET
    @Produces("application/json")
    @Path("/thumbnailer")
    @Context
    public Response startCompressingPicture(@QueryParam("idm") long mediaid) throws Exception {

        JSONObject json = new JSONObject();
        EnumErrors errors = EnumErrors.NO_ERRORS;

        String val = (String) RMemoryAPI.getInstance().
                pullElemFromMemory(Constants.MEDIA_KEY + "mediaid:" + mediaid, String.class);
        log.debug("MEDIA INFO::::::: {}", val);
        if (val != null) {
            Long idUser = Long.valueOf(val.split("-")[1]);
            Long idMedia = Long.valueOf(val.split("-")[0]);
            String tmp = "/opt/dump/000000";

            try {

                String link = Constants.encryptAmazonURL(idUser, idMedia, "", "p", "").trim();
                String thumbJ = Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", "").trim();
                String thumbW = Constants.encryptAmazonURL(idUser, idMedia, "webp", "thumb", "").trim();
                boolean isLinkValid = AmazonS3Module.isValidImageFile(link);

                AmazonS3Module s3Module = new AmazonS3Module();
                if (isLinkValid) {
                    URL url = new URL(Constants.STATIC_URL + link);
                    log.debug("URL::::::: {}", Constants.STATIC_URL + link);
                    InputStream file = url.openStream();
                    tmp = "/opt/dump/" + System.currentTimeMillis();
                    ImageConverter.convertToThumb(file, tmp);

                    boolean isRefresh = AmazonS3Module.isValidImageFile(thumbJ);

                    String link_jpg = Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", "", isRefresh);
                    String link_webp = Constants.encryptAmazonURL(idUser, idMedia, "webp", "thumb", "", isRefresh);

                    s3Module.uploadImageFile(link_jpg, tmp + ".jpg");
                    s3Module.uploadImageFile(link_webp, tmp + ".webp");
                } else {
                    link = Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", "");
                    s3Module.copyImageFile("defaults/media.jpg", link);
                    link = Constants.encryptAmazonURL(idUser, idMedia, "webp", "thumb", "");
                    s3Module.copyImageFile("defaults/media.jpg", link);

                }
            } catch (Exception e) {
                log.error("ERROR IN THUMBNAILER ", e);
            } finally {
                Files.deleteIfExists(Paths.get(tmp + ".jpg"));
                Files.deleteIfExists(Paths.get(tmp + ".webp"));
            }
        }

        json.put("error", errors);

        return createResponse(json);
    }

    @GET
    @Produces("application/json")
    @Path("/transpose")
    @Context
    /*ROTATION ANGLE POINT 1-90_ClockWise, 0-default, -1-90_CounterClockwise, 2-180_upsideDown*/
    public Response startRotating(@QueryParam("idm") long idMedia,
                                  @QueryParam("idu") long idUser,
                                  @QueryParam("rotation") int rotation) throws SQLException {

        JSONObject json = new JSONObject();
        EnumErrors errors = EnumErrors.NO_ERRORS;
        if (idMedia > 0) {
            String rootPath = Media.PATH_SETTINGS.getMediaPath();

            String path = rootPath + idUser + "/original/" + idMedia + "_encoded.mp4";
            log.debug("ROTATING {} angle {}", path, rotation);
            CompressFactory factory =
                    new CompressFactory(path, String.valueOf(idUser), String.valueOf(idMedia), (short) 0, false, rotation, (short) 1);
            try {
                errors = factory.build();
            } catch (Exception e) {
                log.error("ERROR IN COMPRESSING ", e);
                errors = EnumErrors.ERROR_IN_COMPRESSING;
            }
        } else {
            errors = EnumErrors.ERROR_IN_COMPRESSING;
        }

        log.debug("ERROR: " + errors);

        json.put("error", errors);

        return createResponse(json);
    }

    @GET
    @Produces("application/json")
    @Path("/error/{code}")
    @Context
    public Response showBeautyErrorPage(@PathParam("code") String code) {

        return createResponse("ERROR: " + code);
    }

}
