package topica.linh.trungnt9.networking.bai1;

/**
 * Human Name, contain method to standardized name
 * @author ljnk975
 *
 */
public final class HumanName {

	/**
	 * Name that it contain
	 */
	private String name;

	/**
	 * Create a human name
	 * @param name name of human
	 */
	public HumanName(String name) {
		this.name = name;
	}

	/**
	 * Standardized name
	 */
	public final void standardized() {
		String[] arName = this.name.split(" "); // split by space
		StringBuilder newName = new StringBuilder(); // make new Builder
		boolean first = true; // firstName?
		for(int i = 0; i < arName.length; i++) {
			String eName = arName[i]; // element of name
			if(eName.length() == 0) // string not have any char, pass
				continue;
			//-> Add first char as uppercase
			if(first) {
				newName.append(Character.toUpperCase(eName.charAt(0)));
				first = false;
			} else
				newName.append(' ').append(Character.toUpperCase(eName.charAt(0)));
			//-> app end other as lower case
			for(int j = 1; j < eName.length(); j++)
				newName.append(Character.toLowerCase(eName.charAt(j)));
		}
		// set name
		this.name = newName.toString();
	}

	@Override
	public String toString() {
		return this.name;
	}

}
