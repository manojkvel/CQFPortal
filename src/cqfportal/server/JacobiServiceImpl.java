/**
 * 
 */
package cqfportal.server;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;

public class JacobiServiceImpl {

	public Matrix getEigenVectors(double[][] Amat, double atol) {
		double asumsq;
		int n, r;
		double[][] Vmat, Anext;
		Matrix Vnext = null;
		n = Amat[0].length;
		r = 0;
		Vmat = getIdentityMatrix(n).toDoubleArray();
		asumsq = MatrixUTSumSq(Amat);
		while (asumsq > atol) {
			Anext = JacobiAmat(n, Amat);
			Vnext = JacobiVmat(n, Amat, Vmat);
			asumsq = MatrixUTSumSq(Anext);
			Amat = Anext;
			Vmat = Vnext.toDoubleArray();
			r++;
		}
		return Vnext;
	}

	public Matrix getIdentityMatrix(int n) {
		// Returns the (nxn) Identity Matrix
		int i;
		double[][] Imat = new double[n][n];
		for (i = 0; i < n; i++)
			Imat[i][i] = 1;
		return MatrixFactory.importFromArray(Imat);
	}

	public double MatrixUTSumSq(double[][] Xmat) {
		// Returns the Sum of Squares of the Upper Triangle of a Matrix
		double sum;
		int i, j, n;
		n = Xmat[0].length;
		sum = 0;
		for (i = 0; i < n; i++) {
			for (j = i + 1; j < n; j++) {
				sum = sum + (Xmat[i][j] * Xmat[i][j]);
			}
		}
		return sum;
	}

	public double[][] JacobiAmat(int n, double[][] Athis) {
		// Returns Anext matrix, updated using the P rotation matrix
		double[] rthis;
		Matrix Aathis, Pthis, Anext;
		rthis = Jacobirvec(n, Athis);
		Pthis = JacobiPmat(n, rthis);
		Aathis = MatrixFactory.importFromArray(Athis);
		Anext = Pthis.transpose().mtimes(Aathis.mtimes(Pthis));
		return Anext.toDoubleArray();

	}

	public double[] Jacobirvec(int n, double[][] Athis) {
		// Returns vector containing mr, mc and jrad
		// These are the row and column vectors and the angle of rotation for
		// the P matrix
		double maxval, jrad;
		int i, j, mr, mc;
		double[][] Awork = new double[n][n];
		maxval = -1;
		mr = -1;
		mc = -1;
		// Two cycles below represent the search for the largest off-diagonal
		// element in the matrix Athis
		// To elimiate this element, the appropraite roatation angle is
		// calculated
		for (i = 0; i < n; i++) {
			for (j = i + 1; j < n; j++) {
				Awork[i][j] = Math.abs(Athis[i][j]);
				if (Awork[i][j] > maxval) {
					maxval = Awork[i][j];
					mr = i;
					mc = j;
				}
			}
		}
		if (Athis[mr][mr] == Athis[mc][mc]) {
			jrad = 0.25 * Math.PI * Math.signum(Athis[mr][mc]);

		} else {
			jrad = 0.5 * Math.atan(2 * Athis[mr][mc]
					/ (Athis[mr][mr] - Athis[mc][mc]));
		}
		return new double[] { mr, mc, jrad };
	}

	public Matrix JacobiPmat(int n, double[] rthis) {
		// Returns the rotation Pthis matrix
		double[][] Pthis;
		int mr = Double.valueOf(rthis[0]).intValue();
		int mc = Double.valueOf(rthis[1]).intValue();
		Pthis = getIdentityMatrix(n).toDoubleArray();
		Pthis[mr][mr] = Math.cos(rthis[2]);
		Pthis[mc][mr] = Math.sin(rthis[2]);
		Pthis[mr][mc] = -Math.sin(rthis[2]);
		Pthis[mc][mc] = Math.cos(rthis[2]);
		return MatrixFactory.importFromArray(Pthis);
	}

	public Matrix JacobiVmat(int n, double[][] Athis, double[][] Vthis) {
		// Returns Vnext matrix
		// Keeps track of the eigenvectors during the rotations
		double[] rthis;
		Matrix Pthis;
		Matrix Vnext;
		rthis = Jacobirvec(n, Athis); // Search for the largest off-diagonal
										// element to be eliminated by rotation,
										// generate the angle of rotation
		Pthis = JacobiPmat(n, rthis); // Generate rotation matrix P
		Vnext = MatrixFactory.importFromArray(Vthis).mtimes(Pthis); // Improving
																	// eigenvectors
																	// by Vi x
																	// Pi, where
																	// V
																	// reflects
																	// previous
																	// multipliations
		return Vnext;
	}

	public double[] Eigenvaluesevec(double[][] Amat, double atol) {
		// Uses the Jacobi method to get the eigenvalues for a symmetric matrix
		// Amat is rotated (using the P matrix) until its off-diagonal elements
		// are minimal
		double asumsq;
		int i, n, r;
		double[][] Anext;
		n = Amat[0].length;
		double[] evec = new double[n];

		r = 0;
		asumsq = MatrixUTSumSq(Amat);
		while (asumsq > atol) {
			Anext = JacobiAmat(n, Amat);
			asumsq = MatrixUTSumSq(Anext);
			Amat = Anext;
			r++;
		}
		for (i = 0; i < n; i++) {
			evec[i] = Amat[i][i];
		}
		return evec;
	}

}
