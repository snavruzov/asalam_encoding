package com.dgtz.compress.api.compresser;

import com.dgtz.api.compresser.CompressFactory;
import com.dgtz.api.feature.AmazonS3Module;
import com.dgtz.mcache.api.factory.Constants;
import org.slf4j.LoggerFactory;

/**
 * Created by Sardor Navruzov CEO, DGTZ.
 */
public class CompressBuilding {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger("transCoder");

    private Short duration;
    private long idUser;
    private long idMedia;
    private boolean live;
    private int rotation;

    public CompressBuilding() {

    }

    public CompressBuilding(Short duration, long idUser, long idMedia, boolean live, int rotation) {
        this.duration = duration;
        this.idUser = idUser;
        this.idMedia = idMedia;
        this.live = live;
        this.rotation = rotation;
    }

    public void begin() {
        if (idMedia > 0) {
            log.debug("EXECUTER ISLIVE: {}", live);
            try {
                AmazonS3Module s3 = new AmazonS3Module();
                log.debug("Check amazon: ", "OK");
                String link = Constants.encryptAmazonURL(idUser, idMedia, "_original", "origin", Constants.VIDEO_URL);

                log.debug("Start building video");
                CompressFactory factory =
                        new CompressFactory(link, String.valueOf(idUser), String.valueOf(idMedia),
                                duration, live, rotation, (short) 0);


                factory.build();
            } catch (Exception e) {
                log.error("ERROR_IN_COMPRESSING", e);
            }
        }

    }
}
