package pojo.remitta;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class RemittaRrrResponse  implements Serializable {
    private String statuscode;
    private String RRR;
    private String status;


    public String getStatuscode() {
        return statuscode;
    }

    public RemittaRrrResponse setStatuscode(String statuscode) {
        this.statuscode = statuscode;
        return this;
    }

    public String getRRR() {
        return RRR;
    }

    public RemittaRrrResponse setRRR(String RRR) {
        this.RRR = RRR;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public RemittaRrrResponse setStatus(String status) {
        this.status = status;
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RemittaRrrResponse{");
        sb.append("statuscode='").append(statuscode).append('\'');
        sb.append(", RRR='").append(RRR).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
