package pojo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.util.Optional;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class PaginatedRequestDto {
    private Integer offset;
    private Integer limit;
    private String dateCreatedStart;
    private String dateCreatedEnd;

    public Optional<Integer> getOffset() {
        return Optional.ofNullable(offset);
    }

    public PaginatedRequestDto setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }

    public PaginatedRequestDto setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Optional<Timestamp> getDateCreatedStart(String format) {
        if(StringUtils.isBlank(dateCreatedStart)){
            return Optional.empty();
        }
        return Optional.ofNullable(PaymentUtil.getTimestamp(dateCreatedStart,format));
    }

    public PaginatedRequestDto setDateCreatedStart(String dateCreatedStart) {
        this.dateCreatedStart = dateCreatedStart;
        return this;
    }

    public Optional<Timestamp> getDateCreatedEnd(String format) {
        if(StringUtils.isBlank(dateCreatedEnd)){
            return Optional.empty();
        }
        return Optional.ofNullable(PaymentUtil.getTimestamp(dateCreatedEnd,format));
    }

    public PaginatedRequestDto setDateCreatedEnd(String dateCreatedEnd) {
        this.dateCreatedEnd = dateCreatedEnd;
        return this;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("offset", offset)
                .append("limit", limit)
                .append("dateCreatedStart", dateCreatedStart)
                .append("dateCreatedEnd", dateCreatedEnd)
                .toString();
    }
}
