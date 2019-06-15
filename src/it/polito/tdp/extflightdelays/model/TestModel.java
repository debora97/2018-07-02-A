package it.polito.tdp.extflightdelays.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		
		model.getAllAp();
		
		System.out.println(model.getAllAp());
		System.out.println(model.getApMiglia(400));
		model.creaGrafo();

	}

}
