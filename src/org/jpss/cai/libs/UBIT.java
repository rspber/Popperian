package org.jpss.cai.libs;

import org.jpss.cai.util.MM;

// v:0.3 
// Turbo Pascal Source 
// Delphi Source 
// made by Joao Paulo Schwarz Schuler
// jpss@schulers.com
public class UBIT
{
//    type TArrOf2Bytes = array[0..1] of byte;
//    TArrOf3Bytes = array[0..2] of byte;
//    TArrOf4Bytes = array[0..3] of byte;
//    TArrOf2BytesPtr = ^TArrOf2Bytes;
//    TArrOf3BytesPtr = ^TArrOf3Bytes;
//    TArrOf4BytesPtr = ^TArrOf4Bytes;
//    TLongByteArray = array[0..1000000000] of byte;
//    TArrBytePtr = ^TLongByteArray;

	private static int POW2( final int i )
	{
		return 1 << i;
	}

//*  Eleva o numero a sua potencia.
//     exemplo : 3^4= pot(3,4) *
	public static double POT(final double numero, final double elevado)
	{
		if( numero == 0 ) {
			return 0;
		} else {
			return Math.exp(elevado * MM.ln(numero));
		}
	}

// w  <- 1.0 
	public static byte BARead( final byte[] a, final int P)
	{
		final int BytePos = P / 8;
		final int BitPos = P % 8;
		if( BytePos >= a.length ) {
			return 0;
		} else {
			return (byte)((a[BytePos] & POW2(BitPos)) >> BitPos);
		}
	}

// Bit Array Read 
// Flip no bit P 
	public static void BAFlip(final byte[] a, final int P)
	{
		int BytePos, BitPos;

		BytePos = P / 8;
		BitPos = P % 8;
		if( BytePos < a.length ) {
			a[BytePos] ^= POW2(BitPos);
		}
	}

// Bit Array Write 
	public static boolean BATest(final byte[] a, final int P)
	{
		return BARead(a, P) != 0;
	}

	private static byte[] intToByte4( int v )
	{
		final byte[] b = new byte[4];
		for( int i = 0; i < 4; ++i ) {
			b[i] = (byte)(v & 0xff);
			v = v >> 4;
		}
		return b;
	}

