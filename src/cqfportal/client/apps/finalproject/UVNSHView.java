package cqfportal.client.apps.finalproject;

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

public class UVNSHView  implements IViewRenderer{

	private UVolStatHedgeServiceAsync uvshService = GWT.create(UVolStatHedgeService.class);
	@Override
	public void renderFirstTime(FlowPanel rootFlowPanel) {
		
		FlowPanel flowPanel = new FlowPanel();
		final DialogBox mask = new DialogBox();
		HorizontalPanel contentPanel = new HorizontalPanel();

		contentPanel.setSpacing(50);
				
		FlexTable market = new FlexTable();
		market.setHTML(0, 0, "<u><b>Market Inputs</b></u>");
		market.setText(0, 1, "");
		//market.getFlexCellFormatter().setRowSpan(0, 0, 2);
		final TextBox spot = new TextBox();
		final TextBox rfRate = new TextBox();
		final TextBox sigmaN = new TextBox();
		final TextBox signaP = new TextBox();
		spot.setValue("100");
		rfRate.setValue(NumberFormat.getPercentFormat().format(0.05));
		sigmaN.setValue(NumberFormat.getPercentFormat().format(.17));
		signaP.setValue(NumberFormat.getPercentFormat().format(.23));
		
		market.setText(1,0,"Spot");
		market.setWidget(1,1,spot);
		market.setText(2,0,"Risk Free Rate");
		market.setWidget(2,1,rfRate);
		market.setText(3,0,"σ -");
		market.setWidget(3,1,sigmaN);
		market.setText(4,0,"σ +");
		market.setWidget(4,1,signaP);
		
		contentPanel.add(market);
		
		FlexTable exotic = new FlexTable();
		exotic.setHTML(0, 0, "<u><b>Binary Option Inputs</b></u>");
		//exotic.getFlexCellFormatter().setRowSpan(0, 0, 2);
		exotic.setText(0, 1, "");
		
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
		exotic.setText(1,0,"Strike");
		exotic.setWidget(1, 1, strike);
		exotic.setText(2,0,"Maturity");
		exotic.setWidget(2, 1, maturity);
		exotic.setText(3,0,"Payoff");
		exotic.setWidget(3, 1, payOff);
		exotic.setText(4,0,"Option Type(C/P)");
		exotic.setWidget(4, 1, callOrPut);
		
		contentPanel.add(exotic);
		
		FlexTable nonStaticSpread = new FlexTable();
		nonStaticSpread.setHTML(0, 0, "<u><b>No Static hedge Applied</b></u>");
		//exotic.getFlexCellFormatter().setRowSpan(0, 0, 2);
		nonStaticSpread.setText(0, 1, "");
		
		final TextBox nshBid = new TextBox();
		nshBid.setEnabled(false);
		final TextBox nshOffer = new TextBox();
		nshOffer.setEnabled(false);
		final TextBox nshSpread = new TextBox();
		nshSpread.setEnabled(false);
		
		nshBid.setValue(NumberFormat.getCurrencyFormat().format(0));
		nshOffer.setValue(NumberFormat.getCurrencyFormat().format(0));
		nshSpread.setValue(NumberFormat.getCurrencyFormat().format(0));
		nonStaticSpread.setText(1,0,"Bid");
		nonStaticSpread.setWidget(1, 1, nshBid);
		nonStaticSpread.setText(2,0,"Offer");
		nonStaticSpread.setWidget(2, 1, nshOffer);
		nonStaticSpread.setText(3,0,"Spread");
		nonStaticSpread.setWidget(3, 1, nshSpread);
		
		
		contentPanel.add(nonStaticSpread);
		
		FlexTable calculateSHButtonPanel = new FlexTable();
		calculateSHButtonPanel.getFlexCellFormatter().setRowSpan(1,2,2);
		calculateSHButtonPanel.setText(0, 0, "Asset Steps");
		final TextBox assetSteps = new TextBox();
		assetSteps.setValue("100"); 
		calculateSHButtonPanel.setWidget(0, 1, assetSteps);
		Button calculateSHButton = new Button();
		calculateSHButton.setText("Calculate Spread Without Hedge");
		
		
		
		ScrollPanel scrollerFDValues = new ScrollPanel();
		final FlexTable finDiffValues = new FlexTable();
		finDiffValues.setTitle("Finite Difference Values");
		//scroller.setWidth("500");
		//scroller.setHeight("20");
		scrollerFDValues.setSize("500px", "200px");
		
		calculateSHButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if(Double.valueOf(assetSteps.getValue())
						.intValue() >1000) Window.alert("Please select less than or equal to 1000 asset steps.");
				else{
				
				AsyncCallback<double[][]> callBack = new AsyncCallback<double[][]>(){
					
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Finite Difference Method Failed. Please check the values!");
						GWT.log(caught.getMessage());
						
					}

					@Override
					public void onSuccess(double[][] result) {
						String[] headers = {"Spot","Payoff","Value(Worst)","Value(Best)"};
						for (int i = 0; i < 4; i++) {
							finDiffValues.setHTML(0, i, "<th border=\"1\">"
									+ headers[i] + "</th>");
						}
						for (int row = 1; row < result.length + 1; row++) {
							for (int i = 0; i < 4; i++) {
								finDiffValues.setHTML(
										row,
										i,
										"<td border=\"1\">"
												+ String.valueOf(result[row-1][i])
												+ "</td>");
							}
						}
						finDiffValues.setBorderWidth(1);
						finDiffValues.getRowFormatter().addStyleName(0,
								"headerRow");
						
						double bid = getValuePostFiniteDiff(NumberFormat.getDecimalFormat().parse(spot.getValue()),
								result, 2);
						double offer = getValuePostFiniteDiff(NumberFormat.getDecimalFormat().parse(spot.getValue()),
								result, 3);
						
						nshBid.setValue(NumberFormat.getCurrencyFormat().format(bid));
						nshBid.addStyleName("highlightBid");
						nshOffer.setValue(NumberFormat.getCurrencyFormat().format(offer));
						nshOffer.addStyleName("highlightOffer");
						nshSpread.setValue(NumberFormat.getCurrencyFormat().format(offer-bid));
						nshSpread.addStyleName("highlight");
						mask.hide();
						
					}
					
				};
				double spotPrice, intRate, sigmaMinus, sigmaPlus, strikePrice, maturityV, binaryPayoff;
				String callOrPutSelect;
				int numberOfterations = 100,i=0;
				GWT.log(""+i++);
				spotPrice = NumberFormat.getDecimalFormat().parse(spot.getValue());
				GWT.log(""+i++);
				intRate = NumberFormat.getDecimalFormat().parse(rfRate.getValue());
				GWT.log(""+i++);
				sigmaMinus = NumberFormat.getDecimalFormat().parse(sigmaN.getValue());
				GWT.log(""+i++);
				sigmaPlus = NumberFormat.getPercentFormat().parse(signaP.getValue())/100;
				GWT.log(""+i++);
				strikePrice = NumberFormat.getDecimalFormat().parse(strike.getValue());
				GWT.log(""+i++);
				maturityV = NumberFormat.getDecimalFormat().parse(maturity.getValue());
				GWT.log(""+i++);
				binaryPayoff = NumberFormat.getCurrencyFormat().parse(payOff.getValue());
				GWT.log(""+i++);
				callOrPutSelect = callOrPut.getValue(callOrPut.getSelectedIndex());
				GWT.log(""+i++);
				numberOfterations = Double.valueOf(assetSteps.getValue()).intValue();
				GWT.log(""+i++);
				
				uvshService.getFiniteDifferences(spotPrice, intRate, sigmaMinus, sigmaPlus, strikePrice, maturityV, binaryPayoff, callOrPutSelect, numberOfterations, callBack);
				mask.setModal(true);
				mask.setGlassEnabled(true);
				mask.setPopupPosition(300, 200);
				mask.setText("Invoking Finite Difference Method");
				mask.center();
			}
			
			}
		});
		calculateSHButtonPanel.setWidget(0, 2, calculateSHButton);
		
		
