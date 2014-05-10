import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sp.interfacepack.XEToyAssemler1;


public class XEAssembler implements XEToyAssemler1 {
	
	private Map<String,XEOperator> opTable = null;

	public XEAssembler() {
		
	}

	@Override
	public void initialize() {
		// TODO initialize에서는 어셈블 하기 전에 수행해야하는 파일 입출력 및 클래스 생성 등을 수행
		
		initInstructionFile("inst.txt");
	}

	@Override
	public void parseData(File input) {
		
		// TODO parseData는 input 파일에 저장된 명령 라인을 object 코드로 전환하는 과정 수행 // 패스1, 패스2 실행해야함.
		
		Pass1In in = new Pass1In();
		in.opTable = opTable;
		
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
		// TODO printOPCODE는 콘솔창 출력 및 출력 파일 생성 등의 작업을 수행하시면 됩니다.
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
