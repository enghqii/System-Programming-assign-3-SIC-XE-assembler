import java.util.ArrayList;


public class XEControlSection {
	
	// H
	public String 	name 		= "";
	public int 	startAddr 	= 0;
	public int 	sectionSize = 0;
	
	public String 	hRecord = "H"; 				// append here
	
	// R,D
	public String 	rRecord 	= "R"; 	// append here
	public String 	dRecord 	= "D"; 	// append here
	
	// T
	public ArrayList<String> 			tRecord = null;
	
	// M
	public ArrayList<XEModification> 	modifications = null;
	

	public XEControlSection() {
		
		tRecord 		= new ArrayList<String>(1);
		modifications 	= new ArrayList<XEModification>(1);
		
	}

}
