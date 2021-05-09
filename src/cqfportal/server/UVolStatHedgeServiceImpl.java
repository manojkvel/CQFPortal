package cqfportal.server;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cqfportal.client.apps.finalproject.UVolStatHedgeService;

public class UVolStatHedgeServiceImpl extends RemoteServiceServlet implements
		UVolStatHedgeService {

	public double[][] getFiniteDifferences(double Spot, double IntRate,
			double SigmaMinus, double SigmaPlus, double Strike,
			double Maturity, double BinaryPayoff, String CallOrPut, int NAS) {
		double dS;
		dS = 2 * Strike / NAS;

		double dt;
		dt = 0.9 / SigmaPlus / SigmaPlus / NAS / NAS;

		int NTS;
		NTS = Double.valueOf((Maturity / dt)).intValue();
		dt = Maturity / NTS;

		int AssetIter, TimeIter;
		double[] VWorstOld, VWorstNew, VBestOld, VBestNew;
		int i_NAS = Long.valueOf(NAS).intValue();
		VWorstOld = new double[i_NAS];
		VWorstNew = new double[i_NAS];
		VBestOld = new double[i_NAS];
		VBestNew = new double[i_NAS];

		// at expiry
		for (AssetIter = 0; AssetIter < NAS; AssetIter++) {
			VWorstOld[Long.valueOf(AssetIter).intValue()] = getBinaryPayoff(dS
					* AssetIter, Strike, BinaryPayoff, CallOrPut);
			VBestOld[Long.valueOf(AssetIter).intValue()] = VWorstOld[Long
					.valueOf(AssetIter).intValue()];
		}

		double Delta, Gamma, Theta, Sigma, S;

		for (TimeIter = 1; TimeIter < NTS; TimeIter++) {
			for (AssetIter = 1; AssetIter < NAS - 1; AssetIter++) {
				S = AssetIter * dS;

				// worst case
				Delta = (VWorstOld[AssetIter + 1] - VWorstOld[AssetIter - 1])
						/ 2 / dS;
				Gamma = (VWorstOld[AssetIter + 1] + VWorstOld[AssetIter - 1] - 2 * VWorstOld[AssetIter])
						/ dS / dS;

				Sigma = Gamma > 0 ? SigmaMinus : SigmaPlus;

				Theta = IntRate * VWorstOld[AssetIter] - IntRate * S * Delta
						- 0.5 * Sigma * Sigma * S * S * Gamma;

				VWorstNew[AssetIter] = VWorstOld[AssetIter] - Theta * dt;

				// best case
				Delta = (VBestOld[AssetIter + 1] - VBestOld[AssetIter - 1]) / 2
						/ dS;
				Gamma = (VBestOld[AssetIter + 1] + VBestOld[AssetIter - 1] - 2 * VBestOld[AssetIter])
						/ dS / dS;

				Sigma = Gamma < 0 ? SigmaMinus : SigmaPlus;

				Theta = IntRate * VBestOld[AssetIter] - IntRate * S * Delta
						- 0.5 * Sigma * Sigma * S * S * Gamma;

				VBestNew[AssetIter] = VBestOld[AssetIter] - Theta * dt;
			}

			// All S terms vanish at 0. So simply discount at 0
			VWorstNew[0] = VWorstOld[0] * (1 - IntRate);
			VBestNew[0] = VBestOld[0] * (1 - IntRate);

			// value is constant for binary option when S is very large
			VWorstNew[NAS - 1] = VWorstNew[NAS - 2];
			VBestNew[NAS - 1] = VBestNew[NAS - 2];

			for (AssetIter = 0; AssetIter < NAS; AssetIter++) {
				VWorstOld[AssetIter] = VWorstNew[AssetIter];
				VBestOld[AssetIter] = VBestNew[AssetIter];
			}

		}

		double[][] Output = new double[NAS][4];
		for (AssetIter = 0; AssetIter < NAS; AssetIter++) {
			Output[AssetIter][0] = dS * AssetIter;
			Output[AssetIter][1] = getBinaryPayoff(dS * AssetIter, Strike,
					BinaryPayoff, CallOrPut);
			Output[AssetIter][2] = VWorstOld[AssetIter];
			Output[AssetIter][3] = VBestOld[AssetIter];
		}

		return Output;
	}

	private double getBinaryPayoff(double Spot, double Strike,
			double BinaryPayoff, String CallOrPut) {

		if ((CallOrPut.toUpperCase()).equals("CALL"))
			return Spot > Strike ? BinaryPayoff : 0;
		else
			return Spot < Strike ? BinaryPayoff : 0;
	}

	private double getVanillaOptionPayoff(double Spot, double Strike,
			String CallOrPut) {

		return ((CallOrPut).toUpperCase().equals("CALL")) ? Math.max(Spot
				- Strike, 0) : Math.max(Strike - Spot, 0);

	}

	public double[][] getFiniteDiffWithHedge(double IntRate, double SigmaMinus,
			double SigmaPlus, double Strike, double Maturity,
			double BinaryPayoff, String CallOrPut, int NAS,
			double VanillaOptStrike1, double VanillaOptMaturity1,
			double VanillaOptQuantity1, double VanillaOptStrike2,
			double VanillaOptMaturity2, double VanillaOptQuantity2) {

		// assumes all 3 options - binary and the 2 static hedges have the same
		// maturity

		double dS;
		dS = 2 * Strike / NAS;

		double dt;
		dt = 0.9 / SigmaPlus / SigmaPlus / NAS / NAS;

		int NTS;
		NTS = Double.valueOf(Maturity / dt).intValue();
		dt = Maturity / NTS;

		int AssetIter, TimeIter;
		double[] VWorstOld, VWorstNew, VBestOld, VBestNew;
		VWorstOld = new double[NAS];
		VBestOld = new double[NAS];
		VWorstNew = new double[NAS];
		VBestNew = new double[NAS];

		// at expiry
		for (AssetIter = 0; AssetIter < NAS; AssetIter++) {
			double Spot = dS * AssetIter;

			VWorstOld[AssetIter] = getBinaryPayoff(Spot, Strike, BinaryPayoff,
					CallOrPut)
					+ getVanillaOptionPayoff(Spot, VanillaOptStrike1, CallOrPut)
					* VanillaOptQuantity1
					+ getVanillaOptionPayoff(Spot, VanillaOptStrike2, CallOrPut)
					* VanillaOptQuantity2;

			VBestOld[AssetIter] = VWorstOld[AssetIter];
		}

		double Delta, Gamma, Theta, Sigma, S;

		for (TimeIter = 1; TimeIter < NTS; TimeIter++) {
			for (AssetIter = 1; AssetIter < NAS - 1; AssetIter++) {
				S = AssetIter * dS;

				// worst case
				Delta = (VWorstOld[AssetIter + 1] - VWorstOld[AssetIter - 1])
						/ 2 / dS;
				Gamma = (VWorstOld[AssetIter + 1] + VWorstOld[AssetIter - 1] - 2 * VWorstOld[AssetIter])
						/ dS / dS;

				Sigma = Gamma > 0 ? SigmaMinus : SigmaPlus;

				Theta = IntRate * VWorstOld[AssetIter] - IntRate * S * Delta
						- 0.5 * Sigma * Sigma * S * S * Gamma;

				VWorstNew[AssetIter] = VWorstOld[AssetIter] - Theta * dt;

				// best case
				Delta = (VBestOld[AssetIter + 1] - VBestOld[AssetIter - 1]) / 2
						/ dS;
				Gamma = (VBestOld[AssetIter + 1] + VBestOld[AssetIter - 1] - 2 * VBestOld[AssetIter])
						/ dS / dS;

				Sigma = Gamma < 0 ? SigmaMinus : SigmaPlus;

				Theta = IntRate * VBestOld[AssetIter] - IntRate * S * Delta
						- 0.5 * Sigma * Sigma * S * S * Gamma;

				VBestNew[AssetIter] = VBestOld[AssetIter] - Theta * dt;
			}

			// All S terms vanish at 0. So simply discount at 0
			VWorstNew[0] = VWorstOld[0] * (1 - IntRate);
			VBestNew[0] = VBestOld[0] * (1 - IntRate);

			// value is constant for binary option when S is very large
			VWorstNew[NAS - 1] = VWorstNew[NAS - 2];
			VBestNew[NAS - 1] = VBestNew[NAS - 2];

			for (AssetIter = 0; AssetIter < NAS; AssetIter++) {
				VWorstOld[AssetIter] = VWorstNew[AssetIter];
				VBestOld[AssetIter] = VBestNew[AssetIter];
			}

		}

		double[][] Output = new double[NAS][4]; // spot, payoff, v_worst, v_best
		for (AssetIter = 0; AssetIter < NAS; AssetIter++) {
			double Spot = dS * AssetIter;
			Output[AssetIter][0] = Spot;
			Output[AssetIter][1] = getBinaryPayoff(Spot, Strike, BinaryPayoff,
					CallOrPut)
					+ getVanillaOptionPayoff(Spot, VanillaOptStrike1, CallOrPut)
					* VanillaOptQuantity1
					+ getVanillaOptionPayoff(Spot, VanillaOptStrike2, CallOrPut)
					* VanillaOptQuantity2;

			Output[AssetIter][2] = VWorstOld[AssetIter];
			Output[AssetIter][3] = VBestOld[AssetIter];
		}

		return Output;
	}

	public double getBlackScholesPrice(double Spot, double Strike,
			double Maturity, double IntRate, double Sigma, String CallOrPut) {

		double d1 = 0, d2 = 0;

		d1 = Math.log(Spot / Strike) + (IntRate + 0.5 * Sigma * Sigma)
				* Maturity;
		d1 = d1 / Sigma / Math.pow(Maturity, 0.5);

		d2 = d1 - Sigma * Math.pow(Maturity, 0.5);
		NormalDistribution norm = new NormalDistribution();
		if (CallOrPut.toUpperCase().equals("CALL"))
			return Spot * norm.cumulativeProbability(d1) - Strike
					* Math.exp(-IntRate * Maturity)
					* norm.cumulativeProbability(d2);
		else
			return (Strike * Math.exp(-IntRate * Maturity)
					* norm.cumulativeProbability(-d2) - Spot
					* norm.cumulativeProbability(-d1));

	}

	public static void main(String[] args) {

		UVolStatHedgeServiceImpl u = new UVolStatHedgeServiceImpl();
	}

	public HashMap getOptimizedPortfolio(double spot, double IntRate,
			double SigmaMinus, double SigmaPlus, double Strike,
			double Maturity, double BinaryPayoff, String CallOrPut, int NAS,
			double v1bid, double v1off, double v2bid, double v2off,
			double VanillaOptStrike1, double VanillaOptMaturity1,
			double VanillaOptStrike2, double VanillaOptMaturity2) {

		double q1 = Math.random(), q2 = Math.random();
		double binSpread1, rounded;
		HashMap<Double, Integer> countMap = new HashMap<Double, Integer>();
		HashMap map = new HashMap();
		do {
			Random ran = new Random();
			q1 = (ran.nextInt() / Math.pow(2, 32));
			q2 = ran.nextDouble();
			while (q1 * q2 > 0) {
				q1 = (ran.nextInt() / Math.pow(2, 32));
				q2 = ran.nextDouble();
			}
			map = getMin(spot, IntRate, SigmaMinus, SigmaPlus, Strike,
					Maturity, BinaryPayoff, CallOrPut, NAS, v1bid, v1off,
					v2bid, v2off, VanillaOptStrike1, VanillaOptMaturity1, q1,
					VanillaOptStrike2, VanillaOptMaturity2, q2, false);
			binSpread1 = (Double) map.get("bs");
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			rounded = Double.valueOf(twoDForm.format(binSpread1));
			if (countMap.containsKey(rounded)) {
				int count = countMap.get(rounded);
				count++;
				countMap.put(rounded, count);
			} else {
				countMap.put(rounded, 1);
			}

		} while (countMap.get(rounded) <= 100);
		System.out.println("q1 " + q1);
		System.out.println("q2 " + q2);
		System.out.println(binSpread1);
		getMin(spot, IntRate, SigmaMinus, SigmaPlus, Strike, Maturity,
				BinaryPayoff, CallOrPut, NAS, v1bid, v1off, v2bid, v2off,
				VanillaOptStrike1, VanillaOptMaturity1, q1, VanillaOptStrike2,
				VanillaOptMaturity2, q2, true);
		return map;

	}

	public HashMap getMin(double spot, double IntRate, double SigmaMinus,
			double SigmaPlus, double Strike, double Maturity,
			double BinaryPayoff, String CallOrPut, int NAS, double v1bid,
			double v1off, double v2bid, double v2off, double VanillaOptStrike1,
			double VanillaOptMaturity1, double q1, double VanillaOptStrike2,
			double VanillaOptMaturity2, double q2, boolean print) {
		double mp1, mp2;

		double hedgingCost;

		double binBid, binOffer, binSpread;
		double[][] output = getFiniteDiffWithHedge(IntRate, SigmaMinus,
				SigmaPlus, Strike, Maturity, BinaryPayoff, CallOrPut, NAS,
				VanillaOptStrike1, VanillaOptMaturity1, q1, VanillaOptStrike2,
				VanillaOptMaturity2, q2);
		double bid = getValuePostFiniteDiff(100, output, 2), offer = getValuePostFiniteDiff(
				100, output, 3);
		mp1 = q1 > 0 ? v1off : v1bid;
		mp2 = q2 > 0 ? v2off : v2bid;
		hedgingCost = mp1 * q1 + mp2 * q2;
		binBid = bid - hedgingCost;
		binOffer = offer - hedgingCost;
		binSpread = binOffer - binBid;

		// LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]
		// { binOffer, -1*binBid }, binSpread);
		// Collection constraints = new ArrayList();
		// constraints.add(new LinearConstraint(new double[] { mp1, mp2 },
		// Relationship.LEQ, hedgingCost));
		// constraints.add(new LinearConstraint(new double[] { mp1, mp2 },
		// Relationship.LEQ, bid-binBid));
		// constraints.add(new LinearConstraint(new double[] { mp1, mp2 },
		// Relationship.GEQ, offer-binOffer));
		// //constraints.add(new LinearConstraint(new double[] { binOffer, mp2
		// }, Relationship.LEQ, offer-binOffer));
		//
		// PointValuePair solution = new SimplexSolver().optimize(f,
		// constraints, GoalType.MINIMIZE, false);
		// SimplexOptimizer so = new SimplexOptimizer(hedgingCost, binSpread);
		// so.optimize(OptimizationData)
		// double x = solution.getPoint()[0];
		// double y = solution.getPoint()[1];
		// double min = solution.getValue();

		HashMap map = new HashMap();
		map.put("q1", q1);
		map.put("q2", q2);
		map.put("mp1", mp1);
		map.put("mp2", mp2);
		map.put("hc", hedgingCost);
		map.put("b", bid);
		map.put("o", offer);
		map.put("bb", binBid);
		map.put("bo", binOffer);
		map.put("bs", binSpread);
		if (print) {
			System.out.println("q1 " + q1);
			System.out.println("q2 " + q2);
			System.out.println("Markets Prices " + mp1 + " , " + mp2);
			System.out.println("Hedging Cost " + hedgingCost);
			System.out.println("Bid " + bid);
			System.out.println("Offer " + offer);
			System.out.println("binBid " + binBid);
			System.out.println("bin Offer " + binOffer);
			System.out.println("binSPread " + binSpread);
		}
		// System.out.println(binSpread);
		return map;
	}

	public double getValuePostFiniteDiff(double spot, double[][] finDiffArr,
			int col) {
		// row col difference
		double[] closest = { 0, 0, -1 };

		for (int i = 0; i < finDiffArr.length; i++) {
			for (int j = 0; j < 1; j++) {
				if (spot == finDiffArr[i][j]) {
					return finDiffArr[i][col];
				} else {
					if (closest[2] < 0) {
						double diff = spot - finDiffArr[i][j];
						if (diff < 0) {
							diff *= (-1);
							closest[0] = i;
							closest[0] = j;
							closest[0] = diff;
						} else {
							closest[0] = i;
							closest[0] = j;
							closest[0] = diff;
						}
					} else {
						double diff = spot - finDiffArr[i][j];
						if (diff < 0)
							diff *= (-1);
						if (diff < closest[2]) {
							closest[0] = i;
							closest[0] = j;
							closest[0] = diff;
						}
					}
				}
			}
		}
		return finDiffArr[Double.valueOf(closest[0]).intValue()][col];
	}

}
