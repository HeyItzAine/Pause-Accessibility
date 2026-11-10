import app.accesspause.ToggleEngine;
public class ToggleEngineTest {
 static class Fake implements ToggleEngine.Backend {
  String s="reader/.Service:helper/.Service", saved="";int e=1,se;boolean pending,failSave,failWrite,ignoreWrite;int saves;
  public String services(){return s;}public int enabled(){return e;}public boolean pending(){return pending;}
  public String savedServices(){return saved;}public int savedEnabled(){return se;}
  public void save(String v,int n){if(failSave)throw new IllegalStateException();saved=v;se=n;pending=true;saves++;}
  public void services(String v){if(failWrite)throw new IllegalStateException();if(!ignoreWrite)s=v;}
  public void enabled(int n){if(failWrite)throw new IllegalStateException();if(!ignoreWrite)e=n;}
  public void complete(){pending=false;}
 }
 static void check(boolean b){if(!b)throw new AssertionError();}
 static void fails(Fake f){try{ToggleEngine.toggle(f);throw new AssertionError("Expected failure");}catch(IllegalStateException expected){}}
 public static void main(String[] args){
  Fake f=new Fake();String original=f.s;ToggleEngine.toggle(f);check(f.pending&&f.s.isEmpty()&&f.e==0&&f.saved.equals(original));
  ToggleEngine.toggle(f);check(!f.pending&&f.s.equals(original)&&f.e==1);
  f.s="new/.Service";ToggleEngine.toggle(f);check(f.saved.equals("new/.Service")&&f.saves==2);
  f=new Fake();f.failSave=true;fails(f);check(f.e==1&&!f.pending&&!f.s.isEmpty());
  f=new Fake();f.failWrite=true;fails(f);check(f.pending&&f.saved.equals(original));f.failWrite=false;ToggleEngine.toggle(f);check(!f.pending&&f.s.equals(original)&&f.saves==1);
  f=new Fake();ToggleEngine.toggle(f);f.failWrite=true;fails(f);check(f.pending&&f.saved.equals(original));
  f=new Fake();f.ignoreWrite=true;fails(f);check(f.pending);
  f=new Fake();f.e=0;f.s="";ToggleEngine.toggle(f);check(!f.pending&&f.saves==0);
  f=new Fake();f.e=0;ToggleEngine.toggle(f);ToggleEngine.toggle(f);check(f.e==0&&f.s.equals(original));
  System.out.println("9 save/restore and recovery scenarios passed");
 }
}
