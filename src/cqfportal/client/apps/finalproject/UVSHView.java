package cqfportal.client.apps.finalproject;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import cqfportal.client.IViewRenderer;

public class UVSHView implements IViewRenderer {

	private UVolStatHedgeServiceAsync uvshService = GWT
			.create(UVolStatHedgeService.class);
	private static boolean price1Calculated = false;
	private static boolean price2Calculated = false;

	@Override
	public void renderFirstTime(FlowPanel rootFlowPanel) {

		FlowPanel flowPanel = new FlowPanel();
		final DialogBox mask = new DialogBox();
		HorizontalPanel contentPanel = new HorizontalPanel();

		contentPanel.setSpacing(50);

		FlexTable market = new FlexTable();
		// market.getFlexCellFormatter().setRowSpan(0, 1, 2);
		market.setHTML(0, 1, "<u><b>Market Inputs</b></u>");
		// market.setText(0, 1, "");

		final TextBox spot = new TextBox();
		final TextBox rfRate = new TextBox();
		final TextBox sigmaN = new TextBox();
		final TextBox signaP = new TextBox();
		spot.setValue("100");
		rfRate.setValue(NumberFormat.getPercentFormat().format(0.05));
		sigmaN.setValue(NumberFormat.getPercentFormat().format(.17));
		signaP.setValue(NumberFormat.getPercentFormat().format(.23));

		market.setText(1, 0, "Spot");
		market.setWidget(1, 1, spot);
		market.setText(2, 0, "Risk Free Rate");
		market.setWidget(2, 1, rfRate);
		market.setText(3, 0, "σ -");
		market.setWidget(3, 1, sigmaN);
		market.setText(4, 0, "σ +");
		market.setWidget(4, 1, signaP);

		contentPanel.add(market);

		FlexTable exotic = new FlexTable();
		exotic.setHTML(0, 1, "<u><b>Binary Option Inputs</b></u>");
		// exotic.getFlexCellFormatter().setRowSpan(0, 0, 2);
		// exotic.setText(0, 1, "");

		final TextBox strike = new TextBox();
		final TextBox maturity = new TextBox();
		final TextBox payOff = new TextBox();
		final ListBox callOrPut = new ListBox();
		callOrPut.addItem("Call");
		callOrPut.addItem("Put");
		callOrPut.setSelectedIndex(0);

		strike.setValue("100");
		maturity.setValue("1");
		payOff.setValue(NumberFormat.getCurrencyFormat().format(10));
		exotic.setText(1, 0, "Strike");
		exotic.setWidget(1, 1, strike);
		exotic.setText(2, 0, "Maturity");
		exotic.setWidget(2, 1, maturity);
		exotic.setText(3, 0, "Payoff");
		exotic.setWidget(3, 1, payOff);
		exotic.setText(4, 0, "Option Type(C/P)");
		exotic.setWidget(4, 1, callOrPut);

		contentPanel.add(exotic);

		FlexTable staticSpread = new FlexTable();
		staticSpread.setHTML(0, 1, "<u><b>Static hedge Portfolio</b></u>");
		// exotic.getFlexCellFormatter().setRowSpan(0, 0, 2);
		// staticSpread.setText(0, 1, "");

		final TextBox strikeOffset1 = new TextBox();
		strikeOffset1.setValue(NumberFormat.getPercentFormat().format(0.2));
//		final TextBox strikeOffset2 = new TextBox();
//		strikeOffset2.setValue(NumberFormat.getPercentFormat()
//				.format(NumberFormat.getDecimalFormat().parse(
//						strikeOffset1.getValue())));
//		strikeOffset2.setEnabled(false);
		final TextBox shStrike1 = new TextBox();
		shStrike1.setEnabled(false);
		final TextBox shMaturity1 = new TextBox();
		shMaturity1.setEnabled(false);
		final TextBox shCP1 = new TextBox();
		shCP1.setEnabled(false);
		final TextBox shBSP1 = new TextBox();
		shBSP1.setEnabled(false);
		final TextBox shSpread1 = new TextBox();
		shSpread1.setEnabled(false);
		final TextBox shBid1 = new TextBox();
		shBid1.setEnabled(false);
		final TextBox shOffer1 = new TextBox();
		shOffer1.setEnabled(false);

		shBSP1.setValue(NumberFormat.getCurrencyFormat().format(0));
		shBid1.setValue(NumberFormat.getCurrencyFormat().format(0));
		shOffer1.setValue(NumberFormat.getCurrencyFormat().format(0));
		shSpread1.setValue(NumberFormat.getCurrencyFormat().format(0.2));

		final TextBox shStrike2 = new TextBox();
		shStrike2.setEnabled(false);
		final TextBox shMaturity2 = new TextBox();
		shMaturity2.setEnabled(false);
		final TextBox shCP2 = new TextBox();
		shCP2.setEnabled(false);
		final TextBox shBSP2 = new TextBox();
		shBSP2.setEnabled(false);
		final TextBox shSpread2 = new TextBox();
		shSpread2.setEnabled(false);
		final TextBox shBid2 = new TextBox();
		shBid2.setEnabled(false);
		final TextBox shOffer2 = new TextBox();
		shOffer2.setEnabled(false);

		shBSP2.setValue(NumberFormat.getCurrencyFormat().format(0));
		shBid2.setValue(NumberFormat.getCurrencyFormat().format(0));
		shOffer2.setValue(NumberFormat.getCurrencyFormat().format(0));
		shSpread2.setValue(NumberFormat.getCurrencyFormat().format(0.2));
		staticSpread.setText(1, 0, "Strike");
		staticSpread.setWidget(1, 1, shStrike1);
		shStrike1.setValue(""
				+ (1 - NumberFormat.getDecimalFormat().parse(
						strikeOffset1.getValue()))
				* NumberFormat.getDecimalFormat().parse(strike.getValue()));
		staticSpread.setWidget(1, 2, shStrike2);
		shStrike2.setValue(""
				+ (1 + NumberFormat.getDecimalFormat().parse(
						strikeOffset1.getValue()))
				* NumberFormat.getDecimalFormat().parse(strike.getValue()));
		staticSpread.setText(2, 0, "Maturity");
		staticSpread.setWidget(2, 1, shMaturity1);
		shMaturity1.setValue(maturity.getValue());
		staticSpread.setWidget(2, 2, shMaturity2);
		shMaturity2.setValue(maturity.getValue());
		staticSpread.setText(3, 0, "Call/Put");
		staticSpread.setWidget(3, 1, shCP1);
		shCP1.setValue(callOrPut.getValue(callOrPut.getSelectedIndex()));
		staticSpread.setWidget(3, 2, shCP2);
		shCP2.setValue(callOrPut.getValue(callOrPut.getSelectedIndex()));
		staticSpread.setText(4, 0, "Price (by BS)");
		staticSpread.setWidget(4, 1, shBSP1);
		staticSpread.setWidget(4, 2, shBSP2);
		staticSpread.setText(5, 0, "Spread");
		staticSpread.setWidget(5, 1, shSpread1);
		staticSpread.setWidget(5, 2, shSpread2);
		staticSpread.setText(6, 0, "Bid");
		staticSpread.setWidget(6, 1, shBid1);
		staticSpread.setWidget(6, 2, shBid2);
		staticSpread.setText(7, 0, "Offer");
		staticSpread.setWidget(7, 1, shOffer1);
		staticSpread.setWidget(7, 2, shOffer2);

		staticSpread.setText(8, 0, "Strike Offset");
		staticSpread.setWidget(8, 1, strikeOffset1);
//		staticSpread.setWidget(8, 2, strikeOffset2);

		// staticSpread.setText(9,0,"Get Price");
		Button calculateBSP1 = new Button();
		calculateBSP1.setText("Get Prices");
		calculateBSP1.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AsyncCallback<Double> callBack1 = new AsyncCallback<Double>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Black Scholes Pricing Failed. Please check the values!");
						GWT.log(caught.getMessage());

					}

