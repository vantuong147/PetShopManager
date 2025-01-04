package Model;

import DAO.PetSpeciesDAO;
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
    	pc.mode = "PET";
    	pc.name = this.petName;
    	pc.imageUrl = this.images.split(",")[0];
    	pc.des = this.description;
    	pc.quantity = 1;
    	pc.species = p.speciesName;
    	pc.specID = this.petSpeciesId;
    	pc.weight = this.weight;
    	pc.color = this.color;
    	pc.minPrice = -1;
    	pc.maxPrice = -1;
    	pc.petPrice = this.price;
    	return pc;
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
