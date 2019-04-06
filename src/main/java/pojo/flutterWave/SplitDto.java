package pojo.flutterWave;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class SplitDto {
    private String identifier;
    private String ratio;
    private Long amountInKobo;

    public String getIdentifier() {
        return identifier;
    }

    public SplitDto setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getRatio() {
        return ratio;
    }

    public SplitDto setRatio(String ratio) {
        this.ratio = ratio;
        return this;
    }

    public Long getAmountInKobo() {
        return amountInKobo;
    }

    public SplitDto setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
        return this;
    }
}
