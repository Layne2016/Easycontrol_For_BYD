package top.eiyooooo.easycontrol.app.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Process;

public class KillerActivity extends Activity {
    private static final String EXTRA_MAIN_PID = "extra_main_pid";

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, KillerActivity.class);
        intent.putExtra(EXTRA_MAIN_PID, Process.myPid());
        activity.startActivity(intent);
        activity.finish();

    }
}
