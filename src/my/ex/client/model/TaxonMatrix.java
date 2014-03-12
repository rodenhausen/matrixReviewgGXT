package my.ex.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Change so that all changes are to be done through matrix interface.
 * Otherwise problemantic, if Taxon is changed outside and not consistent with characters in matrix
 * @author rodenhausen
 *
 */
public class TaxonMatrix {

	private List<Taxon> taxa = new ArrayList<Taxon>();
	private List<Character> characters = new ArrayList<Character>();

	public TaxonMatrix(List<Character> characters) {
		for(Character character : characters) {
			this.characters.add(character);
		}
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}

	public List<Character> getCharacters() {
		return characters;
	}
	
	public Taxon addTaxon(String name) {
		Taxon result = new Taxon(name, "", characters);
		taxa.add(result);
		return result;
	}
	
	public void addTaxon(Taxon taxon) {
		taxon.init(characters);
		taxa.add(taxon);
	}
	
	public void addCharacter(Character character) {
		this.characters.add(character);
		for(Taxon taxon : taxa) {
			taxon.addCharacter(character);
		}
	}

	//TODO, key really ID in order?
	public String getId(Taxon item) {
		return String.valueOf(item.hashCode());
	}

	public void removeCharacter(int i) {
		this.characters.remove(i);
	}

	public void removeCharacter(Character character) {
		this.characters.remove(character);
	}
	
	public void removeTaxon(Taxon taxon) {
		this.taxa.remove(taxon);
	}
	
	public void removeTaxon(int i) {
		this.taxa.remove(i);
	}
	
	/*public Value getCharacterValue(Character character, Taxon taxon) {
		if(!taxa.contains(taxon)) {
			throw new IllegalArgumentException("Taxon is not contained in the matrix");
		}
		if(!characters.contains(character)) {
			throw new IllegalArgumentException("Character is not contained in the matrix");
		}
		return 
		
		return values.get(taxon).get(character);
	}*/
	
}