	private static int byte4ToInt( byte[] b )
	{
		int v = 0;
		for( int i = 0; i < 4; ++i ) {
			v = (v << 4) + b[i];
		}
		return v;
	}
	
//*  Eleva o numero a sua potencia.
//     exemplo : 3^4= pot(3,4) *
// Bit Array 
	public static boolean LongintBitTest(final int Data, final int P)
	{
		final byte[] a = intToByte4(Data);
		return BATest( a, P);
	}

// Flip no Bit P 
	public static int LongintBitFlip(final int Data, final int P)
	{
		final byte[] a = intToByte4(Data);
		BAFlip( a, P);
		return byte4ToInt(a);
	}

// Flip no bit P 
	public static void BAWrite(final /*var*/ byte[] a, final int P, final int Data)
	{
		final int BytePos = P / 8;
		final int BitPos = P % 8;
		if( Data == 0 ) {
			a[BytePos] &= ~POW2(BitPos);
		} else {
			a[BytePos] |= POW2(BitPos);
		}
	}

// r := x OR y 
// r := x XOR y 
	public static void BAXOr(final /*var*/ byte[] r, final byte[] x, final byte[] y)
	{
		for( int i = 0; i < r.length; i++ ) {
			r[i] = (byte)(x[i] ^ y[i]);
		}
	}

// r := x AND y 
// r := x OR y 
	public static void BAOr(final /*var*/ byte[] r, final byte[] x, final byte[] y)
	{
		for( int i = 0; i < r.length; i++ ) {
			r[i] = (byte)(x[i] | y[i]);
		}
	}

// NOT 
// r := x AND y 
	public static void BAAnd(final /*var*/ byte[] r, final byte[] x, final byte[] y)
	{
		for( int i = 0; i < r.length; i++ ) {
			r[i] = (byte)(x[i] & y[i]);
		}
	}

// transforma um real em BA 
// NOT 
	public static void BANot(final /*var*/ byte[] w)
	{
		for( int i = 0; i < w.length; i++ ) {
			w[i] = (byte)~w[i];
		}
	}

// Flip no Bit P 
// limpa o valor 
	public static void BAClear(final /*var*/ byte[] w)
	{
		for( int i = 0; i < w.length; i++ ) {
			w[i] = 0;
		}
	}

// limpa o valor 
// w  <- 1.0 
	public static void BAMake1(final byte[] w)
	{
//BAClear(w);
//w[High(w)]:=128;
		PFloatToBA(w, 1);
	}
/*
//operands
//esult
//arrier
//bit sum
	private static void BSum(final/ *var* / byte r, final byte x, final byte y, final byte z, final byte C)
	{
		byte s;	//soma

		s = (byte)(x + y + z);
		r = (byte)(s & 1);
		C = (byte)(s >> 1);
	}
*/
// x[POS]:=x[POS]+DADO 
	private static void BASumWordPos(final /*var*/ byte[] x, final int POS, final /*word*/int DADO)
	{
		final int n = x.length;
		int SOMA = x[POS] + DADO;
		x[POS] = (byte)(SOMA & 0xff);
		if( (SOMA >> 8 > 0) && (POS + 1 < n) ) {
			BASumWordPos(/*var*/x, POS + 1, SOMA >> 8);
		}
	}

// Bit Array Test 

// soma registradores: x=x+y 
	public static void BASum(final /*var*/ byte[] x, final byte[] y)
	{
		final int n = y.length;
		for( int i = 0; i < n; i++ ) {
			BASumWordPos(/*var*/x, i, y[i]);
		}
	}


// x[POS]:=x[POS]-DADO 
	private static void BASubBytePos(final byte[] x, final int POS, final byte DADO)
	{
		final int n = x.length;
		int SUB = x[POS];
		if( DADO > x[POS] ) {
			if( (POS + 1 < n) ) {
				BASubBytePos(x, POS + 1, (byte)1);
			}
			SUB = SUB + 256;
		}
		SUB = SUB - DADO;
		x[POS] = (byte)SUB;
	}

// soma registradores: x=x+y 

// soma registradores: x=x+y 
	public static void BASub(final /*var*/ byte[] x, final byte[] y)
	{
		final int n = y.length;
		for( int i = 0; i < n; i++ ) {
			BASubBytePos(x, i, y[i]);
		}
	}

// subtrai registradores x:=x - y 

// procedure BASum(var x,y:array of byte);
//{ soma registradores: x=x+y }
//var NumBits:Longint;
//    C,NX:byte;
//    i:Longint;
//begin
//C:=0;
//NumBits:=(High(y)+1)*8;
//for i:= 0 to NumBits-1
//    do begin
//       BSum(BARead(x,i) ,BARead(y,i),C,NX,C);
//       BAWrite(x,i,NX);
//       end;
//end; { of procedure } 
// Incrementa na Poscicao 
	public static void BAIncPos(final /*var*/ byte[] x, final int POS)
	{
		final int NumBits = x.length * 8;
		int i = POS;
		while( (i < NumBits) && (BARead(x, i) == 1) ) {
			BAWrite(x, i, 0);
			i = i + 1;
		}
		if( (i < NumBits) && (BARead(x, i) == 0) ) {
			BAWrite(x, i, 1);
		}
	}

// Decrementa na Poscicao 
// Incrementa 
	public static void BAInc(final byte[] x)
	{
//BAIncPos(x,0);
		BASumWordPos(x, 0, 1);
	}

// Incrementa na Poscicao 
// Decrementa na Poscicao 
	public static void BADecPos(final /*var*/ byte[] x, final int POS)
	{
		final int NumBits = x.length * 8;
		int i = POS;
		while( (i < NumBits) && (BARead(x, i) == 0) ) {
			BAWrite(x, i, 1);
			i = i + 1;
		}
		if( (i < NumBits) && (BARead(x, i) == 1) ) {
			BAWrite(x, i, 0);
		}
	}

// Incrementa 
// Decrementa 
	public static void BADec(final /*var*/ byte[] x)
	{
//BADecPos(x,0);
		BASubBytePos(x, 0, (byte)1);
	}

// r := x XOR y 
//procedure BASub(var x,y:array of byte);
//{ x:=x - y }
//var NumBits:Longint;
//    i:Longint;
//begin
//NumBits:=(High(x)+1)*8;
//for i:=0 to NumBits-1
//    do begin
//       if BATest(y,i)
//          then BADecPos(x,i);
//       end;
//end;
// x>y ?
	public static boolean BAGrater(final byte[] x, final byte[] y)
	{
		final int NumBits = x.length * 8;
		int i = NumBits;
		while( (i >= 0) && (BARead(x, i) == BARead(y, i)) ) {
			i = i - 1;
		}
		if( (i >= 0) ) {
			return (BARead(x, i) > BARead(y, i));
		}
		return false;
	}

// x>y ?
// x<y? 
	public static boolean BALower(final byte[] x, final byte[] y)
	{
		return BAGrater(y, x);
	}

// x<y? 
// x=y? 
	public static boolean BAEqual(final byte[] x, final byte[] y)
	{
		final int n = x.length;
		int i = 0;
		while( (i < n) && (x[i] == y[i]) ) {
			i = i + 1;
		}
		return (i >= n);
	}
/*
// return the Number of 1s; the first 1; the last 1 
	private static void BANumFirstLast(final byte[] x, final / *var* / int Num, final / *var* / int First, final / *var* / int Last)
	{
		final int NumBits = x.length * 8;
		First = NumBits - 1;
		Last = 0;
		Num = 0;
		for( int i = 0; i <= NumBits - 1; i++ ) {
			if( BATest(x, i) ) {
				Num = Num + 1;
				if( i < First ) {
					First = i;
				}
				if( i > Last ) {
					Last = i;
				}
			}
		}
	}
*/
// x=y? 
// NEW procedure BAPMul(var r,x,y:array of byte);
//{ r:=r + x*y }
//var NumBits:Longint;
//    i,ContY:Longint;
//    FirstX,LastX,FirstY,LastY,NumX,NumY:longint;
//begin
//BANumFirstLast(x,NumX,FirstX,LastX);
//BANumFirstLast(y,NumY,FirstY,LastY);
//NumBits:=(High(x)+1)*8;
//if (NumX>0) and (NumY>0)
//   then begin
//        for i:=LastX downto FirstX
//            do begin
//               ContY:=LastY;
//               while (i+ContY<NumBits) and
//                     BATest(x,i) and
//                     (ContY>=FirstY) do
//                     begin
//                     if BATest(y,ContY)
//                        then BAIncPos(r,i+ContY);
//                     ContY:=ContY-1;
//                     end;
//               end;
//        end;
//end; { of procedure } 
// OLD procedure BAPMul(var r,x,y:array of byte);
//{ r:=r + x*y }
//var NumBits:Longint;
//    i,ContY:Longint;
//begin
//NumBits:=(High(x)+1)*8;
//for i:=0 to NumBits-1
//    do begin
//       ContY:=0;
//       while (i+ContY<NumBits) do
//             begin
//             if (BARead(x,i) and BARead(y,ContY) > 0)
//                then BAIncPos(r,i+ContY);
//             ContY:=ContY+1;
//             end;
//       end;
//end;  o
//procedure BAPMul(var r,x,y:array of byte);
//{ r:=r + x*y }
//var NumBits:Longint;
//    i,ContY,ICX,ICY:Longint;
//begin
//NumBits:=(High(x)+1)*8;
//for i:=NumBits-1 downto 0
//    do begin
//       ContY:=NumBits-1;
//       ICX:=NumBits-i-1; { inverso de i }
//       ICY:=NumBits-ContY-1; { inverso de ContY }
//
//       while (ICX+ICY<NumBits) and
//             BATest(x,i) and
//             (ContY>=0)
//             do
//             begin
//             if BATest(y,ContY)
//                then BAIncPos(r,NumBits-(ICX+ICY)-1);
//             ContY:=ContY-1;
//             ICY:=NumBits-ContY-1; { inverso de ContY }
//             end;
//       end;
//end; 
// r:=r + x*y 
	public static void BAPMul(final /*var*/ byte[] r, final byte[] x, final byte[] y)
	{
		final int n = x.length;
		for( int i = n - 1; i >= 0; i-- ) {
			int ContY = n - 1;
			final int ICX = n - i - 1;	// inverso de i 
			int ICY = n - ContY - 1;	// inverso de ContY 
			while( (ICX + ICY < n) && (x[i] > 0) && (ContY >= 0) ) {
//BATest(x,i)
				final int Produto = x[i] * y[ContY];
				final int POS = n - (ICX + ICY) - 1;
				BASumWordPos(r, POS, Produto);
				ContY = ContY - 1;
				ICY = n - ContY - 1;	// inverso de ContY 
			}
		}
	}

// limpa o valor 
// Real Boolean Functions using Bit Arrays 
	public static void BARAnd(final /*var*/ byte[] r, final byte[] a, final byte[] b)
	{
		BAClear(r);
		BAPMul(r, a, b);
	}

