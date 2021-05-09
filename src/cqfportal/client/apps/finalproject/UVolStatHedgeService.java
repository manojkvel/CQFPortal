package cqfportal.client.apps.finalproject;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("uvshService")
public interface UVolStatHedgeService extends RemoteService {

	public double[][] getFiniteDifferences(double Spot, double IntRate,
			double SigmaMinus, double SigmaPlus, double Strike,
			double Maturity, double BinaryPayoff, String CallOrPut, int NAS);

	public double[][] getFiniteDiffWithHedge(double IntRate, double SigmaMinus,
			double SigmaPlus, double Strike, double Maturity,
			double BinaryPayoff, String CallOrPut, int NAS,
			double VanillaOptStrike1, double VanillaOptMaturity1,
			double VanillaOptQuantity1, double VanillaOptStrike2,
			double VanillaOptMaturity2, double VanillaOptQuantity2);

	public double getBlackScholesPrice(double Spot, double Strike,
			double Maturity, double IntRate, double Sigma, String CallOrPut);

	public HashMap getOptimizedPortfolio(double spot, double IntRate,
			double SigmaMinus, double SigmaPlus, double Strike,
			double Maturity, double BinaryPayoff, String CallOrPut, int NAS,
			 double v1bid,double v1off,double v2bid,double v2off,
			double VanillaOptStrike1, double VanillaOptMaturity1,
			double VanillaOptStrike2, double VanillaOptMaturity2);

}
