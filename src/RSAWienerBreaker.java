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
import java.util.ArrayList;
import java.util.List;

public class RSAWienerBreaker extends RSABreaker
{

	List<BigInteger>	q = new ArrayList<BigInteger>();
	List<Fraction>		r = new ArrayList<Fraction>();
	List<BigInteger>	n = new ArrayList<BigInteger>();
	List<BigInteger>	d = new ArrayList<BigInteger>();
	
	/*
	 * CONSTRUCTOR
	 */
	public RSAWienerBreaker(BigInteger e, BigInteger N) {
		super(e, N);
	}
	
	/*
	 * PERFORMS WIENER ATTACK
	 */
	@Override
	public BigInteger attack() {
		BigInteger d;
		int i = 0;
		while((d = step(i)) == null) {
			i++;
		}
		return d;
	}
	
	/*
	 * EACH STEP OF THE WIENER ALGORITHM
	 */
	public BigInteger step(int i) {
		Log.printVar("**** i="+i+" ****");
		//qi
		BigInteger qi;
		Fraction ri;
		if(i == 0) {
			Fraction f = new Fraction(e, N);
			qi = f.floor();
			ri = f.substract(new Fraction(qi));
		} else {
			Fraction f = new Fraction(BigInteger.ONE, r.get(i-1));
			qi = f.floor();
			ri = f.substract(new Fraction(qi));
		}
		q.add(qi);
		Log.printVar("q="+qi);
		r.add(ri);
		Log.printVar("r="+ri);
		//ni/di = <q0,q1..,qi>
		BigInteger ni, di;
		switch(i) {
		case 0:
			ni = q.get(0);
			di = BigInteger.ONE;
			break;
		case 1:
			ni = q.get(0).multiply(q.get(1)).add(BigInteger.ONE);
			di = q.get(1);
			break;
		default:
			ni = q.get(i).multiply(n.get(i-1)).add(n.get(i-2));
			di = q.get(i).multiply(d.get(i-1)).add(d.get(i-2));
		}
		n.add(ni);
		Log.printVar("n'="+ni);
		d.add(di);
		Log.printVar("d'="+di);
		//double nidi = ni/di;
		//guess of k/dg (depends on parity of i)
		Fraction kdg;
		if((i%2) == 0) {
			incLastItem(q, BigInteger.ONE);
			kdg = continuedFractionValue(q);
			incLastItem(q, BigInteger.ONE.negate());
		} else {
			kdg = continuedFractionValue(q);
		}
		Log.printVar("k/dg="+kdg);
		//guess of edg
		BigInteger edg = e.multiply(kdg.denominator);
		Log.printVar("guess of edg="+edg);
		//guess of (p-1)(q-1)
		BigInteger p1q1 = new Fraction(e, kdg).floor();
		Log.printVar("guess of (p-1)(q-1)="+p1q1);
		//guess of g
		BigInteger g = edg.mod(kdg.numerator);
		Log.printVar("guess of g="+g);
		//guess of (p+q)/2
		BigDecimal pq2 = new BigDecimal(N.subtract(p1q1)).add(BigDecimal.ONE).divide(new BigDecimal("2"));
		Log.printVar("guess of (p+q)/2="+pq2);
		if(!pq2.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO)) {
			return null;
		}
		//guess of ((p-q)/2)^2
		//now we can consider that pq2 is an integer
		BigInteger pq22 = pq2.toBigInteger().pow(2).subtract(N);
		Log.printVar("guess of ((p-q)/2)^2="+pq22);
		BigInteger sqrt = sqrt(pq22);
		if(!sqrt.pow(2).equals(pq22)) {
			return null;
		}
		//d
		BigInteger d = edg.divide(e.multiply(g));
		Log.printVar("--> d= "+d);
		//check if d works ass a private key
		//i.e. if gcd( e*d, (p-1)(q-1) )=1 
		if(e.multiply(d).gcd(p1q1).equals(BigInteger.ONE)) {
			Log.printVar("--> private key works");
		} else {
			Log.printVar("--> private key does not work");
		}
		return d;
	}
	
	/*
	 * INCREMENTS LAST ITEM IN A LIST
	 */
	public static List<BigInteger> incLastItem(List<BigInteger> list, BigInteger inc) {
		BigInteger last = list.remove(list.size()-1);
		list.add(last.add(inc));
		return list;
	}
	
	/*
	 * OUTPUTS VALUE OF FRACTION FROM LIST OF QUOTIENTS
	 * I.E. CALCULATES <q0,q1,...,qi>
	 */
	public static Fraction continuedFractionValue(List<BigInteger>quotiens) {
		return continuedFractionValue(quotiens, 0);
	}
	/*
	 * CORRESPONDING ITERATIVE METHOD
	 */
	public static Fraction continuedFractionValue(List<BigInteger>quotiens, int position) {
		if(position == quotiens.size()) {
			return null;
		}
		Fraction next = continuedFractionValue(quotiens, position+1);
		if(next == null) {
			return new Fraction(quotiens.get(position));
		} else {
			return new Fraction(quotiens.get(position)).add(next.invert());
		}
	}
	
	/*
	 * SQRT OF BIGINTEGER
	 */
	public static BigInteger sqrt (BigInteger x) {
		BigInteger r = BigInteger.valueOf(0);
		BigInteger m = r.setBit(2*x.bitLength());
		BigInteger nr;
		do {
			nr = r.add(m);
			if (nr.compareTo(x) != 1) {
				x = x.subtract(nr);
				r = nr.add(m);
			}
			r = r.shiftRight(1);
			m = m.shiftRight(2);
		} while (m.bitCount() != 0);
		return r;
	}

}
