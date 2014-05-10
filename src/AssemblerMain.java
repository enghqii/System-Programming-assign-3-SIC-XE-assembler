import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
