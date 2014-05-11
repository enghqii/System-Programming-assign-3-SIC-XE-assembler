import java.util.ArrayList;
import java.util.HashMap;

class Pass2Out {

}

public class XEPass2 {
	
	private static Pass1Out in = null;
	
	private static ArrayList<String> extdef = null;
	private static ArrayList<String> extref = null;
	
	private static HashMap<String, Integer> registers;
	
	private static int section = 0;

	public XEPass2() {
		
	}

	public static Pass2Out Pass2(Pass1Out _in){
		
		in = _in;
		Pass2Out out = null;
		
		registers = new HashMap<String, Integer>();
		registers.put("A", 0); registers.put("X", 1); registers.put("L", 2);
		registers.put("B", 3); registers.put("S", 4); registers.put("T", 5);
		registers.put("F", 6);

		extdef = new ArrayList<String>();
		extref = new ArrayList<String>();

		section = 0;

		for (XEToken token : in.tokens) {

			{
				String operands = "";

				for(int i=0;i<token.operands.length;i++){
					if(token.operands[i].compareTo("") != 0)
						operands += (token.operands[i] + " ");
				}

				System.out.print("[" + String.format("%04X", token.addr) + "]\t"+token.label + "\t" + token.operator + "\t" + operands);
			}
			
			// 1. 格利内靛 积己凳?
			int objCode = generateObjectCode(token);

			if (objCode != -1) {
				// generated
				
				System.out.println(""+String.format("\t%X",objCode));
			}

			// 2. 叼泛萍宏甸 贸府
			switch (token.operator) {
			case "EXTDEF":
				extdef.add(token.operands[0]);
				break;
			case "EXTREF":
				extref.add(token.operands[0]);
				break;
			case "CSECT":
				section++;
				break;
			case "RESB":
				break;
			case "RESW":
				break;
			case "EQU":
				break;
			}

			System.out.println("");
		}

		extdef = null;
		extref = null;
		registers = null;

		return out;
	}

	private static int generateObjectCode(XEToken token) {

		/* LITERAL? */{

			if ("*".compareTo(token.label) == 0) {
				XELiteral literal = new XELiteral(token.operands[0]);

				int objCode = literal.getValue();
				return objCode;
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

				return objCode;

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

				return objCode;
			}
		}

		/* WORD BYTE? */{

			if ("WORD".compareTo(token.operator) == 0
					|| "BYTE".compareTo(token.operator) == 0) {

				for (int i = 0; i < token.operands[0].length(); i++) {
					if (token.operands[0].charAt(i) == '+'
							|| token.operands[0].charAt(i) == '-') {
						// expression
						return 0;
					}
				}

				int objCode = parseConst(token.operands[0]);
				return objCode;

			}

		}

		return -1;
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
