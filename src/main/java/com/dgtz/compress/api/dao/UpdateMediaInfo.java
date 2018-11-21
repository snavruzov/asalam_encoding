package com.dgtz.compress.api.dao;

import com.dgtz.api.enums.EnumErrors;
import com.dgtz.compress.api.db.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by root on 1/30/14.
 */
public class UpdateMediaInfo {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger("transCoder");

    public EnumErrors notify(long idUser, long idMedia) throws SQLException {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        Connection dbConnection = null;

        try {
            dbConnection = JDBCUtil.getDBConnection();
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;

            log.debug("INIT Notify");
            if (idUser != 0) {
                try {
                    preparedStatement = dbConnection.prepareStatement(
                            String.format("SELECT a.t FROM create_media_notification(%s,%s,2) as a(t)",
                                    idMedia, idUser));
                    rs = preparedStatement.executeQuery();
                } finally {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }

            }

            dbConnection.commit();
        } catch (SQLException e) {
            dbConnection.rollback();
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN SENDING NOTIFY {} ", e);
        } finally {

            if (dbConnection != null) {
                dbConnection.close();
            }

        }
        return errors;
    }

    public EnumErrors update(Long idMedia, Integer status, boolean isLive) throws SQLException {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            dbConnection = JDBCUtil.getDBConnection();
            log.debug("UPDATE INIT status: {} idMedia: {}", status, idMedia);
            if (!isLive) {
                preparedStatement = dbConnection.prepareStatement("UPDATE dc_media SET progress = ? WHERE id_media = ?");
                preparedStatement.setInt(1, status);
                preparedStatement.setLong(2, idMedia);
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = dbConnection.prepareStatement("UPDATE dc_media SET is_live = FALSE, progress = ?, url='media/v' WHERE id_media = ?");
                preparedStatement.setInt(1, status);
                preparedStatement.setLong(2, idMedia);
                preparedStatement.executeUpdate();

            }

            dbConnection.commit();

        } catch (SQLException e) {
            dbConnection.rollback();
            errors = EnumErrors.PUBLISH_ERR;
            log.error("ERROR IN UPDATING {}", e);

        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }

        }

        return errors;

    }

}
