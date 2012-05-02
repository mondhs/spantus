package scikit.util;

import java.io.Serializable;


public class Pair<A,B> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5145849579516793470L;
	private final A first;
	private final B second;
	
	public static <A,B> Pair<A,B> newPair(A first, B second) {
		return new Pair<A,B>(first, second);
	}

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public A fst() { return first; }
	public B snd() { return second; }

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

//	private static boolean equals(Object x, Object y) {
//		return (x == null && y == null) || (x != null && x.equals(y));
//	}

	@Override
	public boolean equals(Object other) {
		return
			other instanceof Pair<?,?> &&
			first.equals(((Pair<?,?>)other).first) &&
//			equals(first, ((Pair<?,?>)other).first) &&
			second.equals(((Pair<?,?>)other).second 
//			equals(second, ((Pair<?,?>)other).second
					);
	}
	@Override
	public int hashCode() {
		if (first == null) return (second == null) ? 0 : second.hashCode() + 1;
		else if (second == null) return first.hashCode() + 2;
		else return first.hashCode() * 17 + second.hashCode();
	}
}
