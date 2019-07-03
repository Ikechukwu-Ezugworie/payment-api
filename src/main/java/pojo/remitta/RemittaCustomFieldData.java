package pojo.remitta;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class RemittaCustomFieldData {
    @SerializedName(value = "DESCRIPTION", alternate = {"description"})
    private String description;
    @SerializedName(value = "COLVAL", alternate = {"colval"})
    private String colval;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColval() {
        return colval;
    }

    public void setColval(String colval) {
        this.colval = colval;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