//		contentPanel.add(new Label(
//				" Add Yield Curve Data in CSV format using the below widget"));

//		contentPanel.add(getFileUploaderWidget(rootFlowPanel));
		
		
		rootFlowPanel.add(contentPanel);
		rootFlowPanel.add(calculateSHButtonPanel);
		rootFlowPanel.add(new Label("Finite Difference Values"));
		scrollerFDValues.add(finDiffValues);
		rootFlowPanel.add(scrollerFDValues);
		
	}
	
	public double getValuePostFiniteDiff(double spot,double[][] finDiffArr,int col){
		// row col difference
		double[] closest = {0,0,-1};
		
		for(int i=0;i<finDiffArr.length;i++){
			for(int j=0;j<1;j++){
				if(spot==finDiffArr[i][j]){
					return finDiffArr[i][col];
				}else{
					if(closest[2]<0){
						double diff = spot - finDiffArr[i][j];
						if(diff <0) {
							diff*=(-1);
							closest[0]=i;
							closest[0]=j;
							closest[0]=diff;
							}else{
								closest[0]=i;
								closest[0]=j;
								closest[0]=diff;
							}
					}else{
						double diff = spot - finDiffArr[i][j];
						if(diff <0)	diff*=(-1);
						if(diff<closest[2]){
							closest[0]=i;
							closest[0]=j;
							closest[0]=diff;
						}
					}
				}
			}
		}
		return finDiffArr[Double.valueOf(closest[0]).intValue()][col];
	}
	

}
