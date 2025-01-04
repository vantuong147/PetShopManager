package Model;

import ViewModel.PetCard;

public class PetSpecies {
    public int id;
    public String speciesName;
    public float avgMinPrice;
    public float avgMaxPrice;
    public float avgWeight;
    public int avgMaxAge;
    public String imageUrl;
    public String des;
    
    public int totalQuantity = 0;
    public int unsoldQuantity = 0;

    public PetSpecies(int id, String speciesName, float avgMinPrice, float avgMaxPrice, float avgWeight, int avgMaxAge, String imageUrl, String des) {
        this.id = id;
        this.speciesName = speciesName;
        this.avgMinPrice = avgMinPrice;
        this.avgMaxPrice = avgMaxPrice;
        this.avgWeight = avgWeight;
        this.avgMaxAge = avgMaxAge;
        this.imageUrl = imageUrl;
        this.des = des;
    }
    
    public void SetQuantity(int totalQuantity, int unsoldQuantity)
    {
    	this.totalQuantity = totalQuantity;
    	this.unsoldQuantity = unsoldQuantity;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public float getAvgMinPrice() {
        return avgMinPrice;
    }

    public void setAvgMinPrice(float avgMinPrice) {
        this.avgMinPrice = avgMinPrice;
    }

    public float getAvgMaxPrice() {
        return avgMaxPrice;
    }

    public void setAvgMaxPrice(float avgMaxPrice) {
        this.avgMaxPrice = avgMaxPrice;
    }

    public float getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(float avgWeight) {
        this.avgWeight = avgWeight;
    }

    public int getAvgMaxAge() {
        return avgMaxAge;
    }

    public void setAvgMaxAge(int avgMaxAge) {
        this.avgMaxAge = avgMaxAge;
    }
    
    public PetCard toPetCard()
    {
    	PetCard pc = new PetCard();
    	pc.mode = "SPEC";
    	pc.name = this.speciesName;
    	pc.imageUrl = this.imageUrl;
    	pc.des = "";
    	pc.quantity = this.unsoldQuantity;
    	pc.species = this.speciesName;
    	pc.specID = this.id;
    	pc.weight = this.avgWeight;
    	pc.color = "Xanh";
    	pc.minPrice = this.avgMinPrice;
    	pc.maxPrice = this.avgMaxPrice;
    	return pc;
    }

    @Override
    public String toString() {
        return "PetSpecies{" +
                "id=" + id +
                ", speciesName='" + speciesName + '\'' +
                ", avgMinPrice=" + avgMinPrice +
                ", avgMaxPrice=" + avgMaxPrice +
                ", avgWeight=" + avgWeight +
                ", avgMaxAge=" + avgMaxAge +
                ", total=" + totalQuantity +
                ", unsold=" + unsoldQuantity +
                '}';
    }
}
