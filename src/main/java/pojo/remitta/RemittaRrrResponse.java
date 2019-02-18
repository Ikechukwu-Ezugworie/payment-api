package pojo.remitta;

public class RemittaRrrResponse {
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
}
