// *****************************************************************************
//                              UVect Library
//                  by Joao Paulo Schwarz Schuler 1999-2005
//                              version 0.1.3
//                    Web: http://www.schulers.com/jpss
//                       E-mail: jpss@schulers.com
//
// The contents of this file are subject to the Mozilla Public License Version
// 1.0; you may not use this file except in compliance with the License.
// You may obtain a copy of the License at http://www.mozilla.org/MPL/
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
// for the specific language governing rights and limitations under the License.
// ***************************************************************************** 
package org.jpss.cai.libs;

import java.util.ArrayList;
import java.util.List;

import org.jpss.cai.util.MM;

/*
	uses SysUtils, Grids
*/

class T2DArrayOfExtended {
	
	final List<double[]> list = new ArrayList<>();
	
	double[] get(final int i)
	{
		return list.get(i);
	}

	double get(final int i, final int j)
	{
		return list.get(i)[j];
	}

	int size()
	{
		return list.size();
	}

	void add(final double[] li)
	{
		list.add(li);
	}
	
}

// Unidade de vetores v 0.1.3
// por Joao Paulo Schwarz Schuler 
public class UVect
{
// pivot GAUSS
	public static void Pivot(final T2DArrayOfExtended x, final int p1, final int p2)
	{
		ProdutoPorEscalar(x.get(p1), 1.0 / x.get(p1, p2) );	// normaliza
		for( int i = 0; i < x.size(); i++ ) {
			if( p1 != i ) {
				double multiplicador = - x.get(i, p2);
				if( multiplicador != 0 ) {
					SomaVetorialYMultiplicado(x.get(i), x.get(p1), multiplicador);
				}
			}
		}
	}

// limpa 2DArray
	public static void Clean2DArray(final T2DArrayOfExtended x)
	{
		for( final double[] li : x.list ) {
			for( int j = 0; j < li.length; j++ ) {
				li[j] = 0;
			}
		}
	}

	public static boolean CheckModule(final T2DArrayOfExtended x, final double Module)
	{
		for( final double[] li : x.list ) {
			for( final double y : li ) {
				if( y != 0 && Math.abs(y) < Module ) {
					return true;
				}
			}
		}
		return false;
	}

// calcula DP 
	public static double[] CarregaVetor(final /*var*/ double[] x, final double[] y)
	{
		int j = 0;
		for( int i = 0; i < x.length; i++ ) {
			x[i] = y[j];
			j++;
		}
		return x;
	}
/*
	public static void Load2DArrOnStringGrid(final / *var* / TStringGrid x, final / *var* / T2DArrayOfExtended y)
	{
		x.RowCount = y.size();
		x.ColCount = y.get(0).length;

		for( int i = 0; i < y.size(); i++ ) {
			final double[] li = x.get(i);
			for( int j = 0; j < li.length; j++ ) {
				x.Cells[j][i] = Double.toString(li[j]);
			}
		}
//  x.FixedCols:=0;
//  x.FixedRows:=0;
	}
*/
// Load2DArrOnStringGrid
	public static void AddRowTo2DArr(final T2DArrayOfExtended x, final double[] y)
	{
		x.add(CarregaVetor(new double[y.length], y));
	}

// x : destino 
	public static void SomaVetorial(final /*var*/ double[] x, final double[] y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] += y[i];
		}
	}

// x : destino 
//x:=x + y*mul
	public static void SomaVetorialYMultiplicado(final /*var*/ double[] x, final double[] y, final double mul)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] += y[i] * mul;
		}
	}

//x:=x + y*mul
// x : destino 
	public static void SubtracaoVetorial(final /*var*/ double[] x, final double[] y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] -= y[i];
		}
	}

// x : destino 
	public static void MultiplicacaoVetorial(final /*var*/ double[] x, final double[] y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] *= y[i];
		}
	}

// x : destino 
	public static double ProdutoEscalar(final double[] x, final double[] y)
	{
		double r = 0;

		for( int i = 0; i < x.length; i++ ) {
			r += x[i] * y[i];
		}
		return r;
	}

	public static double Distancia(final /*var*/ double[] x, final double[] y)
	{
		double r = 0;

		for( int i = 0; i < x.length; i++ ) {
			r += MM.sqr(x[i] - y[i]);
		}
		return Math.sqrt(r);
	}

	public static void ProdutoPorEscalar(final /*var*/ double[] x, final double y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] *= y;
		}
	}

	public static void DividePorEscalar(final /*var*/ double[] x, final double y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] /= y;
		}
	}

// seta todos elementos do vetor 
	public static void DefineVetor(final /*var*/ double[] x, final double y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] = y;
		}
	}

	public static void DefineVetorBol(final /*var*/ boolean[] x, final boolean y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] = y;
		}
	}

	public static void DefineVetorByte(final /*var*/ byte[] x, final byte y)
	{
		DefineFaixaVetorByte(x, y);
	}

	public static void DefineFaixaVetorByte(final /*var*/ byte[] x, final byte y)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] = y;
		}
	}

// seta todos elementos do vetor 
	public static void MakeRandomVector(final /*var*/ double[] x)
	{
		for( int i = 0; i < x.length; i++ ) {
			x[i] = 0.5 - MM.random(60000) / 60000.0;
		}
	}

// seta todos elementos do vetor 
	public static String MostraVetor(final double[] x)
	{
		StringBuilder ss = new StringBuilder(100);
		for( int i = 0; i < x.length; i++ ) {
			ss.append(String.format("%10.5f", x[i]));
		}
		return ss.toString();
	}

// seta todos elementos do vetor 
// calcula media 
	public static double Media(final double[] x)
	{
		double m = 0;
		for( int i = 0; i < x.length; i++ ) {
			m += x[i];
		}
		return m / x.length;
	}

// calcula media 
// calcula variancia 
	public static double Variancia(final double[] x)
	{
		double m = Media(x);
		double v = 0;
		for( int i = 0; i < x.length; i++ ) {
			v += MM.sqr(m - x[i]);
		}
		return v / x.length;
	}

// calcula variancia 
// calcula DP 
	public static double DesvioPadrao(final double[] x)
	{
		return Math.sqrt(Variancia(x));
	}

	public static int PosicaoDoMaior(final double[] x)
	{
		int pos = 0;
		double Maior = x[pos];
		if( x.length > 1 ) {
			for( int i = 1; i < x.length; i++ ) {
				if( x[i] > Maior ) {
					Maior = x[i];
					pos = i;
				}
			}
		}
		return pos;
	}

	public static double Maior(final double[] x)
	{
		return x[PosicaoDoMaior(x)];
	}

}
