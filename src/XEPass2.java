import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

class Pass2Out {
	public ArrayList<XEControlSection> secitons = new ArrayList<XEControlSection>();
}

public class XEPass2 {
	
	private static Pass1Out in = null;
	
	private static ArrayList<String> extdef = null;
	private static ArrayList<String> extref = null;
	
	private static HashMap<String, Integer> registers;
	
	private static int section = 0;

	private static XEControlSection curSection = null;

	public XEPass2() {
		
	}

	public static Pass2Out Pass2(Pass1Out _in){
		
		in = _in;
		Pass2Out out = new Pass2Out();
		
		registers = new HashMap<String, Integer>();
		registers.put("A", 0); registers.put("X", 1); registers.put("L", 2);
		registers.put("B", 3); registers.put("S", 4); registers.put("T", 5);
		registers.put("F", 6);

		extdef = new ArrayList<String>();
		extref = new ArrayList<String>();

		section = 0;
		
		curSection = null;
		
		int startAddr = 0;
		String txtRecord = "";
		
		for( int idx = 0; idx < in.tokens.size(); idx++ ){
			XEToken token = in.tokens.get(idx);


			// 1. 格利内靛 积己凳?
			String objCode = generateObjectCode(token);

			if (objCode != null) {
				// generated
				
				if(txtRecord.length() + objCode.length() <= 0x1D * 2){
					txtRecord += objCode;
				}else{
					txtRecord = String.format("T%06X%02X%s", startAddr, txtRecord.length()/2, txtRecord);
					curSection.tRecord.add(txtRecord);
					txtRecord = objCode;
					startAddr = token.addr;
				}
			}

			// 2. 叼泛萍宏甸 贸府
			switch (token.operator) {
			case "START" :
				curSection = new XEControlSection();
				curSection.name 	= token.label;
				curSection.startAddr = Integer.parseInt(token.operands[0]);
				startAddr = 0;

				break;

			case "EXTDEF":

				for (int i = 0; i < token.operands.length; i++) {

					if (token.operands[i].compareTo("") != 0) {
						extdef.add(token.operands[i]);

						int addr = in.symbolTables.get(section).get(token.operands[i]).addr;
						curSection.dRecord += String.format("%-6s",token.operands[i]);
						curSection.dRecord += String.format("%06X", addr);
					}
				}

				break;

			case "EXTREF":
				for (int i = 0; i < token.operands.length; i++) {

					if (token.operands[i].compareTo("") != 0) {
						extref.add(token.operands[i]);
						curSection.rRecord += String.format("%-6s",token.operands[i]);
					}
				}
				
				break;
				
			case "CSECT":

				// ends up current section
				if(txtRecord != null && txtRecord.length() > 0){
					txtRecord = String.format("T%06X%02X%s", startAddr, txtRecord.length()/2, txtRecord);
					curSection.tRecord.add(txtRecord);
					txtRecord = "";
				}
				startAddr = 0;
				
				curSection.sectionSize = in.tokens.get(idx-1).addr + in.tokens.get(idx-1).size;
				curSection.hRecord += String.format("%-6s%06X%06X",
						curSection.name, curSection.startAddr,
						curSection.sectionSize);

				out.secitons.add(curSection);

				// create new section
				curSection 			= new XEControlSection();
				curSection.name 	= token.label;
				curSection.startAddr = token.addr;

				section++;
				break;

			case "RESW":
			case "RESB":
				
				// force line feed another text record
				if(txtRecord.length() > 0){
					if(objCode != null){
						// don't write
					} else {
						txtRecord = String.format("T%06X%02X%s", startAddr,
								txtRecord.length() / 2, txtRecord);
						curSection.tRecord.add(txtRecord);
						txtRecord = "";
					}
				}
				startAddr = in.tokens.get(idx+1).addr;
				
				break;
				
			case "EQU":
				
				if(token.operands[0].compareTo("*")  == 0){
					break;
				}

				int val = 0;
				
				for (int i = 0; i < token.operands[0].length(); i++) {
					if (token.operands[0].charAt(i) == '+'
							|| token.operands[0].charAt(i) == '-') {
						// expression;
						
						int accSize = -1;
						
						StringTokenizer stk = new StringTokenizer(token.operands[0], "+-");
						while(stk.hasMoreTokens()){
							String str = stk.nextToken();

							if(accSize <= 0 || token.operands[0].charAt(accSize) == '+')
								val += in.symbolTables.get(section).get(str).addr;
							else if(token.operands[0].charAt(accSize) == '-')
								val -= in.symbolTables.get(section).get(str).addr;
							
							accSize += str.length()+1;
						}
						
					}
				}
				
				token.addr = val;

				break;
			}

			// 3. END
			if(idx ==  in.tokens.size() - 1 ){
				// ends up current section
				if(txtRecord != null || txtRecord.compareTo("") != 0){
					txtRecord = String.format("T%06X%02X%s", startAddr, txtRecord.length()/2, txtRecord);
					curSection.tRecord.add(txtRecord);
					txtRecord = "";
					startAddr = token.addr;
				}
				
				XEToken tkn = in.tokens.get(idx);

				curSection.sectionSize = tkn.addr + tkn.size;
				curSection.hRecord += String.format("%-6s%06X%06X",
						curSection.name, curSection.startAddr,
						curSection.sectionSize);

				out.secitons.add(curSection);
			}
			
			
			// print
			{
				String operands = "";

				for(int i=0;i<token.operands.length;i++){
					if(token.operands[i].compareTo("") != 0)
						operands += (token.operands[i] + " ");
				}

				System.out.print("[" + String.format("%04X", token.addr)
						+ "]\t" + token.label + "\t" + token.operator + "\t"
						+ operands);
				if (objCode != null) {
					System.out.print(""+String.format("\t%s",objCode));
				}
				System.out.println("");
			}
		}

		extdef = null;
		extref = null;
		registers = null;

		return out;
	}

