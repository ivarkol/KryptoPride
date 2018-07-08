package ru.airiva.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.enums.RsStatus;
import ru.airiva.exception.BsException;

import java.io.Serializable;

/**
 * @author Ivan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsDto implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RsDto.class);

    private RsStatus status;
    private String error;

    public RsDto() {
    }

    private RsDto(BsException e, RsStatus status) {
        this.error = e.getMessage();
        this.status = status;
    }

    private RsDto(RsStatus status) {
        this.status = status;
    }

    private RsDto(Exception e) {
        this.status = RsStatus.ERROR;
        this.error = e.getMessage();
//        if (e instanceof BsException) {
//            this.error = e.getMessage();
//        } else {
//            this.error = BsException.create(MbError.UNE01).getMessage();
//        }
        logger.error(e.getMessage(), e);
    }

    public static RsDto success() {
        return new RsDto(RsStatus.SUCCESS);
    }

    public static RsDto error(Exception e) {
        return new RsDto(e);
    }

    public static RsDto unauth(BsException e) {
        return new RsDto(e, RsStatus.UNAUTH);
    }

    public static RsDto unreg() {
        return new RsDto(RsStatus.UNREG);
    }

    public static RsDto codewait() {
        return new RsDto(RsStatus.CODEWAIT);
    }
}

