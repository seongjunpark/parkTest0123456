package www.funsumer.net;

import android.app.Application;
import android.content.Context;

public class ALL extends Application {

	private int state;
    private static Context context;

	@Override
	public void onCreate() {
		// �꾩뿭 蹂�닔 珥덇린��
		state = 0;
		super.onCreate();
        ALL.context = getApplicationContext();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

    public static Context getAppContext() {
        return ALL.context;
    }
}