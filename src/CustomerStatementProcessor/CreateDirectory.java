package CustomerStatementProcessor;

import java.io.File;

public class CreateDirectory {

	public static void main(String[] args) {
		File f = new File("D:\\Basha"); 

		if (f.mkdir() == true) { 
			System.out.println("Directory has been created successfully"); 
		} 
		else { 
			System.out.println("Directory cannot be created"); 
		} 

	}

}
