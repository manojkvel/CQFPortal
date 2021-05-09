package cqfportal.client;


import org.gwt.advanced.client.util.ThemeHelper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

import cqfportal.client.apps.finalproject.CallibrationView;
import cqfportal.client.apps.finalproject.PCAView;
import cqfportal.client.apps.finalproject.PricingView;
import cqfportal.client.apps.finalproject.UVNSHView;
import cqfportal.client.apps.finalproject.UVSHView;
import cqfportal.client.apps.finalproject.UploadView;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CQFPortal implements EntryPoint {
	
	/**
	 * This is the entry point method.
	 */
	final TabLayoutPanel masterTabPanel = new TabLayoutPanel(2.5, Unit.EM);
	
	public void onModuleLoad() {
				
				

		IApp[] cqfApp = getAppDetails();					
		addAppToMasterTabPanel(cqfApp[0]);
		addAppToMasterTabPanel(cqfApp[1]);
		//masterTabPanel.add(app.getMasterWidget(), app.getTitle());
					   
	    // Attach the LayoutPanel to the RootLayoutPanel. The latter will listen for
	    // resize events on the window to ensure that its children are informed of
	    // possible size changes.
		masterTabPanel.addStyleName("tabBackground");
		FlowPanel headerPanel = new FlowPanel();
		Label l = new Label("CQF Final Project - 2013 : HJM & UVSH (Manoj Veluchuri)");
		l.addStyleName("mainHeader");
		headerPanel.add(l);
//		headerPanel.setHeight("120px");
		
		RootLayoutPanel rp = RootLayoutPanel.get();
		rp.add(headerPanel);
		rp.setWidgetHorizontalPosition(headerPanel, Alignment.STRETCH);
		 rp.add(masterTabPanel);		
		rp.setWidgetTopBottom(masterTabPanel, 26, Unit.PX,0,Unit.PX);
		
	    //RootPanel.get("widgets").add(masterTabPanel);
	    //RootPanel.get("widgets").add(new HTML("html widget"));
		//
	      
	}
	
	private IApp[] getAppDetails() {	
		
		//for now there is only 1 app. 
		//in actual situation app details will be souced from a database or a config file
		IApp appHJM = new DefaultApp()
			.setTitle("HJM");
		IApp appUVSH = new DefaultApp()
		.setTitle("UVSH");
		
		//HJM 
		//HJM children		
		//IViewRenderer ren//
		FlowPanel fp = new FlowPanel();
		fp.add(new Label("hjm pricing"));
		
		CallibrationView cView = new CallibrationView();
		IView pricing = new DefaultView()
			.setTitle("Pricing")
			.setClickableText("Pricing")			
			.setApp(appHJM)
			.setRenderer(new PricingView().setCallibrationView(cView))
			.renderFirstTime();				
					
//		IView callibration = new DefaultView()
//			.setTitle("Calibration")
//			.setClickableText("Calibration")			
//			.setApp(app)
//			.setRenderer(cView)
//			.renderFirstTime();
				
		IView pca = new DefaultView()
		.setTitle("PCA")
		.setClickableText("PCA")			
		.setApp(appHJM)
		.setRenderer(new PCAView().setCallibrationView(cView)).renderFirstTime();	
		
		IView fileUpload = new DefaultView()
		.setTitle("Upload Data")
		.setClickableText("Upload Data")			
		.setApp(appHJM)
		.setRenderer(new UploadView()).renderFirstTime();	
		
		IView hjm = new DefaultView()
			.setTitle("HJM")
			.setClickableText("HJM")
			.addChild(fileUpload)
			.addChild(pca)
			.addChild(pricing)
			//.addChild(callibration)
			.setApp(appHJM);
						
		
		IView noStaticHedge = new DefaultView()
			.setTitle("No Static Hedge")
			.setClickableText("No Static Hedge")			
			.setApp(appUVSH)
			.setRenderer(new UVNSHView())
			.renderFirstTime();
		
		IView staticHedge = new DefaultView()
		.setTitle("Static Hedge")
		.setClickableText("Static Hedge")			
		.setApp(appUVSH)
		.setRenderer(new UVSHView())
		.renderFirstTime();
		
		IView uvsh = new DefaultView()
			.setTitle("UVSH")
			.setClickableText("UVSH")
			.addChild(noStaticHedge)
			.addChild(staticHedge);
							
		appHJM.addView(hjm);
			appUVSH.addView(uvsh);
				
			IApp[] apps = {appHJM,appUVSH};
		return apps;
	}
	
	private void addAppToMasterTabPanel(IApp app) {					
		SplitLayoutPanel p = new SplitLayoutPanel();
		
		FlowPanel navPanel = getNavPanel(app.getAllViews());	
		
		FlowPanel mainPanel = new FlowPanel();
		app.setContainer(mainPanel);
		navPanel.addStyleName("navelBackground");
		mainPanel.addStyleName("mainBackground");
		
		p.animate(5000);
		
//		p.addNorth(, mainPanel.getOffsetWidth());
		p.addWest(navPanel, 128);
		p.add(mainPanel);
//		p.addStyleName("mainBackground");
		
		masterTabPanel.add(p, app.getTitle());	
		
	}
	
	private FlowPanel getNavPanel(Iterable<IView> views) {
		FlowPanel p = new FlowPanel();
		
		for (final IView view : views) {
			if (view.hasChildren()) {							
				p.add(getDisclosurePanelForView(view));
			} else {								
				//p.add(clickableLabel);
				Anchor anchor = getClickableHyperlink(view);			
				
				p.add(anchor);
				p.add(new HTML()); //add new line
			}
		}
		
		return p;
	}
	
	private Anchor getClickableHyperlink(final IView view) {
		
		Anchor clickableText = new Anchor(view.getClickableText());		
				
		clickableText.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				view.show();
				
			}
		});
				
		return clickableText;
	}
			
	private DisclosurePanel getDisclosurePanelForView(IView view) {
		if (! view.hasChildren())
			return null;
		
		DisclosurePanel dp = new DisclosurePanel(view.getClickableText());
		dp.setOpen(true);
		FlowPanel fp = new FlowPanel();
		
		for (IView child : view.getAllChildren()) {
			//fp.add(getClickableLabel(child));
			fp.add(getClickableHyperlink(child));
			fp.add(new HTML()); //to introduce new line
		}
		
		dp.add(fp);						
		return dp;
	}
}
