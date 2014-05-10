
public class XEOperator {
	
	public String 	name;
	public int 		type;
	public int 		opcode;
	public int 		nops;

	public XEOperator() {
		
	}
	
	public XEOperator(String name, int type, int opcode, int nops){
		this.name = name;
		this.type = type;
		this.opcode = opcode;
		this.nops = nops;
	}

}
