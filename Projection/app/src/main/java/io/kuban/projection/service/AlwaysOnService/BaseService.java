package io.kuban.projection.service.AlwaysOnService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BaseService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
