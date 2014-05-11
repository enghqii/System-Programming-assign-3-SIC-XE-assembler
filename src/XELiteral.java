
public class XELiteral {

	private String 	ltrStr 	= "";
	
	private int 	value 	= 0;
	private int		size 	= 0;
	
	public int		addr	= 0;
	public int		section = 0;
	
	public XELiteral(String ltrStr) {
		
		this.ltrStr = ltrStr;
		value = 0;
		
		switch(ltrStr.charAt(1)){
		
		case 'C':
			
			for(int i = 3; i < ltrStr.length() - 1; i++){
				char c = ltrStr.charAt(i);
				value <<= 8;
				value |= c;
			}
			
			size = ltrStr.length() - 4;
			
			break;
			
		case 'X':
			
			String hexStr = ltrStr.substring(3, ltrStr.length() - 1);
			value = Integer.parseInt(hexStr);
			
			size = (ltrStr.length() - 4)/2;
			
			break;
		}
	}
	
	public int getValue(){ 
		return this.value; 
	}
	
	public String getLtrStr(){
		return this.ltrStr;
	}
	
	public int getSize(){
		return this.size;
	}

}
