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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Hashtable;

public abstract class RSABreaker {
	
	BigInteger e;
	BigInteger N;

	/*
	 * CONSTRUCTOR
	 */
	public RSABreaker(BigInteger e, BigInteger N) {
		this.e = e;
		this.N = N;
	}
	
	/*
	 * PERFORMS ATTACK
	 */
	public abstract BigInteger attack();
	
	/*
	 * MAIN
	 */
	public static void main(String[] args) throws IOException {
		Hashtable<String,String> argslist = parseArgs(args);
		if(!argslist.containsKey("e")
		|| !argslist.containsKey("n")) {
			printUsage();
			return;
		}
		if(argslist.containsKey("v")) {
			Log.LOG = true;
		}
		BigInteger e = parseBigInteger(argslist.get("e"));
		BigInteger n = parseBigInteger(argslist.get("n"));
		
		RSABreaker breaker = new RSAWienerBreaker(e, n);
		BigInteger d = breaker.attack();
		System.out.println(d);
		
		if(argslist.containsKey("out")) {
			Files.writeKeyToFile(d, argslist.get("out"));
		}
	}
	
	/*
	 * CLI
	 */
	public static Hashtable<String,String> parseArgs(String[] args) {
		Hashtable<String,String> hashtable = new Hashtable<String,String>();
		for(String arg: args) {
			if(arg.matches("^-([a-zA-Z]*)(=)?(.*)$")) {
				arg = arg.substring(1);
				String[] keyval = arg.split("=", 2);
				hashtable.put(keyval[0], (keyval.length == 1) ? "" : keyval[1]);
			}
		}
		return hashtable;
	}
	
	/*
	 * PARSE BIGINTEGER FROM STRING (NUMBER/FILE)
	 */
	public static BigInteger parseBigInteger(String input) throws IOException{
		BigInteger result;
		try {
			result = new BigInteger(input);
		} catch(NumberFormatException ex) {
			result = Files.getKeyFromFile(input);
		}
		return result;
	}
	
	/*
	 * USAGE
	 */
	public static void printUsage() {
		System.out.println("Usage: RSABreaker -e=<e number|file> -n=<n number|file> [-out=<file>] [-v]");
		System.out.println("");
		System.out.println("Where arguments are:");
		System.out.println("\t-e\tRSA 'e' value, either a number or a file");
		System.out.println("\t-n\tRSA 'N' value, either a number or a file");
		System.out.println("\t-out\tpath of output file for RSA 'd' value");
		System.out.println("\t-v\tswitch on verbose mode");
		System.out.println("");
		System.out.println("Example: RSABreaker -e=2621 -n=8927");
	}
	
}
