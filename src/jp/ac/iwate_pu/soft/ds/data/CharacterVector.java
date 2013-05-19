package jp.ac.iwate_pu.soft.ds.data;

import java.lang.IllegalArgumentException;
import java.lang.NegativeArraySizeException;

public class CharacterVector {

	private byte[] bytes;
	private int length;
	private int sL;

	public CharacterVector() {
		this(32);
	}

	public CharacterVector(int num) {
		if(num < 0) {
			throw new NegativeArraySizeException();
		}
		sL = num;
		length = (int) Math.pow(2, num);
		bytes = new byte[(int)Math.ceil(length/8)];
	}

	public void flip(int index) {
		if(index > length) {
			throw new IllegalArgumentException();
		}
		bytes[(int) Math.ceil((index-1)/8)] ^= translateFromComplementValue((int)Math.pow(2, ((index-1)%8)));
	}

	public void addCharacter(byte[] argBytes) {
		int sum = 0;
		for(int i = 0; i < sL; i++) {
			if(argBytes[(int) Math.ceil(i/8)] == 0) {
				i = i + (8 - (i%8));
				break;
			}
			if(argBytes[(int) Math.ceil(i/8)]%2 != 0) {
				sum += Math.pow(2,i);
			}
			argBytes[(int) Math.ceil(i/8)] = (byte) (argBytes[(int) Math.ceil(i/8)] >>> 1); 
		}
		flip(sum);
	}

	public void set(long value) {
		if(((Math.pow(2, length)) - 1) < value) {
			throw new IllegalArgumentException();
		}
		
		for(int i = 0; value != 0; i++) {
			if((value%2) == 1) {
				flip(i);
			}
			value = value >>> 1;
		}
	}

	public void set(int value) {
		set((long)value);
	}

	public void set(byte value) {
		set((long)value);
	}

	public void set(byte[] value) {
		long temp = 0;
		for(int i = 0; i < value.length; i++) {
			temp = temp + value[i];
		}
		set(temp);
	}

	public void set(byte[] value, int indexFromLSB) {

	}

	public int getLength() {
		return length;
	}

	public String getBinaryValue(int argl) {
		byte[] temp = bytes.clone();

		if(length < argl){
			throw new IllegalArgumentException();
		}
		
		StringBuilder sb  = new StringBuilder();
		for(int i = 0; i < length; i++) {
			if(temp[(int)Math.ceil(i/8)]%2 != 0) {
				sb.insert(0, "1");
			}else{
				sb.insert(0, "0");
			}
			temp[(int) Math.ceil(i/8)] = (byte) (temp[(int) Math.ceil(i/8)] >>> 1); 
		}
		
		return new String(sb);
	}
	
	public String getBinaryValue() {
		return getBinaryValue(length);
	}

	public String getValue() {
		return null;
	}

	public static int translateFromComplementValue(int arg) {	
		if(arg < 128) {
			return arg;
		}else{
			return -128 + (arg - 128);
		}
	}
	
	public static int translateToComplementValue(int arg) {
		if(arg >= 0) {
			return arg;
		}else{
			return 127 + (arg * -1);
		}
	}
	
	public int discrepancy(CharacterVector targetCV) {
		int returnValue = 0;
		
		byte temp = 0;
		for(int i = 0; (i*8) < length; i++) {
			temp = (byte) (bytes[i] ^ targetCV.bytes[i]);
			for(int j = 0; j < 8; j++) {
				if(temp%2 == 1) {
					returnValue++;
				}
				temp = (byte) (temp >>> 1);
			}
		}
		
		return returnValue;
	}

	public static void main(String[] args){
		CharacterVector a = new CharacterVector(32);
		CharacterVector b = new CharacterVector(16);
		//a.set(2);
		//b.set(6);
		byte[] ba = new byte[2];
		ba[0] = 0;
		ba[1] = 0;
		a.addCharacter(ba);
		System.out.println(a.getBinaryValue());
	}

}
