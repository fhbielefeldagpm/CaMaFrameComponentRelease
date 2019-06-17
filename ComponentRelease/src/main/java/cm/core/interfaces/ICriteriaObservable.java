package cm.core.interfaces;

import cm.core.sentries.Sentry;

public interface ICriteriaObservable {

	public void registerCriteriaObserver(Sentry obs);
	public void unregisterCriteriaObserver(Sentry obs);
	
}