/*
 * Copyright (c) 2010 Fengtan<https://github.com/Fengtan/>
 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:

 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class Files {
	
	/*
	 * READ FILE AND RETURN LIST OF INTEGERS FROM IT
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File too large");
        }
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        is.close();
        return bytes;
    }
	
	/*
	 * READ THE KEY CONTAINED IN A FILE
	 */
	public static BigInteger getKeyFromFile(String fileName) throws IOException {
		Log.printVar("--> Read file "+fileName+"...");
		File file = new File(fileName);
		byte[] bytes = getBytesFromFile(file);
		BigInteger key = new BigInteger(bytes);
		Log.printVar("--> Key = "+key);
		return key;
	}
	
	/*
	 * WRITE A KEY INTO A FILE
	 */
	public static void writeKeyToFile(BigInteger key, String fileName) throws FileNotFoundException, IOException {
		File file = new File(fileName);
		OutputStream os = new FileOutputStream(file);
		os.write(key.toByteArray());
		Log.printVar("--> Wrote key "+key+" into file "+fileName);
	}
	
	
}
