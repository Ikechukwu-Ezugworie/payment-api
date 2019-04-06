package pojo.flutterWave;

import java.util.List;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class FlutterWaveValidationResponseDto {
    private boolean valid;
    private String currencyCode;
    private List<SplitDto> split;

    public boolean isValid() {
        return valid;
    }

    public FlutterWaveValidationResponseDto setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public FlutterWaveValidationResponseDto setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public List<SplitDto> getSplit() {
        return split;
    }

    public FlutterWaveValidationResponseDto setSplit(List<SplitDto> split) {
        this.split = split;
        return this;
    }
}
