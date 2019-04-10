package pojo;

public class Item {
    private String name;
    private String itemId;
    private String quantity;
    private String pricePerItem;
    private String totalAmount;

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public String getItemId() {
        return itemId;
    }

    public Item setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public String getQuantity() {
        return quantity;
    }

    public Item setQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getPricePerItem() {
        return pricePerItem;
    }

    public Item setPricePerItem(String pricePerItem) {
        this.pricePerItem = pricePerItem;
        return this;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public Item setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }
}
