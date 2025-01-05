package ViewModel;

import Model.Pet;

public class PetCard {
	public String mode;
	public String name;
	public String imageUrl;
	public String des;
	public int quantity;
	public String species;
	public float weight;
	public String color;
	public float minPrice;
	public float maxPrice;
	public int age;
	
	
	public float petPrice;
	public int specID = 0;
	
	public Pet pet;
	public String state = "NORMAL";
	
	public PetCard()
	{
		
	}
	 public PetCard(String mode, String name, String imageUrl, String des, int quantity) {
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
