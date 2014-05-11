import java.util.ArrayList;


public class XEControlSection {
	
	// H
	private String 	name 		= "";
	private int 	startAddr 	= 0;
	private int 	sectionSize = 0;
	
	private String 	hRecord = "H"; 				// append here
	
	// R,D
	private String 	externalReference 	= "R"; 	// append here
	private String 	externalDefinition 	= "D"; 	// append here
	
	// T
	private ArrayList<String> 			textRecord = null;
	
	// M
	private ArrayList<XEModification> 	modifications = null;
	

	public XEControlSection() {
		
		textRecord 		= new ArrayList<String>(1);
		modifications 	= new ArrayList<XEModification>(1);
		
	}

}
