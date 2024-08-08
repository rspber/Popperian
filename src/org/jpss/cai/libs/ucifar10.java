package org.jpss.cai.libs;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/*
	uses ExtCtrls, Graphics
*/

//
//  CIFAR-10 Free Pascal/Lazarus Library by Joao Paulo Schwarz Schuler
//  Conscious Artificial Intelligence Project
//  https://sourceforge.net/projects/cai/
//
//// This is an example showing how to use pascal unit ucifar10:
//procedure TForm1.Button1Click(Sender: TObject);
//var
//  Img: TTinyImage;
//  cifarFile: TTInyImageFile;
//begin
//  AssignFile(cifarFile, 'C:\cifar-10\data_batch_1.bin');
//  Reset(cifarFile);
//
//  while not EOF(cifarFile) do
//  begin
//    Read(cifarFile, Img);
//    // Image1 is a TImage component.
//    LoadTinyImageIntoTImage(Img, Image1);
//    Label1.Caption := csTinyImageLabel[Img.bLabel];
//    Aplication.ProcessMessages();
//    Sleep(5000);
//  end;
//  CloseFile(cifarFile);
//end;
//
public class ucifar10
{
	private static class TTinyImage
	{
		byte bLabel;
		/*TTinyImageChannel*/byte[][] r, G, B;
	
	}
	
	private static class TTinySingleChannelImage
	{
		byte bLabel;
		/*TTinyImageChannel*/byte[][] Grey;
	
	}
/*
	private static class TTinySingleChannelImage1D
	{
		byte bLabel;
		/ *TTinyImageChannel1D* /byte[][] Grey;
	
	}
*/
	//	TTinyImageChannel byte[0 .. 31, 0 .. 31]
//	TTinyImageChannel1D byte[0 .. 32 * 32 - 1]
	// [ValueTODO] TTInyImageFile = TODO(file of)
// Useful for gray scale image
	// [TypePtr] TTinySingleChannelImagePtr = ^TTinySingleChannelImage
	// [TypePtr] TTinySingleChannelImage1DPtr = ^TTinySingleChannelImage1D
	public final static String[] csTinyImageLabel = new String[] {"airplane", "automobile", "bird", "cat", "deer", "dog", "frog", "horse", "ship", "truck"};
// Loads a TinyImage into FCL TImage.

	public static void LoadTinyImageIntoTImage(final TTinyImage TI, final PixelWriter pw)
	{
		for( int i = 0; i <= 31; i++ ) {
			for( int j = 0; j <= 31; j++ ) {
				pw.setColor(j ,i, Color.rgb(TI.r[i][j], TI.G[i][j], TI.B[i][j]) );
			}
		}
	}

// Loads a single channel tiny image into image.
	public static void LoadTISingleChannelIntoImage(final TTinySingleChannelImage TI, final PixelWriter pw)
	{
		for( int i = 0; i <= 31; i++ ) {
			for( int j = 0; j <= 31; j++ ) {
				pw.setColor(j, i, Color.rgb(TI.Grey[i][j], TI.Grey[i][j], TI.Grey[i][j]) );
			}
		}
	}

// Creates a gray scale tiny image
	public static void TinyImageCreateGrey(final TTinyImage TI, final TTinySingleChannelImage TIGray)
	{
		TIGray.bLabel = TI.bLabel;
		for( int i = 0; i <= 31; i++ ) {
			for( int j = 0; j <= 31; j++ ) {
				TIGray.Grey[i][j] = (byte)((TI.r[i][j] + TI.G[i][j] + TI.B[i][j]) / 3);
			}
		}
	}

// Calculates Vertical Edges
	public static void TinyImageVE(final TTinySingleChannelImage TI, final TTinySingleChannelImage TIVE)
	{
		TIVE.bLabel = TI.bLabel;
		for( int i = 1; i <= 31; i++ ) {
			TIVE.Grey[0][i] = (byte)128;
			for( int j = 0; j <= 31; j++ ) {
				int aux = (TI.Grey[i][j] - TI.Grey[i - 1][j]) * 3 + 128;
				if( aux < 0 ) {
					aux = 0;
				}
				if( aux > 255 ) {
					aux = 255;
				}
				TIVE.Grey[i][j] = (byte)aux;
			}
		}
	}

// Calculates Horizontal Edges
	public static void TinyImageHE(final TTinySingleChannelImage TI, final TTinySingleChannelImage TIHE)
	{
		TIHE.bLabel = TI.bLabel;
		for( int i = 0; i <= 31; i++ ) {
			TIHE.Grey[i][0] = (byte)128;
			for( int j = 1; j <= 31; j++ ) {
				int aux = (TI.Grey[i][j] - TI.Grey[i][j - 1]) * 3 + 128;
				if( aux < 0 ) {
					aux = 0;
				}
				if( aux > 255 ) {
					aux = 255;
				}
				TIHE.Grey[i][j] = (byte)aux;
			}
		}
	}

//Zeroes all pixels that have a small distance to the number 128
	public static void TinyImageRemoveZeroGradient(final TTinySingleChannelImage TI, final byte distance)
	{
		for( int i = 0; i <= 31; i++ ) {
			for( int j = 0; j <= 31; j++ ) {
				if( Math.abs(TI.Grey[i][j] - 128) < distance ) {
					TI.Grey[i][j] = 0;
				}
			}
		}
	}

// Calculates Horizontal and Vertical Edges
	public static void TinyImageHVE(final TTinySingleChannelImage TI, final TTinySingleChannelImage TIHE)
	{
		TIHE.bLabel = TI.bLabel;
		for( int i = 1; i <= 31; i++ ) {
			TIHE.Grey[i][0] = (byte)128;
			TIHE.Grey[0][i] = (byte)128;
			for( int j = 1; j <= 31; j++ ) {
				int aux1 = (TI.Grey[i][j] - TI.Grey[i][j - 1]) + 128;
				int aux2 = (TI.Grey[i][j] - TI.Grey[i - 1][j]) + 128;
				int aux = 0;
				if( (Math.abs(aux1 - 128) > Math.abs(aux2 - 128)) ) {
					aux = aux1;
				} else {
					aux = aux2;
				}
				if( aux < 0 ) {
					aux = 0;
				}
				if( aux > 255 ) {
					aux = 255;
				}
				TIHE.Grey[i][j] = (byte)aux;
			}
		}
	}
/*
// This function transforms a 2D TinyImage into 1D TinyImage
	public static TTinySingleChannelImage1D TinyImageTo1D(final TTinySingleChannelImage TI)
	{
		TTinySingleChannelImage1DPtr TIPtr;

		TIPtr = addr(TI);
		return TIPtr;
	}
*/
}
