# Clipboard-backstage-detection
安卓监听后台检测 剪切板 需要后台Service 的帮助

1.startService启动方式：
onCreate()–> onStartCommand()/onStart() —> onDestory(); 

1）创建服务onCreate()在整个生命周期仅执行一次，每次调用服务都会执行onStart()或onStartCommand();

2）停止服务onDestory()在整个生命周期仅执行一次；

3）服务一旦启动，生命周期将不受限于UI线程。应用（Activity）终止，服务仍然在后台运行；

4）直接启动的服务，其它应用不能调用其中的功能。

2.bindService绑定方式

onCreate() —> onBind() —> onUnbind() –> onDestory();

1）创建服务onCreate()在整个生命周期仅执行一次；

2）每次调用服务必须首先bindService/onBind，执行unbindService/onUnbind后不能调用；

3）服务的生命周期受限于UI线程。一旦应用（Activity）终止，服务将onDestory销毁；

4）可以在绑定后调用服务里的功能。

3.混合调用（须按顺序操作）：

1）首先在主界面创建时，startService(intent)启动方式开启服务，保证服务长期后台运行；

2）然后调用服务时，bindService(intent, connection, BIND_AUTO_CREATE)绑定方式绑定服务，这样可以调用服务的方法；

3）调用服务功能结束后，unbindService(connection)解除绑定服务，置空中介对象；

4）最后不再需要服务时，stopService(intent)终止服务。

###############################################

结合 ClipboardManager方法的使用！

clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
	    
            public void onPrimaryClipChanged() {
	    
                ClipData.Item itemAt = clipboardManager.getPrimaryClip().getItemAt(0);
		
                ss = itemAt.getText().toString();
		
                Log.d("ttm.", "监听到剪切板中的内容:" + ss);
		
###############################################

Service 构建接口 Callback 使用 onBind方法 实现回调传参

  public IBinder onBind(Intent intent) {
        return new Binder1();
	
    }

  public class Binder1 extends Binder {
  
        public ClipboardService getService() {
            return ClipboardService.this;
        }
    }
  
  public interface Callback {
        void onData(String data);
    }

  public void setCallback(Callback callback) {
        this.callback = callback;
    }
###############################################

使用 BindService 方法启用 Service 
	
  private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder Ibinder) {
            ClipboardService.Binder1 binder = (ClipboardService.Binder1) Ibinder;
            binder.getService().setCallback(new ClipboardService.Callback() {
                @Override
                public void onData(String data) {
                    datalist.add(data);
                }
            });
        }
  bindService(intent, connection, BIND_AUTO_CREATE); //BIND_AUTO_CREATE
 调用服务功能结束后，unbindService(connection)解除绑定服务，置空中介对象；
 
###############################################
