package ViewModel;

public class PetCard {
	public String name;
	public String imageUrl;
	public String des;
	public int quantity;
	public String species;
	public float weight;
	public String color;
	public float minPrice;
	public float maxPrice;
	
	public PetCard()
	{
		
	}
	 public PetCard(String name, String imageUrl, String des, int quantity) {
	    this.name = name;
	    this.imageUrl = imageUrl;
	    this.des = des;
	    this.quantity = quantity;
	    this.minPrice = 0;
	    this.maxPrice = 10;
	}
	 public String getName()
	 {
		 return name;
	 }
}
