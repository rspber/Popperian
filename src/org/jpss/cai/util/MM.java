package org.jpss.cai.util;

import java.util.Random;

public class MM {

	public static final Random random = new Random();

	public static int random(final int n)
	{
		return random.nextInt(n);
	}

	public static double random(final double n)
	{
		return random.nextDouble() * n;
	}

	public static double sqr(final double d)
	{
		return d * d;
	}

	public static double ln (final double a)
	{
		return Math.log(a);
	}
}