	private static String generateObjectCode(XEToken token) {

		/* LITERAL? */{

			if ("*".compareTo(token.label) == 0) {
				XELiteral literal = new XELiteral(token.operands[0]);

				int objCode = literal.getValue();
				
				String fmt = "%0"+literal.getSize()*2+"X";
				return String.format(fmt, objCode);
			}

		}

		/* OPERATOR? */{
			int type = 0;

			if (token.operator.charAt(0) == '+') {
				type = 4;
			}

			if (in.opTable.containsKey(token.operator) == true) {
				// retrive op-type
				type = in.opTable.get(token.operator).type;
			}

			if (type == 3 || type == 4) {

				int objCode = in.opTable.get(type == 3 ? token.operator
						: token.operator.substring(1)).opcode;

				boolean indirect = false;
				boolean immediate = false;

				if (token.operands[0].compareTo("") != 0
						&& token.operands[0].charAt(0) == '@') {

					objCode |= 0x0002;
					indirect = true;

					token.operands[0] = token.operands[0].substring(1); // cut
																		// off

				} else if (token.operands[0].compareTo("") != 0
						&& token.operands[0].charAt(0) == '#') {

					objCode |= 0x0001;
					immediate = true;

					token.operands[0] = token.operands[0].substring(1); // cut
																		// off

				} else {

					objCode |= 0x0003;
				}

				objCode <<= 4;

				// XBPE
				int xbpe = 0;

				// X
				if (token.operands[1].compareTo("") != 0
						&& token.operands[1].compareTo("X") == 0) {
					xbpe |= 1 << 3;
				}
				// P
				if (type != 4
						&& !immediate
						&& token.operands[0].compareTo("") != 0
						&& (in.symbolTables.get(section).containsKey(token.operands[0]) 
								|| token.operands[0].charAt(0) == '=')) {
					xbpe |= 1 << 1;
				}
				// E
				if (type == 4) {
					xbpe |= 1 << 0;
				}

				objCode |= xbpe;

				objCode <<= (type == 3 ? 12 : 20);

				/* disp */
				int disp = 0;

				if (immediate) {

					disp = Integer.parseInt(token.operands[0]);

				} else if (token.operands[0].compareTo("") != 0) {

					if (in.symbolTables.get(section).containsKey(
							token.operands[0])) {

						// symbol

						int addr = in.symbolTables.get(section).get(token.operands[0]).addr;
						disp = addr - (token.addr + type);

					} else if (extref.contains(token.operands[0])) {

						// external Symbol

						XEModification modif = new XEModification();
						modif.addr = token.addr + 1;
						modif.offset = 5;
						modif.operation = "+"+token.operands[0];
						
						curSection.modifications.add(modif);

						disp = 0;

					} else if (token.operands[0].charAt(0) == '=') {

						// literal

						XELiteral l = new XELiteral(token.operands[0]);
						XELiteral literal = in.literalTable.get(l.getValue());
						disp = literal.addr - (token.addr + type);
					}
				}

				if(type == 3)
					objCode |= (0x00FFF & disp);
				else if (type == 4)
					objCode |= (0xFFFFF & disp);
				
				String fmt = "%0" + type * 2 + "X";
				return String.format(fmt, objCode);

			} else if (type == 2 || type == 1) {

				int objCode = in.opTable.get(token.operator).opcode;

				if (type == 2) {

					objCode <<= 8;
					
					if(token.operands[0].compareTo("") != 0){
						int r1 = registers.get(token.operands[0]);
						r1 <<= 4;
						objCode |= r1;
					}
					if(token.operands[1].compareTo("") != 0){
						int r2 = registers.get(token.operands[1]);
						objCode |= r2;
					}

				} else {

				}

				String fmt = "%0" + type * 2 + "X";
				return String.format(fmt, objCode);
			}
		}

		/* WORD BYTE? */{

			if ("WORD".compareTo(token.operator) == 0
					|| "BYTE".compareTo(token.operator) == 0) {

				for (int i = 0; i < token.operands[0].length(); i++) {
					if (token.operands[0].charAt(i) == '+'
							|| token.operands[0].charAt(i) == '-') {
						// expression
						
						StringTokenizer stk = new StringTokenizer(token.operands[0], "+-");
						
						int accSize = -1;
						while(stk.hasMoreTokens()){
							String str = stk.nextToken();

							XEModification modif = new XEModification();
							modif.addr = token.addr + 1;
							modif.offset = 6;
							char c = accSize<=0?'+':token.operands[0].charAt(accSize - 1);
							modif.operation = c+str;
							accSize += str.length() + 1;
							
							curSection.modifications.add(modif);
						}
						
						
						if ("WORD".compareTo(token.operator) == 0){
							return "000000";
						}else if ("BYTE".compareTo(token.operator) == 0) {
							return "00";
						}
						
						break;
					}
				}

				int objCode = parseConst(token.operands[0]);
				
				String fmt = null;
				
				if ("WORD".compareTo(token.operator) == 0){
					fmt = "%06X";
				}else if ("BYTE".compareTo(token.operator) == 0) {
					fmt = "%02X";
				}

				return String.format(fmt, objCode);
			}

		}

		return null;
	}

	private static int parseConst(String str) {

		switch (str.charAt(0)) {

		case 'C': {
			int value = 0;

			for (int i = 2; i < str.length() - 1; i++) {
				char c = str.charAt(i);
				value <<= 8;
				value |= c;
			}

			return value;
		}
		case 'X': {

			int value = 0;

			String hexStr = str.substring(2, str.length() - 1);
			value = Integer.parseInt(hexStr,16);

			return value;
		}
		}

		return -1;
	}
}
