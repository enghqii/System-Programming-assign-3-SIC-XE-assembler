import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Pass1In {

	public ArrayList<String> lines;
	public Map<String, XEOperator> opTable; // ref

	public Pass1In() {
		lines = new ArrayList<String>();
		opTable = null;
	}

}

class Pass1Out {

	// tokens with addr
	public ArrayList<XEToken> tokens = new ArrayList<XEToken>();

	public ArrayList<HashMap<String, XESymbol>> symbolTables;
	public Map<String, XEOperator> opTable; // ref

	public HashMap<Integer, XELiteral> literalTable;

	public Pass1Out() {

		// SYMTAB with initial capacity = 1
		symbolTables = new ArrayList<HashMap<String, XESymbol>>();
		symbolTables.add(new HashMap<String, XESymbol>());

		opTable = null;
		literalTable = new HashMap<Integer, XELiteral>();
	}

}

public class XEPass1 {

	private static HashMap<Integer, XELiteral> literalPool;

	public XEPass1() {

	}

	// pass  1
	public static Pass1Out Pass1(Pass1In in) {

		Pass1Out out = new Pass1Out();
		out.opTable = in.opTable;

		int locctr = 0;
		int section = 0;

		literalPool = new HashMap<Integer, XELiteral>();

		for (int i = 0; i < in.lines.size(); i++) {
			String line = in.lines.get(i);
			XEToken token = ParseLine(line);
			int incAmt = 0;

			if (token == null) {
				continue;
			}

			token.addr = locctr;			

			/* OPERATOR */
			{
				int type = 0;

				if (token.operator.charAt(0) == '+') {
					type = 4;
				}

				if (in.opTable.containsKey(token.operator) == true) {
					// retrive op-type
					type = in.opTable.get(token.operator).type;
				}
				incAmt += type;
			}

			/* DIRECTIVE */
			switch (token.operator) {

			case "WORD":
				incAmt += 3;
				break;

			case "BYTE":
				incAmt += 1;
				break;

			case "RESW": {
				int size = Integer.parseInt(token.operands[0]);
				incAmt += (size * 3);
			}
				break;
			case "RESB": {
				int size = Integer.parseInt(token.operands[0]);
				incAmt += size;
			}
				break;

			case "END":
			case "LTORG":

				for (int val : literalPool.keySet()) {

					XELiteral literal = literalPool.get(val);
					XEToken ltrToken = new XEToken();
					ltrToken.size = literal.getSize();

					ltrToken.addr = locctr;
					ltrToken.label = "*";
					ltrToken.operands[0] = literal.getLtrStr();

					out.tokens.add(ltrToken);

					literal.addr = locctr;
					locctr += literal.getSize();
				}

				out.literalTable.putAll(literalPool);

				literalPool.clear();
				continue;
				// break;

			case "CSECT":

				locctr = 0;
				token.addr = 0;
				section += 1;

				// prepare one more symbol table.
				out.symbolTables.add(new HashMap<String, XESymbol>());

				break;
			}
			
			/* New symbol found */
			if (token.label.compareTo("") != 0
					&& in.opTable.containsKey(token.label) == false) {

				XESymbol sym = new XESymbol();
				sym.name = token.label;
				sym.addr = locctr;

				//
				out.symbolTables.get(section).put(token.label, sym);
			}

			/* Gathering Literals */
			for (int j = 0; j < token.operands.length; j++) {
				if (token.operands[j].compareTo("") != 0
						&& token.operands[j].charAt(0) == '=') {

					XELiteral ltr = new XELiteral(token.operands[j]);
					ltr.section = section;
					addLiteral(ltr);

				}
			}
			
			token.size = incAmt;
			locctr += incAmt;

			out.tokens.add(token);
		}

		literalPool = null;

		return out;
	}

	// 파일로 부터 읽어들인 라인 하나를 토큰으로 쪼개는 함수
	private static XEToken ParseLine(String line) {

		if (line.charAt(0) != '.') {

			XEToken token = new XEToken();

			String[] slices = line.split("\\t");

			token.label = slices[0];
			token.operator = slices[1];

			if (slices.length >= 3 && slices[2] != null) {

				String[] opnds = slices[2].split(",");

				for (int i = 0; i < opnds.length; i++) {

					token.operands[i] = opnds[i];

				}
			}

			return token;

		}

		return null;
	}

	private static void addLiteral(XELiteral literal) {

		if (literalPool.containsKey(literal.getValue()) == false) {
			literalPool.put(literal.getValue(), literal);
		}
	}

}
