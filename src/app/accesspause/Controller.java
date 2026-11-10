package app.accesspause;
import android.content.*;
import android.content.pm.PackageManager;
import android.provider.Settings;

final class Controller implements ToggleEngine.Backend {
    final Context c; final SharedPreferences p;
    Controller(Context c) { this.c=c; p=c.getSharedPreferences("snapshot",Context.MODE_PRIVATE); }
    boolean permitted(){ return c.checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS")==PackageManager.PERMISSION_GRANTED; }
    public String services(){ String s=Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES); return s==null?"":s; }
    public int enabled(){ return Settings.Secure.getInt(c.getContentResolver(),Settings.Secure.ACCESSIBILITY_ENABLED,0); }
    public boolean pending(){ return p.getBoolean("pending",false); }
    public String savedServices(){ return p.getString("services",""); }
    public int savedEnabled(){ return p.getInt("enabled",0); }
    public void save(String s,int e){ if(!p.edit().putString("services",s).putInt("enabled",e).putLong("time",System.currentTimeMillis()).putBoolean("pending",true).commit()) throw new IllegalStateException("Could not save the recovery snapshot. Nothing was changed."); }
    public void services(String s){ if(!Settings.Secure.putString(c.getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,s)) throw new IllegalStateException("Android refused to change the service list."); }
    public void enabled(int e){ if(!Settings.Secure.putInt(c.getContentResolver(),Settings.Secure.ACCESSIBILITY_ENABLED,e)) throw new IllegalStateException("Android refused to change accessibility state."); }
    public void complete(){ if(!p.edit().putBoolean("pending",false).commit()) throw new IllegalStateException("Services restored, but could not mark recovery complete. Try Restore again."); }
    static synchronized String toggle(Context c){ Controller b=new Controller(c); if(!b.permitted()) return "One-time ADB setup required. Open Access Pause for instructions."; try {return ToggleEngine.toggle(b);}catch(Exception e){return "Could not finish: "+e.getMessage();} }
}