					@Override
					public void onSuccess(Double result) {

						// double bid =
						// getValuePostFiniteDiff(NumberFormat.getDecimalFormat().parse(spot.getValue()),
						// result, 2);
						// double offer =
						// getValuePostFiniteDiff(NumberFormat.getDecimalFormat().parse(spot.getValue()),
						// result, 3);
						GWT.log("" + result);
						GWT.log(""
								+ NumberFormat.getCurrencyFormat().format(
										result.doubleValue()));
						shBSP1.setValue(NumberFormat.getCurrencyFormat()
								.format(result.doubleValue()));
						shBid1.setValue(NumberFormat
								.getCurrencyFormat()
								.format(result.doubleValue()
										- (NumberFormat.getCurrencyFormat()
												.parse(shSpread1.getValue()) / 2)));
						shOffer1.setValue(NumberFormat
								.getCurrencyFormat()
								.format(result.doubleValue()
										+ (NumberFormat.getCurrencyFormat()
												.parse(shSpread1.getValue()) / 2)));
						// nshSpread.setValue(NumberFormat.getCurrencyFormat().format(offer-bid));
						price1Calculated = true;
						if (price1Calculated & price2Calculated)
							mask.hide();

					}

				};
				double spotPrice, intRate, sigmaMinus, sigmaPlus, sigma, strikePrice, maturityV, binaryPayoff;
				String callOrPutSelect;
				int numberOfterations = 100, i = 0;

				spotPrice = NumberFormat.getDecimalFormat().parse(
						spot.getValue());

				intRate = NumberFormat.getDecimalFormat().parse(
						rfRate.getValue());

				sigmaMinus = NumberFormat.getDecimalFormat().parse(
						sigmaN.getValue());

				sigmaPlus = NumberFormat.getPercentFormat().parse(
						signaP.getValue()) / 100;

				sigma = (sigmaMinus + sigmaPlus) / 2;

				strikePrice = NumberFormat.getDecimalFormat().parse(
						shStrike1.getValue());

				maturityV = NumberFormat.getDecimalFormat().parse(
						shMaturity1.getValue());

				// binaryPayoff =
				// NumberFormat.getCurrencyFormat().parse(sh.getValue());

				callOrPutSelect = callOrPut.getValue(callOrPut
						.getSelectedIndex());

				GWT.log(spotPrice + "," + strikePrice + "," + maturityV + ","
						+ intRate + "," + sigma + "," + callOrPutSelect);
				uvshService.getBlackScholesPrice(spotPrice, strikePrice,
						maturityV, intRate, sigma, callOrPutSelect, callBack1);

				AsyncCallback<Double> callBack2 = new AsyncCallback<Double>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Black Scholes Pricing Failed. Please check the values!");
						GWT.log(caught.getMessage());

					}

					@Override
					public void onSuccess(Double result) {

						// double bid =
						// getValuePostFiniteDiff(NumberFormat.getDecimalFormat().parse(spot.getValue()),
						// result, 2);
						// double offer =
						// getValuePostFiniteDiff(NumberFormat.getDecimalFormat().parse(spot.getValue()),
						// result, 3);
						shBSP2.setValue(NumberFormat.getCurrencyFormat()
								.format(result.doubleValue()));
						shBid2.setValue(NumberFormat
								.getCurrencyFormat()
								.format(result.doubleValue()
										- (NumberFormat.getCurrencyFormat()
												.parse(shSpread2.getValue()) / 2)));
						shOffer2.setValue(NumberFormat
								.getCurrencyFormat()
								.format(result.doubleValue()
										+ (NumberFormat.getCurrencyFormat()
												.parse(shSpread2.getValue()) / 2)));
						// nshSpread.setValue(NumberFormat.getCurrencyFormat().format(offer-bid));
						price2Calculated = true;
						if (price1Calculated & price2Calculated)
							mask.hide();

					}

				};
				// double spotPrice, intRate, sigmaMinus, sigmaPlus,
				// sigma,strikePrice, maturityV, binaryPayoff;
				// String callOrPutSelect;
				// int numberOfterations = 100,i=0;

				spotPrice = NumberFormat.getDecimalFormat().parse(
						spot.getValue());

				intRate = NumberFormat.getDecimalFormat().parse(
						rfRate.getValue());

				sigmaMinus = NumberFormat.getDecimalFormat().parse(
						sigmaN.getValue());

				sigmaPlus = NumberFormat.getPercentFormat().parse(
						signaP.getValue()) / 100;

				sigma = (sigmaMinus + sigmaPlus) / 2;

				strikePrice = NumberFormat.getDecimalFormat().parse(
						shStrike2.getValue());

				maturityV = NumberFormat.getDecimalFormat().parse(
						shMaturity2.getValue());

				// binaryPayoff =
				// NumberFormat.getCurrencyFormat().parse(sh.getValue());

				callOrPutSelect = callOrPut.getValue(callOrPut
						.getSelectedIndex());

				uvshService.getBlackScholesPrice(spotPrice, strikePrice,
						maturityV, intRate, sigma, callOrPutSelect, callBack2);

				mask.setModal(true);
				mask.setGlassEnabled(true);
				mask.setPopupPosition(300, 200);
				mask.setText("Pricing by using Black Scholes Formula");
				mask.center();
			}

		});

		staticSpread.setWidget(9, 0, calculateBSP1);

		contentPanel.add(staticSpread);

		FlexTable calculateSHButtonPanel = new FlexTable();
		calculateSHButtonPanel.getFlexCellFormatter().setRowSpan(1, 2, 2);
		calculateSHButtonPanel.setText(0, 0, "Asset Steps");
		final TextBox assetSteps = new TextBox();
		assetSteps.setValue("100");
		calculateSHButtonPanel.setWidget(0, 1, assetSteps);
		Button calculateSHButton = new Button();
		// calculateSHButton.setText("Calculate Spread Without Hedge");

		ScrollPanel scrollerFDValues = new ScrollPanel();
