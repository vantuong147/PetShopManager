package Model;

import java.util.Objects;

import DAO.PetSpeciesDAO;
import Helper.HelperFunc;
import ViewModel.PetCard;

public class Pet {
	public int id;
	public String petName;
	public float price;
	public int age;
	public String color;
    public String description;
    public String state;
    public int petSpeciesId;
    
    public String images;
    public float weight;

    public Pet(int id, String petName, float price, int age, String color, String description, String state, int petSpeciesId, String images, float weight) {
        this.id = id;
        this.petName = petName;
        this.price = price;
        this.age = age;
        this.color = color;
        this.description = description;
        this.state = state;
        this.petSpeciesId = petSpeciesId;
        this.images = images;
        this.weight = weight;
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
    
    public PetCard toPetCard()
    {
    	PetSpeciesDAO psDAO = new PetSpeciesDAO();
    	PetSpecies p = psDAO.getPetSpeciesById(this.petSpeciesId);
    	
    	PetCard pc = new PetCard();
    	pc.pet = this;
    	pc.mode = "PET";
    	pc.name = this.petName;
    	if (this.images != null && this.images != "")
    		pc.imageUrl = this.images.split(",")[0];
    	else
    		pc.imageUrl = "";
    	pc.des = this.description;
    	pc.quantity = 1;
    	pc.species = p.speciesName;
    	pc.specID = this.petSpeciesId;
    	pc.weight = this.weight;
    	pc.color = this.color;
    	pc.minPrice = -1;
    	pc.maxPrice = -1;
    	pc.petPrice = this.price;
    	pc.age = this.age;
    	return pc;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Nếu cùng một đối tượng thì equals là true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Nếu obj là null hoặc không cùng kiểu lớp thì equals là false
        }
        Pet pet = (Pet) obj; // Ép kiểu về Pet
        return this.id == pet.id; // So sánh theo id
    }

    // Override hashCode() để đảm bảo tính nhất quán với equals
    @Override
    public int hashCode() {
        return Objects.hash(id); // Sử dụng id để tạo mã hash
    }

//    @Override
//    public String toString() {
//        return "Pet{" +
//                "id=" + id +
//                ", petName='" + petName + '\'' +
//                ", price=" + price +
//                ", age=" + age +
//                ", color='" + color + '\'' +
//                ", description='" + description + '\'' +
//                ", state='" + state + '\'' +
//                ", petSpeciesId=" + petSpeciesId +
//                '}';
//    }
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", petName='" + petName + '\'' +
                ", price=" + HelperFunc.formatCurrency(price) +
                '}';
    }
}
