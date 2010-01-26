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

import java.math.BigDecimal;
import java.math.BigInteger;

public class Fraction {

	public BigInteger numerator;
	public BigInteger denominator;
	
	public Fraction(BigInteger n, BigInteger d) {
		BigInteger gcd = gcd(n, d);
		numerator = n.divide(gcd); 
		denominator = d.divide(gcd);
	}
	
	public Fraction(BigInteger n, Fraction d) {
		numerator = n.multiply(d.denominator);
		denominator = d.numerator;
		BigInteger gcd = gcd(numerator, denominator);
		numerator = numerator.divide(gcd);
		denominator = denominator.divide(gcd);
	}

	public Fraction(BigInteger n) {
		numerator = n;
		denominator = BigInteger.ONE;
	}

	public Fraction() {
		numerator = BigInteger.ZERO;
		denominator = BigInteger.ONE;
	}

	public String toString() {
		return (numerator + "/" + denominator);
	}

	public BigDecimal evaluate() {
		BigDecimal n = new BigDecimal(numerator);
		BigDecimal d = new BigDecimal(denominator);	
		return n.divide(d);
    }
	
	public BigInteger floor() {
		BigDecimal n = new BigDecimal(numerator);
		BigDecimal d = new BigDecimal(denominator);	
		return n.divide(d, BigDecimal.ROUND_FLOOR).toBigInteger();		
	}
	
	public Fraction invert() {
		return new Fraction(denominator, numerator);
	}

	public Fraction add (Fraction f2) {
		Fraction r = new Fraction(
			(numerator.multiply(f2.denominator)).add(
				f2.numerator.multiply(denominator)
			),
			denominator.multiply(f2.denominator)
		);
		BigInteger gcd = gcd(r.numerator, r.denominator);
		r.numerator = r.numerator.divide(gcd);
		r.denominator = r.denominator.divide(gcd);
		return r;
	}
	
	public Fraction substract(Fraction f2) {
		Fraction r = new Fraction(
			(numerator.multiply(f2.denominator)).subtract(
				f2.numerator.multiply(denominator)
			),
			denominator.multiply(f2.denominator)
		);
		BigInteger gcd = gcd(r.numerator, r.denominator);
		r.numerator = r.numerator.divide(gcd);
		r.denominator = r.denominator.divide(gcd);
		return r;
	}
	
    public static BigInteger gcd(BigInteger m, BigInteger n) {
    	return m.gcd(n);
    }

}

