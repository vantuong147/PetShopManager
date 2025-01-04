package Model;

public class OrderDetail {
    public int id;
    public float salePrice;
    public int orderId;
    public int petId;

    public OrderDetail(int id, float salePrice, int orderId, int petId) {
        this.id = id;
        this.salePrice = salePrice;
        this.orderId = orderId;
        this.petId = petId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", salePrice=" + salePrice +
                ", orderId=" + orderId +
                ", petId=" + petId +
                '}';
    }
}
