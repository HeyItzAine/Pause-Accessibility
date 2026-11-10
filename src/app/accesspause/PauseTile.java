package app.accesspause;
import android.service.quicksettings.*;
import android.widget.Toast;
import android.os.Handler;

public class PauseTile extends TileService {
    public void onStartListening(){refresh();}
    void refresh(){ Tile t=getQsTile(); if(t==null)return; Controller c=new Controller(this); t.setLabel(c.pending()?"Restore access":"Pause access"); t.setState(c.pending()?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE); if(android.os.Build.VERSION.SDK_INT>=29)t.setSubtitle(c.permitted()?(c.pending()?"Saved services":"Accessibility services"):"Open app to set up"); t.updateTile(); }
    public void onClick(){super.onClick(); if(isLocked()){unlockAndRun(()->act());}else act();}
    void act(){Toast.makeText(this,Controller.toggle(this),Toast.LENGTH_LONG).show(); refresh(); new Handler(getMainLooper()).postDelayed(()->refresh(),1000);}
}
