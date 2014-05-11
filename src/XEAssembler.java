import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sp.interfacepack.XEToyAssemler1;


public class XEAssembler implements XEToyAssemler1 {
	
	private Map<String,XEOperator> opTable = null;
	
	private Pass1In 	p1In 	= null;
	private Pass1Out 	p1Out 	= null;
	private Pass2Out 	p2Out 	= null;

	public XEAssembler() {
		
	}

	@Override
	public void initialize() {
		
		initInstructionFile("inst.txt");
	}

	@Override
	public void parseData(File input) {
		
		// 0. READ LINES
		p1In = new Pass1In();
		p1In.opTable = opTable;
		
		try {
			BufferedReader inputReader = new BufferedReader(new FileReader(input));
			
			while(true) {
				String line = inputReader.readLine();
				if(line == null) break;
				p1In.lines.add(line);
			}
			
			inputReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 1. DO PASS1
		p1Out = XEPass1.Pass1(p1In);

		// 2. DO PASS2
		p2Out = XEPass2.Pass2(p1Out);
	}

	@Override
	public void printOPCODE() {
		// TODO printOPCODE�� �ܼ�â ��� �� ��� ���� ���� ���� �۾��� �����Ͻø� �˴ϴ�.
		// with Pass2Out

		try {
			BufferedWriter out = new BufferedWriter(
					new FileWriter("output.txt"));

			boolean _1st = true;

			for (XEControlSection section : p2Out.secitons) {

				out.write(section.hRecord);
				out.newLine();

				if ("D".compareTo(section.dRecord) != 0) {
					out.write(section.dRecord);
					out.newLine();
				}
				if ("R".compareTo(section.rRecord) != 0) {
					out.write(section.rRecord);
					out.newLine();
				}

				for (String txt : section.tRecord) {

					out.write(txt);
					out.newLine();
				}

				for (XEModification modif : section.modifications) {
					out.write(modif.getModificationRecord());
					out.newLine();
				}

				if (_1st) {
					out.write(String.format("E%06x\n",
							section.startAddr));
					out.newLine();
				} else {
					out.write("E\n");
					out.newLine();
				}

				_1st = false;

			}
			
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void initInstructionFile(String fileName) {

		try {

			opTable = new HashMap<String, XEOperator>();
			
			File inst = new File(fileName);
			BufferedReader instReader = new BufferedReader(new FileReader(inst));
			
			while(true){
				
				String line = instReader.readLine();
				if(line == null) break;
				
				String tokens[] = line.split("\\|");
				
				XEOperator op = new XEOperator();
				op.name 	= tokens[0];
				op.type 	= Integer.parseInt(tokens[1]);
				op.opcode 	= Integer.parseInt(tokens[2], 16);
				op.nops 	= Integer.parseInt(tokens[3]);
				
				opTable.put(op.name, op);
			}
			
			instReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
