package com.dgtz.compress.api;

import com.dgtz.compress.api.compresser.CompressBuilding;
import com.dgtz.db.api.domain.MediaStatus;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;

/**
 * Created by sardor on 5/11/16.
 */
public class MAIN {

    public static void main(String[] args) throws Exception {
        String path = "http://video.asalam.com.s3.amazonaws.com/6791/original/931a0c9b677d9ebaca82a70ebd031c1d?AWSAccessKeyId=AKIAJ5XGXWO3GL3LIJOA&amp;Expires=1462991921&amp;Signature=R6gT2ZnbXwti92%2FoWOM%2FiUNwbFM%3D";

        startCompressing(22664l, 6791l, (short)214, false, 0);

        String meidiIDTHUMB = RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "mediaid:thumb:"+61547162);
        System.out.println(meidiIDTHUMB);

        String mediaID = RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "mediaid:"+61548842);
        System.out.println("MediaAAA "+mediaID);

    }

    public static void startCompressing(long idMedia,
                                     long idUser,
                                     Short duration,
                                     boolean isLive,
                                     int rotation) throws Exception {




        //boolean queue = RMemoryAPI.getInstance().pullIfSetElem(Constants.MEDIA_KEY + "queue", idMedia + "");
        if (true) {

            try {
                CompressBuilding building = new CompressBuilding(duration, idUser, idMedia, isLive, rotation);
                building.begin();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY +idMedia, "progress", new MediaStatus(2).toString());

        }
    }
}