	public static void BAROr(final byte[] r, final byte[] AUX, final byte[] a, final byte[] b)
	{
		BAClear(r);
		BAClear(AUX);
		BARAnd(AUX, a, b);	// R2=a*b 
		BASum(r, a);	// R1=a+b 
		BASum(r, b);
		BASub(r, AUX);	// R1 = a+b - a*b 
	}

	public static void BARNot(final /*var*/ byte[] r, final byte[] a)
	{
		BAClear(r);
		r[0] = 1;
		BASub(r, a);
	}

// r:=r + x*y 
// Real Boolean Functions 
	public static double RAnd(final double a, final double b)
	{
		return a * b;
	}

	public static double ROr(final double a, final double b)
	{
		return a + b - RAnd(a, b);
	}

	public static double RNot(final double a)
	{
		return 1 - a;
	}

	public static double RXor(final double a, final double b)
	{
		return ROr(RAnd(RNot(a), b), RAnd(a, RNot(b)));
	}

	public static double REqual(final double a, final double b)
	{
		return RNot(RXor(a, b));
	}

// transforma um registrador em string 
// calcula OR de todos os operandos 
	public static double ROrer(final /*var*/ double[] w)
	{
		double r = 0;	//elemento neutro do OR
		final int n = w.length;
		for( int i = 0; i <= n - 1; i++ ) {
			r = ROr(r, w[i]);
		}
		return r;
	}

// calcula OR de todos os operandos 

// calcula AND de todos os operandos 
	public static double RAnder(final double[] w)
	{
		double r = 1;	//elemento neutro do AND
		final int n = w.length;
		for( int i = 0; i <= n - 1; i++ ) {
			r = RAnd(r, w[i]);
		}
		return r;
	}


// SOMA: entrada: x,y,z; saida: Resultado e Carrier 
	static class RSum {

