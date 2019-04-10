package pojo.flutterWave;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class SplitDto {
    private String identifier;
    private Double ratio;
    //    private Long amountInKobo;
    private String code;

    public String getIdentifier() {
        return identifier;
    }

    public SplitDto setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public Double getRatio() {
        return ratio;
    }

    public SplitDto setRatio(Double ratio) {
        this.ratio = ratio;
        return this;
    }

    public String getCode() {
        return code;
    }

    public SplitDto setCode(String code) {
        this.code = code;
        return this;
    }
}
