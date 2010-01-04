/*
 *
 * Created on Dec 27, 2009 | 2:15:52 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public interface FtseServiceAsync {

	void train(TrainingOptions options, AsyncCallback<String> callback);

	void updateLikelihoods(LikelihoodOptions options, AsyncCallback<String> callback);

	void predict(PredictionOptions options, AsyncCallback<PredictionResult> callback);

	void clearData(AsyncCallback<String> loadDataResetButtonCallback);

	void loadData(Date from, Date to, AsyncCallback<String> loadDataButtonCallback);

}
