import java.net.URI;
import java.util.concurrent.CountDownLatch;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

//WebSocket Instance 
public class PeepsSocketClient extends WebSocketClient {

	public PeepsSocketClient(URI serverURI) {
		super(serverURI);
	}
	
	//Below are event handlers: 
	
	@Override
	public void onClose(int code, String reason, boolean isRemote) {
		System.out.println(reason);
	}

	@Override
	public void onError(Exception ex) {
		System.out.println(ex);
		
	}

	@Override
	public void onMessage(String message) {
		System.out.println(message);
		if (ChatClient.chatWin != null) {
			//Implementing multi-threading with javafx
			Service<Void> service = new Service<Void>() {
		        @Override
		        protected Task<Void> createTask() {
		            return new Task<Void>() {           
		                @Override
		                protected Void call() throws Exception {
		                    //Background work                       
		                    final CountDownLatch latch = new CountDownLatch(1);
		                    Platform.runLater(new Runnable() {                          
		                        @Override
		                        public void run() {
		                            try{
		                                ChatClient.chatWin.printMessage(message);
		                            }finally{
		                                latch.countDown();
		                            }
		                        }
		                    });
		                    latch.await();                      
		                    //Keep with the background work
		                    return null;
		                }
		            };
		        }
		    };
		    service.start();
		}
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		System.out.println("Yay I'm in!");
	}
	
}
