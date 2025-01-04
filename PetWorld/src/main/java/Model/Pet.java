package Model;

public class Pet {
	public int id;
	public String petName;
	public float price;
	public int age;
	public String color;
    public String description;
    public String state;
    public int petSpeciesId;

    public Pet(int id, String petName, float price, int age, String color, String description, String state, int petSpeciesId) {
        this.id = id;
        this.petName = petName;
        this.price = price;
        this.age = age;
        this.color = color;
        this.description = description;
        this.state = state;
        this.petSpeciesId = petSpeciesId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPetSpeciesId() {
        return petSpeciesId;
    }

    public void setPetSpeciesId(int petSpeciesId) {
        this.petSpeciesId = petSpeciesId;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", petName='" + petName + '\'' +
                ", price=" + price +
                ", age=" + age +
                ", color='" + color + '\'' +
                ", description='" + description + '\'' +
                ", state='" + state + '\'' +
                ", petSpeciesId=" + petSpeciesId +
                '}';
    }
}
