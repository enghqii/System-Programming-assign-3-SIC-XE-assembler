import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sp.interfacepack.XEToyAssemler1;


public class XEAssembler implements XEToyAssemler1 {
	
	private Map<String,XEOperator> opTable = null;

	public XEAssembler() {
		
	}

	@Override
	public void initialize() {
		// TODO initialize������ ����� �ϱ� ���� �����ؾ��ϴ� ���� ����� �� Ŭ���� ���� ���� ����
		
		initInstructionFile("inst.txt");
	}

	@Override
	public void parseData(File input) {
		
		// TODO parseData�� input ���Ͽ� ����� ���� ������ object �ڵ�� ��ȯ�ϴ� ���� ���� // �н�1, �н�2 �����ؾ���.
		
		Pass1In in = new Pass1In();
		
		try {
			BufferedReader inputReader = new BufferedReader(new FileReader(input));
			
			while(true) {
				String line = inputReader.readLine();
				if(line == null) break;
				in.lines.add(line);
			}
			
			inputReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		XEPass1.Pass1(in);
	}

	@Override
	public void printOPCODE() {
		// TODO printOPCODE�� �ܼ�â ��� �� ��� ���� ���� ���� �۾��� �����Ͻø� �˴ϴ�.
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