
public class XEModification {
	
	public int 		addr;
	public int 		offset;
	public String 	operation;

	public XEModification() {
		// TODO Auto-generated constructor stub
	}
	
	public String getModificationRecord(){
		
		String str = "M";
		str += String.format("%06X",addr);
		str += String.format("%02X",offset);
		str += operation;
		
		return str;
	}

}
