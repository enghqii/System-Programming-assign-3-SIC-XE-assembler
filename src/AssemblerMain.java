import java.io.File;


public class AssemblerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		XEAssembler assembler = new XEAssembler();
		
		File input = new File("input.txt");
		
		assembler.initialize();		
		assembler.parseData(input);
		
	}

}
