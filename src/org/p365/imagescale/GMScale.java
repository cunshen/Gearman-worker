package org.p365.imagescale;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.im4java.core.IMOperation;
import org.im4java.core.ConvertCmd;
import org.im4java.process.Pipe;


public class GMScale {
	

	public static InputStream ScaleImage(InputStream in, int width, int height) {
		IMOperation op = new IMOperation();
		ConvertCmd convert = new ConvertCmd(true);
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		
	    op.addImage("-");                   // read from stdin
	    if(width == 0){
	    	op.thumbnail(null, height);
	    }
	    else if(height == 0){
	    	op.thumbnail(width, null);
	    }
	    else {
	    	op.thumbnail(width, height);
	    }
	    op.addImage("jpg:-");               // write to stdout in tif-format

	    Pipe pipeIn  = new Pipe(in,null);
	    Pipe pipeOut = new Pipe(null,b);

	    // set up command
	    convert.setInputProvider(pipeIn);
	    convert.setOutputConsumer(pipeOut);
	    try{
	    convert.run(op);
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    byte[] bcopy = b.toByteArray();
		ByteArrayInputStream stream = new ByteArrayInputStream(bcopy);
		
		return stream;
	}
	

}
