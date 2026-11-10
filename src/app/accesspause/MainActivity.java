package app.accesspause;
import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    LinearLayout root; TextView status, details; Button toggle;
    final String command="adb shell pm grant app.accesspause android.permission.WRITE_SECURE_SETTINGS";
    int dp(int n){return (int)(getResources().getDisplayMetrics().density*n);}
    TextView text(String s,int size,int color){TextView t=new TextView(this); t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setPadding(0,dp(8),0,dp(8));root.addView(t);return t;}
    Button button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);root.addView(b,new LinearLayout.LayoutParams(-1,-2));b.setOnClickListener(v->r.run());return b;}
    public void onCreate(Bundle state){super.onCreate(state);
        ScrollView sc=new ScrollView(this); root=new LinearLayout(this); root.setOrientation(1);root.setPadding(dp(24),dp(32),dp(24),dp(32));root.setBackgroundColor(Color.rgb(245,249,247));sc.addView(root);setContentView(sc);
        sc.setOnApplyWindowInsetsListener((v,i)->{root.setPadding(dp(24),dp(24)+i.getSystemWindowInsetTop(),dp(24),dp(24)+i.getSystemWindowInsetBottom());return i;});
        text("ACCESS PAUSE",14,Color.rgb(35,122,104));text("Your services.\nOne pause button.",30,Color.rgb(23,42,36));
        status=text("",18,Color.DKGRAY);toggle=button("Pause",()->doToggle());
        text("Pause saves the currently enabled app accessibility services, then disables them. Restore brings back that saved list. The snapshot stays on this phone across app closure and reboot.",16,Color.DKGRAY);
        text("Use the button anywhere",21,Color.BLACK);
        text("Swipe down twice → edit Quick Settings → drag Access Pause into your tiles. Tap it to pause; tap again to restore. This button keeps working while accessibility services are off.",16,Color.DKGRAY);
        if(Build.VERSION.SDK_INT>=33)button("Add Quick Settings button",()->{android.app.StatusBarManager m=getSystemService(android.app.StatusBarManager.class);m.requestAddTileService(new ComponentName(this,PauseTile.class),"Access Pause",android.graphics.drawable.Icon.createWithResource(this,app.accesspause.R.drawable.ic_pause),getMainExecutor(),result->{});});
        text("One-time setup with a computer",21,Color.BLACK);
        text("1. Install Android Platform Tools on your computer.\n2. On your phone, enable Developer options and USB debugging. Connect by USB and approve your computer.\n3. Run this command on the computer:",16,Color.DKGRAY);
        TextView cmd=text(command,14,Color.rgb(35,100,85));cmd.setTextIsSelectable(true);
        button("Copy setup command",()->{((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("ADB setup",command));Toast.makeText(this,"Copied",0).show();});
        text("Reopen this app after setup. You can turn USB debugging off afterward. Some phone manufacturers block this permission or require an additional developer setting.",15,Color.DKGRAY);
        button("Open Android accessibility settings",()->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        text("Saved recovery list",21,Color.BLACK);details=text("",14,Color.DKGRAY);details.setTextIsSelectable(true);
        text("This pauses app accessibility services (including TalkBack), not every system accessibility option. Use it only if you can navigate without those services. It does not hide them from your bank or guarantee that a banking app will accept the device. Close and reopen the banking app after pausing.\n\nDo not clear Access Pause's data or uninstall it while paused: that deletes your saved restore list. No internet permission, ads, analytics, screen reading, or accessibility service is included.",14,Color.DKGRAY);
    }
    protected void onResume(){super.onResume();refresh();}
    void refresh(){Controller c=new Controller(this);status.setText(!c.permitted()?"Setup needed: grant the ADB permission":c.pending()?"Restore snapshot saved • ready to restore":"Ready to save and pause");toggle.setEnabled(c.permitted());toggle.setText(c.pending()?"Restore saved services":"Save & pause services");String saved=c.savedServices();details.setText(saved.isEmpty()?"No saved services yet.":"Saved "+java.text.DateFormat.getDateTimeInstance().format(new java.util.Date(c.p.getLong("time",0)))+"\n\n"+saved.replace(":","\n\n")+"\n\n"+(c.pending()?"Restore pending. If Android refuses restoration, enable these services manually in Settings.":"Last snapshot restored. Next pause saves a fresh snapshot."));}
    void doToggle(){Controller c=new Controller(this);if(!c.pending()&&!c.p.getBoolean("warned",false)){new AlertDialog.Builder(this).setTitle("Pause accessibility services?").setMessage("This also turns off screen readers and accessibility navigation services. Make sure you can use the touchscreen and Quick Settings without them. Your enabled services will be saved before changes are made.").setNegativeButton("Cancel",null).setPositiveButton("Save & pause",(d,w)->{c.p.edit().putBoolean("warned",true).apply();runToggle();}).show();}else runToggle();}
    void runToggle(){String result=Controller.toggle(this);refresh();new AlertDialog.Builder(this).setMessage(result).setPositiveButton("OK",null).show();new Handler(getMainLooper()).postDelayed(()->refresh(),1000);}
}
