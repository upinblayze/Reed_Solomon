import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class Main2 {

	private static final int K = 3;
	private static final int N = 10;
	private static final int SIZE = 40;
	private static final BigInteger P = new BigInteger("1099511627791");
	private static final BigInteger KEY = new BigInteger("10000000000");
	public static void main(String[] args) {
		String secret1 = "1010101010101010101010101010101010101010";
		String secret2 = "1100110011001100110011001100110011001100";
		String secret3 = "1111000011110000111100001111000011110000";
		
		BigInteger cSecret1 = decode(secret1).add(KEY).mod(P);
		BigInteger cSecret2 = decode(secret2).add(KEY).mod(P);
		BigInteger cSecret3 = decode(secret3).add(KEY).mod(P);
		
		
		
		List<BigInteger> list = new ArrayList<BigInteger>();
		list.add(new BigInteger("543643634"));
		list.add(new BigInteger("765868"));
		list.add(new BigInteger("21452355"));
		list.add(new BigInteger("785865865"));
		list.add(new BigInteger("532532546"));
		list.add(new BigInteger("5435444"));
		list.add(new BigInteger("6436735753"));
		list.add(new BigInteger("3224324324"));
		list.add(new BigInteger("365475745745"));
		list.add(new BigInteger("326342745"));
		
		List<BigInteger> sec = new ArrayList<BigInteger>();
		sec.add(cSecret1);
		sec.add(cSecret2);
		sec.add(cSecret3);
		System.out.println("cipher secret number: " + sec);
		
		
		List<List<BigInteger>> matrix = getMatrix(list, sec);
		
		System.out.println(matrix);
		
		List<BigInteger> answerInt = getInteger(matrix);
		System.out.println("get cipher number: " + answerInt);
		
		for (int i = 0; i < K; i ++)
			answerInt.set(i, answerInt.get(i).subtract(KEY).mod(P));
		System.out.println("bit massage: ");
		for (int i = 0; i < K; i ++)
			System.out.println(encode(answerInt.get(i)));
		
		
	}
	
	private static List<List<BigInteger>> getMatrix(List<BigInteger> aList, List<BigInteger> secret) {
		List<List<BigInteger>> ret = new ArrayList<List<BigInteger>>();
		
		for (int i = 0; i < N; i++) {
			List<BigInteger> temp = new ArrayList<BigInteger>();
			BigInteger sum = BigInteger.ZERO;
			for (int j = 0; j < K; j++) {
				BigInteger power = aList.get(i).modPow(BigInteger.valueOf(j), P);
				sum = sum.add(power.multiply(secret.get(j))).mod(P);
				temp.add(power);
			}
			temp.add(sum.mod(P));
			ret.add(temp);
		}
		
		return ret;
	}
	
	private static List<BigInteger> getInteger(List<List<BigInteger>> matrix) {
		if (matrix.size() < K)
			throw new IllegalArgumentException("not enough equations received");
			
		List<BigInteger> ret = new ArrayList<BigInteger>();
		for (int i = 0; i < K; i++)
			ret.add(null);
		
		for (int i = 0; i < K - 1; i++) {
			BigInteger product = matrix.get(i).get(i);
			for (int j = i + 1; j < K; j++) {
				product = product.multiply(matrix.get(j).get(i));
			}
			
			for (int j = i; j < K; j++) {
				BigInteger temp = product.divide(matrix.get(j).get(i));
				for (int k = i; k <= K; k++) {
					matrix.get(j).set(k, matrix.get(j).get(k).multiply(temp).mod(P));
				}
			}
			for (int j = i + 1; j < K; j++) {
				for (int k = i; k <= K; k++) {
					matrix.get(j).set(k, matrix.get(i).get(k).subtract(matrix.get(j).get(k)).mod(P));
				}
			}
			
			
		}
		
		for (int i = K - 1; i >= 0; i--) {
			BigInteger tempSum = matrix.get(i).get(K);
			for (int j = K - 1; j > i; j--) {
				tempSum = tempSum.subtract(matrix.get(i).get(j).multiply(ret.get(j)));
			}
			ret.set(i, BigIntegerMath.solveLinearCongruence(matrix.get(i).get(i), tempSum, P)[1].mod(P));
		}
	
		return ret;
	}
	
	private static String encode(BigInteger aInt) {
		StringBuilder ret = new StringBuilder();
		while (!aInt.equals(BigInteger.ZERO)) {
			BigInteger[] temp = aInt.divideAndRemainder(BigInteger.valueOf(2));
			if (temp[1].equals(BigInteger.ONE))
				ret.insert(0, '1');
			else
				ret.insert(0, '0');
			aInt = temp[0];
		}
		
		int temp = SIZE - ret.length();
		for (int i = 0; i < temp; i++)
			ret.insert(0, '0');
		return ret.toString();
	}
	
	private static BigInteger decode(String aCode) {
		BigInteger ret =BigInteger.ZERO;
		for (int i = 1; i <= aCode.length(); i++) {
			if (aCode.charAt(aCode.length() - i) == '1')
				ret = ret.add(BigInteger.valueOf(2).pow(i - 1));
		}
		return ret;
	}

}