		double C = 0;
		public double next(final double x, final double y, final double z)
		{
			C = ROr(ROr(x * y, y * z), x * z);
			return ROrer(new double[]	// aux for OR 
			{
				(1 - x) * (1 - y) * z,
				(1 - x) * y * (1 - z),
				x * (1 - y) * (1 - z),
				x * y * z
			});
		}
	}

// SOMA: entrada: x,y,z; saida: Resultado e Carrier 
// soma registradores: x=x+y 
	public static void RegSum(final /*var*/ double[] x, final /*var*/ double[] y)
	{
		final RSum rsum = new RSum();
		final int n = y.length;
		for( int i = n - 1; i >= 0; i-- ) {
			x[i] = rsum.next(x[i], y[i], rsum.C);
		}
		if( x.length > y.length ) {
			x[n] = rsum.next(x[n], 0, rsum.C);
		}
	}

// soma registradores: x=x+y 

// devolve nivel de igualdade entre 2 registradores 
	public static double RegEqual(final double[] x, final double[] y)
	{
		double C = 1;
		final int n = y.length;
		for( int i = n - 1; i >= 0; i-- ) {
			C = RAnd(C, REqual(x[i], y[i]));
		}
		return C;
	}

// devolve nivel de igualdade entre 2 registradores 

// devolve nivel de igualdade entre 2 registradores usando relacao de ordem
	public static double RegOrdEqual(final /*var*/ double[] x, final /*var*/ double[] y)
	{
		double O1 = 0;
		double O2 = 0;
		final int n = y.length;
		for( int i = 0; i <= n - 1; i++ ) {
			O1 = O1 + x[i] * POW2(n - i - 1);
			O2 = O2 + y[i] * POW2(n - i - 1);
		}
		return 1 - (double)(Math.abs(O1 - O2) / (POW2(n) - 1));
	}

// Decrementa 

// transforma um BA em string 
	public static String BAToString(final byte[] w)
	{
		String r = "";	//elemento neutro da string
		final int NumBits = (w.length) * 8;
		for( int i = NumBits - 1; i >= 0; i-- ) {
			if( BARead(w, i) > 0 ) {
				r = r + '1';
			} else {
				r = r + '0';
			}
			if( (i == NumBits - 8) ) {
				r = r + '.';
			}
		}
		return r;
	}

// transforma um BA em string 

// transforma um BA em float 
	public static double BAToFloat(final byte[] w)
	{
		double Fator = 1;
		double r = 0;	//elemento neutro da string
		final int NumBits = (w.length) * 8;
		for( int i = NumBits - 1; i >= 0; i-- ) {
			r = r + Fator * BARead(w, i);
			Fator = Fator / 2;
		}
		return r;
	}

// transforma um BA em float 

// transforma um real em BA 
	public static void PFloatToBA(final /*var*/ byte[] w, double Valor)
	{
		double Fator = 128;
		final int NumBits = (w.length) * 8;
		BAClear(w);
		for( int i = NumBits - 1; i >= 0; i-- ) {
			if( Valor >= Fator ) {
				Valor = Valor - Fator;
				BAWrite(w, i, 1);
			}
			Fator = Fator / 2;
		}
	}

// devolve nivel de igualdade entre 2 registradores usando relacao de ordem

// transforma um registrador em string 
	public static String RegToString(final /*var*/ double[] w)
	{
		String r = "";	//elemento neutro da string
		final int n = w.length;
		for( int i = 0; i <= n - 1; i++ ) {
			if( w[i] > 0.55 ) {
				r = r + '1';
			} else {
				if( w[i] < 0.45 ) {
					r = r + '0';
				} else {
					r = r + 'x';
				}
			}
		}
		return r;
	}

// calcula AND de todos os operandos 

// Not Controlado 
	public static double RCNot(final double x, final double[] w)
	{
		return RXor(x, RAnder(w));
	}

// Not Controlado 
// termo maximo do Or 
	public static double ROrMaxTerm(final double[] w, final int NumMaxTerm)
	{
		double r = 0;	//elemento neutro do OR
		final int n = w.length;
		for( int i = 0; i < n; i++ ) {
			if( LongintBitTest(NumMaxTerm, i) ) {
				r = ROr(r, w[i]);
			} else {
				r = ROr(r, RNot(w[i]));
			}
		}
		return r;
	}

// termo maximo do Or 

// termo maximo do Or 
// string do termo maximo do or 
	public static String ROrMaxTermStr(final int n, final int NumMaxTerm)
	{
		String r = "";	//elemento neutro da + em String
		for( int i = 0; i < n; i++ ) {
			if( i > 0 ) {
				r = r + ' ';
			}
			if( LongintBitTest(NumMaxTerm, i) ) {
				r = r + ' ' + (char)('a' + i);
			} else {
				r = r + '-' + (char)('a' + i);
			}
		}
		return r;
	}

// termo maximo do Or Str 
// string do termo maximo do or 

// funcao Sat 
	public static double RSatFunc(final /*var*/ double[] w, final int NumFunc)
	{
		double r = 1;	//elemento neutro do AND
		int n = w.length;
		int MaxMaxTerm = (int)Math.round(POT(2, n));
// Neste caso, o algoritmo eh exponencial porque
//esta montando a funcao de indice NumFunc.
//  a complexidade dessa funcao nao deve ser computada
//juntamente com a complexidade do algoritmo de otimizacao.
		for( int i = MaxMaxTerm - 1; i >= 0; i-- ) {
			if( LongintBitTest(NumFunc, i) ) {
				r = RAnd(r, ROrMaxTerm(w, i));
			}
		}
		return r;
	}

// funcao Sat 

// funcao Sat 
	public static String RSatFuncStr(final int n, final int NumFunc)
	{
		String r = "";	//elemento neutro da + em String
		final int MaxMaxTerm = (int)Math.round(POT(2, n));
		for( int i = MaxMaxTerm - 1; i >= 0; i-- ) {
			if( LongintBitTest(NumFunc, i) ) {
				r = r + '(' + ROrMaxTermStr(n, i) + ')';
			}
		}
		return r;
	}

// funcao Sat Str

// regenera o valor 
	public static void RRegen(final /*var*/ double[] w)
	{
		for( int i = 0; i < w.length; i++ ) {
			if( w[i] > 0.5 ) {
				w[i] = 1;
			} else {
				w[i] = 0;
			}
		}
	}

// regenera o valor 
// degenera o valor 
	public static void RDegen(final /*var*/ double[] w)
	{
		for( int i = 0; i < w.length; i++ ) {
			if( w[i] > 0.5 ) {
				w[i] = 0.8;
			} else {
				w[i] = 0.2;
			}
		}
	}

// degenera o valor 
// degenera o valor usando parametro 
	public static void RDegenP(final /*var*/ double[] w, final double P)
	{
		for( int i = 0; i < w.length; i++ ) {
			if( w[i] > 0.5 ) {
				w[i] = P;
			} else {
				w[i] = 1 - P;
			}
		}
	}

// degenera o valor usando parametro 
// limpa o valor 
	public static void Clear(final /*var*/ double[] w)
	{
		for( int i = 0; i < w.length; i++ ) {
			w[i] = 0;
		}
	}

}