//		final FlexTable finDiffValues = new FlexTable();
//		finDiffValues.setTitle("Finite Difference Values");
		// scroller.setWidth("500");
		// scroller.setHeight("20");
		scrollerFDValues.setSize("500px", "200px");
		calculateSHButton.setText("Calculate Spread With Hedge");
		calculateSHButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!(price1Calculated & price2Calculated))
					Window.alert("Please claculate the prices first to proceed!");
				else {
					if(Double.valueOf(assetSteps.getValue())
							.intValue() >1000){Window.alert("Please select less than or equal to 1000 asset steps."); return;}
					AsyncCallback<HashMap> callBack = new AsyncCallback<HashMap>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Finite Difference Method Failed. Please check the values!");
							GWT.log(caught.getMessage());

						}

						@Override
						public void onSuccess(HashMap result) {
							mask.hide();
							final DialogBox win = new DialogBox();
							FlowPanel mainPanel = getOptimizedPortfolioPanel(result);

							win.setGlassEnabled(true);
							Button closeButton = new Button("Close Button");
							closeButton.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									win.hide();
								}
							});
							mainPanel.add(closeButton);
							win.add(mainPanel);
							win.center();

						}

					};
					double spotPrice, intRate, sigmaMinus, sigmaPlus, strikePrice, maturityV, binaryPayoff, v1bid, v1off, v2bid, v2off, vanillaStrike1, vanillaMat1, vanillaStrike2, vanillaMat2;
					String callOrPutSelect;
					int numberOfterations = 100, i = 0;
					GWT.log("" + i++);
					spotPrice = NumberFormat.getDecimalFormat().parse(
							spot.getValue());
					GWT.log("" + i++);
					intRate = NumberFormat.getDecimalFormat().parse(
							rfRate.getValue());
					GWT.log("" + i++);
					sigmaMinus = NumberFormat.getDecimalFormat().parse(
							sigmaN.getValue());
					GWT.log("" + i++);
					sigmaPlus = NumberFormat.getPercentFormat().parse(
							signaP.getValue()) / 100;
					GWT.log("" + i++);
					strikePrice = NumberFormat.getDecimalFormat().parse(
							strike.getValue());
					GWT.log("" + i++);
					maturityV = NumberFormat.getDecimalFormat().parse(
							maturity.getValue());
					GWT.log("" + i++);
					binaryPayoff = NumberFormat.getCurrencyFormat().parse(
							payOff.getValue());
					GWT.log("" + i++);
					callOrPutSelect = callOrPut.getValue(callOrPut
							.getSelectedIndex());
					GWT.log("" + i++);
					numberOfterations = Double.valueOf(assetSteps.getValue())
							.intValue();
					GWT.log("" + i++);

					v1bid = NumberFormat.getCurrencyFormat().parse(
							shBid1.getValue());
					v1off = NumberFormat.getCurrencyFormat().parse(
							shOffer1.getValue());
					v2bid = NumberFormat.getCurrencyFormat().parse(
							shBid2.getValue());
					v2off = NumberFormat.getCurrencyFormat().parse(
							shOffer2.getValue());

					vanillaStrike1 = NumberFormat.getDecimalFormat().parse(
							shStrike1.getValue());
					vanillaMat1 = NumberFormat.getDecimalFormat().parse(
							shMaturity1.getValue());
					vanillaStrike2 = NumberFormat.getDecimalFormat().parse(
							shStrike2.getValue());
					vanillaMat2 = NumberFormat.getDecimalFormat().parse(
							shMaturity2.getValue());

					uvshService.getOptimizedPortfolio(spotPrice, intRate,
							sigmaMinus, sigmaPlus, strikePrice, maturityV,
							binaryPayoff, callOrPutSelect, numberOfterations,
							v1bid, v1off, v2bid, v2off, +vanillaStrike1,
							vanillaMat1, vanillaStrike2, vanillaMat2, callBack);
					GWT.log(spotPrice + "," + intRate + "," + sigmaMinus + ","
							+ sigmaPlus + "," + strikePrice + "," + maturityV
							+ "," + binaryPayoff + "," + callOrPutSelect + ","
							+ numberOfterations + "," + v1bid + "," + v1off
							+ "," + v2bid + "," + v2off + "," + vanillaStrike1
							+ "," + vanillaMat1 + "," + vanillaStrike2 + ","
							+ vanillaMat2);
					mask.setModal(true);
					mask.setGlassEnabled(true);
					mask.setPopupPosition(300, 200);
					mask.setText("Invoking Finite Difference Method");
					mask.center();
				}

			}
		});
		calculateSHButtonPanel.setWidget(0, 2, calculateSHButton);

		// contentPanel.add(new Label(
		// " Add Yield Curve Data in CSV format using the below widget"));

		// contentPanel.add(getFileUploaderWidget(rootFlowPanel));

		rootFlowPanel.add(contentPanel);
		rootFlowPanel.add(calculateSHButtonPanel);
