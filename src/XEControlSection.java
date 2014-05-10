import java.util.ArrayList;


public class XEControlSection {
	
	// H
	private String 	name = "";
	private int 	startAddr = 0;
	private int 	sectionSize = 0;
	
	// R,D
	private ArrayList<String> exteralDefinitions;
	private ArrayList<String> exteralReferences;

	public XEControlSection() {
	}

}
