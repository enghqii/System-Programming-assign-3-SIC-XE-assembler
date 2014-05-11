import java.util.ArrayList;


public class XEControlSection {
	
	// H
	public String 	name 		= "";
	public int 	startAddr 	= 0;
	public int 	sectionSize = 0;
	
	public String 	hRecord = "H"; 				// append here
	
	// R,D
	public String 	externalReference 	= "R"; 	// append here
	public String 	externalDefinition 	= "D"; 	// append here
	
	// T
	public ArrayList<String> 			textRecord = null;
	
	// M
	public ArrayList<XEModification> 	modifications = null;
	

	public XEControlSection() {
		
		textRecord 		= new ArrayList<String>(1);
		modifications 	= new ArrayList<XEModification>(1);
		
	}

}
