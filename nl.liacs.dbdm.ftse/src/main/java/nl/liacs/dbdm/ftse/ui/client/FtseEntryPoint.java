/*
 *
 * Created on Dec 26, 2009 | 11:58:02 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseEntryPoint implements EntryPoint {

	private static final String CHART_TITLE = "FTSE Close Value ";
	private RootPanel main;
	private VerticalPanel contentPanel;
	private TabPanel contentTabPanel;

	// Load Data Widgets
	private VerticalPanel loadDataPanel;
	private DateWidget loadDataFromDateWidget;
	private DateWidget loadDataToDateWidget;
	private Button loadDataResetButton;
	private Button loadDataButton;
	private AsyncCallback<String> loadDataResetButtonCallback;
	private AsyncCallback<String> loadDataButtonCallback;

	// Training Widgets
	private VerticalPanel trainingPanel;
	private DateWidget trainingDateWidget;
	private TextBox trainingNumOfIters;
	private Button trainButton;
	private AsyncCallback<String> trainButtonCallback;

	// Likelihood Computation Widgets
	private HorizontalPanel likelihoodPanel;
	private DateWidget likelihoodDateWidget;
	private TextBox likelihoodDays;
	private Button likelihoodButton;
	private AsyncCallback<String> likelihoodButtonCallback;

	// Prediction Widgets
	private VerticalPanel predictionPanel;
	private VerticalPanel predictionChartPanel;
	private DateWidget predictionDateWidget;
	private TextBox predictionDays;
	private TextBox predictionTolerance;
	private Button predictionButton;
	private AsyncCallback<PredictionResult> predictionButtonCallback;
	private PredictionResult predictionResult;

	private FtseServiceAsync service;
	private Runnable initChartCallback;

	private DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd");

	@Override
	public void onModuleLoad() {
		initService();
		initMain();
		initVisualizationApi();
	}

	private void initVisualizationApi() {
		initChartCallback = new Runnable() {
			@Override
			public void run() {
				updateChart(null);
			}
		};
		VisualizationUtils.loadVisualizationApi(initChartCallback, LineChart.PACKAGE);
	}

	private void initService() {
		service = GWT.create(FtseService.class);
	}

	private Panel initMain() {
		main = RootPanel.get("main");
		main.add(initContentPanel());
		return main;
	}

	private Panel initContentPanel() {
		contentTabPanel = new TabPanel();
		contentTabPanel.setWidth("100%");
		contentTabPanel.add(initLoadDataPanel(), "Load Data");
		contentTabPanel.add(initTrainingPanel(), "Train");
		contentTabPanel.add(initLikelihoodPanel(), "Likelihood");
		contentTabPanel.add(initPredictionPanel(), "Predict");
		contentTabPanel.selectTab(0);

		contentPanel = new VerticalPanel();
		contentPanel.setWidth("100%");
		contentPanel.add(contentTabPanel);

		return contentPanel;
	}

	private Panel initLoadDataPanel() {
		loadDataPanel = new VerticalPanel();

		loadDataFromDateWidget = new DateWidget();
		loadDataFromDateWidget.setValue("2005-01-01");

		loadDataToDateWidget = new DateWidget();
		loadDataToDateWidget.setValue("2009-12-01");

		loadDataButton = new Button("Load Data");
		loadDataButtonCallback = new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Window.alert(result);
			}

			@Override
			public void onFailure(Throwable t) {
				Window.alert(t.getMessage());
			}
		};
		loadDataButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				service.loadData(loadDataFromDateWidget.getValue(), loadDataToDateWidget.getValue(),
						loadDataButtonCallback);
			}
		});

		loadDataResetButton = new Button("Clear Data");
		loadDataResetButtonCallback = new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Window.alert(result);
			}

			@Override
			public void onFailure(Throwable e) {
				Window.alert(e.getMessage());
			}
		};
		loadDataResetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				service.clearData(loadDataResetButtonCallback);
			}
		});

		HorizontalPanel holder = new HorizontalPanel();
		holder.add(new Label("Load data from: "));
		holder.add(loadDataFromDateWidget);
		holder.add(new Label("Load data to: "));
		holder.add(loadDataToDateWidget);
		HorizontalPanel holder2 = new HorizontalPanel();
		holder2.add(loadDataResetButton);
		holder2.add(loadDataButton);
		loadDataPanel.add(holder);
		loadDataPanel.add(holder2);

		return loadDataPanel;
	}

	private Panel initTrainingPanel() {
		trainingPanel = new VerticalPanel();

		trainingDateWidget = new DateWidget();

		trainingNumOfIters = new TextBox();
		trainingNumOfIters.setValue("20");

		trainButton = new Button("Train");
		trainButtonCallback = new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable t) {
				Window.alert(t.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				contentTabPanel.selectTab(1);
			}
		};
		trainButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				service.train(getTrainingOptions(), trainButtonCallback);
			}
		});

		HorizontalPanel form = new HorizontalPanel();
		form.add(new Label("Training Start Date:"));
		form.add(trainingDateWidget);
		form.add(new Label("Training Iterations:"));
		form.add(trainingNumOfIters);
		trainingPanel.add(form);
		trainingPanel.add(trainButton);
		return trainingPanel;
	}

	private Panel initLikelihoodPanel() {
		likelihoodPanel = new HorizontalPanel();

		likelihoodDateWidget = new DateWidget();

		likelihoodDays = new TextBox();

		likelihoodButton = new Button("Update Likelihoods");
		likelihoodButtonCallback = new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				contentTabPanel.selectTab(2);
			}

			@Override
			public void onFailure(Throwable t) {
				Window.alert(t.getMessage());
			}
		};
		likelihoodButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				service.updateLikelihoods(getLikelihoodOptions(), likelihoodButtonCallback);
			}
		});

		likelihoodPanel.add(new Label("Likelihood Update Start Date:"));
		likelihoodPanel.add(likelihoodDateWidget);
		likelihoodPanel.add(new Label("Days to update likelihood:"));
		likelihoodPanel.add(likelihoodDays);
		likelihoodPanel.add(likelihoodButton);
		return likelihoodPanel;
	}

	private Panel initPredictionPanel() {
		predictionPanel = new VerticalPanel();
		predictionChartPanel = new VerticalPanel();

		predictionDateWidget = new DateWidget();

		predictionDays = new TextBox();
		predictionDays.setValue("100");

		predictionTolerance = new TextBox();
		predictionTolerance.setValue("0.01");

		predictionButton = new Button("Predict");
		predictionButtonCallback = new AsyncCallback<PredictionResult>() {
			@Override
			public void onSuccess(PredictionResult result) {
				predictionResult = result;
				updateChart(predictionResult);
			}

			@Override
			public void onFailure(Throwable t) {
				Window.alert(t.getMessage());
			}
		};
		predictionButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				service.predict(getPredictionOptions(), predictionButtonCallback);
			}
		});

		HorizontalPanel holder = new HorizontalPanel();
		holder.add(new Label("Prediction Start Date:"));
		holder.add(predictionDateWidget);
		holder.add(new Label("Days to predict:"));
		holder.add(predictionDays);
		holder.add(new Label("Likelihood Tolerance:"));
		holder.add(predictionTolerance);
		holder.add(predictionButton);
		predictionPanel.add(holder);
		predictionPanel.add(predictionChartPanel);
		return predictionPanel;
	}

	public void updateChart(PredictionResult result) {
		predictionChartPanel.clear();
		Map<Date, List<Double>> values = result.getPredictions();
		if (values != null && !values.isEmpty()) {
			DataTable table = DataTable.create();
			table.addColumn(ColumnType.STRING, "Date");
			table.addColumn(ColumnType.NUMBER, "Actual");
			table.addColumn(ColumnType.NUMBER, "Predicted");
			table.addRows(values.size());
			int rowIndex = 0;
			for (Map.Entry<Date, List<Double>> e : values.entrySet()) {
				table.setValue(rowIndex, 0, getSimpleDateString(e.getKey()));
				table.setValue(rowIndex, 1, e.getValue().get(0));
				table.setValue(rowIndex++, 2, e.getValue().get(1));
			}
			Options options = LineChart.Options.create();
			options.setHeight(400);
			options.setWidth(1000);
			options.setTitle(CHART_TITLE + "['" + format.format(result.getStartDate()) + "' - '"
					+ format.format(result.getEndDate()) + "'] - MAPE: " + getPercentage(result.getMape()));
			options.setSmoothLine(true);
			LineChart chart = new LineChart(table, options);
			predictionChartPanel.add(chart);
		}
	}

	private String getSimpleDateString(Date d) {
		return format.format(d);
	}

	private String getPercentage(Double d) {
		return NumberFormat.getFormat("##.##").format(d * 100.0) + "%";
	}

	private TrainingOptions getTrainingOptions() {
		TrainingOptions options = new TrainingOptions();
		options.setStartDate(trainingDateWidget.getValue());
		try {
			options.setNumberOfIterations(Integer.parseInt(trainingNumOfIters.getValue()));
		} catch (NumberFormatException e) {
		}
		return options;
	}

	private LikelihoodOptions getLikelihoodOptions() {
		LikelihoodOptions options = new LikelihoodOptions();
		options.setStartDate(likelihoodDateWidget.getValue());
		try {
			options.setDays(Integer.valueOf(likelihoodDays.getValue()));
		} catch (NumberFormatException e) {
		}
		return options;
	}

	private PredictionOptions getPredictionOptions() {
		PredictionOptions options = new PredictionOptions();
		options.setDate(predictionDateWidget.getValue());
		try {
			options.setDays(Integer.valueOf(predictionDays.getValue()));
		} catch (NumberFormatException e) {
		}
		try {
			options.setTolerance(Double.valueOf(predictionTolerance.getValue()));
		} catch (NumberFormatException e) {
		}
		return options;
	}

}
