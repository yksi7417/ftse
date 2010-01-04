/*
 *
 * Created on Dec 27, 2009 | 2:15:27 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
@RemoteServiceRelativePath(FtseService.URL)
public interface FtseService extends RemoteService {

	String URL = "/gwt/**/ftse.gwt";

	String train(TrainingOptions options);

	String updateLikelihoods(LikelihoodOptions options);

	PredictionResult predict(PredictionOptions options);

}
