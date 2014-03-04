package my.ex.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Taxon {

	public String name;
	private Map<Character, Value> values = new HashMap<Character, Value>();

	public Taxon(String name) {
		this.name = name;
	}
	
	public Taxon(String name, Collection<Character> characters) {
		this.name = name;
		this.init(characters);
	}
	
	public Taxon(String name, Map<Character, Value> values) {
		super();
		this.name = name;
		this.values = values;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Value put(Character key, Value value) {
		return values.put(key, value);
	}

	public Value remove(Character key) {
		return values.remove(key);
	}

	public Value get(Character key) {
		return values.get(key);
	}

	public void init(Collection<Character> characters) {
		for(Character character : characters) {
			this.addCharacter(character);
		}
	}

	public void addCharacter(Character character) {
		if(!values.containsKey(character))
			values.put(character, new Value(""));
	}
	
	
	
	
	
}
