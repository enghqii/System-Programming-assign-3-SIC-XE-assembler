import java.util.ArrayList;
import java.util.HashMap;


class Pass1In{
	
	public ArrayList<String> lines = new ArrayList<String>();
	
}

class Pass1Out{
	
	public ArrayList<XEToken> tokens = new ArrayList<XEToken>();
	public ArrayList<HashMap<String,XESymbol>> symbolTables = new ArrayList<HashMap<String,XESymbol>>();
	
}

public class XEPass1 {

	public XEPass1() {
		
	}
	
	public static Pass1Out Pass1(Pass1In in) {
		
		Pass1Out out = new Pass1Out();
		
		for(String line : in.lines){
			
			XEToken token = ParseLine(line);
			
			if(token != null)
				System.out.println(token.label + " " + token.operator + " ");
			
		}
		
		return out;
	}

	private static XEToken ParseLine(String line) {

		if(line.charAt(0) != '.'){

			XEToken token = new XEToken();

			String[] slices = line.split("\\t");

			token.label 	= slices[0];
			token.operator 	= slices[1];

			if(slices.length >= 3 && slices[2] != null){

				String[] opnds = slices[2].split(",");

				for(int i=0; i<opnds.length; i++){

					token.operands[i] = opnds[i];

				}
			}

			return token;
			
		}
		
		return null;
	}

}
