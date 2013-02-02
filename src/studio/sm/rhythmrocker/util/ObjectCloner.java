package studio.sm.rhythmrocker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCloner
{
	public static Object clone(Object oldObj)
	{  
		Object obj = null;  
	    try 
	    {  
			// Write the object out to a byte array  
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
			ObjectOutputStream out = new ObjectOutputStream(bos);  
			out.writeObject(oldObj);  
			out.flush();  
			out.close();  

	        // Retrieve an input stream from the byte array and read  
	        // a copy of the object back in.  
	        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());   
	        ObjectInputStream in = new ObjectInputStream(bis);  
	        obj = in.readObject();  

	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } catch (ClassNotFoundException cnfe) {  
	        cnfe.printStackTrace();  
	    }  

	    return obj;  
	}  
}
