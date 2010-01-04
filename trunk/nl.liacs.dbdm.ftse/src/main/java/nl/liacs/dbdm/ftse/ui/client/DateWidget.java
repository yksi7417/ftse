/*
 *
 * Created on Jan 3, 2010 | 4:40:19 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.client;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class DateWidget extends Composite {

	private static final DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd");

	private HorizontalPanel container;
	private DatePicker picker;
	private TextBox text;
	private Image icon;

	public DateWidget() {
		init(new Date());
		super.initWidget(container);
	}

	public DateWidget(Date date) {
		init(date);
		super.initWidget(container);
	}

	private HorizontalPanel init(Date date) {

		container = new HorizontalPanel();
		picker = new DatePicker();
		text = new TextBox();
		icon = new Image(FtseImageBundle.INSTANCE.iconCalendar());

		picker.setVisible(false);
		picker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date d = event.getValue();
				text.setText(format.format(d));
				picker.setVisible(false);
			}
		});

		text.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				picker.setVisible(false);
			}
		});
		text.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				try {
					Date guess = format.parse(text.getValue());
					picker.setValue(guess);
				} catch (IllegalArgumentException e) {
					Window.alert("Date should be of form yyyy-MM-dd");
				}
			}
		});

		icon.setStyleName("hand");
		icon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				picker.setVisible(true);
			}
		});

		container.add(text);
		container.add(icon);
		container.add(picker);

		picker.setValue(date);
		text.setValue(format.format(date));

		return container;
	}

	public Date getValue() {
		return picker.getValue();
	}

	public void setValue(String date) {
		try {
			Date value = format.parse(date);
			picker.setValue(value);
			text.setValue(date);
		} catch (IllegalArgumentException e) {
		}
	}

}