//		rootFlowPanel.add(new Label("Finite Difference Values"));
//		scrollerFDValues.add(finDiffValues);
//		rootFlowPanel.add(scrollerFDValues);

	}

	public FlowPanel getOptimizedPortfolioPanel(HashMap map) {

		FlowPanel mainPanel = new FlowPanel();
		FlexTable mainFlex = new FlexTable();
		mainFlex.setHTML(0, 0, "<b> Vanilla-Portfolio</b>");
		mainFlex.setHTML(0, 1, "<b> Binary Option-Hedged Portfolio</b>");

		FlexTable vanFlex = new FlexTable();

		vanFlex.setHTML(0, 1, "<b> Vanilla 1</b>");
		vanFlex.setHTML(0, 2, "<b> Vanilla 2</b>");
		vanFlex.setHTML(1, 0, "<b> Quantity</b>");
		TextBox q1 = new TextBox();
		q1.setValue("" + map.get("q1"));
		q1.setReadOnly(true);
		vanFlex.setWidget(1, 1, q1);
		TextBox q2 = new TextBox();
		q2.setValue("" + map.get("q2"));
		q2.setReadOnly(true);
		vanFlex.setWidget(1, 2, q2);
		vanFlex.setHTML(2, 0, "<b> Market Price</b>");
		TextBox mp1 = new TextBox();
		mp1.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("mp1")));
		mp1.setReadOnly(true);
		vanFlex.setWidget(2, 1, mp1);
		TextBox mp2 = new TextBox();
		mp2.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("mp2")));
		mp2.setReadOnly(true);
		vanFlex.setWidget(2, 2, mp2);
		vanFlex.setHTML(3, 0, "<b> Hedging Cost</b>");
		TextBox hc = new TextBox();
		hc.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("hc")));
		hc.setReadOnly(true);
		vanFlex.setWidget(3, 1, hc);

		vanFlex.setHTML(4, 0, "<b> Portfolio Bid</b>");
		TextBox bid = new TextBox();
		bid.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("b")));
		bid.setReadOnly(true);
		vanFlex.setWidget(4, 1, bid);

		vanFlex.setHTML(5, 0, "<b> Portfolio Offer</b>");
		TextBox offer = new TextBox();
		offer.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("o")));
		offer.setReadOnly(true);
		vanFlex.setWidget(5, 1, offer);

		mainFlex.setWidget(1, 0, vanFlex);

		FlexTable boFlex = new FlexTable();
		boFlex.setHTML(0, 0, "<b> Bid</b>");
		TextBox bbid = new TextBox();
		bbid.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("bb")));
		bbid.setReadOnly(true);
		bbid.addStyleName("highlightBidS");
		boFlex.setWidget(0, 1, bbid);

		boFlex.setHTML(1, 0, "<b> Offer</b>");
		TextBox boffer = new TextBox();
		boffer.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("bo")));
		boffer.setReadOnly(true);
		boffer.addStyleName("highlightOfferS");
		boFlex.setWidget(1, 1, boffer);

		boFlex.setHTML(2, 0, "<b> Spread</b>");
		TextBox bs = new TextBox();
		bs.addStyleName("highlightS");
		bs.setValue(NumberFormat.getCurrencyFormat().format(
				(Double) map.get("bs")));
		bs.setReadOnly(true);
		boFlex.setWidget(2, 1, bs);
		// mainFlex.setWidget(1,0, vanFlex);
		mainFlex.setWidget(1, 1, boFlex);
		mainPanel.add(mainFlex);
		mainPanel.addStyleName("mainBackground");
		return mainPanel;
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
