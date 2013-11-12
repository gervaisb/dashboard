package be.gervaisb.oss.dashboard.repos.build.mocks;

import java.util.Date;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.repos.build.BuildsRepository;
import be.gervaisb.oss.dashboard.repos.build.Status;

public class MockBuildRepository implements BuildsRepository {

    private enum MockState implements Status.State {
	Successful (true),
	Failed	   (false);

	private final boolean successful;

	private MockState(final boolean successful) {
	    this.successful = successful;
	}
	@Override
	public String getLabel() {
	    return name();
	}
	@Override
	public boolean isSuccessful() {
	    return successful;
	}
    }

    private class MockStatus extends Status {
	public MockStatus(final boolean failed) {
	    super(
		    failed?MockState.Failed:MockState.Successful, new Date(),
			    failed?"No failed tests found, a possible compilation error occurred.":null);
	}
    }

    @Override
    public Status getStatus(final Reference reference) {
	return new MockStatus( (Math.random()*10)%2==0 );
    }

}
