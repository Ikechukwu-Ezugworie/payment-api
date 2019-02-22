package pojo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.util.Optional;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public abstract class PaginatedRequestDto {
    private Integer offset;
    private Integer limit;
    private String dateCreatedStart;
    private String dateCreatedEnd;

    public Integer getOffset() {
        return offset;
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
        return Optional.ofNullable(PaymentUtil.getTimestamp(format));
    }

    public PaginatedRequestDto setDateCreatedStart(String dateCreatedStart) {
        this.dateCreatedStart = dateCreatedStart;
        return this;
    }

    public Optional<Timestamp> getDateCreatedEnd(String format) {
        return Optional.ofNullable(PaymentUtil.getTimestamp(format));
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
